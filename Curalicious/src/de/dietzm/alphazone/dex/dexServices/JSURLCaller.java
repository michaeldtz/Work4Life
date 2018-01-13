package de.dietzm.alphazone.dex.dexServices;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSFunction;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class JSURLCaller extends ScriptableObject {

	public JSURLCaller(){
		String[] functionNames = { "urlGet", "urlPost" };
		defineFunctionProperties(functionNames, JSURLCaller.class, ScriptableObject.DONTENUM);
	}
	
	@JSFunction
	public void setProxy(String hostname, String port){
		System.setProperty("http.proxyHost", hostname);
		System.setProperty("http.proxyPort", port);
	}
	
	@JSFunction
	public String urlGet(String urlString, String headers) {

		try {
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();

			conn.setDoInput(true);
			conn.setDoOutput(false);
	
			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			String text = "";
			String line;
			while (((line = br.readLine()) != null)) {
				text += line + "\n";
			}

			return text;
			
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		
		return null;
	}

	@JSFunction
	public String urlPost(String urlString, String content, String headers) throws JSONException {

		try {
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();

			conn.setDoInput(true);

			if (!content.equals("undefined")) {
				content = "";
			}
			
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Content-Length",
					String.valueOf(content.getBytes().length));

			if(headers!= null && !headers.equals("")){
				JSONObject headerJSON = new JSONObject(headers);
				JSONArray headerNames = headerJSON.names();
				for (int i = 0; i < headerNames.length(); i++) {
					String name = headerNames.getString(i);
					String value = headerJSON.getString(name);
					conn.setRequestProperty(name, value);
				}
			}
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					conn.getOutputStream()));
			bw.write(content);
			bw.flush();
			bw.close();
			

			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			String text = "";
			String line;
			while (((line = br.readLine()) != null)) {
				text += line + "\n";
			}

			return text;
			
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		
		return null;
	}

	
	@Override
	public String getClassName() {
		return "JSURLCaller";
	}

}
