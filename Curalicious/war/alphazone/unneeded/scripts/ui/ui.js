var ui = ui || {};

// Define spinner
ui.spinnerCount = 0;

ui.spinner = {};
ui.spinner.show = function() {

	ui.spinnerCount++;

	if (ui.spinnerCount > 0) {
		$("#loadingIndicator").show();
	}

}

ui.spinner.hide = function() {

	ui.spinnerCount--;

	if (ui.spinnerCount <= 0) {
		$("#loadingIndicator").hide();
	}

}

//UI Sizing Tools

ui.sizing = {};
ui.sizing.centerBoxOnScreen = function(jqElement){
	
	var docSize = {
			width : $("body").outerWidth(),
			height: $("body").outerHeight()
	};
	
	var elemSize = {
			width : jqElement.outerWidth(),
			height: jqElement.outerHeight()
	};
	
	var xPos = (docSize.width  - elemSize.width) / 2; 
	var yPos = (docSize.height - elemSize.height) / 2;
	
	jqElement.css({
		left: xPos + "px",
		top:  yPos + "px"
	});
}

ui.sizing.centerBoxHorizontally = function(jqElement){
	
	var docSize = {
			width : $("body").outerWidth()
	};
	
	var elemSize = {
			width : jqElement.outerWidth()
	};
	
	var xPos = (docSize.width  - elemSize.width) / 2; 
	
	jqElement.css({
		left: xPos + "px"
	});
	
}

ui.sizing.stretchHeight = function(jqElement){
	
	var maxHeight  = $(window).height();
	var elementTop = jqElement.offset().top;
	
	var margin = 40;
	var height = maxHeight - 55 - 22;
	
	if(jqElement.children().eq(0).hasClass("fullsizeSheet")){
		jqElement.css("min-height", "");
		jqElement.css({
			"height" : height + "px"
		});	
	} else {
		jqElement.css("height", "");
		jqElement.css({
			"min-height" : height + "px"
		});	
	}
	
}

ui.sizing.autoResizeContentArea = function(jqElement){
	
	function doResizeOfContentSheet(element){
		ui.sizing.centerBoxHorizontally(element);
		ui.sizing.stretchHeight(element);
	}
	
	doResizeOfContentSheet(jqElement);
	
	$(window).resize(function(){
		doResizeOfContentSheet(jqElement);
	});
	
}


/**
 * Reload functions
 */
ui.loader = {};
ui.loader.loadScript = function(url, callback) {

	var script = document.createElement("script")
	script.type = "text/javascript";

	if (script.readyState) { // IE
		script.onreadystatechange = function() {
			if (script.readyState == "loaded"
					|| script.readyState == "complete") {
				script.onreadystatechange = null;
				callback();
			}
		};
	} else { // Others
		script.onload = function() {
			callback();
		};
	}

	script.src = url;
	document.getElementsByTagName("head")[0].appendChild(script);
}

ui.loader.loadScripts = function(urlArray, callback) {

	var cnt = urlArray.length;
	var i 	= 0;
	
	function loadThis(){		
		var url = urlArray[i];
		ui.loader.loadScript(url, function(){
			i++;
			if(i == cnt){
				callback();
			} else {
				loadThis();
			}			
		});
		
	}

	loadThis();
	
}

// Load a stylesheet
ui.loader.loadStyleSheet = function(url, callback) {

	var link;
	
	if (document.createStyleSheet) {
		link = document.createStyleSheet(url);
	} else {
		link = document.createElement("link")	
	}
	
	link.setAttribute( 'rel', 'stylesheet' );
	link.setAttribute( 'type', 'text/css' );
	
	if (link.readyState) { // IE
		link.onreadystatechange = function() {
			if (link.readyState == "loaded"
					|| link.readyState == "complete") {
				link.onreadystatechange = null;
				callback();
			}
		};
	} else { // Others
		link.onload = function() {
			callback();
		};
	}

	link.setAttribute( 'href', url );
	document.getElementsByTagName("head")[0].appendChild(link);

}




// Define MessageArea
ui.messageArea = {};
ui.messageArea.uiProvider = [];

ui.messageArea.showSuccessMessage = function(messageText, category) {
	category = category || "";
	for(var i = 0; i < ui.messageArea.uiProvider.length; i++){
		ui.messageArea.uiProvider[i].showMessage("S", errorText, category);
	}
	setTimeout(ui.messageArea.removeAllMessages, 4000);
}

ui.messageArea.showMessage = function(messageText, category) {
	category = category || "";
	for(var i = 0; i < ui.messageArea.uiProvider.length; i++){
		ui.messageArea.uiProvider[i].showMessage("M", errorText, category);
	}
	setTimeout(ui.messageArea.removeAllMessages, 4000);
}

ui.messageArea.showError = function(errorText, category) {
	category = category || "";
	for(var i = 0; i < ui.messageArea.uiProvider.length; i++){
		ui.messageArea.uiProvider[i].showMessage("E", errorText, category);
	}
	
	setTimeout(ui.messageArea.removeAllMessages, 10000);
}

ui.messageArea.removeAllMessages = function(){
	for(var i = 0; i < ui.messageArea.uiProvider.length; i++){
		ui.messageArea.uiProvider[i].showMessage("", "", "");
	}
}

ui.messageArea.registerUIProvider = function(messageUIProvider) {
	if (messageUIProvider !== undefined) {
		if (typeof messageUIProvider.showMessage == "function") {
			ui.messageArea.uiProvider.push(messageUIProvider);
		}
	}
}
