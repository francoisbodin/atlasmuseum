package fr.atlasmuseum.contribution;

import java.util.ArrayList;
import java.util.Arrays;
import fr.atlasmuseum.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ModifActivity extends Activity {
	Bundle mBundle;
	ContributionProperty mProp;
	
	TextView titre_contrib;
	EditText edit;
	Button buttonOKBottom;
	Button buttonOK;//bouton ok dans le relativMain
	Button delete;
	final static String DEBUG_TAG = "AtlasMuseum/ModifActivity";
	
	String value;//valeur donnée par l'utilisateur
	ListView listV; //utiliser pour afficher
	LinearLayout relativMain;
	private ArrayList<CheckBoxElt> mCheckList;
	private BaseAdapter adapter;
	private TextView infos;
	private TextView date;

	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.contrib_layout_modif);

		Log.d(DEBUG_TAG, "onCreate()");

		buttonOKBottom = (Button) findViewById(R.id.button1);
		buttonOK= (Button) findViewById(R.id.button2);
		titre_contrib = (TextView) findViewById(R.id.titre_contrib);
		edit = (EditText) findViewById(R.id.editText1);
		listV = (ListView) findViewById(R.id.listView1); //sert � afficher les liste radio et checkboxes
		infos = (TextView) findViewById(R.id.infos); 
		date = (TextView) findViewById(R.id.date); 
		//relativMain utilis� pour cacher a la fois la zone de saisie et aussi le bouton OK du haut
		relativMain = (LinearLayout) findViewById(R.id.linearMain);//zone de saisie de texte normal+buttonOK

		getActionBar().setTitle(getResources().getString(R.string.Contribuer));

		mBundle = getIntent().getExtras();
		mProp = (ContributionProperty) mBundle.getSerializable("property");	

		// gestion bundle
		gestionBundle();

		buttonOKBottom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onClickOk();
			}
		});

		buttonOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onClickOk();
			}
		});

		// pour autoriser le retour en cliquant sur l'icone de l'application
		// dans l'action bar
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.show();
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(this.getResources().getString(
					R.string.Contribuer));
			actionBar.setDisplayShowTitleEnabled(true);
			// actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		}
	}

	private void gestionBundle() {
		String title = getResources().getString(mProp.getTitle());
		String value = mProp.getValue();
		int info = mProp.getInfo();
		String choices[] = mProp.getChoices();
		
		titre_contrib.setText(title);

    	switch(mProp.getType()) {
    	case text:
    		edit.setVisibility(View.VISIBLE);
			date.setVisibility(View.GONE);
    		buttonOKBottom.setVisibility(View.GONE);
    		edit.setText( value );
    		
    		if( info == 0 ) {
    			infos.setVisibility(View.GONE);
    		}
    		else {
    			infos.setVisibility(View.VISIBLE);
    			infos.setText(getResources().getString(info));    			
    		}
    		break;
    		
    	case date:
    		edit.setVisibility(View.GONE);
			date.setVisibility(View.VISIBLE);
    		buttonOKBottom.setVisibility(View.GONE);
    		date.setText( value );
    		
    		if( info == 0 ) {
    			infos.setVisibility(View.GONE);
    		}
    		else {
    			infos.setVisibility(View.VISIBLE);
    			infos.setText(getResources().getString(info));    			
    		}
       		break;
       		
    	case check:
    		relativMain.setVisibility(View.GONE);
			listV.setVisibility(View.VISIBLE);

			Arrays.sort(choices);// tri la liste
			mCheckList = new ArrayList<CheckBoxElt>();
			for (int i = 0; i < choices.length; i++) {
				mCheckList.add(new CheckBoxElt(choices[i]));
			}

			adapter = new CheckBoxAdapter(getApplicationContext(), mCheckList);
			listV.setAdapter(adapter);

			for (int i = 0; i < mCheckList.size(); i++) {
				if (mProp.getValue().contains(mCheckList.get(i).getValue())) {
					mCheckList.get(i).setBool(true); 
				}
			}
			adapter.notifyDataSetChanged();

    		break;
    		
    	case radio:
    		relativMain.setVisibility(View.GONE);
			listV.setVisibility(View.VISIBLE);
			
    		Arrays.sort(choices);
    		adapter = new RadioListAdapter(getApplicationContext(), choices);
    		listV.setAdapter(adapter);

    		// TODO: select current value
    		
    		break;
    	}
	}


//private void setSelectedFromString(String ajoutnat) {
//		RadioListAdapter a = (RadioListAdapter) adapter;
//		
//		//A revoir pour setSelected une valeur
//		Boolean finish=false;
//		/**for(int i=0;i<radioList.length-1 && !finish;i++)
//		{
//			if(radioList[i].equals(ajoutnat))
//			{
//				a.listView.get(i).radioBtn
//				a.valueSelected=radioList[i];
//				finish=true;
//			}
//		}**/
//	}





	public void onClickOk() {
		String val = "";
		switch (mProp.getType()) {
		case text:
			val = edit.getText().toString();
			break;

		case check:
			for (int i = 0; i < mCheckList.size(); i++) {
				CheckBoxElt check = (CheckBoxElt) adapter.getItem(i);
				if (check.getBool()) {
					if( val != "" ) { val += ", "; }
					val += mCheckList.get(i).getValue();
				}
			}
			break;

		case radio:
			RadioListAdapter rdAdapt = (RadioListAdapter) adapter;
			val = rdAdapt.getValue();
			break;
			
		case date:
			val = date.getText().toString();
			break;
		}

		mProp.setValue(val);
		Intent intent = new Intent();
		intent.putExtra("position", mBundle.getInt("position"));
        intent.putExtra("property",mProp); 
		setResult(RESULT_OK, intent);
		finish();
	}


	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
	}
	

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		
		if(itemId == android.R.id.home)	{
			setResult(RESULT_CANCELED);
			finish();
			return true;
		}
		else {
			return false;
		}

    }
	
}
