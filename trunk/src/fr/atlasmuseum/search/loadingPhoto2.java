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


import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Classe qui recupere la photo d'une notice
 * Utilisé dans ListChampsNoticeModif
 * Permet de charger une photo qu'on a prise à l'aide du telephone, ou chargé une photo uploadée
 */
public class loadingPhoto2 extends AsyncTask<String, String, Boolean> {
	
	ImageView imgView;
	private loadPhotoInterface mContext;
	Boolean isphoto;//defini si c'est une photo prise par la camera du tel ou image upload
	String photoName;
	int idx;
	private ProgressDialog mProgress;//barre d'avancement
	private static final String DEBUG_TAG = "AtlasMuseum/chargementPhoto";
	boolean loadInternalPhoto;
	boolean loadWebPhoto;
	String error;
	private Bitmap bmSmall;
	
	public loadingPhoto2(loadPhotoInterface main, String photo, Boolean b) {
		Log.d(DEBUG_TAG, "lancement....... ");
		//this.imgView = img;
		mContext = main;
		this.photoName=photo;
		imgView= main.getImageView();
		this.isphoto=b;
		loadInternalPhoto=false;
		loadWebPhoto=false;
	}


	@Override
	protected void onPreExecute() {
		Log.d(DEBUG_TAG, "onPreExecute");
	    super.onPreExecute();

		mProgress = new ProgressDialog(mContext.getContext());
		mProgress.setMessage(mContext.getContext().getResources().getString(R.string.uploading));
		mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgress.setCancelable(false);
		mProgress.show();
		
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		// TODO Auto-generated method stub

		//ImageView imgView =(ImageView) mContext.findViewById(R.id.imageView1);
		bmSmall = null;
    	try {
    		//String fichierImage = SearchActivity.extractDataFromDb(idx,"image_principale");
    		String fichierImage = this.photoName;
    		Log.d(DEBUG_TAG, "error id ="+idx);
    		Log.d(DEBUG_TAG, "error photo ="+fichierImage);
    		if ("?".equals(fichierImage) == false){
    			File fimage ;
    			if(isphoto)
    			{
    				fimage = SearchActivity.checkIfPhotoFileExists(fichierImage) ;
    			}
    			else
    			{
    				fimage = SearchActivity.checkIfImageFileExists(fichierImage) ;
    			}
    			
    			if (fimage == null){
    				
    				error = mContext.getContext().getString(R.string.image_non_dispo)+" : " +fichierImage;
    				return false;
    			} else {
    				Log.d(DEBUG_TAG, "Image file exist");
    				bmSmall = BitmapFactory.decodeFile(fimage.getAbsolutePath());
    				this.loadInternalPhoto = true;
    				return true;
    			}
    		} else {
    			error = mContext.getContext().getString(R.string.notice_no_image);//"Cette notice n'a pas d'image";
    			//AtlasError.showErrorDialog(mContext, "5.3", "");// ERROR "Cette notice n'a pas d'image"
    			//Toast.makeText(mContext,"Cette notice n'a pas d'image", Toast.LENGTH_SHORT).show();
    			return false;
    			
    		}
		} catch (IOException e) 
    	{
			Log.d(DEBUG_TAG, "Could not view photo");
			error =mContext.getContext().getString(R.string.cannot_upl_image_notice);//"L'image de cette notice n'a pu être chargé";
			return false;
			
		}

	}
	
	public void chargerPhotoFromWeb(Bitmap bmSmall) throws IOException
	{
		 //mContext.imgView = new ImageView(mContext);
        mContext.getImageView().setImageBitmap(bmSmall);
		File thefile = SearchActivity.createImageFile(this.photoName);
		OutputStream fOut = null;
		fOut = new FileOutputStream(thefile);
		bmSmall.compress(Bitmap.CompressFormat.PNG, 90, fOut);
		fOut.close();
		
		loadThumbPhoto(this.photoName);
	}
	
	public void chargerPhotoFromInternalDB(Bitmap bmSmall)
	{

		if(isphoto)
		{
			int width = bmSmall.getWidth();
	        int height = bmSmall.getHeight();
	        Log.d(DEBUG_TAG, "photo size (H,W):"+height+"-"+width);
	        if (width >= height){ // deal with portrait versus landscape // check how size adapt to screen
	        	bmSmall = getResizedBitmap(bmSmall,200,300);
	        } else {
	        	bmSmall = getResizedBitmap(bmSmall,300,200);
	        }
		}
		/**ImageView i = mContext.getImageView();
		i.setImageBitmap(bmSmall);**/
		 mContext.getImageView().setImageBitmap(bmSmall);
		//mContext.getContext().getImageView().setImageBitmap(bmSmall);
	}
	
	
	public void loadThumbPhoto(String name)
	{
		try
		{
			String photoThumb="thumb_"+name;
			File fimage = SearchActivity.checkIfImageFileExists(photoThumb) ;
			if (fimage == null)
			{
				Bitmap bm = LoadImageFromWebOperations(mContext.getContext().getString(R.string.images_url)+photoThumb);
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
			if(loadInternalPhoto)
			{
				chargerPhotoFromInternalDB(bmSmall);
			}
			else if(loadWebPhoto)
			{
				
				try {
					chargerPhotoFromWeb(bmSmall);
				} catch (IOException e) {
					showImgParDefaut();
					Log.d(DEBUG_TAG, "Could not view photo");
					error = mContext.getContext().getString(R.string.cannot_upl_image_notice);//"L'image de cette notice n'a pu être chargé";
					Toast.makeText(mContext.getContext(),error, Toast.LENGTH_SHORT).show();
				}
			}
		}
		else {
			showImgParDefaut();
			Log.d(DEBUG_TAG, "echec loading photo");
			Toast.makeText(mContext.getContext(),error, Toast.LENGTH_SHORT).show();
		}
	}
public void showImgParDefaut()
{
	Drawable imgParDefaut = (Drawable)mContext.getContext().getApplicationContext().getResources().getDrawable(R.drawable.contrib_img_default);
	mContext.getImageView().setImageDrawable(imgParDefaut);
	
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
