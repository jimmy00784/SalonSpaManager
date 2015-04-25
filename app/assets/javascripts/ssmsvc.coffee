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
