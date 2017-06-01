(function(){
    'use strict';

    angular.module('certApp').factory('userService', function($http, baseUrl, $window){

        var that = this;

        that.loggedInUser = null;
        that.authenticated = false;
        fetchLoggedInUser();

        return {
            getLoggedInUser: getLoggedInUser,
            login: login,
            register: register,
            setLoggedInUser: setLoggedInUser,
            isAuthenticated: isAuthenticated,
            logout: logout,
            getUser: getUser
        };
        ////////////////

        function fetchLoggedInUser(){
            $http.get(baseUrl + 'api/user/logged')
                .then(function(data){
                    if (data.data != null && data.data != ''){
                        that.loggedInUser = data.data;
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
            if (user != null)
                that.authenticated = true;
            else
                that.authenticated = false;
        }

        function login(loginData){
            return $http.post(baseUrl + 'api/user/login', loginData);
        }

        function register(registerData){
            return $http.post(baseUrl + 'api/user/registration', registerData);
        }

        function isAuthenticated(){
            return that.authenticated;
        }

        function logout(){
            return $http.get(baseUrl + 'api/user/logout');
        }


    });

})();