ui.mvc.newViewController = function(view, viewController){
	

	viewController.onLoad = function(){
		console.log("LOADED");
	};
	
	viewController.onAppear = function(){
		console.log("APPEAERED");
	};
	
};
