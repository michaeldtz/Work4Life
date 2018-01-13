ui.mvc.newViewController = function(view, viewController){
	
	var openIDProviders;
	
	viewController.onLoad = function(){
		
	};
	
	viewController.onAppear = function(){
		registerLogoutButton();
		
		//var loginForm = view.find("#logoutView");
		//ui.sizing.centerBoxOnScreen(loginForm);
		
	};
	
	
	function registerLogoutButton(){
		var button = view.find("#logoutButton");
		button.click(function(){
			$.get("papi/session/getLogoutURL", function(result){
				window.location = result.logoutURL;
			})
		});
	}
	
	
	return viewController;
};
