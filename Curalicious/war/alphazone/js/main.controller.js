angular.module('main.controller', [])

.controller('main.controller', function($scope, $http) {

	$scope.subareaname = " | Alphazone";
	$scope.slogan = "";
	$scope.notauthorized = "";
	
	$scope.checkUserRole = function(){
		$http({
			method : "GET",
			url : "/rest/session/public/userhasrole?role=ROLE_CORE_USER_ALPHAZONE"
			
		}).success(function(data, status, headers, config) {
			console.log(data);
			if(data.sucess == true){
				
			} else {
				$scope.notauthorizedreason = data.errormessage;
			}
		});
	}
	
	$scope.createSidebarMenu = function(){
		$http({
			method : "GET",
			url : "/rest/alphazonemenu/user/list"
			
		}).success(function(data, status, headers, config) {
			console.log(data);
			if(data.sucess == true){
				$scope.sidebarentries = data.list;
			} else {
				$scope.notauthorizedreason = data.errormessage;
			}
		});
		
	};
	
	
	$scope.createSidebarMenu();
	$scope.checkUserRole();
	
});