package fr.atlasmuseum.contribution;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




import fr.atlasmuseum.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Classe qui permet l'envoi de toutes les photos sauvegard�es non envoy�es
 * @author Expert
 *
 */
public class EnvoiPhoto extends AsyncTask<String, String, Boolean> {
	
	String mStatus;
	private boolean continuerAEnvoyer;
	private final Context mContext;
	final String urlSend ="";
	//int nbc=1;
	private ProgressDialog mProgress;//barre d'avancement
	private List<Contribution> listContribPhoto;
	private static final String DEBUG_TAG = "AtlasMuseum/envoiPhoto";
	public String chaineXml;
	public Boolean erasefile;
	EnvoiPhoto(Context act, List<Contribution> setImage, String string, Boolean erasefile) 
	{
		listContribPhoto = setImage;
		mContext = act;
		mStatus = "";
		this.chaineXml = string;
		this.erasefile=erasefile;
	}
	
	



	@Override
	protected void onPreExecute() {
		Log.d(DEBUG_TAG, "onPreExecute");
	    super.onPreExecute();
	    continuerAEnvoyer = true; //permet de determiner si on peut ou pas envoyer
		mProgress = new ProgressDialog(mContext);
		mProgress.setMessage(mContext.getResources().getString(R.string.send_photo_saved));
		mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgress.setCancelable(false);
		mProgress.show();
		
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		Log.d(DEBUG_TAG+"/sendPhoto", "******** ENVOI PHOTO *******");
		//boolean continuerAEnvoyer
		String mStatus;
		ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
		for(int i=0;i<listContribPhoto.size() && this.continuerAEnvoyer ;i++)
		{
			Contribution currentImg = listContribPhoto.get(i);
			nvps.add(new BasicNameValuePair("arg_photo", currentImg.photoToString()));
			nvps.add(new BasicNameValuePair("arg_name", currentImg.photoPath));
			HttpJson httpJ = new HttpJson();
			
			JSONArray result = httpJ.getJSONFromUrl("http://atlasmuseum.irisa.fr/scripts/storeContribImage.php", nvps);
			if (result != null && result.length() > 0)
			{ //si c'est bien envoy�
				JSONObject json_data;
				try
				{
					json_data = result.getJSONObject(0);
					String server_status = json_data.getString("commandstatus");
					Log.i(DEBUG_TAG+"/photo", "Feedback from server: " + server_status);
					if (! server_status.equals("ok")) 
					{
						mStatus = "upload failed"+server_status;
						Log.w(DEBUG_TAG, mStatus);
						Log.w(DEBUG_TAG, "photo"+currentImg.photoPath+" mal envoy�e");
						this.continuerAEnvoyer= false;
					}
				else
					{//feedback s'est bien pass�
						mStatus = "upload success";
						Log.w(DEBUG_TAG+"/photo", mStatus);
						Log.w(DEBUG_TAG, "photo"+currentImg.photoPath+" bien envoy�e");
						this.continuerAEnvoyer= true;
					}
				
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.w(DEBUG_TAG+"/photo"+" failed", "JSONException");
					//e.printStackTrace();
					this.continuerAEnvoyer=false;
				}
			}
		}
		
		return this.continuerAEnvoyer;
	}
		
	@Override
	protected void onProgressUpdate(String... progress) {
	}

	@Override
	protected void onPostExecute(Boolean result) {     
		Log.i(DEBUG_TAG, "onPostExecute");
		
		//ENVOIE du xml 
		
		//Log.d(DEBUG_TAG, "contenu XML avant envoi ="+this.chaineXml);
    	EnvoiXml upl = new EnvoiXml(mContext,chaineXml, erasefile);
     	upl.execute();
     	
		if( mProgress != null ) {
			mProgress.hide();
			mProgress.cancel();
		}
		if( result ) {
			Log.d(DEBUG_TAG, "envoi effectuer avec succes");
			//mContext.deleteFile(ContribXml.nomFichier);
			//mContext.recreate();
		}
		else {
			Log.d(DEBUG_TAG, "ECHEC DE l'ENVOI");
			
		}
	}

	

}
