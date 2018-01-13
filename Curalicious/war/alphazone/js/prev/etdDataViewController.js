ui.mvc.newViewController = function(view, viewController){
	
	var selectedProjectID 	= 0;
	var items				= [];
	
	var urlList				= "uapi/repository/project/listItems";
	

	//Container in the HTML
	var entityContent		= view.find("#entitycontent");
	var entityName 			= view.find("#entityname");
	var projectid 			= view.find("#projectid");

	viewController.onLoad = function(){
		
			
	}; 

	
	viewController.onNavigation = function(navigationParameters){
		var passedProjectId  = navigationParameters.projectID;
		var passedEntityName = navigationParameters.itemName;
		
		entityName.val(passedEntityName);
		projectid.val(passedProjectId);
	};
	
	
	viewController.loadData = function(){
		
	}
	
	viewController.onAppear = function(){
	
	};
	
				
	
};
