package fr.atlasmuseum.compte;


import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;



import fr.atlasmuseum.R;
import fr.atlasmuseum.contribution.HttpJson;
import fr.atlasmuseum.main.AtlasError;
import fr.atlasmuseum.main.MainActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Classe qui permet la connexion utilisateur, appell� dans ConnexionActivity
 *
 */
public class ConnexionAsync extends AsyncTask<String, String, Boolean> {
	
	String mStatus;
	private ConnexionActivity mContext;
	private ProgressDialog mProgress;//barre d'avancement
	private static final String DEBUG_TAG = "AtlasMuseum/ConnexionAsync";
	String user;
	String passwd;
	//message erreur
	final String Success ="Success";
			final String WebServiceNoId = "WebServiceNoId";
			final String WebServiceWrongId = "WebServiceWrongId";
			final String WebServiceNoPass = "WebServiceNoPass";
			final String WebServiceWrongPass = "WebServiceWrongPass";
			final String NoName = "NoName";
			final String Illegal = "Illegal";
			final String NotExists = "NotExists";
			final String EmptyPass = "EmptyPass";
			final String WrongPass = "WrongPass";
			
	static enum error
	{
		WebServiceNoId, WebServiceWrongId, WebServiceNoPass,
		WebServiceWrongPass, NoName, Illegal, NotExists, 
		EmptyPass, WrongPass
	}

	
	public ConnexionAsync(ConnexionActivity act)
	{
		mContext = act;
		mStatus = "";
	}
	
	



	@Override
	protected void onPreExecute() {
		Log.d(DEBUG_TAG, "onPreExecute");
	    super.onPreExecute();
		mProgress = new ProgressDialog(mContext);
		mProgress.setMessage(mContext.getResources().getString(R.string.connexion_encours));
		mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgress.setCancelable(false);
		mProgress.show();
		
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		
		Log.d(DEBUG_TAG+"/doInBackground", "******** connexion en cours... *******");
		//boolean continuerAEnvoyer
		
		user=this.mContext.getUser();
		Log.d(DEBUG_TAG+"/doInBackground", "user = *"+user+"*");
		
		passwd=this.mContext.getPassword();
		
		boolean uValid = ((user ==null)|| user.equals(""));
		boolean pValid = ((passwd ==null)|| passwd.equals(""));
		
		if(uValid)
		{
			mStatus=NoName;
			return false;
		}
		if(pValid)
		{
			mStatus=EmptyPass;
			return false;
		}
		
		//String url = "http://atlasmuseum.irisa.fr/www/webservice/api.php?action=validuser&webserviceid=appli&webservicepass=test123&username="+user+"&userpass="+passwd;
		String url = "http://publicartmuseum.net/w/webservice/api.php?action=validuser&webserviceid=appli&webservicepass=test123&username="+user+"&userpass="+passwd;
		ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
		//inutilis�
		nvps.add(new BasicNameValuePair("action", "validuser"));
		nvps.add(new BasicNameValuePair("webserviceid", "XXX"));
		nvps.add(new BasicNameValuePair("webservicepass", "YYY"));
		nvps.add(new BasicNameValuePair("username", ""));
		nvps.add(new BasicNameValuePair("userpass", ""));
		HttpJson httpJ = new HttpJson();
		
		JSONObject result = null;
		try
		{

			result = httpJ.getJSONObjectFromUrl(url, nvps);
		} catch (Exception e)
		{
			this.mStatus ="pas de reponse serveur";
			return false;
		}
		
		if (result != null && result.length() > 0)
		{ 

			try
			{
				
				JSONObject data = result.getJSONObject("validuser");
				String server_status = data.getString("username");
				Log.i(DEBUG_TAG, "username: " + server_status);
				
				
				String server_result = data.getString("result");
				Log.i(DEBUG_TAG, "result: " + server_result);
				
				this.mStatus =server_result.trim();
				
				if(mStatus.equals(Success))
				{
					return true;
				}
				else
				{
					return false;
				}
				
			
			} catch (JSONException e) {
				this.mStatus ="erreur format reponse serveur";
				Log.w(DEBUG_TAG+"/connexion"+" failed", "JSONException");
				return false;
			}
		}
		
		return false;
	}
		
	@Override
	protected void onProgressUpdate(String... progress) {
	}

	@Override
	protected void onPostExecute(Boolean result) {     
		Log.i(DEBUG_TAG, "onPostExecute");
    	
		
     	String f = error.WebServiceNoId.toString();
		if( mProgress != null ) {
			mProgress.hide();
			mProgress.cancel();
		}
		if( result ) {
			Log.d(DEBUG_TAG, "connexion success");
			Authentification.setisConnected(true);
			Authentification.setUsername(user);
			Authentification.setPassword(passwd);
			
			if(MainActivity.getCPrefs().getBoolean("AUTO_ISCHECK", false))
			{
				MainActivity.getCPrefs().edit().putString("username", user).commit();
				MainActivity.getCPrefs().edit().putString("password", passwd).commit();
			}
			
			MainActivity.getCPrefs().edit().putBoolean("DEJACO", true).commit();
			Toast.makeText(mContext, mContext.getResources().getString(R.string.CONNECTED_AS)+" "+Authentification.getUsername(), Toast.LENGTH_SHORT).show();
			
			/**if(backtomain)
			{
				Intent intent=new Intent(mContext, MainActivity.class);
				mContext.jumpAct(intent);
				mContext.finish();
			}
			else
			{**/
				mContext.giveGoodResult();
		}
		else 
		{
			switch(mStatus)
			{
				case WebServiceNoId:
					AtlasError.showErrorDialog(mContext, "9.1", "no id");
					break;
				case WebServiceWrongId:
					AtlasError.showErrorDialog(mContext, "9.2", "wrong id");
					break;
				case WebServiceNoPass:
					AtlasError.showErrorDialog(mContext, "9.3", "no passwd");
					break;
				case WebServiceWrongPass:
					AtlasError.showErrorDialog(mContext, "9.4", "wrong passwd");
					break;
				case NoName:
					AtlasError.showErrorDialog(mContext, "9.5", "no name");
					break;
				case Illegal:
					AtlasError.showErrorDialog(mContext, "9.6", "username inexistant");
					break;
				case NotExists:
					AtlasError.showErrorDialog(mContext, "9.7", "no id");
					break;
				case EmptyPass:
					AtlasError.showErrorDialog(mContext, "9.8", "mdp vide");
					break;
				case WrongPass:
					AtlasError.showErrorDialog(mContext, "9.9", "mdp invalide");
					break;
			}
			Log.d(DEBUG_TAG, "ECHEC connexion");
			Authentification.setisConnected(false);
			//AtlasError.showErrorDialog(mContext, "1.1", mContext.getResources().getString(R.string.ECHEC_AUTHENTIFICATION));
			//*********************************************
			mContext.getAuto_check().setChecked(false);//d�coche la connexion auto, sinon on peut plus acceder a ecran co
			
			
			//*********************************************
		}
	}

	

}
