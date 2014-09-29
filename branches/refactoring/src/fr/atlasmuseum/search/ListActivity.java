package fr.atlasmuseum.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import fr.atlasmuseum.AtlasmuseumActivity;
import fr.atlasmuseum.R;
import fr.atlasmuseum.location.LocationStruct;
import fr.atlasmuseum.search.module.SimpleAdapterSearch;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * Liste l'ensemble des auteurs ou pays ou villes
 *
 */
@SuppressLint("DefaultLocale") public class ListActivity extends Activity  {
	
	private static final String DEBUG_TAG = "AtlasMuseum/ListActivity";
	private ListView lvSelection = null;
	private Bundle bundle ;
	private  List<String> selectionStringList;
	private  SimpleAdapterSearch adapter;
	private TextView TitreHaut;
	//action bar
	private ActionBar actionBar = null;

	String champs; //champs sur laquelle porte la recherche: ex artiste, pays ou villes ..etc
	
    public void onCreate(Bundle savedInstanceState)
    {
    	
    	//Toast.makeText(this,"ListActivity", Toast.LENGTH_LONG).show();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.search_activity_list);
        Log.d(DEBUG_TAG, "onCreateView");
        bundle= new Bundle();
        bundle = getIntent().getExtras();
        
        TitreHaut =  (TextView) findViewById(R.id.titlelist);
        TitreHaut.setText(Integer.toString(getNumberOfEntries()) + " ELEMENTS TROUVES");
		lvSelection =  (ListView) findViewById(R.id.list_view);
		lvSelection.setFastScrollEnabled(true);
		selectionStringList = new ArrayList<String>();
		champs= bundle.getString(SearchActivity.CHAMPS_ITEM,"artiste");//quand on arriv sur l'activit�, rechercher par defaut: artiste
		
		ArrayList<String> listElement = new ArrayList<String>();
		int idx;
		for (idx = 0; idx < getNumberOfEntries(); idx++)
		{
			int idxloc = getEntryNumberForFragment(idx);
			
			String element  = SearchActivity.extractDataFromDb(idxloc,champs);
			//Log.d(DEBUG_TAG,"name ="+element);
			
			listElement.add(element);
			
		}
		Collections.sort(listElement);
		for(idx = 0; idx < getNumberOfEntries(); idx++)
		{
			selectionStringList.add(listElement.get(idx));
		}
		adapter = new SimpleAdapterSearch(this.getApplicationContext(),selectionStringList);
		//adapter = new ArrayAdapter<String>(this,R.xml.my_item_spinner, selectionStringList);
		lvSelection.setAdapter(adapter);
		//pour autoriser le retour en cliquant sur l'icone de l'application dans l'action bar
		////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////
		//ACTION BAR
		actionBar = getActionBar();
		if (actionBar != null)
		{
			actionBar.show();
			
			actionBar.setTitle(this.getResources().getString(R.string.menu_rechercher));
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		this.setActionsForTheList();
    }//fin oncreate
	    
	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	/// Action for the list
	void setActionsForTheList()
	{
		lvSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked,
			int position, long id) 
			{
				bundle.putString(SearchActivity.CHAMPS_ITEM,champs);//cr�e un bundle avec la chaine associ�
				Log.d(DEBUG_TAG, "champs ="+champs);
				Log.d(DEBUG_TAG+"/setActionForlist", "recherche de "+ adapter.getItem(position));
				showList(champs, (String)adapter.getItem(position),false); //important !! gere le bundle

				Intent intent = new Intent(getApplication(), ResultActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);

			}
			});
	}//fin setActionsForList
    
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	/// HELPER ROUTINES ///
	
	int getEntryNumberForFragment(int idx){
	if (bundle == null) return -1;
	return bundle.getInt(Integer.toString(idx));
	}
	
	int getNumberOfEntries(){
	int v = bundle.getInt(SearchActivity.NB_ENTRIES);
	//Log.d(DEBUG_TAG, "getNumberOfEntries : " + v);
	return v;
	}
	
	Double getCurrentLatitude(){
	Double v = bundle.getDouble(SearchActivity.CURRENT_LAT );
	Log.d(DEBUG_TAG, "getCurrentLatitude : " + v);
	return v;
	}
	
	Double getCurrentLongitude(){
	Double v = bundle.getDouble(SearchActivity.CURRENT_LONG );
	Log.d(DEBUG_TAG, "getCurrentLongitude : " + v);
	return v;
	}
	

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		Log.i(DEBUG_TAG, "revenir page accueil");
		if(itemId == android.R.id.home)
		{
			super.onBackPressed();
			finish();
			return true;
		}
		else return false;
    	
    }
    
  /**
   * fonction de recherche de cText dans le champs c
   * modifie le bundle, en fonction du nb resultat trouvé
   * ajoute les info de localisation
   * @param c champ: ex artiste, lieux, tous_les_artistes ..;
   * @param cText chaine à chercher dans la BDD interne du telephone
   * @param startIntent pour demarrer ou non une activité
   */
  		private void showList(String c, String cText, Boolean startIntent){
  			
  			LocationStruct mLastLocation = AtlasmuseumActivity.mLastLocation;
  			
  			int i,j=0;//j = nbEntries trouvé, et i pour parcourir 
  			Intent intent = new Intent(this, ListActivity.class);
  			Log.d(DEBUG_TAG, "Build list: "+SearchActivity.db.nbentries+" entries");
  			Bundle extra = new Bundle();
  			for (i=0;i < SearchActivity.db.nbentries; i++){	
  				switch (c) {
  				
  				case "artiste": 
  					//Log.d(DEBUG_TAG, "recherche");
  					String artiste = SearchActivity.extractDataFromDb(i,"artiste");
  					if ((artiste != null) && (artiste.toLowerCase().contains(cText.toLowerCase()))){
  						extra.putInt(Integer.toString(j),i); 
  						j++;// le nombre d'items trouvés
  					}
  					break;
  				case  "Siteville": 
  					String ville = SearchActivity.extractDataFromDb(i,"Siteville");
  					if ((ville != null) && (ville.toLowerCase().contains(cText.toLowerCase()))){
  						extra.putInt(Integer.toString(j),i); 
  						j++;
  					}
  					break;
  				
  				case "titre_oeuvre": 
  					String titre = SearchActivity.extractDataFromDb(i,"titre");
  					if ((titre != null) && (titre.toLowerCase().contains(cText.toLowerCase()))){
  						extra.putInt(Integer.toString(j),i); 
  						j++;
  					}
  					break;
  				case "Sitepays": 
  					String region = SearchActivity.extractDataFromDb(i,"Sitepays");
  					if ((region != null) && (region.toLowerCase().contains(cText.toLowerCase()))){
  						extra.putInt(Integer.toString(j),i); 
  						j++;
  					}
  					break;
  				case "": 
  					titre = SearchActivity.extractDataFromDb(i,"titre");
  					artiste = SearchActivity.extractDataFromDb(i,"artiste");
  					ville = SearchActivity.extractDataFromDb(i,"Siteville");
  					String nature = SearchActivity.extractDataFromDb(i,"nature");
  					String motscles = SearchActivity.extractDataFromDb(i,"mot_cle");
  					String inauguration = SearchActivity.extractDataFromDb(i,"inauguration");
  					
  					if ((titre != null) && (titre.toLowerCase().contains(cText.toLowerCase()))){
  						extra.putInt(Integer.toString(j),i); 
  						j++;
  					} else  if ((artiste != null) && (artiste.toLowerCase().contains(cText.toLowerCase()))){
  						extra.putInt(Integer.toString(j),i); 
  						j++;
  					}else if ((ville != null) && (ville.toLowerCase().contains(cText.toLowerCase()))){
  						extra.putInt(Integer.toString(j),i); 
  						j++;
  					}else if ((nature != null) && (nature.toLowerCase().contains(cText.toLowerCase()))){
  						extra.putInt(Integer.toString(j),i); 
  						j++;
  					}else if ((motscles != null) && (motscles.toLowerCase().contains(cText.toLowerCase()))){
  						extra.putInt(Integer.toString(j),i); 
  						j++;
  					}else if ((inauguration != null) && (inauguration.toLowerCase().contains(cText.toLowerCase()))){
  						extra.putInt(Integer.toString(j),i); 
  						j++;
  					}
  					break;
  				
  				}//fin switch
  			}//fin for
  			
  			
  			//.makeText(getApplicationContext(),
  			//		"Nombre d'oeuvres trouv�es : " + Integer.toString(j) + " ", Toast.LENGTH_SHORT).show();
  			if (mLastLocation != null)
  			{
  				extra.putDouble(SearchActivity.CURRENT_LAT,mLastLocation.getLatitude());
  				extra.putDouble(SearchActivity.CURRENT_LONG,mLastLocation.getLongitude());
  			} else 
  			{
  			//	Toast.makeText(getApplicationContext(),
  			//			"Position GPS inconnue", Toast.LENGTH_SHORT).show();
  				extra.putDouble(SearchActivity.CURRENT_LAT,0.0);
  				extra.putDouble(SearchActivity.CURRENT_LONG,0.0);
  			}
  			extra.putInt(SearchActivity.NB_ENTRIES,j);
  			if (startIntent)
  			{
  				intent.putExtras(extra);
  				startActivity(intent);
  			} else 
  			{
  				bundle = extra;
  			}
  		} //fin showList
}
