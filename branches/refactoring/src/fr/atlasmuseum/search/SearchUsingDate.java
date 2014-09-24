package fr.atlasmuseum.search;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SearchUsingDate extends Activity{
	
	private EditText beginDate;
	private EditText endDate;
	private static final String DEBUG_TAG = "AtlasMuseum/SearchUsingDate";
	private final String SEARCH_DATE = "recherche par date";
	private Button buttonOk;
	private Bundle bundle;
	
	 public void onCreate(Bundle savedInstanceState) 
	 {
		
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_ACTION_BAR);
	        setContentView(R.layout.layout_serach_using_date);
	        beginDate = (EditText) findViewById(R.id.date_begin);
	        endDate = (EditText) findViewById(R.id.date_end);
	        buttonOk = (Button) findViewById(R.id.buttonRechercher);
	        TextView txt = (TextView) findViewById(R.id.textView1);
	        Typeface font_regular = Typeface.createFromAsset(this.getAssets(), "RobotoCondensed-Regular.ttf");
	        beginDate.setTypeface(font_regular);
	        endDate.setTypeface(font_regular);
	        txt.setTypeface(font_regular);
	        
	        buttonOk.setTypeface(font_regular);
	        bundle = new Bundle();
	      //pour autoriser le retour en cliquant sur l'icone de l'application dans l'action bar
			////////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////
			//ACTION BAR
			ActionBar actionBar = getActionBar();
			if (actionBar != null)
			{
				actionBar.show();
				actionBar.setDisplayHomeAsUpEnabled(true);
				actionBar.setTitle(this.getResources().getString(R.string.recherche_par_date));
				actionBar.setDisplayShowTitleEnabled(true);
				//actionBar.setHomeButtonEnabled(true);
			}
			
			buttonOk.setOnClickListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
		        	//1 verifier si input utilisateur good
		        	boolean good = isInputGood();
		        	//2 effectuer la recherche
		        	if (good)
		        	{
		        		rechercherLesOeuvre();
		        		Intent intent = new Intent(getApplication(), ResultActivity.class);
		    			
		    			intent.putExtras(bundle);
		    			startActivity(intent);
		        	}
		        	else
		        	{
		        		afiicheError();
		        		
		        	}
		        }

				
	    	});
			
	 }
	 protected void afiicheError() {
		Toast.makeText(this, "V�rifier les dates que vous avez entr�es", Toast.LENGTH_SHORT).show();
	}
	protected void rechercherLesOeuvre() {
		Log.d(DEBUG_TAG, "Build list: "+SearchActivity.db.nbentries+" entries");
		
		int begin_int = Integer.parseInt((this.beginDate.getText().toString()));
		int end_int = Integer.parseInt((this.endDate.getText().toString()));
		int j=0;
		for (int i=0;i < SearchActivity.db.nbentries; i++)
		{
			String date_oeuvre = extractDataFromDb(i,"inauguration");
			Log.d(DEBUG_TAG, "date: "+date_oeuvre);
			if ((date_oeuvre != null) && !date_oeuvre.equals("?") )
			{
				try {
					int date = Integer.parseInt((date_oeuvre.toString()));;
				
					if (date>= begin_int && end_int>= date)
					{
						bundle.putInt(Integer.toString(j),i);
						j++;
					}
				} catch (NumberFormatException nfe) {
					Log.d(DEBUG_TAG, "Bad date format:" + date_oeuvre);
				}
			}
		}
		bundle.putInt(SearchActivity.NB_ENTRIES,j);
		bundle.putBoolean(this.SEARCH_DATE, true);
	}
	private boolean isInputGood() {
		 String begin = this.beginDate.getText().toString().trim();
		 String end = this.endDate.getText().toString().trim();
		 Log.d(DEBUG_TAG, "begin date ="+begin);
		 if(begin.equals("") || begin.isEmpty())
		 {
			 return false;
		 }

		    try {
		    	int int_begin = Integer.parseInt(begin);
		        int int_end = Integer.parseInt(end);
		        
		        if ((int_end - int_begin) >=0)
		        {
		        	return true;
		        }
		        else
		        {
		        	return false;
		        }
		        	
		        
		    } catch (NumberFormatException nfe) {
		    	Log.d(DEBUG_TAG, "error conversion int");
		        return false;
		        
		    }
		}
	 
	 
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        int itemId = item.getItemId();
			
	       
			if(itemId == android.R.id.home)
			{
				super.onBackPressed();
				finish();
				return true;
			}
			else return false;
	    	
	    }
	 
		///////////////////////////// helper routines  //////////////
		static public String extractDataFromDb(int index, String field)
		{
			JSONObject obj = null; 
			//Log.d(DEBUG_TAG, "extractDataFromDb");
			try {
					obj = SearchActivity.db.data.getJSONObject(index);
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
		
}
