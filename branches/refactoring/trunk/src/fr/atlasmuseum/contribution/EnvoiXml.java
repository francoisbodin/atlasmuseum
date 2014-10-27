package fr.atlasmuseum.contribution;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;







import fr.atlasmuseum.R;
import fr.atlasmuseum.main.MainActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class EnvoiXml extends AsyncTask<String, String, Boolean> {
	String chaineXml;
	String mStatus;
	String urlSend;
	private final Context mContext;
	private ProgressDialog mProgress;//barre d'avancement
	private static final String DEBUG_TAG = "AtlasMuseum/envoiXml";
	Boolean erase;
	EnvoiXml(Context main, String chaine, Boolean erase) {
		//Log.d(DEBUG_TAG, "chaineXML ="+chaine);
		this.chaineXml = chaine;
		mContext = main;
		mStatus = "";
		this.erase=erase;
	}


	@Override
	protected void onPreExecute() {
		Log.d(DEBUG_TAG, "onPreExecute");
	    super.onPreExecute();

		mProgress = new ProgressDialog(mContext);
		mProgress.setMessage(mContext.getResources().getString(R.string.sending_contrib_file));
		mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgress.setCancelable(false);
		mProgress.show();
		
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		// TODO Auto-generated method stub

		
		Log.d(DEBUG_TAG+"/sendXML", "******** ENVOI XML *******");
		Log.d(DEBUG_TAG+"/sendXML", "contenu = "+chaineXml); //affiche le XML
		String mStatus;
		ArrayList<NameValuePair> nvps1 = new ArrayList<NameValuePair>();
		nvps1.add(new BasicNameValuePair("argxml", chaineXml));
		
		HttpJson httpJ = new HttpJson();
		
		JSONArray result = httpJ.getJSONFromUrl("http://atlasmuseum.irisa.fr/scripts/receiveContributionFile.php", nvps1);
		if (result != null && result.length() > 0)
		{ //si c'est bien envoy�
			JSONObject json_data;
			try
			{
				json_data = result.getJSONObject(0);
				String server_status = json_data.getString("commandstatus");
				
				Log.i(DEBUG_TAG, "Feedback from server: " + server_status);
				if (! server_status.equals("ok")) 
				{
					mStatus = "upload failed"+server_status;
					Log.w(DEBUG_TAG, mStatus);
					return false;
				}
			else
				{//feedback s'est bien pass�
					mStatus = "upload success";
					Log.w(DEBUG_TAG, mStatus);
					return true;
				}
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.w(DEBUG_TAG+" failed", "JSONException");
				e.printStackTrace();
			}
		}
		return true;
	}
		
	@Override
	protected void onProgressUpdate(String... progress) {
	}

	@Override
	protected void onPostExecute(Boolean result) {     
		Log.i(DEBUG_TAG, "onPostExecute");
		
		//callValidationXml();
		if( mProgress != null ) {
			mProgress.hide();
			mProgress.cancel();
		}
		if( result ) {
			Log.d(DEBUG_TAG, "envoi XML avec succes");
			if(this.erase)
			{
				mContext.deleteFile(ContribXml.nomFichier);
				((MainContribActivity) mContext).recreate();
			}
			else
			{
				((ListChampsNoticeModif) mContext).setResult(Activity.RESULT_OK);
				((ListChampsNoticeModif) mContext).finish();
				/**((ListChampsNoticeModif) mContext).onBackPressed();
				Intent intent= new Intent(mContext,MainContribActivity.class);
				mContext.startActivity(intent);**/
			}

		}
		else {
			Log.d(DEBUG_TAG, "ECHEC DE l'ENVOI xml");
		}
	}


	private void callValidationXml() {
		// TODO Auto-generated method stub
		Log.d(DEBUG_TAG+"/callValidation xml", "call validation XML....");
		
		HttpJson httpJ = new HttpJson();
		JSONArray result = httpJ.getJSONFromUrl("http://atlasmuseum.irisa.fr/scripts/validcontrib.php");
		if (result != null && result.length() > 0)
		{ //si c'est bien envoy�
			Log.d(DEBUG_TAG+"/callValidation xml", "validation: ok");
		}
		else
		{
			Log.d(DEBUG_TAG+"/callValidation xml", "validation: nok");
		}
	}

	

}
