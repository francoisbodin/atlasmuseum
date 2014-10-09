package fr.atlasmuseum.search;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

import fr.atlasmuseum.R;
import fr.atlasmuseum.data.JsonRawData;
import fr.atlasmuseum.helper.AtlasError;
import fr.atlasmuseum.search.module.SimpleAdapterSearch;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

public class SearchActivity extends Activity {

	private static final String DEBUG_TAG = "AtlasMuseum/SearchActivity";
	
	static final String NB_ENTRIES = "nbentries";
	static final String CURRENT_LAT = "curlat";
	static final String CURRENT_LONG = "curlong";
	public static final String MAP_FOCUS_NOTICE = "mapfocusnotice";
	static final String CHAMPS_ITEM = "champs_select";
	static public JsonRawData db = null;
	
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
					intent = new Intent(SearchActivity.this, SearchAutoCompleteActivity.class);
					break;

				case 1:
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
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		Intent intent = null;
		switch(itemId) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_search_around:
			intent = new Intent(this, SearchAroundActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_search_map:
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
		return false;
	}
	
	///////////////////////////// helper routines  //////////////
	static public String extractDataFromDb(int index, String field)	{
		try {
			JSONObject obj = JsonRawData.data.getJSONObject(index);
			return obj.getString(field);
		}
		catch (JSONException e) {
			return "?";
		}
	}


	static double haversine_meter(double lat1, double long1, double lat2, double long2)	{
		final double d2r= 0.0174532925199433;
		double dlong = (long2 - long1) * d2r;
		double dlat = (lat2 - lat1) * d2r;
		double a = Math.pow(Math.sin(dlat/2.0), 2) + Math.cos(lat1*d2r) * Math.cos(lat2*d2r) * Math.pow(Math.sin(dlong/2.0), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double d = 6367 * c;

		return d*1000.0;
	}

}

