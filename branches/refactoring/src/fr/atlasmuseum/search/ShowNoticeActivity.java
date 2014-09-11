package fr.atlasmuseum.search;

import java.io.File;
import java.io.IOException;
import fr.atlasmuseum.R;
import fr.atlasmuseum.contribution.Contribution;
import fr.atlasmuseum.contribution.Contribution2;
import fr.atlasmuseum.contribution.ListChampsNoticeModif;
import fr.atlasmuseum.main.AtlasError;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ShowNoticeActivity extends Activity{

	private static final String DEBUG_TAG = "AtlasMuseum/ShowNotice";
	public static final String ARG_FRAGMENT = "IDFragment";
	private int idFragment;
	private Button btn_map;
	private Button btn_acces_wikipedia;
	private RelativeLayout mButtonPhoto;
	
	private Bundle bundle;
	private int idx;
	
	public ImageView imgView;//pour afficher l'image de la notice
	private int REQUEST_CONTRIB=12345874;
	
	TextView creditphoto;
	
	String url; //pour acces wiki
	
    @Override
    public void onCreate(Bundle savedInstanceState){
    	
    	super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.show_notice_layout_new);
        final String ARG_FRAGMENT = "IDFragment";
    	Log.d(DEBUG_TAG, "onCreateView");
    	
    	btn_map = (Button) findViewById(R.id.btn_map);
    	imgView = (ImageView) findViewById(R.id.imageView1);
    	ImageView littlemapbutton = (ImageView) findViewById(R.id.littlemapbutton);
    	btn_acces_wikipedia = (Button) findViewById(R.id.btn_acces_wiki);
    	btn_acces_wikipedia.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	showAccesWiki();
	        	
	        }
    	});
    	
    	btn_map.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	showMap();
	        }
    	});
    	
    	littlemapbutton.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	showMap();
	        }
    	});
    	
    	bundle = getIntent().getExtras();
    	idFragment = bundle.getInt(ARG_FRAGMENT);
    	Log.d(DEBUG_TAG, "idBDD = "+idFragment);
    	
    	this.idx = idFragment;//idfrag = id dans la bdd
    	Log.i(DEBUG_TAG, "entry in being activated "+ idFragment+"-" +idx);
    	
    	creditphoto = (TextView) findViewById(R.id.credit_photo); //zone credit photo
    	
    	creditphoto.setText(SearchActivity.extractDataFromDb(idx,"creditphoto"));
    	
    
    	//url
    	this.url = SearchActivity.extractDataFromDb(idx,"url");
    	
    	
    	
    	Contribution2 contribution = new Contribution2(this);
    	contribution.updateFromDb(idx);
    	
    	TextView txtview_titre = (TextView) findViewById(R.id.notice_titre);
    	String titre = contribution.getProperty(Contribution.TITRE).getValue();
    	txtview_titre.setText(titre);
    	//txtview_titre.setText("Pas de titre");
    	
    	TextView txtview_auteur = (TextView) findViewById(R.id.notice_artiste);
    	String artiste = contribution.getProperty(Contribution.ARTISTE).getValue();
    	txtview_auteur.setText(artiste);
    	//txtview_auteur.setText("Unknown");

    	TextView txtview_annee = (TextView) findViewById(R.id.notice_annee);
    	String annee = contribution.getProperty(Contribution.DATE_INAUGURATION).getValue();
		txtview_annee.setText(annee);
		//txtview_annee.setVisibility(View.GONE);
		
		TextView txtview_ville_pays = (TextView) findViewById(R.id.notice_ville_pays);
    	String ville = contribution.getProperty("Siteville").getValue();
    	String pays = contribution.getProperty("Sitepays").getValue();
    	txtview_ville_pays.setText(ville+"-"+pays);
    	//txtview_ville_pays.setVisibility(View.GONE);
    	
    	TextView txtview_nature = (TextView) findViewById(R.id.oeuvre_nature_value);
    	String nature = contribution.getProperty(Contribution.NATURE).getValue();
		txtview_nature.setText(nature);
		//RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_oeuvre_nature);
		//r.setVisibility(View.GONE);;
    	
		TextView txtview_couleur = (TextView) findViewById(R.id.oeuvre_couleur_value);
		String couleur = contribution.getProperty(Contribution.COULEUR).getValue();
		txtview_couleur.setText(couleur);
		//RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_oeuvre_couleur);
		//r.setVisibility(View.GONE);
		
		TextView txtview_materiaux = (TextView) findViewById(R.id.oeuvre_materiauw_value);
		String materiaux = contribution.getProperty(Contribution.MATERIAUX).getValue();
		txtview_materiaux.setText(materiaux);
		//RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_oeuvre_materiaux);
		//r.setVisibility(View.GONE);
    	
		TextView txtview_description = (TextView) findViewById(R.id.oeuvre_description_value);
		String description = contribution.getProperty(Contribution.DESCRIPTION).getValue();
		txtview_description.setText(description);
		//RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_oeuvre_description);
		//r.setVisibility(View.GONE);
		
		TextView txtview_mots_cle = (TextView) findViewById(R.id.oeuvre_mots_cles_value);
		String mots_cles = contribution.getProperty("mot_cle").getValue();
		txtview_mots_cle.setText(mots_cles);
		//RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_oeuvre_mots_cles);
		//r.setVisibility(View.GONE);

		TextView txtview_context = (TextView) findViewById(R.id.oeuvre_contexte_value);
    	String contexte = contribution.getProperty("contexte_production").getValue();
		txtview_context.setText(contexte);
		//RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_oeuvre_contexte);
		//r.setVisibility(View.GONE);
    	
		TextView txtview_nomsite = (TextView) findViewById(R.id.site_nomsite_value);
		String nomsite = contribution.getProperty(Contribution.NOM_SITE).getValue();
		txtview_nomsite.setText(nomsite);
		//RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_site_nomsite);
		//r.setVisibility(View.GONE);
		
		TextView txtview_nomville = (TextView) findViewById(R.id.site_ville_value);
		String nomville = contribution.getProperty("Siteville").getValue();
		txtview_nomville.setText(nomville);
		//RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_site_ville);
		//r.setVisibility(View.GONE);
		
		TextView txtview_nomregion = (TextView) findViewById(R.id.site_region_value);
		String nomregion = contribution.getProperty("Siteregion").getValue();
		txtview_nomregion.setText(nomregion);
		//RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_site_region);
		//r.setVisibility(View.GONE);
    	
		TextView txtview_nompays = (TextView) findViewById(R.id.site_pays_value);
		String nompays = contribution.getProperty("Sitepays").getValue();
		txtview_nompays.setText(nompays);
		//RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_site_pays);
		//r.setVisibility(View.GONE);
    	
		TextView txtview_mouvement = (TextView) findViewById(R.id.artiste_mouvement_value);
		String mouvement = contribution.getProperty("mouvement_artistes").getValue();
		txtview_mouvement.setText(mouvement);
		//RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_artiste_mouvement);
		//r.setVisibility(View.GONE);

		TextView txtview_pmr = (TextView) findViewById(R.id.site_pmr_value);
		String pmr = contribution.getProperty(Contribution.PMR).getValue();
		txtview_pmr.setText(pmr);
		//RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_site_pmr);
		//r.setVisibility(View.GONE);
    	
		TextView txtview_detail_site = (TextView) findViewById(R.id.site_site_detailsite_value);
		String detail_site = contribution.getProperty(Contribution.DETAIL_SITE).getValue();
		txtview_detail_site.setText(detail_site);
		//RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_site_detailsite);
		//r.setVisibility(View.GONE);

    	
    	//fin chargement infos description de la notice
    	
    	
    	
    	
    	
    	mButtonPhoto = (RelativeLayout) findViewById(R.id.image_loading);
    	if (showExistingPhoto() == false) 
    	{
    		imgView.setVisibility(View.GONE);
    		mButtonPhoto.setVisibility(View.VISIBLE);
    		mButtonPhoto.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				Log.i(DEBUG_TAG, "Button Photo activation");
    				showPhoto();
    			}
    		});
    	} 
    	else 
    	{ // hide button
    		mButtonPhoto.setVisibility(View.GONE);
    		imgView.setVisibility(View.VISIBLE);
    	}
    	
    	//pour autoriser le retour en cliquant sur l'icone de l'application dans l'action bar
		////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////
		//ACTION BAR
		ActionBar actionBar = getActionBar();
		if (actionBar != null)
		{
			actionBar.show();
			
			actionBar.setTitle(this.getResources().getString(R.string.notice_oeuvre));
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setHomeButtonEnabled(true);

			actionBar.setDisplayHomeAsUpEnabled(true);
			//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
				}
    }//fin onCreate


	  
    protected void showAccesWiki() {
		// TODO Auto-generated method stub
    	//Toast.makeText(this, "Acces wikipedia", Toast.LENGTH_SHORT).show();
    	
    	
    	Intent i = new Intent(Intent.ACTION_VIEW);
    	i.setData(Uri.parse(this.url));
    	startActivity(i);
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
    	
		if (itemId == R.id.action_contrib)
		{
			Contribute();
			return true;
		}
		else return false;
		    
    }
    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_notice_menu, menu);
        return super.onCreateOptionsMenu(menu);
	}
	
    private void showMap(){
    	Bundle extra = new Bundle();
		Intent intent = new Intent(this, MapActivity.class);
		extra.putInt("0",idx);
		Log.i(DEBUG_TAG, "idx for MAP is "+idx);
		extra.putInt(SearchActivity.NB_ENTRIES,1);
		extra.putInt(SearchActivity.MAP_FOCUS_NOTICE,1);
		intent.putExtras(extra);
		startActivity(intent);
	}
    

    private Boolean showExistingPhoto(){
    	ImageView imgView =(ImageView) findViewById(R.id.imageView1);
    	Bitmap bmSmall = null;
    	try {
    		String fichierImage = SearchActivity.extractDataFromDb(idx,"image_principale");
    		Log.d(DEBUG_TAG, "id = "+idx);
    		Log.d(DEBUG_TAG, "image file = "+fichierImage);
    		File fimage = SearchActivity.checkIfImageFileExists(fichierImage) ;
    		if (fimage != null) {
    			Log.d(DEBUG_TAG, "showExistingPhoto Image file exist");
    			bmSmall = BitmapFactory.decodeFile(fimage.getAbsolutePath());
    			imgView.setImageBitmap(bmSmall);
    			return true;
    		}
    	} catch (IOException e) {
    		Log.d(DEBUG_TAG, "Could not view photo");
    		AtlasError.showErrorDialog(this, "5.1", "");// ERROR "notice n'a pas pu �tre visualis�"
    		//Toast.makeText(getActivity(),"L'image de cette notice n'a pu �tre visualis�", Toast.LENGTH_SHORT).show();
    	}
    	return false;
    }
    
    private void showPhoto(){
    	
    	//ImageView imgView =(ImageView) findViewById(R.id.imageView1);
    	
    	LoadingPhotoAsync upl = new LoadingPhotoAsync(this, this.idx);
    	upl.execute();
    	
    }
    
    
    // here we select the entries to display
    Bundle selectEntries(){
    	Bundle extra = new Bundle();
    	// temporary setting
    	extra.putInt(SearchActivity.MAP_FOCUS_NOTICE,1);
    	extra.putInt(SearchActivity.NB_ENTRIES,1);
    	extra.putInt(Integer.toString(0),getEntryNumberForFragment(idFragment));
    	extra.putDouble(SearchActivity.CURRENT_LAT,0.0);
    	extra.putDouble(SearchActivity.CURRENT_LONG,0.0);
    	return extra;
    }
    
   
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
	Log.d(DEBUG_TAG, "getNumberOfEntries : " + v);
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
	

		

		public void Contribute()
		{
			//String uniqueID = UUID.randomUUID().toString();
			//bundle.putString(Contribution.LOCALID, uniqueID);

			Contribution2 contribution = new Contribution2(this);
			contribution.updateFromDb(idx);
			bundle.putSerializable("contribution", contribution);
			Intent intent = new Intent(this, ListChampsNoticeModif.class);
			intent.putExtras(bundle);
			startActivityForResult(intent, this.REQUEST_CONTRIB);
		}
   
		public ImageView getImageVew() {
			return this.imgView;
		}

		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		    // TODO Auto-generated method stub
		    Log.d(DEBUG_TAG, "result code="+requestCode);
		    if(requestCode == this.REQUEST_CONTRIB)
		    {
		    	if(resultCode == RESULT_OK)
		    	{

			    	Toast.makeText(this, getResources().getString(R.string.contrib_envoi_success), Toast.LENGTH_SHORT).show();
		    	}
		    }
		    else
		    {
		    	super.onActivityResult(requestCode, resultCode, data);
		    }
		    
		}
}
