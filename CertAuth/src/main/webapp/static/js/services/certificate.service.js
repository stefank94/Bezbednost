(function(){
    'use strict';

    angular.module('certApp').factory('certService', function($http, baseUrl){

        return {
            getCertificateByID : getCertificateByID,
            getMyCertificates: getMyCertificates,
            downloadCertificate: downloadCertificate
        };

        //

        function getCertificateByID(id){
            return $http.get(baseUrl + 'api/cert/' + id);
        }

        function getMyCertificates(){
            return $http.get(baseUrl + 'api/cert/getMyCertificates');
        }

        function downloadCertificate(id){
            return $http.get(baseUrl + 'api/cert/download/' + id, {
                responseType: 'arraybuffer'
            });
        }

    });

})();
