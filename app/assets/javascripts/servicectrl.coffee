angular.module("ssmApp").controller "ServiceCtrl",['$scope','$http','$routeParams','$location','Service','Product',($scope,$http,$routeParams,$location,Service,Product) ->
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
        if obj? && obj.name? && obj.desc? && obj.duration?
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
    $scope.remove = (id) ->
        Service.service.remove {svcid:id}, () ->
            idx = -1
            for svc in $scope.services
                if svc._id == id
                    idx = $scope.services.indexOf svc
                    break

            $scope.services.splice idx,1 if idx > -1
    true
    ]
