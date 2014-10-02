package fr.atlasmuseum.contribution;

import fr.atlasmuseum.R;
import fr.atlasmuseum.account.ConnectionActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;

public class SentContributionsActivity extends Activity {
	private static final String DEBUG_TAG = "AtlasMuseum/SentContributionsActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sent_contributions_activity);
		
		SharedPreferences prefs = getSharedPreferences(ConnectionActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);
		String username = prefs.getString(ConnectionActivity.PREF_KEY_USERNAME, "");
		
		WebView webView = (WebView) findViewById(R.id.webview_sent);
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
