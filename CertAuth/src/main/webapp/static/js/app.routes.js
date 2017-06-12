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
                controller: 'CreateCAController',
                controllerAs: 'caCtrl'
            })
            .when('/cert/:id', {
                templateUrl: 'static/html/certificate.html',
                controller: 'CertController',
                controllerAs: 'certCtrl'
            })
            .when('/registration', {
                templateUrl: 'static/html/registration.html',
                controller: 'LoginController',
                controllerAs: 'loginCtrl'
            })
            .when('/profile', {
                templateUrl: 'static/html/profile.html',
                controller: 'ProfileController',
                controllerAs: 'userCtrl'
            })
            .when('/myCertificates', {
                templateUrl: 'static/html/myCertificates.html',
                controller: 'MyCertificatesController',
                controllerAs: 'certCtrl'
            })
            .when('/requests', {
                templateUrl: 'static/html/requests.html',
                controller: 'RequestsController',
                controllerAs: 'reqCtrl'
            })
            .when('/request/:id', {
                templateUrl: 'static/html/request.html',
                controller: 'RequestController',
                controllerAs: 'reqCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
        $locationProvider.hashPrefix('');
    }]);

})();