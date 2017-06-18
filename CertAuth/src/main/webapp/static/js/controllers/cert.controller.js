(function(){

    'use strict';

    angular.module('certApp').controller('CertController', CertController);
    CertController.$inject = ['$window', 'toastr', 'certService', 'userService', '$routeParams'];

    function CertController($window, toastr, certService, userService, $routeParams){

        var vm = this;

        vm.certificate = {};

        //

        vm.isAdmin = userService.isAdmin;
        vm.getLoggedInUser = userService.getLoggedInUser;
        vm.getCertificate = getCertificate;
        vm.restore = restore;
        vm.fullyRevoke =fullyRevoke;

        //

        function getCertificate(){
            if ($routeParams && $routeParams.id){
                var id = $routeParams.id;
                certService.getCertificateByID(id)
                    .then(function(data){
                        vm.certificate = data.data;
                    })
                    .catch(function(error){
                        toastr.error(error.data.message);
                    });
            } else
                $window.location.href = "/#/caList";
        }

        function restore(){
            certService.restoreCertificate(vm.certificate.id)
                .then(function (data) {
                    toastr.info('Certificate restored.');
                    vm.certificate = data.data;
                })
                .catch(function (error) {
                    toastr.error(error.data.message);
                });
        }

        function fullyRevoke(){
            certService.fullyRevokeCertificate(vm.certificate.id)
                .then(function (data) {
                    toastr.info('Certificate fully revoked.');
                    vm.certificate = data.data;
                })
                .catch(function (error) {
                    toastr.error(error.data.message);
                });
        }

    }

})();
