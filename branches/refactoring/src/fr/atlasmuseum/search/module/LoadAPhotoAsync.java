package fr.atlasmuseum.search.module;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import fr.atlasmuseum.R;
import fr.atlasmuseum.search.SearchActivity;
import fr.atlasmuseum.search.loadPhotoInterface;


/**
 * utiliser dans NoticeAdapterWithDistance pour charger une photo thumb
 *
 */
public class LoadAPhotoAsync extends AsyncTask<String, String, Boolean> {
	
	private loadPhotoInterface mContext;
	String photoName;
	int idx;
	ImageView img;
	private ProgressDialog mProgress;//barre d'avancement
	private String error;
	private static final String DEBUG_TAG = "AtlasMuseum/LoadAPhoto";
	
	
	public LoadAPhotoAsync(loadPhotoInterface context, String photo, ImageView img) {
		Log.d(DEBUG_TAG, "lancement....... ");
		//this.imgView = img;
		mContext = context;
		this.photoName=photo;
		this.img=img;
	}


	@Override
	protected void onPreExecute() {
		Log.d(DEBUG_TAG, "onPreExecute");
	    super.onPreExecute();

		mProgress = new ProgressDialog(mContext.getContext());
		mProgress.setMessage("uploading");
		mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgress.setCancelable(false);
		mProgress.show();
		
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		// TODO Auto-generated method stub
		File fimage;
		try {
			fimage = SearchActivity.checkIfImageFileExists(photoName);
			if (fimage == null)
			{
				Log.d(DEBUG_TAG, "loading photo from web...."+photoName);
				Bitmap bm = LoadImageFromWebOperations(mContext.getContext().getString(R.string.images_url)+photoName);
				if (bm != null)
				{
					
					File thefile = SearchActivity.createImageFile(photoName);
					OutputStream fOut = null;
					fOut = new FileOutputStream(thefile);
					bm.compress(Bitmap.CompressFormat.PNG, 90, fOut);
					fOut.close();
					return true;
				}
				else
				{
					Log.d(DEBUG_TAG, "Problème de chargement de l'image : "+photoName);
					error = mContext.getContext().getResources().getString(R.string.prob_charg_image)+" "+photoName;//"Problème de chargement de l'image : "
				}
			}
			else
			{
				error="image file exist"+photoName;
				return false;
			}
		} catch (IOException e) 
		{
			String er= mContext.getContext().getResources().getString(R.string.image_non_dispo);	
			error =er+" "+photoName;return false;//"file error"
		}
		String er= mContext.getContext().getResources().getString(R.string.echec_upl_image);
		error=er+" "+photoName;//"Echec du chargement de l'image: "
		return false;
	}
	

	
	
	 private Bitmap LoadImageFromWebOperations(String urls) {
	    	try {
	    		Log.d(DEBUG_TAG, "loading image file ....");
	    		URL url = new URL(urls);
	    		URLConnection urlConnection = url.openConnection();
	    		urlConnection.setConnectTimeout(3000);
	    		Bitmap bmp = BitmapFactory.decodeStream(urlConnection.getInputStream());
	    		return bmp;
	    	}catch (Exception e) {
	    		Log.d(DEBUG_TAG,"Exc="+e);
	    		//Toast.makeText(mContext,"erreur chargement..",Toast.LENGTH_SHORT).show();
	    		return null;
	    	}
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
			try //charge la photo si dispo
			{
				
				File imageR = SearchActivity.checkIfImageFileExists("thumb_"+this.photoName);
				if(imageR != null)
				{
					Bitmap bitmap = BitmapFactory.decodeFile(imageR.getAbsolutePath());
					img.setImageBitmap(bitmap);
					Log.d(DEBUG_TAG, "chargement réussi");
				}
				mContext.getNoticeAdapter().notifyDataSetChanged();
				
			}catch(Exception e){Toast.makeText(mContext.getContext(),"", Toast.LENGTH_SHORT).show();}
			//Toast.makeText(mContext.getContext(),"success", Toast.LENGTH_SHORT).show();
		}
		else {
			Log.d(DEBUG_TAG, "echec loading photo");
			Toast.makeText(mContext.getContext(),error, Toast.LENGTH_SHORT).show();
		}
	}
	    
	

}
