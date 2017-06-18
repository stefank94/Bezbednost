(function(){

    'use strict';

    angular.module('certApp').controller('RevokeController', RevokeController);
    RevokeController.$inject = ['$window', 'toastr', '$routeParams', 'certService'];

    function RevokeController($window, toastr, $routeParams, certService){

        var vm = this;
        vm.revocation = {
            certificate : 0,
            fullyRevoked: true,
            reason : 'keyCompromise',
            invalidityDate : null
        };
        vm.idk = false;
        vm.date = '';
        vm.certificate = {};

        //

        vm.initRevoke = initRevoke;
        vm.submitRevoke = submitRevoke;
        vm.fullyRevoke = fullyRevoke;

        //

        function initRevoke(){
            if ($routeParams && $routeParams.id){
                vm.revocation.certificate = $routeParams.id;
                certService.getCertificateByID($routeParams.id)
                    .then(function (data) {
                        vm.certificate = data.data;
                    })
                    .catch(function (){
                        console.log('Could not load certificate.');
                    });
            }
        }

        function submitRevoke(){
            if (!vm.idk) {
                var d = new Date(vm.date);
                if (d > new Date()){
                    toastr.error('Cannot set invalidity date in the future.');
                    return;
                }
                vm.revocation.invalidityDate = d;
            }
            else
                vm.revocation.invalidityDate = null;
            certService.revokeCertificate(vm.revocation)
                .then(function (data){
                    toastr.info('Certificate revoked!');
                    $window.location.href = '/#/cert/' + data.data.id;
                })
                .catch(function (error) {
                    toastr.error(error.data.message);
                });
        }

        function fullyRevoke(){
            certService.fullyRevokeCertificate(vm.certificate.id, vm.revocation.reason)
                .then(function (data) {
                    toastr.info('Certificate fully revoked.');
                    $window.location.href = '/#/cert/' + data.data.id;
                })
                .catch(function (error) {
                    toastr.error(error.data.message);
                });
        }

    }

})();