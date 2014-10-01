package fr.atlasmuseum.search;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener;

import fr.atlasmuseum.AtlasmuseumActivity;
import fr.atlasmuseum.R;
import fr.atlasmuseum.helper.AtlasError;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MapActivity extends Activity {
	private static final String DEBUG_TAG = "AtlasMuseum/MapActivity";
	private Bundle mBundle = null;
    private GoogleMap mGoogleMap = null;
    private ClusterManager<MyItem> mClusterManager = null;

    private class MyItem implements ClusterItem {
        private final LatLng mPosition;
        private int mPos;
        
		public MyItem(double lat, double lng, int num) {
            mPosition = new LatLng(lat, lng);
            mPos = num;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }
        
		public int getPos() {
			return mPos;
		}
    }
    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		mBundle = getIntent().getExtras();

		if (mGoogleMap == null) {
			mGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

			// check if map is created successfully or not
			if (mGoogleMap == null) {
				AtlasError.showErrorDialog(MapActivity.this, "4.3", "");
			}
		}
		
		if (mGoogleMap != null) {
	        // Position the map.
	    	if ((AtlasmuseumActivity.mLastLocation != null) && !focusOnNotice()){
	    		mGoogleMap.moveCamera(
	    				CameraUpdateFactory.newLatLngZoom(
	    						new LatLng(AtlasmuseumActivity.mLastLocation.getLatitude(),
	    								   AtlasmuseumActivity.mLastLocation.getLongitude()),
	    								   4.0f));
	    	}

	        // Initialize the manager with the context and the map.
	        // (Activity extends context, so we can pass 'this' in the constructor.)
	        mClusterManager = new ClusterManager<MyItem>(this, mGoogleMap);

	        // Point the map's listeners at the listeners implemented by the cluster manager.
	        mGoogleMap.setOnCameraChangeListener(mClusterManager);
	        mGoogleMap.setOnMarkerClickListener(mClusterManager);

	        // Add cluster items (markers) to the cluster manager.
	        // Add clusters
			for (int idx = 0; idx < mBundle.getInt(SearchActivity.NB_ENTRIES); idx++) {
				int idxloc = getEntryNumberForFragment(idx);
				try {
					double longi = Double.parseDouble(SearchActivity.extractDataFromDb(idxloc,"longitude"));
					double lati = Double.parseDouble(SearchActivity.extractDataFromDb(idxloc,"latitude"));
					if ((longi != 0.0) && (lati != 0.0)){
						 new LatLng(longi, lati);
						 MyItem offsetItem = new MyItem(lati, longi, idxloc);
						 mClusterManager.addItem(offsetItem);
					}
				}
				catch(Exception e){
					//AtlasError.showErrorDialog(MapActivity.this, "4.2", "");
	                Log.d(DEBUG_TAG, "Issue with double conversion");
	            }
			}
			
			// Zoom if only one marker
			if(  mBundle.getInt(SearchActivity.NB_ENTRIES) == 1 ) {
				int idxloc = getEntryNumberForFragment(0);
				try {
					double latitutde = Double.parseDouble(SearchActivity.extractDataFromDb(idxloc,"latitude"));
					double longitude = Double.parseDouble(SearchActivity.extractDataFromDb(idxloc,"longitude"));
					if ((longitude != 0.0) && (latitutde != 0.0)){
						mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitutde,longitude), 12.0f));
					}
				}
				catch(Exception e){
					Log.d(DEBUG_TAG, "Issue with double conversion");
	            }
			}

			mClusterManager.setOnClusterClickListener(new OnClusterClickListener<MyItem>() {
				@Override
				public boolean onClusterClick(Cluster<MyItem> cluster) {
					return true;
				}
		    });
		    
		    mClusterManager.setOnClusterItemClickListener(new OnClusterItemClickListener<MyItem>() {
				@Override
				public boolean onClusterItemClick(MyItem item) {
					Bundle bundle = new Bundle();
					Intent intent = new Intent(MapActivity.this, ShowNoticeActivity.class);
					bundle.putInt(ShowNoticeActivity.ARG_FRAGMENT, item.getPos());
					intent.putExtras(bundle);
					startActivity(intent);
					return true;
				}
		    });
		}
		
    	
    	//pour autoriser le retour en cliquant sur l'icone de l'application dans l'action bar
		////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////
		//ACTION BAR
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.show();
			String carte_des_oeuvres = getResources().getString(R.string.carte_des_oeuvres);
			actionBar.setTitle(carte_des_oeuvres);
			actionBar.setDisplayShowTitleEnabled(true);
			//actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	private Boolean focusOnNotice() {
		if (mBundle == null) return false;
		try {
			int v = mBundle.getInt(SearchActivity.MAP_FOCUS_NOTICE);
			return (v == 1);
		}
		catch(Exception e) {
			return false;
		}
	}
	
	int getEntryNumberForFragment(int idx) {
		if (mBundle == null) return -1;
		return mBundle.getInt(Integer.toString(idx));
	}
	 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		
		if(itemId == android.R.id.home) {
			onBackPressed();
			finish();
			return true;
		}
		else return false;
    }
    
    @Override
    public void onBackPressed() {
    	setResult(RESULT_OK);
    	finish();
    	super.onBackPressed();
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
	

}
