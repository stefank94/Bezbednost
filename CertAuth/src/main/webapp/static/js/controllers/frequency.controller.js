(function(){

    'use strict';

    angular.module('certApp').controller('FrequencyController', FrequencyController);
    FrequencyController.$inject = ['$window', 'toastr', 'caService', '$routeParams'];

    function FrequencyController($window, toastr, caService, $routeParams){

        var vm = this;

        vm.id = null;
        vm.all = false;

        //

        vm.load = load;
        vm.save = save;

        //

        function load(){
            if ($routeParams && $routeParams.id){
                vm.id = $routeParams.id;
                vm.all = false;
            } else {
                vm.id = null;
                vm.all = true;
            }
        }

        function save(){

        }

    }

})();
