(function(){
    'use strict';

    angular.module('certApp').controller('HomeController', HomeController);
    HomeController.$inject = ['$window', 'toastr', 'userService'];

    function HomeController($window, toastr, userService){

        var vm = this;

        vm.isAdmin = userService.isAdmin;

    }

})();