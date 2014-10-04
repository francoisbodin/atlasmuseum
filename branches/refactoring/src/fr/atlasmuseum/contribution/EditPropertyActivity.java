package fr.atlasmuseum.contribution;

import java.util.ArrayList;
import java.util.Arrays;
import fr.atlasmuseum.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class EditPropertyActivity extends Activity {
	
	final static String DEBUG_TAG = "AtlasMuseum/EditPropertyActivity";

	Bundle mBundle;
	ContributionProperty mProp;
	
	EditText mEditText;
	
	String value;
	ListView mEditList;
	private ArrayList<CheckBoxListElement> mCheckList;
	private BaseAdapter mAdapter;
	private TextView mTextInfos;
	private TextView mEditDate;

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.edit_property_activity);

		LinearLayout textLayout = (LinearLayout) findViewById(R.id.text_layout);
		mEditText = (EditText) findViewById(R.id.edit_text);
		mEditDate = (TextView) findViewById(R.id.edit_date); 
		mEditList = (ListView) findViewById(R.id.edit_list);
		mTextInfos = (TextView) findViewById(R.id.text_info); 

		mBundle = getIntent().getExtras();
		mProp = (ContributionProperty) mBundle.getSerializable("property");	

		Button buttonOkBottom = (Button) findViewById(R.id.button_ok_bottom);
		buttonOkBottom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onClickOk();
			}
		});

		Button buttonOkTop = (Button) findViewById(R.id.button_ok_top);
		buttonOkTop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onClickOk();
			}
		});

		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.show();
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(getResources().getString(R.string.Contribute));
			actionBar.setDisplayShowTitleEnabled(true);
			// actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		}

		TextView textTitle = (TextView) findViewById(R.id.text_title);
		textTitle.setText(getResources().getString(mProp.getTitle()));

		String value = mProp.getValue();
		int info = mProp.getInfo();
		String choices[] = mProp.getChoices();
		
    	switch(mProp.getType()) {
    	case text:
    		mEditText.setVisibility(View.VISIBLE);
			mEditDate.setVisibility(View.GONE);
    		buttonOkBottom.setVisibility(View.GONE);
    		mEditText.setText( value );
    		
    		if( info == 0 ) {
    			mTextInfos.setVisibility(View.GONE);
    		}
    		else {
    			mTextInfos.setVisibility(View.VISIBLE);
    			mTextInfos.setText(getResources().getString(info));    			
    		}
    		break;
    		
    	case date:
    		mEditText.setVisibility(View.GONE);
			mEditDate.setVisibility(View.VISIBLE);
    		buttonOkBottom.setVisibility(View.GONE);
    		mEditDate.setText( value );
    		
    		if( info == 0 ) {
    			mTextInfos.setVisibility(View.GONE);
    		}
    		else {
    			mTextInfos.setVisibility(View.VISIBLE);
    			mTextInfos.setText(getResources().getString(info));    			
    		}
       		break;
       		
    	case check:
    		textLayout.setVisibility(View.GONE);
			mEditList.setVisibility(View.VISIBLE);

			Arrays.sort(choices);
			mCheckList = new ArrayList<CheckBoxListElement>();
			for (int i = 0; i < choices.length; i++) {
				mCheckList.add(new CheckBoxListElement(choices[i]));
			}

			mAdapter = new CheckBoxAdapter(getApplicationContext(), mCheckList);
			mEditList.setAdapter(mAdapter);

			for (int i = 0; i < mCheckList.size(); i++) {
				if (mProp.getValue().contains(mCheckList.get(i).getValue())) {
					mCheckList.get(i).setBool(true); 
				}
			}
			mAdapter.notifyDataSetChanged();

    		break;
    		
    	case radio:
    		textLayout.setVisibility(View.GONE);
			mEditList.setVisibility(View.VISIBLE);
			
    		Arrays.sort(choices);
    		mAdapter = new RadioListAdapter(getApplicationContext(), choices);
    		mEditList.setAdapter(mAdapter);

    		// TODO: select current value
    		
    		break;
    	}

	}

	public void onClickOk() {
		String val = "";
		switch (mProp.getType()) {
		case text:
			val = mEditText.getText().toString();
			break;

		case check:
			for (int i = 0; i < mCheckList.size(); i++) {
				CheckBoxListElement check = (CheckBoxListElement) mAdapter.getItem(i);
				if (check.getBool()) {
					if( val != "" ) { val += ", "; }
					val += mCheckList.get(i).getValue();
				}
			}
			break;

		case radio:
			RadioListAdapter rdAdapt = (RadioListAdapter) mAdapter;
			val = rdAdapt.getValue();
			break;
			
		case date:
			val = mEditDate.getText().toString();
			break;
		}

		mProp.setValue(val);
		Intent intent = new Intent();
		intent.putExtra("position", mBundle.getInt("position"));
        intent.putExtra("property",mProp); 
		setResult(RESULT_OK, intent);
		finish();
	}


	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
	}
	

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		
		if(itemId == android.R.id.home)	{
			setResult(RESULT_CANCELED);
			finish();
			return true;
		}
		else {
			return false;
		}

    }
	
}
