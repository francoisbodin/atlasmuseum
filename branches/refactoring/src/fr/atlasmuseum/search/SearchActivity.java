package fr.atlasmuseum.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

import fr.atlasmuseum.R;
import fr.atlasmuseum.data.JsonRawData;
import fr.atlasmuseum.helper.AtlasError;
import fr.atlasmuseum.search.module.SimpleAdapterSearch;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

public class SearchActivity extends Activity implements ActionBar.TabListener {

	private static final String DEBUG_TAG = "AtlasMuseum/SearchActivity";
	
	static final String NB_ENTRIES = "nbentries";
	static final String CURRENT_LAT = "curlat";
	static final String CURRENT_LONG = "curlong";
	public static final String MAP_FOCUS_NOTICE = "mapfocusnotice";
	static final String CHAMPS_ITEM = "champs_select";
	static public JsonRawData db = null;
	
	private static final String ATLASMUSEUM_ALBUM = "atlasmuseum";
	
	private ListView mListView;
	private SimpleAdapterSearch mAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.simple_list_layout);

		Log.d(DEBUG_TAG, "onCreate");
		
		mListView = (ListView) findViewById(R.id.list_view);

		// test si la bdd pas déjà chargée, risque de doubler la bdd sinon
		if(db == null) {
			// Load the data from the internal DB
			try	{
				db = new JsonRawData();
			}
			catch (JSONException e) {
				AtlasError.showErrorDialog(this, "6.1", "");// ERROR "impossible de charger la base de donnée"
			}
		}

		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			String menu_rechercher = this.getResources().getString(R.string.menu_rechercher);
			actionBar.show();
			actionBar.setTitle(menu_rechercher);
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setHomeButtonEnabled(true);
			//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  

			Drawable search_img = getResources().getDrawable(R.drawable.ic_search);
			Drawable autour_img = getResources().getDrawable(R.drawable.ic_autour);
			Drawable map_img = getResources().getDrawable(R.drawable.ic_map);
			actionBar.addTab(actionBar.newTab().setIcon(search_img).setTabListener(this));//search
			actionBar.addTab(actionBar.newTab().setIcon(autour_img).setTabListener(this));//autour
			actionBar.addTab(actionBar.newTab().setIcon(map_img).setTabListener(this));//map
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			actionBar.selectTab(actionBar.getTabAt(0));
		}
		
		ArrayList<String> selectionStringList = new ArrayList<String>();
		selectionStringList.add(getResources().getString(R.string.search_all));//0
		selectionStringList.add(getResources().getString(R.string.recherche_date));//1
		selectionStringList.add(getResources().getString(R.string.list_author));//2
		selectionStringList.add(getResources().getString(R.string.list_city));//3
		selectionStringList.add(getResources().getString(R.string.list_country));//4
		mAdapter = new SimpleAdapterSearch(this, selectionStringList);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id)	{
				Bundle bundle = new Bundle();
				Intent intent = null;
				switch(position) {
				case 0:
					getActionBar().setSelectedNavigationItem(0);
					intent = new Intent(SearchActivity.this, SearchAutoCompleteActivity.class);
					break;

				case 1:
					getActionBar().setSelectedNavigationItem(0);
					intent = new Intent(SearchActivity.this, SearchByDateActivity.class);
					break;

				case 2:
					bundle.putString(SearchActivity.CHAMPS_ITEM, "artiste");
					intent = new Intent(SearchActivity.this, SearchListActivity.class);
					break;
				case 3:
					bundle.putString(SearchActivity.CHAMPS_ITEM, "Siteville");
					intent = new Intent(SearchActivity.this, SearchListActivity.class);
					break;
				case 4:
					bundle.putString(SearchActivity.CHAMPS_ITEM, "Sitepays");
					intent = new Intent(SearchActivity.this, SearchListActivity.class);
					break;
				}
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});	
	}

	@Override
	protected void onDestroy() {
		Log.d(DEBUG_TAG, "onDestroy");
		super.onDestroy();
	}	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		if(itemId == android.R.id.home)	{
			super.onBackPressed();
			finish();
			return true;
		}
		else return false;
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		Intent intent = null;
		switch(tab.getPosition()) {
		case 0: //rechercher
			break;
		case 1:	//autour de moi
			getActionBar().setSelectedNavigationItem(0);
			intent = new Intent(this, SearchAroundActivity.class);
			startActivity(intent);
			break;
		case 2: //map
			getActionBar().setSelectedNavigationItem(0);
			Bundle bundle = new Bundle();
			for(int i = 0 ; i < SearchActivity.db.nbentries ; i++) {
				bundle.putInt(Integer.toString(i), i);
			}
			bundle.putInt(SearchActivity.NB_ENTRIES, SearchActivity.db.nbentries);
			intent = new Intent(getApplication(), MapActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// Do Nothing
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// Do Nothing
	}
	
	///////////////////////////// helper routines  //////////////
	static public String extractDataFromDb(int index, String field)
	{
		JSONObject obj = null; 
		//Log.d(DEBUG_TAG, "extractDataFromDb");
		try {
			obj = db.data.getJSONObject(index);
		} catch (JSONException e) 
		{
			return "?";
		}
		try {
			return obj.getString(field);
		} catch (JSONException e)
		{
			return "?";
		}
	}


	static double haversine_meter(double lat1, double long1, double lat2, double long2)
	{
		final double d2r= 0.0174532925199433;
		double dlong = (long2 - long1) * d2r;
		double dlat = (lat2 - lat1) * d2r;
		double a = Math.pow(Math.sin(dlat/2.0), 2) + Math.cos(lat1*d2r) * Math.cos(lat2*d2r) * Math.pow(Math.sin(dlong/2.0), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double d = 6367 * c;

		return d*1000.0;
	}

}

