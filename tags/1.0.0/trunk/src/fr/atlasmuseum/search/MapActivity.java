package fr.atlasmuseum.search;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener;
import com.irisa.unpourcent.location.LocationStruct;
import com.irisa.unpourcent.location.LocationProvider;
import com.irisa.unpourcent.location.LocationUtils;

import fr.atlasmuseum.R;
import fr.atlasmuseum.main.AtlasError;
import fr.atlasmuseum.main.MainActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class MapActivity extends Activity {
	private static final String DEBUG_TAG = "UnPourCent/MapActivity";
	private Bundle bundle = null;
    private GoogleMap googleMap = null;
    private Context ContextApps = null;
    private ClusterManager<MyItem> mClusterManager = null;
    GoogleMap getMap(){
    	return googleMap;
    }
    
    private class MyItem implements ClusterItem {
        private final LatLng mPosition;
        private int Pos;	
        public int getPos() {
			return Pos;
		}

		public void setPos(int pos) {
			Pos = pos;
		}

		public MyItem(double lat, double lng, int num) {
            mPosition = new LatLng(lat, lng);
            Pos = num;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }
        String getTitle(){
        	return "4";
        }
    }
    
    private void addItems() {
        // Add clusters
        int idx;
		LatLng oeuvre = null;
		for (idx = 0; idx < getNumberOfEntries(); idx++){
			int idxloc = getEntryNumberForFragment(idx);
			Log.i(DEBUG_TAG, "idxloc inside MAP is "+idxloc);
			try {
				double longi = Double.parseDouble(SearchActivity.extractDataFromDb(idxloc,"longitude"));
				double lati = Double.parseDouble(SearchActivity.extractDataFromDb(idxloc,"latitude"));
				Log.d(DEBUG_TAG, "value of longitude =" +String.valueOf(longi)+";"+ String.valueOf(lati));
				if ((longi != 0.0) && (lati != 0.0)){
					 oeuvre = new LatLng(longi, lati);
					 MyItem offsetItem = new MyItem(lati, longi,idxloc);
					 mClusterManager.addItem(offsetItem);
				}
			} catch(Exception e){
				//AtlasError.showErrorDialog(MaQpActivity.this, "4.2", "");
                Log.d(DEBUG_TAG, "Issue with double conversion");
            }
		}
    }
    
    private void setUpClusterer() {
        // Position the map.
    	if ((MainActivity.mLastLocation != null) && !focusOnNotice()){
    		getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(MainActivity.mLastLocation.getLatitude(),
    				MainActivity.mLastLocation.getLongitude()), 4.0f));
    	}

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyItem>(this, getMap());

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        getMap().setOnCameraChangeListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }
    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(DEBUG_TAG, "onCreateView");
		
		setContentView(R.layout.activity_map);
		bundle = getIntent().getExtras();
		initilizeMap();
		
		//tester si GPS activé??
		
		
		if (getMap()!=null){
			setUpClusterer();
		
			//if ((oeuvre != null)&& focusOnNotice()){
			//	getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(oeuvre, 12.0f));
			//}
		    //THERE IS A BUG WITH THIS CODE
		    mClusterManager.setOnClusterClickListener(new OnClusterClickListener<MyItem>()
		    {
				@Override
				public boolean onClusterClick(Cluster<MyItem> cluster) {
					//Toast.makeText(getApplicationContext(),"in onClusterClick", Toast.LENGTH_SHORT).show();
					return true;
				}
		    });  
		    mClusterManager.setOnClusterItemClickListener(new OnClusterItemClickListener<MyItem>(){

				@Override
				public boolean onClusterItemClick(MyItem item) {
					// TODO Auto-generated method stub
					//Toast.makeText(getApplicationContext(),"in setOnClusterItemClickListener", Toast.LENGTH_SHORT).show();
					showNotice(item.getPos());
					return false;
				}
		    });
		  
		    
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
			String carte_des_oeuvres = getResources().getString(R.string.carte_des_oeuvres);
			actionBar.setTitle(carte_des_oeuvres);
			actionBar.setDisplayShowTitleEnabled(true);
			//actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
				}
	}

	private void showAccueil(){
		Intent intent = new Intent(this, SearchActivity.class);
		Log.d(DEBUG_TAG, "Going to Accueil");
		startActivity(intent);
	}
	
	private void showNotice(int position){
		Bundle nbundle = new Bundle();
		Log.d(DEBUG_TAG, "showNotice Trying to display entry:" + position);
//		Toast.makeText(getApplicationContext(),
//				"Looking for entry " +position, Toast.LENGTH_SHORT).show();
//		Log.d(DEBUG_TAG, "Going to Notice");
		Intent intent = new Intent(getApplication(), ShowNoticeActivity.class);
		nbundle.putInt(SearchActivity.NB_ENTRIES,1);
		nbundle.putDouble(SearchActivity.CURRENT_LAT,0.0);
		nbundle.putDouble(SearchActivity.CURRENT_LONG,0.0);
		//nbundle.putInt("0",position);
		nbundle.putInt(ShowNoticeActivity.ARG_FRAGMENT,position);
		intent.putExtras(nbundle);
		startActivity(intent);
	}

	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				AtlasError.showErrorDialog(MapActivity.this, "4.3", "");
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
		Log.d(DEBUG_TAG, "getNumberOfEntries : " + v);
		return v;
	}
	
	Double getCurrentLongitude(){
		Double v = bundle.getDouble(SearchActivity.CURRENT_LONG );
		Log.d(DEBUG_TAG, "getNumberOfEntries : " + v);
		return v;
	}

	
	////KEEP THIS CODE FOR LATER
	public class MyWebViewClient extends WebViewClient {        
		/* (non-Java doc)
		 * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
		 */


		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.endsWith(".mp4")) 
			{
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse(url), "video/*");

				view.getContext().startActivity(intent);
				return true;
			} 
			else {
				return super.shouldOverrideUrlLoading(view, url);
			}
		}
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		
		if(itemId == android.R.id.home)//quand on clique sur l'icone de l'application dans la barre Action Bar
		{
			onBackPressed();
			finish();
			return true;
		}
		else return false;
    }
    
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	if(bundle.containsKey(SearchActivity.getCOME_FROM_SEARCHACT()))
    	{
    		Intent intent= new Intent(this,SearchActivity.class);
			startActivity(intent);
			finish();
    	}
    	else
    	{
    		super.onBackPressed();
    	}
    	
    }
}
