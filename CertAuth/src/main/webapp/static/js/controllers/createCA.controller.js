(function(){
    'use strict';

    angular.module('certApp').controller('createCAController', createCAController);
    createCAController.$inject = ['$window', 'toastr', 'caService', '$routeParams'];

    function createCAController($window, toastr, caService, $routeParams){

        var vm = this;

        vm.issuer = null;
        vm.CA = caService.CertificateAuthority();

        load();

        //

        vm.create = create;

        //

        function load(){
            if ($routeParams && $routeParams.id){
                var id = $routeParams.id;
                vm.issuer = id;
            }
        }

        function create(){
            vm.CA.issuer = vm.issuer;
            caService.create(vm.CA)
                .then(function(data){
                    toastr.info('CA created successfully.');
                    $window.location.href = '/#/ca/' + data.data.id;
                })
                .catch(function(error){
                    toastr.error(error.data.message);
                });
        }


    }

})();
