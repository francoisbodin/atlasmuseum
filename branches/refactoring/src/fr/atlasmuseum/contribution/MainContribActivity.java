package fr.atlasmuseum.contribution;



import java.io.File;
import java.io.FilenameFilter;
import java.util.UUID;

import fr.atlasmuseum.AtlasmuseumActivity;
import fr.atlasmuseum.R;
import fr.atlasmuseum.account.ConnexionActivity;
import fr.atlasmuseum.main.AtlasError;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainContribActivity extends Activity {

	@SuppressWarnings("unused")
	private static final String DEBUG_TAG = "AtlasMuseum/MainContribAcitivity";
	
	static private int REQUEST_CONTRIBUTE = 1;
	static private int REQUEST_CONNEXION = 2;
	static private int REQUEST_VIEW_SAVED_CONTRIBUTIONS = 3;

	TextView mTextSavedTitle;
	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.contrib_main_layout);

		Typeface fontBold = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Bold.ttf");
		Typeface fontLight = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Light.ttf");

		TextView textContributeTitle = (TextView) findViewById(R.id.text_contribute_title);
		textContributeTitle.setTypeface(fontBold);
		
		TextView textContribute = (TextView) findViewById(R.id.text_contribute);
		textContribute.setTypeface(fontLight);

		TextView textStatusTitle = (TextView) findViewById(R.id.text_status_title);
		textStatusTitle.setTypeface(fontBold);

		TextView textStatus = (TextView) findViewById(R.id.text_status);
		textStatus.setTypeface(fontLight);

		mTextSavedTitle = (TextView) findViewById(R.id.text_saved_title);
		mTextSavedTitle.setTypeface(fontBold);

		TextView textSaved = (TextView) findViewById(R.id.text_saved);
		textSaved.setTypeface(fontLight);

		TextView textInfoTitle = (TextView) findViewById(R.id.text_info_title);
		textInfoTitle.setTypeface(fontBold);
		
		WebView textInfo = (WebView) findViewById(R.id.text_info);
		textInfo.loadUrl("file:///android_asset/contribuer.html");

		updateSavedContributionNumber();
		
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.show();
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(this.getResources().getString(R.string.Contribuer));
			actionBar.setDisplayShowTitleEnabled(true);
			//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
		}


		RelativeLayout layoutContribute = (RelativeLayout) findViewById(R.id.layout_contribute);
		layoutContribute.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Contribution contribution = new Contribution();
				contribution.setLocalId(UUID.randomUUID().toString());
				Bundle bundle = new Bundle();
				bundle.putSerializable("contribution", contribution);
				Intent intent= new Intent(MainContribActivity.this, ListChampsNoticeModif.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUEST_CONTRIBUTE );
			}
		});

		RelativeLayout relativEtatContrib = (RelativeLayout) findViewById(R.id.layout_status);
		relativEtatContrib.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(! AtlasmuseumActivity.checkInternetConnection(MainContribActivity.this)) {
					AtlasError.showErrorDialog(MainContribActivity.this, "7.1", "pas internet connexion");
					return;
				}

				SharedPreferences prefs = getSharedPreferences(ConnexionActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);
				String username = prefs.getString(ConnexionActivity.PREF_KEY_USERNAME, "");
				if( username.equals("") ) {
					AtlasError.showErrorDialog(MainContribActivity.this, "7.3", "compte util requis");
					return;
				}

				Intent intent = new Intent(MainContribActivity.this, SuiviContribActivity.class);
				startActivity(intent);
			}
		});


		RelativeLayout relativEnvoi = (RelativeLayout) findViewById(R.id.layout_send);
		relativEnvoi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainContribActivity.this, SavedContributionsActivity.class);
				startActivityForResult(intent, REQUEST_VIEW_SAVED_CONTRIBUTIONS);
			}
		});
	}

	private void updateSavedContributionNumber() {
		// Get number of saved contributions
		File saveDir = new File( Contribution.getSaveDir(this) );
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("new_") || name.startsWith("modif_");
			}
		};
		int nbSavedContributions = saveDir.list(filter).length;

		// Update contribute title with number of saved contributions
		mTextSavedTitle.setText(nbSavedContributions+" "+getResources().getString(nbSavedContributions <= 1 ? R.string.contrib_save : R.string.contrib_saves));

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( requestCode == REQUEST_CONTRIBUTE  ) {
			switch(resultCode) {
			case ListChampsNoticeModif.RESULT_SAVED:
				Toast.makeText(this, getResources().getString(R.string.contrib_save), Toast.LENGTH_SHORT).show();
				updateSavedContributionNumber();
				break;
			case ListChampsNoticeModif.RESULT_SENT:
    			Toast.makeText(this, getResources().getString(R.string.contrib_envoi_success), Toast.LENGTH_SHORT).show();
				updateSavedContributionNumber();
				break;
			case RESULT_CANCELED:
			default:
				break;
			}
		}
		else if( requestCode == REQUEST_CONNEXION ) {
		}
		else if( requestCode == REQUEST_VIEW_SAVED_CONTRIBUTIONS ) {
			updateSavedContributionNumber();
		}
		else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		if(itemId == android.R.id.home) {
			finish();
			return true;
		}
		else if(itemId == R.id.action_account) {
			Intent intent= new Intent(this,ConnexionActivity.class);
			startActivityForResult(intent, REQUEST_CONNEXION);
			return true;
		}
		else {
			return false;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contribuer_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
