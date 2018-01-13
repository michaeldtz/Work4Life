package de.dietzm.alphazone.entitystore.scriptobject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSFunction;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;

import de.dietzm.alphazone.dex.dexServices.JSServletRequest;
import de.dietzm.alphazone.dex.dexServices.JSServletResponse;
import de.dietzm.alphazone.entitystore.EntityStoreAccess;
import de.dietzm.alphazone.entitystore.exceptions.EntityHandlingException;
import de.dietzm.alphazone.entitystore.exceptions.EntityMandatoryFieldNotProvided;
import de.dietzm.alphazone.entitystore.model.EntityAPIDefinition;
import de.dietzm.alphazone.entitystore.model.EntityFieldDefinition;
import de.dietzm.alphazone.entitystore.model.EntityFieldType;
import de.dietzm.alphazone.entitystore.model.ReverseLinkDefinition;

@SuppressWarnings("serial")
public class EntityStoreScriptObject extends ScriptableObject {

	private HashMap<String, EntityStoreAccess> entityStores = new HashMap<String, EntityStoreAccess>();
	private long projectID;
	private ServletContext servletContext;
	
	public EntityStoreScriptObject(long projectID, ServletContext servletContext) {
		this.projectID = projectID;
		this.servletContext = servletContext;
		
		String[] functionNames = { "get", "put", "delete", "query", "queryAll", "updateselective", "update", "api", "getFX", "queryReverseFX", "deleteAll"};
		defineFunctionProperties(functionNames, EntityStoreScriptObject.class, ScriptableObject.DONTENUM);
	}

	private  EntityStoreAccess getStoreByEntityName(String entityName){
		if(entityStores.containsKey(entityName)){
			return entityStores.get(entityName);
		} else {
			EntityStoreAccess entityStore = new EntityStoreAccess(projectID, entityName);
			entityStores.put(entityName, entityStore);
			return entityStore;
		}
	}
	
	@JSFunction
	public String get(String entityName, String id) throws EntityHandlingException, JSONException{
		Long idL = new Long(id);
		EntityStoreAccess store = getStoreByEntityName(entityName);
		return store.getEntity(idL).toString();
	
	}
	
	@JSFunction
	public String getFX(String entityName, String id, String fxFieldname) throws EntityHandlingException, JSONException{
		Long idL = new Long(id);
		EntityStoreAccess store = getStoreByEntityName(entityName);
		return store.getForeign(idL, fxFieldname).toString();
	}
	
	@JSFunction
	public String queryReverseFX(String entityName, String id, String foreignEntity, String foreignFieldname, String sort) throws EntityHandlingException, JSONException{
		
		if(sort.equals("undefined"))
			sort = null;
		
		Long idL = new Long(id);
		EntityStoreAccess store = getStoreByEntityName(entityName);
		return store.queryReserveForeign(idL, foreignEntity, foreignFieldname, sort).toString();
	}
	
	@JSFunction
	public long put(String entityName, String jsonData) throws EntityHandlingException, JSONException, EntityMandatoryFieldNotProvided{
		EntityStoreAccess store = getStoreByEntityName(entityName);
		return store.putEntity(jsonData);
	}
	
	@JSFunction
	public void update(String entityName, String jsonData) throws EntityHandlingException, JSONException{
		EntityStoreAccess store = getStoreByEntityName(entityName);
		store.update(jsonData);
	}
	
	@JSFunction
	public void updateselective(String entityName, String jsonData) throws EntityHandlingException, JSONException{
		EntityStoreAccess store = getStoreByEntityName(entityName);
		store.updateselective(jsonData);
	}
	
	@JSFunction
	public void delete(String entityName, String id) throws EntityHandlingException, JSONException, EntityNotFoundException{
		Long idL = new Long(id);
		EntityStoreAccess store = getStoreByEntityName(entityName);
		store.deleteEntity(idL);
	}
	
	
	@JSFunction
	public void deleteAll(String entityName, String filter, String value) throws EntityHandlingException, JSONException, EntityNotFoundException{
		EntityStoreAccess store = getStoreByEntityName(entityName);
		store.deleteEntityAll(filter, value);
	}
	
	@JSFunction
	public String query(String entityName, String attr, String val, String sort) throws EntityHandlingException, JSONException{
		EntityStoreAccess store = getStoreByEntityName(entityName);
		
		if(sort.equals("undefined"))
			sort = null;
		
		return store.queryEntity(attr, val, sort).toString();
	}	
	
	@JSFunction
	public String queryAll(String entityName, String sort) throws EntityHandlingException, JSONException{
		EntityStoreAccess store = getStoreByEntityName(entityName);
		
		if(sort.equals("undefined"))
			sort = null;
		
		JSONArray arr = store.queryAllEntity(sort);
		String jsonStr = arr.toString();
		return jsonStr;
	}	
	
