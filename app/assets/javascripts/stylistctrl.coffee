angular.module("ssmApp").controller "StylistCtrl",['$scope','$http','$routeParams','$location','Stylist','Service',($scope,$http,$routeParams,$location,Stylist,Service) ->
    $scope.stylists = []
    $scope.stylist = {}
    $scope.services = []
    Stylist.all (data) ->
        $scope.stylists = data

    id = 0
    if $routeParams? && $routeParams.id?
        $scope.stylist = Stylist.get {id:$routeParams.id}, () ->
            $scope.services = []
            for svcid in $scope.stylist.services
                Service.service.get {svcid:svcid}, (service) ->
                    $scope.services.push service if service?

    $scope.add = (obj) ->
        if obj? && obj.name?
            $scope.stylist = {name: obj.name, active: if obj.active? then obj.active else false}
            Stylist.add $scope.stylist, (data) ->
                $scope.stylists.push data
                $scope.stylist = {}

    $scope.remove = (id) ->
        Stylist.remove {id:id}, () ->
            idx = -1
            for stylist in $scope.stylists
                if stylist._id == id
                    idx = $scope.stylists.indexOf stylist
                    break
            $scope.stylists.splice idx,1 if idx > -1

    $scope.save = () ->
        Stylist.save {id:$scope.stylist._id}, $scope.stylist, () ->
            $location.path('/stylist').replace()

    allservices = []
    Service.service.all (data) ->
        allservices = data

    $scope.unusedservices = () ->
        result = []
        if $routeParams? && $routeParams.id? && $scope.stylist? && allservices? && Array.isArray(allservices) && Array.isArray($scope.stylist.services)
            for s in allservices
                found = false
                for svc in $scope.stylist.services
                    if svc == s._id
                        found = true
                        break
                result.push s if !found
        result

    $scope.addservice = (svcid) ->
        Stylist.addservice {id:$scope.stylist._id,svcid:svcid},{}, (data) ->
            $scope.services = data

    $scope.removeservice = (svcid) ->
        Stylist.delservice {id:$scope.stylist._id,svcid:svcid}, (data) ->
            $scope.services = data

    ]