angular.module('main.controller', ['breadcrumb.controller'])

.controller('main.controller', function($scope, $rootScope, $http, session) {

	$scope.redirectAfterLogin = "?redirect=/admin";
	
	session.checkSession(function(data){
		console.log("SESSIONINFO", data);
	});
	//$rootScope.addBreadcrumb({ name : "Admin", url : "/admin" });
	
	$scope.doInit = function(){
		$http({
			method : "POST",
			url : "/rest/initialization/admin/initialize"
			
		}).success(function(data, status, headers, config) {
			console.log(data);
			
		});
		
		
		
	}
	
	
	
	
});