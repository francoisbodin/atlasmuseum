package fr.atlasmuseum.contribution;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/*Creer la requete HTTP pour recuperer les donnees JSON et retourne un objet JSON
Note : dans la chaine JSON, "[ ]" est un tableau, "{}" est un objet*/

public class HttpJson  {
	private DefaultHttpClient httpClient;
	//private static String json = "";

	private static final String DEBUG_TAG = "atlasContrib/HttpJson";

	public HttpJson(){}

	public JSONArray getJSONFromUrl(String url) {
		return getJSONFromUrl(url, null);
	}
	
	public JSONArray getJSONFromUrl(String url, List<NameValuePair> values) {
		JSONArray returnValue = null;
		InputStream is = null;
		boolean KO = false;
		try {		
			httpClient = new DefaultHttpClient();
			HttpParams httpParameters = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
			HttpConnectionParams.setSoTimeout(httpParameters, 20000);
			HttpConnectionParams.getConnectionTimeout(httpParameters);
			HttpPost httpPost = new HttpPost(url);
			if( values != null) {
				httpPost.setEntity(new UrlEncodedFormEntity(values));
				Log.d(DEBUG_TAG+"/valuePair", "Objet possede bien value pair");
			}
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();   
		} catch (Exception e) {
			KO = true;
			Log.e(DEBUG_TAG, "HTTP error");
			//e.printStackTrace();
				Log.d(DEBUG_TAG+"/reponse failed", "FAILED");
			returnValue = null;
		}

				
		String json = "";
		if(!KO){
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8000);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}

				is.close();
				json = sb.toString();
			} catch (Exception e) {
				Log.e(DEBUG_TAG, "Read buffer error");
				e.printStackTrace();
			}

			try {
				Log.d(DEBUG_TAG, "contenu reponse = "+json);
				returnValue = new JSONArray(json);
				httpClient.getConnectionManager().shutdown();
			}
			catch (JSONException e){
				Log.e(DEBUG_TAG, "JSON Parser, Error parsing data");
				e.printStackTrace();
				returnValue = null;;
			}
		}
		//Log.d(DEBUG_TAG, "return value ="+returnValue.toString());
		return returnValue;
	}
	
	

	public JSONObject getJSONObjectFromUrl(String url, List<NameValuePair> values) {
		JSONObject returnValue = null;
		InputStream is = null;
		boolean KO = false;
		try {		
			httpClient = new DefaultHttpClient();
			HttpParams httpParameters = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
			HttpConnectionParams.setSoTimeout(httpParameters, 20000);
			HttpConnectionParams.getConnectionTimeout(httpParameters);
			HttpPost httpPost = new HttpPost(url);
			if( values != null) {
				httpPost.setEntity(new UrlEncodedFormEntity(values));
				Log.d(DEBUG_TAG+"/valuePair", "Objet possede bien value pair");
			}
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();   
		} catch (Exception e) {
			KO = true;
			Log.e(DEBUG_TAG, "HTTP error");
			//e.printStackTrace();
			Log.d(DEBUG_TAG+"/reponse failed", "FAILED");
			returnValue = null;
		}

				
		String json = "";
		if(!KO){
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8000);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}

				is.close();
				json = sb.toString();
			} catch (Exception e) {
				Log.e(DEBUG_TAG, "Read buffer error");
				e.printStackTrace();
			}

			try {
				Log.d(DEBUG_TAG, "contenu reponse = "+json);
				returnValue = new JSONObject(json);
				httpClient.getConnectionManager().shutdown();
			}
			catch (JSONException e){
				Log.e(DEBUG_TAG, "JSON Parser, Error parsing data");
				e.printStackTrace();
				returnValue = null;;
			}
		}
		//Log.d(DEBUG_TAG, "return value ="+returnValue.toString());
		return returnValue;
	}
}