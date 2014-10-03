package fr.atlasmuseum.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import fr.atlasmuseum.AtlasmuseumActivity;
import fr.atlasmuseum.R;
import fr.atlasmuseum.contribution.Contribution;
import fr.atlasmuseum.helper.AtlasError;
import fr.atlasmuseum.helper.PictureDownloader.PictureDownloaderListener;
import fr.atlasmuseum.search.module.NoticeAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchAroundActivity extends Activity
                                  implements PictureDownloaderListener {

	private final String DEBUG_TAG = "AtlasMuseum/SearchAroundActivity";
	
	public static final String ARG_FRAGMENT = "IDFragment";
	
	private ListView mListView;
	private NoticeAdapter mAdapter;
	private List<Contribution> mNoticeList;

	public void onCreate(Bundle savedInstanceState)	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.simple_list_layout);
		Log.d(DEBUG_TAG, "onCreate");
		
		mListView = (ListView) findViewById(R.id.list_view);

		mNoticeList = new ArrayList<Contribution>();
		Location mLastLocation = AtlasmuseumActivity.mLastLocation;
		if (mLastLocation != null) {
			double lat1 = mLastLocation.getLatitude();
			double long1 = mLastLocation.getLongitude();
			for (int idxloc = 0; idxloc < SearchActivity.db.nbentries; idxloc++) {
				Contribution contribution = new Contribution();
				contribution.updateFromDb(idxloc);
		    	
				double latitude = 0;
				double longitude = 0;
				
				try {
					latitude = Double.parseDouble(contribution.getProperty(Contribution.LATITUDE).getValue());
					longitude = Double.parseDouble(contribution.getProperty(Contribution.LONGITUDE).getValue());
				}
				catch(NumberFormatException e) {
					Log.d(DEBUG_TAG, "issue with location");
					continue;
				}

				double distance = haversine_meter(lat1, long1, latitude, longitude);
				double maxDistance = 50000;
				
				if (distance > maxDistance) {
					continue;
				}
				
				contribution.setDistance((int)distance);
				mNoticeList.add(contribution);
			}

			Collections.sort(mNoticeList, Contribution.DistanceComparator);
		}
		else {
			Log.d(DEBUG_TAG, "mLastLocation null");
			AtlasError.showErrorDialog(SearchAroundActivity.this, "8.1", "impossible de récupérer votre position pour le moment.");
		}

		mAdapter = new NoticeAdapter(this, mNoticeList);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				Contribution notice = (Contribution) mListView.getItemAtPosition(position);
				Bundle bundle = new Bundle();
				bundle.putInt(ARG_FRAGMENT, notice.getDbId() );
				Intent intent = new Intent(SearchAroundActivity.this, ShowNoticeActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.show();
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(this.getResources().getString(R.string.title_autour_de_moi));
			actionBar.setDisplayShowTitleEnabled(true);
			//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.discovart, menu);
		return super.onCreateOptionsMenu(menu);
	}

	double haversine_meter(double lat1, double long1, double lat2, double long2) {
		final double d2r = 0.0174532925199433;
		double dlong = (long2 - long1) * d2r;
		double dlat = (lat2 - lat1) * d2r;
		double a = Math.pow(Math.sin(dlat/2.0), 2) + Math.cos(lat1*d2r) * Math.cos(lat2*d2r) * Math.pow(Math.sin(dlong/2.0), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double d = 6367 * c;
		return d*1000.0;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		if(itemId == android.R.id.home)	{
			finish();
			return true;
		}
		else if(itemId == R.id.action_map) {
			Intent intent = new Intent(SearchAroundActivity.this, MapActivity.class);
			Bundle bundle = new Bundle();
			int i = 0;
			for( Contribution notice: mNoticeList) {
				bundle.putInt(Integer.toString(i), notice.getDbId());
				i++;
			}
			bundle.putInt(SearchActivity.NB_ENTRIES, mNoticeList.size());
			intent.putExtras(bundle);
			startActivity(intent);
			return true;
		}
		else return false;

	}

	@Override
	public void onBackPressed() {
		finish();
	}
	
	@Override
	public void onPictureDownloaded(String filename) {
		mAdapter.notifyDataSetChanged();
	}

}
