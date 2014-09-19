package fr.atlasmuseum.main;

import com.irisa.unpourcent.location.LocationProvider;
import com.irisa.unpourcent.location.LocationStruct;


import fr.atlasmuseum.R;
import fr.atlasmuseum.compte.Authentification;
import fr.atlasmuseum.compte.AutoConnexionAsync;
import fr.atlasmuseum.compte.ConnexionActivity;
import fr.atlasmuseum.contribution.MainContribActivity;
import fr.atlasmuseum.search.SearchActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationProvider.OnLocationChanged {
	
	private static final String DEBUG_TAG = "AltasMuseum/mainActivity";
	Bundle bundle;
	public static String idUser = "moderateurAppli";
	
	//longitude = -1
	//boutons de la page d'accueil
	public RelativeLayout mButtonExplorer;
	public RelativeLayout mButtonProfil;
	public RelativeLayout mButtonContribuer;
	public RelativeLayout mButtonActu;
	public RelativeLayout mButtonAPropos;

	
	//location stuff
	private LocationProvider mLocationProvider;
   public static LocationStruct mLastLocation = new LocationStruct();
  
    private Boolean mUpdateLocationRequested = true;
	private int RESULT_CONNEXION=125455;
    
    public static SharedPreferences cPref; //preference pour le systeme de compte utilisateur, contient user et password AUTO_ISCHECK et DEJACO
	
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mButtonExplorer = (RelativeLayout) findViewById(R.id.mButtonExplorer);
		mButtonProfil = (RelativeLayout) findViewById(R.id.mButtonProfil);
		mButtonContribuer = (RelativeLayout) findViewById(R.id.mButtonContribuer);
		mButtonActu = (RelativeLayout) findViewById(R.id.mButtonActu);
		mButtonAPropos = (RelativeLayout) findViewById(R.id.mButtonAPropos);
		
		this.setActionForButton();//ajoute les listener sur les boutons
		
		RelativeLayout fav = (RelativeLayout) findViewById(R.id.mButtonAide);
		fav.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		
	    		gotoAideActivity();
	    	}
	    });
		
		Typeface font = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Bold.ttf");
		
		TextView txtsearch = (TextView) findViewById(R.id.txt_view_search);
		txtsearch.setTypeface(font);
		
		TextView txtaide = (TextView) findViewById(R.id.txt_view_aide);
		txtaide.setTypeface(font);
		
		TextView txtprf = (TextView) findViewById(R.id.txt_view_profil);
		txtprf.setTypeface(font);
		
		TextView txtactu = (TextView) findViewById(R.id.txt_view_actu);
		txtactu.setTypeface(font);
		
		TextView txtaprop = (TextView) findViewById(R.id.txt_view_apropos);
		txtaprop.setTypeface(font);
		
		TextView txtcontrib = (TextView) findViewById(R.id.txt_view_contribuer);
		txtcontrib.setTypeface(font);
		
		//User account stuff
		cPref=getSharedPreferences("UserInfo", Context.MODE_WORLD_READABLE);
		boolean auto=cPref.getBoolean("AUTO_ISCHECK", false);
		boolean deja=cPref.getBoolean("DEJACO", false);
		if(auto&&(!deja))
			{
				autologin(auto); //si on est pas connect�, et que la case connexion auto is check, on se connecte
			}
		boolean isConnected= Authentification.getisConnected();//recupere le status de l'utilisateur, connect� ou non
		
		if(isConnected)
		{
			txtprf.setText(this.getResources().getString(R.string.deconnexion));
		}
		else
		{
			txtprf.setText(this.getResources().getString(R.string.connexion));
		}
		//end user account stuff
		
		////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////
		//location stuff
		mLocationProvider = new LocationProvider(this);
		mLastLocation = null;
		mUpdateLocationRequested = false;
		startLocationUpdate();
		
		////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////
		updateLocation();
		
		afficheInfo();
		
	}
	
	void gotoAideActivity()
	{
		Intent intent= new Intent(this, HelpActivity.class);
		startActivity(intent);
		
	}
	
	private void afficheInfo() {
		// TODO Auto-generated method stub
		Log.i(DEBUG_TAG, "cpref-Autoischeck = "+cPref.getBoolean("AUTO_ISCHECK", false));
		Log.i(DEBUG_TAG, "cpref-dejaco = "+cPref.getBoolean("DEJACO", false));
		Log.i(DEBUG_TAG, "cpref-user = "+cPref.getString("user", "unknow"));
		Log.i(DEBUG_TAG, "cpref-password= "+cPref.getString("password", "unknow"));
		

		Log.i(DEBUG_TAG, "Authentification_username = "+Authentification.getUsername());
		Log.i(DEBUG_TAG, "Authentification_password = "+Authentification.getPassword());
	}

	public static SharedPreferences getCPrefs() {
		return cPref;
	}
	
	private void autologin(boolean auto) {
		// TODO Auto-generated method stub
		
			String username=cPref.getString("user", "");
			
			String mdp=cPref.getString("password", "");
			
			Log.i(DEBUG_TAG+"autolog main","user:"+username+" mdp: "+mdp);
			
			AutoConnexionAsync pck=new AutoConnexionAsync(this, username, mdp);
			pck.execute();
			
			cPref.edit().putBoolean("DEJACO", true).commit();
	}
	//ajouter le Deconnexion
		public void deconnecter(){
			Authentification.Deconnexion();
			//cPref.edit().putString("user", "").commit();
			//cPref.edit().putString("password", "").commit();
			cPref.edit().putBoolean("AUTO_ISCHECK", false).commit();
			cPref.edit().putBoolean("DEJACO", false).commit();
			recreate();
		}
	private void setActionForButton() {
		// TODO Auto-generated method stub
		  //setting the actions for freesearch
		mButtonExplorer.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	onClickExplorer(v);
	        }
	    });
		mButtonProfil.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		if(!Authentification.getisConnected())
	    		{
	    			onClickProfil(v);
	    		}
	    		else
	    		{
	    			deconnecter();//on se deconnecte
	    		}
	    		
	    	}
	    });
		
		mButtonActu.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		onClickActu();
	    	}
		});
			
		
		mButtonContribuer.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		onClickContribuer();
	    	}
	    });
		
		mButtonAPropos.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		onClickAPropos();
	    	}
	    });
	}//end setAction
	
	public void onClickProfil(View v)
	{	
		boolean auto=cPref.getBoolean("AUTO_ISCHECK", false);
		if(checkInternetConnection())
		{
			if(auto)
			{
				autologin(auto);
			}
			else
			{
				Intent intent= new Intent(this, ConnexionActivity.class);
				startActivityForResult(intent, RESULT_CONNEXION);
				//finish();
			}
		}
		else //afficher une erreur car pas de connexion internet
     	{
     		AtlasError.showErrorDialog(MainActivity.this, "7.1", "pas de connexion internet");
     	}
		
		
		
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // TODO Auto-generated method stub
	    super.onActivityResult(requestCode, resultCode, data);
	    if(requestCode == RESULT_CONNEXION)
	    {
	    	if(resultCode== RESULT_OK)
	    	{
	    		Log.d(DEBUG_TAG, "RESULT OK ****************");
	    		TextView txtprf = (TextView) findViewById(R.id.txt_view_profil);
	    		txtprf.setText(this.getResources().getString(R.string.deconnexion));//"Deconnexion"
	    		//on a réussi a se connecter
	    	}
	    	
	    }
	    
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
    
    
	public void onClickExplorer(View v)
	{
		Intent intent= new Intent(this, SearchActivity.class);
		startActivity(intent);
	}
	
	private void onClickActu() {
		Intent intent= new Intent(this, ActuActivity.class); //lance la webview pour visualiser les contributions
		startActivity(intent);
	}
	
	private void onClickContribuer() {
		Intent intent= new Intent(this, MainContribActivity.class); //lance la webview pour visualiser les contributions
		startActivity(intent);
	}
	private void onClickAPropos() {
		Intent intent= new Intent(this, AproposActivity.class);
		startActivity(intent);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////
	////////Dealing with various events /////////
	
	@Override
	protected void onStart() 
	{
		Log.d(DEBUG_TAG, "onStart");
		super.onStart();
		mLocationProvider.connect();
	}
	
	@Override
	protected void onResume() 
	{
		Log.d(DEBUG_TAG, "onResume");
		super.onResume();
		
		if (mLastLocation == null ) {
		updateLocation();
	}

	
	}
	
	@Override
	protected void onPause()
	{
		Log.d(DEBUG_TAG, "onPause");
		stopLocationUpdate();
		super.onPause();
	}
	
	
	private void stopLocationUpdate() 
	{
		mLocationProvider.stopPeriodicUpdates();
		Log.d(DEBUG_TAG, "stopLocationUpdate");
	}
	@Override
	protected void onStop() {
		Log.d(DEBUG_TAG, "onStop");
		mLocationProvider.disconnect();
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		Log.d(DEBUG_TAG, "onDestroy");
		super.onDestroy();
	}	
	
	private void updateLocation() 
	{
		mUpdateLocationRequested = true;
		Log.d(DEBUG_TAG, "updateLocation");
	}
	
	// dealing with GPS Location
	private void startLocationUpdate()
	{
		mLocationProvider.startPeriodicUpdates();
		Log.d(DEBUG_TAG, "startLocationUpdate");
	}
	public void onLocationUpdated( Location location ) {
		Log.d(DEBUG_TAG, "onLocationUpdated");
		if( location == null ) 
		{
			Log.d(DEBUG_TAG, "onLocationUpdated returning");
			return;
		}
		if( mUpdateLocationRequested ) {
			mUpdateLocationRequested = false;
			mLastLocation = new LocationStruct(location);
			/**Toast.makeText(getApplicationContext(),
					"Coordonnées GPS : " + 
							Double.toString(mLastLocation.getLatitude())+", "+ Double.toString(mLastLocation.getLongitude())
							, Toast.LENGTH_SHORT).show();**/
			Log.d(DEBUG_TAG, "Location updated");
			Log.d(DEBUG_TAG,"Latitude :" + Double.toString(mLastLocation.getLatitude()));
			Log.d(DEBUG_TAG,"Longitude :" + Double.toString(mLastLocation.getLongitude()));
//			mButtonRefresh.clearAnimation();
			
		}
	}
}
