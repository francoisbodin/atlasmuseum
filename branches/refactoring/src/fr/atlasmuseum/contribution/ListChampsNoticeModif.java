package fr.atlasmuseum.contribution;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import fr.atlasmuseum.R;
import fr.atlasmuseum.compte.ConnexionActivity;
import fr.atlasmuseum.main.AtlasError;
import fr.atlasmuseum.main.MainActivity;

public class ListChampsNoticeModif extends Activity implements ContributionSend.ContributionSendListener {
	private static final String DEBUG_TAG = "AtlasMuseum/ListChampsNoticeModif";
	
	static final String SHARED_PREFERENCES = "fr.atlasmuseum.contribution.ListChampsNoticeModif.SHARED_PREFERENCES";

	static final int REQUEST_MODIFY_PROPERTY = 1;
	static final int REQUEST_TAKE_PICTURE = 2;
	static final int REQUEST_CONNECTION = 3;
	
	public static final int RESULT_SAVED = 1;
	public static final int RESULT_SENT = 2;
	
	private Bundle mBundle;
	private Contribution mContribution;
	private int mNoticeId;
	
	private List<ContributionProperty> mList;
	
	private RelativeLayout mLayoutLoadPicture;
	private ImageView mViewModifPicture;
	private RelativeLayout mLayoutModifPicture;

