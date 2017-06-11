(function(){

    'use strict';

    var app = angular.module('certApp');

    app.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider){
        $routeProvider
            .when('/', {
                templateUrl: 'static/html/login.html',
                controller: 'LoginController',
                controllerAs: 'loginCtrl'
            })
            .when('/home', {
                templateUrl: 'static/html/home.html',
                controller: 'HomeController',
                controllerAs: 'homeCtrl'
            })
            .when('/caList', {
                templateUrl: 'static/html/CAList.html',
                controller: 'CAListController',
                controllerAs: 'caCtrl'
            })
            .when('/ca/:id', {
                templateUrl: 'static/html/CA.html',
                controller: 'CAController',
                controllerAs: 'caCtrl'
            })
            .when('/addChildCA/:id', {
                templateUrl: 'static/html/createNewCA.html',
                controller: 'createCAController',
                controllerAs: 'caCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
        $locationProvider.hashPrefix('');
    }]);

})();