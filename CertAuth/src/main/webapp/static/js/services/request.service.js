(function(){

    'use strict';

    angular.module('certApp').factory('requestService', function($http, baseUrl){

       return {

           getMyRequests: getMyRequests,
           getRequestByID: getRequestByID,
           getAllSubmittedRequests: getAllSubmittedRequests,
           approveRequest: approveRequest,
           rejectRequest: rejectRequest

       } ;

       //

        function getMyRequests(){
            $http.get(baseUrl + 'api/request/getMyRequests');
        }

        function getRequestByID(id){
            $http.get(baseUrl + 'api/request/' + id);
        }

        function getAllSubmittedRequests(){
            $http.get(baseUrl + 'api/request/getAllSubmittedRequests');
        }

        function approveRequest(id){
            $http.put(baseUrl + 'api/request/approve/' + id);
        }

        function rejectRequest(id){
            $http.put(baseUrl + 'api/request/reject/' + id);
        }

    });

})();
