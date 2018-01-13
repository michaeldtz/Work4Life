package de.dietzm.alphazone.entitystore;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import de.dietzm.alphazone.entitystore.exceptions.EntityHandlingException;
import de.dietzm.alphazone.entitystore.exceptions.EntityMandatoryFieldNotProvided;
import de.dietzm.alphazone.entitystore.model.EntityDefinition;
import de.dietzm.alphazone.entitystore.model.EntityFieldDefinition;
import de.dietzm.alphazone.entitystore.model.EntityFieldType;
import de.dietzm.alphazone.repository.model.RepositoryItem;
import de.dietzm.foundation.db.base.DAO;
import de.dietzm.foundation.db.base.DAOFactory;
import de.dietzm.foundation.useraccess.UserAccessFactory;
import de.dietzm.foundation.useraccess.UserAccessService;

public class EntityStoreAccess {

	private static final String USERNAME_FIELD = "OWNER_USER_FIELD";
	private Long projectID;
	private String entityName;
	private EntityDefinition entityDef;

	public EntityStoreAccess(Long projectID, String entityName) {
		this.projectID = projectID;
		this.entityName = entityName;		
	}

	public EntityDefinition getEntityDefinition() throws EntityHandlingException {

		if (this.entityDef != null)
			return this.entityDef;

		DAO<RepositoryItem> itemDAO = DAOFactory.getEntityManager("RepositoryItem");
		List<RepositoryItem> items = itemDAO.query("projectID", new Long(projectID));

		RepositoryItem selItem = null;

		for (RepositoryItem item : items) {
			if (item.getName().equals(entityName)) {
				selItem = item;
				break;
			} else if (item.getName().equals(entityName + ".etd")) {
				selItem = item;
				break;
			}
		}

		if (selItem == null)
			throw new EntityHandlingException("Entity " + entityName + " not found in this project");

		this.entityDef = EntityDefinition.loadFromRepositoryItem(selItem, entityName);

		return this.entityDef;
	}

	private boolean isUserAuthorized(Entity entity) {
		if (entityDef.isUserSpecific()) {
			
			UserAccessService userService = UserAccessFactory.getAccessService();

			if (entity == null)
				return false;

			String userName = (String) entity.getProperty(USERNAME_FIELD);
			String currentUser = "ANONYMOUS";

			if (userService.isUserLoggedIn())
				currentUser = userService.getCurrentUserName();

			if (userName.equals(currentUser)) {
				return true;
			}

			return false;
		}
		return true;
	}

	private void setUserAuthorization(Entity entity) {
		if (entityDef.isUserSpecific()) {

			String currentUser = "ANONYMOUS";
			UserAccessService userService = UserAccessFactory.getAccessService();
			
			if (userService.isUserLoggedIn())
				currentUser = userService.getCurrentUserName();

			entity.setProperty(USERNAME_FIELD, currentUser);
		}
	}

	public long putEntity(String jsonString) throws EntityHandlingException, JSONException,
			EntityMandatoryFieldNotProvided {

		EntityDefinition def = getEntityDefinition();
		List<EntityFieldDefinition> entityFields = def.getFields();
		JSONObject json = new JSONObject(jsonString);

		Entity entity = new Entity(def.getEntityName());

		for (EntityFieldDefinition field : entityFields) {
			String fieldName = field.getName();

			if (json.has(fieldName)) {
				Object value = json.get(fieldName);
				String valueString = value.toString();
				checkForeignFields(field, valueString);

				value = getValueInRightTypeForElement(def, fieldName, valueString);
				entity.setProperty(fieldName, value);
			} else if (!field.isMandatory()) {
				Object value = getValueInRightTypeForElement(def, fieldName, "");
				entity.setProperty(fieldName, value);				
			} else {
				throw new EntityMandatoryFieldNotProvided(fieldName);
			}

		}

		// Assign Authority Field if defined
		setUserAuthorization(entity);

		DatastoreService dso = DatastoreServiceFactory.getDatastoreService();
		dso.put(entity);

		return entity.getKey().getId();
	}

	private void checkForeignFields(EntityFieldDefinition field, String value) throws NumberFormatException,
			EntityHandlingException {

		// Is the field foreign key enabled
		if (field.getType() == EntityFieldType.FOREIGN_ENTITY && value != null && !value.equals("")) {

			String foreignEnt = field.getForeignEntity();
			EntityStoreAccess foreignStore = new EntityStoreAccess(projectID, foreignEnt);

			if (!foreignStore.hasEntityWithID(new Long(value).longValue())) {
				throw new EntityHandlingException("Foreign Key Invalid Exception");
			}

		}

	}

