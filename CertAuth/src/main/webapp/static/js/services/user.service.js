(function(){
    'use strict';

    angular.module('certApp').factory('userService', function($http, baseUrl, $window){

        var that = this;

        that.loggedInUser = null;
        that.authenticated = false;
        that.isAdmin = false;
        fetchLoggedInUser();

        return {
            getLoggedInUser: getLoggedInUser,
            login: login,
            register: register,
            setLoggedInUser: setLoggedInUser,
            isAuthenticated: isAuthenticated,
            logout: logout,
            getUser: getUser,
            isAdmin: isAdmin,
            changePassword: changePassword,
            registerAdmin: registerAdmin
        };
        ////////////////

        function fetchLoggedInUser(){
            $http.get(baseUrl + 'api/user/logged')
                .then(function(data){
                    if (data.data != null && data.data != ''){
                        that.loggedInUser = data.data;
                        if (that.loggedInUser.admin)
                            that.isAdmin = true;
                        else
                            that.isAdmin = false;
                        that.authenticated = true;
                    } else {
                        that.loggedInUser = null;
                        that.authenticated = false;
                        $window.location.href = "/#/login";
                    }
                })
                .catch(function(){
                    console.log('Fetch logged in user failed.');
                    that.loggedInUser = null;
                    that.authenticated = false;
                    $window.location.href = "/#/login";
                });
        }

        function getUser(success, error) {
            return $http.get(baseUrl + 'api/user/logged')
                .then(success)
                .catch(error);
        }

        function getLoggedInUser(){
            return that.loggedInUser;
        }

        function setLoggedInUser(user){
            that.loggedInUser = user;
            if (user != null) {
                that.authenticated = true;
                if (user.admin)
                    that.isAdmin = true;
            }
            else
                that.authenticated = false;
        }

        function login(loginData){
            return $http.post(baseUrl + 'api/user/login', loginData);
        }

        function register(registerData){
            return $http.post(baseUrl + 'api/user/register', registerData);
        }

        function isAuthenticated(){
            return that.authenticated;
        }

        function logout(){
            that.isAdmin = false;
            return $http.get(baseUrl + 'api/user/logout');
        }

        function isAdmin(){
            return that.isAdmin;
        }

        function changePassword(changePass){
            return $http.put(baseUrl + 'api/user/changePassword', changePass);
        }

        function registerAdmin(admin){
            console.log(admin);
            return $http.post(baseUrl + 'api/admin/registration', admin);
        }

    });

})();