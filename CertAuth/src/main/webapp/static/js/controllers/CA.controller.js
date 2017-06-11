(function(){
    'use strict';

    angular.module('certApp').controller('CAController', CAController);
    CAController.$inject = ['$window', 'toastr', 'caService', 'userService', '$routeParams'];

    function CAController($window, toastr, caService, userService, $routeParams){

        var vm = this;
        vm.CA = {};

        //

        vm.getCA = getCA;
        vm.isAdmin = userService.isAdmin;

        //

        function getCA(){
            if ($routeParams && $routeParams.id) {
                var id = $routeParams.id;
                caService.findCAByID(id)
                    .then(function(data){
                        vm.CA = data.data;
                        console.log(vm.CA);
                    })
                    .catch(function(error){
                        toastr.error(error.data.message);
                    });
            }
        }

    }

})();