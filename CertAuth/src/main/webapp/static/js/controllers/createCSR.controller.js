(function(){

    'use strict';

    angular.module('certApp').controller('CreateCSRController', CreateCSRController);
    CreateCSRController.$inject = ['$window', 'toastr', 'requestService'];

    function CreateCSRController($window, toastr, requestService){

        var vm = this;

        vm.usage = "HTTPS";

        //

        vm.upload = upload;

        //

        function upload(){
            var f = document.getElementById('file').files[0];
            if (f != null && f != ""){
                requestService.upload(f, vm.usage)
                    .then(function(data){
                        toastr.info("CSR uploaded successfully.");
                        console.log(data);
                        $window.location.href = "/#/request/" + data.data.id;
                    })
                    .catch(function(error){
                        toastr.error(error.data.message);
                    });
            }
        }

    }

})();
