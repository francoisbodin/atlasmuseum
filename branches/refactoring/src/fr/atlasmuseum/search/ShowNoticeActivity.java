package fr.atlasmuseum.search;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import fr.atlasmuseum.R;
import fr.atlasmuseum.contribution.Contribution;
import fr.atlasmuseum.contribution.ContributionProperty;
import fr.atlasmuseum.contribution.ContributionRestoreDialogFragment;
import fr.atlasmuseum.contribution.ListChampsNoticeModif;
import fr.atlasmuseum.main.AtlasError;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ShowNoticeActivity extends Activity implements ContributionRestoreDialogFragment.ContributionRestoreDialogListener{

	private static final String DEBUG_TAG = "AtlasMuseum/ShowNotice";
	public static final String ARG_FRAGMENT = "IDFragment";
	
	private int mDbIndex;
	Contribution mContribution;
	ImageView mViewPhoto;
	
	private int REQUEST_CONTRIB=12345874;
	
	TextView creditphoto;
	
	String url; //pour acces wiki
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.show_notice_layout_new);

        Log.d(DEBUG_TAG, "onCreate()");
    	
    	Bundle bundle = getIntent().getExtras();
    	mDbIndex = bundle.getInt(ARG_FRAGMENT);
    	Log.d(DEBUG_TAG, "idBDD = " + mDbIndex);
    	
    	mContribution = new Contribution();
    	mContribution.updateFromDb(mDbIndex);
    	
		for (ContributionProperty prop : mContribution.getProperties()) {
			int showViewText = prop.getShowViewText();

			if( showViewText == 0 ) continue;
			
    		TextView viewText = (TextView) findViewById(showViewText);
    		String value = prop.getValue();
    		if( value == "" ) {
    			value = prop.getDefaultValue();
    		}
    		if( value != "" ) {
    			viewText.setText(value);
    		}
    		else {
    			int viewToHideId = prop.getShowViewToHide();
    			if( viewToHideId != 0 ) {
    				View viewToHide = findViewById(viewToHideId);
    				viewToHide.setVisibility(View.GONE);
    			}
    		}
    	}

    	// Special handling for Ville-Pays
    	ContributionProperty propVille = mContribution.getProperty(Contribution.VILLE);
    	ContributionProperty propPays = mContribution.getProperty(Contribution.PAYS);
    	String ville = propVille.getValue();
    	String pays = propPays.getValue();
    	
		TextView textViewVillePays = (TextView) findViewById(R.id.notice_ville_pays);
    	if( ville == "" || pays == "" ) {
    		textViewVillePays.setVisibility(View.GONE);
    	}
    	else {
    		textViewVillePays.setText(ville+"-"+pays);
    	}
  	
    	Button buttonWikipedia = (Button) findViewById(R.id.btn_acces_wiki);
    	buttonWikipedia.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	Intent intent = new Intent(Intent.ACTION_VIEW);
	        	intent.setData(Uri.parse(mContribution.getProperty(Contribution.URL).getValue()));
	        	startActivity(intent);
	        }
    	});
    	
    	
    	ImageView buttonMap = (ImageView) findViewById(R.id.littlemapbutton);
    	buttonMap.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	Bundle extra = new Bundle();
	    		Intent intent = new Intent(getApplication(), MapActivity.class);
	    		extra.putInt("0",mDbIndex);
	    		extra.putInt(SearchActivity.NB_ENTRIES,1);
	    		extra.putInt(SearchActivity.MAP_FOCUS_NOTICE,1);
	    		intent.putExtras(extra);
	    		startActivity(intent);
	        }
    	});
    	
    	
    	mViewPhoto = (ImageView) findViewById(R.id.imageView1);
    	RelativeLayout buttonPhoto = (RelativeLayout) findViewById(R.id.image_loading);
    	if (showExistingPhoto() == false) {
    		mViewPhoto.setVisibility(View.GONE);
    		buttonPhoto.setVisibility(View.VISIBLE);
    		buttonPhoto.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				showPhoto();
    			}
    		});
    	} 
    	else { // hide button
    		buttonPhoto.setVisibility(View.GONE);
    		mViewPhoto.setVisibility(View.VISIBLE);
    	}
    	
    	ActionBar actionBar = getActionBar();
    	if (actionBar != null) {
    		actionBar.show();

    		actionBar.setTitle(this.getResources().getString(R.string.notice_oeuvre));
    		actionBar.setDisplayShowTitleEnabled(true);
    		actionBar.setHomeButtonEnabled(true);

    		actionBar.setDisplayHomeAsUpEnabled(true);
    		//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
    	}
    }


	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		
		if(itemId == android.R.id.home) {
			super.onBackPressed();
			finish();
			return true;
		}
    	
		if (itemId == R.id.action_contrib) {
			mContribution.setLocalId(UUID.randomUUID().toString());

			// Check if a saved contribution matches this one
			File saveDir = new File( Contribution.getSaveDir(this) );
			File saveFile = new File( saveDir, "modif_" + Integer.toString(mContribution.getNoticeId()));
			if( saveFile.exists() ) {
				DialogFragment newFragment = new ContributionRestoreDialogFragment();
			    newFragment.show(getFragmentManager(), "ContributionRestoreDialogFragment");
			}
			else {
				modifyNotice(mContribution);
			}
			return true;
		}
		else {
			return false;
		}
    }
    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_notice_menu, menu);
        return super.onCreateOptionsMenu(menu);
	}
	
    private Boolean showExistingPhoto(){
    	try {
    		String fichierImage = mContribution.getProperty(Contribution.PHOTO).getValue();
    		File fimage = SearchActivity.checkIfImageFileExists(fichierImage) ;
    		if (fimage != null) {
    			Bitmap bmSmall = BitmapFactory.decodeFile(fimage.getAbsolutePath());
    			ImageView viewPhoto =(ImageView) findViewById(R.id.imageView1);
    			viewPhoto.setImageBitmap(bmSmall);
    			return true;
    		}
    	} catch (IOException e) {
    		Log.d(DEBUG_TAG, "Could not view photo");
    		AtlasError.showErrorDialog(this, "5.1", "");// ERROR "notice n'a pas pu �tre visualis�"
    		//Toast.makeText(getActivity(),"L'image de cette notice n'a pu �tre visualis�", Toast.LENGTH_SHORT).show();
    	}
    	return false;
    }
    
    private void showPhoto(){
    	LoadingPhotoAsync upl = new LoadingPhotoAsync(this, mContribution.getProperty(Contribution.PHOTO).getValue(), mViewPhoto);
    	upl.execute();
    }

    private void modifyNotice( Contribution contribution ) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("contribution", contribution);
		Intent intent = new Intent(this, ListChampsNoticeModif.class);
		intent.putExtras(bundle);
		startActivityForResult(intent, this.REQUEST_CONTRIB);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == this.REQUEST_CONTRIB) {
    		if(resultCode == RESULT_OK) {
    			Toast.makeText(this, getResources().getString(R.string.contrib_envoi_success), Toast.LENGTH_SHORT).show();
    		}
    	}
    	else {
    		super.onActivityResult(requestCode, resultCode, data);
    	}
    }
    
	@Override
	public void onRestoreSavedModifications() {
		Log.d(DEBUG_TAG, "onRestoreSavedModifications()");
		File saveDir = new File( Contribution.getSaveDir(this) );
		File saveFile = new File( saveDir, "modif_" + Integer.toString(mContribution.getNoticeId()));		
		Contribution contribution = Contribution.restoreFromFile(saveFile.getAbsolutePath());
		modifyNotice(contribution);
	}


	@Override
	public void onDiscardSavedModifications() {
		modifyNotice(mContribution);
	}

}
