package fr.atlasmuseum;

import fr.atlasmuseum.R;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;


public class AboutActivity extends Activity {
	private static final String DEBUG_TAG = "AtlasMuseum/AboutActivity";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		
		Log.d(DEBUG_TAG, "onCreate()");

		WebView webView = (WebView) findViewById(R.id.webview_apropos);
		webView.loadUrl("file:///android_asset/apropos.html");

		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.show();
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(this.getResources().getString(R.string.main_about));
			actionBar.setDisplayShowTitleEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		if (itemId == android.R.id.home) {
			super.onBackPressed();
			finish();
			return true;
		} else
			return false;

	}
}
