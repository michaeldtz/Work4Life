
var ui = ui || {};
ui.controls = ui.controls || {};

ui.controls.List = function List(placeAt, settings){
	
	var defaultSettings = {
			id 			: "",
			icon		: undefined,
			itemRender 	: undefined,
			listContent	: [],
			searchBar	: true,
			onSelect	: function(){},
			addFunction : undefined,
			delFunction : undefined,
			favFunction : undefined,
			keepSelected: true,
			contextMenu : undefined,

	};
	
	settings = $.extend({}, defaultSettings, settings);
	
	var listControl = new ui.controls.AbstractControl(placeAt, settings);	
	var list		= $("<ul id='" + settings.id + "' class='ui-list-control'></ul>").appendTo(listControl.view);
	var searchContainer;
	var searchBar;
	var toolbar;
	var addButton;
	var header;
	
	listControl.setListContent = function(array){
		settings.listContent  = array;
		listControl.render();
	}
	
	listControl.render = function(){
		list.empty();
		for(var i = 0; i < settings.listContent.length; i++){
			
			var entry = settings.listContent[i];
			
			var item = $("<li id='list_item_" + entry.id + "' class='ui-list-item'><span>" + entry.name + "</span></li>");
			
			if(settings.icon != undefined){
				var iconURL = "";
				if(typeof settings.icon == "function"){
					iconURL = settings.icon(entry);
				} else {
					iconURL = settings.icon;
				}
				var iconImg = $("<img src='" + iconURL + "' style='margin-right:5px;' />");
				item.prepend(iconImg);
			}
			
			if(typeof settings.itemRender == "function")
				item = settings.itemRender(item, entry); 
			
			
			registerClickHandler(item, entry, i);
			registerDeleteButton(item, entry);
			registerFavoriteButton(item, entry);
			
			list.append(item);
		}
		
		if(settings.contextMenu != undefined){
			list.contextMenu({				
		        selector: '.ui-list-item', 
		        callback: function(key, options) {
		        	var id = $(this).attr("id");
		        	
		        	if(id != ""){
		        		if(id.indexOf("list_item_") == 0)
		        			id = id.substr(10);
		        		settings.contextMenu.callback(key, id, $(this));	
		        	}
		        	
		        },
		        items: settings.contextMenu.items
		    });
		}
	
		
		
		listControl.renderGroups();
	};
	
	listControl.renderGroups = function(){
		
		var lastGroup = { name : undefined, entries : []};
		
		function finishGrp(){
			if(lastGroup.entries.length >= 2){
				var firstID = lastGroup.entries[0].id;
				var grpID   = lastGroup.name.replace(/ /g,'___');
				
				var groupEntry = $("<li class='lstGrp' id='list_grp_" + grpID + "'>" + lastGroup.name + "</li>");
				var iconImg = $("<img src='../../resources/images/icons/box.png' style='margin-right:5px;' />");
				groupEntry.prepend(iconImg);
				$("#list_item_" + firstID).before(groupEntry);
				
				groupEntry.click(function(){
					var grpID = $(this).attr("id");
					$(".in_" + grpID).toggle(); 
				});
				
				for(var idx in lastGroup.entries){
					var item = $("#list_item_" + lastGroup.entries[idx].id);
					item.addClass("in_list_grp_" + grpID + "");
					item.css("margin-left", "10px");
					item.hide();
				}
			}
			lastGroup  = { name : undefined, entries : []};
		}
		
		for(var i = 0; i < settings.listContent.length; i++){
			var entry  = settings.listContent[i];
			var splits = entry.name.split("/");
			
			if(splits.length <= 1){
				finishGrp();
				continue;
			}
				
			
			var group  = splits[0]; 
			
			if(lastGroup.name != undefined && lastGroup.name == group){
				lastGroup.entries.push(entry); 
			} else {
				finishGrp();				
				lastGroup = { name : group, entries : [entry]};
			}
		}
		finishGrp();
		
	};

	//Add Toolbar
	header  = $("<div class='ui-list-header'></div>").prependTo(listControl.view);
	
	if(settings.title != undefined){
		toolbar = $("<div class=''></div>").append(settings.title).appendTo(header);
	}
	
	toolbar = $("<div class='ui-list-toolbar'></div>").appendTo(header);
	
	
	
	if(typeof settings.addFunction == "function"){
		addButton = $("<div class='ui-list-toolbar-button'><img src='resources/images/icons/plus_16x16.png'></div>");
		toolbar.append(addButton);
		addButton.click(function(){
			settings.addFunction();
		});
	}
	
	if(settings.searchBar == true){
		var searchContainer = $("<div></div>");
		var searchBar = $("<input class='ui-list-search' type='search'>");
		searchContainer.append(searchBar);
		toolbar.append(searchContainer);
		
		searchBar.bind("keyup change blur", function(){
			var searchText = searchBar.val();
			list.find("li").each(function(){
				var li = $(this);
				var liContent = li.text();
				if(liContent.indexOf(searchText) >= 0)
					li.show();
				else
					li.hide();
			})
		});
	}
	
	function registerClickHandler(item, entry, index){
		item.click(function(){
			list.find("li.selected").removeClass("selected");
			item.addClass("selected");
			settings.onSelect(entry, index, item);
		});		
	}
	
	function registerDeleteButton(item, entry){
			
		if(typeof settings.delFunction == "function"){
			
			item.css("position", "relative");
			var delBtn = $("<img src='resources/images/icons/delete.png' class='ui-list-deleteButton' />");
			delBtn.hide();
			item.append(delBtn);
			
			delBtn.click(function(event){
				var answer = confirm("Are you sure that you want delete this entry");
				if(answer == true)
					settings.delFunction(entry,item);
				event.stopPropagation();
			});
			
			item.hover(function(){
				delBtn.show();
			}, function(){
				delBtn.hide();
				//setTimeout(delBtn.hide,1000);
			});			
		}	
	}
	
	function registerFavoriteButton(item, entry){
		
		if(typeof settings.favFunction == "function"){
			
			item.css("position", "relative");
			var favBtn = $("<img src='resources/images/icons/star.png' class='ui-list-favoriteButton' />");
			favBtn.hide();
			item.append(favBtn);
			
			favBtn.click(function(event){
				var answer = confirm("Are you sure that you want delete this entry");
				if(answer == true)
					settings.favFunction(entry,item);
				event.stopPropagation();
			});
			
			item.hover(function(){
				favBtn.show();
			}, function(){
				favBtn.hide();
			});			
		}	
	}
	
	
	listControl.render();
	
	return listControl;
}