package fr.atlasmuseum.contribution;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.atlasmuseum.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class ContributionSend extends AsyncTask<String, String, Boolean> {

    public interface ContributionSendListener {
        public void onContributionSent(Boolean success);
    }

	private static final String DEBUG_TAG = "AtlasMuseum/ContributionSend";
	
	private Context mContext;
	private ContributionSendListener mListener;
	private Contribution mContribution;
	private ProgressDialog mProgress;
	
	
	public ContributionSend( Context context, Contribution contribution ) {
		mContext = context;
		mContribution = contribution;
		
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ContributionSendListener so we can send events to the host
            mListener = (ContributionSendListener) mContext;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(mContext.toString() + " must implement ContributionSendListener");
        }
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
		ArrayList<NameValuePair> nvps;
		
		// Send picture if new
		nvps = new ArrayList<NameValuePair>();
		ContributionProperty prop = mContribution.getProperty(Contribution.PHOTO);
		if( prop.isModified() && ! prop.getValue().equals("")) {
			onProgressUpdate(mContext.getResources().getString(R.string.send_photo_saved ));
			String path = prop.getValue();
			File file = new File(path);
			InputStream is;
			try {
				is = new FileInputStream(file);
			}
			catch (FileNotFoundException e) {
				Log.i(DEBUG_TAG, "Can't read " + path);
				e.printStackTrace();
				return false;
			}
			ByteArrayOutputStream os = new ByteArrayOutputStream(1000);
			Bitmap bitmap = BitmapFactory.decodeStream(is, null, null);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, os);  
			byte[] bytearray = os.toByteArray();
			String string = Base64.encodeToString(bytearray, Base64.DEFAULT);
			
			nvps.add(new BasicNameValuePair("arg_name", file.getName()));
			nvps.add(new BasicNameValuePair("arg_photo", string));
			
			HttpJson httpJ = new HttpJson();
			
			JSONArray result = httpJ.getJSONFromUrl("http://atlasmuseum.irisa.fr/scripts/storeContribImage.php", nvps);
			if (result != null && result.length() > 0) {
				JSONObject json_data;
				try {
					json_data = result.getJSONObject(0);
					String server_status = json_data.getString("commandstatus");
					Log.i(DEBUG_TAG, "doInBackground(): Feedback from server: " + server_status);
					if (! server_status.equals("ok")) {
						Log.w(DEBUG_TAG, "doInBackground(): Photo " + file.getAbsolutePath() + " mal envoyée");
						return false;
					}
					else {
						Log.d(DEBUG_TAG, "doInBackground(): Photo " + file.getAbsolutePath() + " bien envoyée");
					}				
				}
				catch (JSONException e) {
					Log.w(DEBUG_TAG, "doInBackground(): JSONException");
					e.printStackTrace();
					return false;
				}
			}
		}
		
		// Send contributions
		onProgressUpdate(mContext.getResources().getString(R.string.sending_contrib_file));
		nvps = new ArrayList<NameValuePair>();
		
		Element root = new Element("xml");
		Document document = new Document(root);
		mContribution.addToXML(root);
		
		XMLOutputter xmlOutputter = new XMLOutputter(); 
		String xmlString = xmlOutputter.outputString(document);

		nvps.add(new BasicNameValuePair("argxml", xmlString));
		
		HttpJson httpJ = new HttpJson();
		
		JSONArray result = httpJ.getJSONFromUrl("http://atlasmuseum.irisa.fr/scripts/receiveContributionFile.php", nvps);
		if (result != null && result.length() > 0) {
			JSONObject json_data;
			try	{
				json_data = result.getJSONObject(0);
				String server_status = json_data.getString("commandstatus");
				
				Log.i(DEBUG_TAG, "doInBackground(): Feedback from server: " + server_status);
				if (! server_status.equals("ok")) {
					Log.w(DEBUG_TAG, "doInBackground(): Contribution mal envoyée");
					return false;
				}
				else {
					Log.d(DEBUG_TAG, "doInBackground(): Contribution bien envoyée");
				}
			
			} catch (JSONException e) {
				Log.w(DEBUG_TAG, "doInBackground(): JSONException");
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}

	@Override
	protected void onProgressUpdate(String... progress) {
		mProgress.setMessage(mContext.getResources().getString(R.string.sending_contrib_file));
	}

	@Override
	protected void onPostExecute(Boolean result) {     
		Log.i(DEBUG_TAG, "onPostExecute()");
		
		if( mProgress != null ) {
			mProgress.hide();
			mProgress.cancel();
		}
		
		if( result ) {
			Log.d(DEBUG_TAG, "onPostExecute(): Contribution sent successfully");
		}
		else {
			Log.d(DEBUG_TAG, "onPostExecute(): Failed to send contribution");
		}
		
		mListener.onContributionSent(result);
	}

}
