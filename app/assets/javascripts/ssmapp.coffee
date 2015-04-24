
ssmSvc = angular.module "SalonServices", ["ngResource"]

ssmSvc.factory "Product", ($resource) ->
    {
        all: () ->
            [{"_id":"1","name":"Product 1","active":true},
            {"_id":"2","name":"Product 2","active":true}]
        get: (id) ->
            {"_id":"1","name":"Product 1","active":true}
    }

ssmApp = angular.module "ssmApp",["ngRoute","SalonServices"]

ssmApp.config ($routeProvider,$locationProvider) ->
    $routeProvider.when '/product', {
        template: '<ul><li ng-repeat="product in products">{{ product.name }}</li></ul>',
        controller: 'ProductCtrl'
    }
    true

ssmApp.controller "ProductCtrl",['$scope','$http','Product', ($scope,$http,Product) ->
    $scope.products = Product.all()
    true
    ]