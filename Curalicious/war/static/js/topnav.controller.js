angular.module('topnav.controller', [])

.controller('topnav.controller', function($scope, $http) {

	$scope.firstname = "";	
	$scope.lastname = "";	
	$scope.showLogin = false;
	
	/*if($scope.redirectAfterLogin == undefined)
		$scope.redirectAfterLogin = ""; //?redirect=
	*/ 	
	if($scope.slogan == undefined){
		$scope.slogan = "Life Balancing and Work Simplification Platform";
	} 
	
	$http({
		method : "GET",
		url : "/rest/session/public/check"
		
	}).success(function(data, status, headers, config) {
		console.log(data);
		
		if (data.session == "true") {				
			$scope.sessionActive = true;
			$scope.firstname = data.username;
		} else {
			$scope.sessionActive = false;
			$scope.showLogin = true;
		}
		
	
		
	}).error(function(data, error){
		console.error(error, data);
	});
	
	
	$scope.toggleMenu = function(){
			
		if($("#wrapper").hasClass("toggled")){
			$('.hamburger').addClass('is-open');
			$('.hamburger').removeClass('is-closed');
		} else {
			$('.hamburger').removeClass('is-open');
			$('.hamburger').addClass('is-closed');
		}
		
		$("#wrapper").toggleClass("toggled");
		
	};
	
	$scope.gotoLogin = function(){
		window.location.href = "/login/login.html";
	}
	
	
});