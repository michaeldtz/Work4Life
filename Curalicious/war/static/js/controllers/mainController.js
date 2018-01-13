/**
 * Created by michael on 19.10.14.
 */

angular.module('app.mainController', ['ngAnimate', 'ngSanitize', 'textAngular', "app.formController", "app.mediaController"])

	.config(function($provide){
		
		$provide.decorator('taOptions', ['taRegisterTool', '$delegate', function(taRegisterTool, taOptions){
	        taRegisterTool('localimage', {
	            iconclass: "ion-images",
	            
	            action: function(){
	            	var image = prompt("Enter name of local media");
	            	if(image != undefined && image != null && image != "")
	            		this.$editor().wrapSelection('inserthtml',  '<img src="/services/content/media/' + image + '" class="localimage" />', 'red');
	            }
	        });
	        // add the button to the default toolbar definition
	        taOptions.toolbar[1].push('localimage');
	        return taOptions;
	    }]);
		
	})

    .run(function(){
    	
    })

    .controller('mainController', function($scope, $http) {
    	
    	
    	
    	$scope.accesskey = "";
		$scope.password  = "";
    	$scope.loggedin     = undefined;
    	$scope.projecttitle = "";
    	$scope.navbarclass = "navbarOut";
    	$scope.contentlist = [];
    	$scope.contentitem = undefined;
    	$scope.htmlcontent = "Heello ";
    	$scope.busy		   = false;
    	$scope.medialist   = [];
    	$scope.showMedia = false;
    	
    	$scope.checkLogin = function(){
    		
    		$http.get("../services/project/loggedin")
			.success(function(data, status, headers, config){
				if(data.status == "S"){
					$scope.loggedin = true;
					$scope.projecttitle = data.title;
					$scope.navbarclass = "navbarIn";
					$scope.loadContentList();
				} else {
					$scope.loggedin = false;
				}
			});
    		
    	};
    	
    	$scope.login = function(){
    		
    		var accesskey = $scope.accesskey;
    		var password = $scope.password;
    		
    		$http({
    				method	: "POST",
    				headers	: {'Content-Type': 'application/x-www-form-urlencoded'},
    				url 	: "../services/project/login", 
    				data 	: "accesskey="+ accesskey+"&password="+password
    			})
    			.success(function(data, status, headers, config){
    				if(data.status == "S"){
    					$scope.loggedin = true;
    					$scope.checkLogin();
    				}
    			});
    		
    	}
    	
    	$scope.logout = function(){
    		
    		$http.post("../services/project/logout").success(function(data){
    			$scope.loggedin = false;
    			$scope.navbarclass = "navbarOut";
    			$scope.contentitem = undefined;
    		});
    		
    	}
    	
    	$scope.reorderContentList = function(){
    		$scope.contentlist.sort(function(a,b){
    			return (a.sortorder > b.sortorder) ? 1 : -1;
    		});
    	}
    	
    	$scope.loadContentList = function(){
    		
    		$scope.contentlist = [];
    		$scope.contentitem = undefined;
    		
    		$http.get("../services/content/list")
    			.success(function(data){
    				$scope.contentlist = data;
    				for(var i in $scope.contentlist){
    					if($scope.contentlist[i].quiz == undefined || $scope.contentlist[i].quiz == ""){
    						$scope.contentlist[i].quiz = {
    								title : "",
    								questions : [],
    								results : []    								
    						};
    					} else {
    						$scope.contentlist[i].quiz = JSON.parse($scope.contentlist[i].quiz);	
    					}    					    					
    				}
    				$scope.reorderContentList();
    			});
    	}
    	
    	
    	
    	$scope.createContent = function(){
    		
    		var sortorder = 0;
    		try{
    			sortorder = $scope.contentlist[$scope.contentlist.length-1].sortorder;
    		} catch(e){
    			
    		}
    		
    		sortorder += 10;
    		var title = prompt("Enter the Title");
    		if(title == undefined)
    			return;
    		$http.post("../services/content/create", "title=" + title + "&sortorder="+sortorder)
    			.success(function(result){
    				
    				if(result.success == true){
	    				$scope.contentlist.push(
	    					result.object
	    				);
	    				
	    				$scope.contentitemSelected(result.object);
    				} else {
    					result.message;
    				}
    				
    				$scope.reorderContentList();
    			});
    	}
    	
    	$scope.contentitemSelected = function(selectedItem){
    		if($scope.askIfReset()){
	    		$scope.mastercontentitem = selectedItem;
	    		$scope.contentitem = angular.copy($scope.mastercontentitem);
	    		$scope.openArea('contentitem');
    		}
    	}
    	
    	$scope.askIfReset = function(){
    		if($scope.contentitem == undefined)
    			return true;
    		
    		if(!$scope.isUnchanged()){
    			var answer = confirm("You have unsaved changes, sure that you want to dismiss these?");
    			if(answer == true)
    				return true;
    			else
    				return false;
    		} 
    		return true;
    	};
    	
	    $scope.reset = function() {
	    	$scope.contentitem = angular.copy($scope.mastercontentitem);
	    };

	    $scope.isUnchanged = function(user) {
	      return angular.equals($scope.contentitem, $scope.mastercontentitem);
	    };
    	    
    	$scope.deleteItem = function(){
    		var really = confirm("Really delete this item");
    		if(really == true){
	    		var idToDelete = $scope.contentitem.id;
	    		$http.post("../services/content/delete", "id=" + idToDelete)
				.success(function(result){
					
					if(result.success == true){
						for(var i = 0; i < $scope.contentlist.length; i++){
							if($scope.contentlist[i].id == idToDelete){
								$scope.contentlist.splice(i,1);
							}
						}
						$scope.contentitem = undefined;
					}
				});
    		}
    	}
    	
    
    	$scope.updateItem = function(){
    		angular.copy($scope.contentitem, $scope.mastercontentitem);
    		$scope.busy = true;
    		var currentItem = angular.copy($scope.mastercontentitem);    		
    		currentItem.quiz = JSON.stringify(currentItem.quiz);
    		
    		$http.post("../services/content/update", JSON.stringify(currentItem))
			.success(function(data){
				$scope.busy = false;
				$scope.reorderContentList();
				
			});
    	}
    	
    	$scope.openArea = function(areaName){
    		if($scope.area == "contentitem"){
    			$scope.reset();
    		}
    		$scope.area = areaName;    
    		
    		if(areaName == "media"){
    			//$scope.loadMediaList();
    		}
    	} 
    	
    	$scope.checkIconInput = function(){
    		
    		if($scope.contentitem.icon.indexOf("ion-") == 0){
    			$scope.contentitem.icon = $scope.contentitem.icon.substr(4,$scope.contentitem.icon.length);
    		}
    		
    	}
    	
    	$scope.checkLogin();
    	
    	
    });