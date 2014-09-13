package fr.atlasmuseum.contribution;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import fr.atlasmuseum.R;
import fr.atlasmuseum.compte.Authentification;
import fr.atlasmuseum.compte.ConnexionActivity;
import fr.atlasmuseum.contribution.Contribution.champ_status;
import fr.atlasmuseum.main.AtlasError;
import fr.atlasmuseum.main.MainActivity;
import fr.atlasmuseum.search.loadPhotoInterface;
import fr.atlasmuseum.search.loadingPhoto2;

public class ListChampsNoticeModif extends Activity implements loadPhotoInterface {
	private static final String DEBUG_TAG = "AtlasMuseum/ListChampAct";

	private Bundle mBundle;
	private Contribution2 mContribution;
	private int mNoticeId;
	
	private List<ContributionProperty> mList;
	static String CHAMPS_ITEM = "champs_item";

	private RelativeLayout mLoadPhoto;
	private ImageView mImageNotice;
	private RelativeLayout mBlockModifierImage;

	static ContribXml mContribXml;
	public ContributionPropertyAdapter mAdapter;
	String IDLocal;

	//IMPORTANT
	//lors de la modification de cPref, faire attention à faire commit() 
	public static final String SHARED_PREFERENCES = "fr.atlasmuseum";
	public static SharedPreferences cPref;

	//String pour la ListView Ajouter/Modifier _ Utiliser pour récupérer les valeurs associées à l'aide de cPref
	//exemple: String ajoutTitreValue = cPref.getString(ajout_titre, "");
	final static String ajout_titre = "Ajouter un titre" ;
	final static String modif_titre = "Modifier le titre";
	final static String ajout_photo = "Ajouter une photo";
	final static String modif_photo ="Modifier la photo";
	final static String ajout_couleur = "Ajouter une couleur";
	final static String modif_couleur = "Modifier la couleur";
	final static String ajout_date = "Ajouter la date d'inauguration";
	final static String modif_date = "Modifier la date d'inauguration";
	final static String ajout_materiaux = "Ajouter des matériaux";
	final static String modif_materiaux = "Modifier les matériaux";
	final static String ajout_description = "Ajouter une description";
	final static String modif_description = "Modifier la description";
	final static String ajout_artiste = "Ajouter un artiste";
	final static String modif_artiste = "Modifier l'artiste";

	final static String ajout_nature = "Ajouter la nature de l'oeuvre";
	final static String modif_nature = "Modifier la nature de l'oeuvre";

	final static String ajout_nomsite = "Ajouter le nom du site";
	final static String modif_nomsite = "Modifier le nom du site";

	final static String ajout_autre = "Autres informations";
	final static String ajout_etat = "Ajouter l'état de conservation";
	final static String ajout_petat = "Précision sur l'état de conservation";
	final static String modif_etat = "Modifier l'état de conservation";

	final static String modif_petat = "Modifier la précision sur l'état de conservation";

	final static String  ajout_detailsite = "Ajouter detail site";
	final static String  modif__detailsite = "Modifier detail site";

	final static String ajout_pmr = "Définir l'accessibilité du site";
	final static String ajout_localisation = "Ajouter localisation";
	final static String modif_localisation = "Modifier la localisation";
	final static String creationNotice = "creeNoticeVrai/Faux"; //valeur de cpref, VRAI si cest une création

	static String champs_a_modifier = "champs a modifier ";//utilisé dans Activity, détermine quelle valeur de cPref est modifiée

	public static String erasepref = "erase pref";//utiliser comme clé pour bundle, détermine si on doit effacer ou pas preference



	//valeur utilisée dans ShowNoticeAcitivty pour déterminer si la contribution en cours
	//peut être continuée ou pas (dans le cas où l'utilisateur fait des retours successifs)
	public final static String notice_idOeuvre = "id de la notice"; //A utiliser en tant que clé dans cpref
	static final int REQUEST_FINISH = 158742;
	public static final int RESUME = 1451515;

	static final int REQUEST_MODIFY_PROPERTY = 1;

	//liste des valeur des champs connus de la notice de référence 
	String notice_titre;
	String notice_artiste;
	String notice_couleur;
	String notice_description;
	String notice_dateinauguration;
	String notice_materiaux;
	String notice_nomsite;
	String notice_photo;
	Double notice_latitude;
	Double notice_longitude;
	String notice_nature;
	String notice__detailsite;

	//liste String du contenu de Cpref
	//Récupère les champs déjà entrés par le user
	String ajoutTitreValue ;
	String TitreValue;
	String ajoutPhotoValue;
	String PhotoValue;
	String ajoutCouleurValue ;
	String CouleurValue ;
	String ajoutDateValue ;
	String modif_dateValue;


	String ajoutMateriauxValue;
	String modif_materiauxValue;

	String ajoutNatureValue;
	String modif_NatureValue;

	String ajout_descriptionValue ;
	String modif_descriptionValue;
	String ajoutArtisteValue;
	String ArtisteValue;

	String ajoutNomSiteValue;
	String modif_NomSiteValue;

	String ajoutAutreValue;

	String ajoutEtatValue;

	String ajoutPetatValue;
	String ajoutPmrValue;

	String ajoutDetailSiteValue;
	String modif_DetailSiteValue;

	//valeur de la localisation, dans le cas ajout ou modif
	Double latitude;
	Double longitude;
	//string pour le bundle
	public final static String LATITUDE="latitude";
	public final static String LONGITUDE="longitude";
	boolean ajoutLocalisationValue;
	boolean modifLocalisationValue;

