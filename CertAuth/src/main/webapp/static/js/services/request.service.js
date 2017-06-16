(function(){

    'use strict';

    angular.module('certApp').factory('requestService', function($http, baseUrl){

       return {

           getMyRequests: getMyRequests,
           getRequestByID: getRequestByID,
           getAllSubmittedRequests: getAllSubmittedRequests,
           approveRequest: approveRequest,
           rejectRequest: rejectRequest,
           upload: upload

       } ;

       //

        function upload(file, usage){
            var fd = new FormData();
            fd.append("file", file);
            return $http.post(baseUrl + "api/request/upload", fd, {
                withCredentials: false,
                headers: {
                    'Content-Type': undefined,
                    'Accept': 'application/json'
                },
                params: {
                    'usage': usage,
                    'csr': fd
                },
                transformRequest: angular.identity
            });
        }

        function getMyRequests(){
            return $http.get(baseUrl + 'api/request/getMyRequests');
        }

        function getRequestByID(id){
            return $http.get(baseUrl + 'api/request/' + id);
        }

        function getAllSubmittedRequests(){
            return $http.get(baseUrl + 'api/request/getAllSubmittedRequests');
        }

        function approveRequest(id){
            return $http.put(baseUrl + 'api/request/approve/' + id);
        }

        function rejectRequest(id){
            return $http.put(baseUrl + 'api/request/reject/' + id);
        }

    });

})();
