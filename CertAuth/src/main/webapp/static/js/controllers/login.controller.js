(function(){
    'use strict';

    angular.module('certApp').controller('LoginController', LoginController);
    LoginController.$inject = ['userService', '$window', 'toastr'];

    function LoginController(userService, $window, toastr){

        var vm = this;

        vm.loginData = {
            'username': '',
            'password': ''
        };

        ///

        vm.login = login;

        ///

        function login(){
            if (vm.loginData.username === ""){
                toastr.error("You did not enter a username!");
                return;
            }
            if (vm.loginData.password === ""){
                toastr.error("You did not enter a password!");
                return;
            }

            console.log(vm.loginData);

            userService.login(vm.loginData)
                .then(function(data){
                    userService.setLoggedInUser(data.data);
                    toastr.info("Welcome.");
                    $window.location.href = '/#/home';
                })
                .catch(function(error){
                    toastr.error(error.data.message);
                });
        }

    }

})();