package de.dietzm.alphazone.entitystore.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.google.appengine.api.datastore.Blob;
import com.sun.jersey.core.util.Base64;

import de.dietzm.alphazone.entitystore.exceptions.EntityHandlingException;
import de.dietzm.alphazone.repository.model.RepositoryItem;

public class EntityDefinition {

	String entityName;
	List<EntityFieldDefinition> fields;
	boolean userSpecific;

	private HashMap<String,EntityAPIDefinition> apiDefinition = new HashMap<String,EntityAPIDefinition>();
	private ArrayList<ReverseLinkDefinition> reverseLinkDef = new ArrayList<ReverseLinkDefinition>();
	
	public EntityDefinition() {
		this.fields = new ArrayList<EntityFieldDefinition>();
	}

	public EntityDefinition(String entityName) {
		this();
		this.entityName = entityName;
	}

	public static EntityDefinition loadFromRepositoryItem(RepositoryItem item, String origEntityName) throws EntityHandlingException {
		Blob bl = item.getContentBlob();
	
		if (bl == null)
			throw new EntityHandlingException("Entity " + origEntityName + " not found");

		String name = item.getProjectID() + "_" + origEntityName;
		String entityDefinition = new String(Base64.decode(bl.getBytes()));	
		EntityDefinition entity = new EntityDefinition(name);
		
		ArrayList<EntityFieldDefinition> fields = new ArrayList<EntityFieldDefinition>();

		StringTokenizer st = new StringTokenizer(entityDefinition, "\n");
		while (st.hasMoreTokens()) {
			String line = st.nextToken();

			if (line.startsWith("@")) {
				handleEntityDescriptor(line, entity, fields);
				continue;
			}

			String[] fieldDef = line.split(":");

			if (fieldDef.length < 1)
				continue;
			
			EntityFieldDefinition field = new EntityFieldDefinition(fieldDef[0]);
			if(fieldDef.length >= 2){
				
				String typeStr = fieldDef[1];
				
				if(typeStr.startsWith("#")){
					String foreignEntity = typeStr.substring(1);
					field.setType(EntityFieldType.FOREIGN_ENTITY);
					field.setForeignEntity(foreignEntity);
				} else {
					EntityFieldType type = EntityFieldType.byName(typeStr);
					if(type != EntityFieldType.NULL)
						field.setType(type);
				}				
			}
			
			if(fieldDef.length > 2){
				for(int j = 2; j < fieldDef.length; j++){					
					String fieldDescr = fieldDef[j];
					handleEntityFieldDescriptor(fieldDescr, field);
				}				
			}
			fields.add(field);
		}

		entity.fields = fields;
		
		return entity;

	}

	private static void handleEntityFieldDescriptor(String fieldDescr, EntityFieldDefinition field) {
		if (fieldDescr.equalsIgnoreCase("@Mandatory")) {
			field.setMandatory(true);
		}		
	}

	private static void handleEntityDescriptor(String line, EntityDefinition entity, ArrayList<EntityFieldDefinition> fields) {

		if (line.equalsIgnoreCase("@AdministrativeData")) {
			fields.add(new EntityFieldDefinition("createdBy", EntityFieldType.USERID));
			fields.add(new EntityFieldDefinition("changedBy", EntityFieldType.USERID));
			fields.add(new EntityFieldDefinition("createdOn", EntityFieldType.TIMESTAMP));
			fields.add(new EntityFieldDefinition("changedOn", EntityFieldType.TIMESTAMP));
		}
		
		if (line.equalsIgnoreCase("@UserSpecific")) {
			entity.setUserSpecific(true);
		}
		
		//Generate API: Example @API:put,get,queryAll:PUBLIC
		if (line.startsWith("@API")) {
			
			String[] splits = line.split(":");
			
			String role = null;
			
			if(splits.length >= 3){
				role = splits[2];
			}
			
			if(splits.length >= 2){
				String methods = splits[1];
				String[] methodList = methods.split(",");
				
				for(String method : methodList){
				
					EntityAPIDefinition apiDef = entity.apiDefinition.get(method);
					if(apiDef == null)
						apiDef = new EntityAPIDefinition(method);
					
					apiDef.addRole(role);
					
					//Update
					entity.apiDefinition.put(method,apiDef);
				}			
				
			}
			
		}
		
		
		//Generate API: Example @API:put,get,queryAll:PUBLIC
		if (line.startsWith("@ReverseEntity")) {
					
			String[] splits = line.split(":");
				
			if(splits.length >= 4){
				
				String name    = splits[1];
				String fentity = splits[2];
				String field   = splits[3];
				
				
				ReverseLinkDefinition link = new ReverseLinkDefinition(fentity, field, name);
				entity.reverseLinkDef.add(link);					
					
			}
		}

	}

	private void setUserSpecific(boolean userSpecific) {
		this.userSpecific = userSpecific;
	}

	public boolean isUserSpecific() {
		return userSpecific;
	}

	public String getEntityName() {
		return entityName;
	}

	public List<EntityFieldDefinition> getFields() {
		return fields;
	}

	public HashMap<String, EntityAPIDefinition> getApiDefinition() {
		return apiDefinition;
	}

	public ArrayList<ReverseLinkDefinition> getReverseLinkDefinitions() {
		return reverseLinkDef;
	}

	public EntityFieldDefinition getFieldByName(String attr) {
		Iterator<EntityFieldDefinition> fieldIt = fields.iterator();
		while(fieldIt.hasNext()){
			EntityFieldDefinition field = fieldIt.next();
			if(field.getName().equals(attr))
				return field;
		}
		return null;
	}
	
	

}
