package fr.atlasmuseum.search;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import fr.atlasmuseum.R;
import fr.atlasmuseum.contribution.Contribution;
import fr.atlasmuseum.contribution.ListChampsNoticeModif;
import fr.atlasmuseum.contribution.MainContribActivity;
import fr.atlasmuseum.main.AtlasError;
import fr.atlasmuseum.main.MainActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
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
	
	private TextView txtview_titre;
	private TextView txtview_materiaux;
	private TextView txtview_auteur;
	private TextView txtview_ville_pays;
	TextView txtview_description;
	private TextView txtview_annee;
	private TextView txtview_nature;
	private TextView txtview_couleur;
	
	public ImageView imgView;//pour afficher l'image de la notice
	private TextView txtview_mots_cle;
	private TextView txtview_nomsite;
	private TextView txtview_nomville;
	private TextView txtview_nomRegion;
	private TextView txtview_nomPays;
	private TextView txtview_mouvement;
	private TextView txtview_pmr;
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
    	
    	//titre
    	String titre = SearchActivity.extractDataFromDb(idx,"titre");
    	txtview_titre = (TextView) findViewById(R.id.notice_titre);
    	if (titre != null && !titre.equals("?"))
	    	{
    			txtview_titre.setText(titre);//affiche le titre
	    	} 
    	else
	    	{
    			txtview_titre.setText("Pas de titre");
	    	}
    	
    	//Auteur
    	String artiste = SearchActivity.extractDataFromDb(idx,"artiste");
    	txtview_auteur = (TextView) findViewById(R.id.notice_artiste);
    	if (artiste != null  && !artiste.equals("?"))
	    	{
    			txtview_auteur.setText(artiste);//affiche le titre
	    	} 
    	else
	    	{
    			txtview_auteur.setText("Unknow");
	    	}
    	
    	//Annee
    	String annne = SearchActivity.extractDataFromDb(idx,"inauguration");
    	txtview_annee = (TextView) findViewById(R.id.notice_annee);
    	if (annne != null  && !annne.equals("?"))
	    	{
    			txtview_annee.setText(annne);//affiche le titre
	    	} 
    	else
	    	{
    			txtview_annee.setVisibility(View.GONE);
	    	}
    	
    	//Ville - Pays
    	String ville = SearchActivity.extractDataFromDb(idx,"Siteville");
    	String pays = SearchActivity.extractDataFromDb(idx,"Sitepays");
    	String ville_pays = ville+"-"+pays;
    	txtview_ville_pays = (TextView) findViewById(R.id.notice_ville_pays);
    	if (ville_pays != null  && !ville_pays.equals("?-?"))
	    	{
    			txtview_ville_pays.setText(ville_pays);//affiche le titre
	    	} 
    	else
	    	{
	    		
    			txtview_ville_pays.setVisibility(View.GONE);;
	    	}
    	
    	//Nature
    	String nature = SearchActivity.extractDataFromDb(idx,"nature");
    	txtview_nature = (TextView) findViewById(R.id.oeuvre_nature_value);
    	if (nature != null  && !nature.equals("?"))
	    	{
    		  txtview_nature.setText(nature);//affiche le titre
	    	} 
    	else
	    	{
    			RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_oeuvre_nature);
    			r.setVisibility(View.GONE);;
	    	}
    	
    	//Couleur
    	String couleur = SearchActivity.extractDataFromDb(idx,"couleur").trim();
    	Log.d(DEBUG_TAG, "*******couleur ="+couleur);
    	txtview_couleur = (TextView) findViewById(R.id.oeuvre_couleur_value);
    	if (couleur != null  && !couleur.equals("?") && !couleur.trim().equals(""))
	    	{
    			txtview_couleur.setText(couleur);//affiche le titre
	    	} 
    	else
	    	{
	    		RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_oeuvre_couleur);
				r.setVisibility(View.GONE);
	    	}
    	
    	//Materiaux
    	String materiaux = SearchActivity.extractDataFromDb(idx,"materiaux");
    	txtview_materiaux = (TextView) findViewById(R.id.oeuvre_materiauw_value);
    	if (materiaux != null  && !materiaux.equals("?"))
	    	{
    		txtview_materiaux.setText(materiaux);//affiche le titre
	    	} 
    	else
	    	{
	    		RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_oeuvre_materiaux);
				r.setVisibility(View.GONE);
	    	}
    	//Description
    	String description = SearchActivity.extractDataFromDb(idx,"description");
    	txtview_description = (TextView) findViewById(R.id.oeuvre_description_value);
    	if (description != null  && !description.equals("?"))
	    	{
    		txtview_description.setText(description);//affiche le titre
	    	} 
    	else
	    	{
    		RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_oeuvre_description);
			r.setVisibility(View.GONE);
	    	}
    	
    	//Mots cl�s
    	String mots_cles = SearchActivity.extractDataFromDb(idx,"mot_cle");
    	txtview_mots_cle = (TextView) findViewById(R.id.oeuvre_mots_cles_value);
    	if (mots_cles != null  && !mots_cles.equals("?"))
	    	{
    		txtview_mots_cle.setText(mots_cles);//affiche le titre
	    	} 
    	else
	    	{
    		RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_oeuvre_mots_cles);
			r.setVisibility(View.GONE);
	    	}
    	
    	//Contexte
    	String contexte = SearchActivity.extractDataFromDb(idx,"contexte_production");
    	txtview_mots_cle = (TextView) findViewById(R.id.oeuvre_contexte_value);
    	if (contexte != null  && !contexte.equals("?"))
	    	{
    		txtview_mots_cle.setText(contexte);//affiche le titre
	    	} 
    	else
	    	{
    		RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_oeuvre_contexte);
			r.setVisibility(View.GONE);
	    	}
    	
    	//nom Site
    	String nomsite = SearchActivity.extractDataFromDb(idx,"Sitenom");
    	txtview_nomsite = (TextView) findViewById(R.id.site_nomsite_value);
    	if (nomsite != null  && !nomsite.equals("?"))
	    	{
    		txtview_nomsite.setText(nomsite);//affiche le titre
	    	} 
    	else
	    	{
    		RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_site_nomsite);
			r.setVisibility(View.GONE);
	    	}
    	
    	//nom ville
    	String nomVille = SearchActivity.extractDataFromDb(idx,"Siteville");
    	txtview_nomville = (TextView) findViewById(R.id.site_ville_value);
    	if (nomVille != null  && !nomVille.equals("?"))
	    	{
    		txtview_nomville.setText(nomVille);//affiche le titre
	    	} 
    	else
	    	{
    		RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_site_ville);
			r.setVisibility(View.GONE);
	    	}
    	//nom region
    	String nomRegion = SearchActivity.extractDataFromDb(idx,"Siteregion");
    	txtview_nomRegion = (TextView) findViewById(R.id.site_region_value);
    	if (nomRegion != null  && !nomRegion.equals("?"))
	    	{
    		txtview_nomRegion.setText(nomRegion);//affiche le titre
	    	} 
    	else
	    	{
    		RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_site_region);
			r.setVisibility(View.GONE);
	    	}
    	
    	//nom pays
    	String nomPays = SearchActivity.extractDataFromDb(idx,"Sitepays");
    	txtview_nomPays = (TextView) findViewById(R.id.site_pays_value);
    	if (nomPays != null  && !nomPays.equals("?"))
	    	{
    		txtview_nomPays.setText(nomPays);//affiche le titre
	    	} 
    	else
	    	{
    		RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_site_pays);
			r.setVisibility(View.GONE);
	    	}
    	
    	//artiste mouvement
    	String mouvement = SearchActivity.extractDataFromDb(idx,"mouvement_artistes");
    	txtview_mouvement = (TextView) findViewById(R.id.artiste_mouvement_value);
    	if (mouvement != null  && !mouvement.equals("?"))
	    	{
    			txtview_mouvement.setText(mouvement);//affiche le titre
	    	} 
    	else
	    	{
    		RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_artiste_mouvement);
			r.setVisibility(View.GONE);
	    	}

    	//site pmr = Personne � Mobilit� R�duite
    	String pmr = SearchActivity.extractDataFromDb(idx,"Sitepmr");
    	txtview_pmr = (TextView) findViewById(R.id.site_pmr_value);
    	if (pmr != null  && !pmr.equals("?"))
	    	{
    		txtview_pmr.setText(pmr);//affiche le titre
	    	} 
    	else
	    	{
    		RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_site_pmr);
			r.setVisibility(View.GONE);
	    	}
    	
    	//details site
    	String detail_site =  SearchActivity.extractDataFromDb(idx,"Sitedetails");
    	txtview_pmr = (TextView) findViewById(R.id.site_site_detailsite_value);
    	if (detail_site != null  && !detail_site.equals("?"))
	    	{
    			txtview_pmr.setText(detail_site);//affiche le titre
	    	} 
    	else
	    	{
    			RelativeLayout r = (RelativeLayout) findViewById(R.id.relativ_site_detailsite);
    			r.setVisibility(View.GONE);
	    	}
    	
    	
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
			// Envoi de la donn�e pour la contribution
			int idnotice = Integer.parseInt(SearchActivity.extractDataFromDb(idx,"id"));
			String description = SearchActivity.extractDataFromDb(idx,"description");
			String artiste = SearchActivity.extractDataFromDb(idx,"artiste");
			String titre = SearchActivity.extractDataFromDb(idx,"titre");
			String couleur = SearchActivity.extractDataFromDb(idx,"couleur");
			String  date_inauguration = SearchActivity.extractDataFromDb(idx,"inauguration");
			String  materiaux = SearchActivity.extractDataFromDb(idx,"materiaux");
			String image_principale = SearchActivity.extractDataFromDb(idx,"image_principale");
			String nomsite = SearchActivity.extractDataFromDb(idx,"Sitenom");
			String nature = SearchActivity.extractDataFromDb(idx,"nature");
			String detail_site = SearchActivity.extractDataFromDb(idx,"Sitedetails");
			
			detail_site=setEmptyString(detail_site);
			nature=setEmptyString(nature);
			description=setEmptyString(description);
			artiste=setEmptyString(artiste);
			couleur=setEmptyString(couleur);
			date_inauguration=setEmptyString(date_inauguration);
			materiaux=setEmptyString(materiaux);
			image_principale=setEmptyString(image_principale);
			nomsite =setEmptyString(nomsite);
			
			
			double longi = Double.parseDouble(SearchActivity.extractDataFromDb(idx,"longitude"));
			double lati = Double.parseDouble(SearchActivity.extractDataFromDb(idx,"latitude"));
			Log.d(DEBUG_TAG, "value of longitude =" +String.valueOf(longi)+";"+ String.valueOf(lati));
			if ((longi != 0.0) && (lati != 0.0))
			{
				
				bundle.putDouble(Contribution.LATITUDE, longi);
				bundle.putDouble(Contribution.LONGITUDE, lati);
			}
			
			
			Log.d("description", description);
			Log.d("artiste", artiste);
			Log.d("couleur", couleur);
			Log.d("date_inauguration", date_inauguration);
			Log.d("materiaux", materiaux);
			Log.d("image_principale", image_principale);
			Log.d("Sitenom", nomsite);
			Log.d("nature", nature);
			Log.d("detail_site", detail_site);
			Log.d("id", idnotice+"");
			
			bundle.putString(Contribution.TITRE, titre);
			bundle.putString(Contribution.ARTISTE, artiste);
			bundle.putInt(Contribution.IDNOTICE, idnotice);
			bundle.putString(Contribution.COULEUR, couleur);
			bundle.putString(Contribution.DESCRIPTION, description);
			bundle.putString(Contribution.PHOTO, image_principale);
			bundle.putString(Contribution.DATE_INAUGURATION, date_inauguration);
			bundle.putString(Contribution.MATERIAUx, materiaux);
			bundle.putString(Contribution.NOM_SITE, nomsite);
			bundle.putString(Contribution.NATURE, nature);
			bundle.putString(Contribution.DETAIL_SITE, detail_site);
			
			
			//pour recuperer une eventuelle modification non terminer dans contribution 
			if(ListChampsNoticeModif.cPref != null && ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.notice_idOeuvre))
			{
				
				if(ListChampsNoticeModif.cPref.getInt(ListChampsNoticeModif.notice_idOeuvre, -1) != idnotice)
				{
					bundle.putBoolean(ListChampsNoticeModif.erasepref, true); //va permettre de erase les preferences de listnoticemodif
				}
				else
				{
					Log.d("shownotice", "les id correspondent");
					bundle.putBoolean(ListChampsNoticeModif.erasepref, false); //va permettre de erase les preferences de listnoticemodif
				}
				Log.d(DEBUG_TAG, "cpref id=" + ListChampsNoticeModif.cPref.getInt(ListChampsNoticeModif.notice_idOeuvre, -1) );
				Log.d(DEBUG_TAG, "idnotice id=" + idnotice );
			}
			else
			{
				bundle.putBoolean(ListChampsNoticeModif.erasepref, true);
				Log.d("shownotice", "idNotice non existant");
			}
			
			
			String uniqueID = UUID.randomUUID().toString();
			bundle.putString(Contribution.LOCALID, uniqueID);
			
			Intent intent = new Intent(this, ListChampsNoticeModif.class);
			intent.putExtras(bundle);
			Log.d(DEBUG_TAG, "Going to Contribution Notice");
			
			startActivityForResult(intent, this.REQUEST_CONTRIB);
		}
   
		public String setEmptyString(String v)
		{
			if(v.trim().equals("?"))
			{
				Log.d("?", "true");
				return "";
			}
			return v;
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
