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

        vm.registrationData = {
            'username': '',
            'password': '',
            'repeatPassword': ''
        };

        ///

        vm.login = login;
        vm.logout = logout;
        vm.register = register;
        vm.isAuthenticated = userService.isAuthenticated;
        vm.getLoggedInUser = userService.getLoggedInUser;

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

        function logout(){
            userService.logout()
                .then(function(){
                    userService.setLoggedInUser(null);
                    $window.location.href = "/#/login";
                })
                .catch(function(error){
                   toastr.error(error.data.message);
                });

        }

        function register(){
            if (vm.registrationData.username === ""){
                toastr.error("You did not enter a username!");
                return;
            }
            if (vm.registrationData.password === ""){
                toastr.error("You did not enter a password!");
                return;
            }
            if (vm.registrationData.password !== vm.registrationData.repeatPassword){
                toastr.error("Passwords don't match!");
                return;
            }
            userService.register(vm.registrationData)
                .then(function (data) {
                    console.log(data);
                    userService.setLoggedInUser(data.data);
                    toastr.info("Registration successful.\nWelcome.");
                    $window.location.href = "/#/home";
                })
                .catch(function(error){
                    toastr.error(error.data.message);
                });
        }

    }

})();