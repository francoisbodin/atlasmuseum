package fr.atlasmuseum.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import fr.atlasmuseum.AtlasmuseumActivity;
import fr.atlasmuseum.R;
import fr.atlasmuseum.location.LocationStruct;
import fr.atlasmuseum.main.AtlasError;
import fr.atlasmuseum.search.module.NoticeAdapterWithDistance;
import fr.atlasmuseum.search.module.NoticeCompar;
import fr.atlasmuseum.search.module.NoticeOeuvre;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchAutourList extends Activity implements loadPhotoInterface{
	private final String DEBUG_TAG = "AtlasMuseum/SearchAutourList";
	public static final String ARG_FRAGMENT = "IDFragment";
	public ListView lvSelection = null;
	NoticeAdapterWithDistance noticeAdapter;
	List<NoticeOeuvre> listNotice;
	public Bundle bundle;
	//Button mButtonMapBas;
	
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.search_result_list_layout);
        
        lvSelection = (ListView) findViewById(R.id.list_view);
        //mButtonMapBas = (Button) findViewById(R.id.mButtonMapBas);
        Log.d(DEBUG_TAG, "onCreateView");
        bundle = new Bundle();
        
        ArrayList<NoticeCompar> compNoticeList = new ArrayList<NoticeCompar>();
        
		listNotice = new ArrayList<NoticeOeuvre>();//va contenir les notices d'oeuvre triées suivant la distance
        int idxloc;
        int j = 0;//nombre d'oeuvres trouvées aux alentours
        LocationStruct mLastLocation = AtlasmuseumActivity.mLastLocation;
        if (mLastLocation != null)
		{
			double lat1 = mLastLocation.getLatitude();
			double long1 = mLastLocation.getLongitude();
			for (idxloc = 0; idxloc < SearchActivity.db.nbentries; idxloc++) //parcours de toutes la bdd interne
			{
				String artiste  = SearchActivity.extractDataFromDb(idxloc,"artiste");
				String nomOvreu  = SearchActivity.extractDataFromDb(idxloc,"titre");
				String date = SearchActivity.extractDataFromDb(idxloc,"inauguration");
				String photo = SearchActivity.extractDataFromDb(idxloc,"image_principale");
				String lieux = SearchActivity.extractDataFromDb(idxloc,"Siteville");
				String pays = SearchActivity.extractDataFromDb(idxloc,"Sitepays");
				
				double longi=0;
				double lati = 0;
				
				try {
						longi = Double.parseDouble(SearchActivity.extractDataFromDb(idxloc,"longitude"));
						lati = Double.parseDouble(SearchActivity.extractDataFromDb(idxloc,"latitude"));
						
						
							double distance;
							double radiusValue =8000000; //FBO 1000 meters is the default
							try {
								
								//radiusValue = Double.parseDouble(cText);
								distance = haversine_meter(lat1,long1,lati,longi);
							} catch(Exception e) {
								distance = 100000000; //TODO to be modified later
							
							}
							if (distance < radiusValue)
							{
								bundle.putInt(Integer.toString(j), idxloc);
								NoticeOeuvre notice = new NoticeOeuvre(nomOvreu, artiste,idxloc); //cr�� une notice
								try
								{
									int annee = Integer.valueOf(date);
									notice.setAnnee(annee);
								}catch(Exception e) {notice.setAnnee(1000000);}
								
								Log.d(DEBUG_TAG, "id oeuvre ajout� ="+notice.getId());
								notice.setVille(lieux);
								notice.setPays(pays);
								notice.setLatitude(lati);
								notice.setLongitude(longi);
								notice.setDistance((int)distance);
								notice.setPhoto(photo);
								
								/**
								File fimage = SearchActivity.checkIfImageFileExists("thumb_"+photo) ;
								if(fimage == null || !listPhoto.contains(photo))//l'image n'existe pas deja dans listphoto
								{
									listPhoto.add("thumb_"+photo);
									Log.d(DEBUG_TAG+"/ajoutPhotoToLoad", photo);
								}
								**/
								//Log.d(DEBUG_TAG, "photo= "+photo);
								NoticeCompar n = new NoticeCompar(notice, j);
								compNoticeList.add(n);
								j++;
							}
							Log.d(DEBUG_TAG, "distance "+idxloc+": "+distance);
						
						
						
					}catch(Exception e) {
						Log.d(DEBUG_TAG, "issue with location"); 
					}
				
			}//fin for
    		
       
			Collections.sort(compNoticeList);
			
			for(int i=0;i< compNoticeList.size();i++)
			{
				if(compNoticeList.get(i).getOeuvre().getDistance() <= 50000)
				{
					listNotice.add(compNoticeList.get(i).getOeuvre());
				}
				
			}
    	}
		else
		{
			Log.d(DEBUG_TAG, "mLaslocation null");
			AtlasError.showErrorDialog(SearchAutourList.this, "8.1", "imppossible de r�cup�rer votre position pour le moment.");
		}
        
        
        
		
		bundle.putInt(SearchActivity.NB_ENTRIES, j);
		Log.d(DEBUG_TAG, "fin chargement... ");
		noticeAdapter = new NoticeAdapterWithDistance(this, listNotice);
		lvSelection.setAdapter(noticeAdapter);
		//Enfin on met un �couteur d'�v�nement sur notre listView
				lvSelection.setOnItemClickListener(new OnItemClickListener() {
				
					@Override
		         	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
						//on r�cup�re la HashMap contenant les infos de notre item (titre, description, img)
						//lvSelection.getItemAtPosition(position).getClass();
		        		Log.d(DEBUG_TAG, "class ="+lvSelection.getItemAtPosition(position).getClass());
		        		
		        		NoticeOeuvre noticeSelected = (NoticeOeuvre) lvSelection.getItemAtPosition(position);
		        		 bundle = new Bundle();
		        		Log.d(DEBUG_TAG, "titres ="+noticeSelected.getTitre());
		        		Log.d(DEBUG_TAG, "titres ="+noticeSelected.getId());
		        		int idfrag = noticeSelected.getId();
						bundle.putInt(ARG_FRAGMENT,idfrag );//idfrag, c'est l'id dans la BDD
		    			Intent intent = new Intent(getApplication(), ShowNoticeActivity.class);
		    			intent.putExtras(bundle);
		    			startActivity(intent);
		        	}
		         });
				
				
				
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
		  			actionBar.setTitle(this.getResources().getString(R.string.title_autour_de_moi));
		  			actionBar.setDisplayShowTitleEnabled(true);
		  			//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
		  		}
				
    }
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.discovart, menu);
        return super.onCreateOptionsMenu(menu);
	}
	
	int getNumberOfEntries(){
		int v = bundle.getInt(SearchActivity.NB_ENTRIES);
		Log.d(DEBUG_TAG, "getNumberOfEntries : " + v);
		return v;
	}
	
	
	
    double haversine_meter(double lat1, double long1, double lat2, double long2)
    {
    	final double d2r= 0.0174532925199433;
        double dlong = (long2 - long1) * d2r;
        double dlat = (lat2 - lat1) * d2r;
        double a = Math.pow(Math.sin(dlat/2.0), 2) + Math.cos(lat1*d2r) * Math.cos(lat2*d2r) * Math.pow(Math.sin(dlong/2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = 6367 * c;

        return d*1000.0;
    }
	private void showMap(){
		
		Intent intent = new Intent(getApplication(),MapActivity.class);
		if (getNumberOfEntries() == 1) 
		{
			bundle.putInt(SearchActivity.MAP_FOCUS_NOTICE,1);
		}
		intent.putExtras(bundle);
		startActivity(intent);
		
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		
		if(itemId == android.R.id.home)
		{
			Intent intent= new Intent(this,SearchActivity.class);
			
			startActivity(intent);
			//super.onBackPressed();
			finish();
			return true;
		}
		
		if(itemId == R.id.action_map)
		{
			showMap();
			return true;
		}
		else return false;
    	
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



	@Override
	public Context getContext() {
		return SearchAutourList.this;
	}


	@Override
	public ImageView getImageView() {
		return null;
	}


	@Override
	public BaseAdapter getNoticeAdapter() {
		return this.noticeAdapter;
	}
	
	@Override
	public void onBackPressed() {
		Intent intent= new Intent(this,SearchActivity.class);
		
		startActivity(intent);
		finish();
	}
}
