angular.module('logout.controller', [])

.controller('logout.controller', function($scope, $http) {

	
	$scope.login = function(){
		
		window.location.href = "/login/login.html";
		
	};
	
	

	

});