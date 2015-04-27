angular.module("ssmApp").controller "ClientCtrl", ['$scope','$http','$location','$routeParams','Client', ($scope, $http, $location, $routeParams,Client) ->

    $scope.clients = []
    $scope.client = {}

    $scope.newclient = {}
    $scope.newclient.firstname = ""
    $scope.newclient.lastname = ""
    $scope.newclient.phone = []
    $scope.newclient.email = []

    if $routeParams? && $routeParams.id?
        $scope.client = Client.get({id: $routeParams.id}) if $routeParams?
    else
        Client.all {}, (data) ->
            $scope.clients = data

    $scope.add = (client) ->
        Client.save client, (data) ->
            $scope.clients.push data

    $scope.addphone = (phone) ->
        if not $scope.newclient.phone?
            $scope.newclient.phone = []
        $scope.newclient.phone.push(phone) if $scope.newclient.phone.indexOf(phone) < 0
    $scope.addemail = (email) ->
        if not $scope.newclient.email?
            $scope.newclient.email = []
        $scope.newclient.email.push(email)
    $scope.removephone = (idx) ->
        $scope.newclient.phone.splice(idx,1) if $scope.newclient.phone?
    $scope.removeemail = (idx) ->
        $scope.newclient.email.splice(idx,1) if $scope.newclient.email?

    $scope.addclientphone = (phone) ->
        if not $scope.client.phone?
            $scope.client.phone = []
        Client.addphone {id:$scope.client._id,phone:phone},{}, () ->
            $scope.client.phone.push(phone) if $scope.client.phone.indexOf(phone) < 0
    $scope.addclientemail = (email) ->
        if not $scope.client.email?
            $scope.client.email = []
        Client.addemail {id:$scope.client._id,email:email},{}, () ->
            $scope.client.email.push(email) if $scope.client.email.indexOf(email) < 0
    $scope.removeclientphone = (idx) ->
        Client.delphone {id:$scope.client._id,phoneid:idx},{}, () ->
            $scope.client.phone.splice(idx,1) if $scope.client.phone?
    $scope.removeclientemail = (idx) ->
        Client.delemail {id:$scope.client._id,emailid:idx},{}, () ->
            $scope.client.email.splice(idx,1) if $scope.client.email?

    $scope.updateclient = () ->
        Client.save {id:$scope.client._id},$scope.client, () ->
            $scope.client = {}
            $location.path('/client').replace()
    true
    ]