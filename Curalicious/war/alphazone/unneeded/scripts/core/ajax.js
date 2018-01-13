//define the ajax object
var ajax = ajax || {};

//Load data
ajax.get = function(url, callback){
	
	ui.spinner.show();
	
	$.ajax({
		async 		: true,
		url			: url, 
		type		: "GET",
		cache		: false,
		success 	: function(data, textStatus, jqXHR){
			ui.spinner.hide();
			if(typeof callback == "function")
				callback(data, jqXHR);
		},
		error		: function(jqXHR, textStatus, errorThrown){
			ui.spinner.hide();
			ui.messageArea.showError("Loading " + url + " failed");
		}
	});
	
	
};

//Load data
ajax.post = function(url, data, callback){
	
	ui.spinner.show();
	
	$.ajax({
		async 		: true,
		url			: url, 
		data		: data,
		type		: "POST",
		contentType	: "application/json",
		cache		: false,
		success 	: function(data, textStatus, jqXHR){
			ui.spinner.hide();
			if(typeof callback == "function")
				callback(data, jqXHR);
		},
		error		: function(jqXHR, textStatus, errorThrown){
			ui.spinner.hide();
			ui.messageArea.showError("Loading " + url + " failed");
		}
	});
};


//Load data
ajax.load = function(url, callback){
	
	ui.spinner.show();
	
	$.ajax({
		async 		: true,
		url			: url, 
		type		: "GET",
		cache		: true,
		success 	: function(data, textStatus, jqXHR){
			ui.spinner.hide();
			if(typeof callback == "function")
				callback(data, jqXHR);
		},
		error		: function(jqXHR, textStatus, errorThrown){
			ui.spinner.hide();
			ui.messageArea.showError("Loading " + url + " failed");
		}
	});	
};

//Load script
ajax.loadScript = function(url, callback){
	
	ui.spinner.show();
	
	$.ajax({
		type		: "GET",
		cache		: true,
		dataType    : "script",
		success 	: function(data, textStatus, jqXHR){
			ui.spinner.hide();
			if(typeof callback == "function")
				callback(data, jqXHR);
		},
		error		: function(jqXHR, textStatus, errorThrown){
			ui.spinner.hide();
			ui.messageArea.showError("Loading " + url + " failed");
		}
	});	
};

//Load data
ajax.uploadFile = function(url, data, progressCallback, callback){
	
	
	
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
//		  XMLHttpRequest.upload.addEventListener("progress", function(evt){
//		    if (evt.lengthComputable) {
//		      var percentComplete = evt.loaded / evt.total;
//		      progressCallback(percentComplete);
//		    }
//		  }, false);
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





