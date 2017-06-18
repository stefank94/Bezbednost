(function(){

    'use strict';

    angular.module('certApp').controller('RevokeController', RevokeController);
    RevokeController.$inject = ['userService', '$window', 'toastr', '$routeParams', 'certService'];

    function RevokeController(userService, $window, toastr, $routeParams, certService){

        var vm = this;
        vm.revocation = {
            certificate : 0,
            fullyRevoked: true,
            reason : 'keyCompromise',
            invalidityDate : null
        };
        vm.idk = false;
        vm.date = '';

        //

        vm.initRevoke = initRevoke;
        vm.submitRevoke = submitRevoke;

        //

        function initRevoke(){
            if ($routeParams && $routeParams.id){
                vm.revocation.certificate = $routeParams.id;
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

    }

})();