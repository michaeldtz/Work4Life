package de.dietzm.alphazone.dex.dexServices;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSFunction;

@SuppressWarnings("serial")
public class JSServletRequest extends ScriptableObject {

	private HttpServletRequest req;
	@SuppressWarnings("unused")
	private ScriptableObject scope;
	private String urlPath;

	public JSServletRequest(HttpServletRequest req, String urlPath, ScriptableObject scope) {
		this.req = req;
		this.urlPath = urlPath;
		this.scope = scope;
		
		String[] functionNames = { "getParameter", "getJSONParameter", "getBodyContent", "getUrlPath", "getMethod" };
		defineFunctionProperties(functionNames, JSServletRequest.class, ScriptableObject.DONTENUM);
	}

	@JSFunction
	public String getParameter(String name){
		String param = req.getParameter(name);
		if(param == null)
			return "";
		return param;
	}
	
	@JSFunction
	public String getMethod(){
		String method = req.getMethod();
		if(method == null)
			return "GET";
		return method;
	}
	
	
	@SuppressWarnings("unchecked")
	@JSFunction
	public Enumeration<String> getParameterNames(){
		Enumeration<String> enu = req.getParameterNames();
		while (enu.hasMoreElements()) {
			String string = (String) enu.nextElement();
			System.out.println(string);
		}
		return  enu;
	}
	
	@JSFunction
	public Object getJSONParameter(String name){
		String param = req.getParameter(name);
		
		if(param == null)
			return "";
		
		return param;
//		Context ctx = Context.getCurrentContext();
//		
//		
//		Object obj = NativeJSON.parse(ctx, this.scope, param, (Callable)jsonObject);
//		return obj;
	}
	
	@JSFunction	
	public String getBodyContent(String string) throws IOException {
		String body = IOUtils.toString(req.getInputStream(), "UTF-8");
		return body;
	}

	@JSFunction	
	public String getUrlPath() {
		return urlPath;
	}

	@Override
	public String getClassName() {
		return "JSServletRequest";
	}


}
