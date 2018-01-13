var ui = ui || {};
ui.controls = ui.controls || {};

ui.controls.CodeEditor = function List(placeAt, settings){
	
	var codeMirror;
	var changing 		= false;
	var defaultSettings = {
			id 			: "",
			updateEvent : function(){},
			value		: "",
	};
	settings = $.extend({}, defaultSettings, settings);
	
	
	//Create control from abstract control
	var editorControl = new ui.controls.AbstractControl(placeAt, settings);	
	
	//Init function // will be called at the end
	function initControl(){
		
		var container = editorControl.view.get(0);
		
		var jsScripts = ["resources/jslibs/codemirror/codemirror.js",
		                 "resources/jslibs/codemirror/mode/xml/xml.js",
		                 "resources/jslibs/codemirror/mode/javascript/javascript.js",
		                 "resources/jslibs/codemirror/mode/css/css.js",
		                 "resources/jslibs/codemirror/mode/htmlmixed/htmlmixed.js"];
		
		ui.loader.loadStyleSheet("resources/jslibs/codemirror/codemirror.css", function(){
			ui.loader.loadScripts(jsScripts, function(){
					
					var myKeyMap = { 
							"Ctrl-S": function(cm) { 
								var newValue = codeMirror.getValue();
								settings.updateEvent(newValue, editorControl); 
							},
							"Cmd-S": function(cm) { 
								var newValue = codeMirror.getValue();
								settings.updateEvent(newValue, editorControl); 
							}
					};
					
					codeMirror = CodeMirror(container, {
						  mode				: settings.mode,
						  value		  		: settings.value,
						  lineNumbers 		: true,
						  lineWrapping		: true,
						  onCursorActivity: function() {
							  codeMirror.setLineClass(hlLine, null, null);
							  hlLine = codeMirror.setLineClass(codeMirror.getCursor().line, null, "activeline");
						  },
						  extraKeys : myKeyMap, 
						  onBlur : function(){
							  if(changing !== true){
								  var newValue = codeMirror.getValue();
								  settings.updateEvent(newValue, editorControl);
							  }								  
						  }
					});
					var hlLine = codeMirror.setLineClass(0, "");
					
			});
		});
		
	}
	
	editorControl.setSize = function(width, height){
		if(codeMirror != undefined)
			codeMirror.setSize(width,height);
	}
	
	
	editorControl.setMode = function(newMode){
		if(codeMirror != undefined)
			codeMirror.setOption("mode",newMode);
	}
	
	editorControl.getValue = function(){
		if(codeMirror != undefined)
			return codeMirror.getValue();
		return "";
	};	
	
	editorControl.getValueBase64 = function(){
		if(codeMirror != undefined){
			var value			= codeMirror.getValue();
			var newValueEncoded = utils.base64.encode(value);
			return newValueEncoded;
		}
		return "";
	};	
	
	
	editorControl.setValue = function(newValue){
		changing = true;
		
		if(codeMirror != undefined)
			codeMirror.setValue(newValue);
		changing = false;
	};	
	

	editorControl.setValueBase64 = function(newValue){
		var newValueDecoded = utils.base64.decode(newValue);
		editorControl.setValue(newValueDecoded);
	};	
	
	
	initControl();
	
	return editorControl;
	
}