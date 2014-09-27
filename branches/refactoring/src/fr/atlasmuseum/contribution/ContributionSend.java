package fr.atlasmuseum.contribution;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.json.JSONObject;

import fr.atlasmuseum.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ContributionSend extends AsyncTask<String, Integer, Boolean> {

    public interface ContributionSendListener {
        public void onContributionSent(Boolean success, String status);
    }

	private static final String DEBUG_TAG = "AtlasMuseum/ContributionSend";
	
	private Context mContext;
	private ContributionSendListener mListener;
	private Contribution mContribution;
	private ProgressDialog mProgress;
	private String mStatus;
	
	public ContributionSend( Context context, Contribution contribution ) {
		mContext = context;
		mContribution = contribution;
		mStatus = "";
		
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
		mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgress.setCancelable(false);
		mProgress.setMax(100);
		mProgress.show();
	}

	@Override
	protected Boolean doInBackground(String... params) {
		Element root = new Element("xml");
		Document document = new Document(root);
		mContribution.addToXML(root);
		
		XMLOutputter xmlOutputter = new XMLOutputter(); 
		String xmlString = xmlOutputter.outputString(document);

		// create the json data structure to be sent...
		Map<String,String> postValues = new HashMap<String,String>();
		postValues.put("argxml", xmlString);

		ContributionProperty prop = mContribution.getProperty(Contribution.PHOTO);
		String path = prop.getValue();
		File file = new File(path);
		if( prop.isModified() && ! prop.getValue().equals("")) {
			postValues.put("image_filename", file.getName());
		}
		
		try { // open a URL connection to the Servlet
		    String lineEnd = "\r\n";
		    String twoHyphens = "--";
		    String boundary = "xxxxxxxx";

            URL url = new URL("http://atlasmuseum.irisa.fr/atlas/scripts/receive_contribution.php");            
            
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            
            for (Map.Entry<String, String> e : postValues.entrySet()) {
            	dos.writeBytes(twoHyphens + boundary + lineEnd);
            	dos.writeBytes("Content-Disposition: form-data; name=\"" + e.getKey() + "\"" + lineEnd);
            	dos.writeBytes("Content-Type: text/plain; charset=" + lineEnd);
            	dos.writeBytes(lineEnd);
            	dos.writeBytes(e.getValue());
            	dos.writeBytes(lineEnd);
            	dos.flush();
            }
            
            if( prop.isModified() && ! prop.getValue().equals("")) {
    			FileInputStream fileInputStream = new FileInputStream(file);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
    			dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + file.getName() + "\"" + lineEnd);
    			dos.writeBytes(lineEnd);
    			int bytesAvailable = fileInputStream.available();
    			int bufferSize = (int) file.length()/200; // suppose you want to write file in 200 chunks
    			byte[] buffer = new byte[bufferSize];
    			int sentBytes = 0;
    			// read file and write it into form...
    			int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
    			while (bytesRead > 0) {
    				dos.write(buffer, 0, bufferSize);
    				// Update progress dialog
    				sentBytes += bufferSize;
    				publishProgress((int)((float)sentBytes * 100 / bytesAvailable));
    				//Log.d(DEBUG_TAG, "sentBytes = " + sentBytes + " / " + bytesAvailable);
    				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
    			}
    			// send multipart form data necessary after file data...
                fileInputStream.close();
    			dos.writeBytes(lineEnd);
    		}
    		
    		dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            
            // Responses from the server (code and message)
            int serverResponseCode = conn.getResponseCode();
            Log.d(DEBUG_TAG, "Server response code = "+ serverResponseCode);
            String serverResponseMessage = conn.getResponseMessage();
            Log.d(DEBUG_TAG, "Server response message = "+ serverResponseMessage);
            // close streams
            dos.flush();
            dos.close();
            
            if (serverResponseCode != HttpURLConnection.HTTP_OK) {
            	mStatus = serverResponseMessage;
            	return false;
            }
            
            InputStream responseStream = new BufferedInputStream(conn.getInputStream());

            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            String string_response = stringBuilder.toString();
            Log.d(DEBUG_TAG, "response = " + string_response);
            
            JSONObject json_response = new JSONObject(string_response);
            if( ! json_response.has("status") ) {
            	mStatus = "Bad response from server";
            	return false;
            }
            if( ! json_response.getString("status").equals("ok") ) {
            	mStatus = json_response.getString("status");
            	return false;
            }
        } catch (MalformedURLException e) {
        	mStatus = "MalformedURLException: " + e.getMessage();
            e.printStackTrace();
            return false;
        } catch (Exception e) {
        	mStatus = "Exception: " + e.getMessage();
            e.printStackTrace();
            return false;
        }

		
		return true;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		//Log.d(DEBUG_TAG, "onProgessUpdate(" + progress[0] + ")");
		mProgress.setProgress(progress[0]);
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
		
		mListener.onContributionSent(result, mStatus);
	}

}
