package de.dietzm.alphazone.dex.dexServices;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJSON;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSFunction;





@SuppressWarnings("serial")
public class JSServletResponse extends ScriptableObject {


	private HttpServletResponse resp;
	private PrintWriter writer = null;


	public JSServletResponse(HttpServletResponse resp) {
		this.resp = resp;
		String[] functionNames = { "sendResponse", "sendJSONResponse", "setContentType", "sendError" };
		defineFunctionProperties(functionNames, JSServletResponse.class, ScriptableObject.DONTENUM);
	}


	@JSFunction
	public void sendResponse(String content) throws IOException{
		resp.setCharacterEncoding("UTF-8");
		
		if(writer == null)
			this.writer = resp.getWriter();
		IOUtils.write(content, writer);
		//writer.print(content);
	}
	
	@JSFunction
	public void sendJSONResponse(ScriptableObject jsObject) throws IOException{
		resp.setCharacterEncoding("UTF-8");
		
		Context ctx = Context.getCurrentContext();
		Scriptable scope = jsObject.getParentScope();
		String jsonStr = NativeJSON.stringify(ctx, scope, jsObject, null, null).toString();
		sendResponse(jsonStr);
	}

	@JSFunction
	public void setContentType(String type){
		resp.setContentType(type);
		resp.setCharacterEncoding("UTF-8");
	}
	
	@JSFunction
	public void sendError(int code, String text) throws IOException {
		resp.sendError(code, text);
	}
	
	@Override
	public String getClassName() {
		return "JSServletResponse";
	}


	public boolean scriptHasSentResponse() {
		return (writer != null);
	}




}
