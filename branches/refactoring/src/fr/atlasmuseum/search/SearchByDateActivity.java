package fr.atlasmuseum.search;

import fr.atlasmuseum.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SearchByDateActivity extends Activity {
	private static final String DEBUG_TAG = "AtlasMuseum/SearchByDateActivity";

	private EditText mDateBegin;
	private EditText mDateEnd;
	private Button mButtonSearch;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.search_by_date_activity);
		
		mDateBegin = (EditText) findViewById(R.id.date_begin);
		mDateEnd = (EditText) findViewById(R.id.date_end);
		mButtonSearch = (Button) findViewById(R.id.button_search);
		TextView txt = (TextView) findViewById(R.id.test_info);
		Typeface font_regular = Typeface.createFromAsset(this.getAssets(), "RobotoCondensed-Regular.ttf");
		mDateBegin.setTypeface(font_regular);
		mDateEnd.setTypeface(font_regular);
		txt.setTypeface(font_regular);

		mButtonSearch.setTypeface(font_regular);

		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.show();
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(getResources().getString(R.string.search_by_date));
			actionBar.setDisplayShowTitleEnabled(true);
			//actionBar.setHomeButtonEnabled(true);
		}

		mButtonSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String beginString = mDateBegin.getText().toString().trim();
				String endString = mDateEnd.getText().toString().trim();
				if( beginString.isEmpty() || beginString.equals(""))	{
					Toast.makeText(SearchByDateActivity.this, getResources().getString(R.string.search_check_date_format), Toast.LENGTH_SHORT).show();
					return;
				}

				int beginDate = 0;
				int endDate = 0;
				try {
					beginDate = Integer.parseInt(beginString);
					endDate = Integer.parseInt(endString);

					if ((endDate - beginDate) < 0) {
						Toast.makeText(SearchByDateActivity.this, getResources().getString(R.string.search_check_date_format), Toast.LENGTH_SHORT).show();
						return;
					}
				}
				catch (NumberFormatException nfe) {
					Toast.makeText(SearchByDateActivity.this, getResources().getString(R.string.search_check_date_format), Toast.LENGTH_SHORT).show();
					return;
				}
				
				Bundle bundle = new Bundle();
				int j = 0;
				for (int i = 0 ; i < SearchActivity.getDB().nbentries ; i++)	{
					String date_oeuvre = SearchActivity.extractDataFromDb(i,"inauguration");
					if ((date_oeuvre != null) && !date_oeuvre.equals("?") )	{
						try {
							int date = Integer.parseInt((date_oeuvre.toString()));
							if (date >= beginDate && endDate >= date) {
								bundle.putInt(Integer.toString(j),i);
								j++;
							}
						}
						catch (NumberFormatException nfe) {
							Log.d(DEBUG_TAG, "Bad date format:" + date_oeuvre);
						}
					}
				}
				bundle.putInt(SearchActivity.NB_ENTRIES, j);
				Intent intent = new Intent(SearchByDateActivity.this, ResultActivity.class);
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
