package fr.atlasmuseum.search;

import java.io.File;
import java.io.IOException;
import fr.atlasmuseum.R;
import fr.atlasmuseum.contribution.Contribution;
import fr.atlasmuseum.contribution.Contribution2;
import fr.atlasmuseum.contribution.ContributionProperty;
import fr.atlasmuseum.contribution.ListChampsNoticeModif;
import fr.atlasmuseum.main.AtlasError;
import android.app.ActionBar;
import android.app.Activity;
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


public class ShowNoticeActivity extends Activity{

	private static final String DEBUG_TAG = "AtlasMuseum/ShowNotice";
	public static final String ARG_FRAGMENT = "IDFragment";
	private Button btn_map;
	private Button btn_acces_wikipedia;
	private RelativeLayout mButtonPhoto;
	
	private Bundle bundle;
	private int mDbIndex;
	Contribution2 mContribution;
	
	public ImageView imgView;//pour afficher l'image de la notice
	private int REQUEST_CONTRIB=12345874;
	
	TextView creditphoto;
	
	String url; //pour acces wiki
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.show_notice_layout_new);

        Log.d(DEBUG_TAG, "onCreate()");
    	
    	bundle = getIntent().getExtras();
    	mDbIndex = bundle.getInt("IDFragment");
    	Log.d(DEBUG_TAG, "idBDD = " + mDbIndex);
    	
    	mContribution = new Contribution2(this);
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
  	
    	

    	
    	btn_map = (Button) findViewById(R.id.btn_map);
    	imgView = (ImageView) findViewById(R.id.imageView1);
    	ImageView littlemapbutton = (ImageView) findViewById(R.id.littlemapbutton);
    	btn_acces_wikipedia = (Button) findViewById(R.id.btn_acces_wiki);
    	btn_acces_wikipedia.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	Intent intent = new Intent(Intent.ACTION_VIEW);
	        	intent.setData(Uri.parse(mContribution.getProperty(Contribution.URL).getValue()));
	        	startActivity(intent);
	        }
    	});
    	
    	btn_map.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	showMap();
	        }
    	});
    	
    	littlemapbutton.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	showMap();
	        }
    	});
    	
    	
    	mButtonPhoto = (RelativeLayout) findViewById(R.id.image_loading);
    	if (showExistingPhoto() == false) 
    	{
    		imgView.setVisibility(View.GONE);
    		mButtonPhoto.setVisibility(View.VISIBLE);
    		mButtonPhoto.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				Log.i(DEBUG_TAG, "Button Photo activation");
    				showPhoto();
    			}
    		});
    	} 
    	else 
    	{ // hide button
    		mButtonPhoto.setVisibility(View.GONE);
    		imgView.setVisibility(View.VISIBLE);
    	}
    	
    	//pour autoriser le retour en cliquant sur l'icone de l'application dans l'action bar
		////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////
		//ACTION BAR
		ActionBar actionBar = getActionBar();
		if (actionBar != null)
		{
			actionBar.show();
			
			actionBar.setTitle(this.getResources().getString(R.string.notice_oeuvre));
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setHomeButtonEnabled(true);

			actionBar.setDisplayHomeAsUpEnabled(true);
			//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
				}
    }//fin onCreate


	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		
		if(itemId == android.R.id.home)
		{
			super.onBackPressed();
			finish();
			return true;
		}
    	
		if (itemId == R.id.action_contrib)
		{
			//String uniqueID = UUID.randomUUID().toString();
			//bundle.putString(Contribution.LOCALID, uniqueID);

			bundle.putSerializable("contribution", mContribution);
			Intent intent = new Intent(this, ListChampsNoticeModif.class);
			intent.putExtras(bundle);
			startActivityForResult(intent, this.REQUEST_CONTRIB);
			return true;
		}
		else return false;
		    
    }
    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_notice_menu, menu);
        return super.onCreateOptionsMenu(menu);
	}
	
    private void showMap(){
    	Bundle extra = new Bundle();
		Intent intent = new Intent(this, MapActivity.class);
		extra.putInt("0",mDbIndex);
		Log.i(DEBUG_TAG, "idx for MAP is "+mDbIndex);
		extra.putInt(SearchActivity.NB_ENTRIES,1);
		extra.putInt(SearchActivity.MAP_FOCUS_NOTICE,1);
		intent.putExtras(extra);
		startActivity(intent);
	}
    

    private Boolean showExistingPhoto(){
    	ImageView imgView =(ImageView) findViewById(R.id.imageView1);
    	Bitmap bmSmall = null;
    	try {
    		String fichierImage = SearchActivity.extractDataFromDb(mDbIndex,"image_principale");
    		Log.d(DEBUG_TAG, "id = "+mDbIndex);
    		Log.d(DEBUG_TAG, "image file = "+fichierImage);
    		File fimage = SearchActivity.checkIfImageFileExists(fichierImage) ;
    		if (fimage != null) {
    			Log.d(DEBUG_TAG, "showExistingPhoto Image file exist");
    			bmSmall = BitmapFactory.decodeFile(fimage.getAbsolutePath());
    			imgView.setImageBitmap(bmSmall);
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
    	
    	//ImageView imgView =(ImageView) findViewById(R.id.imageView1);
    	
    	LoadingPhotoAsync upl = new LoadingPhotoAsync(this, this.mDbIndex);
    	upl.execute();
    	
    }
    
    
    // here we select the entries to display
    Bundle selectEntries(){
    	Bundle extra = new Bundle();
    	// temporary setting
    	extra.putInt(SearchActivity.MAP_FOCUS_NOTICE,1);
    	extra.putInt(SearchActivity.NB_ENTRIES,1);
    	extra.putInt(Integer.toString(0),getEntryNumberForFragment(mDbIndex));
    	extra.putDouble(SearchActivity.CURRENT_LAT,0.0);
    	extra.putDouble(SearchActivity.CURRENT_LONG,0.0);
    	return extra;
    }
    
   
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	/// HELPER ROUTINES ///
	
	int getEntryNumberForFragment(int idx){
	if (bundle == null) return -1;
	return bundle.getInt(Integer.toString(idx));
	}
	
	int getNumberOfEntries(){
	int v = bundle.getInt(SearchActivity.NB_ENTRIES);
	Log.d(DEBUG_TAG, "getNumberOfEntries : " + v);
	return v;
	}
	
	Double getCurrentLatitude(){
	Double v = bundle.getDouble(SearchActivity.CURRENT_LAT );
	Log.d(DEBUG_TAG, "getCurrentLatitude : " + v);
	return v;
	}
	
	Double getCurrentLongitude(){
	Double v = bundle.getDouble(SearchActivity.CURRENT_LONG );
	Log.d(DEBUG_TAG, "getCurrentLongitude : " + v);
	return v;
	}
	

		public ImageView getImageVew() {
			return this.imgView;
		}

		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		    // TODO Auto-generated method stub
		    Log.d(DEBUG_TAG, "result code="+requestCode);
		    if(requestCode == this.REQUEST_CONTRIB)
		    {
		    	if(resultCode == RESULT_OK)
		    	{

			    	Toast.makeText(this, getResources().getString(R.string.contrib_envoi_success), Toast.LENGTH_SHORT).show();
		    	}
		    }
		    else
		    {
		    	super.onActivityResult(requestCode, resultCode, data);
		    }
		    
		}
}
