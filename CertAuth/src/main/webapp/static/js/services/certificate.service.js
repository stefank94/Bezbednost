(function(){
    'use strict';

    angular.module('certApp').factory('certService', function($http, baseUrl){

        return {
            getCertificateByID : getCertificateByID,
            getMyCertificates: getMyCertificates,
            downloadCertificate: downloadCertificate,
            revokeCertificate: revokeCertificate,
            restoreCertificate: restoreCertificate,
            fullyRevokeCertificate: fullyRevokeCertificate
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

        function revokeCertificate(revoke){
            return $http.post(baseUrl + 'api/cert/revoke', revoke);
        }

        function restoreCertificate(id){
            return $http.put(baseUrl + 'api/cert/restore/' + id);
        }

        function fullyRevokeCertificate(id, reason){
            return $http.put(baseUrl + 'api/cert/fullyRevoke/' + id, reason);
        }

    });

})();
