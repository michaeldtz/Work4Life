 angular.module('session.service', [])

.factory('session', ["$http", function($http) {

	var service = {};
	 
	service.isUserLoggedIn = function(){
		
	};
	
	return service;
	
}]);