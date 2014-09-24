package fr.atlasmuseum.contribution;

import fr.atlasmuseum.R;
import fr.atlasmuseum.compte.ConnexionActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;

public class SuiviContribActivity extends Activity {
	private static final String DEBUG_TAG = "AtlasMuseum/SuiviContribActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contrib_webview);
		
		SharedPreferences prefs = getSharedPreferences(ConnexionActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		String username = prefs.getString(ConnexionActivity.PREF_KEY_USERNAME, "");
		
		WebView webView = (WebView) findViewById(R.id.webview);
		webView.loadUrl("http://atlasmuseum.irisa.fr/scripts/listContribUser.php?id_user="+username);
		Log.d(DEBUG_TAG, "webview starts... ");
		
 		ActionBar actionBar = getActionBar();
 		if (actionBar != null) {
 			actionBar.show();
 			actionBar.setDisplayHomeAsUpEnabled(true);
 			actionBar.setTitle(this.getResources().getString(R.string.Contribuer));
 			actionBar.setDisplayShowTitleEnabled(true);
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
    	return false;    
    }
}
