ui.mvc.newViewController = function(view, viewController){
	
	var selectedProjectID 	= 0;
	var selectedItemID   	= 0;
	var selectedItem		= undefined;
	
	var urlList				= "uapi/repository/project/listItems";
	var urlDelete			= "uapi/repository/deleteItem";
	var urlCreate			= "uapi/repository/createItem";
	var urlGet				= "uapi/repository/getItem";
	var urlUpload			= "uapi/repository/uploadNewItem";
	var urlDownload			= "uapi/repository/downloadProject";
	
	var contentArea			= view.find("#repositoryContentEditorArea");
	var itemListArea	 	= view.find("#repositoryContentListArea");
	var itemList			= undefined;
	var dropZone			= view.find("#dropzone");
	var dropUpload			= undefined;
	
	var projectDetailsVC	= undefined;
	var itemDetailsVC		= undefined;
	
	
	
	/*
	 * On load of view and viewcontroller
	 */
	viewController.onLoad = function(){
		
		var projectEntry = $("<div class='projectEntry'><img src='resources/images/icons/package.png'>Project</div>");
		itemListArea.append(projectEntry);
		projectEntry.click(showProjectMasterdata);
		
		itemList = new ui.controls.List(itemListArea, {
			 onSelect 	 : itemSelected,
			 icon		 : iconURL,
			 top		 : "20px",
			 //itemRender: renderItem,
			 addFunction : addNewItem,
			 delFunction : deleteItem
		 });
		
		dropUpload = new ui.controls.DropUpload(dropZone, {
			uploadURL  : urlUpload + "?projectID=" + selectedProjectID ,
			onFinished : uploadFinished
		});
		
		ui.mvc.loadMVC("labsUser/repository/repoEditor/projectDetails", undefined, function(viewController, view){
			projectDetailsVC = viewController;
			showProjectMasterdata();
		});
		
		ui.mvc.loadMVC("labsUser/repository/repoEditor/itemDetails", undefined, function(viewController, view){
			itemDetailsVC = viewController;
		});
			
	}; 
	
	viewController.onNavigation = function(navigationParameters){
		if(navigationParameters != undefined){
			selectedProjectID = navigationParameters.projectID;
			
			if(projectDetailsVC != undefined)
				showProjectMasterdata();
		}
		
		if(dropUpload !== undefined)
			dropUpload.settings("uploadURL", urlUpload + "?projectID=" + selectedProjectID);
	}
	
	viewController.onAppear = function(){
		if(projectDetailsVC != undefined)
			projectDetailsVC.clear();
		
		if(itemDetailsVC != undefined)
			itemDetailsVC.clear();
		
		reloadItemList();		
	};
	
	function iconURL(entry){
		if(entry.name.endsWith(".html"))
			return "../../resources/images/icons/html.png";
		else if(entry.name.endsWith(".htm"))
			return "../../resources/images/icons/html.png";
		else if(entry.name.endsWith(".css"))
			return "../../resources/images/icons/css.png";
		else if(entry.name.endsWith(".js"))
			return "../../resources/images/icons/script_code.png";
		else if(entry.name.endsWith(".sjs"))
			return "../../resources/images/icons/script_code_red.png";
		else if(entry.name.endsWith(".json"))
			return "../../resources/images/icons/database_table.png";
		else if(entry.name.endsWith(".jpg"))
			return "../../resources/images/icons/images.png";
		else if(entry.name.endsWith(".png"))
			return "../../resources/images/icons/images.png";
		else if(entry.name.endsWith(".gif"))
			return "../../resources/images/icons/images.png";
		else if(entry.name.endsWith(".etd"))
			return "../../resources/images/icons/database_table.png";
		else 
			return "../../resources/images/icons/page_white_text.png";
	}
	
	function addNewItem(){
		var name = prompt("Please enter the name of the new project.");
		if(name !== undefined && name !== null && name !== ""){
			ajax.post(urlCreate, { name : name, projectID : selectedProjectID }, function(result){
				reloadItemList();
				itemSelected({ id : result.newId });
			});
		}
	}
	
	function deleteItem(object, item){
		var contentID = object.id;
		ajax.post(urlDelete, { id : contentID }, function(result){
			item.fadeOut();
			setEmptyItem();
		});
	}
	
	function uploadFinished(result){
		reloadItemList();
	}
	
	function itemSelected(object){		
		itemDetailsVC.setSelectedItemID(object.id);
		ui.navi.navigateVC(itemDetailsVC, contentArea);
	}
	
	function showProjectMasterdata(){
		itemListArea.find(".selected").removeClass("selected");
		projectDetailsVC.setSelectedProject(selectedProjectID);
		ui.navi.navigateVC(projectDetailsVC, contentArea);
	}
	
	function setEmptyItem(animate){
		showProjectMasterdata();
	}

	function reloadItemList(){
		ajax.get(urlList + "?projectID=" + selectedProjectID,function(result){
			itemList.setListContent(result);
		});
	}

	function isEditableFile(filename){
		if(filename.endsWith(".html") || filename.endsWith(".htm") 
		|| filename.endsWith(".css") || filename.endsWith(".js")
		|| filename.endsWith(".sjs") || filename.endsWith(".txt")
		|| filename.endsWith(".etd"))
			return true;
		
		return false;
	}
	
	
	
	
	
	
	
	
};
