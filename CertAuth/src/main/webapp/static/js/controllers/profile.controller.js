(function(){

    'use strict';

    angular.module('certApp').controller('ProfileController', ProfileController);
    ProfileController.$inject = ['userService', '$window', 'toastr', '$uibModal', '$scope'];

    function ProfileController(userService, $window, toastr, $uibModal, $scope){

        var vm = this;

        vm.changePass = {
            'string1': '',
            'string2': '',
            'repeatPassword': ''
        };
        vm.user = {};

        //

        vm.getLoggedInUser = userService.getLoggedInUser;
        vm.changePassword = changePassword;
        vm.openModal = openModal;
        vm.closeModal = closeModal;
        vm.getUser = getUser;

        //

        function changePassword(){
            if (vm.changePass.string1 == ''){
                toastr.error("You did not enter the old password.");
                return;
            }
            if (vm.changePass.string2 == ''){
                toastr.error("You did not enter a new password.");
                return;
            }
            if (vm.changePass.repeatPassword !== vm.changePass.string2){
                toastr.error("Passwords don't match.");
                return;
            }
            if (vm.changePass.string1 === vm.changePass.string2){
                toastr.error("Cannot use the old password again.");
                return;
            }
            userService.changePassword(vm.changePass)
                .then(function(){
                    toastr.info("Password changed.");
                    closeModal();
                })
                .catch(function(error){
                    toastr.error(error.data.message);
                });
        }

        function openModal(size, parentSelector){
            var modalScope = $scope.$new();
            var parentElem = parentSelector ?
                angular.element($document[0].querySelector('.modal-demo ' + parentSelector)) : undefined;
            var modalInstance = $uibModal.open({
                animation: true,
                ariaLabelledBy: 'modal-title',
                ariaDescribedBy: 'modal-body',
                templateUrl: 'static/html/changePasswordModal.html',
                controller: 'ProfileController',
                controllerAs: 'userCtrl',
                size: size,
                appendTo: parentElem,
                scope: modalScope
            });
            modalScope.modalInstance = modalInstance;
        }

        function closeModal(){
            $scope.modalInstance.dismiss('cancel');
        }

        function getUser(){
            vm.user = userService.getLoggedInUser();
            console.log("This is vm.user: ");
            console.log(vm.user);
        }

    }

})();
