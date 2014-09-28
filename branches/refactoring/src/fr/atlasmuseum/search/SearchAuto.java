package fr.atlasmuseum.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.irisa.unpourcent.location.LocationStruct;

import fr.atlasmuseum.R;
import fr.atlasmuseum.main.MainActivity;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("DefaultLocale") public class SearchAuto extends Activity{

	private static final String DEBUG_TAG = "AtlasMuseum/SearchAuto";
	public ListView lvSelection=null;
	public EditText inputSearch;
	private  List<String> selectionStringList;
	private  List<String> currentListView;
	private  ArrayAdapter<String> adapter;
	private Bundle bundle;
	
	TextView error_text; //affiche no result si pas de resultat
	//voice
	private ImageButton voiceBtn;
	private ImageButton eraseInput;
	private static final int REQUEST_CODE = 1234; //request code pour voice
	
	Button buttonsearch_tous;
	Button buttonsearch_artiste;
	Button buttonsearch_lieux;
	Button buttonsearch_titre;
	
	
	
	String champs; //champs selectionner parmi: tous, artiste, lieux, titre
	 public void onCreate(Bundle savedInstanceState) 
	 {
		
	    	//Toast.makeText(this,"hELLO woRLD", Toast.LENGTH_SHORT).show();
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_ACTION_BAR);
	        setContentView(R.layout.search_auto_complete2_layout);
	        eraseInput  = (ImageButton)  findViewById(R.id.erase_input);
	        buttonsearch_tous = (Button)  findViewById(R.id.buttonsearch_tous);
	        buttonsearch_artiste = (Button)  findViewById(R.id.buttonsearch_artiste);
	        buttonsearch_lieux = (Button)  findViewById(R.id.buttonsearch_lieux);
	        buttonsearch_titre = (Button)  findViewById(R.id.buttonsearch_titre);
	        
			Log.d(DEBUG_TAG, "onCreateView");
			voiceBtn = (ImageButton) findViewById(R.id.voice_btn);
			lvSelection = (ListView) findViewById(R.id.list_view);
			inputSearch = (EditText) findViewById(R.id.editText1);
			error_text = (TextView) findViewById(R.id.error_text);
			error_text.setVisibility(TextView.INVISIBLE);
			selectionStringList =new ArrayList<String>(); //contient toutes les donn�es disponible
			bundle = new Bundle();
			bundle = getIntent().getExtras();
			Log.d(DEBUG_TAG, "bundle = "+bundle.toString());
			
			champs= bundle.getString(SearchActivity.CHAMPS_ITEM,"tous");
			getActionBar().setTitle("Rechercher par artiste");
			//methode pour disabled bouton correspondant a la recherche actuelle
			Log.d(DEBUG_TAG, "***********SearchAuto champs ="+champs);
			disabledBouton(champs);
			genereList(); //
			
			currentListView =new ArrayList<String>();//contient un sous ensemble de selectionStringList, cest la liste sur l'�cran
			adapter = new ArrayAdapter<String>(this,R.xml.my_item_list, currentListView);
			lvSelection.setAdapter(adapter);
			
			this.onTextChange(inputSearch);//ajoute le listener sur l'editText dans lequel on lance la recherche
			this.setActionsForTheList();//ajoute le listener sur la liste
			
			
			buttonsearch_artiste.setTextColor(Color.parseColor("#686868"));
    		buttonsearch_titre.setTextColor(Color.parseColor("#686868"));
    		buttonsearch_tous.setTextColor(Color.parseColor("#ffda00"));
    		buttonsearch_lieux.setTextColor(Color.parseColor("#686868"));
    		
    		
			voiceBtn.setOnClickListener(new OnClickListener() {
		    	@Override
		    	public void onClick(View v) {
		    		startVoiceRecognitionActivity();
		    	}
		    });
			eraseInput.setOnClickListener(new OnClickListener() {
		    	@Override
		    	public void onClick(View v) {

		    		inputSearch.setText("");
		    	}
		    });
			buttonsearch_titre.setOnClickListener(new OnClickListener() {
		    	@Override
		    	public void onClick(View v) {
		    		//bundle.putString(SearchActivity.CHAMPS_ITEM,"titre");
		    		champs ="titre";
		    		clearList();
		    		genereList();
		    		buttonsearch_titre.setTextColor(Color.parseColor("#ffda00"));
		    		buttonsearch_artiste.setTextColor(Color.parseColor("#686868"));
		    		buttonsearch_tous.setTextColor(Color.parseColor("#686868"));
		    		buttonsearch_lieux.setTextColor(Color.parseColor("#686868"));
		    		String rechercher_par_titre = getResources().getString(R.string.rechercher_par_titre);
		    		getActionBar().setTitle(rechercher_par_titre);
		    	}
		    });
			buttonsearch_artiste.setOnClickListener(new OnClickListener() {
		    	@Override
		    	public void onClick(View v) {
		    		bundle.putString(SearchActivity.CHAMPS_ITEM,"artiste");
		    		champs ="artiste";
		    		clearList();
		    		genereList();
		    		buttonsearch_artiste.setTextColor(Color.parseColor("#ffda00"));
		    		
		    		buttonsearch_titre.setTextColor(Color.parseColor("#686868"));
		    		buttonsearch_tous.setTextColor(Color.parseColor("#686868"));
		    		buttonsearch_lieux.setTextColor(Color.parseColor("#686868"));
		    		String rechercher_par_artiste = getResources().getString(R.string.rechercher_par_artiste);
		    		getActionBar().setTitle(rechercher_par_artiste);
		    	}
		    });
			buttonsearch_tous.setOnClickListener(new OnClickListener() {
		    	@Override
		    	public void onClick(View v) {
		    		bundle.putString(SearchActivity.CHAMPS_ITEM,"tous");
		    		champs ="tous";
		    		clearList();
		    		genereList();
		    		buttonsearch_tous.setTextColor(Color.parseColor("#ffda00"));
		    		
		    		buttonsearch_titre.setTextColor(Color.parseColor("#686868"));
		    		buttonsearch_artiste.setTextColor(Color.parseColor("#686868"));
		    		buttonsearch_lieux.setTextColor(Color.parseColor("#686868"));
		    		String recherche_titre_artiste_lieux = getResources().getString(R.string.recherche_titre_artiste_lieux);
		    		getActionBar().setTitle(recherche_titre_artiste_lieux);
		    	}
		    });
			buttonsearch_lieux.setOnClickListener(new OnClickListener() {
		    	@Override
		    	public void onClick(View v) {
		    		bundle.putString(SearchActivity.CHAMPS_ITEM,"lieux");
		    		champs ="lieux";
		    		clearList();
		    		genereList();
		    		buttonsearch_lieux.setTextColor(Color.parseColor("#ffda00"));
		    		
		    		buttonsearch_titre.setTextColor(Color.parseColor("#686868"));
		    		buttonsearch_artiste.setTextColor(Color.parseColor("#686868"));
		    		buttonsearch_tous.setTextColor(Color.parseColor("#686868"));
		    		String rechercher_par_lieux = getResources().getString(R.string.rechercher_par_lieux);
		    		getActionBar().setTitle(rechercher_par_lieux);
		    	}
		    });
			
			 //pour autoriser le retour en cliquant sur l'icone de l'application dans l'action bar
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
	  			actionBar.setDisplayShowTitleEnabled(true);
	  			//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
	  		}
	 }
	 
	 void genereList()
	 {
		 //selectionStringList =new ArrayList<String>(); //contient tous les artistes
		 //String champs = bundle.getString(SearchActivity.CHAMPS_ITEM);
		 
			if (champs.equals("tous"))
			{
				int idx;
				for (idx = 0; idx < SearchActivity.db.nbentries; idx++)
				{
					//Recherche dans toute la bdd interne
					String artiste  = SearchActivity.extractDataFromDb(idx,"artiste");
					String titre  = SearchActivity.extractDataFromDb(idx,"titre");
					String ville  = SearchActivity.extractDataFromDb(idx,"Siteville");
					//Log.d(DEBUG_TAG,"name ="+artiste);				
					if (!(artiste.equals("?")))
					{
						artiste = artiste.trim();
						selectionStringList.add(artiste );
					}
					if (!(titre.equals("?")))
					{
						titre = titre.trim();
						selectionStringList.add(titre );
					}
					if (!(ville.equals("?")))
					{
						ville = ville.trim();
						selectionStringList.add(ville );
					}
				}
				
				getActionBar().setTitle(this.getResources().getString(R.string.recherche_titre_artiste_lieux));
			}
			else if(champs.equals("lieux"))
			{
				int idx;
				for (idx = 0; idx < SearchActivity.db.nbentries; idx++)
				{
					
					String artiste  = SearchActivity.extractDataFromDb(idx,"Siteregion");
					String titre  = SearchActivity.extractDataFromDb(idx,"Sitenom");
					String ville  = SearchActivity.extractDataFromDb(idx,"Siteville");
					//Log.d(DEBUG_TAG,"name ="+artiste);				
					if (!(artiste.equals("?")))
					{
						artiste = artiste.trim();
						selectionStringList.add(artiste );
					}
					if (!(titre.equals("?")))
					{
						titre = titre.trim();
						selectionStringList.add(titre );
					}
					if (!(ville.equals("?")))
					{
						ville = ville.trim();
						selectionStringList.add(ville );
					}
				}
				
				getActionBar().setTitle(this.getResources().getString(R.string.rechercher_par_lieux));
			}
			else // si c'est champs= artiste ou titre
			{
				if(champs.equals("artiste"))
				{
					getActionBar().setTitle(this.getResources().getString(R.string.rechercher_par_lieux));
				}
				else if(champs.equals("titre"))
				{
					getActionBar().setTitle(this.getResources().getString(R.string.rechercher_par_titre));
				}
					
				int idx;
				for (idx = 0; idx < SearchActivity.db.nbentries; idx++)
				{
					
					String artiste  = SearchActivity.extractDataFromDb(idx,champs);
					//Log.d(DEBUG_TAG,"name ="+artiste);				
					if ((artiste != null))
					{
						artiste = artiste.trim();
						selectionStringList.add(artiste );
					}
				}
			}
			Collections.sort(selectionStringList);
			for(int i=0; i<selectionStringList.size();i++)
			{
				//Log.d(DEBUG_TAG, i+": "+selectionStringList.get(i));
			}
	 }
		void disabledBouton(String champs) {
			switch(champs)
			{
			case "artiste":
				this.buttonsearch_artiste.setClickable(false);
				break;
			case "tous":
				this.buttonsearch_tous.setClickable(false);
				break;
			case "lieux":
				this.buttonsearch_lieux.setClickable(false);
				break;
			case "titre":
				this.buttonsearch_titre.setClickable(false);
				break;
			default:
					champs ="artiste";
					this.buttonsearch_artiste.setClickable(false);
					break;
			}
		
	}
		int getEntryNumberForFragment(int idx){
			if (bundle == null) return -1;
			return bundle.getInt(Integer.toString(idx));
			}

		 /// Action for the list
		    void setActionsForTheList()
		{
			lvSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id)
				{
					//recupere l'artiste selectionn�
		
				bundle = new Bundle();
				String ChampsSelect="";
				Intent intent =null;
				//ChampsSelect = "artiste";
				ChampsSelect = bundle.getString(SearchActivity.CHAMPS_ITEM, "");
				Log.d("reussi", "value: "+ currentListView.get(position)+"; ch item ="+ChampsSelect); //nom sur lequel on a cliqu�.
				
				bundle.putString(SearchActivity.CHAMPS_ITEM,ChampsSelect);//cr�e un bundle avec la chaine associ�
				//showList(ChampsSelect, currentListView.get(position),false); //important !! gere le bundle
				showList(champs, currentListView.get(position),false); //important !! gere le bundle
				intent = new Intent(getApplication(), ResultActivity.class);
				
				intent.putExtras(bundle);
				startActivity(intent);
				
			}
		
		});	
		}//fin setActionForList
	 
		public void onTextChange(EditText edit)
		 {
			//permet de rafraichir la liste view
			 edit.addTextChangedListener(new TextWatcher(){
				    public void afterTextChanged(Editable s) {}
				    public void beforeTextChanged(CharSequence s, int start, int count, int after){}
				    public void onTextChanged(CharSequence s, int start, int before, int count)
				    {
				    	
				    	actuliseListView(s);//� chaque ajout de char s
				    }
				});
			 
		 }
		public void clearList()
		{
			this.inputSearch.setText("");
			selectionStringList.clear();
			currentListView.clear();
			adapter.notifyDataSetChanged();//actualise la vue
		}

		 public void actuliseListView(CharSequence s)
		 {
			 Log.d(DEBUG_TAG, "actualiseListView****** char s="+s);
			 currentListView.clear();
			 for(int i=0;i<selectionStringList.size();i++)
			 {
				 //Log.d(DEBUG_TAG, "traitement de"+selectionStringList.get(i));
				 //FBO startsWith --> contains
				 if(selectionStringList.get(i).trim().toLowerCase().contains((s.toString().toLowerCase())))
				 {
					
					 if(!currentListView.contains(selectionStringList.get(i)))
					 {
						 //Log.d(DEBUG_TAG, "*** add"+selectionStringList.get(i));
						 currentListView.add(selectionStringList.get(i));
					 }
				 }
				 
			 }
			 if(currentListView.isEmpty())
			 {
				 error_text.setText("No result");
				 error_text.setVisibility(TextView.VISIBLE);
			 }
			 else
			 {
				 error_text.setVisibility(TextView.INVISIBLE);
			 }
			 
			 adapter.notifyDataSetChanged();//actualise la vue
			 
			 Log.d(DEBUG_TAG, "******************************************************");
		 }
	 
	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    	switch (requestCode) {
	    		//voice
	    	case REQUEST_CODE:
	    		if(resultCode == RESULT_OK)
	    		{
	    			ArrayList<String> matches = data.getStringArrayListExtra(
	    					RecognizerIntent.EXTRA_RESULTS);
	    			
	    			String m = matches.get(0);
	    			Toast.makeText(this,m, Toast.LENGTH_LONG).show();
	    			inputSearch.setText(m);
	    		}
	    		
	    		break;
	    	}
	 }
	 
	/**
	 * 
	 * VOICE ACTIVITY
	 */
	 public void startVoiceRecognitionActivity() 
	 {
		  Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		  intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
		    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		  intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
		    this.getResources().getString(R.string.app_name));
		  startActivityForResult(intent, REQUEST_CODE);
	}
	 
	int getNumberOfEntries()
	{
		int v = bundle.getInt(SearchActivity.NB_ENTRIES);
		Log.d(DEBUG_TAG, "getNumberOfEntries : " + v);
		return v;
	}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		
		if(itemId == android.R.id.home)
		{
			/**Intent intent= new Intent(this,MainActivity.class);
			
			startActivity(intent);
			finish();**/
			
			super.onBackPressed();
			finish();
			return true;
		}
		else return false;
    	
    }

    //fonction de recherche de cText dans le champs c
   		private void showList(String c, String cText, Boolean startIntent){
   			
   			LocationStruct mLastLocation = MainActivity.mLastLocation;
   			
   			int i,j=0;//j = nbEntries trouv�, et i pour parcourir 
   			Intent intent = new Intent(this, ListActivity.class);
   			Log.d(DEBUG_TAG, "Build list: "+SearchActivity.db.nbentries+" entries");
   			Bundle extra = new Bundle();
   			for (i=0;i < SearchActivity.db.nbentries; i++){	
   				switch (c) {
   				case "artiste": 
   					String artiste = SearchActivity.extractDataFromDb(i,"artiste");
   					if ((artiste != null) && (artiste.toLowerCase().contains(cText.toLowerCase()))){
   						extra.putInt(Integer.toString(j),i); 
   						j++;// le nombre de item trouv�
   					}
   					break;
   				case "lieux": 
   					String ville_ville = SearchActivity.extractDataFromDb(i,"Siteville");
   					if ((ville_ville != null) && (ville_ville.toLowerCase().contains(cText.toLowerCase()))){
   						extra.putInt(Integer.toString(j),i); 
   						j++;
   						break;
   					}
   					String villes_region = SearchActivity.extractDataFromDb(i,"Siteregion");
   					if ((villes_region != null) && (villes_region.toLowerCase().contains(cText.toLowerCase()))){
   						extra.putInt(Integer.toString(j),i); 
   						j++;
   						break;
   					}
   					String villes_site = SearchActivity.extractDataFromDb(i,"Sitenom");
   					if ((villes_site != null) && (villes_site.toLowerCase().contains(cText.toLowerCase()))){
   						extra.putInt(Integer.toString(j),i); 
   						j++;
   						break;
   					}
   					
   					break;
   				case "tous": 
   					String artiste1 = SearchActivity.extractDataFromDb(i,"artiste");
   					if ((artiste1 != null) && (artiste1.toLowerCase().contains(cText.toLowerCase()))){
   						extra.putInt(Integer.toString(j),i); 
   						j++;
   						break;
   					}
   					String ville_ville1 = SearchActivity.extractDataFromDb(i,"Siteville");
   					if ((ville_ville1 != null) && (ville_ville1.toLowerCase().contains(cText.toLowerCase()))){
   						extra.putInt(Integer.toString(j),i); 
   						j++;
   						break;
   					}
   					String villes_region1 = SearchActivity.extractDataFromDb(i,"Siteregion");
   					if ((villes_region1 != null) && (villes_region1.toLowerCase().contains(cText.toLowerCase()))){
   						extra.putInt(Integer.toString(j),i); 
   						j++;
   						break;
   					}
   					String villes_site1 = SearchActivity.extractDataFromDb(i,"Sitenom");
   					if ((villes_site1 != null) && (villes_site1.toLowerCase().contains(cText.toLowerCase()))){
   						extra.putInt(Integer.toString(j),i); 
   						j++;
   						break;
   					}
   					String titre1 = SearchActivity.extractDataFromDb(i,"titre");
   					if ((titre1 != null) && (titre1.toLowerCase().contains(cText.toLowerCase()))){
   						extra.putInt(Integer.toString(j),i); 
   						j++;
   						break;
   					}
   					break;
   				case "titre": 
   					String titre = SearchActivity.extractDataFromDb(i,"titre");
   					if ((titre != null) && (titre.toLowerCase().contains(cText.toLowerCase()))){
   						extra.putInt(Integer.toString(j),i); 
   						j++;
   					}
   					break;
   				
   					
   				}
   			}
   			
   			
   			//.makeText(getApplicationContext(),
   			//"Nombre d'oeuvres trouv�es : " + Integer.toString(j) + " ", Toast.LENGTH_SHORT).show();
   			if (mLastLocation != null){
   				extra.putDouble(SearchActivity.CURRENT_LAT,mLastLocation.getLatitude());
   				extra.putDouble(SearchActivity.CURRENT_LONG,mLastLocation.getLongitude());
   			} else {
   			//	Toast.makeText(getApplicationContext(),
   			//	"Position GPS inconnue", Toast.LENGTH_SHORT).show();
   				extra.putDouble(SearchActivity.CURRENT_LAT,0.0);
   				extra.putDouble(SearchActivity.CURRENT_LONG,0.0);
   			}
   			extra.putInt(SearchActivity.NB_ENTRIES,j);
   			if (startIntent){
   				intent.putExtras(extra);
   				startActivity(intent);
   			} else {
   				bundle = extra;
   			}
   			Log.d(DEBUG_TAG, "showlist nb entries = "+j);
   		}  
}
