package fr.atlasmuseum.main;

import fr.atlasmuseum.R;
import fr.atlasmuseum.compte.Authentification;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;

/**
 * Webview qui montre les contributions envoy�s par l'utilisateur
 * @author Expert
 *
 */
public class ActuActivity extends Activity {
	WebView webView;
	private static final String DEBUG_TAG = "AtlasMuseum/SuiviContribActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contrib_webview);
		
 
		webView = (WebView) findViewById(R.id.webview);
		webView.loadUrl("http://atlasmuseum.irisa.fr/scripts/actu.html");
		Log.d(DEBUG_TAG, "webview starts... ");
		
		//pour autoriser le retour en cliquant sur l'icone de l'application dans l'action bar
 		////////////////////////////////////////////////////////////////////////////////////
 		////////////////////////////////////////////////////////////////////////////////////
 		////////////////////////////////////////////////////////////////////////////////////
 		//ACTION BAR
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.show();
			actionBar.setDisplayHomeAsUpEnabled(true);
			// actionBar.setTitle(this.getResources().getString(R.string.actualites));
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
