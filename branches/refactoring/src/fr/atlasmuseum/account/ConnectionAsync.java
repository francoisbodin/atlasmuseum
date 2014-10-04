package fr.atlasmuseum.account;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import fr.atlasmuseum.R;
import fr.atlasmuseum.helper.AtlasError;
import fr.atlasmuseum.helper.HttpJson;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class ConnectionAsync extends AsyncTask<String, String, Boolean> {
	
    public interface ConnectionListener {
        public void onConnectionOk();
        public void onConnectionFailed();
    }

	private static final String DEBUG_TAG = "AtlasMuseum/ConnectionAsync";
	
	private Context mContext;
	private ConnectionListener mListener;
	String mUsername;
	String mPassword;
	String mStatus;
	private ProgressDialog mProgress;
	
	// Messages d'erreur
	final String Success = "Success";
	final String WebServiceNoId = "WebServiceNoId";
	final String WebServiceWrongId = "WebServiceWrongId";
	final String WebServiceNoPass = "WebServiceNoPass";
	final String WebServiceWrongPass = "WebServiceWrongPass";
	final String NoName = "NoName";
	final String Illegal = "Illegal";
	final String NotExists = "NotExists";
	final String EmptyPass = "EmptyPass";
	final String WrongPass = "WrongPass";
	
	static enum error {
		WebServiceNoId,
		WebServiceWrongId,
		WebServiceNoPass,
		WebServiceWrongPass,
		NoName,
		Illegal,
		NotExists, 
		EmptyPass,
		WrongPass
	}

	
	public ConnectionAsync(Activity context, String username, String password) {
		mContext = context;
		mUsername = username;
		mPassword = password;
		mStatus = "";
		mProgress = null;
		
        try {
            // Instantiate the ConnectionListener so we can send events to the host
        	mListener = (ConnectionListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + " must implement ConnectionListener");
        }
	}

	@Override
	protected void onPreExecute() {
		Log.d(DEBUG_TAG, "onPreExecute");
	    super.onPreExecute();
		mProgress = new ProgressDialog(mContext);
		mProgress.setMessage(mContext.getResources().getString(R.string.account_connecting));
		mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgress.setCancelable(false);
		mProgress.show();
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		Log.d(DEBUG_TAG, "doInBackground");
		
		if(mUsername == null || mUsername.equals("")) {
			mStatus = NoName;
			return false;
		}
		
		if( mPassword == null || mPassword.equals("")) {
			mStatus = EmptyPass;
			return false;
		}
		
		//String url = "http://atlasmuseum.irisa.fr/www/webservice/api.php?action=validuser&webserviceid=appli&webservicepass=test123&username="+user+"&userpass="+passwd;
		String url = "http://publicartmuseum.net/w/webservice/api.php?action=validuser&webserviceid=appli&webservicepass=test123&username="+mUsername+"&userpass="+mPassword;
		HttpJson httpJ = new HttpJson();
		JSONObject result = null;
		try	{
			Log.d(DEBUG_TAG, "Trying to connect using " + mUsername + "/" + mPassword);
			result = httpJ.getJSONObjectFromUrl(url, new ArrayList<NameValuePair>());
		}
		catch (Exception e) {
			mStatus = "pas de reponse serveur";
			return false;
		}
		
		if (result == null || result.length() == 0)	{
			return false;
		}

		try {
			JSONObject data = result.getJSONObject("validuser");
			Log.i(DEBUG_TAG, "username: " + data.getString("username"));

			mStatus = data.getString("result").trim();
			Log.i(DEBUG_TAG, "result: " + mStatus);

			return mStatus.equals(Success);
		}
		catch (JSONException e) {
			mStatus = "erreur format reponse serveur";
			Log.w(DEBUG_TAG, "Connexion failed: JSONException");
			return false;
		}
	}
	
	@Override
	protected void onProgressUpdate(String... progress) {
	}

	@Override
	protected void onPostExecute(Boolean result) {     
		Log.i(DEBUG_TAG, "onPostExecute");
    	
     	if( mProgress != null ) {
			mProgress.hide();
			mProgress.cancel();
		}
		
		if( result ) {
			Log.d(DEBUG_TAG, "connection success");
			Toast.makeText(mContext, mContext.getResources().getString(R.string.account_connected_as, mUsername), Toast.LENGTH_SHORT).show();
			mListener.onConnectionOk();
			return;
		}
		
		Log.d(DEBUG_TAG, "Connection failed");
		switch(mStatus)	{
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
		mListener.onConnectionFailed();
	}
}
