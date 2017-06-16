(function(){

    'use strict';

    angular.module('certApp').controller('RequestController', RequestController);
    RequestController.$inject = ['userService', '$window', 'toastr', '$routeParams', 'requestService'];

    function RequestController(userService, $window, toastr, $routeParams, requestService){

        var vm = this;

        vm.request = {};

        //

        vm.isAdmin = userService.isAdmin;
        vm.getCertificateRequest = getCertificateRequest;
        vm.approveRequest = approveRequest;
        vm.rejectRequest = rejectRequest;

        //

        function getCertificateRequest(){
            if ($routeParams && $routeParams.id){
                var id = $routeParams.id;
                requestService.getRequestByID(id)
                    .then(function(data){
                        vm.request = data.data;
                        console.log(data.data);
                    })
                    .catch(function(error){
                        toastr.error(error.data.message);
                    });
            }
        }

        function approveRequest(){
            requestService.approveRequest(vm.request.id)
                .then(function(data){ // Should return the new certificate
                    toastr.info('Request approved. Certificate created.');
                    $window.location.href = '/#/cert/' + data.data.id;
                })
                .catch(function(error){
                    toastr.error(error.data.message);
                });
        }

        function rejectRequest(){
            requestService.rejectRequest(vm.request.id)
                .then(function (data) { // Should return the same request, only rejected
                    toastr.info('Request rejected.');
                    vm.request = data.data;
                })
                .catch(function (error) {
                    toastr.error(error.data.message);
                });
        }

    }

})();
