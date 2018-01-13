ui.mvc.newViewController = function(view, viewController){
	
	
	var selectedProjectID 	= 0;
	var	selectedProject		= undefined;
	
	var urlGet				= "uapi/repository/getProject";
	var urlUpdate			= "uapi/repository/updateProject";
	var urlListStartable	= "uapi/repository/project/listItems";
	var urlDownload			= "uapi/repository/downloadProject";
	
	var infoArea			= view.find("#infoArea");
	var _ui					= ui.mvc.loadAllElementsWithID(view);
	
	/*
	 * On load of view and viewcontroller
	 */
	viewController.onLoad = function(){
			view.hide();
	}; 
	
	viewController.onAppear = function(){
			
	};
	
	viewController.clear = function(){
		view.hide();
	};
	
	viewController.setSelectedProject = function(projectID, itemList){
		selectedProjectID = projectID;	
		reloadProject();
	};
	
	function reloadProject(){
		ajax.get(urlGet + "?id=" + selectedProjectID, function(result){
			selectedProject = result;
			showProjectDetails(selectedProject);
		});
	}
	
	function showProjectDetails(projectObj){
		
		view.show();
		
			_ui.infoarea_name.val(projectObj.name);
			_ui.infoarea_template.val(projectObj.template);
			_ui.infoarea_runitem.val(projectObj.startupItem);
			_ui.infoarea_vis.val(projectObj.displayAuth);
			_ui.infoarea_exe.val(projectObj.executeAuth);
			_ui.infoarea_chg.val(projectObj.changeAuth);
			
			//Load available startup items
			ajax.get(urlListStartable + "?projectID=" + selectedProjectID,function(result){
				_ui.infoarea_runitem.empty();
				var emptyOpt = $("<option value='0'>Not Startable</option>");
				_ui.infoarea_runitem.append(emptyOpt);
				for(var i = 0; i < result.length; i++){					
					var entry = result[i];
					var opt = $("<option value='" + entry.id + "'>" + entry.name + "</option>");
					_ui.infoarea_runitem.append(opt);					
				}
				_ui.infoarea_runitem.val(projectObj.startupItem);
			});
			
			infoArea.find("input,select").unbind("change").change(function(){
				updateProject();			
			});			
			
			/*_ui.infoarea_runlabs.unbind().click(function(){
				var contentURL = "labs/run/" + projectObj.id;
				window.open(contentURL,'_newtab');
			});*/
		
			_ui.infoarea_download.unbind().click(function(){
				var downloadURL = urlDownload + "?projectID=" + projectObj.id;
				window.open(downloadURL,'_newtab');
			});
	}
	
	
	function updateProject(){
		
		if(selectedProject !== undefined){
			selectedProject.name 		   	= _ui.infoarea_name.val();			
			selectedProject.template	  	= _ui.infoarea_template.val();
			selectedProject.startupItem		= _ui.infoarea_runitem.val();
			selectedProject.displayAuth		= _ui.infoarea_vis.val();
			selectedProject.executeAuth		= _ui.infoarea_exe.val();
			selectedProject.changeAuth		= _ui.infoarea_chg.val();
			
				
			if(selectedProject !== undefined){
				ajax.post(urlUpdate, JSON.stringify(selectedProject) , function(result){
					
				});	
			}
		}
	}
	
	
};
