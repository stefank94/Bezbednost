(function(){
    'use strict';

    angular.module('certApp').factory('caService', function($http, baseUrl, $window){

        return {
            findCAByID : findCAByID,
            getRootCA : getRootCA,
            getIntermediateCAs : getIntermediateCAs,
            CertificateAuthority: CertificateAuthority,
            create: create,
            getDefaultCRLGeneration: getDefaultCRLGeneration,
            rescheduleCA: rescheduleCA,
            rescheduleAllCAs: rescheduleAllCAs
        };

        /////////////////

        function findCAByID(id){
            return $http.get(baseUrl + 'api/ca/' + id);
        }

        function getRootCA() {
            return $http.get(baseUrl + 'api/ca/root');
        }

        function getIntermediateCAs(){
            return $http.get(baseUrl + 'api/ca/intermediateCAs');
        }

        function CertificateAuthority(){
            var ca = {
                'issuer' : '',
                'certificate': {
                    'certificateData': {
                        'commonName': '',
                        'givenName': '',
                        'surname': '',
                        'organization': '',
                        'organizationalUnit': '',
                        'countryCode': '',
                        'emailAddress': '',
                        'uid': '',
                        'isCA': true
                    }
                },
                'caRole': 'INTERMEDIATE',
                'duration': 0,
                'durationOfIssuedCertificates': 0
            };
            return ca;
        }

        function create(ca){
            return $http.post(baseUrl + 'api/ca/create', ca);
        }

        function getDefaultCRLGeneration(){
            return $http.get(baseUrl + 'api/crl/getDefault');
        }

        function rescheduleCA(id, cron, freqDesc){
            var twoStrings = new Object();
            twoStrings.string1 = cron;
            twoStrings.string2 = freqDesc;
            console.log(twoStrings);
            return $http.put(baseUrl + 'api/crl/reschedule/' + id, twoStrings);
        }

        function rescheduleAllCAs(cron, freqDesc){
            var twoStrings = new Object();
            twoStrings.string1 = cron;
            twoStrings.string2 = freqDesc;
            return $http.put(baseUrl + 'api/crl/rescheduleAll', twoStrings);
        }

    });

})();
