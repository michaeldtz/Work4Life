angular.module('login.controller', [])

.controller('login.controller', function($scope, $http) {

	$scope.j_username = "";
	$scope.j_password = "";
	$scope.errormessage = "Please Log In";
	$scope.errorclass = "";
	$scope.errorclass_username = "";
	$scope.errorclass_password = "";
	$scope.sessionActive = false;
	$scope.activeUsername = "";
	
	function getUrlVars() {
		var vars = [], hash;
	    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
	    for(var i = 0; i < hashes.length; i++)
	    {
	        hash = hashes[i].split('=');
	        vars.push(hash[0]);
	        vars[hash[0]] = hash[1];
	    }
	    return vars;
	  }
	
	$scope.checkSession = function(){
		
		$http({
			method : "GET",
			url : "/rest/session/public/check"
			
		}).success(function(data, status, headers, config) {
			console.log(data);
			if (data.status == "true" || data.username != undefined) {				
				$scope.sessionActive = true;
				$scope.activeUsername = data.username;
			} else {
				
			}
		});
		
	};
	
	$scope.logout = function(){
		
		window.location.href = "/logout";
		/*
		$http({
			method : "POST",
			url : "/logout",
			headers : {
				'Content-Type': 'application/x-www-form-urlencoded',
				"z_noredirect" : "true"
			}
						
		}).success(function(data, status, headers, config) {
			console.log(data);
		});
		*/
	};
	
	$scope.checkSession();
	
	$scope.login = function() {
		
		$scope.errorclass = "";
		$scope.errorclass_username = "";
		$scope.errorclass_password = "";
		
		if($scope.j_username == "" || $scope.j_password == ""){
			if($scope.j_username == ""){
				$scope.errorclass_username = "warn";
			} 
			if($scope.j_password == ""){
				$scope.errorclass_password = "warn";
			}
			$scope.errormessage = "Please enter user and password";
			return;
		}
		
		
		$http({
			method : "POST",
			url : "/loginservice",
			headers : {
				'Content-Type': 'application/x-www-form-urlencoded',
				"z_noredirect" : "true"
			},
			data : "j_username=" + $scope.j_username + "&j_password=" + $scope.j_password			
		}).success(function(data, status, headers, config) {
			if (data.success == true) {
				
				var urlVars = getUrlVars();
				
				$scope.errormessage = "Successful";		
				$scope.errorclass = "";
				
				if(urlVars.redirect !== undefined){
					window.location.href = urlVars.redirect;
				} else {
					window.location.href = "/dashboard/";
				}
				
			} else {
				$scope.errormessage = data.message;
				$scope.errorclass = "error";
				$scope.j_password = "";
			}
		});


	};

});