(function(){

    'use strict';

    angular.module('certApp').controller('FrequencyController', FrequencyController);
    FrequencyController.$inject = ['$window', 'toastr', 'caService', '$routeParams'];

    function FrequencyController($window, toastr, caService, $routeParams){

        var vm = this;

        vm.id = null;
        vm.all = false;
        vm.useDeafult = false;
        vm.ca = {};
        vm.default = {
            cronExp: '',
            freqDesc: ''
        };
        vm.newFreq = {
            cronExp: '',
            freqDesc: ''
        };
        vm.options = [
            new Frequency("0 0/5 * 1/1 * ?", "Every 5 minutes"),
            new Frequency("0 0/2 * 1/1 * ?", "Every 2 minutes"),
            new Frequency("0 0 12 1 1/1 ?", "Every 1st of the month"),
            new Frequency("0 0 0/1 * * ?", "Every hour"),
            new Frequency("0 0 0 * * ?", "Every day at midnight"),
            new Frequency("0 0 12 1/2 * ? *", "Every two days at midnight")
        ];

        //

        vm.load = load;
        vm.save = save;

        //

        function load(){
            if ($routeParams && $routeParams.id){
                vm.id = $routeParams.id;
                vm.all = false;
                caService.findCAByID(vm.id)
                    .then(function (data) {
                        vm.ca = data.data;
                    })
                    .catch(function (error) {
                        toastr.error(error.data.message);
                    });
            } else {
                vm.id = null;
                vm.all = true;
            }
            caService.getDefaultCRLGeneration()
                .then(function (data) {
                    vm.default.cronExp = data.data.string1;
                    vm.default.freqDesc = data.data.string2;
                })
                .catch(function (error) {
                    toastr.error(error.data.message);
                });
        }

        function save(){
            if (vm.useDeafult){
                vm.newFreq = vm.default;
            } else {
                if (vm.newFreq.cronExp === ''){
                    toastr.error('Choose something.');
                    return;
                }
                var found = findByCronExp(vm.newFreq.cronExp);
                vm.newFreq.freqDesc = found.freqDesc;
            }
            if (vm.all){
                caService.rescheduleAllCAs(vm.newFreq.cronExp, vm.newFreq.freqDesc)
                    .then(function () {
                        toastr.info('Frequency changed for all CAs.');
                        load();
                    })
                    .catch(function (error) {
                        toastr.error(error.data.message);
                    });
            } else {
                console.log(vm.newFreq.cronExp);
                caService.rescheduleCA(vm.id, vm.newFreq.cronExp, vm.newFreq.freqDesc)
                    .then(function () {
                        toastr.info('Frequency changed.');
                        $window.location.href = '/#/ca/' + vm.id;
                    })
                    .catch(function (error) {
                        toastr.error(error.data.message);
                    });
            }
        }

        function Frequency(c, d){
            var fr = new Object();
            fr.cronExp = c;
            fr.freqDesc = d;
            return fr;
        }

        function findByCronExp(exp){
            for (var i = 0; i < vm.options.length; i++){
                var freq = vm.options[i];
                if (freq.cronExp === exp)
                    return freq;
            }
            return null;
        }

    }

})();
