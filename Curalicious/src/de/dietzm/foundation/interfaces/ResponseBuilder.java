package de.dietzm.foundation.interfaces;

import java.util.List;

import javax.ws.rs.core.Response;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class ResponseBuilder {

	
	
	public static Response createListJSONResponse(List<?> list, boolean detail) {

		try {
			JSONArray jsonArr = new JSONArray();
			for (Object entry : list) {
				JSONObject jsonObj = JSONBuilder.convertObjectToJSON(entry, detail);
				jsonArr.put(jsonObj);
			}

			return Response.ok(jsonArr.toString()).build();
		} catch (JSONException e) {
			return Response.serverError().build();
		}
	}

	public static Response createObjectJSONResponse(Object object, boolean detail) {
		try {
			JSONObject json = JSONBuilder.convertObjectToJSON(object, detail);
			json.put("success", true);
			return Response.ok(json.toString()).build();
		} catch (JSONException e) {
			return Response.serverError().build();
		}
	}

	public static Response createSuccessResponse() {
		try {
			JSONObject json = new JSONObject();
			json.put("success", true);

			return Response.ok(json.toString()).build();
		} catch (JSONException e) {
			return Response.serverError().build();
		}

	}

	public static Response createNewObjectCreated(Long idOfNewObject) {


		try {
			JSONObject json = new JSONObject();
			json.put("success", true);
			json.put("newId", idOfNewObject);
			json.put("success", true);
	
			return Response.ok(json.toString()).build();
		} catch (JSONException e) {
			return Response.serverError().build();
		}
	}

	public static Response createErrorResponse(String message, String errorcode) {

		try {
			JSONObject json = new JSONObject();
			json.put("success", false);
			json.put("error", true);
			json.put("errorcode", errorcode);
			json.put("message", message);

			return Response.ok(json.toString()).build();
		} catch (JSONException e) {
			return Response.serverError().build();
		}
	}
	
	public static Response createErrorResponseWithReference(String message, String errorcode, String reference) {

		try {
			JSONObject json = new JSONObject();
			json.put("success", false);
			json.put("error", true);
			json.put("errorcode", errorcode);
			json.put("message", message);
			json.put("reference", reference);
			

			return Response.ok(json.toString()).build();
		} catch (JSONException e) {
			return Response.serverError().build();
		}
	}

	public static Response createNewObjectCreatedResponse(String object, Long newId) {

		try {
			JSONObject json = new JSONObject();
			json.put("success", true);
			json.put("object", object);
			json.put("id", newId);

			return Response.ok(json.toString()).build();
		} catch (JSONException e) {
			return Response.serverError().build();
		}
	}

	public static Response createStatusResponse(String status, String message) {
		try {
			JSONObject json = new JSONObject();
			json.put("success", true);
			json.put("status", status);
			json.put("message", message);

			return Response.ok(json.toString()).build();
		} catch (JSONException e) {
			return Response.serverError().build();
		}
	}

	public static Response createCustomResponse(String key, String value) {
		try {
			JSONObject json = new JSONObject();
			json.put("success", true);
			json.put(key, value);

			return Response.ok(json.toString()).build();
		} catch (JSONException e) {
			return Response.serverError().build();
		}
	}
	
	public static Response createFlexibleResponse(String... values) {
		try {
			JSONObject json = new JSONObject();
			json.put("success", true);
			for (int i = 0; i < values.length; i=i+2) {				
				json.put(values[i], values[i+1]);
			}
			return Response.ok(json.toString()).build();
		} catch (JSONException e) {
			return Response.serverError().build();
		}
	}

	public static Response createNewObjectCreatedResponse(String object, Object newObject) {
		try {
			JSONObject json = new JSONObject();
			json.put("success", true);
			json.put("object", JSONBuilder.convertObjectToJSON(newObject, false));

			return Response.ok(json.toString()).build();
		} catch (JSONException e) {
			return Response.serverError().build();
		}
	}

	

}
