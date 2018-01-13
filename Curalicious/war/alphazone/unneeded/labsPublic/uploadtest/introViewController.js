ui.mvc.newViewController = function(view, viewController){
	
	viewController.onLoad = function(){
			
		
		 
	};
	
	viewController.onAppear = function(){
		
		refreshlist();
		
		view.find("#upload").click(function(){
			
			var obj = {
					nickname 		: "Michael",
					email    		: "michael.dtz@gmail.com",
					longtitude		: "12.343434",
					latitude		: "10.121211",
			};
			var json = JSON.stringify(obj);
			
			
			ajax.post("submitdata", json , function(blobKey){
				ajax.post("uploadimage?blobkey=" + blobKey, view.find("#content").val(), function(result){
					console.log(result);	
					refreshlist();
				});
			});
			
		});
		
	};
	
	function refreshlist(){
		var resultCnt = view.find("#resultCnt");
		resultCnt.empty();
		ajax.get("submitdata", function(result){
			resultCnt.append(result);
		});
	}
	
	
	return viewController;
};
