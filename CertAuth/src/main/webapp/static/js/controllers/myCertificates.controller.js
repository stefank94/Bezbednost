(function(){

    'use strict';

    angular.module('certApp').controller('MyCertificatesController', MyCertificatesController);
    MyCertificatesController.$inject = ['$window', 'toastr', 'certService'];

    function MyCertificatesController($window, toastr, certService){

        var vm = this;

        vm.certificates = [];

        //

        vm.getCertificates = getCertificates;

        //

        function getCertificates(){
            certService.getMyCertificates()
                .then(function(data){
                    vm.certificates = data.data;
                })
                .catch(function(error){
                    toastr.error(error.data.message);
                });
        }



    }

})();
