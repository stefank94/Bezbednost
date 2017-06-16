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
        vm.downloadCertificate = downloadCertificate;

        //

        function getCertificate(){
            if ($routeParams && $routeParams.id){
                var id = $routeParams.id;
                certService.getCertificateByID(id)
                    .then(function(data){
                        vm.certificate = data.data;
                        console.log(vm.certificate);
                        console.log(vm.isAdmin());
                        console.log(vm.getLoggedInUser());
                    })
                    .catch(function(error){
                        toastr.error(error.data.message);
                    });
            } else
                $window.location.href = "/#/caList";
        }

        function downloadCertificate(){

        }

    }

})();
