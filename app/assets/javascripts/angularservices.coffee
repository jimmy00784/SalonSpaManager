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
        delservice: { method: 'DELETE', isArray: true},
        forservice: { url: '/admin/room/forservice/:svcid', method: 'GET', isArray: true}
    }

ssmSvc.factory "Stylist", ($resource) ->
    $resource '/admin/stylist/:id/:svcid',{},{
        all: { method: 'GET',isArray: true},
        add: { method: 'POST'},
        addservice: { method: 'POST', isArray: true},
        delservice: { method: 'DELETE', isArray: true},
        forservice: { url: '/admin/stylist/forservice/:svcid', method: 'GET', isArray: true}
    }

ssmSvc.factory "Client", ($resource) ->
    $resource '/client/:id',{},{
        all: { method: 'GET', isArray: true},
        search: { method: 'GET', isArray: true},
        addphone: { url: '/client/:id/phone/:phone', method: 'POST'},
        delphone: { url: '/client/:id/phone/:phoneid', method: 'DELETE'},
        addemail: { url: '/client/:id/email/:email', method: 'POST'},
        delemail: { url: '/client/:id/email/:emailid', method: 'DELETE'},
        visits:   { url: '/client/:id/visit', method: 'GET'},
        getvisit: { url: '/client/:id/visit/:visit', method: 'GET'},
        addvisit: { url: '/client/:id/visit', method: 'POST' },
        updatevisit: { url: '/client/:id/visit/:visit', method: 'POST'},
        addproduct:  { url: '/client/:id/visit/:visit/product', method: 'POST'},
        updateproduct: { url: '/client/:id/visit/:visit/product/:product', method: 'POST'},
        removeproduct: { url: '/client/:id/visit/:visit/product/:product', method: 'DELETE'}
    }