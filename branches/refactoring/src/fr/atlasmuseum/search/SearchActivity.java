package fr.atlasmuseum.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import fr.atlasmuseum.R;
import fr.atlasmuseum.main.AtlasError;
import fr.atlasmuseum.search.module.SimpleAdapterSearch;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

public class SearchActivity extends Activity implements ActionBar.TabListener {
	private  SimpleAdapterSearch  adapter;

	private static final String DEBUG_TAG = "SearchActivity";
	public ListView lvSelection = null;
	static final String CONTENTSTRARRAY = "contentStringArray";
	static final String NB_ENTRIES = "nbentries";
	static final String CURRENT_LAT = "curlat";
	static final String CURRENT_LONG = "curlong";
	public static final String MAP_FOCUS_NOTICE = "mapfocusnotice";
	private static final String ATLASMUSEUM_ALBUM = "atlasmuseum";
	static public JsonRawData db = null;
	private static String COME_FROM_SEARCHACT="pour le retour vers searchActivity";
	final static String CHAMPS_ITEM ="champs_select";

	private  List<String> selectionStringList;

	public String rechercheGlobale ;
	public String liste_artistes;
	public String date;
	
	private String liste_villes;
	private String liste_pays;
	private String recherche_date;



	public void onCreate(Bundle savedInstanceState) 
	{
		//Toast.makeText(this,"hELLO woRLD", Toast.LENGTH_SHORT).show();
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.rechercher);

		Log.d(DEBUG_TAG, "onCreateView");
		lvSelection = (ListView) findViewById(R.id.list_view);

		selectionStringList = new ArrayList<String>();//instancie la liste des element

		//chargement des ressources
		rechercheGlobale =this.getResources().getString(R.string.recherche_titre_artiste_lieux);
		liste_artistes=this.getResources().getString(R.string.liste_artistes);
		liste_villes = this.getResources().getString(R.string.liste_villes);
		liste_pays = this.getResources().getString(R.string.liste_pays);
		recherche_date = this.getResources().getString(R.string.recherche_date);

		//Typeface font_regular = Typeface.createFromAsset(this.getAssets(), "RobotoCondensed-Regular.ttf");

		//a faire: changer police 

		if(db ==null)//test si la bdd pas deja charg�, risque de doubl� la bdd sinon
		{
			////////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////
			//Load the data from the internal DB
			try 
			{
				db = new JsonRawData();
			} catch (JSONException e) {
				AtlasError.showErrorDialog(this, "6.1", "");// ERROR "impossible de charger la base de donn�e"
			}
		}

		selectionStringList.add(rechercheGlobale);//0
		selectionStringList.add(recherche_date);//1

		selectionStringList.add(liste_artistes);//2
		selectionStringList.add(liste_villes);//3
		selectionStringList.add(liste_pays);//4

		//adapter = new ArrayAdapter<String>(this,R.xml.my_item_list, selectionStringList);
		adapter = new SimpleAdapterSearch(this.getApplicationContext(),selectionStringList);


		lvSelection.setAdapter(adapter);
		setActionsForTheList();
		//pour autoriser le retour en cliquant sur l'icone de l'application dans l'action bar
		////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////
		//ACTION BAR
		ActionBar actionBar = getActionBar();
		if (actionBar != null)
		{

			String menu_rechercher =this.getResources().getString(R.string.menu_rechercher);
			actionBar.show();
			actionBar.setTitle(menu_rechercher);
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setHomeButtonEnabled(true);
			//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
		}
		ActionBar bar = getActionBar();
		Drawable search_img = getResources().getDrawable(R.drawable.ic_search);
		Drawable autour_img = getResources().getDrawable(R.drawable.ic_autour);
		Drawable map_img = getResources().getDrawable(R.drawable.ic_map);