	@JSFunction
	public void api(String entityName, JSServletRequest jsReq, JSServletResponse jsResp) throws EntityHandlingException, IOException, JSONException, EntityMandatoryFieldNotProvided, EntityNotFoundException{
		EntityStoreAccess store = getStoreByEntityName(entityName);
		
		
		if(store == null){
			jsResp.sendError(400,"Entity not found");
			return;
		}
		
		String data = jsReq.getBodyContent("data");
		jsReq.getParameterNames();
		
		HashMap<String, EntityAPIDefinition> apiDefs = store.getEntityDefinition().getApiDefinition();
		
		//Prepare List of Foreign Entities
		List<EntityFieldDefinition> fields = store.getEntityDefinition().getFields();
		String fxEntities = "[";
		int cnt = 0;
		for(EntityFieldDefinition field : fields){
			if(field.getType() == EntityFieldType.FOREIGN_ENTITY){
				
				if(cnt > 0)
					fxEntities += ",";
				
				fxEntities += "{ entity : \"" + field.getForeignEntity() + "\" , field : \"" + field.getName() + "\" } ";
				cnt++;
			}
		}
		fxEntities += "]";
		
		//Prepare List of Reverse Foreign Entities
		String reverseFxEntities = "[";
		int cnt2 = 0;
		ArrayList<ReverseLinkDefinition> reverseLinks = store.getEntityDefinition().getReverseLinkDefinitions();
		for(ReverseLinkDefinition link : reverseLinks){
			if(cnt2 > 0)
				fxEntities += ",";
			
			reverseFxEntities += "{ entity : \"" + link.getForeignEntity() + "\" , field : \"" + link.getField() + "\" , name : \"" + link.getName() + "\" } ";
			cnt2++;		
		}
		reverseFxEntities += "]";
		
		String action = (String) jsReq.getParameter("action");
					
		if(action == null || action.equals("") || action.equals("jslib")){
			jsResp.setContentType("text/javascript");
			InputStream is = servletContext.getResourceAsStream("/WEB-INF/entityAPI_TEMPLATE.js");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuffer file = new StringBuffer();
			String line;
			while((line = br.readLine()) != null){
				line = line.replaceAll("#ENTITYNAME#", entityName);
				line = line.replaceAll("#APIURL#", jsReq.getUrlPath());
				line = line.replaceAll("#FX_ENTITIES#", fxEntities);
				line = line.replaceAll("#REVERSE_FX_ENTITIES#", reverseFxEntities);
								
				if(line.startsWith("/*")){
					String actLine = line.substring(2, line.indexOf("*/"));
					if(apiDefs.containsKey(actLine))
						file.append(line.substring(line.indexOf("*/")+2)  + "\n");
				} else {
					file.append(line + "\n");
				}
			}
			
			jsResp.sendResponse(file.toString());
			return;
		}
		
		EntityAPIDefinition apiDef = apiDefs.get(action);
		
		if(apiDef == null){
			jsResp.sendError(400,"Action not allowed/supported");
			return;
		}
		
		if(!apiDef.isAuthorized()){
			jsResp.sendError(400,"Action not allowed/supported");
			return;
		} 
			
		
		if(action.equalsIgnoreCase("put")){
			long newID = put(entityName, data);
			jsResp.sendResponse(new Long(newID).toString());
		}else if(action.equalsIgnoreCase("update")){
			update(entityName, data);
		}else if(action.equalsIgnoreCase("get")){
			String id = jsReq.getParameter("id");
			String result = get(entityName, id);
			
			jsResp.setContentType("application/json");
			jsResp.sendResponse(result);
		}else if(action.equalsIgnoreCase("delete")){
			String id = jsReq.getParameter("id");
			delete(entityName, id);
		}else if(action.equalsIgnoreCase("query")){
			String criteria = jsReq.getParameter("criteria");
			String value = jsReq.getParameter("value");
			String result = query(entityName, criteria, value, "");
			
			jsResp.setContentType("application/json");
			jsResp.sendResponse(result);
		}else if(action.equalsIgnoreCase("queryAll")){
			String result = queryAll(entityName, "");
			
			jsResp.setContentType("application/json");
			jsResp.sendResponse(result);
		}else if(action.equalsIgnoreCase("getFX")){
			String id       = jsReq.getParameter("id");
			String fxEntity = jsReq.getParameter("fxentity");
			
			
			String result = getFX(entityName, id, fxEntity);
			
			jsResp.setContentType("application/json");
			jsResp.sendResponse(result);
			
		}else if(action.equalsIgnoreCase("queryReverseFX")){
			String id       = jsReq.getParameter("id");
			String fxEntity = jsReq.getParameter("fxentity");
			String fxField = jsReq.getParameter("fxfield");
			
			String result = queryReverseFX(entityName, id, fxEntity, fxField, "");
			
			jsResp.setContentType("application/json");
			jsResp.sendResponse(result);
			
		}else{
			jsResp.sendError(400,"Unknown action");
			return;
		}
		
	}
	
	@Override
	public String getClassName() {
		return "EntityStoreScriptObject";
	}

}
