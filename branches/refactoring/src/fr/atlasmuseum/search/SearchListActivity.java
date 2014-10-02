package fr.atlasmuseum.search;

import java.util.ArrayList;
import java.util.Collections;

import fr.atlasmuseum.R;
import fr.atlasmuseum.search.module.SimpleAdapterSearch;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Liste l'ensemble des auteurs ou pays ou villes
 */
@SuppressLint("DefaultLocale")
public class SearchListActivity extends Activity  {
	
	private static final String DEBUG_TAG = "AtlasMuseum/SearchListActivity";

	private String mChamps;
	private SimpleAdapterSearch mAdapter;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.search_list_activity);
        Log.d(DEBUG_TAG, "onCreate");

        Bundle mBundle = getIntent().getExtras();
        mChamps = mBundle.getString(SearchActivity.CHAMPS_ITEM, "artiste");
        
        ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.show();
			actionBar.setTitle(this.getResources().getString(R.string.Search));
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		ArrayList<String> listElement = new ArrayList<String>();
		for (int i = 0 ; i < SearchActivity.db.nbentries ; i++) {
			String element = SearchActivity.extractDataFromDb(i, mChamps);
			if( element.equals("?") ) {
				continue;
			}
			if( mChamps.equals("Siteville") && element.equals("Ville") ) {
				continue;
			}
			if( mChamps.equals("Sitepays") && element.equals("Pays") ) {
				continue;
			}
			if( ! listElement.contains(element) ) {
				listElement.add(element);
			}
		}
		Collections.sort(listElement);
		mAdapter = new SimpleAdapterSearch(this, listElement);
		
        ListView listView = (ListView) findViewById(R.id.list_view);
		listView.setFastScrollEnabled(true);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
				String selectedItem = (String)mAdapter.getItem(position);
		    	int j = 0;
		    	Bundle bundle = new Bundle();
		    	for (int i = 0 ; i < SearchActivity.db.nbentries ; i++) {	
		    		String value = SearchActivity.extractDataFromDb(i,mChamps);
		    		if ((value != null) && (value.equals(selectedItem))){
		    			bundle.putInt(Integer.toString(j),i);
		    			j++;
		    		}
		    	}
		    	bundle.putInt(SearchActivity.NB_ENTRIES,j);
		    	Intent intent = new Intent(SearchListActivity.this, ResultActivity.class);
		    	intent.putExtras(bundle);
		    	startActivity(intent);
			}
		});
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
}
