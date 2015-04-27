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

smsSvc.factory "Client", ($resource) ->
    $resource '/client/:id',{},{
        search: { method: 'GET', isArray: true},
        addphone: { url: '/client/:id/phone/:phone', method: 'POST', isArray: true },
        delphone: { url: '/client/:id/phone/:phone', method: 'DELETE', isArray: true },
        addemail: { url: '/client/:id/email/:email', method: 'POST', isArray: true },
        delphone: { url: '/client/:id/email/:email', method: 'DELETE', isArray: true },
        visits:   { url: '/client/:id/visit', method: 'GET', isArray: true },
        addvisit: { url: '/client/:id/visit', method: 'POST' },
        updatevisit: { url: '/client/:id/visit/:visit', method: 'POST'},
        addproduct:  { url: '/client/:id/visit/:visit/product', method: 'POST'},
        updateproduct: { url: '/client/:id/visit/:visit/product/:product', method: 'POST'},
        removeproduct: { url: '/client/:id/visit/:visit/product/:product', method: 'DELETE'}
    }