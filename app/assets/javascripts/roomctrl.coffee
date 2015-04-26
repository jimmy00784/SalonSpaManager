angular.module("ssmApp").controller "RoomCtrl",['$scope','$http','$routeParams','$location','Room','Service',($scope,$http,$routeParams,$location,Room,Service) ->
    $scope.rooms = []
    $scope.room = {}
    $scope.services = []
    Room.all (data) ->
        $scope.rooms = data

    id = 0
    if $routeParams? && $routeParams.id?
        $scope.room = Room.get {id:$routeParams.id}, () ->
            $scope.services = []
            for svcid in $scope.room.services
                Service.service.get {svcid:svcid}, (service) ->
                    $scope.services.push service if service?

    $scope.add = (obj) ->
        if obj? && obj.name?
            $scope.room = {name: obj.name, active: if obj.active? then obj.active else false}
            Room.add $scope.room, (data) ->
                $scope.rooms.push data
                $scope.room = {}

    $scope.remove = (id) ->
        Room.remove {id:id}, () ->
            idx = -1
            for room in $scope.rooms
                if room._id == id
                    idx = $scope.rooms.indexOf room
                    break
            $scope.rooms.splice idx,1 if idx > -1

    $scope.save = () ->
        Room.save {id:$scope.room._id}, $scope.room, () ->
            $location.path('/room').replace()

    allservices = []
    Service.service.all (data) ->
        allservices = data

    $scope.unusedservices = () ->
        result = []
        if $routeParams? && $routeParams.id? && $scope.room? && allservices? && Array.isArray allservices
            for s in allservices
                found = false
                for svc in $scope.room.services
                    if svc == s._id
                        found = true
                        break
                result.push s if !found
        result

    $scope.addservice = (svcid) ->
        Room.addservice {id:$scope.room._id,svcid:svcid},{}, (data) ->
            $scope.services = data

    $scope.removeservice = (svcid) ->
        Room.delservice {id:$scope.room._id,svcid:svcid}, (data) ->
            $scope.services = data

    ]