package fr.atlasmuseum.main;

import com.irisa.unpourcent.location.LocationProvider;
import com.irisa.unpourcent.location.LocationStruct;


import fr.atlasmuseum.R;
import fr.atlasmuseum.compte.ConnexionActivity;
import fr.atlasmuseum.compte.ConnexionAsync;
import fr.atlasmuseum.contribution.MainContribActivity;
import fr.atlasmuseum.search.SearchActivity;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity
                          implements LocationProvider.OnLocationChanged,
                                                      ConnexionAsync.ConnectionListener {
	
	private static final String DEBUG_TAG = "AtlasMuseum/MainActivity";
	static final String STATE_CONNECTED = "connected";

	private static final int REQUEST_CONNECTION = 125455;

	TextView mTextProfil;
	private Boolean mConnected;
	
	//location stuff
	private LocationProvider mLocationProvider;
    public static LocationStruct mLastLocation = new LocationStruct();
  
    private Boolean mUpdateLocationRequested = true;
    
    public static SharedPreferences cPref;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(DEBUG_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mConnected = false;
		
		Typeface font = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Bold.ttf");
		
		TextView textSearch = (TextView) findViewById(R.id.txt_view_search);
		textSearch.setTypeface(font);
		
		TextView textContribute = (TextView) findViewById(R.id.txt_view_contribuer);
		textContribute.setTypeface(font);
		
		TextView textHelp = (TextView) findViewById(R.id.txt_view_aide);
		textHelp.setTypeface(font);
		
		mTextProfil = (TextView) findViewById(R.id.txt_view_profil);
		mTextProfil.setTypeface(font);
		
		TextView textNews = (TextView) findViewById(R.id.txt_view_actu);
		textNews.setTypeface(font);
		
		TextView textAbout = (TextView) findViewById(R.id.txt_view_apropos);
		textAbout.setTypeface(font);
		
		
		RelativeLayout buttonExplorer = (RelativeLayout) findViewById(R.id.mButtonExplorer);
		buttonExplorer.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	    		Intent intent = new Intent(MainActivity.this, SearchActivity.class);
	    		startActivity(intent);
	        }
	    });
		
		RelativeLayout buttonContribuer = (RelativeLayout) findViewById(R.id.mButtonContribuer);
		buttonContribuer.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		Intent intent = new Intent(MainActivity.this, MainContribActivity.class); 
	    		startActivity(intent);
	    	}
	    });
		
		RelativeLayout buttonProfil = (RelativeLayout) findViewById(R.id.mButtonProfil);
		buttonProfil.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		if( mConnected ) {
	    			mTextProfil.setText(getResources().getString(R.string.connexion));
	    			mConnected = false;
	    			return;
	    		}

	    		if( ! checkInternetConnection(MainActivity.this)) {
	    			AtlasError.showErrorDialog(MainActivity.this, "7.1", "pas de connexion internet");
	    			return;
	    		}
	    		
	    		Intent intent = new Intent(MainActivity.this, ConnexionActivity.class);
	    		startActivityForResult(intent, REQUEST_CONNECTION);
	    	}
	    });
		
		RelativeLayout buttonActu = (RelativeLayout) findViewById(R.id.mButtonActu);
		buttonActu.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		Intent intent = new Intent(MainActivity.this, ActuActivity.class);
	    		startActivity(intent);
	    	}
		});
			
		
		RelativeLayout buttonAPropos = (RelativeLayout) findViewById(R.id.mButtonAPropos);
		buttonAPropos.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		Intent intent = new Intent(MainActivity.this, AproposActivity.class);
	    		startActivity(intent);
	    	}
	    });
		
		RelativeLayout buttonHelp = (RelativeLayout) findViewById(R.id.mButtonAide);
		buttonHelp.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		Intent intent = new Intent(MainActivity.this, HelpActivity.class);
	    		startActivity(intent);
	    	}
	    });
		
		
		// Location stuff
		mLocationProvider = new LocationProvider(this);
		mLastLocation = null;
		mUpdateLocationRequested = false;
		startLocationUpdate();
		updateLocation();
	}
	
	@Override
	protected void onStart() {
		Log.d(DEBUG_TAG, "onStart");
		super.onStart();
		mLocationProvider.connect();
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
		
		if (mLastLocation == null ) {
			updateLocation();
		}
		
		mTextProfil.setText(this.getResources().getString(mConnected ? R.string.deconnexion : R.string.connexion));
		
		// Try auto-login
		if(mConnected || ! checkInternetConnection(this)) {
			return;
		}
		SharedPreferences prefs = getSharedPreferences(ConnexionActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		if(! prefs.getBoolean(ConnexionActivity.PREF_KEY_AUTO_LOGIN, false)) {
			return;
		}
		String username = prefs.getString(ConnexionActivity.PREF_KEY_USERNAME, "");
		String password = prefs.getString(ConnexionActivity.PREF_KEY_PASSWORD, "");
		if( ! username.equals("") && ! password.equals("") ) {
			ConnexionAsync connection = new ConnexionAsync(this, username, password);
			connection.execute();
		}
	}
	
	@Override
	protected void onPause() {
		Log.d(DEBUG_TAG, "onPause");
		stopLocationUpdate();
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
		mLocationProvider.disconnect();
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		Log.d(DEBUG_TAG, "onDestroy");
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
	
	   
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void startLocationUpdate() {
		Log.d(DEBUG_TAG, "startLocationUpdate");
		mLocationProvider.startPeriodicUpdates();
	}

	private void updateLocation() {
		Log.d(DEBUG_TAG, "updateLocation");
		mUpdateLocationRequested = true;
	}
	
	private void stopLocationUpdate()  {
		Log.d(DEBUG_TAG, "stopLocationUpdate");
		mLocationProvider.stopPeriodicUpdates();
	}

	public void onLocationUpdated( Location location ) {
		Log.d(DEBUG_TAG, "onLocationUpdated");
		if( location == null ) {
			return;
		}
		if( mUpdateLocationRequested ) {
			mUpdateLocationRequested = false;
			mLastLocation = new LocationStruct(location);
			Log.d(DEBUG_TAG, "Location updated");
		}
	}
	
	@Override
	public void onConnectionOk() {
		mTextProfil.setText(this.getResources().getString(R.string.deconnexion));
		mConnected = true;
	}

	@Override
	public void onConnectionFailed() {
		mTextProfil.setText(this.getResources().getString(R.string.connexion));
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
