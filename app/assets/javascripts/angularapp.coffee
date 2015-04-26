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

    $routeProvider.when '/room', {
        templateUrl: 'views/rooms/index.html',
        controller: 'RoomCtrl'
    }
    $routeProvider.when '/room/:id', {
        templateUrl: 'views/rooms/room.html',
        controller: 'RoomCtrl'
    }

    $routeProvider.when '/stylist', {
        templateUrl: 'views/stylists/index.html',
        controller: 'StylistCtrl'
    }
    $routeProvider.when '/stylist/:id', {
        templateUrl: 'views/stylists/stylist.html',
        controller: 'StylistCtrl'
    }

    $routeProvider.when '/client', {
        templateUrl: 'views/clients/index.html',
        controller: 'ClientCtrl'
    }

    true


