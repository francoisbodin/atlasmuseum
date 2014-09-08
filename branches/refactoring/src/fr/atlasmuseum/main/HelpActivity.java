package fr.atlasmuseum.main;

import fr.atlasmuseum.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HelpActivity extends Activity{

	private static final String DEBUG_TAG = "atlasmuseum/HelpActivity";
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_layout);
        Log.d(DEBUG_TAG, "onCreate");
        
        addListenerOnScreen();

    }
    public void addListenerOnScreen() {
    	
		final Context context = this;
		Button icon = null;
		
		icon = (Button) findViewById(R.id.clickscreen2);
 
		icon.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
			    Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);   
			}
 
		});
 
	}
}
