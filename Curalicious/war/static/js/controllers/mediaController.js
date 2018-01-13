/**
 * Created by michael on 19.10.14.
 */

angular.module('app.mediaController', ['ngAnimate', 'ngSanitize', 'textAngular'])

	.config(function($provide){
		
		
		
	})

    .run(function(){
    	
    	
    })

    .controller('mediaController', function($scope, $http) {
    

		$(function () {
  			$('[data-toggle="tooltip"]').tooltip()
		})
		
		var dropzone = $("#uploadarea");
    	dropUpload = new ui.controls.DropUpload(dropzone, {
			fullscreenDrop : false,
			uploadURL      : "/services/content/uploadmedia",
			onFinished     : function(){
				$scope.loadMediaList();
			}
		});
		
    	$scope.loadMediaList = function(){
			$scope.medialist = [];
			//setTimeout(function(){
	    		$http.get("../services/content/listmedia")
				.success(function(data){
					$scope.medialist = data;
				});
	    		//},500);
    	}
		
		$scope.deleteMediaItem = function(mediaItem){
			var really = confirm("Really delete this media item");
    		if(really == true){
    			$scope.medialist = [];
	    		var idToDelete = mediaItem.id;
	    		$http.post("../services/content/deleteMedia", "id=" + idToDelete)
				.success(function(data){
					$scope.loadMediaList();
				});
    		}
    		
		}
    	
    	$scope.loadMediaList();
    	
    
    });