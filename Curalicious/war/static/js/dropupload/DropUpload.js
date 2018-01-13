var ui = ui || {};
ui.controls = ui.controls || {};

ui.controls.DropUpload = function List(placeAt, settings){
	
	var isUploading		= 0;
	var defaultSettings = {
			uploadURL	: "",
			text		: "Drop Files Here",
			maxFiles	: 3,
			onUpload 	: uploadFile,
			onFinished  : function(){},
			fullscreenDrop : false
	};
	settings = $.extend({}, defaultSettings, settings);
	
	
	//Create control from abstract control
	var dropControl = { view : $("<div class='ui-control'></div>") };
	var dropZone = dropControl.view;
	dropZone.appendTo(placeAt);
	
	function ajaxFileUpload(url, data, progressCallback, callback){
		
		$.ajax({
			async 		: true,
			url			: url, 
			data		: data,
			type		: "POST",
			contentType	: "application/json",
			cache		: false,
			beforeSend: function(XMLHttpRequest)
			{
			  //Upload progress
				console.log(XMLHttpRequest);
//			  XMLHttpRequest.upload.addEventListener("progress", function(evt){
//			    if (evt.lengthComputable) {
//			      var percentComplete = evt.loaded / evt.total;
//			      progressCallback(percentComplete);
//			    }
//			  }, false);
			},
			success 	: function(data, textStatus, jqXHR){
				if(typeof callback == "function")
					callback(data, jqXHR);
			},
			error		: function(jqXHR, textStatus, errorThrown){
				ui.messageArea.showError("Loading " + url + " failed");
			}
		});
		
		
	};


	
	function init(){
		if(settings.fullscreenDrop == true){
			settings.text = "";
			$("body > *").bind("dragover",  handleDragOver);
			dropZone.parent().bind("dragleave", handleDragLeave);
			dropZone.parent().bind("drop", 		handleDrop);
		} else {
			dropZone.addClass("ui-dropzone");
			dropZone.text(settings.text);
			dropZone.bind("dragover",  	handleDragOver);
			dropZone.bind("dragleave", 	handleDragLeave);
			dropZone.bind("drop", 		handleDrop);
		}
	}
	
	  	function handleDragOver(jEvent) {
	  		
	  		
	  		var evt = jEvent.originalEvent;
			  if(isUploading == 0){
			    evt.stopPropagation();
			    evt.preventDefault();
			    dropZone.addClass("dropping");
			    dropZone.parent().addClass("dropping");
			    evt.dataTransfer.dropEffect = 'copy'; // Explicitly show this is a copy.
			  }
	  	}
	  
	  	function handleDragLeave(){
	  		dropZone.parent().removeClass("dropping");
	  		dropZone.removeClass("dropping");
	  	}
		
		function handleDrop(jEvent) {
			var evt = jEvent.originalEvent;
			if(isUploading == 0){
			    evt.stopPropagation();
			    evt.preventDefault();
			    
			    dropZone.removeClass("dropping");
			    
			    var files = evt.dataTransfer.files; // FileList object.
		
			    var numFiles = files.length;
			    if( numFiles > settings.maxFiles){
			    	numFiles = settings.maxFiles;
			    }
			    
			    isUploading += numFiles;
			    dropZone.empty();
			    var dzHeight = dropZone.parent().height();
			    var lbHeight = dzHeight / numFiles;
			    
			    // files is a FileList of File objects. List some properties.	    
			    for (var i = 0; i < numFiles; i++) {
			    	var f = files[i];
			    	var loadingBar = $("<div id='" + i + "' class='ui-dropzone-loadingIndicator' style='height:" + lbHeight + "px; line-height:" + lbHeight + "px; width:0%;'>" + f.name + "</div>");
			    	dropZone.append(loadingBar);
			    	f.loadingBar = loadingBar;
			    	readFile(f);     
			    }		    
			}
		  }

		function readFile(file){
			
			var reader = new FileReader();
	    	 
	    	 reader.onerror = function(){
	    		 singleUploadFinished();
	    	 };
	    	 
	    	 reader.onabort = function(){
	    		 singleUploadFinished();
	    	 };
	    	  
	    	 reader.onload = function(progressEvent) {	    		
	    		 file.loadingBar.css({"width" : "10%" }, 500);
	    		 var fileContent = this.result;
	    		 
	    		 //Convert file content
	    		 fileContent = fileContent.split(";base64,")[1];	    		
	    		 uploadFile(file, fileContent);
	    	 };
	    
	    	 //Start Reading
	    	 reader.readAsDataURL(file);     
		}
		
		function uploadFile(file, fileContent){
			console.log(file);
			
			ajaxFileUpload(settings.uploadURL, { filename : file.name, content : fileContent }, 
	    		 function progressEvent(progressPct){
	    			 file.loadingBar.animate({"width" : progressPct + "%"}, 1000);
	    		 }, function finished(){
	    			 file.loadingBar.animate({"width" : "100%"}, 1000, function(){
	    				 file.loadingBar.delay(500).slideUp(singleUploadFinished);	    				 
	    			 });
	    	 });
		}
		
		function singleUploadFinished(){
			isUploading--;
			 
			if(isUploading == 0){
				dropZone.parent().removeClass("dropping");
				dropZone.text(settings.text);
				settings.onFinished();
			}
		}
		
		
		init();
		
		
		function base64_encode (data) {

			  var b64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
			  var o1, o2, o3, h1, h2, h3, h4, bits, i = 0,
			    ac = 0,
			    enc = "",
			    tmp_arr = [];

			  if (!data) {
			    return data;
			  }

			  do { // pack three octets into four hexets
			    o1 = data.charCodeAt(i++);
			    o2 = data.charCodeAt(i++);
			    o3 = data.charCodeAt(i++);

			    bits = o1 << 16 | o2 << 8 | o3;

			    h1 = bits >> 18 & 0x3f;
			    h2 = bits >> 12 & 0x3f;
			    h3 = bits >> 6 & 0x3f;
			    h4 = bits & 0x3f;

			    // use hexets to index into b64, and append result to encoded string
			    tmp_arr[ac++] = b64.charAt(h1) + b64.charAt(h2) + b64.charAt(h3) + b64.charAt(h4);
			  } while (i < data.length);

			  enc = tmp_arr.join('');

			  var r = data.length % 3;

			  return (r ? enc.slice(0, r - 3) : enc) + '==='.slice(r || 3);

			}
		
		return dropControl;
};