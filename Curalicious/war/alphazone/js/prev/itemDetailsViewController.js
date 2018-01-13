ui.mvc.newViewController = function(view, viewController){
	
	
	var selectedProjectID 	= 0;
	var selectedItemID   	= 0;
	var selectedItem		= undefined;
	
	var urlList				= "uapi/repository/project/listItems";
	var urlGet				= "uapi/repository/getItem";
	var urlUpload			= "uapi/repository/uploadNewItem";
	var urlUnzip			= "uapi/repository/unzipItem";
	
	var contentArea			= view.find("#repositoryContentEditorArea");
	var editorDiv	 		= view.find("#contentEditorContainer");
	var previewDiv   	 	= view.find("#contentPreview");
	var otherDiv    	 	= view.find("#contentOthers");
	var docStatus    	 	= view.find("#docStatus");
	var fullScreenBtn 	 	= view.find("#fullscreen");
	var editorWrap			= view.find("#editorWrapper");
	
	var editor		 		= undefined;
	
	
	var infoArea			= view.find("#infoArea");
	var _ui					= ui.mvc.loadAllElementsWithID(view);	
	
	
	
	/*
	 * On load of view and viewcontroller
	 */
	viewController.onLoad = function(){
		view.hide();
		
	
	    editor = new ui.controls.AceCodeEditor(editorDiv , {
			updateEvent   : updateItem,
			statusUpdate  : function(text){
				docStatus.text(text);
			}
		});
	    
	    fullScreenBtn.click(function(){
	    	
			 if(!editorWrap.hasClass("fullscreenEditor"))
				 editorWrap.addClass("fullscreenEditor");
			 else
				 editorWrap.removeClass("fullscreenEditor");
		     editor.resize();
		     
		    	
		    });
	     
	    editorWrap.hide();
		previewDiv.hide();
		otherDiv.hide();
	}; 
	
	viewController.onAppear = function(){
		setEmptyItem(false);
		loadItemDetails();
		
		editor.resize();
		
		
	};
	
	viewController.clear = function(){
		view.hide();
		editorWrap.hide();
	};
	
	viewController.setSelectedItemID = function(itemID){
		selectedItemID = itemID;
	};
	
	
	function loadItemDetails(object){		
		editor.setValue(""); 
		
		editorWrap.hide();
		editorDiv.hide();
		previewDiv.hide();
		otherDiv.hide();
		
		ajax.get(urlGet + "?id=" + selectedItemID, function(result){
			selectedItem = result;

			view.show();
			
			
			if(isEditableFile(selectedItem.name)){
				editorDiv.show();
				editorWrap.show();
				var content  = selectedItem.content || "";
				editor.setValueBase64(content);
				
				if(selectedItem.name.endsWith(".html"))
					editor.setMode("html");
				else if(selectedItem.name.endsWith(".js")||selectedItem.name.endsWith(".sjs"))
					editor.setMode("javascript");
				else if(selectedItem.name.endsWith(".css"))
					editor.setMode("css");
				else
					editor.setMode("text");
				
				editor.gotoLine(1);
			} else if(isShowableFile(selectedItem.name)) {
				previewDiv.empty();
				
				var contentURL = "repo/" + selectedItem.projectID + "/" + selectedItem.name;
				previewDiv.append("<iframe src='" + contentURL + "' width='100%' height='" + editorDiv.height() + "px'>");
				previewDiv.show();
				
			} else {
				
				if(selectedItem.name.endsWith("zip") == true){
					otherDiv.show();
					var unzipButton = $("<button class='bluebutton'>Unzip File</button>");
					otherDiv.append(unzipButton);
					unzipButton.click(function(){
						unzipButton.detach();
						otherDiv.text("Unzipping");
						ajax.post(urlUnzip, { id : selectedItem.id }, function(){
							otherDiv.text("Finished");	
						});						
					});
				}
				
			}
			
			setInfoArea(selectedItem, selectedItemID);			
		});
	}
	

	
	function setInfoArea(contentObj){
		_ui.infoarea_name.val(contentObj.name);
		_ui.infoarea_template.val(contentObj.template);
		
		infoArea.find("input,select").unbind("change").change(function(){
			updateItem();			
		});
		
		_ui.infoarea_open.unbind();
		_ui.infoarea_open.click(function(){
			var contentURL = "repo/" + contentObj.projectID + "/" + contentObj.name;
			window.open(contentURL,'_newtab');
		});

	}
	
	function updateItem(newValue, object, callback){
		
		if(selectedItem !== undefined){
			selectedItem.name 		   	= _ui.infoarea_name.val();
			selectedItem.template	  	= _ui.infoarea_template.val();
			
			if(isEditableFile(selectedItem.name)){
				selectedItem.content	   	= editor.getValueBase64();
			}
			
			if(selectedItem !== undefined){
				ajax.post("uapi/repository/updateItem", JSON.stringify(selectedItem) , function(result){
					if(typeof callback == "function")
						callback(result);
				});	
			}
		}
	}
	
	function setEmptyItem(animate){
		if(editor !== undefined)
			editor.setValue("");
		
		infoArea.find("input,select").val("");
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
