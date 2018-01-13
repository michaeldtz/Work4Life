ui.mvc.newViewController = function(view, viewController){
	
	var projectListArea	= view.find("#repositoryProjectsListArea");	
	var addProBtn    	= view.find("#createProjectButton");
	var projectList;		
	
	var urlList			= "aapi/repository/listProjects";
	var urlDelete		= "aapi/repository/deleteProject";
	var urlCreate		= "aapi/repository/createProject";
	
	/*
	 * On load of view and viewcontroller
	 */
	viewController.onLoad = function(){
		
		 projectList = new ui.controls.List(projectListArea, {
			 onSelect 	 : projectSelected,
			 icon		 : "../../resources/images/icons/package.png",
			 addFunction : addNewProject,
			 delFunction : deleteProject
		 });
		 
		
	}; //end of onload
	
	viewController.onAppear = function(){
		reloadProjectList();		
	};	
	
	function addNewProject(){
		var name = prompt("Please enter the name of the new project.");
		if(name !== undefined && name !== null){
			ajax.post(urlCreate, { name : name }, function(result){
				reloadProjectList();
			});
		}
	}
	
	function deleteProject(object, item){
		var projectID = object.id;
		ajax.post(urlDelete, { id : projectID }, function(result){
			item.fadeOut();
		});
	}
	
	function projectSelected(object, index){
		var projectID = object.id;
		ui.navi.navigateToTarget("repository", { projectID : projectID });
	}	
	
	function reloadProjectList(){
		ajax.get(urlList,function(result){
			projectList.setListContent(result);
		});
	}
	
	
};