	private int REQUEST_CONNEXION=1450233;
	private int REQUEST_localisation;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.contrib_list_modif);

		Log.d(DEBUG_TAG, "onCreate()");
		
		mBundle = getIntent().getExtras();
		if( ! mBundle.containsKey("contribution") ) {
			// TODO: handle this case, even if it should never happen
		}
		Log.d(DEBUG_TAG, "onCreate(): Retrieve Contribution from bundle");
		mContribution = (Contribution2) mBundle.getSerializable("contribution");
		mNoticeId = mContribution.getNoticeId();
		
		mImageNotice = (ImageView) findViewById(R.id.imageView1);

		mBlockModifierImage = (RelativeLayout) findViewById(R.id.relativeLayoutPhotoModifier);

		mList = new ArrayList<ContributionProperty>();
		mAdapter = new ContributionPropertyAdapter(ListChampsNoticeModif.this, mList);
		ListView listView = (ListView) findViewById(R.id.list_view);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id)
			{
				ContributionProperty prop = (ContributionProperty) mAdapter.getItem(position);
				String field = (String) prop.getDbField();
				Log.d(DEBUG_TAG, "Edit " + field);
				
				if( field == Contribution.LATITUDE ||
				    field == Contribution.LONGITUDE) {
					// TODO
					showLocationChangeAlertToUser();
				}
				else {
					//vue modification avec champ saisi/listview checkbox ou radioButton
					Bundle bundle = new Bundle();
					bundle.putSerializable("position", position);
					bundle.putSerializable("property", prop);
					Intent intent = new Intent(getApplication(), ModifActivity.class);
					intent.putExtras(bundle);
					startActivityForResult(intent, REQUEST_MODIFY_PROPERTY);	
				}
			}
		});	



		ImageButton buttoModifPhoto = (ImageButton) findViewById(R.id.modif_photo);
		buttoModifPhoto.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) {
				String champ = "";
				boolean f = cPref.getBoolean(ListChampsNoticeModif.creationNotice, false);//true si creation notice

				if(f) {
					Log.d(DEBUG_TAG, "ajout enclenché");
					champ = ListChampsNoticeModif.ajout_photo;
				}
				else {
					Log.d(DEBUG_TAG, "modification enclenchée");
					champ = ListChampsNoticeModif.modif_photo;
				}
				mBundle.putString(ListChampsNoticeModif.CHAMPS_ITEM, champ);
				getSharedPreferences(ContribPhoto.SHARED_PREFERENCES, Context.MODE_PRIVATE).edit().putString("photoPath", "").commit();
				getSharedPreferences(ContribPhoto.SHARED_PREFERENCES, Context.MODE_PRIVATE).edit().putString("lastPhotoValid", "").commit();
				Log.i(DEBUG_TAG, "photo = "+champ);
				
				Intent intent = new Intent(getApplication(), ContribPhoto.class);
				intent.putExtras(mBundle);
				startActivityForResult(intent, REQUEST_FINISH);
			}
		});

		mLoadPhoto = (RelativeLayout) findViewById(R.id.image_loading);
		mLoadPhoto.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) {
				String champ = ListChampsNoticeModif.ajout_photo;
				mBundle.putString(ListChampsNoticeModif.CHAMPS_ITEM, champ);
				getSharedPreferences(ContribPhoto.SHARED_PREFERENCES, Context.MODE_PRIVATE).edit().putString("photoPath", "").commit();
				getSharedPreferences(ContribPhoto.SHARED_PREFERENCES, Context.MODE_PRIVATE).edit().putString("lastPhotoValid", "").commit();
				Log.i(DEBUG_TAG, "photo = "+champ);
				Intent intent = new Intent(getApplication(), ContribPhoto.class);
				intent.putExtras(mBundle);
				startActivityForResult(intent, REQUEST_FINISH);
			}
		});

		RelativeLayout annuler_button = (RelativeLayout) findViewById(R.id.buttonAnnuler);
		annuler_button.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) {
				showAlertDialogBeforeMain();
			}
		});

		RelativeLayout buttonSave = (RelativeLayout) findViewById(R.id.mbuttonSave);
		buttonSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickSave();
			}
		});

		RelativeLayout buttonEnvoyer = (RelativeLayout) findViewById(R.id.mbuttonEnvoyer);
		buttonEnvoyer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				envoiContrib();
			}
		});

		ActionBar actionBar = getActionBar();
		if (actionBar != null)
		{
			actionBar.show();
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setTitle(this.getResources().getString(R.string.Contribuer));
			actionBar.setDisplayShowTitleEnabled(true);
			//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
		}

		ListChampsNoticeModif.mContribXml = new ContribXml(readContrib());
		
		if( mContribution.getNoticeId() == 0 ) {
			getActionBar().setTitle("Création d'une nouvelle oeuvre");  // TODO : resourcify this string
		}
		else {
			getActionBar().setTitle("Ajout/Modif oeuvre existante"); // TODO : resourcify this string
		}

		ContributionProperty cp = mContribution.getProperty("photo");
		if (cp.getValue() != "") {
			mLoadPhoto.setVisibility(View.GONE);
			mBlockModifierImage.setVisibility(View.VISIBLE);
			this.notice_photo = cp.getValue();
			loadingPhoto2 upl = new loadingPhoto2(this, this.notice_photo, false);
			upl.execute();
		}
		else {
			mLoadPhoto.setVisibility(View.VISIBLE);
			mBlockModifierImage.setVisibility(View.GONE);
		}

		String propsToShow[] = {
				Contribution.TITRE,
				Contribution.ARTISTE,
				Contribution.COULEUR,
				Contribution.DATE_INAUGURATION,
				Contribution.DESCRIPTION,
				Contribution.MATERIAUX,
				Contribution.NOM_SITE,
				Contribution.DETAIL_SITE,
				Contribution.NATURE,
				Contribution.LATITUDE,
				Contribution.LONGITUDE,
				Contribution.AUTRE,
				Contribution.ETAT,
				Contribution.PETAT,
				Contribution.PMR
				};

		mList.clear();
		for (int i = 0; i < propsToShow.length; i++) {
			mList.add(mContribution.getProperty(propsToShow[i]));
		}
	}

	protected void envoiContrib() {

		boolean internetDispo = checkInternetConnection();

		if(Authentification.getisConnected())
		{
			if(internetDispo)
			{
				String auteur=Authentification.getUsername();
				String password=Authentification.getPassword();

				boolean bool = cPref.getBoolean(ListChampsNoticeModif.creationNotice, false);

				if(bool)//si c'est une creation
				{
					Log.d(DEBUG_TAG, "Envoi d'une creation de notice");
					Contribution c = new Contribution();
					boolean saveContrib = generateContribCreer(c);//creer la contribution de type creer
					if(saveContrib)
					{
						if(MainActivity.mLastLocation != null)
						{
							c.latitude = MainActivity.mLastLocation.getLatitude();
							c.longitude =MainActivity.mLastLocation.getLongitude();
						}
						c.auteur=auteur;
						c.password=password;
						c.statut=champ_status.enattente;
						c.idLocal=this.IDLocal;
						//methode pour cree un nouveau document XML a l'aide de contribution
						ArrayList<Contribution> l =new ArrayList<>();
						l.add(c);
						ContribXmlEnCours contribXMLEnvoi = new ContribXmlEnCours(l);
						List<Contribution> SetImage = new ArrayList<>();//liste des contributions avec image
						if (!c.photoPath.equals(""))
						{

							SetImage.add(c);
							Log.d("image trouvée", "name ="+c.photoPath);
						}

						//export sous forme de string
						String xml = contribXMLEnvoi.xmlToString() ;
						//asyncTask pour envoyer photo
						EnvoiPhoto env = new EnvoiPhoto(this, SetImage, xml, false);
						env.execute();
					}
					else
					{
						//afficher msg d'erreur
						Toast.makeText(this, this.getResources().getString(R.string.au_moins_champ_pour_contribuer), Toast.LENGTH_LONG).show();;
					}
				}
				else
				{

					Log.d(DEBUG_TAG, "PAS CREATION D UNE NOTICE");
					Log.d(DEBUG_TAG+"/onclicksave", "Ce n'est pas une creation");
					Log.d(DEBUG_TAG+"/onclicksave", "size ="+mContribXml.listContributionEnCours.size());
					//this.generateContributionEnCours();
					if(this.generateContributionEnCours())
					{
						List<Contribution> SetImage = new ArrayList<Contribution>();
						for(int i=0;i<mContribXml.listContributionEnCours.size();i++)
						{
							Contribution c = mContribXml.listContributionEnCours.get(i);
							c.statut=champ_status.enattente;
							c.idLocal = IDLocal;
							if(MainActivity.mLastLocation != null)
							{
								c.latitude = MainActivity.mLastLocation.getLatitude();
								c.longitude =	MainActivity.mLastLocation.getLongitude();
							}
							SetImage = new ArrayList<>();//liste des contributions avec image
							if (!c.photoPath.equals(""))
							{

								SetImage.add(c);
								Log.d("image trouvée", "name ="+c.photoPath);
							}
							c.auteur=auteur;
							c.password=password;
						}
						ContribXmlEnCours contribXMLEnvoi = new ContribXmlEnCours(mContribXml.listContributionEnCours);
						//export sous forme de string
						String xml = contribXMLEnvoi.xmlToString() ;
						//asyncTask pour envoyer photo
						EnvoiPhoto env = new EnvoiPhoto(this, SetImage, xml, false);
						env.execute();
					}
					else
					{
						//afficher msg d'erreur
						Toast.makeText(this, getResources().getString(R.string.completer_au_moins_un_champs), Toast.LENGTH_LONG).show();;
					}


				}
			}
			else //afficher une erreur car pas de connexion internet
			{
				AtlasError.showErrorDialog(ListChampsNoticeModif.this, "7.1", "pas internet connexion");
			}
		}//en authentificationisconneted
		else//erreur car pas connecté
		{
			AtlasError.showErrorDialog(ListChampsNoticeModif.this, "7.3", "compte util requis");
		}


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



	protected void gotoBefore()
	{
		this.onBackPressed();
	}

	protected void afficheClearInput()//bouton mode debug
	{
		Toast.makeText(this, "suppression de tous les entrées du formulaire...", Toast.LENGTH_SHORT).show();
	}


	public void gotomain() {
		//Intent intent = new Intent(getApplication(), MainContribActivity.class);
		//startActivity(intent);
		setResult(RESULT_CANCELED);
		finish();
		super.onBackPressed();
	}
	
	public void afficheAlerte(String ch)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(ch);
		builder.setNegativeButton(this.getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

			}
		});
		builder.create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_MODIFY_PROPERTY)
		{
			if( resultCode == RESULT_OK ) {
				Log.d(DEBUG_TAG, "onActivityResult(REQUEST_MODIFY_PROPERTY, RESULT_OK)");
				
				if( data.hasExtra("property") ) {
					int position = data.getIntExtra("position", -1);
					ContributionProperty prop = (ContributionProperty) data.getSerializableExtra("property");
					Log.d(DEBUG_TAG, "onActivityResult(): modified property " + position + " => " + prop.getTitle() + " = " + prop.getValue());
					
					// Update current ListView
					mList.set(position, prop);
					mAdapter.notifyDataSetChanged();
					
					// Update the Activity Intent so that modifications will be preserved on screen rotation
					Intent intent = getIntent();
					Bundle bundle = intent.getExtras();
					if( bundle.containsKey("contribution") ) {
						Contribution2 contribution = (Contribution2) bundle.getSerializable("contribution");
						contribution.setProperty(prop.getDbField(), prop);
						bundle.putSerializable("contribution", contribution);
						intent.putExtras(bundle);
						this.setIntent(intent);
					}
				}
			}
		}
		if(requestCode == REQUEST_FINISH)
		{
			finish();
		}
		if(requestCode == REQUEST_CONNEXION)
		{
			if(requestCode == RESULT_OK)
			{
				Log.d(DEBUG_TAG, "connexion success");
			}
		}
		else if(requestCode == REQUEST_localisation) 
		{
			finish();
		}

	}

	public String readContrib()
	{
		String contenu = "";
		FileInputStream fIn;
		try {
			fIn = openFileInput(ContribXml.nomFichier);

			InputStreamReader isr = new InputStreamReader ( fIn ) ;
			BufferedReader buffreader = new BufferedReader ( isr ) ;

			String line;
			while ((line=buffreader.readLine())!=null) 
			{
				contenu = contenu+line;//System.out.println(line);
			}
			Log.d(DEBUG_TAG	, "contenu ="+contenu);
		} catch (FileNotFoundException e) {
			Log.d(DEBUG_TAG+"ECHEC file not found", "echec enregistrement");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d(DEBUG_TAG+"ECHEC ioException", "echec enregistrement");
			e.printStackTrace();
		}
		return contenu;
	}


	public void enregistreXml()
	{
		Log.d(DEBUG_TAG, "enregistrement...");
		String ch = ContribXml.xmlToString();//recupere le xml sous forme de chaine

		FileOutputStream outputStream;
		deleteFile(ContribXml.nomFichier);

		try {
			//ContribFile.delete();
			//this.ContribFile = createContribFile();
			Log.d(DEBUG_TAG, "au moment de l'enregistrement contribFile ="+readContrib());
			outputStream = openFileOutput(ContribXml.nomFichier, Context.MODE_APPEND);//create the file if no exists
			outputStream.write(ch.getBytes());
			outputStream.close();
			// Log.d(DEBUG_TAG, "enregistrement de  ="+ReadContrib());
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(DEBUG_TAG+"ECHEC", "echec enregistrement");
		}
	}

	public void onClickSave()
	{
		Log.d(DEBUG_TAG+"/save", "sauvegarde en cours ...");
		boolean bool = cPref.getBoolean(ListChampsNoticeModif.creationNotice, false);
		Log.d(DEBUG_TAG+"cprefCreationNotice", "creation notice?? "+bool);
		String auteur="";
		String password="";
		if(Authentification.getisConnected())
		{
			auteur=Authentification.getUsername();
			password= Authentification.getPassword();
		}

		Log.d(DEBUG_TAG, "auteur= "+auteur);
		if(bool) //une creation de notice
		{
			Log.d(DEBUG_TAG, "CREATION D UNE NOTICE");
			Contribution c = new Contribution();
			boolean saveContrib = generateContribCreer(c);//creer la contribution de type creer
			if(saveContrib)
			{
				if(MainActivity.mLastLocation != null)
				{
					c.latitude = MainActivity.mLastLocation.getLatitude();
					c.longitude =MainActivity.mLastLocation.getLongitude();
				}
				c.auteur=auteur;
				c.password=password;
				c.statut=champ_status.enattente;
				c.idLocal=this.IDLocal;
				ContribXml.addContributionToXml(c);
				enregistreXml();
				setResult(RESULT_OK);
				erasecpref();//efface les données utilisateurs
				finish();
			}
			else
			{
				//afficher msg d'erreur
				Toast.makeText(this, this.getResources().getString(R.string.completer_au_moins_un_champs), Toast.LENGTH_LONG).show();;
			}
		}
		else //c'est pas une creation
		{
			Log.d(DEBUG_TAG, "PAS CREATION D UNE NOTICE");
			Log.d(DEBUG_TAG+"/onclicksave", "Ce n'est pas une creation");
			Log.d(DEBUG_TAG+"/onclicksave", "size ="+mContribXml.listContributionEnCours.size());
			this.generateContributionEnCours();
			if(this.generateContributionEnCours())
			{
				for(int i=0;i<mContribXml.listContributionEnCours.size();i++)
				{
					Contribution c = mContribXml.listContributionEnCours.get(i);
					c.statut=champ_status.enattente;
					c.idLocal = IDLocal;
					if(MainActivity.mLastLocation != null)
					{
						c.latitude = MainActivity.mLastLocation.getLatitude();
						c.longitude =	MainActivity.mLastLocation.getLongitude();
					}
					c.auteur=auteur;
					c.password=password;
					ContribXml.addContributionToXml(c);
					enregistreXml();
				}
				setResult(RESULT_OK);
				erasecpref();//efface les donn�e utilisateurs
				finish();
			}
			else
			{
				//afficher msg d'erreur
				Toast.makeText(this, this.getResources().getString(R.string.completer_au_moins_un_champs), Toast.LENGTH_LONG).show();;
			}


		}

	}


	public void recupStringCpref()
	{
		//Recupere les champs deja entree par le user
		ajoutTitreValue = cPref.getString(ListChampsNoticeModif.ajout_titre, "");
		TitreValue = cPref.getString(ListChampsNoticeModif.modif_titre, "");
		ajoutPhotoValue = cPref.getString(ListChampsNoticeModif.ajout_photo, "");
		PhotoValue = cPref.getString(ListChampsNoticeModif.modif_photo, "");
		ajoutCouleurValue = cPref.getString(ListChampsNoticeModif.ajout_couleur, "");
		CouleurValue = cPref.getString(ListChampsNoticeModif.modif_couleur, "");
		ajoutDateValue = cPref.getString(ListChampsNoticeModif.ajout_date, "");
		modif_dateValue = cPref.getString(ListChampsNoticeModif.modif_date, "");


		ajoutMateriauxValue = cPref.getString(ListChampsNoticeModif.ajout_materiaux, "");
		modif_materiauxValue = cPref.getString(ListChampsNoticeModif.modif_materiaux, "");
		ajout_descriptionValue = cPref.getString(ListChampsNoticeModif.ajout_description, "");
		modif_descriptionValue = cPref.getString(ListChampsNoticeModif.modif_description, "");
		ajoutArtisteValue = cPref.getString(ListChampsNoticeModif.ajout_artiste, "");
		ArtisteValue = cPref.getString(ListChampsNoticeModif.modif_artiste, "");

		ajoutNomSiteValue=cPref.getString(ListChampsNoticeModif.ajout_nomsite, "");
		modif_NomSiteValue=cPref.getString(ListChampsNoticeModif.modif_nomsite, "");
		ajoutAutreValue=cPref.getString(ListChampsNoticeModif.ajout_autre, "");
		ajoutNatureValue=cPref.getString(ListChampsNoticeModif.ajout_nature, "");
		modif_NatureValue=cPref.getString(ListChampsNoticeModif.modif_nature, "");

		ajoutEtatValue=cPref.getString(ListChampsNoticeModif.ajout_etat, "");
		ajoutPetatValue=cPref.getString(ListChampsNoticeModif.ajout_petat, "");
		ajoutPmrValue=cPref.getString(ListChampsNoticeModif.ajout_pmr, "");

		ajoutLocalisationValue= cPref.getBoolean(ListChampsNoticeModif.ajout_localisation, false);
		modifLocalisationValue= cPref.getBoolean(ListChampsNoticeModif.modif_localisation, false);

		ajoutDetailSiteValue = cPref.getString(ListChampsNoticeModif.ajout_detailsite, "");
		modif_DetailSiteValue = cPref.getString(ListChampsNoticeModif.modif__detailsite, "");

		try
		{
			this.latitude=Double.parseDouble(cPref.getString(ListChampsNoticeModif.LATITUDE, "0.0"));
			this.longitude=Double.parseDouble(cPref.getString(ListChampsNoticeModif.LONGITUDE, "0.0"));
		}catch(Exception e)
		{
			Log.d(DEBUG_TAG, "probleme de conversion");
		}

	}

	/**
	 * créer une contribution creer, qui comprend tous les champs entrés par l'utilisateur
	 * @param c
	 */
	private boolean generateContribCreer(Contribution c) {
		this.recupStringCpref(); //recupere les valeurs de cpref, dans les variables utilisées ci après

		Log.d(DEBUG_TAG, "generateContribCreer en cours.....");
		c.type = Contribution.type_contrib.creer;
		boolean valueModif= false;//determine si une valeur a ete modifier
		if (!ajoutTitreValue.equals(""))
		{
			c.titre = ajoutTitreValue;
			valueModif= true;
		}
		if(!ajoutPhotoValue.equals(""))
		{
			c.photoPath = ajoutPhotoValue;
			valueModif= true;
		}

		if(!ajoutCouleurValue.equals(""))
		{
			c.couleur = ajoutCouleurValue;
			valueModif= true;
		}

		if(!ajoutDateValue.equals(""))
		{
			c.dateinauguration = ajoutDateValue;
			valueModif= true;
		}
		if(!ajoutMateriauxValue.equals(""))
		{
			c.materiaux = ajoutMateriauxValue;
			valueModif= true;
		}
		if(!ajout_descriptionValue.equals(""))
		{
			c.description = ajout_descriptionValue;
			valueModif= true;
		}
		if(!ajoutArtisteValue.equals(""))
		{
			c.artiste = ajoutArtisteValue;
			valueModif= true;
		}
		if(!ajoutNomSiteValue.equals(""))
		{
			c.nomsite = ajoutNomSiteValue;
			valueModif= true;
		}
		if(!ajoutNatureValue.equals(""))
		{
			c.nature = ajoutNatureValue;
			valueModif= true;
		}
		if(!ajoutAutreValue.equals(""))
		{
			c.autre = ajoutAutreValue;
			valueModif= true;
		}
		if(!ajoutEtatValue.equals(""))
		{
			c.etat = ajoutEtatValue;
			valueModif= true;
		}
		if(!ajoutPetatValue.equals(""))
		{
			c.petat = ajoutPetatValue;
			valueModif= true;
		}
		if(!ajoutPmrValue.equals(""))
		{
			c.pmr = ajoutPmrValue;
			valueModif= true;
		}
		if(!ajoutDetailSiteValue.equals(""))
		{
			c.detailsite = ajoutDetailSiteValue;
			valueModif= true;
		}

		if(ajoutLocalisationValue)
		{
			c.latitude = this.latitude;
			c.longitude = this.longitude;
			valueModif= true;
		}

		return valueModif;
		
		
	}

	/**
	 * Ajoute les contributions en cours à la listContribEnCours
	 * 
	 */
	public boolean generateContributionEnCours()
	{
		boolean valueModif= false;//determine si une valeur a ete modifier
		this.recupStringCpref(); //recupere les valeurs de cpref, dans les variables utilis�es ci apres
		Log.d(DEBUG_TAG, "generateContribution en cours.....");

		if (!ajoutTitreValue.equals(""))
		{
			Contribution c1 = new Contribution();
			addInfoNoticeToContribution(c1);
			Contribution.createContributionAjout(c1, mNoticeId, this.IDLocal, Contribution.TITRE, ajoutTitreValue);

			mContribXml.listContributionEnCours.add(c1);
			valueModif= true;

		}
		if(!TitreValue.equals(""))
		{
			Contribution c2 = new Contribution();
			addInfoNoticeToContribution(c2);
			Contribution.createContributionRemplacement(c2,this.mNoticeId, this.IDLocal, Contribution.TITRE, TitreValue);
			mContribXml.listContributionEnCours.add(c2);
			valueModif= true;
		}
		Log.d(DEBUG_TAG+"/photo ajout", "value photo ="+ajoutPhotoValue);
		if(!ajoutPhotoValue.equals(""))
		{
			Contribution c3 = new Contribution();
			addInfoNoticeToContribution(c3);
			Contribution.createContributionAjout(c3, this.mNoticeId, this.IDLocal, Contribution.PHOTO, ajoutPhotoValue);
			mContribXml.listContributionEnCours.add(c3);
			valueModif= true;
		}
		if(!PhotoValue.equals(""))
		{
			Contribution c4 = new Contribution();
			addInfoNoticeToContribution(c4);
			Contribution.createContributionRemplacement(c4,this.mNoticeId, this.IDLocal, Contribution.PHOTO, PhotoValue);
			mContribXml.listContributionEnCours.add(c4);
			valueModif= true;
		}
		if(!ajoutCouleurValue.equals(""))
		{
			Contribution c5 = new Contribution();
			addInfoNoticeToContribution(c5);
			Contribution.createContributionAjout(c5, this.mNoticeId, this.IDLocal, Contribution.COULEUR, ajoutCouleurValue);
			mContribXml.listContributionEnCours.add(c5);
			valueModif= true;
		}
		if(!CouleurValue.equals(""))
		{
			Contribution c16 = new Contribution();
			addInfoNoticeToContribution(c16);
			Contribution.createContributionRemplacement(c16, this.mNoticeId, this.IDLocal, Contribution.COULEUR, CouleurValue);
			mContribXml.listContributionEnCours.add(c16);
			valueModif= true;
		}
		if(!ajoutDateValue.equals(""))
		{
			Contribution c6 = new Contribution();
			addInfoNoticeToContribution(c6);
			Contribution.createContributionAjout(c6, this.mNoticeId, this.IDLocal, Contribution.DATE_INAUGURATION, ajoutDateValue);
			mContribXml.listContributionEnCours.add(c6);
			valueModif= true;
		}
		if(!modif_dateValue.equals(""))
		{
			Contribution c7 = new Contribution();
			addInfoNoticeToContribution(c7);
			Contribution.createContributionRemplacement(c7, this.mNoticeId, this.IDLocal, Contribution.DATE_INAUGURATION, modif_dateValue);
			mContribXml.listContributionEnCours.add(c7);
			valueModif= true;
		}

		if(!ajoutMateriauxValue.equals(""))
		{
			Contribution c10 = new Contribution();
			addInfoNoticeToContribution(c10);
			Contribution.createContributionAjout(c10, this.mNoticeId, this.IDLocal, Contribution.MATERIAUX, ajoutMateriauxValue);
			mContribXml.listContributionEnCours.add(c10);
			valueModif= true;
		}
		if(!modif_materiauxValue.equals(""))
		{
			Contribution c11 = new Contribution();
			addInfoNoticeToContribution(c11);
			Contribution.createContributionRemplacement(c11, this.mNoticeId, this.IDLocal, Contribution.MATERIAUX, modif_materiauxValue);
			mContribXml.listContributionEnCours.add(c11);
			valueModif= true;
		}
		if(!ajout_descriptionValue.equals(""))
		{
			Contribution c12 = new Contribution();
			addInfoNoticeToContribution(c12);
			Contribution.createContributionAjout(c12, this.mNoticeId, this.IDLocal, Contribution.DESCRIPTION, ajout_descriptionValue);
			mContribXml.listContributionEnCours.add(c12);
			valueModif= true;
		}
		if(!modif_descriptionValue.equals(""))
		{
			Contribution c13 = new Contribution();
			addInfoNoticeToContribution(c13);
			Contribution.createContributionRemplacement(c13, this.mNoticeId, this.IDLocal, Contribution.DESCRIPTION, modif_descriptionValue);
			mContribXml.listContributionEnCours.add(c13);
			valueModif= true;
		}
		if(!ajoutArtisteValue.equals(""))
		{
			Contribution c14 = new Contribution();
			addInfoNoticeToContribution(c14);
			Contribution.createContributionAjout(c14, this.mNoticeId, this.IDLocal, Contribution.ARTISTE, ajoutArtisteValue);
			mContribXml.listContributionEnCours.add(c14);
			valueModif= true;
		}
		if(!ArtisteValue.equals(""))
		{
			Contribution c15 = new Contribution();
			addInfoNoticeToContribution(c15);//rajoute les infos notice pour identification, puis on ajoute les modif de l"utilisateur
			Contribution.createContributionRemplacement(c15, this.mNoticeId, this.IDLocal, Contribution.ARTISTE, ArtisteValue);
			mContribXml.listContributionEnCours.add(c15);
			valueModif= true;
		}
		if(!modif_NomSiteValue.equals(""))
		{
			Contribution c15 = new Contribution();
			addInfoNoticeToContribution(c15);//rajoute les infos notice pour identification, puis on ajoute les modif de l"utilisateur
			Contribution.createContributionRemplacement(c15, this.mNoticeId, this.IDLocal, Contribution.NOM_SITE, modif_NomSiteValue);
			mContribXml.listContributionEnCours.add(c15);
			valueModif= true;
		}
		if(!ajoutNomSiteValue.equals(""))
		{
			Contribution c14 = new Contribution();
			addInfoNoticeToContribution(c14);
			Contribution.createContributionAjout(c14, this.mNoticeId, this.IDLocal, Contribution.NOM_SITE, ajoutNomSiteValue);
			mContribXml.listContributionEnCours.add(c14);
			valueModif= true;
		}
		if(!ajoutNatureValue.equals(""))
		{
			Contribution c14 = new Contribution();
			addInfoNoticeToContribution(c14);
			Contribution.createContributionAjout(c14, this.mNoticeId, this.IDLocal, Contribution.NATURE, ajoutNatureValue);
			mContribXml.listContributionEnCours.add(c14);
			valueModif= true;
		}
		if(!modif_NatureValue.equals(""))
		{
			Contribution c15 = new Contribution();
			addInfoNoticeToContribution(c15);//rajoute les infos notice pour identification, puis on ajoute les modif de l"utilisateur
			Contribution.createContributionRemplacement(c15, this.mNoticeId, this.IDLocal, Contribution.NATURE, modif_NatureValue);
			mContribXml.listContributionEnCours.add(c15);
			valueModif= true;
		}
		if(!ajoutAutreValue.equals(""))
		{
			Contribution c14 = new Contribution();
			addInfoNoticeToContribution(c14);
			Contribution.createContributionAjout(c14, this.mNoticeId, this.IDLocal, Contribution.AUTRE, ajoutAutreValue);
			mContribXml.listContributionEnCours.add(c14);
			valueModif= true;
		}
		if(!ajoutEtatValue.equals(""))
		{
			Contribution c14 = new Contribution();
			addInfoNoticeToContribution(c14);
			Contribution.createContributionAjout(c14, this.mNoticeId, this.IDLocal, Contribution.ETAT, ajoutEtatValue);
			mContribXml.listContributionEnCours.add(c14);
			valueModif= true;
		}
		if(!ajoutPetatValue.equals(""))
		{
			Contribution c14 = new Contribution();
			addInfoNoticeToContribution(c14);
			Contribution.createContributionAjout(c14, this.mNoticeId, this.IDLocal, Contribution.PETAT, ajoutPetatValue);
			mContribXml.listContributionEnCours.add(c14);
			valueModif= true;
		}
		if(!ajoutPmrValue.equals(""))
		{
			Contribution c14 = new Contribution();
			addInfoNoticeToContribution(c14);
			Contribution.createContributionAjout(c14, this.mNoticeId, this.IDLocal, Contribution.PMR, ajoutPmrValue);
			mContribXml.listContributionEnCours.add(c14);
			valueModif= true;
		}

		if(!ajoutDetailSiteValue.equals(""))
		{
			Contribution c14 = new Contribution();
			addInfoNoticeToContribution(c14);
			Contribution.createContributionAjout(c14, this.mNoticeId, this.IDLocal, Contribution.DETAIL_SITE, ajoutDetailSiteValue);
			mContribXml.listContributionEnCours.add(c14);
			valueModif= true;
		}

		if(!modif_DetailSiteValue.equals(""))
		{
			Contribution c14 = new Contribution();
			addInfoNoticeToContribution(c14);
			Contribution.createContributionRemplacement(c14, this.mNoticeId, this.IDLocal, Contribution.DETAIL_SITE, modif_DetailSiteValue);
			mContribXml.listContributionEnCours.add(c14);
			valueModif= true;
		}
		if(modifLocalisationValue )
		{
			Contribution c15 = new Contribution();
			addInfoNoticeToContribution(c15);//rajoute les infos notice pour identification, puis on ajoute les modif de l"utilisateur
			Contribution.createContributionRemplacementCoordonnee(c15, this.mNoticeId, this.IDLocal, Contribution.COORDONNEES, this.latitude, this.longitude);
			mContribXml.listContributionEnCours.add(c15);
			valueModif= true;
		}
		if(ajoutLocalisationValue)
		{
			Contribution c15 = new Contribution();
			addInfoNoticeToContribution(c15);//rajoute les infos notice pour identification, puis on ajoute les modif de l"utilisateur
			Contribution.createContributionAjoutCoordonnee(c15, this.mNoticeId, this.IDLocal, Contribution.COORDONNEES, this.latitude, this.longitude);
			mContribXml.listContributionEnCours.add(c15);
			valueModif= true;
		}
		return valueModif;
	}

	private void addInfoNoticeToContribution(Contribution c1) {
		//liste des valeur des champs connus de la notice de référence 
		//executer avant l'affectation des valeurs ajout/modif.(valeur notice sera ecrasee eventuellement)

		if(notice_artiste!=null && !notice_artiste.equals("") && !notice_artiste.equals("?"))
		{
			Log.d(DEBUG_TAG, "ajout info notice"+notice_artiste);
			c1.artiste = notice_artiste;
		}
		if(notice_titre!=null && !notice_titre.equals("") && !notice_titre.equals("?"))
		{
			Log.d(DEBUG_TAG, "ajout info notice"+notice_titre);
			c1.titre = notice_titre;
		}
		if(notice_couleur!=null && !notice_couleur.equals("") && !notice_couleur.equals("?"))
		{
			Log.d(DEBUG_TAG, "ajout info notice"+notice_couleur);
			c1.couleur = notice_couleur;
		}
		if(notice_description!=null && !notice_description.equals("") && !notice_description.equals("?"))
		{
			Log.d(DEBUG_TAG, "ajout info notice"+notice_description);
			c1.description = notice_description;
		}
		if(notice_materiaux!=null && !notice_materiaux.equals("") && !notice_materiaux.equals("?"))
		{
			Log.d(DEBUG_TAG, "ajout info notice"+notice_materiaux);
			c1.materiaux = notice_materiaux;
		}
		if(notice_dateinauguration!=null && !notice_dateinauguration.equals("")  && !notice_dateinauguration.equals("?"))
		{
			Log.d(DEBUG_TAG, "ajout info notice"+notice_dateinauguration);
			c1.dateinauguration = notice_dateinauguration;
		}
		if(notice_photo!=null && !notice_photo.equals("")  && !notice_photo.equals("?"))
		{
			Log.d(DEBUG_TAG, "ajout info notice"+notice_photo);
			c1.photoPath = notice_photo;
		}
		if(notice_latitude!=null && !notice_latitude.equals("")  && !notice_latitude.equals("?"))
		{
			Log.d(DEBUG_TAG, "ajout info notice"+notice_latitude);
			c1.latitude = notice_latitude;
		}
		if(notice_longitude!=null && !notice_longitude.equals("")  && !notice_longitude.equals("?"))
		{
			Log.d(DEBUG_TAG, "ajout info notice"+notice_longitude);
			c1.longitude = notice_longitude;
		}
		if(notice_nomsite!=null && !notice_nomsite.equals("")  && !notice_nomsite.equals("?"))
		{
			Log.d(DEBUG_TAG, "ajout info notice"+notice_nomsite);
			c1.nomsite = notice_nomsite;
		}
		if(notice_nature!=null && !notice_nature.equals("")  && !notice_nature.equals("?"))
		{
			Log.d(DEBUG_TAG, "ajout info notice"+notice_nature);
			c1.nature = notice_nature;
		}
		if(notice__detailsite != null && !notice__detailsite.equals("")  && !notice__detailsite.equals("?"))
		{
			c1.detailsite=notice__detailsite;
		}

	}
	
	public void erasecpref()
	{
		boolean f =cPref.edit().clear().commit();
		mList.clear();
		generateContributionEnCours();
		mAdapter.notifyDataSetChanged();

		Log.d(DEBUG_TAG+"/erasecpref()", "finish, clear cpref ="+f);
	}



	public void showAlertDialogBeforeMain()
	{
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();

		// Le titre
		alertDialog.setTitle(this.getResources().getString(R.string.app_name));//titre l'application

		// Le message
		alertDialog.setMessage(this.getResources().getString(R.string.leave_contrib));

		// L'icone
		alertDialog.setIcon(android.R.drawable.btn_star);

		// Ajout du bouton "OK"
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface arg0, final int arg1) {
				// Le code à exécuter après le clique sur le bouton
				//Toast.makeText(mcontext, "Tu as cliqué sur le bouton 'OK'",Toast.LENGTH_SHORT).show();

				//gotomain();
				//gotoBefore();
				if(cPref.getBoolean(ListChampsNoticeModif.creationNotice, false))//si c'est une creation afficher le menu quitter pour effacer les preferences
				{
					cPref.edit().clear().commit();
					gotomain();
				}
				else
				{	//on a cliqu� sur annuler
					goBackToNotice();
				}
			}

		});

		alertDialog.setButton2(this.getResources().getString(R.string.annuler),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
			}
		});

		// Affichage
		alertDialog.show();
	}

	public void goBackToNotice()
	{
		this.erasecpref();
		setResult(RESULT_CANCELED);
		finish();
		//super.onBackPressed();
	}

	@Override
	public Context getContext() {
		return this;
	}

	@Override
	public ImageView getImageView() {
		return this.mImageNotice;
	}

	@Override
	public BaseAdapter getNoticeAdapter() {
		return null;
	}

	@Override
	public void onBackPressed() {
		if(cPref.getBoolean(ListChampsNoticeModif.creationNotice, false))//si c'est une creation afficher le menu quitter pour effacer les preferences
		{
			showAlertDialogBeforeMain();

		}
		else
		{
			super.onBackPressed();
		}

	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		if(itemId == android.R.id.home)
		{
			showAlertDialogBeforeMain();
			setResult(RESULT_CANCELED);
			/**Intent intent= new Intent(this,MainActivity.class);
				startActivity(intent);
				finish();**/
			return true;
		}
		if(itemId == R.id.action_account)
		{
			Intent intent= new Intent(this,ConnexionActivity.class);
			startActivityForResult(intent, REQUEST_CONNEXION);
			return true;
		}
		else return false;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contribuer_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	private void showLocationChangeAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getResources().getString(R.string.location_change_alert))
        .setCancelable(false)
        .setPositiveButton(getResources().getString(R.string.mise_ajour),
                new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
            	if( MainActivity.mLastLocation == null ) {
					Toast.makeText(getApplication(), getResources().getString(R.string.desole_position_pas_recup), Toast.LENGTH_LONG).show();
            		return;
            	}
            	String lat = Double.toString(MainActivity.mLastLocation.getLatitude());
            	String longi = Double.toString(MainActivity.mLastLocation.getLongitude());
            	Log.d(DEBUG_TAG,"Latitude :" + lat);
    			Log.d(DEBUG_TAG,"Longitude :" + longi);
    			ListChampsNoticeModif.cPref.edit().putString(ListChampsNoticeModif.LATITUDE,""+MainActivity.mLastLocation.getLatitude()).commit();
				ListChampsNoticeModif.cPref.edit().putString(ListChampsNoticeModif.LONGITUDE,""+MainActivity.mLastLocation.getLongitude()).commit();
				// on doit relancer l'activité pour un affichage ok
				setResult(RESULT_OK);//pour fermer l'activité ListChampsNoticeModif précédente
				Intent intent = new Intent(getApplication(), ListChampsNoticeModif.class);
	        	intent.putExtras(mBundle);
	  			startActivity(intent);
	  			finish();
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
