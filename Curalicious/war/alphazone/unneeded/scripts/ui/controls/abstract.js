
var ui = ui || {};
ui.controls = ui.controls || {};

ui.controls.AbstractControl = function AbstractControl(placeAt, settings){
	
	abstractControl = {};
	
	abstractControl.view = $("<div class='ui-control'></div>");
	
	abstractControl.placeAt = function(container){
		if(container !== undefined)
			container.append(abstractControl.view);
	};
	
	abstractControl.hide = function(){
		view.hide();
	};
	
	abstractControl.show = function(){
		view.show();
	};
	
	abstractControl.render = function(){
		
	};
	
	abstractControl.settings = function(name, value){
		if(value != undefined){
			settings[name] = value;
			return abstractControl;
		} else {
			return settings[name];
		} 
			
	};
	
	abstractControl.oldFunc = function(){
		abstractControl.newFunc();
	};
	
	abstractControl.placeAt(placeAt);
	
	return abstractControl;
	
}