(function(){
    'use strict';

    angular.module('certApp').controller('CAListController', CAListController);
    CAListController.$inject = ['$window', 'toastr', 'caService'];

    function CAListController($window, toastr, caService){

        var vm = this;

        vm.rootCA = {};
        vm.intermediateCAs = [];
        vm.bottomCAs = [];

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
                    vm.intermediateCAs = data.data;
                })
                .catch(function(error){
                    toastr.error(error.data.message);
                });
            caService.getBottomCAs()
                .then(function(data){
                    vm.bottomCAs = data.data;
                })
                .catch(function(error){
                    toastr.error(error.data.message);
                });
        }

    }

})();
