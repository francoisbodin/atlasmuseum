package fr.atlasmuseum;

import fr.atlasmuseum.R;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;

public class HelpActivity extends Activity{

	private static final String DEBUG_TAG = "AtlasMuseum/HelpActivity";
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);
        
		Log.d(DEBUG_TAG, "onCreate()");

		WebView webView = (WebView) findViewById(R.id.webview_help);
		webView.loadUrl("file:///android_asset/aide.html");

		// ACTION BAR
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.show();
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(this.getResources().getString(R.string.main_help));
			actionBar.setDisplayShowTitleEnabled(true);
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
}
