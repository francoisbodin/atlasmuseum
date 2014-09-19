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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import fr.atlasmuseum.R;
import fr.atlasmuseum.compte.Authentification;
import fr.atlasmuseum.compte.ConnexionActivity;
import fr.atlasmuseum.main.AtlasError;
import fr.atlasmuseum.main.MainActivity;
import fr.atlasmuseum.search.loadPhotoInterface;
import fr.atlasmuseum.search.loadingPhoto2;

public class ListChampsNoticeModif extends Activity	implements loadPhotoInterface {
	private static final String DEBUG_TAG = "AtlasMuseum/ListChampAct";
	
	static final int REQUEST_MODIFY_PROPERTY = 1;
	static final int REQUEST_TAKE_PICTURE = 2;
	static final int REQUEST_CONNEXION = 1450233;
	
	private Bundle mBundle;
	private Contribution mContribution;
	private int mNoticeId;
	private String mNewPhotoFilename;
	
	private List<ContributionProperty> mList;
	
	private RelativeLayout mLoadPhoto;
	private ImageView mImageNotice;
	private RelativeLayout mBlockModifierImage;

	public ContributionPropertyAdapter mAdapter;
	
	
	public void onCreate(Bundle savedInstanceState)
	{
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
		
		mNewPhotoFilename = "";
		
		mImageNotice = (ImageView) findViewById(R.id.imageView1);

		mBlockModifierImage = (RelativeLayout) findViewById(R.id.relativeLayoutPhotoModifier);

		mList = new ArrayList<ContributionProperty>();
		mAdapter = new ContributionPropertyAdapter(ListChampsNoticeModif.this, mList);
		ListView listView = (ListView) findViewById(R.id.list_view);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id)
			{
				ContributionProperty prop = (ContributionProperty) mAdapter.getItem(position);
				String field = (String) prop.getDbField();
				
				if( field == Contribution.LATITUDE ||
				    field == Contribution.LONGITUDE) {
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


		ImageButton buttoModifPhoto = (ImageButton) findViewById(R.id.modif_photo);
		buttoModifPhoto.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) {
				// Create a temp filename to store new picture
				try {
					File file = File.createTempFile(
							/* prefix */ "photo_" + String.valueOf(mNoticeId) + "-",
							/* suffix */ ".jpg",
							/* directory */ Contribution.getPhotoDir());
					mNewPhotoFilename = file.getAbsolutePath();
					file.delete();
				} catch (IOException e) {
					// TODO: Display error message 
					e.printStackTrace();
					return;
				}

				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mNewPhotoFilename)));
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURE);
			}
		});

		mLoadPhoto = (RelativeLayout) findViewById(R.id.image_loading);
		mLoadPhoto.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) {
				// Create a temp filename to store new picture
				try {
					File file = File.createTempFile(
							/* prefix */ "photo_" + String.valueOf(mNoticeId) + "-",
							/* suffix */ ".jpg",
							/* directory */ Contribution.getPhotoDir());
					mNewPhotoFilename = file.getAbsolutePath();
					file.delete();
				} catch (IOException e) {
					// TODO: Display error message 
					e.printStackTrace();
					return;
				}

				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mNewPhotoFilename)));
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURE);
			}
		});

		RelativeLayout annuler_button = (RelativeLayout) findViewById(R.id.buttonAnnuler);
		annuler_button.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) {
				finishWithWarning();
			}
		});

		RelativeLayout buttonSave = (RelativeLayout) findViewById(R.id.mbuttonSave);
		buttonSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveContributions();
			}
		});

		RelativeLayout buttonEnvoyer = (RelativeLayout) findViewById(R.id.mbuttonEnvoyer);
		buttonEnvoyer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendContributions();
			}
		});

		ActionBar actionBar = getActionBar();
		if (actionBar != null)
		{
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

		ContributionProperty cp = mContribution.getProperty("photo");
		if (! cp.getValue().equals("")) {
			mLoadPhoto.setVisibility(View.GONE);
			mBlockModifierImage.setVisibility(View.VISIBLE);
			loadingPhoto2 upl = new loadingPhoto2(this, cp.getValue(), false);
			upl.execute();
		}
		else {
			mLoadPhoto.setVisibility(View.VISIBLE);
			mBlockModifierImage.setVisibility(View.GONE);
		}

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

	public void saveContributions() {
		// Add login/password information
		if(Authentification.getisConnected()) {
			mContribution.setLogin(Authentification.getUsername());
			mContribution.setPassword(Authentification.getPassword());
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
		
		setResult(RESULT_OK);
		finish();
		
		// TODO: check we have at least one modification in current contribution
		//Toast.makeText(this, this.getResources().getString(R.string.completer_au_moins_un_champs), Toast.LENGTH_LONG).show();
	}
	protected void sendContributions() {
		if(! checkInternetConnection()) {
			AtlasError.showErrorDialog(ListChampsNoticeModif.this, "7.1", "pas internet connexion");
			return;
		}

		if(! Authentification.getisConnected()) {
			AtlasError.showErrorDialog(ListChampsNoticeModif.this, "7.3", "compte util requis");
			return;
		}

		// Add login/password information
		if(Authentification.getisConnected()) {
			mContribution.setLogin(Authentification.getUsername());
			mContribution.setPassword(Authentification.getPassword());
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

		ContributionSend contributionSend = new ContributionSend(this, mContribution);
		contributionSend.execute();
		
		//Toast.makeText(this, getResources().getString(R.string.completer_au_moins_un_champs), Toast.LENGTH_LONG).show();;
	}

	public boolean checkInternetConnection()
	{
		ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isWiFi = false;

		if(activeNetwork!=null && ( activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)){
			isWiFi = true;
		}
		else
		{
			isWiFi =false;
		}
		return isWiFi;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_MODIFY_PROPERTY)
		{
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
				mLoadPhoto.setVisibility(View.GONE);
				mBlockModifierImage.setVisibility(View.VISIBLE);

				// Update the Activity Intent so the new photo will be preserved on screen rotation
				Intent intent = getIntent();
				Bundle bundle = intent.getExtras();
				if( bundle.containsKey("contribution") ) {
					ContributionProperty prop = mContribution.getProperty(Contribution.PHOTO);
					prop.setValue(mNewPhotoFilename);
					mContribution.setProperty(prop.getDbField(), prop);
					bundle.putSerializable("contribution", mContribution);
					intent.putExtras(bundle);
				}
				loadingPhoto2 upl = new loadingPhoto2(this, (new File(mNewPhotoFilename)).getName(), true);
				upl.execute();
			}
			mNewPhotoFilename = "";
		}
		else if(requestCode == REQUEST_CONNEXION) {
			if(requestCode == RESULT_OK) {
				Log.d(DEBUG_TAG, "Connection success");
			}
		}
	}


	public void finishWithWarning()
	{
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
	public Context getContext() {
		return this;
	}

	@Override
	public ImageView getImageView() {
		return this.mImageNotice;
	}

	@Override
	public BaseAdapter getNoticeAdapter() {
		return null;
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
			startActivityForResult(intent, REQUEST_CONNEXION);
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
	
	private void showLocationChangeAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getResources().getString(R.string.location_change_alert))
        .setCancelable(false)
        .setPositiveButton(getResources().getString(R.string.mise_ajour),
                new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
            	if( MainActivity.mLastLocation == null ) {
					Toast.makeText(getApplication(), getResources().getString(R.string.desole_position_pas_recup), Toast.LENGTH_LONG).show();
            		return;
            	}
            	String lat = Double.toString(MainActivity.mLastLocation.getLatitude());
            	String longi = Double.toString(MainActivity.mLastLocation.getLongitude());
            	Log.d(DEBUG_TAG,"Latitude :" + lat);
    			Log.d(DEBUG_TAG,"Longitude :" + longi);
    			//ListChampsNoticeModif.cPref.edit().putString(ListChampsNoticeModif.LATITUDE,""+MainActivity.mLastLocation.getLatitude()).commit();
				//ListChampsNoticeModif.cPref.edit().putString(ListChampsNoticeModif.LONGITUDE,""+MainActivity.mLastLocation.getLongitude()).commit();
				// on doit relancer l'activité pour un affichage ok
				setResult(RESULT_OK);//pour fermer l'activité ListChampsNoticeModif précédente
				Intent intent = new Intent(getApplication(), ListChampsNoticeModif.class);
	        	intent.putExtras(mBundle);
	  			startActivity(intent);
	  			finish();
            }
        });
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.annuler),
                new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
