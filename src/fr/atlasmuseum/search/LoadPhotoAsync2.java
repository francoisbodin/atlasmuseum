package fr.atlasmuseum.search;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
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
import fr.atlasmuseum.main.AtlasError;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Classe qui recupere les photo d'une liste de photo
 * 
 *
 */
public class LoadPhotoAsync2 extends AsyncTask<String, String, Boolean> {
	
	ImageView imgView;
	ArrayList<String> listPhoto;//list des noms de photos à charger
	private ProgressDialog mProgress;//barre d'avancement
	private static final String DEBUG_TAG = "AtlasMuseum/LoadingThumbPhoto";
	
	String error;
	private loadPhotoInterface activity;
	
	LoadPhotoAsync2(loadPhotoInterface mcontext, ArrayList<String> listPhoto) {
		Log.d(DEBUG_TAG, "lancement....... ");
		//this.imgView = img;
		this.activity=mcontext;
		this.listPhoto=listPhoto;
	}


	@Override
	protected void onPreExecute() {
		Log.d(DEBUG_TAG, "onPreExecute");
	    super.onPreExecute();

		mProgress = new ProgressDialog(activity.getContext());
		mProgress.setMessage("uploading...");
		mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgress.setCancelable(false);
		mProgress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	cancel(true);
		    }
		});
		
		 
		mProgress.show();
		
		
	}

	
	 @Override
     protected void onProgressUpdate(String... values) {
         super.onProgressUpdate(values);
         String intro="Chargement des 50 thumb des oeuvres les plus proches\n";
         String info ="\nOK:thumb téléchargé \n FAILED: problème de chargement";
         mProgress.setMessage(values[0].toString()+info);  

     }
	@Override
	protected Boolean doInBackground(String... params) {
		// TODO Auto-generated method stub
		Log.d(DEBUG_TAG, "doinbackground ...... ");
		for(int i=0; i<listPhoto.size() && !isCancelled();i++)
		{
			Log.d(DEBUG_TAG, "isCancelled()= "+isCancelled());
		
	    	//Bitmap bmSmall = null;
	    	try {
	    		String fichierImage = listPhoto.get(i);
	    		
	    		//publishProgress("uploading thumb "+i+"/"+listPhoto.size());//sert a afficher l'avancement dans la vue
	    		
	    		Log.d(DEBUG_TAG+"/info", "loading ..."+i+"/"+listPhoto.size());
	    		if ("?".equals(fichierImage) == false)
	    		{
	    			
	    			File fimage = SearchActivity.checkIfImageFileExists(fichierImage) ;
	    			if (fimage == null)
	    			{
	    				Bitmap bm = LoadImageFromWebOperations("http://atlasmuseum.irisa.fr/images/"+fichierImage);
	    				if (bm != null)
	    				{
	    					
	    					File thefile = SearchActivity.createImageFile(fichierImage);
	    					OutputStream fOut = null;
	    					fOut = new FileOutputStream(thefile);
	    					bm.compress(Bitmap.CompressFormat.PNG, 90, fOut);
	    					fOut.close();
	    					publishProgress("thumb "+i+"/"+listPhoto.size()+"..OK");
	    				}
	    				else
	    				{
	    					publishProgress("thumb "+i+"/"+listPhoto.size()+"..FAILED");
	    					Log.d(DEBUG_TAG, "Problème de chargement de l'image : "+fichierImage);
	    					
	    				}
	    			}
	    			else 
	    			{
	    				publishProgress("thumb "+i+"/"+listPhoto.size()+"..OK");
	    				Log.d(DEBUG_TAG, "*****Image file exist*****");
	    			}
	    		}
	    		else
	    		{
	    			Log.d(DEBUG_TAG, "image n'existe pas");
	    			publishProgress("thumb "+i+"/"+listPhoto.size()+"..FAILED");
	    		}
			} catch (IOException e) {
				Log.d(DEBUG_TAG, "Could not view photo");
				publishProgress("thumb "+i+"/"+listPhoto.size()+"..FAILED");
			}
		}
		return true;

	}
	
	@Override
	protected void onCancelled(Boolean result) {
		if( mProgress != null ) {
			mProgress.hide();
			mProgress.cancel();
		}
		activity.getNoticeAdapter().notifyDataSetChanged();
		super.onCancelled(result);
	};
		


	@Override
	protected void onPostExecute(Boolean result) {     
		Log.i(DEBUG_TAG, "onPostExecute");
		
		//callValidationXml();
		if( mProgress != null ) {
			mProgress.hide();
			mProgress.cancel();
		}
		if( result ) {
			Log.d(DEBUG_TAG, "loading photo success");
			//mContext.recreate();

			activity.getNoticeAdapter().notifyDataSetChanged();
			
		}
		else {
			Log.d(DEBUG_TAG, "echec loading photo");
			Toast.makeText(activity.getContext(),error, Toast.LENGTH_SHORT).show();
		}
	}


	 private Bitmap LoadImageFromWebOperations(String urls) {
	    	try {
	    		URL url = new URL(urls);
	    		Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
	    		return bmp;
	    	}catch (Exception e) {
	    		Log.d(DEBUG_TAG,"Exc="+e);
	    		return null;
	    	}
	    }
	    
	    public static Bitmap getResizedBitmap(Bitmap image, int newHeight, int newWidth) {
	        int width = image.getWidth();
	        int height = image.getHeight();
	        float scaleWidth = ((float) newWidth) / width;
	        float scaleHeight = ((float) newHeight) / height;
	        // create a matrix for the manipulation
	        Matrix matrix = new Matrix();
	        // resize the bit map
	        matrix.postScale(scaleWidth, scaleHeight);
	        // recreate the new Bitmap
	        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height,
	                matrix, false);
	        return resizedBitmap;
	    }
	    
	

}
