package fr.atlasmuseum.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import fr.atlasmuseum.AtlasmuseumActivity;
import fr.atlasmuseum.R;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("DefaultLocale")
public class SearchAutoCompleteActivity extends Activity {

	private static final String DEBUG_TAG = "AtlasMuseum/SearchAutoCompleteActivity";
	private static final int REQUEST_VOICE_RECOGNITION = 1;

	private Button mTabTous;
	private Button mTabArtiste;
	private Button mTabLieux;
	private Button mTabTitre;
	
	private EditText mTextSearch;
	private TextView mTextError; 
	private List<String> mListFull;
	private List<String> mListCurrent;
	private ArrayAdapter<String> mAdapter;
	
	
	String mChamps; //champs selectionner parmi: tous, artiste, lieux, titre
	
	public void onCreate(Bundle savedInstanceState) {
		Log.d(DEBUG_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.search_auto_complete_activity);
		
		mTabTous = (Button) findViewById(R.id.tab_tous);
		mTabArtiste = (Button) findViewById(R.id.tab_artiste);
		mTabLieux = (Button) findViewById(R.id.tab_lieux);
		mTabTitre = (Button) findViewById(R.id.tab_titre);
	        
		ListView listView = (ListView) findViewById(R.id.list_view);
		mTextSearch = (EditText) findViewById(R.id.text_search);
		mTextError = (TextView) findViewById(R.id.text_error);
		mTextError.setVisibility(TextView.INVISIBLE);
		
		mChamps = "tous";
		mListFull = new ArrayList<String>();
		mListCurrent = new ArrayList<String>();
		mAdapter = new ArrayAdapter<String>(this,R.xml.my_item_list, mListCurrent);
		listView.setAdapter(mAdapter);

		updateTabs();
		updateListFull();

		mTextSearch.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mListCurrent.clear();
				for(String current: mListFull) {
					if(current.trim().toLowerCase().contains((s.toString().toLowerCase()))) {
						if( ! mListCurrent.contains(current) ) {
							mListCurrent.add(current);
						}
					}
				}
				if(mListCurrent.isEmpty()) {
					mTextError.setText("No result");
					mTextError.setVisibility(TextView.VISIBLE);
				}
				else {
					mTextError.setVisibility(TextView.INVISIBLE);
				}
				mAdapter.notifyDataSetChanged();
			}
		});

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
				Bundle bundle = new Bundle();
				ArrayList<String> properties = new ArrayList<String>();

				switch( mChamps ) {
				case "tous":
					properties.add("artiste");
					properties.add("titre");
					properties.add("Siteville");
					break;
				case "lieux":
					properties.add("Siteregion");
					properties.add("Sitenom");
					properties.add("Siteville");
					break;
				case "artiste":
					properties.add("artiste");
					break;
				case "titre":
					properties.add("titre");
					break;
				}

				int j = 0;
				for (int i = 0; i < SearchActivity.getDB().nbentries; i++) {
					for( String prop: properties ) {
						String value = SearchActivity.extractDataFromDb(i,prop);
						if(value.equals("?")) {
							continue;
						}
						if(value.toLowerCase().contains(mListCurrent.get(position).toLowerCase())) {
							bundle.putInt(Integer.toString(j), i); 
							j++;
						}
					}
				}
				bundle.putInt(SearchActivity.NB_ENTRIES,j);

				Location mLastLocation = AtlasmuseumActivity.mLastLocation;
				if (mLastLocation != null) {
					bundle.putDouble(SearchActivity.CURRENT_LAT,mLastLocation.getLatitude());
					bundle.putDouble(SearchActivity.CURRENT_LONG,mLastLocation.getLongitude());
				}
				else {
					//	Toast.makeText(getApplicationContext(),
					//	"Position GPS inconnue", Toast.LENGTH_SHORT).show();
					bundle.putDouble(SearchActivity.CURRENT_LAT,0.0);
					bundle.putDouble(SearchActivity.CURRENT_LONG,0.0);
				}
				Intent intent = new Intent(SearchAutoCompleteActivity.this, ResultActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});	


		ImageButton buttonVoice = (ImageButton) findViewById(R.id.button_voice);
		buttonVoice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources().getString(R.string.app_name));
				startActivityForResult(intent, REQUEST_VOICE_RECOGNITION);
			}
		});
			
			
		ImageButton buttonErase = (ImageButton) findViewById(R.id.button_erase);
		buttonErase.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTextSearch.setText("");
			}
		});

			
		mTabTous.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(DEBUG_TAG, "Click on tab Tous");
				mChamps = "tous";
				getActionBar().setTitle(getResources().getString(R.string.search_all));
				updateTabs();
				updateListFull();
			}
		});
		
		mTabArtiste.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(DEBUG_TAG, "Click on tab Artiste");
				mChamps = "artiste";
				getActionBar().setTitle(getResources().getString(R.string.search_by_artist));
				updateTabs();
				updateListFull();
			}
		});

		mTabLieux.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(DEBUG_TAG, "Click on tab Lieux");
				mChamps = "lieux";
				getActionBar().setTitle(getResources().getString(R.string.search_by_place));
				updateTabs();
				updateListFull();
			}
		});

		mTabTitre.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(DEBUG_TAG, "Click on tab Titre");
				mChamps = "titre";
				getActionBar().setTitle(getResources().getString(R.string.search_by_title));
				updateTabs();
				updateListFull();
			}
		});

		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.show();
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowTitleEnabled(true);
			//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
		}
	}
	 
	 void updateListFull() {
		 mListFull.clear();
		 mListCurrent.clear();
		 mAdapter.notifyDataSetChanged();
		 
		 int titleId = -1;
		 ArrayList<String> properties = new ArrayList<String>();
		 
		 switch( mChamps ) {
		 case "tous":
			 titleId = R.string.search_all;
			 properties.add("artiste");
			 properties.add("titre");
			 properties.add("Siteville");
			 break;
		 case "lieux":
			 titleId = R.string.search_by_place;
			 properties.add("Siteregion");
			 properties.add("Sitenom");
			 properties.add("Siteville");
			 break;
		 case "artiste":
			 titleId = R.string.search_by_artist;
			 properties.add("artiste");
			 break;
		 case "titre":
			 titleId = R.string.search_by_title;
			 properties.add("titre");
			 break;
		 }

		 getActionBar().setTitle(this.getResources().getString(titleId));
		 for (int idx = 0; idx < SearchActivity.getDB().nbentries; idx++) {
			 for( String prop: properties ) {
				 String value = SearchActivity.extractDataFromDb(idx,prop);
				 if(value.equals("?")) {
					 continue;
				 }
				 mListFull.add(value.trim());
			 }
		 }
		 Collections.sort(mListFull);
	 }

	 void updateTabs() {
		 mTabTous.setTextColor(Color.parseColor("#686868"));
		 mTabArtiste.setTextColor(Color.parseColor("#686868"));
		 mTabTitre.setTextColor(Color.parseColor("#686868"));
		 mTabLieux.setTextColor(Color.parseColor("#686868"));
		 mTabTous.setClickable(true);
		 mTabLieux.setClickable(true);
		 mTabTitre.setClickable(true);
		 mTabArtiste.setClickable(true);
		 
		 switch(mChamps) {
		 case "tous":
			 mTabTous.setClickable(false);
			 mTabTous.setTextColor(Color.parseColor("#ffda00"));
			 break;
		 case "lieux":
			 mTabLieux.setClickable(false);
			 mTabLieux.setTextColor(Color.parseColor("#ffda00"));
			 break;
		 case "titre":
			 mTabTitre.setClickable(false);
			 mTabTitre.setTextColor(Color.parseColor("#ffda00"));
			 break;
		 case "artiste":
		 default:
			 mChamps = "artiste";
			 mTabArtiste.setClickable(false);
			 mTabArtiste.setTextColor(Color.parseColor("#ffda00"));
			 break;
		 }
	}
	 
	 
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 super.onActivityResult(requestCode, resultCode, data);
		 switch (requestCode) {
		 case REQUEST_VOICE_RECOGNITION:
			 if(resultCode == RESULT_OK) {
				 ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				 String m = matches.get(0);
				 Toast.makeText(this,m, Toast.LENGTH_LONG).show();
				 mTextSearch.setText(m);
			 }
			 break;
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
		else return false;
    }
}
