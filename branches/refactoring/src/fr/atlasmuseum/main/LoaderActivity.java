package fr.atlasmuseum.main;


import fr.atlasmuseum.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.location.LocationManager;
/**
 * affiche l'écran d'accueil de l'application
 * First Activity called when app is launched
 *
 */
public class LoaderActivity extends Activity  {
	private static final String DEBUG_TAG = "atlasmuseum/Loader";
	static final String NB_ENTRIES = "nbentries";
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loader);
        Log.d(DEBUG_TAG, "onCreate");
        
        addListenerOnScreen();
       // getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        TextView titre = (TextView) findViewById(R.id.app_name);
        TextView text_intro2 = (TextView)findViewById(R.id.text_intro2);
        TextView text_intro1 = (TextView)findViewById(R.id.text_intro1);
        Typeface font1 = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Bold.ttf");
        Typeface font = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Regular.ttf");
        titre.setTypeface(font1);
        text_intro2.setTypeface(font);
        text_intro1.setTypeface(font);
        
      //check if GPS is activated
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, R.string.GPSactivatedmes, Toast.LENGTH_SHORT).show();
        }else
        {
            showGPSDisabledAlertToUser();
        }
        
    }
    public void addListenerOnScreen() {
    	
		final Context context = this;
		Button icon = null;
		
		icon = (Button) findViewById(R.id.clickscreen);
 
		icon.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
			    Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                            finish();
			}

		});
 
	}
    

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getResources().getString(R.string.gps_click_to_enable))
        .setCancelable(false)
        .setPositiveButton(getResources().getString(R.string.gsp_link_to_enable),
                new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                Intent callGPSSettingIntent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(callGPSSettingIntent);
            }
        });
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.annuler),
                new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
