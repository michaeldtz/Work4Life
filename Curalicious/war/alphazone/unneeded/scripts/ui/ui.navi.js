//Define UI object
var ui = ui || {}

//Define navi object
ui.navi = {};

ui.navi.targets 		= {};
ui.navi.defaultPlace	= "body";
ui.navi.defHideFunc		= "hide;"
ui.navi.defShowFunc		= "show";
ui.navi.sethash			= "";

ui.navi.registerTarget_Viewname = function(targetName, viewName, defaultAttributes){
	
	var target = {
			type 		: "viewname",
			name 		: targetName,
			viewName	: viewName,
			defaultAttr : defaultAttributes
	};
		
	ui.navi.targets[targetName] = target;
}

ui.navi.initialize = function(defaultPlace, defaultHideFunction, defaultShowFunction){
	ui.navi.defaultPlace	= defaultPlace		  || ui.navi.defaultPlace;	
	ui.navi.defHideFunc 	= defaultHideFunction || ui.navi.defHideFunc;
	ui.navi.defShowFunc 	= defaultShowFunction || ui.navi.defShowFunc;
}

ui.navi.encodeAttributes = function(attributes){
	var attrJSON 	= JSON.stringify(attributes);
	var attrBase64	= core.base64.encode(attrJSON);
	return encodeURIComponent(attrBase64);
}

ui.navi.navigateToTarget = function(targetName, attributes, callback){
	
	
	var target		= ui.navi.targets[targetName];
	
	if(target !== undefined){
		
		attributes 			= $.extend({}, target.defaultAttr ,attributes); 
		attributes.placeAt 	= attributes.placeAt 	|| ui.navi.defaultPlace;
		
		if(attributes.urlTarget == true){
			
			var urlAttr = $.extend({}, attributes);			
			if(urlAttr.placeAt == ui.navi.defaultPlace)			
				delete urlAttr.placeAt;
			delete urlAttr.urlTarget;
			
			if(!utils.isObjectEmpty(urlAttr)){
				var attrJSON 	= JSON.stringify(urlAttr);
				var attrBase64	= core.base64.encode(attrJSON);
				location.hash 	= "/" + targetName + "?attr=" + encodeURIComponent(attrBase64);
				ui.navi.sethash = location.hash;
			} else {
				location.hash 	= "/" + targetName;
				ui.navi.sethash = location.hash;
			}
		}
		
		if(target.type == "viewname"){
			ui.navi.navigateToViewName(target.viewName, attributes, callback)
		} else if(target.type == "viewcontroller"){
			
		}
		
		return true;
	} else {
		console.log("Target " + targetName + " not found");
		return false;
	}
}

ui.navi.navigateVC = function(viewController, placeAt, attributes){
	
	attributes = attributes || {};
	attributes.placeAt = placeAt;
	
	ui.navi.navigateToViewController(viewController, attributes);
}

ui.navi.navigateToViewController = function(viewController, attributes){
	
	var placeRef = $(attributes.placeAt);
	
	if(attributes.doNotEmptyPlace !== true){
		placeRef.children(".hasViewController").each(function(){
			var viewInHere = $(this);
			var viewName = viewInHere.attr("viewname");
			if(viewName !== undefined){
				var viewControllerInHere = ui.mvc.viewControllers[viewName]; 
				if(viewControllerInHere !== undefined){
					viewControllerInHere.removeView();
				}
			}
		});
		placeRef.empty();
	} else {
		
	}
	
	//Show new view
	viewController.onNavigation(attributes);
	viewController.placeViewAt(placeRef, true);
	placeRef.resize();
	
	
};

ui.navi.navigateToViewName = function(viewName, attributes, callback){
	ui.mvc.loadMVC(viewName, function(viewController, view){
		ui.navi.navigateToViewController(viewController, attributes, callback);
	});
}

ui.navi.handleStartupNavigation = function(){
	
	//Read URL Navigation INFO
	var startupTarget = location.hash;
	if(startupTarget.substr(0,1) == '#')
		startupTarget = startupTarget.substr(1);
	
	if(startupTarget.indexOf("?") >= 0)
		startupTarget = startupTarget.substr(0,startupTarget.indexOf("?"));
	
	if(startupTarget.substr(0,1) == "/")
		startupTarget = startupTarget.substr(1);	
	
	var attributes = {};
	var attrBase64 	= core.url.getURLHashParameter();	
	if(attrBase64.attr !== undefined){
		var attrJSON 	= core.base64.decode(attrBase64.attr);
		attributes  	= $.parseJSON(attrJSON);		
	}
	
	if(startupTarget !== undefined && startupTarget !== ""){
		if(ui.navi.navigateToTarget(startupTarget, attributes) === false)
			ui.navi.navigateToTarget("main", {urlTarget : false});
	}else
		ui.navi.navigateToTarget("main", {urlTarget : false});
	
}

window.onhashchange = function(){
	if(ui.navi.sethash !== location.hash){
		ui.navi.handleStartupNavigation();
		ui.navi.sethash = location.hash;
	}
	
};