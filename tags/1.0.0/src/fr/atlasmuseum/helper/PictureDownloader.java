package fr.atlasmuseum.helper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import fr.atlasmuseum.R;
import fr.atlasmuseum.contribution.Contribution;

/**
 * Background Async Task to download file
 * */
public class PictureDownloader extends AsyncTask<String, String, String> {

    public interface PictureDownloaderListener {
        public void onPictureDownloaded(String filename);
    }

	@SuppressWarnings("unused")
	private static final String DEBUG_TAG = "AtlasMuseum/Contribution.PictureDownloader";

	private Context mContext;
	private PictureDownloaderListener mListener;
	private ProgressDialog mProgress;
	
	public PictureDownloader(Context context) {
		mContext = context;
		
        try {
            // Instantiate the ContributionModificationListener so we can send events to the host
        	mListener = (PictureDownloaderListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + " must implement PictureDownloaderListener");
        }

	}
	
    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
		mProgress = new ProgressDialog(mContext);
		mProgress.setMessage(mContext.getResources().getString(R.string.contribution_uploading));
		mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgress.setCancelable(true);
		mProgress.setIndeterminate(false);
        mProgress.setMax(100);
        mProgress.show();
    }

    /**
     * Downloading file in background thread
     * */
    @Override
    protected String doInBackground(String... args) {
        int count;
        try {
        	URL url = new URL(mContext.getString(R.string.contribution_url_images) + URLEncoder.encode(args[0], "UTF-8"));
        	File outputFile = new File(Contribution.getPhotoDir(), args[0]);
    	    
            URLConnection connection = url.openConnection();
            connection.connect();
            // getting file length
            int lenghtOfFile = connection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file
            OutputStream output = new FileOutputStream(outputFile.getAbsoluteFile());

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress(""+(int)((total*100)/lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

            return outputFile.getAbsolutePath();
        }
        catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
    }

    /**
     * Updating progress bar
     * */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        mProgress.setProgress(Integer.parseInt(progress[0]));
   }

    /**
     * After completing background task
     * Dismiss the progress dialog
     * **/
    @Override
    protected void onPostExecute(String filename) {
    	// Notify parent activity that the property has been modified
    	mListener.onPictureDownloaded(filename);
    	
    	mProgress.hide();
		mProgress.cancel();
    }

}