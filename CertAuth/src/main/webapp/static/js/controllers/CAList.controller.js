(function(){
    'use strict';

    angular.module('certApp').controller('CAListController', CAListController);
    CAListController.$inject = ['$window', 'toastr', 'caService'];

    function CAListController($window, toastr, caService){

        var vm = this;

        vm.rootCA = {};
        vm.CAs = [];


        //

        vm.getCAs = getCAs;

        //

        function getCAs(){
            caService.getRootCA()
                .then(function(data){
                    vm.rootCA = data.data;
                })
                .catch(function(error){
                    toastr.error(error.data.message);
                });
            caService.getIntermediateCAs()
                .then(function(data){
                    vm.CAs = data.data;
                })
                .catch(function(error){
                    toastr.error(error.data.message);
                });
        }

    }

})();
