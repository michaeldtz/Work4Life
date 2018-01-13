package de.dietzm.foundation.interfaces;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class JSONBuilder {

	public static JSONArray convertListToJSONArray(List<Object> list, boolean detail) throws JSONException {
		JSONArray arr = new JSONArray();
		for (Object object : list) {
			JSONObject obj = convertObjectToJSON(object, detail);
			arr.put(obj);
		}

		return arr;

	}

	public static JSONObject convertObjectToJSON(Object object, boolean detail) throws JSONException {
		JSONObject jsonObj = new JSONObject();

		Method[] methods = object.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			String name = method.getName();

			if (name.equals("getClass"))
				continue;

			if (name.startsWith("get")) {
				try {
					if (!method.isAnnotationPresent(NoJSONConversion.class)) {
						if (method.isAnnotationPresent(DetailOnlyJSONConversion.class) && !detail) {
							continue;
						}
						Object obj = method.invoke(object, new Object[0]);
						String varName = name.substring(3);
						varName = varName.substring(0, 1).toLowerCase() + varName.substring(1);
						jsonObj.put(varName, obj);
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new JSONException("Dynamic Object Creation Failed " + e.toString());
				}
			}
		}
		return jsonObj;
	}

	public static Object convertJSON2Object(String jsonString, Class<?> clz) throws JSONConversionException {

		try {
			JSONObject json = new JSONObject(jsonString);
			Object object = clz.newInstance();

			Method[] methods = object.getClass().getMethods();
			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				String name = method.getName();
				if (name.startsWith("set")) {
					try {
						if (!method.isAnnotationPresent(NoJSONConversion.class)) {
							String varName = name.substring(3);
							varName = varName.substring(0, 1).toLowerCase() + varName.substring(1);

							if (json.has(varName)) {
								Object value = json.get(varName);

								Class<?> clzType = method.getParameterTypes()[0];
								if (clzType == int.class) {
									int intValue = new Integer(value.toString()).intValue();
									method.invoke(object, new Object[] { intValue });
								} else if (clzType == long.class) {
									long longValue = (long) value;
									method.invoke(object, new Object[] { longValue });
								} else {
									method.invoke(object, new Object[] { value });
								}
							}
						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| JSONException e) {
						System.out.println("JSON to Object Conversion Error " + e.toString());
					}
				}
			}

			return object;
		} catch (Exception e) {
			throw new JSONConversionException(e.getLocalizedMessage());
		}
	}

}
