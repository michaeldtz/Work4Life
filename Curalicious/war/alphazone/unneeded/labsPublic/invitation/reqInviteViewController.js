ui.mvc.newViewController = function(view, viewController){
	
	var fullnameInp = view.find("#fullname");
	var emailInp    = view.find("#email");
	var reasonTA    = view.find("#reason");
	
	viewController.onLoad = function(){
		
	};
	
	viewController.onAppear = function(){
		registerButton();
		
		var loginForm = view.find("#logoutView");
		ui.sizing.centerBoxOnScreen(loginForm);
		
		
	};
	
	
	function registerButton(){
		var button = view.find("#requestButton");
		button.unbind("click");
		button.click(function(){
			button.attr("disabled", "disabled");
			ajax.post("papi/session/requestInviation", 
					{ fullname : fullnameInp.val(), email : emailInp.val(), reason : reasonTA.val() }, 
					function(result){
						if(result.success == true){
							alert("Thanks for requesting an invitation. You'll receive an email soon.");
							ui.navi.navigateToTarget("main");
						} else {
							console.log(result.error);
						}
			})
		});
	}
	
	
	return viewController;
};
