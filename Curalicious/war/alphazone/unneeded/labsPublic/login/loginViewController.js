ui.mvc.newViewController= function(view, viewController){
	
	var openIDProviders;
	
	viewController.onLoad = function(){
		initOpenIDProviderSelect();
	};
	
	viewController.onAppear = function(){
		//view.children().hide();
		registerLoginButton();
		
		/*var loginForm = view.find("#loginForm");
		ui.sizing.centerBoxOnScreen(loginForm);
		*/
	};
	
	
	function registerLoginButton(){
		var select = view.find("#openIDProviderSelect");
		var button = view.find("#loginButton");
		button.click(function(){
			var selectedProvider = select.find(":selected").text();
			var selectedURL = openIDProviders[selectedProvider];
			window.location = selectedURL;
		});
	}
	
	
	function initOpenIDProviderSelect(){
		$.get("papi/session/getOpenIDProvider", function(result){
			view.children().show();
			openIDProviders = result.openIDProviders;
			var select = view.find("#openIDProviderSelect");	
			select.empty();
			for(var providerName in openIDProviders){
				var providerURL = openIDProviders[providerName];
				var option = $("<option>" + providerName + "</option>");
				
				if(providerName == "Google")
					option.attr("selected", "selected")
				
				select.append(option);
			}
			
		});		
	}
	
	return viewController;
};
