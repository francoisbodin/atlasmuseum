package fr.atlasmuseum.compte;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import fr.atlasmuseum.contribution.HttpJson;

/**
 * classe qui permet de verifier si l'utilisateur est bien dans la BDD, pour la connexion
 * utilisé dans AutoConnexionAsync
 * @author Expert
 *
 */
public class UserChecker {

	private HashMap<String, String> infoUtilisateur =Authentification.getinfoUser();
	private String pseudo;
	private String mdp;
	private String URL_LOGIN;
	private String DEBUG_TAG="UserChecker";
	
	public UserChecker(String pseudo, String mdp, String url){
		this.pseudo=pseudo;
		this.mdp=mdp;
		URL_LOGIN= url;
	}

/**
 * Utiliser dans AutoCheckAsync
 * @return
 */
public boolean checkUser(){
    	
	String u=pseudo;
	String p=mdp;
	
	boolean uValid = ((u ==null)|| u.equals(""));
	boolean pValid = ((p ==null)|| p.equals(""));
	
	/**if(uValid)
	{
		
		return false;
	}
	if(pValid)
	{
		
		return false;
	}**/
	
	String url = "http://atlasmuseum.irisa.fr/www/webservice/api.php?action=validuser&webserviceid=appli&webservicepass=test123&username="+u+"&userpass="+p;
	Log.d(DEBUG_TAG, url);
	ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
	
	HttpJson httpJ = new HttpJson();

	
	JSONObject result = httpJ.getJSONObjectFromUrl(url, nvps);
	
	if (result != null && result.length() > 0)
	{ 

		try
		{
			String mStatus;
			JSONObject data = result.getJSONObject("validuser");
			String server_status = data.getString("username");
			System.out.println(DEBUG_TAG+"/username: " + server_status);
			
			String server_result = data.getString("result");
			System.out.println(DEBUG_TAG+"/result: " + server_result);
			mStatus =server_result.trim();
			
			if(mStatus.equals("Success"))
			{
				Authentification.setUsername(pseudo);
				Authentification.setPassword(mdp);
				return true;
			}
			else
			{
				return false;
			}
			
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.w(DEBUG_TAG+"/connexion"+" failed", "JSONException");
			return false;
		}
	}
	
	return false;
    }
}
