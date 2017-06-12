(function(){
    'use strict';

    angular.module('certApp').controller('RequestsController', RequestsController);
    RequestsController.$inject = ['userService', '$window', 'toastr', 'requestService'];

    function RequestsController(userService, $window, toastr, requestService){

        var vm = this;

        vm.requests = [];

        //

        vm.isAdmin = userService.isAdmin;
        vm.getCertificateRequests = getCertificateRequests;

        //

        function getCertificateRequests(){
            if (!vm.isAdmin()){
                requestService.getMyRequests()
                    .then(function(data){
                        vm.requests = data.data;
                    })
                    .catch(function (error) {
                        toastr.error(error.data.message);
                    });
            } else {
                requestService.getAllSubmittedRequests()
                    .then(function(data){
                        vm.requests = data.data;
                    })
                    .catch(function(error){
                        toastr.error(error.data.message);
                    });
            }

        }

    }

})();