	public ContributionPropertyAdapter mAdapter;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.contrib_list_modif);

		Log.d(DEBUG_TAG, "onCreate()");
		
		mBundle = getIntent().getExtras();
		if( ! mBundle.containsKey("contribution") ) {
			// TODO: handle this case, even if it should never happen
		}
		Log.d(DEBUG_TAG, "onCreate(): Retrieve Contribution from bundle");
		mContribution = (Contribution) mBundle.getSerializable("contribution");
		mNoticeId = mContribution.getNoticeId();
		
		mLayoutModifPicture = (RelativeLayout) findViewById(R.id.modif_layout_picture);
		mViewModifPicture = (ImageView) findViewById(R.id.modif_view_picture);
		
		mViewModifPicture.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				String filename = mContribution.getProperty(Contribution.PHOTO).getValue();
		    	File file = new File(filename);
		    	if( ! file.exists() || ! file.isAbsolute() ) {
		    		return;
		    	}
		    	
		    	Intent intent = new Intent(Intent.ACTION_VIEW);
				final PackageManager packageManager = getPackageManager();
				List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
				if( list.size() == 0 ) {
					return;
				}

				intent.setDataAndType(Uri.fromFile(file), "image/*");
				startActivity(intent);
			}
		});

		mList = new ArrayList<ContributionProperty>();
		mAdapter = new ContributionPropertyAdapter(ListChampsNoticeModif.this, mList);
		ListView listView = (ListView) findViewById(R.id.list_view);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id)	{
				ContributionProperty prop = (ContributionProperty) mAdapter.getItem(position);
				String field = (String) prop.getDbField();
				
				if( field.equals(Contribution.LATITUDE) ||
				    field.equals(Contribution.LONGITUDE)) {
					// TODO: handle special case for location
					showLocationChangeAlertToUser();
				}
				else {
					Bundle bundle = new Bundle();
					bundle.putSerializable("position", position);
					bundle.putSerializable("property", prop);
					Intent intent = new Intent(ListChampsNoticeModif.this, ModifActivity.class);
					intent.putExtras(bundle);
					startActivityForResult(intent, REQUEST_MODIFY_PROPERTY);	
				}
			}
		});	


		ImageButton buttonModifPicture = (ImageButton) findViewById(R.id.modif_button_take_picture);
		buttonModifPicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				takePicture();
			}
		});

		mLayoutLoadPicture = (RelativeLayout) findViewById(R.id.modif_layout_load_picture);
		mLayoutLoadPicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				takePicture();
			}
		});

		Button buttonCancel = (Button) findViewById(R.id.button_cancel);
		buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finishWithWarning();
			}
		});

		Button buttonSave = (Button) findViewById(R.id.button_save);
		buttonSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveContributions();
			}
		});

		Button buttonSend = (Button) findViewById(R.id.button_send);
		buttonSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendContributions();
			}
		});

		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.show();
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(getResources().getString(R.string.Contribuer));
			actionBar.setDisplayShowTitleEnabled(true);
			//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
		}

		if( mContribution.getNoticeId() == 0 ) {
			getActionBar().setTitle("Création d'une nouvelle oeuvre");  // TODO : resourcify this string
		}
		else {
			getActionBar().setTitle("Ajout/Modif oeuvre existante"); // TODO : resourcify this string
		}

		// Populate the list view
		String propsToShow[] = {
				Contribution.TITRE,
				Contribution.ARTISTE,
				Contribution.COULEUR,
				Contribution.DATE_INAUGURATION,
				Contribution.DESCRIPTION,
				Contribution.MATERIAUX,
				Contribution.NOM_SITE,
				Contribution.DETAIL_SITE,
				Contribution.NATURE,
				Contribution.LATITUDE,
				Contribution.LONGITUDE,
				Contribution.AUTRE,
				Contribution.ETAT,
				Contribution.PETAT,
				Contribution.PMR
				};

		mList.clear();
		for (int i = 0; i < propsToShow.length; i++) {
			mList.add(mContribution.getProperty(propsToShow[i]));
		}
	}

    /*
     * Called when the system detects that this Activity is now visible.
     */
	@Override
	protected void onResume() {
		Log.d(DEBUG_TAG, "onResume");
		super.onResume();

		updatePictureView();
	}

	public void saveContributions() {
		if( ! mContribution.isModified() ) {
			Toast.makeText(this, this.getResources().getString(R.string.completer_au_moins_un_champs), Toast.LENGTH_LONG).show();
			return;
		}
		
		// Update location if needed for new notice
		if(MainActivity.mLastLocation != null && mContribution.getNoticeId() == 0) {
			ContributionProperty propLatitude = mContribution.getProperty(Contribution.LATITUDE);
			if( propLatitude.getValue() == "" ) {
				propLatitude.setValue(String.valueOf(MainActivity.mLastLocation.getLatitude()));
			}
			ContributionProperty propLongitude= mContribution.getProperty(Contribution.LONGITUDE);
			if( propLongitude.getValue() == "" ) {
				propLongitude.setValue(String.valueOf(MainActivity.mLastLocation.getLongitude()));
			}
		}
		
		mContribution.save(this);
		
		setResult(RESULT_SAVED);
		finish();
	}
	
	protected void sendContributions() {
		if( ! mContribution.isModified() ) {
			Toast.makeText(this, this.getResources().getString(R.string.completer_au_moins_un_champs), Toast.LENGTH_LONG).show();
			return;
		}

		if(! MainActivity.checkInternetConnection(this)) {
			AtlasError.showErrorDialog(ListChampsNoticeModif.this, "7.1", "pas internet connexion");
			return;
		}

		SharedPreferences prefs = getSharedPreferences(ConnexionActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		String username = prefs.getString(ConnexionActivity.PREF_KEY_USERNAME, "");
		String password = prefs.getString(ConnexionActivity.PREF_KEY_PASSWORD, "");
		if( username.equals("") || password.equals("") ) {
			AtlasError.showErrorDialog(ListChampsNoticeModif.this, "7.3", "compte util requis");
			return;
		}

		mContribution.setLogin(username);
		mContribution.setPassword(password);

		// Update location if needed for new notice
		if(MainActivity.mLastLocation != null && mContribution.getNoticeId() == 0) {
			ContributionProperty propLatitude = mContribution.getProperty(Contribution.LATITUDE);
			if( propLatitude.getValue() == "" ) {
				propLatitude.setValue(String.valueOf(MainActivity.mLastLocation.getLatitude()));
			}
			ContributionProperty propLongitude= mContribution.getProperty(Contribution.LONGITUDE);
			if( propLongitude.getValue() == "" ) {
				propLongitude.setValue(String.valueOf(MainActivity.mLastLocation.getLongitude()));
			}
		}

		ContributionSend contributionSend = new ContributionSend(this, mContribution);
		contributionSend.execute();
		
		//Toast.makeText(this, getResources().getString(R.string.completer_au_moins_un_champs), Toast.LENGTH_LONG).show();;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_MODIFY_PROPERTY) {
			if( resultCode == RESULT_OK ) {
				Log.d(DEBUG_TAG, "onActivityResult(REQUEST_MODIFY_PROPERTY, RESULT_OK)");
				
				if( data.hasExtra("property") ) {
					int position = data.getIntExtra("position", -1);
					ContributionProperty prop = (ContributionProperty) data.getSerializableExtra("property");
					Log.d(DEBUG_TAG, "onActivityResult(): modified property " + position + " => " + prop.getTitle() + " = " + prop.getValue());
					
					// Update current ListView
					mList.set(position, prop);
					mAdapter.notifyDataSetChanged();
					
					// Update the Activity Intent so that modifications will be preserved on screen rotation
					Intent intent = getIntent();
					Bundle bundle = intent.getExtras();
					if( bundle.containsKey("contribution") ) {
						mContribution.setProperty(prop.getDbField(), prop);
						bundle.putSerializable("contribution", mContribution);
						intent.putExtras(bundle);
						this.setIntent(intent);
					}
				}
			}
		}
		else if(requestCode == REQUEST_TAKE_PICTURE) {
			if( resultCode == RESULT_OK ) {
				Log.d(DEBUG_TAG, "onActivityResult(REQUEST_TAKE_PICTURE, RESULT_OK)");

				// Update the Activity Intent so the new photo will be preserved on screen rotation
				Intent intent = getIntent();
				Bundle bundle = intent.getExtras();
				if( bundle.containsKey("contribution") ) {
					ContributionProperty prop = mContribution.getProperty(Contribution.PHOTO);
			        SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
			        String newPhotoFilename = "";
			        if( prefs.contains("newPhotoFilename") ) {
			        	newPhotoFilename = prefs.getString("newPhotoFilename", "");
				        prefs.edit().remove(newPhotoFilename).commit();
			        }
					prop.setValue(newPhotoFilename);
					mContribution.setProperty(prop.getDbField(), prop);
					bundle.putSerializable("contribution", mContribution);
					intent.putExtras(bundle);
					this.setIntent(intent);
				}
			}
		}
		else if(requestCode == REQUEST_CONNECTION) {
			if(requestCode == RESULT_OK) {
				Log.d(DEBUG_TAG, "Connection success");
			}
		}
	}


	public void finishWithWarning()	{
		if( ! mContribution.isModified() ) {
			setResult(RESULT_CANCELED);
			finish();
			return;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.getResources().getString(R.string.app_name));
		builder.setMessage(this.getResources().getString(R.string.leave_contrib));
		
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
					setResult(RESULT_CANCELED);
					finish();
	           }
	       });
		
		builder.setNegativeButton(this.getResources().getString(R.string.annuler), new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User cancelled the dialog
	           }
	       });

	
		AlertDialog dialog = builder.create();
		dialog.show();

	}

	@Override
	public void onBackPressed() {
		finishWithWarning();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		if(itemId == android.R.id.home)
		{
			finishWithWarning();
			return true;
		}
		else if(itemId == R.id.action_account)
		{
			Intent intent= new Intent(this,ConnexionActivity.class);
			startActivityForResult(intent, REQUEST_CONNECTION);
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contribuer_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public void onContributionSent(Boolean success, String status) {
		if( success ) {
			String filename = mContribution.getSavedFilename();
			if( ! filename.equals("") ) {
				File file = new File(filename);
				if( file.exists() ) {
					file.delete();
				}
			}
			setResult(RESULT_SENT);
			finish();
		}
		else {
			Toast.makeText(this, getResources().getString(R.string.upload_failed, status), Toast.LENGTH_LONG).show();;
		}
	}
	
	private void takePicture() {
		// Create a temp filename to store new picture
		String newPhotoFilename = "";
		try {
			File file = File.createTempFile(
					/* prefix */ "photo_" + String.valueOf(mNoticeId) + "-",
					/* suffix */ ".jpg",
					/* directory */ Contribution.getPhotoDir());
			newPhotoFilename = file.getAbsolutePath();
			file.delete();
		} catch (IOException e) {
			// TODO: Display error message 
			e.printStackTrace();
			return;
		}

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        prefs.edit().putString("newPhotoFilename", newPhotoFilename).commit();
		
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(newPhotoFilename)));
		startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURE);
	}
	
	private void updatePictureView() {
    	String filename = mContribution.getProperty(Contribution.PHOTO).getValue();
    	File file = new File(filename);
    	if( file.exists() && file.isAbsolute() ) {
			mLayoutLoadPicture.setVisibility(View.GONE);
			mLayoutModifPicture.setVisibility(View.VISIBLE);
			mViewModifPicture.setVisibility(View.VISIBLE);
			
			/* Get the size of the image */
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = true;
			
			BitmapFactory.decodeFile(filename, bmOptions);

			int photoW = bmOptions.outWidth;
			int photoH = bmOptions.outHeight;
			int rotation = 0;
			
			try {
				ExifInterface exif = new ExifInterface(filename);
				int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
				switch( orientation ) {
				case ExifInterface.ORIENTATION_NORMAL:
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
					break;
				}
			} catch (IOException e) {
			}

			if (rotation != 0) {
				photoW = bmOptions.outHeight;
				photoH = bmOptions.outWidth;
			}
			
			int targetH = 512;
			int targetW = (int) (((double)targetH)/photoH*photoW);
			
			int scaleFactor = Math.max((int)Math.ceil((double)photoW/targetW), (int)Math.ceil((double)photoH/targetH));

			/* Set bitmap options to scale the image decode target */
			bmOptions.inJustDecodeBounds = false;
			bmOptions.inSampleSize = scaleFactor;
			bmOptions.inPurgeable = true;

			/* Decode the JPEG file into a Bitmap */
			Bitmap bitmap = BitmapFactory.decodeFile(filename, bmOptions);

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

			//Drawable drawable = Drawable.createFromPath(file.getAbsolutePath());
			mViewModifPicture.setImageBitmap(resizedBitmap);
    	}
    	else {
			mLayoutLoadPicture.setVisibility(View.VISIBLE);
			mLayoutModifPicture.setVisibility(View.GONE);
    	}
	}

	private void showLocationChangeAlertToUser() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage(getResources().getString(R.string.location_change_alert));
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setPositiveButton(
				getResources().getString(R.string.mise_ajour),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (MainActivity.mLastLocation == null) {
							Toast.makeText(
									getApplication(),
									getResources().getString(R.string.desole_position_pas_recup),
									Toast.LENGTH_LONG).show();
							return;
						}
						String latitude = Double.toString(MainActivity.mLastLocation.getLatitude());
						ContributionProperty propertyLatitude = mContribution.getProperty(Contribution.LATITUDE);
						propertyLatitude.setValue(latitude);

						String longitude = Double.toString(MainActivity.mLastLocation.getLongitude());
						ContributionProperty propertyLongitude = mContribution.getProperty(Contribution.LONGITUDE);
						propertyLongitude.setValue(longitude);
						
						// Update current ListView
						mAdapter.notifyDataSetChanged();
						
						// Update the Activity Intent so that modifications will be preserved on screen rotation
						Intent intent = getIntent();
						Bundle bundle = intent.getExtras();
						if( bundle.containsKey("contribution") ) {
							mContribution.setProperty(Contribution.LATITUDE, propertyLatitude);
							mContribution.setProperty(Contribution.LONGITUDE, propertyLongitude);
							bundle.putSerializable("contribution", mContribution);
							intent.putExtras(bundle);
							ListChampsNoticeModif.this.setIntent(intent);
						}
					}
				});
		alertDialogBuilder.setNegativeButton(
				getResources().getString(R.string.annuler),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

}
