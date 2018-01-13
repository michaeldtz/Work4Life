angular.module('breadcrumb.controller', [])

.controller('breadcrumb.controller', function($scope, $rootScope, $http) {
	
	
		$scope.breadcrumbs = [
		     { name : "Curalicious", url : "/" }
		];
	
		$rootScope.addBreadcrumb = function(breadcrumb){
			$scope.breadcrumbs.push(breadcrumb);
		};
	
	
});