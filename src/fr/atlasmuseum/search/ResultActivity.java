package fr.atlasmuseum.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import fr.atlasmuseum.R;
import fr.atlasmuseum.contribution.Contribution;
import fr.atlasmuseum.helper.PictureDownloader.PictureDownloaderListener;
import fr.atlasmuseum.search.module.NoticeAdapter;
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
import android.widget.AdapterView.OnItemClickListener;

public class ResultActivity extends Activity implements PictureDownloaderListener {
	
	private static final String DEBUG_TAG = "AtlasMuseum/ResultActivity";

	private Bundle mBundle;
	private ListView mListView;
	
	NoticeAdapter mAdapter;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.simple_list_layout);
        Log.d(DEBUG_TAG, "onCreate()");
        
        mBundle = getIntent().getExtras();
        
        List<Contribution> listNotice = new ArrayList<Contribution>();
        
        for (int idx = 0; idx < getNumberOfEntries() ; idx++ ) {
			int idxloc = mBundle.getInt(Integer.toString(idx));
			Contribution contribution = new Contribution();
			contribution.updateFromDb(idxloc);
			listNotice.add(contribution);
		}
		Collections.sort(listNotice, Contribution.DateComparator);

		mAdapter = new NoticeAdapter(this, listNotice);

        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
         	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				Contribution contribution = (Contribution) mListView.getItemAtPosition(position);
				Bundle bundle = new Bundle();
				bundle.putInt(ShowNoticeActivity.ARG_FRAGMENT, contribution.getDbId() );
    			Intent intent = new Intent(ResultActivity.this, ShowNoticeActivity.class);
    			intent.putExtras(bundle);
    			startActivity(intent);
    			
        	}
         });
 
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.show();
			actionBar.setTitle("Rechercher");
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowTitleEnabled(true);
			//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
		}
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		
		if(itemId == android.R.id.home)	{
			super.onBackPressed();
			finish();
			return true;
		}
		
		if(itemId == R.id.action_map) {
			Intent intent = new Intent(ResultActivity.this,MapActivity.class);
			if (getNumberOfEntries() == 1) {
				mBundle.putInt(SearchActivity.MAP_FOCUS_NOTICE,1);
			}
			intent.putExtras(mBundle);
			startActivity(intent);
			return true;
		}
		else return false;
    	
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_map, menu);
        return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onPictureDownloaded(String filename) {
		mAdapter.notifyDataSetChanged();
	}

	///////////////////////
	/// HELPER ROUTINES ///
	///////////////////////
	
	
	int getNumberOfEntries(){
		int v = mBundle.getInt(SearchActivity.NB_ENTRIES);
		Log.d(DEBUG_TAG, "getNumberOfEntries : " + v);
		return v;
	}
	
}
