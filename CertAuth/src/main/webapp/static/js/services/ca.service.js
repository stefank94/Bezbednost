(function(){
    'use strict';

    angular.module('certApp').factory('caService', function($http, baseUrl, $window){

        return {
            findCAByID : findCAByID,
            getRootCA : getRootCA,
            getIntermediateCAs : getIntermediateCAs,
            CertificateAuthority: CertificateAuthority,
            create: create
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
                'bottomCA': false
            }
            return ca;
        }

        function create(ca){
            return $http.post(baseUrl + 'api/ca/create', ca);
        }

    });

})();
