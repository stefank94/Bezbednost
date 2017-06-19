(function(){

    'use strict';

    angular.module('certApp').filter('showDateFilter', function(){
        return function(input){

            var monthNames = [
                "January", "February", "March",
                "April", "May", "June", "July",
                "August", "September", "October",
                "November", "December"
            ];

            var date = new Date(input);

            var day = date.getDate();
            if (day < 10)
                day = "0" + day
            var month = monthNames[date.getMonth()];
            var year = date.getFullYear();
            var hour = date.getHours();
            if (hour < 10)
                hour = "0" + hour
            var minute = date.getMinutes();
            if (minute < 10)
                minute = "0" + minute

            return day + ". " + month + " " + year + ". " + hour + ":" + minute;


        };
    });

})();
