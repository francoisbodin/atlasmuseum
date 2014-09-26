package fr.atlasmuseum.search;

import java.io.File;
import java.util.List;
import java.util.UUID;

import fr.atlasmuseum.R;
import fr.atlasmuseum.contribution.Contribution;
import fr.atlasmuseum.contribution.ContributionProperty;
import fr.atlasmuseum.contribution.ContributionRestoreDialogFragment;
import fr.atlasmuseum.contribution.ListChampsNoticeModif;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
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


public class ShowNoticeActivity extends Activity
                                implements ContributionRestoreDialogFragment.ContributionRestoreDialogListener,
                                           Contribution.ContributionModificationListener {

	private static final String DEBUG_TAG = "AtlasMuseum/ShowNotice";
	public static final String ARG_FRAGMENT = "IDFragment";
	
	private int mDbIndex;
	Contribution mContribution;
	ImageView mViewPicture;
	RelativeLayout mButtonLoadPicture;
	
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
    	
    	mButtonLoadPicture = (RelativeLayout) findViewById(R.id.image_loading);
		mButtonLoadPicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mContribution.loadPicture(ShowNoticeActivity.this);
				updatePictureView();
			}
		});

    	mViewPicture = (ImageView) findViewById(R.id.imageView1);
    	mViewPicture.setOnClickListener(new OnClickListener() {	
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
    
    /*
     * Called when the system detects that this Activity is now visible.
     */
	@Override
	protected void onResume() {
		Log.d(DEBUG_TAG, "onResume");
		super.onResume();

		updatePictureView();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_notice_menu, menu);
        return super.onCreateOptionsMenu(menu);
	}
	

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == this.REQUEST_CONTRIB) {
			switch(resultCode) {
			case ListChampsNoticeModif.RESULT_SAVED:
				Toast.makeText(this, getResources().getString(R.string.contrib_save), Toast.LENGTH_SHORT).show();
				break;
			case ListChampsNoticeModif.RESULT_SENT:
    			Toast.makeText(this, getResources().getString(R.string.contrib_envoi_success), Toast.LENGTH_SHORT).show();
				break;
			case RESULT_CANCELED:
			default:
				break;
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
	
	@Override
	public void onPictureModified() {
		updatePictureView();
	}
	
	private void modifyNotice( Contribution contribution ) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("contribution", contribution);
		Intent intent = new Intent(this, ListChampsNoticeModif.class);
		intent.putExtras(bundle);
		startActivityForResult(intent, this.REQUEST_CONTRIB);
	}
	    
	private void updatePictureView() {
    	String filename = mContribution.getProperty(Contribution.PHOTO).getValue();
    	File file = new File(filename);
    	if( file.exists() && file.isAbsolute() ) {
    		// Image already loaded
    		mButtonLoadPicture.setVisibility(View.GONE);
    		mViewPicture.setVisibility(View.VISIBLE);
    		mViewPicture.setImageDrawable(Drawable.createFromPath(file.getAbsolutePath()));
    	}
    	else {
    		mViewPicture.setVisibility(View.GONE);
    		mButtonLoadPicture.setVisibility(View.VISIBLE);
    	}
	}
}
