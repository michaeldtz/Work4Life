ui.mvc.newViewController = function(view, viewController){
	
	viewController.renderNavigationArea = function(loginResult){
		
		view.empty();
		
		if(loginResult.loggedIn == true){
			if(loginResult.isAdmin == true){
				
				
				addNavigationItem("Admin","adminArea");	
			}
			
			if(loginResult.hasAppRole == true){ 
				
				addNavigationItem("Web Prototypes","projectList");
				
				
			} else {
				
				
				addNavigationItem("Request Invitation","reqInvitation");	
			}	
		}
		
	}
	
	function addNavigationItem(text, navigationTargetOrFunction){
		
		var separator = "";
		if($("a.navilink").length >= 1)
			separator = "|";
		
		if(typeof navigationTargetOrFunction == "function"){
			var btn = $("<a class='navilink' href='#/'> "+ separator +" <span class='title link'>" + text + "</span>  </a>");
			view.append(btn);
		
			btn.click(navigationTargetOrFunction);
		} else {
			
			var btn = $("<a class='navilink' href='#/"+ navigationTargetOrFunction + "'> "+ separator +" <span class='title link'>" + text + "</span>  </a>");
			view.append(btn);
		}
		
	}
	
	return viewController;
};
