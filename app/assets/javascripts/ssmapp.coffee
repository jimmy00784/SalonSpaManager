ssmApp = angular.module "ssmApp",[]

ssmApp.controller "ProductCtrl", ($scope,$http) ->
    $scope.products = [{"_id":"1","name":"Product 1","active":true},
                       {"_id":"2","name":"Product 2","active":true}]
    true