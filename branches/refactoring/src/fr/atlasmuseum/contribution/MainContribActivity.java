package fr.atlasmuseum.contribution;



import java.util.List;
import java.util.UUID;

import fr.atlasmuseum.R;
import fr.atlasmuseum.compte.Authentification;
import fr.atlasmuseum.compte.ConnexionActivity;
import fr.atlasmuseum.main.AtlasError;
import fr.atlasmuseum.main.MainActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainContribActivity extends Activity {

	private static final String DEBUG_TAG = "AtlasMuseum/MainContribAcitivity";
	
	private int REQUEST_CONNEXION=1245;
	public static int REQUEST_FINISH=154213;

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

		TextView textSavedTitle = (TextView) findViewById(R.id.text_saved_title);
		textSavedTitle.setTypeface(fontBold);

		TextView textSaved = (TextView) findViewById(R.id.text_saved);
		textSaved.setTypeface(fontLight);

		TextView textInfoTitle = (TextView) findViewById(R.id.text_info_title);
		textInfoTitle.setTypeface(fontBold);
		
		WebView textInfo = (WebView) findViewById(R.id.text_info);
		textInfo.loadUrl("file:///android_asset/contribuer.html");

		List<Contribution> contributions = Contribution.getContributionsFromXmlString(Contribution.readSaveFile(this));
		textSavedTitle.setText(contributions.size()+" "+getResources().getString(contributions.size() <= 1 ? R.string.contrib_save : R.string.contrib_saves));


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
				startActivityForResult(intent, MainContribActivity.REQUEST_FINISH);
			}
		});

		RelativeLayout relativEtatContrib = (RelativeLayout) findViewById(R.id.layout_status);
		relativEtatContrib.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(! checkInternetConnection()) {
					AtlasError.showErrorDialog(MainContribActivity.this, "7.1", "pas internet connexion");
					return;
				}

				if(! Authentification.getisConnected()) {
					AtlasError.showErrorDialog(MainContribActivity.this, "7.3", "compte util requis");
					return;
				}

				Intent intent= new Intent(MainContribActivity.this, SuiviContribActivity.class);
				startActivity(intent);
			}
		});


		RelativeLayout relativEnvoi = (RelativeLayout) findViewById(R.id.layout_send);
		relativEnvoi.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				if(! checkInternetConnection()) {
					AtlasError.showErrorDialog(MainContribActivity.this, "7.1", "pas de connexion internet");
					return;
				}
				
				if(! Authentification.getisConnected()){
					AtlasError.showErrorDialog(MainContribActivity.this, "7.3", "compte utilisateur requis");
					return;
				}
				
				List<Contribution> contributions = Contribution.getContributionsFromXmlString(Contribution.readSaveFile(MainContribActivity.this));
				if (contributions.size() == 0) {
					AtlasError.showErrorDialog(MainContribActivity.this, "7.2", "pas de contribution sauvegardée");
					return;
				}
				for (Contribution contribution: contributions) {
					contribution.setLogin(Authentification.getUsername());
					contribution.setPassword(Authentification.getPassword());
					
					ContributionSend contributionSend = new ContributionSend(MainContribActivity.this, contribution);
					contributionSend.execute();
				}
			}
		});
	}

	public boolean checkInternetConnection()
	{
		ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isWiFi = false;

		if(activeNetwork!=null && ( activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)){
			isWiFi = true;
		}
		else
		{
			isWiFi =false;
		}
		return isWiFi;
	}


	//	private void afficheContrib()
//	{
//		List<String> lcont = new ArrayList<String>();
//		for(int i=0;i<contXml.listContrib.size();i++)
//		{
//			lcont.add(contXml.listContrib.get(i).getDate()+ " "+ contXml.listContrib.get(i).getHeure());
//		}
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.xml.my_item_list, lcont);
//		listViewContrib.setAdapter(adapter);
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.d(DEBUG_TAG, "result code="+requestCode);
		if(requestCode == MainContribActivity.REQUEST_FINISH)
		{

			this.recreate();
		}
		if(requestCode == REQUEST_CONNEXION )
		{

		}
		else
		{
			super.onActivityResult(requestCode, resultCode, data);
		}

	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		if(itemId == android.R.id.home)
		{
			//super.onBackPressed();//forcement on vient de l'activit� MAIN
			/**Intent intent= new Intent(this,MainActivity.class);
			startActivity(intent);**/
			finish();
			return true;
		}
		if(itemId == R.id.action_account)
		{
			Intent intent= new Intent(this,ConnexionActivity.class);
			startActivityForResult(intent, REQUEST_CONNEXION);
			return true;
		}
		else return false;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contribuer_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
