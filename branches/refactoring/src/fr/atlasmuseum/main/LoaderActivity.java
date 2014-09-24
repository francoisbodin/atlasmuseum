package fr.atlasmuseum.main;

import fr.atlasmuseum.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.location.LocationManager;
/**
 * affiche l'écran d'accueil de l'application
 * First Activity called when app is launched
 *
 */
public class LoaderActivity extends Activity  {
	private static final String DEBUG_TAG = "AtlasMuseum/LoaderActivity";
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loader);
        Log.d(DEBUG_TAG, "onCreate");
        
        getActionBar().hide();

        Typeface fontRegular = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Regular.ttf");
        Typeface fontBold = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Bold.ttf");

        TextView textTitre = (TextView) findViewById(R.id.text_title);
        textTitre.setTypeface(fontBold);
        
        TextView textIntro1 = (TextView)findViewById(R.id.text_intro1);
        textIntro1.setTypeface(fontRegular);

        TextView text_intro2 = (TextView)findViewById(R.id.text_intro2);
        text_intro2.setTypeface(fontRegular);
        
		try {
			PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
	        TextView textVersion = (TextView)findViewById(R.id.text_version);
	        textVersion.setHint(pinfo.versionName);
		} catch (NameNotFoundException e) {
		}
        
		LinearLayout layout = (LinearLayout)findViewById(R.id.layout);
		layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoaderActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			};
		});


        
		//check if GPS is activated
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, R.string.GPSactivatedmes, Toast.LENGTH_SHORT).show();
        }
        else {
            showGPSDisabledAlertToUser();
        }
    }

    private void showGPSDisabledAlertToUser(){
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    	alertDialogBuilder.setMessage(getResources().getString(R.string.gps_click_to_enable));
    	alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setPositiveButton(
				getResources().getString(R.string.gsp_link_to_enable),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(callGPSSettingIntent);
					}
				});
		alertDialogBuilder.setNegativeButton(
				getResources().getString(R.string.annuler),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
    		}
    	});
    	AlertDialog alert = alertDialogBuilder.create();
    	alert.show();
    }
}
