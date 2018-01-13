package de.dietzm.alphazone.dex;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.google.appengine.api.datastore.Blob;

import de.dietzm.alphazone.dex.dexServices.JSServletRequest;
import de.dietzm.alphazone.dex.dexServices.JSServletResponse;
import de.dietzm.alphazone.dex.dexServices.JSURLCaller;
import de.dietzm.alphazone.entitystore.scriptobject.EntityStoreScriptObject;
import de.dietzm.alphazone.jsservices.EmailService;
import de.dietzm.alphazone.repository.model.RepositoryItem;

public class JSExecutor {

	public String execute(ServletContext srvctx, String sccriptname, HttpServletRequest req, HttpServletResponse resp, RepositoryItem reqContent){
		
		// Get Script
				Blob blob = reqContent.getContentBlob();
				byte[] blobContent = blob.getBytes();
				String script = new String(com.sun.jersey.core.util.Base64.decode(blobContent));

				// Init context and scope
				Context ctx = Context.enter();
				ScriptableObject scope = ctx.initStandardObjects();

				// Get Basic Object
				JSServletRequest  	jsRequest     = new JSServletRequest(req, sccriptname, scope);
				JSServletResponse 	jsResponse    = new JSServletResponse(resp);
				JSURLCaller		 	urlCaller     = new JSURLCaller();
				EntityStoreScriptObject entAccess = new EntityStoreScriptObject(reqContent.getProjectID(), srvctx);
				EmailService emailing = new EmailService();
				
				// Register the main classes
				regsiterEmptyObject(ctx, scope, "dex");
				registerObject(ctx, scope, jsRequest, "dxrequest");
				registerObject(ctx, scope, jsResponse, "dxresponse");
				registerObject(ctx, scope, urlCaller, "dxurl");
				registerObject(ctx, scope, entAccess, "eas");
				registerObject(ctx, scope, emailing, "email");
				
				try {
					// Evaluate the Script and get the result
					Object result = ctx.evaluateString(scope, script, sccriptname, 1, null);
					
					if (result != null && !jsResponse.scriptHasSentResponse()) {
						return result.toString();
					}
					
					return null;
				
				} catch (Exception e) {
					return e.toString();
				}
	}
	
	private void registerObject(Context ctx, ScriptableObject scope, ScriptableObject object, String name) {
		scope.put(name, scope, object);
	}

	private void regsiterEmptyObject(Context ctx, ScriptableObject scope, String name) {
		Scriptable object = ctx.newObject(scope);
		scope.put(name, scope, object);
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	private void registerClass(ScriptableObject scope, Class clz) throws IllegalAccessException,
			InstantiationException, InvocationTargetException {
		ScriptableObject.defineClass(scope, clz);
	}

}
