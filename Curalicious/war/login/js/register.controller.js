angular.module('register.controller', [])

.controller('register.controller', function($scope, $http) {

	$scope.prename = "";
	$scope.surname = "";
	$scope.emailAdress = "";
	$scope.emailAdressConfirm = "";
	$scope.password = "";
	$scope.passwordConfirm = "";
	$scope.termsAndCondiditions = false;
	
	$scope.showLogin = true;
	$scope.showSuccess = false;
	
	$scope.errormessage = "Please let us know something about you";
	$scope.errorclass = "";
	$scope.errorclass_username = "";
	$scope.errorclass_password = "";

	$scope.loginnow = function(){
		window.location.href = "/login/login.html";
	};
	
	$scope.register = function() {
		
		
		//Check all input is filled
		//Check password matching
		//check email matching
		var data = {
				prename 			: $scope.prename,
				surname 			: $scope.surname,
				emailAdress 		: $scope.emailAdress,
				//emailAdressConfirm	: $scope.emailAdressConfirm,
				password 			: $scope.password,
				passwordConfirm 	: $scope.passwordConfirm,
				tac					: $scope.termsAndCondiditions
		};
		
		$scope.password = "";
		$scope.passwordConfirm = "";
		
		
		$http({
			method : "POST",
			url : "/rest/registration/public/register",
			data : data			
		}).success(function(response, status, headers, config) {
			if(response.success == true){
				$scope.showLogin = false;
				$scope.showSuccess = true;
			} else {
				
				$scope.errormessage = response.message;
				if(response.reference !== undefined)
					$scope["errorclass_" + response.reference] = "warn";
				
				
			}
			
			
		});
		

	};
	
	$scope.checkEmail = function(){
		
		$http({
			method : "GET",
			url : "/rest/registration/public/checkusername?username=" + $scope.emailAdress			
		}).success(function(response, status, headers, config) {
			if(response.UsernameExisting == "Yes"){
				$scope.errormessage_emailAdress = "Email address is already in use";
				$scope.errorclass_emailAdress = "error";
			} else {
				$scope.errormessage_emailAdress = "";
				$scope.errorclass_emailAdress = "";
			}
		});
		
	};

});