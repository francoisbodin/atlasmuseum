package fr.atlasmuseum;

import fr.atlasmuseum.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NewsActivity extends Activity {

	private class MyWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        view.loadUrl(url);
	        return false;
	    }
	}
	
	@SuppressWarnings("unused")
	private static final String DEBUG_TAG = "AtlasMuseum/NewsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_activity);

		WebView webView = (WebView) findViewById(R.id.webview_help);
		webView.setWebViewClient(new MyWebViewClient());
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.loadUrl("http://atlasmuseum.irisa.fr/scripts/actu.html");

		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.show();
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(this.getResources().getString(R.string.main_news));
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