	public void update(String jsonString) throws EntityHandlingException, JSONException {

		EntityDefinition def = getEntityDefinition();
		List<EntityFieldDefinition> entityFields = def.getFields();
		JSONObject json = new JSONObject(jsonString);
		long ID = json.getLong("id");

		Key entityKey = KeyFactory.createKey(def.getEntityName(), ID);

		try {

			DatastoreService dso = DatastoreServiceFactory.getDatastoreService();
			Entity entity = dso.get(entityKey);

			if (!isUserAuthorized(entity)) {
				throw new EntityNotFoundException(entityKey);
			}

			for (EntityFieldDefinition field : entityFields) {
				String fieldName = field.getName();
				String valueString = json.get(fieldName).toString();
				Object value = getValueInRightTypeForElement(def, fieldName, valueString);
				entity.setProperty(fieldName, value);
			}

			// Assign Authority Field if defined
			setUserAuthorization(entity);

			dso.put(entity);

		} catch (EntityNotFoundException e) {
			json.put("error", e.getLocalizedMessage());
		}

	}

	
	public void updateselective(String jsonString) throws EntityHandlingException, JSONException  {
		
		EntityDefinition def = getEntityDefinition();
		List<EntityFieldDefinition> entityFields = def.getFields();
		JSONObject json = new JSONObject(jsonString);
		long ID = json.getLong("id");

		Key entityKey = KeyFactory.createKey(def.getEntityName(), ID);

		try {

			DatastoreService dso = DatastoreServiceFactory.getDatastoreService();
			Entity entity = dso.get(entityKey);

			if (!isUserAuthorized(entity)) {
				throw new EntityNotFoundException(entityKey);
			}

			for (EntityFieldDefinition field : entityFields) {
				String fieldName = field.getName();
				
				if(json.has(fieldName)){
					String valueString = json.get(fieldName).toString();
					Object value = getValueInRightTypeForElement(def, fieldName, valueString);
					entity.setProperty(fieldName, value);
				}
			}

			// Assign Authority Field if defined
			setUserAuthorization(entity);

			dso.put(entity);

		} catch (EntityNotFoundException e) {
			json.put("error", e.getLocalizedMessage());
		}

	}
	
	
	public JSONObject getForeign(long ID, String foreignEntityFieldname) throws EntityHandlingException,
			JSONException {

		EntityDefinition def = getEntityDefinition();
		EntityStoreAccess foreignStore = null;
		for (EntityFieldDefinition field : def.getFields()) {
			if (field.getName().equalsIgnoreCase(foreignEntityFieldname)) {
				String foreignEntity = field.getForeignEntity();
				foreignStore = new EntityStoreAccess(projectID, foreignEntity);
			}
		}

		if (foreignStore == null)
			throw new EntityHandlingException("Foreign Entity not found");

		JSONObject json = new JSONObject();
		Key entityKey = KeyFactory.createKey(def.getEntityName(), ID);

		try {

			DatastoreService dso = DatastoreServiceFactory.getDatastoreService();
			Entity entity = dso.get(entityKey);

			if (!isUserAuthorized(entity)) {
				return json;
			}

			String foreignKey = (String) entity.getProperty(foreignEntityFieldname);
			json = foreignStore.getEntity(new Long(foreignKey).longValue());

		} catch (EntityNotFoundException e) {
			json.put("error", e.getLocalizedMessage());
		}

		return json;
	}

	public Object queryReserveForeign(Long idL, String foreignEntity, String foreignFieldname, String sort)
			throws EntityHandlingException, JSONException {

		EntityStoreAccess foreignStore = new EntityStoreAccess(projectID, foreignEntity);
		return foreignStore.queryEntity(foreignFieldname, idL.toString(), sort);

	}

	private boolean hasEntityWithID(long ID) throws EntityHandlingException {

		EntityDefinition def = getEntityDefinition();

		Key entityKey = KeyFactory.createKey(def.getEntityName(), ID);

		try {

			DatastoreService dso = DatastoreServiceFactory.getDatastoreService();
			Entity entity = dso.get(entityKey);

			if (!isUserAuthorized(entity)) {
				return false;
			}

			return true;

		} catch (EntityNotFoundException e) {

		}

		return false;
	}

	public JSONObject getEntity(long ID) throws EntityHandlingException, JSONException {

		EntityDefinition def = getEntityDefinition();
		List<EntityFieldDefinition> entityFields = def.getFields();
		JSONObject json = new JSONObject();

		Key entityKey = KeyFactory.createKey(def.getEntityName(), ID);

		try {

			DatastoreService dso = DatastoreServiceFactory.getDatastoreService();
			Entity entity = dso.get(entityKey);

			if (!isUserAuthorized(entity)) {
				return json;
			}

			for (EntityFieldDefinition field : entityFields) {
				String fieldName = field.getName();
				Object value = entity.getProperty(fieldName);
				
				value = decodeObjectFromRightType(field, value);
				
				json.put(fieldName, value);
			}

			json.put("id", entity.getKey().getId());

		} catch (EntityNotFoundException e) {
			json.put("error", e.getLocalizedMessage());
		}

		return json;
	}

