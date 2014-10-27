package fr.atlasmuseum;



import fr.atlasmuseum.R;
import fr.atlasmuseum.account.ConnectionActivity;
import fr.atlasmuseum.account.ConnectionAsync;
import fr.atlasmuseum.contribution.ContributeActivity;
import fr.atlasmuseum.helper.AtlasError;
import fr.atlasmuseum.location.LocationProvider;
import fr.atlasmuseum.search.MapActivity;
import fr.atlasmuseum.search.SearchActivity;
import fr.atlasmuseum.search.SearchAroundActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AtlasmuseumActivity extends Activity
                                 implements LocationProvider.OnLocationChanged,
                                            ConnectionAsync.ConnectionListener {
	
	private static final String DEBUG_TAG = "AtlasMuseum/AtlasmuseumActivity";
	static final String STATE_CONNECTED = "connected";

	private static final int REQUEST_CONNECTION = 125455;

	TextView mTextProfil;
	private Boolean mConnected;
	
	//location stuff
	private LocationProvider mLocationProvider;
    public static Location mLastLocation = null;
  
    public static SharedPreferences cPref;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(DEBUG_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.atlasmuseum_activity);
		
		mConnected = false;
		
		Typeface font = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Bold.ttf");
		
		TextView textExplore = (TextView) findViewById(R.id.text_explore);
		textExplore.setTypeface(font);
		
		TextView textContribute = (TextView) findViewById(R.id.text_contribute);
		textContribute.setTypeface(font);
		
		TextView textAround = (TextView) findViewById(R.id.text_around);
		textAround.setTypeface(font);
		
		TextView textMap = (TextView) findViewById(R.id.text_map);
		textMap.setTypeface(font);
		
		TextView textHelp = (TextView) findViewById(R.id.text_help);
		textHelp.setTypeface(font);
		
		mTextProfil = (TextView) findViewById(R.id.text_profil);
		mTextProfil.setTypeface(font);
		
		TextView textNews = (TextView) findViewById(R.id.text_news);
		textNews.setTypeface(font);
		
		TextView textAbout = (TextView) findViewById(R.id.text_about);
		textAbout.setTypeface(font);
		
		
		LinearLayout buttonExplore = (LinearLayout) findViewById(R.id.button_explore);
		buttonExplore.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	    		Intent intent = new Intent(AtlasmuseumActivity.this, SearchActivity.class);
	    		startActivity(intent);
	        }
	    });
		
		LinearLayout buttonContribute = (LinearLayout) findViewById(R.id.button_contribute);
		buttonContribute.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		Intent intent = new Intent(AtlasmuseumActivity.this, ContributeActivity.class); 
	    		startActivity(intent);
	    	}
	    });
		
		LinearLayout buttonAround = (LinearLayout) findViewById(R.id.button_around);
		buttonAround.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	    		Intent intent = new Intent(AtlasmuseumActivity.this, SearchAroundActivity.class); 
	    		startActivity(intent);
	        }
	    });
		
		LinearLayout buttonMap = (LinearLayout) findViewById(R.id.button_map);
		buttonMap.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
				Bundle bundle = new Bundle();
				for(int i = 0 ; i < SearchActivity.getDB().nbentries ; i++) {
					bundle.putInt(Integer.toString(i), i);
				}
				bundle.putInt(SearchActivity.NB_ENTRIES, SearchActivity.getDB().nbentries);
				Intent intent = new Intent(AtlasmuseumActivity.this, MapActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
	    	}
	    });
		
		LinearLayout buttonHelp = (LinearLayout) findViewById(R.id.button_help);
		buttonHelp.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		Intent intent = new Intent(AtlasmuseumActivity.this, HelpActivity.class);
	    		startActivity(intent);
	    	}
	    });
		
		LinearLayout buttonProfil = (LinearLayout) findViewById(R.id.button_profil);
		buttonProfil.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		if( mConnected ) {
	    			mTextProfil.setText(getResources().getString(R.string.main_connection));
	    			mConnected = false;
	    			return;
	    		}

	    		if( ! checkInternetConnection(AtlasmuseumActivity.this)) {
	    			AtlasError.showErrorDialog(AtlasmuseumActivity.this, "7.1", "pas de connexion internet");
	    			return;
	    		}
	    		
	    		Intent intent = new Intent(AtlasmuseumActivity.this, ConnectionActivity.class);
	    		startActivityForResult(intent, REQUEST_CONNECTION);
	    	}
	    });
		
		LinearLayout buttonNews = (LinearLayout) findViewById(R.id.button_news);
		buttonNews.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		Intent intent = new Intent(AtlasmuseumActivity.this, NewsActivity.class);
	    		startActivity(intent);
	    	}
		});
			
		
		LinearLayout buttonAbout = (LinearLayout) findViewById(R.id.button_about);
		buttonAbout.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		Intent intent = new Intent(AtlasmuseumActivity.this, AboutActivity.class);
	    		startActivity(intent);
	    	}
	    });
		
		
		// Location stuff
		mLocationProvider = new LocationProvider(this);
		mLastLocation = null;
		mLocationProvider.connect();
		startLocationUpdate();
	}
	
	@Override
	protected void onStart() {
		Log.d(DEBUG_TAG, "onStart");
		super.onStart();
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.d(DEBUG_TAG, "onRestoreInstanceState");

		// Always call the superclass so it can restore the view hierarchy
	    super.onRestoreInstanceState(savedInstanceState);
	   
	    // Restore state members from saved instance
	    mConnected = savedInstanceState.getBoolean(STATE_CONNECTED);
	}
	
	@Override
	protected void onResume() {
		Log.d(DEBUG_TAG, "onResume");
		super.onResume();
		
		mTextProfil.setText(this.getResources().getString(mConnected ? R.string.main_disconnection : R.string.main_connection));
		
		// Try auto-login
		if(mConnected || ! checkInternetConnection(this)) {
			return;
		}
		SharedPreferences prefs = getSharedPreferences(ConnectionActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		if(! prefs.getBoolean(ConnectionActivity.PREF_KEY_AUTO_LOGIN, false)) {
			return;
		}
		String username = prefs.getString(ConnectionActivity.PREF_KEY_USERNAME, "");
		String password = prefs.getString(ConnectionActivity.PREF_KEY_PASSWORD, "");
		if( ! username.equals("") && ! password.equals("") ) {
			ConnectionAsync connection = new ConnectionAsync(this, username, password);
			connection.execute();
		}
	}
	
	@Override
	protected void onPause() {
		Log.d(DEBUG_TAG, "onPause");
		super.onPause();
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		Log.d(DEBUG_TAG, "onSaveInstanceState");

		// Save the user's current game state
	    savedInstanceState.putBoolean(STATE_CONNECTED, mConnected);
	    
	    // Always call the superclass so it can save the view hierarchy state
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onStop() {
		Log.d(DEBUG_TAG, "onStop");
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		Log.d(DEBUG_TAG, "onDestroy");
		stopLocationUpdate();
		mLocationProvider.disconnect();
		super.onDestroy();
	}	
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if(requestCode == REQUEST_CONNECTION) {
	    	if(resultCode == RESULT_OK) {
	    		mConnected = true;
	    	}
	    	else if(resultCode == RESULT_CANCELED) {
	    		mConnected = false;
	    	}
	    }
	}
	
	   
	private void startLocationUpdate() {
		Log.d(DEBUG_TAG, "startLocationUpdate");
		mLocationProvider.startPeriodicUpdates();
	}

	private void stopLocationUpdate()  {
		Log.d(DEBUG_TAG, "stopLocationUpdate");
		mLocationProvider.stopPeriodicUpdates();
	}

	public void onLocationUpdated( Location location ) {
		Log.d(DEBUG_TAG, "onLocationUpdated: Latitude = " + location.getLatitude() + " - Longitude = " + location.getLongitude() + " - Accuracy = " + location.getAccuracy());
		mLastLocation = new Location(location);
	}
	
	@Override
	public void onConnectionOk() {
		mTextProfil.setText(this.getResources().getString(R.string.main_disconnection));
		mConnected = true;
	}

	@Override
	public void onConnectionFailed() {
		mTextProfil.setText(this.getResources().getString(R.string.main_connection));
		mConnected = false;
	}
    
	static public boolean checkInternetConnection(Context context) {
    	ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return (activeNetwork != null &&
			   (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI ||
			    activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE));
    }


}
