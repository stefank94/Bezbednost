(function () {

    'use strict';

    angular.module('certApp').controller('RegisterAdminController', RegisterAdminController);
    RegisterAdminController.$inject = ['userService', '$window', 'toastr'];

    function RegisterAdminController(userService, $window, toastr){

        var vm = this;

        vm.admin = {
            email: '',
            password: '',
            repeatPassword: ''
        };

        //

        vm.register = register;

        //

        function register(){
            if (!vm.admin.email)
                return;
            if (!vm.admin.password)
                return;
            if (vm.admin.password !== vm.admin.repeatPassword){
                toastr.error('Passwords don\'t match');
                return;
            }
            userService.registerAdmin(vm.admin)
                .then(function () {
                    toastr.info('Admin registered successfully.');
                    $window.location.href = "/#/home";
                })
                .catch(function(error){
                   toastr.error(error.data.message);
                });
        }

    }

})();