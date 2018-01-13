ui.mvc.newViewController = function(view, viewController){
	
	viewController.onLoad = function(){
			
		
		 
	};
	
	viewController.onAppear = function(){
		
		$(document).ready(function(){
			$("#slideshow").flexslider({
				//animation : "slide",
				//smoothHeight : true,
				pauseOnHover   : true,
				slideshowSpeed : 4000
			});
		});
	};
	
	function nextStep(){
		var curr = $("#slideshow").find("li a.active");
		var next  = curr.parent().next("li").find("a");
		
		if(next.length == 1)
			next.click();	
		else 
			$("#slideshow").find("li a").eq(0).click();
		
		setTimeout(nextStep, 5000);
	}
	
	return viewController;
};
