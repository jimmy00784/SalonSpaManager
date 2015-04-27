angular.module("ssmApp").controller "ClientCtrl", ['$scope','$http','$location','$routeParams', ($scope, $http, $location, $routeParams) ->

    $scope.newclient = {}
    $scope.newclient.firstname = ""
    $scope.newclient.lastname = ""
    $scope.newclient.phone = []
    $scope.newclient.email = []

    $scope.addphone = (phone) ->
        $scope.newclient.phone.push(phone) if $scope.newclient.phone.indexOf(phone) < 0
    $scope.addemail = (email) ->
        $scope.newclient.email.push(email)
    $scope.removephone = (idx) ->
        $scope.newclient.phone.splice(idx,1)
    true
    ]