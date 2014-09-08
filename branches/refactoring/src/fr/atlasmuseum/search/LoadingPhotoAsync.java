package fr.atlasmuseum.search;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Classe qui recupere la photo d'une notice
 * puis le place dans l'imageView
 * Utilisé dans ShowNoticeActivity
 * @author Expert
 *
 */
public class LoadingPhotoAsync extends AsyncTask<String, String, Boolean> {
	
	ImageView imgView;
	private ShowNoticeActivity mContext;
	int idx;
	private ProgressDialog mProgress;//barre d'avancement
	private static final String DEBUG_TAG = "AtlasMuseum/chargementPhoto";
	
	String error;
	
	LoadingPhotoAsync(ShowNoticeActivity main, int idx) {
		Log.d(DEBUG_TAG,"lancement");
		//this.imgView = mContext.getImageVew();
		mContext = main;
		this.idx=idx;
	}


	@Override
	protected void onPreExecute() {
		Log.d(DEBUG_TAG, "onPreExecute");
	    super.onPreExecute();

		mProgress = new ProgressDialog(mContext);
		mProgress.setMessage(mContext.getResources().getString(R.string.uploading));
		mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgress.setCancelable(false);
		mProgress.show();
		
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		// TODO Auto-generated method stub

		//ImageView imgView =(ImageView) mContext.findViewById(R.id.imageView1);
    	Bitmap bmSmall = null;
    	try {
    		String fichierImage = SearchActivity.extractDataFromDb(idx,"image_principale");
    		if ("?".equals(fichierImage) == false){
    			File fimage = SearchActivity.checkIfImageFileExists(fichierImage) ;
    			if (fimage == null){
    				Log.d(DEBUG_TAG, "Downloading image file");
    				Bitmap bm = LoadImageFromWebOperations("http://atlasmuseum.irisa.fr/images/"+fichierImage);
    				if (bm != null) {
    					int width = bm.getWidth();
    			        int height = bm.getHeight();
    			        Log.d(DEBUG_TAG, "photo size (H,W):"+height+"-"+width);
    			        if (width >= height){ // deal with portrait versus landscape // check how size adapt to screen
    			        	bmSmall = getResizedBitmap(bm,200,300);
    			        } else {
    			        	bmSmall = getResizedBitmap(bm,300,200);
    			        }
    			        mContext.imgView = new ImageView(mContext);
    			        mContext.imgView.setImageBitmap(bmSmall);
    					File thefile = SearchActivity.createImageFile(fichierImage);
    					OutputStream fOut = null;
    					fOut = new FileOutputStream(thefile);
    					bmSmall.compress(Bitmap.CompressFormat.PNG, 90, fOut);
    					fOut.close();
    					
    					loadThumbPhoto(fichierImage);
    					
    					return true;
    				} else {
    					error =mContext.getString(R.string.prob_charg_image)+" "+fichierImage;
    					Log.d(DEBUG_TAG, "Problme de chargement de l'image : "+fichierImage);
    					return false;
    				}
    			} else {
    				Log.d(DEBUG_TAG, "Image file exist");
    				mContext.imgView = new ImageView(mContext);
    				bmSmall = BitmapFactory.decodeFile(fimage.getAbsolutePath());
    				imgView.setImageBitmap(bmSmall);
    				return true;
    			}
    		} else {
    			error = mContext.getString(R.string.notice_no_image);//"Cette notice n'a pas d'image";
    			//AtlasError.showErrorDialog(mContext, "5.3", "");// ERROR "Cette notice n'a pas d'image"
    			//Toast.makeText(mContext,"Cette notice n'a pas d'image", Toast.LENGTH_SHORT).show();
    			return false;
    			
    		}
		} catch (IOException e) 
    	{
			Log.d(DEBUG_TAG, "Could not view photo");
			error =mContext.getString(R.string.cannot_upl_image_notice);//"L'image de cette notice n'a pu être chargé";
			return false;
			
		}

	}
	
	public void loadThumbPhoto(String name)
	{
		try
		{
			String photoThumb="thumb_"+name;
			File fimage = SearchActivity.checkIfImageFileExists(photoThumb) ;
			if (fimage == null)
			{
				Bitmap bm = LoadImageFromWebOperations("http://atlasmuseum.irisa.fr/images/"+photoThumb);
				if (bm != null)
				{
					
					File thefile = SearchActivity.createImageFile(photoThumb);
					OutputStream fOut = null;
					fOut = new FileOutputStream(thefile);
					bm.compress(Bitmap.CompressFormat.PNG, 90, fOut);
					fOut.close();
				}
				else
				{
					Log.d(DEBUG_TAG, "Problème de chargement de l'image : "+photoThumb);
					
				}
			}
		}
		catch(Exception e){}
		
		
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
			Log.d(DEBUG_TAG, "loading photo success");
			mContext.recreate();
		}
		else {
			Log.d(DEBUG_TAG, "echec loading photo");
			Toast.makeText(mContext,error, Toast.LENGTH_SHORT).show();
		}
	}


	 private Bitmap LoadImageFromWebOperations(String urls) {
	    	try {
	    		URL url = new URL(urls);
	    		URLConnection urlConnection = url.openConnection();
	    		urlConnection.setConnectTimeout(3000);
	    		Bitmap bmp = BitmapFactory.decodeStream(urlConnection.getInputStream());
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
