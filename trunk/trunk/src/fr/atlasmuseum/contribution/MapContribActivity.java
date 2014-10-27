package fr.atlasmuseum.contribution;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.irisa.unpourcent.location.LocationProvider;
import com.irisa.unpourcent.location.LocationStruct;

import fr.atlasmuseum.R;
import fr.atlasmuseum.main.AtlasError;
import fr.atlasmuseum.main.MainActivity;
import fr.atlasmuseum.search.SearchActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MapContribActivity extends Activity  implements LocationProvider.OnLocationChanged{
	private static final String DEBUG_TAG = "atlasmuseum/MapActivity";
	private Bundle bundle = null;
    private GoogleMap googleMap = null;
    private Button mButtonmaj;
    private Button mButtonsave;
    private Context ContextApps = null;
    //les valeurs pour le retour
    private double latitude;
	private double longitude;
	
	private LocationProvider mLocationProvider;
	private Boolean mUpdateLocationRequested = true;
	
	
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		Log.d(DEBUG_TAG, "onCreateView");
		
		setContentView(R.layout.activity_map_contrib);
		bundle = getIntent().getExtras();
		initilizeMap();
		
		mButtonsave = (Button) findViewById(R.id.mButtonsave);
		mButtonmaj = (Button) findViewById(R.id.mButtonmaj);
    	
		mButtonmaj.setOnClickListener(new OnClickListener() 
  		{
				@Override
				public void onClick(View arg0) 
				{
					////////////////////////////////////////////////////////////////////////////////////
					////////////////////////////////////////////////////////////////////////////////////
					////////////////////////////////////////////////////////////////////////////////////
					//location stuff
					mLocationProvider = new LocationProvider(MapContribActivity.this);
					// FBO change false to true
					mUpdateLocationRequested = true;
					startLocationUpdate();
					
					////////////////////////////////////////////////////////////////////////////////////
					////////////////////////////////////////////////////////////////////////////////////
					////////////////////////////////////////////////////////////////////////////////////
					updateLocation();
				}
  		});
		
		mButtonsave.setOnClickListener(new OnClickListener() 
  		{
				@Override
				public void onClick(View arg0) 
				{
					//recupere notre position actuel
					
					if(MainActivity.mLastLocation != null)
					{
						ListChampsNoticeModif.cPref.edit().putString(ListChampsNoticeModif.LATITUDE,""+MainActivity.mLastLocation.getLatitude()).commit();
						ListChampsNoticeModif.cPref.edit().putString(ListChampsNoticeModif.LONGITUDE,""+MainActivity.mLastLocation.getLongitude()).commit();
					}
					else
					{
						showErreurLocalisation();
					}
					
					setResult(RESULT_OK);//pour fermer l'activité ListChampsNoticeModif précédente
					Intent intent = new Intent(getApplication(), ListChampsNoticeModif.class);
		        	intent.putExtras(bundle);
		  			startActivity(intent);
					finish();
				}
  		});
		
		if (googleMap!=null)
		{
			googleMap.setMyLocationEnabled(true); //-- generating error message
			if ((MainActivity.mLastLocation != null) && !focusOnNotice())
			{
				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(MainActivity.mLastLocation.getLatitude(),
						MainActivity.mLastLocation.getLongitude()), 4.0f));
			}
			LatLng oeuvre = null;

//TODO FBO inversion pb A REVOIR
			  latitude = bundle.getDouble(ListChampsNoticeModif.LATITUDE);
			  longitude   = bundle.getDouble(ListChampsNoticeModif.LONGITUDE);


			Log.d(DEBUG_TAG, "value of longitude =" +String.valueOf(longitude)+";"+ String.valueOf(latitude));
			if ((longitude != 0.0) && (latitude != 0.0))
			{
				 oeuvre = new LatLng(latitude,longitude);

				Marker loco = googleMap.addMarker(new MarkerOptions().position(oeuvre));//miss the setTitle()
				loco.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
			}

		}//fin googleMap != null
		
		//pour autoriser le retour en cliquant sur l'icone de l'application dans l'action bar
		////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////
		//ACTION BAR
		ActionBar actionBar = getActionBar();
		if (actionBar != null)
		{
			actionBar.show();
			actionBar.setTitle(getResources().getString(R.string.Contribuer));
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		
    }
    

	protected void showErreurLocalisation()
	{
		Toast.makeText(this, this.getResources().getString(R.string.desole_position_pas_recup), Toast.LENGTH_SHORT).show();
	}


	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				AtlasError.showErrorDialog(MapContribActivity.this, "4.3", "");
				//Toast.makeText(getApplicationContext(),"Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
			}
		}
	}
	

	private Boolean focusOnNotice(){
		if (bundle == null) return false;
		try {
			int v = bundle.getInt(SearchActivity.MAP_FOCUS_NOTICE);
			if (v == 1) return true;
			else return false;
		} catch(Exception e){
			return false;
		}
	}
	

	private void updateLocation() 
	{
		mUpdateLocationRequested = true;
		Log.d(DEBUG_TAG, "updateLocation");
	}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		
		if(itemId == android.R.id.home)//quand on clique sur l'icone de l'application dans la barre Action Bar
		{
			setResult(RESULT_CANCELED);//pour fermer l'activite precedente
			Intent intent = new Intent(this, ListChampsNoticeModif.class);
	    	intent.putExtras(bundle);
	        startActivity(intent);
			finish();
			return true;
		}
		else return false;
    }
    

	// dealing with GPS Location
	private void startLocationUpdate()
	{
		mLocationProvider.startPeriodicUpdates();
		Log.d(DEBUG_TAG, "startLocationUpdate");
	}
	public void onLocationUpdated( Location location ) {
		Log.d(DEBUG_TAG, "onLocationUpdated");
		if( location == null ) 
		{
			Log.d(DEBUG_TAG, "onLocationUpdated returning");
			return;
		}
		if( mUpdateLocationRequested ) {
			mUpdateLocationRequested = false;
			MainActivity.mLastLocation = new LocationStruct(location);
			Toast.makeText(getApplicationContext(),
					"Coordonnées GPS : " + 
							 Double.toString(MainActivity.mLastLocation.getLongitude()) + ", " +Double.toString(MainActivity.mLastLocation.getLatitude())
							, Toast.LENGTH_SHORT).show();
			Log.d(DEBUG_TAG, "Location updated");
			Log.d(DEBUG_TAG,"Latitude :" + Double.toString(MainActivity.mLastLocation.getLatitude()));
			Log.d(DEBUG_TAG,"Longitude :" + Double.toString(MainActivity.mLastLocation.getLongitude()));
//			mButtonRefresh.clearAnimation();
			
		}
	}
}
