package fr.atlasmuseum.search;

import java.util.ArrayList;
import java.util.Collections;

import fr.atlasmuseum.AtlasmuseumActivity;
import fr.atlasmuseum.R;
import fr.atlasmuseum.search.module.SimpleAdapterSearch;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * Liste l'ensemble des auteurs ou pays ou villes
 *
 */
@SuppressLint("DefaultLocale")
public class ListActivity extends Activity  {
	
	private static final String DEBUG_TAG = "AtlasMuseum/ListActivity";

	private Bundle mBundle ;
	String mChamps; // champs sur lequel porte la recherche: ex artiste, pays, villes...
	private SimpleAdapterSearch mAdapter;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.search_activity_list);
        Log.d(DEBUG_TAG, "onCreate");

        mBundle = getIntent().getExtras();
        mChamps = mBundle.getString(SearchActivity.CHAMPS_ITEM, "artiste");//quand on arrive sur l'activité, rechercher par défaut: artiste
		
        TextView textTitle = (TextView) findViewById(R.id.titlelist);
        textTitle.setText(Integer.toString(getNumberOfEntries()) + " ELEMENTS TROUVES"); // TODO: Resourcify this string
        
        ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.show();
			actionBar.setTitle(this.getResources().getString(R.string.menu_rechercher));
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		ArrayList<String> listElement = new ArrayList<String>();
		for (int idx = 0; idx < getNumberOfEntries(); idx++) {
			int idxloc = getEntryNumberForFragment(idx);
			String element = SearchActivity.extractDataFromDb(idxloc, mChamps);
			listElement.add(element);
		}
		
		Collections.sort(listElement);
		mAdapter = new SimpleAdapterSearch(this, listElement);
		
        ListView listView =  (ListView) findViewById(R.id.list_view);
		listView.setFastScrollEnabled(true);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
				showList(mChamps, (String)mAdapter.getItem(position)); //important !! gere le bundle
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
    
  /**
   * fonction de recherche de cText dans le champs c
   * modifie le bundle, en fonction du nb resultat trouvé
   * ajoute les info de localisation
   * @param c champ: ex artiste, lieux, tous_les_artistes ..;
   * @param cText chaine à chercher dans la BDD interne du telephone
   */
    private void showList(String c, String cText) {
    	int j = 0;
    	Bundle extra = new Bundle();
    	for (int i = 0 ; i < SearchActivity.db.nbentries ; i++) {	
    		String value = SearchActivity.extractDataFromDb(i,c);
    		if ((value != null) && (value.toLowerCase().contains(cText.toLowerCase()))){
    			extra.putInt(Integer.toString(j),i); 
    			j++;
    		}
    	}

    	Location mLastLocation = AtlasmuseumActivity.mLastLocation;	
    	if (mLastLocation != null) {
    		extra.putDouble(SearchActivity.CURRENT_LAT, mLastLocation.getLatitude());
    		extra.putDouble(SearchActivity.CURRENT_LONG, mLastLocation.getLongitude());
    	}
    	else {
    		//	Toast.makeText(getApplicationContext(),
    		//			"Position GPS inconnue", Toast.LENGTH_SHORT).show();
    		extra.putDouble(SearchActivity.CURRENT_LAT, 0.0);
    		extra.putDouble(SearchActivity.CURRENT_LONG, 0.0);
    	}
    	extra.putInt(SearchActivity.NB_ENTRIES,j);
    	Intent intent = new Intent(getApplication(), ResultActivity.class);
    	intent.putExtras(extra);
    	startActivity(intent);

    }

	///////////////////////
	/// HELPER ROUTINES ///
	///////////////////////
	
    int getEntryNumberForFragment(int idx){
    	if (mBundle == null) return -1;
    	return mBundle.getInt(Integer.toString(idx));
    }

    int getNumberOfEntries(){
    	int v = mBundle.getInt(SearchActivity.NB_ENTRIES);
    	return v;
    }

}
