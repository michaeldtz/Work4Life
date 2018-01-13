package de.dietzm.foundation.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import de.dietzm.foundation.interfaces.ResponseBuilder;

@Path("/asmc")
public class AgularServiceModuleCreator {

	@GET
	@Path("/public/asmc")
	@Produces("application/x-javascript")
	public Response createModule(@QueryParam("servicename") String serviceName, @QueryParam("classname") String classname) {
		
		StringBuffer module = new StringBuffer();
		
		try {
			
			if(serviceName == null || classname == null || serviceName.equals("") || classname.equals("")){
				return ResponseBuilder.createErrorResponse("Parameters missing", "ASMC002");
			}
			
			Class clz = Class.forName(classname);
			Method[] methods = clz.getMethods();
			
			module.append("angular.module('" + serviceName + ".service', [])\n");
			module.append(".factory('" + serviceName + "', ['$http', function($http) {\n");
			module.append("var service = {};\n");
		
			Path clzPathAn = (Path) clz.getAnnotation(Path.class);
			String basePath = clzPathAn.value();
			
			
			for (int i = 0; i < methods.length; i++) {
				Method m = methods[i];
				if(m.getReturnType() == Response.class){
					
					Path mPathAn = (Path) m.getAnnotation(Path.class);
					String mPath = mPathAn.value();
					String method = "GET"; 
					
					String url = "/rest" + basePath + mPath;
					
					if(m.isAnnotationPresent(POST.class))
						method = "POST";
					
					StringBuffer dataInit = new StringBuffer();
					dataInit.append("var data = {};\n");
					
					module.append("service." + m.getName() + " = function(");
					
					for (int j = 0; j < m.getParameterCount(); j++) {
						String paramName = "p" + j;
															
						module.append(paramName);
						module.append(",");
						dataInit.append("data." + paramName + " = " + paramName + ";\n");
					} 
					 
					module.append("callback, callbackerror) {\n");
					
					module.append(dataInit);
					
					module.append("$http({\n");
					module.append("method: '" + method + "',\n");
					module.append("url: '" + url + "',\n");
					module.append("data: data\n");
					module.append("}).success(function(data, status, headers, config) {\n");
					module.append("if(typeof callback == 'function')\n");
					module.append("\tcallback(data,status);\n");
					module.append("}).error(function(data, error){\n");
					module.append("console.error(error, data);\n");
					module.append("if(typeof callbackerror == 'function')\n");
					module.append("\tcallbackerror(data,status);\n");
					module.append("});\n");
					module.append("");
					module.append("");
					
					module.append("};\n");
					
				}
			}
			
			/*
			 * $http({
		method : "GET",
		url : "/rest/session/public/check"
		
	}).success(function(data, status, headers, config) {
		console.log(data);
		
		if (data.session == "true") {				
			$scope.sessionActive = true;
			$scope.firstname = data.username;
		} else {
			$scope.sessionActive = false;
			$scope.showLogin = true;
		}
		
	
		
	}).error(function(data, error){
		console.error(error, data);
	});
	
			 */
			
			module.append("return service;\n");
			module.append("}]);\n");		
		
			
			return Response.ok().entity(module.toString()).build();
			
		} catch(ClassNotFoundException e){
			return ResponseBuilder.createErrorResponse("ASMC Error", "ASMC001");
		}
		
		
	}
	

}
