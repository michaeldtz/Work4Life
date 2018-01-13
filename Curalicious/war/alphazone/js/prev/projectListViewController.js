ui.mvc.newViewController = function(view, viewController){
	
	var projectListArea	= view.find("#repositoryProjectsListArea");	
	var addProBtn    	= view.find("#createProjectButton");
	var projectList;		
	
	var urlList			= "uapi/repository/listProjects";
	var urlDelete		= "uapi/repository/deleteProject";
	var urlCreate		= "uapi/repository/createProject";
	var urlCreateWTempl	= "uapi/repository/createProjectFromTemplate";	
	var urlListTempl	= "uapi/repository/listProjectTemplates";
	
	/*
	 * On load of view and viewcontroller
	 */
	viewController.onLoad = function(){
		
		 projectList = new ui.controls.List(projectListArea, {
			 onSelect 	 : projectSelected,
			 icon		 : "resources/images/icons/package.png",
			 addFunction : addNewProject,
			 delFunction : deleteProject
		 });
		 
		
	}; //end of onload
	
	viewController.onAppear = function(){
		reloadProjectList();		
	};	
	
	viewController.onNavigation = function(attributes){
		if(attributes.admin == true){
			urlList			= "aapi/repository/listProjects";
			urlDelete		= "aapi/repository/deleteProject";
			urlCreate		= "aapi/repository/createNewCoreProject";
			urlCreateWTempl	= "aapi/repository/createProjectFromTemplate";	
			urlListTempl	= "aapi/repository/listProjectTemplates";
			reloadProjectList();	
		}	
	};	
	
	
	function addNewProject(){
		

		
		ajax.get(urlListTempl, function(tempList){
			
			var popup  = $("<div></div>");
			var input  = $("<input id='name' name='name' style='width: 200px'>");
			var select = $("<select style='width: 200px'></select>");
			
			select.append("<option value=''>No Template</option>");
			for(var i = 0; i < tempList.length; i++){
				select.append("<option value='" + tempList[i].id + "'>" + tempList[i].name + "</option>");
			}
			
			$("<p></p>").append("<span>Name: </span>").append(input).appendTo(popup);
			$("<p></p>").append("<span>Template: </span>").append(select).appendTo(popup);
			$("<p></p>").append("<button >Create Project</option>").appendTo(popup);
			
			$.blockUI({ message: popup, css: { width: '375px' } }); 
			$('.blockOverlay').attr('title','Click to unblock').click($.unblockUI); 
			
			$(popup).find("button").click(function(){
				var name  = input.val();
				var templ = select.val();
				
				ajax.post(urlCreateWTempl, { name : name, templateID : templ }, function(result){
					reloadProjectList();
					$.unblockUI();
				});				
			});
			
			
		});
		
		/*
		var name = prompt("Please enter the name of the new project.");
		if(name !== undefined && name !== null){
			ajax.post(urlCreate, { name : name }, function(result){
				reloadProjectList();
			});
		}
		*/
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