	private Object decodeObjectFromRightType(EntityFieldDefinition field, Object value) {
		if(value instanceof Text){
			Text txt = (Text)value;
			return txt.getValue();
		}
		
		return value;
	}

	public boolean deleteEntity(long ID) throws EntityHandlingException, JSONException,
			EntityNotFoundException {

		EntityDefinition def = getEntityDefinition();
		Key entityKey = KeyFactory.createKey(def.getEntityName(), ID);

		DatastoreService dso = DatastoreServiceFactory.getDatastoreService();
		Entity entity = dso.get(entityKey);

		if (!isUserAuthorized(entity)) {
			throw new EntityNotFoundException(entityKey);
		}

		dso.delete(entityKey);

		return true;
	}

	public void deleteEntityAll(String filterAttr, String filterVal) throws EntityHandlingException {

		EntityDefinition def = getEntityDefinition();
		Filter filter = new FilterPredicate(filterAttr, FilterOperator.EQUAL, filterVal);
		Query query = new Query(def.getEntityName()).setFilter(filter);

		DatastoreService dso = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery prepQuery = dso.prepare(query);

		for (Entity entity : prepQuery.asIterable()) {
			dso.delete(entity.getKey());
		}

	}

	public JSONArray queryEntity(String attr, String val, String sort) throws EntityHandlingException,
			JSONException {

		EntityDefinition def = getEntityDefinition();
		List<EntityFieldDefinition> entityFields = def.getFields();
		JSONArray jsonArr = new JSONArray();

		//EntityFieldDefinition fieldDef = def.getFieldByName(attr);
	
		Object filterValue = getValueInRightTypeForElement(def, attr, val);
		Filter filter = new FilterPredicate(attr, Query.FilterOperator.EQUAL, filterValue);

		Query query = new Query(def.getEntityName());
		query.setFilter(filter);

		if (sort != null && !sort.equals(""))
			query.addSort(sort);

		DatastoreService dso = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery prepQuery = dso.prepare(query);

		for (Entity entity : prepQuery.asIterable()) {

			if (!isUserAuthorized(entity))
				continue;

			JSONObject json = new JSONObject();
			for (EntityFieldDefinition field : entityFields) {
				String fieldName = field.getName();
				Object value = entity.getProperty(fieldName);
				value = decodeObjectFromRightType(field, value);
				json.put(fieldName, value);
			}

			json.put("id", entity.getKey().getId());

			jsonArr.put(json);
		}

		return jsonArr;
	}

	public JSONArray queryAllEntity(String sort) throws EntityHandlingException, JSONException {

		EntityDefinition def = getEntityDefinition();
		List<EntityFieldDefinition> entityFields = def.getFields();
		JSONArray jsonArr = new JSONArray();

		Query query = new Query(def.getEntityName());

		if (sort != null && !sort.equals(""))
			query.addSort(sort);

		DatastoreService dso = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery prepQuery = dso.prepare(query);

		for (Entity entity : prepQuery.asIterable()) {

			if (!isUserAuthorized(entity))
				continue;

			JSONObject json = new JSONObject();
			for (EntityFieldDefinition field : entityFields) {
				String fieldName = field.getName();
				Object value = entity.getProperty(fieldName);
				value = decodeObjectFromRightType(field, value);
				json.put(fieldName, value);
			}

			json.put("id", entity.getKey().getId());

			jsonArr.put(json);
		}

		return jsonArr;
	}

	public Object getValueInRightTypeForElement(EntityDefinition def, String attr, String value) {
		EntityFieldDefinition fieldDef = def.getFieldByName(attr);
		if (fieldDef != null)
			return getValueInRightType(fieldDef.getType(), value);
		return value;
	}

	public Object getValueInRightType(EntityFieldType type, String value) {
		if (type == EntityFieldType.FOREIGN_ENTITY)
			return new Long(value);
		else if (type == EntityFieldType.INT) {
			try {
				return new Integer(value);
			} catch (NumberFormatException e) {
				return new Integer(0);
			}
		} else if (type == EntityFieldType.DOUBLE) {
			try {
				return new Double(value);
			} catch (NumberFormatException e) {
				return new Double(0);
			}
		} else if (type == EntityFieldType.FLOAT) {
			try {
				return new Float(value);
			} catch (NumberFormatException e) {
				return new Float(0);
			}
		} else if (type == EntityFieldType.TIMESTAMP) {
			try {
				return new Long(value);
			} catch (NumberFormatException e) {
				return new Long(0);
			}
		} else if (type == EntityFieldType.TEXT) {
			try {
				return new Text(value);
			} catch (NumberFormatException e) {
				return new Text("");
			}
		} else if (type == EntityFieldType.USERID) {
			try {
				return new Integer(value);
			} catch (NumberFormatException e) {
				return new Integer(0);
			}
		} else
			return value;
	}

	

}
