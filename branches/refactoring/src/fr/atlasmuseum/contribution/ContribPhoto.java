package fr.atlasmuseum.contribution;

import java.io.File;
import java.io.IOException;
import java.util.List;

import fr.atlasmuseum.R;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Classe qui permet d'ajouter une photo pour une contribution
 *
 */
public class ContribPhoto extends Activity {
	
	private static final String DEBUG_TAG = "AtlasMuseum/ContribPhoto";
    static final String SHARED_PREFERENCES = "fr.atlasmuseum.ContribPhoto.SHARED_PREFERENCES";
	private static final int TAKE_PICTURE_REQUEST = 1;
	private static final String ATLASMUSEUM_ALBUM = "atlasmuseum";
	public static final String ATLASMUSEUM_IMAGE_SUFFIX = ".png";

	// Handle to SharedPreferences for this app
    private SharedPreferences mPrefs;

    // Handle to a SharedPreferences editor
    private SharedPreferences.Editor mPrefEditor;

    ImageView mImagePreview;//zone d'affichage de la photo
	Bundle mBundle;
	
	String mPhotoPath; //contient le chemin complet vers la photo prise si existante
	String mLastPhotoValid;//derniere photo valide, dans le cas ou la prise de photo est annulée
	
	private TextView mTextStatus;

	String mChamps;
	public void onCreate(Bundle savedInstanceState)
    {
 		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.contrib_take_picture);
        mImagePreview = (ImageView) findViewById(R.id.image_previewE);
		
	    // Open Shared Preferences
        mPrefs = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        // Get an editor
        mPrefEditor = mPrefs.edit();


        mBundle = getIntent().getExtras(); //recupere le bundle
        mChamps = mBundle.getString(ListChampsNoticeModif.CHAMPS_ITEM); //pour savoir le champs qu'on va modifier
        
