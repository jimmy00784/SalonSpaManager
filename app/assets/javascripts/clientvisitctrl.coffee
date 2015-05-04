angular.module("ssmApp").controller "ClientVisitCtrl", ['$scope','$http','$location','$routeParams','Client','Service','Stylist','Room', ($scope, $http, $location, $routeParams,Client, Service,Stylist,Room) ->
    $scope.client = {}
    $scope.visit = {}

    $scope.services = []
    $scope.service = ""
    $scope.stylists = []

    $scope.rooms = []

    $scope.newvisit = {}
    $scope.newvisit.details = []
    $scope.newvisit.details.push({service:"",stylist:"",room:""})

    $scope.formatdate = (date) ->
        r = new Date(date) if date? && not isNaN(date)
        if r?
            M = r.getMonth() + 1
            d = r.getDate()
            y = r.getFullYear()
            h = r.getHours()
            h = h - 12 if h > 2
            m = r.getMinutes()
            m = "0" + m if m < 9
            a = "AM"
            a = "PM" if r.getHours() > 11

            M + "/" + d + "/" + y + " " + h + ":" + m + " " + a

    if $routeParams? && $routeParams.id?
        if not $routeParams.visit?
            Client.visits {id:$routeParams.id}, (data) ->
                $scope.client = data
                $scope.visit = {}
        else
            Client.getvisit {id:$routeParams.id, visit:$routeParams.visit}, (data) ->
                $scope.client = data
                $scope.visit = $scope.client.history[0] #if $scope.client.history? && Array.isArray($scope.client.history) && $scope.client.history.length > 0
        Service.service.all (data) ->
            $scope.services = data
            $scope.stylists = []
            $scope.rooms = []

    $scope.getstylistsandrooms = () ->
        $scope.service = $scope.newvisit.details[0].service
        if $routeParams? && $routeParams.id? && $scope.service?
            Stylist.forservice {svcid:$scope.service},{}, (data) ->
                $scope.stylists = data
            Room.forservice {svcid:$scope.service},{}, (data) ->
                $scope.rooms = data

    $scope.addnewvisit = (obj) ->
        Client.addvisit {id:$routeParams.id},obj,(data) ->
            $scope.client.history.push(data)
    ]