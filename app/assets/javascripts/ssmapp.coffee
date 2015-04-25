ssmApp = angular.module "ssmApp",["ngRoute","SalonServices"]

ssmApp.config ($routeProvider,$locationProvider) ->
    $routeProvider.when '/product', {
        templateUrl: 'views/products/index.html',
        controller: 'ProductCtrl'
    }
    $routeProvider.when '/product/:id', {
        templateUrl: 'views/products/product.html',
        controller: 'ProductCtrl'
    }

    $routeProvider.when '/service', {
        templateUrl: 'views/services/index.html',
        controller: 'ServiceCtrl'
    }
    $routeProvider.when '/service/:id', {
        templateUrl: 'views/services/service.html',
        controller: 'ServiceCtrl'
    }
    true

ssmApp.controller "ProductCtrl",['$scope','$http','$location','$routeParams','Product', ($scope,$http,$location,$routeParams,Product) ->
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

ssmApp.controller "ServiceCtrl",['$scope','$http','$routeParams','$location','Service','Product',($scope,$http,$routeParams,$location,Service,Product) ->
    $scope.services = []
    Service.service.all (data) ->
        $scope.services = data

    allproducts = []
    Product.all (data) ->
        allproducts = data

    $scope.service = {}
    $scope.products = []
    id = 0
    if $routeParams? && $routeParams.id
        $scope.service = Service.service.get {svcid:$routeParams.id}, () ->
            $scope.products = []
            for prodid in $scope.service.products
                Product.get {prodid:prodid}, (prod) ->
                    $scope.products.push prod if prod?

    $scope.add = (obj) ->
        if obj? && obj.name? && obj.desc? && obj.duration
            $scope.service = {name: obj.name, desc: obj.desc, duration: obj.duration, active: if obj.active? then obj.active else false}
            Service.service.add $scope.service, (data) ->
                $scope.services.push data
                $scope.service = {}

    $scope.save = () ->
        $scope.service._id = $scope.service._id if $scope.service._id?
        Service.service.save {svcid:$scope.service._id}, $scope.service, () ->
            $location.path('/service').replace()

    $scope.removeprod = (id) ->
        Service.product.remove {svcid: $scope.service._id, prdid:id},{},(data) ->
            $scope.products = data
        true

    $scope.addproduct = (id) ->
        Service.product.save { svcid: $scope.service._id, prdid:id},{},(data) ->
            $scope.products = data
        true
    $scope.unusedproducts = () ->
        result = []
        if $scope.service? && allproducts?
            for p in allproducts
                found = false
                for prod in $scope.service.products
                    if prod == p._id
                        found = true
                        break
                result.push p if !found
        result

    true
    ]