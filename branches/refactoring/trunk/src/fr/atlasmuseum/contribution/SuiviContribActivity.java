package fr.atlasmuseum.contribution;

import fr.atlasmuseum.R;
import fr.atlasmuseum.compte.Authentification;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SuiviContribActivity extends Activity {
	WebView webView;
	private static final String DEBUG_TAG = "AtlasMuseum/SuiviContribActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contrib_webview);
		
 
		webView = (WebView) findViewById(R.id.webview);
		webView.loadUrl("http://atlasmuseum.irisa.fr/scripts/listContribUser.php?id_user="+Authentification.getUsername());
		Log.d(DEBUG_TAG, "webview starts... ");
		
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
 			actionBar.setTitle(this.getResources().getString(R.string.Contribuer));
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