		bar.addTab(bar.newTab().setIcon(search_img).setTabListener(this));//search
		bar.addTab(bar.newTab().setIcon(autour_img).setTabListener(this));//autour
		bar.addTab(bar.newTab().setIcon(map_img).setTabListener(this));//map
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.selectTab(bar.getTabAt(0));

	}	//end onCreate





	protected void gotoSearchAround() {
		Intent intent= new Intent(this,SearchAutourList.class);
		getActionBar().setSelectedNavigationItem(0);
		startActivity(intent);
		finish();
	}


	/// Action for the list
	void setActionsForTheList()
	{
		lvSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id)
			{
				Bundle sbundle = new Bundle();
				String ChampsSelect="";
				Intent intent =null;
				//String ch = adapter.getItem(position);//contient le champs selectionner
				switch(position)
				{
				case 0:
					ChampsSelect = "tous";
					sbundle.putString(SearchActivity.CHAMPS_ITEM,ChampsSelect);//crée un bundle avec la chaine associé
					genereBundle(sbundle, ChampsSelect);

					intent = new Intent(getApplication(), SearchAuto.class);
					intent.putExtras(sbundle);
					getActionBar().setSelectedNavigationItem(0);
					startActivity(intent);
					break;

				case 1:
					ChampsSelect = recherche_date;

					intent = new Intent(getApplication(), SearchUsingDate.class);
					getActionBar().setSelectedNavigationItem(0);
					startActivity(intent);
					break;


				case 2:
					ChampsSelect = liste_artistes;

					sbundle.putString(SearchActivity.CHAMPS_ITEM,"artiste");//crée un bundle avec la chaine associé
					//sbundle.putStringArray(SearchActivity.CONTENTSTRARRAY, setArtisteStringList(sbundle));//envoi le tableau de tous les artistes
					genereBundle(sbundle, "artiste");
					intent = new Intent(getApplication(), ListActivity.class);
					intent.putExtras(sbundle);
					startActivity(intent);
					break;
				case 3:
					ChampsSelect = liste_villes;

					sbundle.putString(SearchActivity.CHAMPS_ITEM,"Siteville");//crée un bundle avec la chaine associé
					genereBundle(sbundle, "Siteville");
					intent = new Intent(getApplication(), ListActivity.class);
					intent.putExtras(sbundle);
					startActivity(intent);
					break;
				case 4:
					ChampsSelect = liste_pays;

					sbundle.putString(SearchActivity.CHAMPS_ITEM,"Sitepays");//crée un bundle avec la chaine associé
					genereBundle(sbundle, "Sitepays");
					intent = new Intent(getApplication(), ListActivity.class);
					intent.putExtras(sbundle);
					startActivity(intent);
					break;
				}

				// Secondary activity used with one notices currently, but can handle multiple ones


				//Toast.makeText(getApplicationContext(),
				//		"Activating list intent for position : " + position, Toast.LENGTH_SHORT).show();
			}
		});	
	}//fin setActionForList



	@Override
	protected void onDestroy() {
		Log.d(DEBUG_TAG, "onDestroy");
		super.onDestroy();
	}	



	////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////// helper routines  //////////////
	static public String extractDataFromDb(int index, String field)
	{
		JSONObject obj = null; 
		//Log.d(DEBUG_TAG, "extractDataFromDb");
		try {
			obj = db.data.getJSONObject(index);
		} catch (JSONException e) 
		{
			return "?";
		}
		try {
			return obj.getString(field);
		} catch (JSONException e)
		{
			return "?";
		}
	}



	//utiliser dans LoadingPhotoAsync
	public static File checkIfImageFileExists(String filename) throws IOException {
		File albumF = getAlbumDir();
		File imageF = new File(albumF.getAbsolutePath() + "/" + filename);
		if (imageF.exists()) return imageF;
		else return null;
	}

	//utiliser dans LoadingPhotoAsync
	public static File checkIfPhotoFileExists(String filename) throws IOException {
		File albumF = getAlbumDir();
		File imageF = new File(albumF.getAbsolutePath() + "/" + filename);
		if (imageF.exists()) return imageF;
		else return null;
	}

	// for images managements
	//utiliser dans ObjectFragmentActivity
	private static File getAlbumDir() {
		File storageDir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			String dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
			storageDir = new File( dcimDir + "/" + ATLASMUSEUM_ALBUM );
			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d(DEBUG_TAG, "failed to create directory");
						return null;
					}
				}
			}
		} else {
			Log.v(DEBUG_TAG, "External storage is not mounted READ/WRITE.");
		}
		return storageDir;
	}

	//utiliser dans ObjectFragmentActivity
	public static File createImageFile(String filename) throws IOException {
		// Create an image file name
		//String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		Log.d(DEBUG_TAG, "creating imagefile = " + filename);
		//String timeStamp2 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US).format(new Date());
		//Log.d(DEBUG_TAG, "timeStamp2 = " + timeStamp2);
		//String imageFileName = ATLASMUSEUM_IMAGE_PREFIX + timeStamp;
		File albumF = getAlbumDir();
		File imageF =  new File(albumF.getAbsolutePath() + "/" + filename);
		return imageF;
	}




	static double haversine_meter(double lat1, double long1, double lat2, double long2)
	{
		final double d2r= 0.0174532925199433;
		double dlong = (long2 - long1) * d2r;
		double dlat = (lat2 - lat1) * d2r;
		double a = Math.pow(Math.sin(dlat/2.0), 2) + Math.cos(lat1*d2r) * Math.cos(lat2*d2r) * Math.pow(Math.sin(dlong/2.0), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double d = 6367 * c;

		return d*1000.0;
	}

/*
	private String[] setArtisteStringList(Bundle extra){
		int i,j;
		int nbEntries = 0;
		boolean found = false;
		String[]artisteStringList = new String[db.nbentries];
		for (i=0;i < db.nbentries; i++){	
			String artiste = extractDataFromDb(i,"artiste");
			if (artiste != null){
				artiste = artiste.trim();
				found = false;
				for (j=0;j < nbEntries && (found == false); j++){
					if ((artiste != null) && (artiste.equals(artisteStringList[j]))){
						found = true;
					}
				}
				if ((found == false) && (artiste != null)){
					artisteStringList[nbEntries] = artiste;
					Log.d(DEBUG_TAG, "adding artiste to menu :" + artiste);
					extra.putInt(Integer.toString(j),i); 
					nbEntries++;
				}
			}
		}
		for (i=nbEntries;i < db.nbentries; i++){
			artisteStringList[i] = "ZZZZZ"; 
		}
		extra.putInt(NB_ENTRIES,nbEntries);
		Arrays.sort(artisteStringList);
		return artisteStringList;
	}
*/

	private void genereBundle(Bundle extra, String champs){
		int i,j;
		int nbEntries = 0;
		boolean found = false;
		List<String> valueStringList = new ArrayList<String>();;
		for (i=0;i < db.nbentries; i++)
		{	
			String valuechamps = extractDataFromDb(i,champs);
			if (valuechamps != null)
			{
				valuechamps = valuechamps.trim();
				found = false;
				for (j=0;j < nbEntries && (found == false); j++)//nbEntries est le nombre d'elt qu'on a deja trouvé
				{
					if ((valuechamps != null)  && (valuechamps.equals(valueStringList.get(j))) )
					{
						found = true;
					}
				}
				if ((found == false) && (valuechamps != null))
				{
					valueStringList.add(valuechamps);
					Log.d(DEBUG_TAG, "adding elt to "+champs+" :" + valueStringList.get(nbEntries));
					extra.putInt(Integer.toString(j),i); 
					nbEntries++;
				}
			}
		}

		extra.putInt(SearchActivity.NB_ENTRIES,nbEntries);

	}

/*
	//fonction de recherche de cText dans le champs c
	private void showList(String c, String cText, Boolean startIntent){
		int i,j=0;//j = nbEntries trouv�, et i pour parcourir 
		double distanceClosest = 100000000.0;
		int indexDistanceClosest = -1;
		Intent intent = new Intent(this, ListActivity.class);
		Log.d(DEBUG_TAG, "Build list: "+db.nbentries+" entries");
		Bundle extra = new Bundle();
		for (i=0;i < db.nbentries; i++){	
			switch (c) {
			case "artiste": 
				String region = extractDataFromDb(i,"Siteregion");
				if ((region != null) && (region.toLowerCase().contains(cText.toLowerCase()))){
					extra.putInt(Integer.toString(j),i); 
					j++;// le nombre de item trouv�
				}
				break;
			case  "tous_les_artistes": 
				String ville = extractDataFromDb(i,"Siteville");
				if ((ville != null) && (ville.toLowerCase().contains(cText.toLowerCase()))){
					extra.putInt(Integer.toString(j),i); 
					j++;
				}
				break;
			case "lieux": 
				extra.putInt(Integer.toString(j),i); 
				j++;
				break;
			case "villes": 
				String artiste = extractDataFromDb(i,"artiste");
				if ((artiste != null) && (artiste.toLowerCase().contains(cText.toLowerCase()))){
					extra.putInt(Integer.toString(j),i); 
					j++;
				}
				break;
			case "titre_oeuvre": 
				String titre = extractDataFromDb(i,"titre");
				if ((titre != null) && (titre.toLowerCase().contains(cText.toLowerCase()))){
					extra.putInt(Integer.toString(j),i); 
					j++;
				}
				break;
			case "": 
				titre = extractDataFromDb(i,"titre");
				artiste = extractDataFromDb(i,"artiste");
				ville = extractDataFromDb(i,"Siteville");
				String nature = extractDataFromDb(i,"nature");
				String motscles = extractDataFromDb(i,"mot_cle");
				String inauguration = extractDataFromDb(i,"inauguration");

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
			case "hh": 
				if (mLastLocation != null){
					double lat1 = mLastLocation .getLatitude();
					double long1 = mLastLocation.getLongitude();
					double lat2, long2, distance;
					double radiusValue = 1000; //FBO 1000 meters is the default
					try {
						radiusValue = Double.parseDouble(cText);
						lat2 = Double.parseDouble(extractDataFromDb(i,"longitude"));
						long2 = Double.parseDouble(extractDataFromDb(i,"latitude"));
						distance = haversine_meter(lat1,long1,lat2,long2);
					} catch(Exception e) {
						distance = 100000000; //TODO to be modified later
					}
					if (distance < radiusValue){
						extra.putInt(Integer.toString(j),i); j++;
					}
				}
				break;
			case "ee":
				if (mLastLocation != null){
					double lat1 = mLastLocation.getLatitude();
					double long1 = mLastLocation.getLongitude();
					double lat2, long2, distance;
					try {
						lat2 = Double.parseDouble(extractDataFromDb(i,"longitude"));
						long2 = Double.parseDouble(extractDataFromDb(i,"latitude"));
						distance = haversine_meter(lat1,long1,lat2,long2);
					} catch(Exception e) {
						distance = 100000000; //TODO to be modified later
					}
					if (distance < distanceClosest){
						indexDistanceClosest = i;
						distanceClosest = distance;
					}
				}
				break;
			}
		}


		//.makeText(getApplicationContext(),
		//		"Nombre d'oeuvres trouv�es : " + Integer.toString(j) + " ", Toast.LENGTH_SHORT).show();
		if (mLastLocation != null){
			extra.putDouble(CURRENT_LAT,mLastLocation.getLatitude());
			extra.putDouble(CURRENT_LONG,mLastLocation.getLongitude());
		} else {
			//	Toast.makeText(getApplicationContext(),
			//			"Position GPS inconnue", Toast.LENGTH_SHORT).show();
			extra.putDouble(CURRENT_LAT,0.0);
			extra.putDouble(CURRENT_LONG,0.0);
		}
		extra.putInt(NB_ENTRIES,j);
		if (startIntent){
			intent.putExtras(extra);
			startActivity(intent);
		}
	}
*/

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		if(itemId == android.R.id.home)
		{
			super.onBackPressed();
			/**Intent intent= new Intent(this,MainActivity.class);

			startActivity(intent);**/
			finish();
			return true;
		}
		else return false;

	}


	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// Do Nothing

	}


	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		int i = tab.getPosition();
		Log.d(DEBUG_TAG, "item position="+i);
		Log.d(DEBUG_TAG, "value="+tab.getText());
		switch(i)
		{
		case 0: //rechercher
			break;
		case 1:	//autour de moi
			gotoSearchAround();
			break;
		case 2: //map
			showMap();
			break;
		}
	}


	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// Do Nothing

	}

	private void showMap(){
		Intent intent = new Intent(getApplication(),MapActivity.class);
		Bundle bundle = new Bundle();
		for(int i=0;i<SearchActivity.db.nbentries;i++)
		{
			bundle.putInt(Integer.toString(i), i);
		}
		bundle.putBoolean(SearchActivity.getCOME_FROM_SEARCHACT(), true);
		bundle.putInt(SearchActivity.NB_ENTRIES, SearchActivity.db.nbentries);
		intent.putExtras(bundle);
		getActionBar().setSelectedNavigationItem(0);
		startActivity(intent);
		finish();
	}





	public static String getCOME_FROM_SEARCHACT() {
		return COME_FROM_SEARCHACT;
	}





	public static void setCOME_FROM_SEARCHACT(String cOME_FROM_SEARCHACT) {
		COME_FROM_SEARCHACT = cOME_FROM_SEARCHACT;
	}




}

