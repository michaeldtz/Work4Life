ui.mvc.newViewController = function(view, viewController){
	
	var selectedProjectID 	= 0;
	var items				= [];
	
	var urlList				= "uapi/repository/project/listItems";
	var urlDelete			= "uapi/repository/deleteItem";
	var urlCreate			= "uapi/repository/createItem";
	var urlGet				= "uapi/repository/getItem";
	var urlUpload			= "uapi/repository/uploadNewItem";
	var urlDownload			= "uapi/repository/downloadProject";
	var urlUnzip			= "uapi/repository/unzipItem";
	var urlUpdate			= "uapi/repository/updateItemAttribute";
	

	//Container in the HTML
	var ideEditorArea		= view.find("#ideCode");
	var ideTabBar 			= view.find("#ideTabbar");
	var ideToolbar 			= view.find("#ideToolbar");
	var itemListArea	 	= view.find("#ideContentList");
	var dropZone			= view.find("#dropZone");
	
	//Controls
	var itemList			= undefined;
	var dropUpload			= undefined;
	
	var projectDetailsVC	= undefined;
	var itemDetailsVC		= undefined;
	var contentItemOpen     = [];
	
	/*
	 * On load of view and viewcontroller
	 */
	viewController.onLoad = function(){
		
		var projectEntry = $("<div class='projectEntry'><img src='resources/images/icons/package.png'><span>Project</span></div>");
		projectEntry.click(showProjectMasterdata);
		
		itemList = new ui.controls.List(itemListArea, {
			 onSelect 	 : itemSelected,
			 icon		 : iconURL,
			 title       : projectEntry,
			 //itemRender: renderItem,
			 addFunction : addNewItem,
			 delFunction : deleteItem,
			 favFunction : deleteItem,
			 contextMenu : {
				 callback : listContextMenu,
				 items    : { "rename" : { name : "Rename File"} } 
			 }
		 });
		
		dropUpload = new ui.controls.DropUpload(dropZone, {
			fullscreenDrop : true,
			uploadURL      : urlUpload + "?projectID=" + selectedProjectID ,
			onFinished     : uploadFinished
		});
		
		view.find("#btnRun").unbind("click").click(function(){
			var itemID   = $(".ideCodeTab.active").attr("itemID");
			var tabEntry = getOpenedTabEntryForID(itemID);
			if(tabEntry != undefined && tabEntry.item != undefined)
				runItem(tabEntry.item);
		});
		
		view.find("#btnOpenContent").unbind("click").click(function(){
			var itemID   = $(".ideCodeTab.active").attr("itemID");
			var tabEntry = getOpenedTabEntryForID(itemID);
			if(tabEntry != undefined && tabEntry.item != undefined)
				openContent(tabEntry.item);
		});
		
		
		
		//Register Context Menu
		ideTabBar.contextMenu({
	        selector: '.ideCodeTab', 
	        callback: function(key, options) {
	        	if(key == "close"){
		        	var itemID = $(this).attr("itemid");
		        	closeTabByID(itemID);
	        	} else if(key == "closeAll"){
	        		while(contentItemOpen.length > 0){
	        			closeTabByID(contentItemOpen[0].id);
	        		}
	        	} else if(key == "closeOth"){
	        		var itemID = $(this).attr("itemid");
	        		while(contentItemOpen.length > 1){
	        			if(itemID != contentItemOpen[0].id)
	        				closeTabByID(contentItemOpen[0].id);
	        			if(itemID != contentItemOpen[1].id)
	        				closeTabByID(contentItemOpen[1].id);
	        		}
	        		activateTabByID(itemID);	        			
	        	}
	        },
	        items: {
	            "close":   {name: "Close"},
	            "closeAll":   {name: "Close All"},
	            "closeOth":   {name: "Close Others"},
	        }
	    });
		
	}; 

	
	viewController.onNavigation = function(navigationParameters){
		if(navigationParameters != undefined){
			selectedProjectID = navigationParameters.projectID;
		}
		
		ideEditorArea.empty();
		ideTabBar.empty();
		contentItemOpen = [];
		
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
	
	
	function itemSelected(object){		
		if(object == undefined)
			return;
		
		var openedItem = getOpenedTabEntryForID(object.id);	
		
		if(openedItem != undefined){
			activateTabByID(object.id);
		} else {
			loadItemDetails(object);
		}
		
		if(endsWith(object.name,".etd")){
			view.find("#btnOpenContent").show();
		} else {
			view.find("#btnOpenContent").hide();
		}
		
		console.log(object);
	}
	
	function endsWith(str, suffix) {
    	return str.indexOf(suffix, str.length - suffix.length) !== -1;
	}
	
	
	function getOpenedTabEntryForID(itemID){
		var tabEntry = undefined;
		for(var i = 0; i < contentItemOpen.length; i++){
			if(itemID == contentItemOpen[i].id)
				tabEntry = contentItemOpen[i];
		}
		return tabEntry;
	}
	
	function closeTabByID(itemID){
		var tabEntry = getOpenedTabEntryForID(itemID);
		if(tabEntry != undefined){
			if(tabEntry.editor != undefined)
				tabEntry.editor.destroy();
			tabEntry.divArea.detach();
			tabEntry.tabArea.detach();
			
			for(var i = 0; i < contentItemOpen.length; i++){
				if(itemID == contentItemOpen[i].id)
					contentItemOpen.splice(i,1);
			}
		}
	}
	
	function activateTabByID(itemID){
		var tabEntry = getOpenedTabEntryForID(itemID);
		if(tabEntry != undefined){
			$(".ideCodeArea").removeClass("active").hide();
			$(".ideCodeTab").removeClass("active");			
			tabEntry.divArea.addClass("active").show();
			tabEntry.tabArea.addClass("active");
		}
	}
	
	function getNextPreviousHandlerFor(itemID){
		var idx = 0;
		return function(next){
			var currItem = $(this);		
			var nextItem;
			
			if(next)			
				var nextItem = currItem.next(".ideCodeTab");
			else 
				var nextItem = currItem.prev(".ideCodeTab");
			
			if(nextItem.length > 0){
				$(".ideCodeArea").removeClass("active").hide();
				$(".ideCodeTab").removeClass("active");			
				nextItem.divArea.addClass("active").show();
				nextItem.tabArea.addClass("active");
			}
		}
	}	

	function loadItemDetails(object){		
		
		ajax.get(urlGet + "?id=" + object.id, function(result){
			var item = result;
			
			//Hide all other Editors, Deactivate all other tabs
			$(".ideCodeArea").removeClass("active").hide();
			$(".ideCodeTab").removeClass("active")
			
			openedItem         = {};
			openedItem.item    = item;
			openedItem.id      = item.id;
			openedItem.divArea = $("<div class='ideCodeArea active' itemid='" + item.id + "'></div>").appendTo(ideEditorArea);
			openedItem.tabArea = $("<div class='ideCodeTab active' itemid='" + item.id + "'>" + item.name + "</div>").appendTo(ideTabBar);
				
			openedItem.tabArea.click(function(){
				itemSelected(object);
			});
			
			
			if(isEditableFile(item.name)){

				 var aceSettings = {
						updateEvent   : function(newValue, editorControl, callback){
							updateItem(item, editorControl, callback);
						},
						statusUpdate  : getStatusUpdateHandlerFor(openedItem),
						nextPrev      : getNextPreviousHandlerFor(item.id)
				}
				
				openedItem.type    = "code";	
				openedItem.editor  = new ui.controls.AceCodeEditor(openedItem.divArea, aceSettings);
				

				var content  = item.content || "";
				openedItem.editor.setValueBase64(content);
				openedItem.editor.gotoLine(1);
			
				if(item.name.endsWith(".html"))
					openedItem.editor.setMode("html");
				else if(item.name.endsWith(".js")||item.name.endsWith(".sjs"))
					openedItem.editor.setMode("javascript");
				else if(item.name.endsWith(".css"))
					openedItem.editor.setMode("css");
				else
					openedItem.editor.setMode("text");;
				
				contentItemOpen.push(openedItem);
			
			} else if(isShowableFile(item.name)) {
				
				var contentURL = "repo/" + item.projectID + "/" + item.name;
				
				openedItem.type    = "showable";			
				openedItem.showFrame = $("<iframe class='previewFrame' src='" + contentURL + "' width='100%' height='" + openedItem.divArea.height() + "px'>").appendTo(openedItem.divArea);
				contentItemOpen.push(openedItem);
				
				
			} else {

				if(item.name.endsWith("zip") == true){
					openedItem.type     = "zip";			
					openedItem.unzipBtn = $("<button class='bluebutton'>Unzip this File into the Project</button>").appendTo(openedItem.divArea);
					contentItemOpen.push(openedItem);
					
					openedItem.unzipBtn.click(function(){
						openedItem.unzipBtn.detach();
						$.blockUI({ message: "Unzipping File", css: { width: '175px', height: "100px" } }); 
						ajax.post(urlUnzip, { id : item.id }, function(){
							$.unblockUI();
							reloadItemList();
						});						
					});
				
				}
			}
			
		});
	}
	
	
	

	function updateItem(item, editor, callback){		
		if(item !== undefined){
		
			if(isEditableFile(item.name)){
				item.content	   	= editor.getValueBase64();
			}
			
			if(item !== undefined){
				ajax.post("uapi/repository/updateItem", JSON.stringify(item) , function(result){
					if(typeof callback == "function")
						callback(result);
				});	
			}
		}
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

	function listContextMenu(key, itemID, listEntry) {
		if(key == "rename"){
			for(var i = 0; i < items.length; i++){
				if(items[i].id == itemID){
					var name = items[i].name;
					var chgName = prompt("Change Name", name);
					
					if(chgName != "" && chgName != name){
						$.post(urlUpdate, { id : itemID, attribute : "Name", value : chgName }, function(){
							reloadItemList();
						});
					}
					return;
				}
			}
		}
	}

	function showProjectMasterdata(){
		var container = $("<div></div>");
		ui.mvc.loadViewControllerAndStyle("labsUser/repository/repoEditor/projectDetails", container, function(viewController){
			viewController.getView().show();
			viewController.setSelectedProject(selectedProjectID);
			$.blockUI({ message: container, css: { width: '700px' } });
			$('.blockOverlay').attr('title','Click to unblock').click($.unblockUI);
		});
	}
	
	function runItem(item){
		var contentURL = "repo/" + selectedProjectID + "/" + item.name;
		window.open(contentURL,'_newtab');
	}
	
	
	function openContent(item){
		var attr= ui.navi.encodeAttributes({ projectID : selectedProjectID, itemName : item.name });
		var contentURL = "/#/dataviewer?attr=" + attr;
		window.open(contentURL,'_newtab');
	}
	
	
	function setEmptyItem(animate){
	
	}

	function reloadItemList(){
		ajax.get(urlList + "?projectID=" + selectedProjectID,function(result){
			items = result;
			itemList.setListContent(result);
		});
	}

	
	function getStatusUpdateHandlerFor(openedItem){
		return function(text, code){
			if(code > 0)
				openedItem.tabArea.addClass("unsaved");
			else
				openedItem.tabArea.removeClass("unsaved");
		};
	}
	
	function iconURL(entry){
		if(entry.name.endsWith(".html"))
			return "resources/images/icons/html.png";
		else if(entry.name.endsWith(".htm"))
			return "resources/images/icons/html.png";
		else if(entry.name.endsWith(".css"))
			return "resources/images/icons/css.png";
		else if(entry.name.endsWith(".js"))
			return "resources/images/icons/script_code.png";
		else if(entry.name.endsWith(".sjs"))
			return "resources/images/icons/script_code_red.png";
		else if(entry.name.endsWith(".json"))
			return "resources/images/icons/database_table.png";
		else if(entry.name.endsWith(".jpg"))
			return "resources/images/icons/images.png";
		else if(entry.name.endsWith(".png"))
			return "resources/images/icons/images.png";
		else if(entry.name.endsWith(".gif"))
			return "resources/images/icons/images.png";
		else if(entry.name.endsWith(".etd"))
			return "resources/images/icons/database_table.png";
		else 
			return "resources/images/icons/page_white_text.png";
	}
	
	function isEditableFile(filename){
		if(filename.endsWith(".html") || filename.endsWith(".htm") 
		|| filename.endsWith(".css") || filename.endsWith(".js")
		|| filename.endsWith(".sjs") || filename.endsWith(".txt")
		|| filename.endsWith(".etd"))
			return true;
		
		return false;
	}
	
	function isShowableFile(filename){
		if(filename.endsWith(".jpg") || filename.endsWith(".png") 
		|| filename.endsWith(".pdf"))
			return true;
		
		return false;
	}
	
	
	
	
	
	
	
	
};
