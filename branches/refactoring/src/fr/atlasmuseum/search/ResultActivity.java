package fr.atlasmuseum.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import fr.atlasmuseum.R;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ResultActivity extends Activity implements loadPhotoInterface {
	private static final String DEBUG_TAG = "AtlasMuseum/ResultActivity";

	private Bundle mBundle;
	private ListView mListViewSelection;
	
	NoticeAdapterWithDistance noticeAdapter;
	
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.search_result_list_layout);
        Log.d(DEBUG_TAG, "onCreate()");
        
        mBundle = getIntent().getExtras();
        
        ArrayList<NoticeCompar> compNoticeList = new ArrayList<NoticeCompar>();
        
        Log.d(DEBUG_TAG+"/numberEntries", getNumberOfEntries()+" entries");
        int j=0;
		for (int idx = 0; idx < getNumberOfEntries(); idx++)
		{
			int idxloc = getEntryNumberForFragment(idx);
			
			String artiste  = SearchActivity.extractDataFromDb(idxloc,"artiste");
			String nomOvreu  = SearchActivity.extractDataFromDb(idxloc,"titre");
			String date = SearchActivity.extractDataFromDb(idxloc,"inauguration");
			String photo = SearchActivity.extractDataFromDb(idxloc,"image_principale");
			String lieux = SearchActivity.extractDataFromDb(idxloc,"Siteville");
			String pays = SearchActivity.extractDataFromDb(idxloc,"Sitepays");
			int id= idxloc;
			NoticeOeuvre notice = new NoticeOeuvre(nomOvreu, artiste,idxloc); //cr�� les notices
			try
			{
				int annee = Integer.valueOf(date);
				notice.setAnnee(annee);
			}catch(Exception e) {notice.setAnnee(1000000);}
			
			notice.setPhoto(photo);
			notice.setDistance(-1);
			notice.setPays(pays);
			notice.setVille(lieux);
			Log.d(DEBUG_TAG,"name ="+artiste);
			if ((artiste!=null) && (nomOvreu !=null) && (nomOvreu !=null))
			{
				NoticeCompar n = new NoticeCompar(notice, j);
				compNoticeList.add(n);
				//listNotice.add(notice );//on ajoute la notice
			}
			//Toast.makeText(this, "+1 notice", Toast.LENGTH_SHORT);
			
			/**chargement de la liste photo
			 * 
			 */
			/**try
        	{
            	File fimage = SearchActivity.checkIfImageFileExists("thumb_"+photo) ;
    			if(fimage == null || !listPhoto.contains(photo))//l'image n'existe pas deja dans listphoto
    			{
    				listPhoto.add("thumb_"+photo);
    				Log.d(DEBUG_TAG+"/ajoutPhotoToLoad", photo);
    			}
        	}
        	catch(Exception e){}**/
			j++;
		}
		Collections.sort(compNoticeList, NoticeCompar.NoticeDateComparator);
		List<NoticeOeuvre> listNotice = new ArrayList<NoticeOeuvre>();
        for(int i=0;i< compNoticeList.size();i++)
		{
			listNotice.add(compNoticeList.get(i).getOeuvre());
		}
		//NoticeAdapter noticeAdapter = new NoticeAdapter(this, listNotice);
		noticeAdapter = new NoticeAdapterWithDistance(this, listNotice);

        mListViewSelection = (ListView) findViewById(R.id.list_view);
        mListViewSelection.setAdapter(noticeAdapter);
		
		mListViewSelection.setAdapter(noticeAdapter);
		//Enfin on met un �couteur d'�v�nement sur notre listView
		mListViewSelection.setOnItemClickListener(new OnItemClickListener() {
			@Override
        	@SuppressWarnings("unchecked")
         	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				//on r�cup�re la HashMap contenant les infos de notre item (titre, description, img)
				//lvSelection.getItemAtPosition(position).getClass();
        		Log.d(DEBUG_TAG, "class ="+mListViewSelection.getItemAtPosition(position).getClass());
        		/**NoticeOeuvre noticeSelected = (NoticeOeuvre) lvSelection.getItemAtPosition(position);
        		int idfrag = noticeSelected.getId();
				bundle.putInt(ShowNoticeActivity.ARG_FRAGMENT,idfrag );//idfrag, c'est l'id dans la BDD
    			Intent intent = new Intent(getApplication(), ShowNoticeActivity.class);
    			intent.putExtras(bundle);
    			startActivity(intent);**/
    			
        	}
         });
 
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.show();
			actionBar.setTitle("Rechercher");
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowTitleEnabled(true);
			//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
		}
    }
	

	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	/// HELPER ROUTINES ///
	
	int getEntryNumberForFragment(int idx){
		if (mBundle == null) return -1;
		return mBundle.getInt(Integer.toString(idx));
	}
	
	int getNumberOfEntries(){
		int v = mBundle.getInt(SearchActivity.NB_ENTRIES);
		Log.d(DEBUG_TAG, "getNumberOfEntries : " + v);
		return v;
	}
	
	Double getCurrentLatitude(){
		Double v = mBundle.getDouble(SearchActivity.CURRENT_LAT );
		Log.d(DEBUG_TAG, "getCurrentLatitude : " + v);
		return v;
	}
	
	Double getCurrentLongitude(){
		Double v = mBundle.getDouble(SearchActivity.CURRENT_LONG );
		Log.d(DEBUG_TAG, "getCurrentLongitude : " + v);
		return v;
	}
	
	private void showMap(){
		Intent intent = new Intent(getApplication(),MapActivity.class);
		if (getNumberOfEntries() == 1) 
		{
			mBundle.putInt(SearchActivity.MAP_FOCUS_NOTICE,1);
		}
		intent.putExtras(mBundle);
		startActivity(intent);
		finish();
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		
		if(itemId == android.R.id.home)
		{
			super.onBackPressed();
			finish();
			return true;
		}
		
		if(itemId == R.id.action_map)
		{
			this.showMap();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.discovart, menu);
        return super.onCreateOptionsMenu(menu);
	}

	@Override
	public NoticeAdapterWithDistance getNoticeAdapter() {
		// TODO Auto-generated method stub
		return this.noticeAdapter;
	}


	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return ResultActivity.this;
	}


	@Override
	public ImageView getImageView() {
		// TODO Auto-generated method stub
		return null;
	}
}