        if(mChamps.equals(ListChampsNoticeModif.ajout_photo))
        {
        	this.mPhotoPath = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_photo, "");
        	Log.d(DEBUG_TAG, "ajout ++++++++++++");
        }
        else if(mChamps.equals(ListChampsNoticeModif.modif_photo))
        {
        	this.mPhotoPath = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_photo, "");
        	Log.d(DEBUG_TAG, "modif_photo ++++++++++++");
        }
        else
        {
        	mChamps ="unknow";
        	Log.d(DEBUG_TAG, "unknow ++++++++++++");
        }

        if( (mPhotoPath == null || mPhotoPath == "") && mPrefs.contains("photoPath") ) {
        	mPhotoPath = mPrefs.getString("photoPath", "");
			Log.d(DEBUG_TAG, "onCreate: Restore photoPath with " + mPhotoPath);
		}
		Log.d(DEBUG_TAG, "onCreate: photoPath = " + mPhotoPath);
		
        if( (mLastPhotoValid == null || mLastPhotoValid == "") && mPrefs.contains("lastPhotoValid") ) {
			mLastPhotoValid = mPrefs.getString("lastPhotoValid", "");
			Log.d(DEBUG_TAG, "onCreate: Restore lastPhotoValid with " + mLastPhotoValid);
		}		
		Log.d(DEBUG_TAG, "onCreate: lastPhotoValid = " + mLastPhotoValid);
		
    	Button mButtonTake = (Button) findViewById(R.id.mButtonTake);
    	mButtonTake.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	Log.i(DEBUG_TAG, "Taking a picture");
	        	takePicture();
	        }
	    });

		Button mButtonOk = (Button) findViewById(R.id.mButtonOk);
		mButtonOk.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	if(mPhotoPath != null || !mPhotoPath.equals(""))
	        	{
	        		boolean f =ListChampsNoticeModif.cPref.edit().putString(mChamps, mPhotoPath).commit();
	        		Log.d(DEBUG_TAG, "save photo ="+f+" "+mChamps);
	        	}
	        	setResult(RESULT_OK);
	        	Intent intent = new Intent(getApplication(), ListChampsNoticeModif.class);
	        	intent.putExtras(mBundle);
	  			startActivity(intent);
	  			finish();
	        }
	    });
		
		 
        
	       //pour autoriser le retour en cliquant sur l'icone de l'application dans l'action bar
	 		////////////////////////////////////////////////////////////////////////////////////
	 		////////////////////////////////////////////////////////////////////////////////////
	 		////////////////////////////////////////////////////////////////////////////////////
	 		//ACTION BAR
	 		ActionBar actionBar = getActionBar();
	 		if (actionBar != null)
	 		{
	 			actionBar.show();
	 			actionBar.setDisplayHomeAsUpEnabled(true);
	 			actionBar.setTitle(this.getResources().getString(R.string.Contribuer));
	 			actionBar.setDisplayShowTitleEnabled(true);
	 			//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
	 				}
	 		
	 		
    }
	
	@Override
	protected void onDestroy() {
		Log.d(DEBUG_TAG, "onDestroy");
		super.onDestroy();

		if( mPhotoPath != "" ) {
			mPrefEditor.putString("photoPath", mPhotoPath);
			Log.d(DEBUG_TAG, "onDestroy(): Save photoPath = " + mPhotoPath);
		}

		if( mLastPhotoValid != "" ) {
			mPrefEditor.putString("lastPhotoValid", mLastPhotoValid);
			Log.d(DEBUG_TAG, "onDestroy(): Save lastPhotoValid = " + mLastPhotoValid);
		}

		mPrefEditor.commit();

	}	

	public void afficheAlerte(String ch)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(ch);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
 		public void onClick(DialogInterface dialog, int id) {
 			
 		}
 	});
    builder.create().show();
	}
	

	private void takePicture() {
	    if (! isIntentAvailable(this, MediaStore.ACTION_IMAGE_CAPTURE)) {
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setMessage(R.string.camera_unavailable);
	        builder.setIcon(android.R.drawable.ic_dialog_alert);
	        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int id) {
	        		}
	        	});
	        builder.create().show();
	    	return;
	    }

		/*
		 * Create graffity and request location update,
		 * so graffity location will be updated on location update.
		 */
		
		File f = null;
		try {
			f = createImageFile();
		}
		catch(IOException e) {
			e.printStackTrace();
			mTextStatus.setText("Error: unable to create file to store graffity image.");
			return;
		}
		String dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
		mPhotoPath= dcimDir + "/" + ATLASMUSEUM_ALBUM + "/" + f.getName();
		Log.d(DEBUG_TAG, "path to file = " + mPhotoPath);
		
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		//takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(photoPath)));
		startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST);
	}
	
	// to check if Intent is available
		private static boolean isIntentAvailable(Context context, String action) {
			final PackageManager packageManager = context.getPackageManager();
			final Intent intent = new Intent(action);
			List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
			return list.size() > 0;
		 }
	
		private File createImageFile() throws IOException {
			// Create an image file name
			
			String m = String.valueOf(System.currentTimeMillis());
			String imageFileName = MainContribActivity.IDUtil+"_"+m;
			Log.d(DEBUG_TAG+"/creation img", "Image name =  " + imageFileName);
			File albumF = getAlbumDir();
			File imageF = File.createTempFile(imageFileName, ATLASMUSEUM_IMAGE_SUFFIX, albumF);
			return imageF;
		}
		
		private File getAlbumDir() {
			File storageDir = null;

			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				String dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
				storageDir = new File( dcimDir + "/" + ATLASMUSEUM_ALBUM );
				if (storageDir != null) {
					if (! storageDir.mkdirs()) {
						if (! storageDir.exists()){
							Log.d(DEBUG_TAG, "failed to create directory");
							return null;
						}
					}
				}
			} else {
				Log.v(DEBUG_TAG, "External storage is not mounted READ/WRITE.");
			}
			
			return storageDir;
		}
		
		@Override
		protected void onResume() {
			Log.d(DEBUG_TAG, "onResume");
			super.onResume();
			
			updatePreview();
		}

		private void updatePreview() { 
			Log.d(DEBUG_TAG, "updatePreview()");

			/* There isn't enough memory to open up more than a couple camera photos */
			/* So pre-scale the target bitmap into which the file is decoded */

			/* Get the size of the image */
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = true;
			if(mPhotoPath == null  || mPhotoPath.equals(""))
			{
				if(mLastPhotoValid != null && !mLastPhotoValid.equals(""))//si derniere photo valide pas vide
				{
					mPhotoPath = mLastPhotoValid;
				}
				else
				{
					mPhotoPath= "";
				}
				
			}
			Log.d(DEBUG_TAG, "updatePreview(): update using " + mPhotoPath);

			if( (new File(mPhotoPath).exists() )) 
			{
				BitmapFactory.decodeFile(mPhotoPath, bmOptions);
			}
			
			if( ! (new File(mPhotoPath).exists() || bmOptions.outHeight == -1 ))
			{
				mImagePreview.setImageDrawable(null);
				//mImagePreview.setBackgroundResource(android.R.color.darker_gray);
				//mImagePreview.setAlpha(0.7f);
				// ******* mImagePreview.setBackgroundResource(R.color.preview_background_transparent);

				return;
			}
			
			// Get rotation
			int rotation = 0;
			try {
				ExifInterface exif = new ExifInterface(mPhotoPath);
				int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
				switch( orientation ) {
				case ExifInterface.ORIENTATION_NORMAL:
					rotation = 0;
					break;
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotation = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotation = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotation = 270;
					break;
				case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
				case ExifInterface.ORIENTATION_FLIP_VERTICAL:
				case ExifInterface.ORIENTATION_TRANSPOSE:
				case ExifInterface.ORIENTATION_TRANSVERSE:
				case ExifInterface.ORIENTATION_UNDEFINED:
					rotation = -1;
				}
			} catch (IOException e) {
				rotation = -1;
			}

			int photoW = bmOptions.outWidth;
			int photoH = bmOptions.outHeight;
			
			Log.d(DEBUG_TAG, "Bitmap size = " + photoH + "x" + photoW + ")");

			
			DisplayMetrics displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int targetH = displaymetrics.heightPixels;
			int targetW = displaymetrics.widthPixels;

			Log.d(DEBUG_TAG, "Screen size = " + targetH + "x" + targetW + " (rot = " + getWindowManager().getDefaultDisplay().getRotation() + ")" );

			// Reduce the target size according to the layout
			switch( getWindowManager().getDefaultDisplay().getRotation() ) {
			case Surface.ROTATION_0:
			case Surface.ROTATION_180:
				targetH /= 2;
				break;
			case Surface.ROTATION_90:
			case Surface.ROTATION_270:
				targetW /=2;
			}
			
			
			/* Figure out which way needs to be reduced less */
			int scaleFactor = 1;
			if ((targetW > 0) || (targetH > 0)) {
				scaleFactor = Math.max((int)Math.ceil((double)photoW/targetW), (int)Math.ceil((double)photoH/targetH));
			}
			
			/* Set bitmap options to scale the image decode target */
			bmOptions.inJustDecodeBounds = false;
			bmOptions.inSampleSize = scaleFactor;
			bmOptions.inPurgeable = true;

			/* Decode the JPEG file into a Bitmap */
			Bitmap bitmap = BitmapFactory.decodeFile(this.mPhotoPath, bmOptions);
			
			if(bitmap == null)
			{
				return;
			}
			/* Associate the Bitmap to the ImageView */
			mImagePreview.setBackgroundResource(android.R.color.transparent);
			//mImagePreview.setAlpha(1.0f);

			// If the image is rotated, we need to scale the image precisely
			// because the ImageView scaling seems to be applied before rotation...
			photoH = bitmap.getHeight();
			photoW = bitmap.getWidth();

			// calculate the scale
			float scale = Math.max(((float) targetH) / photoH, ((float) targetW) / photoW);

			// create a matrix for the manipulation
			Matrix matrix = new Matrix();
			// resize the bit map
			matrix.postScale(scale, scale);
			// rotate the Bitmap
			matrix.postRotate(rotation);

			// recreate the new Bitmap
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, photoW, photoH, matrix, true);

			mImagePreview.setImageBitmap(resizedBitmap);

	     	 
		}
		

	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    	switch (requestCode) {
	    	case TAKE_PICTURE_REQUEST:
	    		if (resultCode == RESULT_OK) 
	    		{
	    			Log.d(DEBUG_TAG, "on activityResult OK");
	    			mLastPhotoValid = mPhotoPath;
	    			Toast.makeText(this, this.getResources().getString(R.string.photo_save_)+" " +this.mPhotoPath, Toast.LENGTH_SHORT).show();
	    			updatePreview();
	    		}
	    		else
	    		{
	    			//deleteFile(photoPath); probleme de suppression
	    			this.mPhotoPath="";
	    			updatePreview();
	    			Log.d(DEBUG_TAG, "on activityResult canceled");
	    		}

				break;

	    	
	    	}
	    }

		@Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        int itemId = item.getItemId();
			
			if(itemId == android.R.id.home)
			{
				setResult(RESULT_CANCELED, null);//pour fermer l'activite precedente
				Intent intent = new Intent(this, ListChampsNoticeModif.class);
		    	intent.putExtras(mBundle);
		        startActivity(intent);
				finish();
				return true;
			}
	    	
		
			else return false;
			    
	    }
		
		@Override
		public void onBackPressed() {
			setResult(RESULT_CANCELED, null);//pour fermer l'activite precedente
			Intent intent = new Intent(this, ListChampsNoticeModif.class);
	    	intent.putExtras(mBundle);
	        startActivity(intent);
			finish();
		}
		
}
