angular.module("ssmApp").controller "ProductCtrl",['$scope','$http','$location','$routeParams','Product', ($scope,$http,$location,$routeParams,Product) ->
    $scope.products = []
    $scope.product = {}

    Product.all {}, (data) ->
        $scope.products = data

    $scope.add = (obj) ->
        if obj? && obj.name?
            $scope.product = {name: obj.name, active: if obj.active? then obj.active else false}
            Product.add $scope.product, (data) ->
                $scope.products.push data
                $scope.product = {}
                $scope.$apply()

    id = 0
    $scope.product = Product.get({prodid: $routeParams.id}) if $routeParams?

    $scope.save = () ->
        $scope.product._id = $scope.product._id if $scope.product._id?
        Product.save {prodid:$scope.product._id}, $scope.product, () ->
            $location.path('/product').replace()
            $scope.apply()

    $scope.remove = (id) ->
        Product.remove {prodid:id}, () ->
            idx = -1
            for prod in $scope.products
                if prod._id == id
                    idx = $scope.products.indexOf prod
                    break

            $scope.products.splice idx,1 if idx > -1
    true
    ]