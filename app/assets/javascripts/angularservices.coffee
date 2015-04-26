ssmSvc = angular.module "SalonServices", ["ngResource"]

ssmSvc.factory "Product", ($resource) ->
    $resource '/admin/product/:prodid',{},{
        all: { method: 'GET',isArray: true},
        add: { method: 'POST'}
    }

ssmSvc.factory "Service", ($resource) ->
    {
        product: $resource('/admin/service/:svcid/:prdid',{},{
                save: { method: 'POST', isArray:true},
                remove: { method: 'DELETE', isArray: true}
            }
        ),
        service: $resource('/admin/service/:svcid',{},{
                all: { method: 'GET', isArray: true},
                add: { method: 'POST'}
            })
    }

ssmSvc.factory "Room", ($resource) ->
    $resource '/admin/room/:id/:svcid',{},{
        all: { method: 'GET',isArray: true},
        add: { method: 'POST'},
        addservice: { method: 'POST', isArray: true},
        delservice: { method: 'DELETE', isArray: true}
    }

ssmSvc.factory "Stylist", ($resource) ->
    $resource '/admin/stylist/:id/:svcid',{},{
        all: { method: 'GET',isArray: true},
        add: { method: 'POST'},
        addservice: { method: 'POST', isArray: true},
        delservice: { method: 'DELETE', isArray: true}
    }