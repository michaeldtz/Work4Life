var ui = ui || {};
ui.controls = ui.controls || {};

ui.controls.AceCodeEditor = function List(placeAt, settings){
	
	var aceEditor;
	var changing 		= false;
	var defaultSettings = {
			id 			 : "",
			updateEvent  : function(){},
			value		 : "",
			statusUpdate : function(){},
			nextPrev     : function(){},
	};
	settings = $.extend({}, defaultSettings, settings);
	
	
	//Create control from abstract control
	var editorControl = new ui.controls.AbstractControl(placeAt, settings);	
	
	//Init function // will be called at the end
	function initControl(){
		
		var container = editorControl.view.get(0);
		
					var myKeyMap = { 
							"Ctrl-S": function(cm) { 
								var newValue = aceEditor.getValue();
								settings.updateEvent(newValue, editorControl); 
							},
							"Cmd-S": function(cm) { 
								var newValue = aceEditor.getValue();
								settings.updateEvent(newValue, editorControl); 
							}
					};
					
					
			aceEditor = ace.edit(placeAt.get(0));
			//aceEditor.setTheme("ace/mode/twilight");				    
			aceEditor.getSession().setMode("ace/mode/javascript");
				
			
			aceEditor.getSession().on('change', function(e) {
				settings.statusUpdate("Changed", 1);
			});
			
			aceEditor.on("blur", function(e) {
				settings.statusUpdate("Autosaving", 2);
				
				var newValue = aceEditor.getValue();
				settings.updateEvent(newValue, editorControl, function(){
					settings.statusUpdate("Saved", 0);
				});
			});
			
			aceEditor.commands.addCommand({
			    name: 'Saving',
			    bindKey: {win: 'Ctrl-S',  mac: 'Command-S'},
			    exec: function(editor) {
			    	settings.statusUpdate("Saving", 2);
			    	var newValue = aceEditor.getValue();
					settings.updateEvent(newValue, editorControl, function(){
						settings.statusUpdate("Saved", 0);
					});
			    },
			    readOnly: true
			});
			
			aceEditor.commands.addCommand({
			    name: 'Switching',
			    bindKey: {win: 'Alt-W',  mac: 'Alt-W'},
			    exec: function(editor) {
			    	settings.nextPrev(true);
			    },
			    readOnly: true
			});
			
			aceEditor.commands.addCommand({
			    name: 'Switching',
			    bindKey: {win: 'Alt-E',  mac: 'Alt-E'},
			    exec: function(editor) {
			    	settings.nextPrev(false);
			    },
			    readOnly: true
			});
		
	}
	
	editorControl.gotoLine = function(index){
		aceEditor.gotoLine(1);
		aceEditor.moveCursorTo(0,1);
		aceEditor.navigateFileStart();
		aceEditor.clearSelection();
	}
	
	editorControl.resize = function(){
		
		aceEditor.resize();
		editorControl.gotoLine(1);
	}
	
	editorControl.setMode = function(newMode){
		if(aceEditor != undefined)
			aceEditor.getSession().setMode("ace/mode/"+newMode);
	}
	
	editorControl.getValue = function(){
		if(aceEditor != undefined)
			return aceEditor.getValue();
		return "";
	};	
	
	editorControl.getValueBase64 = function(){
		if(aceEditor != undefined){
			var value			= aceEditor.getValue();
			var newValueEncoded = utils.base64.encode(value);
			return newValueEncoded;
		}
		return "";
	};	
	
	editorControl.destroy = function(){
		aceEditor.destroy();
	};
	
	editorControl.setValue = function(newValue){
		
		
		
		changing = true;
		
		if(aceEditor != undefined)
			aceEditor.setValue(newValue);
		changing = false;
		
		settings.statusUpdate("Loaded", 0);
	};	
	

	editorControl.setValueBase64 = function(newValue){
		var newValueDecoded = utils.base64.decode(newValue);
		editorControl.setValue(newValueDecoded);
	};	
	
	
	initControl();
	
	return editorControl;
	
}