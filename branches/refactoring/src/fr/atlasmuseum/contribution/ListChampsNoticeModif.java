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
import fr.atlasmuseum.search.module.ContribListAdapter;

public class ListChampsNoticeModif extends Activity implements loadPhotoInterface {
	private static final String DEBUG_TAG = "AtlasMuseum/ListChampAct";

	private Bundle mBundle;
	private List<ContribElementList> mList;
	static String CHAMPS_ITEM = "champs_item";

	private RelativeLayout mLoadPhoto;
	private ImageView mImageNotice;
	private RelativeLayout mBlockModifierImage;

	static ContribXml mContribXml;
	public ContribListAdapter mAdapter;
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


	//liste des valeur des champs connus de la notice de référence 
	String notice_titre;
	String notice_artiste;
	int notice_id;
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

		mImageNotice = (ImageView) findViewById(R.id.imageView1);

		mBlockModifierImage = (RelativeLayout) findViewById(R.id.relativeLayoutPhotoModifier);

		cPref = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
		cPref.edit().putString(champs_a_modifier, "").commit();
		cPref.edit().putBoolean(creationNotice, false).commit();
		
		mList = new ArrayList<ContribElementList>();
		mAdapter = new ContribListAdapter(ListChampsNoticeModif.this, mList);
		ListView listView = (ListView) findViewById(R.id.list_view);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id)
			{
				ContribElementList elem = (ContribElementList) mAdapter.getItem(position);
				Log.d(DEBUG_TAG, "cliqué sur = " + elem);
				
				String champ = (String)elem.getChampAModifier();//recuperation de la valeur champ a partir de la listView
				
				//récupérer le champs à modifier
				cPref.edit().putString(champs_a_modifier, champ).commit();
				Log.d(DEBUG_TAG, "cpref all = " + cPref.getAll().toString());
				
				if(champ == ListChampsNoticeModif.ajout_localisation ||
				   champ == ListChampsNoticeModif.modif_localisation) {
					cPref.edit().putBoolean(champ, true).commit();
					showLocationChangeAlertToUser();
				}
				else {
					//vue modification avec champ saisi/listview checkbox ou radioButton
					Intent intent = new Intent(getApplication(), ModifActivity.class);
					intent.putExtras(mBundle);
					startActivityForResult(intent, REQUEST_FINISH);	

				}


			}
		});	



		ImageButton button_modif_photo = (ImageButton) findViewById(R.id.modif_photo);
		button_modif_photo.setOnClickListener(new OnClickListener() 
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

		ListChampsNoticeModif.mContribXml = new ContribXml(readContrib());
		
		showItemUsingBundle();//affiche ajout/er en fonction des champs reçu
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
			actionBar.setTitle(this.getResources().getString(R.string.Contribuer));
			actionBar.setDisplayShowTitleEnabled(true);
			//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
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

	private void showItemUsingBundle()
	{
		///GESTION DU BUNDLE - pour afficher ou pas les item
		mBundle = new Bundle();
		mBundle = getIntent().getExtras();//recupere le bundle
		Log.d(DEBUG_TAG,"bundle ListChamp ="+ mBundle.toString());

		if (mBundle.containsKey(Contribution.IDNOTICE) && mBundle.getInt(Contribution.IDNOTICE) != 0 ) //ce n'est pas une creation
		{

			cPref.edit().putInt(ListChampsNoticeModif.notice_idOeuvre, mBundle.getInt(Contribution.IDNOTICE)).commit();//utiliser dans showNoticeAct

		}
		if (mBundle.containsKey(ListChampsNoticeModif.erasepref) && mBundle.getBoolean(ListChampsNoticeModif.erasepref) ) 
		{
			this.erasecpref();//si on vient de showNoticeActivity, on remet tout a zero
			mBundle.putBoolean(ListChampsNoticeModif.erasepref, false);
			Log.d(DEBUG_TAG, "ERASE Cpref showitemUsingBundle");
		}
		else
		{
			Log.d(DEBUG_TAG, "DO NOT ERASE Cpref showitemUsingBundle");
		}
		if (mBundle.containsKey(Contribution.LOCALID) && !mBundle.getString(Contribution.LOCALID).equals("") )
		{
			this.IDLocal = mBundle.getString(Contribution.LOCALID);
			Log.d(DEBUG_TAG+"/idLocal =", "idlocal ="+IDLocal);
		}


		/**
		 * 	Si Notice existante ******************************************************************************
		 */
		if (mBundle.containsKey(Contribution.IDNOTICE) && mBundle.getInt(Contribution.IDNOTICE) != 0 ) //ce n'est pas une creation
		{
			Log.d(DEBUG_TAG, "idnotice existe");
			boolean f = cPref.edit().putBoolean(ListChampsNoticeModif.creationNotice, false).commit();//on a un IDnotice, donc ce n'est pas une creation
			Log.d(DEBUG_TAG, "enregistrement des preference creationNotice ="+ f);



			cPref.edit().putInt(ListChampsNoticeModif.notice_idOeuvre, mBundle.getInt(Contribution.IDNOTICE)).commit();//utiliser dans showNoticeAct



			getActionBar().setTitle("Ajout/Modif oeuvre existante");

			this.notice_id = mBundle.getInt(Contribution.IDNOTICE);

			/**
			 * 	Photo - Notice existante
			 */
			if (mBundle.containsKey(Contribution.PHOTO) && !mBundle.getString(Contribution.PHOTO).isEmpty() )
			{
				//cacher le bouton charger photo
				this.mLoadPhoto.setVisibility(View.GONE);

				//afficher la photo

				mBlockModifierImage.setVisibility(View.VISIBLE);
				Log.d(DEBUG_TAG, "idnotice="+notice_id);
				this.notice_photo = mBundle.getString(Contribution.PHOTO);

				//recupere la photo modifiée

				Log.d(DEBUG_TAG, "=====+++====== modifphotoValue = "+cPref.getString(ListChampsNoticeModif.modif_photo, ""));

				String[] PhotoValue = cPref.getString(ListChampsNoticeModif.modif_photo, "").split("/");
				Log.d(DEBUG_TAG, "modif photo value ="+PhotoValue[PhotoValue.length-1]);
				if(PhotoValue != null && !PhotoValue[PhotoValue.length-1].equals(""))//
				{
					loadingPhoto2 upl = new loadingPhoto2(this,  PhotoValue[PhotoValue.length-1], true);
					upl.execute();
				}

				else
				{
					loadingPhoto2 upl = new loadingPhoto2(this,  this.notice_photo, false);
					upl.execute();
				}


			}

			/**
			 * 	TITRE - Notice existante
			 */

			ContribElementList elt= new ContribElementList();
			elt.setTitre(getResources().getString(R.string.Titre));

			if (mBundle.getString(Contribution.TITRE).isEmpty())
			{
				if ( ListChampsNoticeModif.cPref.contains(ajout_titre) )
				{
					elt.setValue(ListChampsNoticeModif.cPref.getString(ajout_titre, ""));
				}
				elt.setChampAModifier(ajout_titre);
			}
			else
			{
				if ( ListChampsNoticeModif.cPref.contains(modif_titre) )
				{
					elt.setValue(ListChampsNoticeModif.cPref.getString(modif_titre, ""));
				}

				this.notice_titre = mBundle.getString(Contribution.TITRE);
				elt.setChampAModifier(modif_titre);
				elt.setOriginalValue( this.notice_titre);
			}
			mList.add(elt);


			/**
			 * 	Artiste  - Notice existante- 
			 */
			if (mBundle.containsKey(Contribution.ARTISTE) && mBundle.getString(Contribution.ARTISTE).isEmpty())
			{
				ContribElementList eltArtisteAjout= new ContribElementList();
				eltArtisteAjout.setTitre(getResources().getString(R.string.Artiste));
				eltArtisteAjout.setChampAModifier(ajout_artiste);
				if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_artiste))
				{
					eltArtisteAjout.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_artiste, ""));
				}
				mList.add(eltArtisteAjout);
			}
			else
			{
				this.notice_artiste = mBundle.getString(Contribution.ARTISTE);
				/**
		        	 ContribElementList eltArtisteAjout= new ContribElementList();
		        	 eltArtisteAjout.setTitre(getResources().getString(R.string.Artiste));
		        	 eltArtisteAjout.setChampsAModifier(ajout_artiste);
		        	 if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_artiste))
		        	 {
		        		 eltArtisteAjout.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_artiste, ""));
		        	 }
		        	 list.add(eltArtisteAjout); //Ajout Artiste **/

				ContribElementList eltArtisteModif= new ContribElementList();
				eltArtisteModif.setTitre(getResources().getString(R.string.Artiste));
				eltArtisteModif.setChampAModifier(modif_artiste);
				if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.modif_artiste))
				{
					eltArtisteModif.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_artiste, ""));
				}
				eltArtisteModif.setOriginalValue(this.notice_artiste);

				mList.add(eltArtisteModif); //Modifier Artiste 
			}

			/**
			 * 
			 * COULEUR - Notice existante
			 * 
			 */
			if(mBundle.containsKey(Contribution.COULEUR) && mBundle.getString(Contribution.COULEUR).isEmpty() )
			{
				ContribElementList altCouleurAjout= new ContribElementList();
				altCouleurAjout.setTitre(getResources().getString(R.string.Couleurs));
				altCouleurAjout.setChampAModifier(ajout_couleur);
				if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_couleur))
				{
					altCouleurAjout.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_couleur, ""));
				}
				mList.add(altCouleurAjout);


			}
			else
			{
				this.notice_couleur = mBundle.getString(Contribution.COULEUR);
				//list.add(modif_couleur);
				ContribElementList altCouleurModif= new ContribElementList();
				altCouleurModif.setTitre(getResources().getString(R.string.Couleurs));
				altCouleurModif.setChampAModifier(modif_couleur);
				altCouleurModif.setOriginalValue(notice_couleur);

				if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.modif_couleur))
				{
					altCouleurModif.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_couleur, ""));
				}
				mList.add(altCouleurModif);

				/**
		        	 ContribElementList altCouleurAjout= new ContribElementList();
		        	 altCouleurAjout.setTitre(getResources().getString(R.string.Couleurs));
		        	 altCouleurAjout.setChampsAModifier(ajout_couleur);
		        	 if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_couleur))
		        	 {
		        		 altCouleurAjout.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_couleur, ""));
		        	 }
		        	 list.add(altCouleurAjout);**/

			}

			/**
			 * 
			 * Date inauguration - Notice existante
			 * 
			 */
			if(mBundle.containsKey(Contribution.DATE_INAUGURATION) && mBundle.getString(Contribution.DATE_INAUGURATION).isEmpty() )
			{ 
				ContribElementList altdateAjout= new ContribElementList();
				altdateAjout.setTitre(getResources().getString(R.string.Date));
				altdateAjout.setChampAModifier(ajout_date);
				if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_date))
				{
					altdateAjout.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_date, ""));
				}
				mList.add(altdateAjout);
			}
			else
			{
				this.notice_dateinauguration = mBundle.getString(Contribution.DATE_INAUGURATION);
				ContribElementList altCouleurModif= new ContribElementList();
				altCouleurModif.setTitre(getResources().getString(R.string.Date));
				altCouleurModif.setChampAModifier(modif_date);
				altCouleurModif.setOriginalValue(notice_dateinauguration);

				if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.modif_date))
				{
					altCouleurModif.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_date, ""));
				}
				mList.add(altCouleurModif);
			}

			/**
			 * 
			 * Description - Notice existante
			 * 
			 */
			if(mBundle.containsKey(Contribution.DESCRIPTION) && mBundle.getString(Contribution.DESCRIPTION).isEmpty() )
			{
				ContribElementList altdateAjout= new ContribElementList();
				altdateAjout.setTitre(getResources().getString(R.string.Description));
				altdateAjout.setChampAModifier(ajout_description);
				if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_description))
				{
					altdateAjout.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_description, ""));
				}
				mList.add(altdateAjout);
				//list.add(ajout_description);
			}
			else
			{
				this.notice_description = mBundle.getString(Contribution.DESCRIPTION);

				ContribElementList altCouleurModif= new ContribElementList();
				altCouleurModif.setTitre(getResources().getString(R.string.Description));
				altCouleurModif.setChampAModifier(modif_description);
				altCouleurModif.setOriginalValue(notice_description);

				if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.modif_description))
				{
					altCouleurModif.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_description, ""));
				}
				mList.add(altCouleurModif);
			}

			/**
			 * 
			 * Materiaux - Notice existante 
			 * 
			 */

			if(mBundle.containsKey(Contribution.MATERIAUx) && mBundle.getString(Contribution.MATERIAUx).isEmpty() )
			{
				// list.add(ajout_materiaux);
				ContribElementList altdateAjout= new ContribElementList();
				altdateAjout.setTitre(getResources().getString(R.string.Materiaux));
				altdateAjout.setChampAModifier(ajout_materiaux);
				if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_materiaux))
				{
					altdateAjout.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_materiaux, ""));
				}
				mList.add(altdateAjout);
			}
			else
			{
				this.notice_materiaux = mBundle.getString(Contribution.MATERIAUx);
				// list.add(modif_materiaux);
				// list.add(ajout_materiaux);

				ContribElementList altCouleurModif= new ContribElementList();
				altCouleurModif.setTitre(getResources().getString(R.string.Materiaux));
				altCouleurModif.setChampAModifier(modif_materiaux);
				altCouleurModif.setOriginalValue(notice_materiaux);

				if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.modif_materiaux))
				{
					altCouleurModif.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_materiaux, ""));
				}
				mList.add(altCouleurModif);

				/**
		        	 ContribElementList altdateAjout= new ContribElementList();
		        	 altdateAjout.setTitre(getResources().getString(R.string.Materiaux));
		        	 altdateAjout.setChampsAModifier(ajout_materiaux);
		        	 if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_materiaux))
		        	 {
		        		 altdateAjout.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_materiaux, ""));
		        	 }
		        	 list.add(altdateAjout);**/
			}
			/**
			 * 
			 * Nom site - Notice existante
			 * 
			 */
			if(mBundle.containsKey(Contribution.NOM_SITE) && mBundle.getString(Contribution.NOM_SITE).isEmpty() )
			{
				// list.add(ajout_nomsite);

				ContribElementList altdateAjout= new ContribElementList();
				altdateAjout.setTitre(getResources().getString(R.string.Nom_du_site));
				altdateAjout.setChampAModifier(ajout_nomsite);
				if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_nomsite))
				{
					altdateAjout.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_nomsite, ""));
				}
				mList.add(altdateAjout);
			}
			else
			{
				this.notice_nomsite = mBundle.getString(Contribution.NOM_SITE);
				ContribElementList altCouleurModif= new ContribElementList();
				altCouleurModif.setTitre(getResources().getString(R.string.Nom_du_site));
				altCouleurModif.setChampAModifier(modif_nomsite);
				altCouleurModif.setOriginalValue(notice_nomsite);

				if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.modif_nomsite))
				{
					altCouleurModif.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_nomsite, ""));
				}
				mList.add(altCouleurModif);
				//list.add(modif_nomsite);

			}

			/**
			 * DETAIL SITE
			 */

			if(mBundle.containsKey(Contribution.DETAIL_SITE) && mBundle.getString(Contribution.DETAIL_SITE).isEmpty() )
			{
				// list.add(ajout_nomsite);

				ContribElementList altdateAjout= new ContribElementList();
				altdateAjout.setTitre(getResources().getString(R.string.Detail_site));
				altdateAjout.setChampAModifier(ajout_detailsite);
				if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_detailsite))
				{
					altdateAjout.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_detailsite, ""));
				}
				mList.add(altdateAjout);
			}
			else
			{
				this.notice__detailsite = mBundle.getString(Contribution.DETAIL_SITE);
				ContribElementList altCouleurModif= new ContribElementList();
				altCouleurModif.setTitre(getResources().getString(R.string.Detail_site));
				altCouleurModif.setChampAModifier(modif__detailsite);
				altCouleurModif.setOriginalValue(notice__detailsite);

				if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.modif__detailsite))
				{
					altCouleurModif.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif__detailsite, ""));
				}
				mList.add(altCouleurModif);
				//list.add(modif_nomsite);

			}

			//fin detail site

			/**
			 * 
			 * Nature - Notice existante
			 * 
			 */
			if(mBundle.containsKey(Contribution.NATURE) && mBundle.getString(Contribution.NATURE).isEmpty() )
			{
				// list.add(ajout_nature);


				ContribElementList altdateAjout= new ContribElementList();
				altdateAjout.setTitre(getResources().getString(R.string.Nature));
				altdateAjout.setChampAModifier(ajout_nature);
				if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_nature))
				{
					altdateAjout.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_nature, ""));
				}
				mList.add(altdateAjout);
			}
			else
			{
				this.notice_nature = mBundle.getString(Contribution.NATURE);
				ContribElementList altCouleurModif= new ContribElementList();
				altCouleurModif.setTitre(getResources().getString(R.string.Nature));
				altCouleurModif.setChampAModifier(modif_nature);
				altCouleurModif.setOriginalValue(notice_nature);

				if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.modif_nature))
				{
					altCouleurModif.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_nature, ""));
				}
				mList.add(altCouleurModif);
			}

			/**
			 * 
			 * Localisation - Notice existante
			 * 
			 */
			if(mBundle.containsKey(Contribution.LATITUDE) )
			{
				ContribElementList localisationModif= new ContribElementList();
				if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.modif_localisation))
				{
					if(ListChampsNoticeModif.cPref.getBoolean(ListChampsNoticeModif.modif_localisation, false))
					{	
						String la= ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.LATITUDE, "");
						String lo=ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.LONGITUDE, "");
						//FBO inversion ici a priori à vérifier - inversion ok pour l'instant
						localisationModif.setValue(lo+" , "+la);
					}

				}
				notice_longitude=mBundle.getDouble(Contribution.LONGITUDE);
				notice_latitude= mBundle.getDouble(Contribution.LATITUDE);
				//FBO affichage est long / Latitude ??? to check
				String aff = notice_latitude+" - "+notice_longitude;
				localisationModif.setOriginalValue(aff);
				localisationModif.setTitre(getResources().getString(R.string.Localisation));
				localisationModif.setChampAModifier(modif_localisation);
				mList.add(localisationModif);
			}
			else
			{
				ContribElementList localisationAjout= new ContribElementList();
				localisationAjout.setTitre(getResources().getString(R.string.Localisation));
				localisationAjout.setChampAModifier(ajout_localisation);
				if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_localisation))
				{
					if(ListChampsNoticeModif.cPref.getBoolean(ListChampsNoticeModif.ajout_localisation, false))
					{	
						String la= ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.LATITUDE, "");
						String lo=ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.LONGITUDE, "");
						//FBO to check affichage par défaut dans l'autre ordre
						localisationAjout.setValue(lo+" , "+la);
					}

				}
				mList.add(localisationAjout);
			}


			/**
			 * 
			 * Autres infos - Notice existante
			 * 
			 */
			ContribElementList altdateAjout2= new ContribElementList();
			altdateAjout2.setTitre(getResources().getString(R.string.Autres_infos));
			altdateAjout2.setChampAModifier(ajout_autre);
			if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_autre))
			{
				altdateAjout2.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_autre, ""));
			}
			mList.add(altdateAjout2);

			/**
			 * 
			 * Etat de conservation - Notice existante
			 * 
			 */
			ContribElementList altdateAjout3= new ContribElementList();
			altdateAjout3.setTitre(getResources().getString(R.string.Etat_de_conservation));
			altdateAjout3.setChampAModifier(ajout_etat);
			if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_etat))
			{
				altdateAjout3.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_etat, ""));
			}
			mList.add(altdateAjout3);


			/**
			 * 
			 * Precision etat de contribution - Notice existante
			 * 
			 */
			ContribElementList altdateAjout4= new ContribElementList();
			altdateAjout4.setTitre(getResources().getString(R.string.Precision_sur_l_etat_de_conservation));
			altdateAjout4.setChampAModifier(ajout_petat);
			if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_petat))
			{
				altdateAjout4.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_petat, ""));
			}
			mList.add(altdateAjout4);

			/**
			 * 
			 * Accessibilite PMR - Notice existante
			 * 
			 */
			ContribElementList altdateAjout54= new ContribElementList();
			altdateAjout54.setTitre(getResources().getString(R.string.accessibilite_pmr));
			altdateAjout54.setChampAModifier(ajout_pmr);
			if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_pmr))
			{
				altdateAjout54.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_pmr, ""));
			}
			mList.add(altdateAjout54);  
		}
		/**
		 * 
		 *  CREATION **********************************************************************************
		 */
		else //c'est une creation
		{
			Log.d(DEBUG_TAG, "idnotice n'existe pas");
			this.notice_id =0;
			boolean f = cPref.edit().putBoolean(ListChampsNoticeModif.creationNotice, true).commit();//on a pas IDnotice, donc c'est une creation 
			Log.d(DEBUG_TAG, "enregistrement des preference creationNotice ="+ f);
			getActionBar().setTitle("Création d'une nouvelle oeuvre");
			//on ajoute a la listView tous les ajouts possibles

			/**
			 * 
			 * PHOTO - création de notice
			 * 
			 */
			//affiche le bouton "prendre photo", ou l'image qu'on a prise sinon
			String[] modifPhoto= cPref.getString(ListChampsNoticeModif.ajout_photo, "").split("/");
			Log.d(DEBUG_TAG, "photo = "+modifPhoto[modifPhoto.length-1]);
			if(modifPhoto != null && !modifPhoto[modifPhoto.length-1].equals(""))
			{
				mBlockModifierImage.setVisibility(View.VISIBLE);
				this.mLoadPhoto.setVisibility(View.GONE);
				loadingPhoto2 upl = new loadingPhoto2(this,  modifPhoto[modifPhoto.length-1], true);
				upl.execute();
			}
			else
			{
				//affiche le bouton prendre photo
				mBlockModifierImage.setVisibility(View.GONE);
				this.mLoadPhoto.setVisibility(View.VISIBLE);
			}


			/**
			 * 
			 * TITRE - création de notice
			 * 
			 */
			ContribElementList altCouleurModif2= new ContribElementList();
			altCouleurModif2.setTitre(getResources().getString(R.string.Titre));
			altCouleurModif2.setChampAModifier(ajout_titre);
			altCouleurModif2.setOriginalValue(notice_titre);

			if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_titre))
			{
				altCouleurModif2.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_titre, ""));
			}
			mList.add(altCouleurModif2);


			/**
			 * 
			 * ARTISTE - création de notice
			 * 
			 */
			ContribElementList altCouleurModif23= new ContribElementList();
			altCouleurModif23.setTitre(getResources().getString(R.string.Artiste));
			altCouleurModif23.setChampAModifier(ajout_artiste);
			altCouleurModif23.setOriginalValue(notice_artiste);

			if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_artiste))
			{
				altCouleurModif23.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_artiste, ""));
			}
			mList.add(altCouleurModif23);

			/*
			 * 
			 * LOCALISATION
			 * 
			 */
			ContribElementList localisationAjout= new ContribElementList();
			localisationAjout.setTitre(getResources().getString(R.string.Localisation));
			localisationAjout.setChampAModifier(ajout_localisation);
			if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_localisation))
			{
				if(ListChampsNoticeModif.cPref.getBoolean(ListChampsNoticeModif.ajout_localisation, false))
				{	
					String la= ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.LATITUDE, "");
					String lo=ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.LONGITUDE, "");
					//FBO to check if OK
					localisationAjout.setValue(lo+" , "+la);
				}

			}
			mList.add(localisationAjout);

			/**
			 * 
			 * DATE - création de notice
			 * 
			 */
			ContribElementList altCouleurModif234= new ContribElementList();
			altCouleurModif234.setTitre(getResources().getString(R.string.Date));
			altCouleurModif234.setChampAModifier(ajout_date);
			altCouleurModif234.setOriginalValue(notice_dateinauguration);

			if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_date))
			{
				altCouleurModif234.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_date, ""));
			}
			mList.add(altCouleurModif234);

			/**
			 * 
			 * COULEUR - création de notice
			 * 
			 */

			ContribElementList altCouleurModif2345= new ContribElementList();
			altCouleurModif2345.setTitre(getResources().getString(R.string.Couleurs));
			altCouleurModif2345.setChampAModifier(ajout_couleur);
			altCouleurModif2345.setOriginalValue(notice_couleur);

			if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_couleur))
			{
				altCouleurModif2345.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_couleur, ""));
			}
			mList.add(altCouleurModif2345);

			/**
			 * 
			 * DESCRIPTION - création de notice
			 * 
			 */
			ContribElementList altCouleurModif23456= new ContribElementList();
			altCouleurModif23456.setTitre(getResources().getString(R.string.Description));
			altCouleurModif23456.setChampAModifier(ajout_description);
			altCouleurModif23456.setOriginalValue(notice_couleur);

			if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_description))
			{
				altCouleurModif23456.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_description, ""));
			}
			mList.add(altCouleurModif23456);

			/**
			 * 
			 * MATERIAUX - création de notice
			 * 
			 */
			ContribElementList altCouleurModif234567= new ContribElementList();
			altCouleurModif234567.setTitre(getResources().getString(R.string.Materiaux));
			altCouleurModif234567.setChampAModifier(ajout_materiaux);
			altCouleurModif234567.setOriginalValue(notice_materiaux);

			if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_materiaux))
			{
				altCouleurModif234567.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_materiaux, ""));
			}
			mList.add(altCouleurModif234567);

			/**
			 * 
			 * nom du site - création de notice
			 * 
			 */
			ContribElementList nomSite= new ContribElementList();
			nomSite.setTitre(getResources().getString(R.string.Nom_du_site));
			nomSite.setChampAModifier(ajout_nomsite);


			if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_nomsite))
			{
				nomSite.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_nomsite, ""));
			}
			mList.add(nomSite);

			/**
			 * 
			 * dETAIL SITE - création de notice
			 * 
			 */
			ContribElementList DetailSite= new ContribElementList();
			DetailSite.setTitre(getResources().getString(R.string.Detail_site));
			DetailSite.setChampAModifier(ajout_detailsite);


			if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_detailsite))
			{
				DetailSite.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_detailsite, ""));
			}
			mList.add(DetailSite);
			/**
			 * 
			 * NATURE - création de notice
			 * 
			 */
			ContribElementList eltNature= new ContribElementList();
			eltNature.setTitre(getResources().getString(R.string.Nature));
			eltNature.setChampAModifier(ajout_nature);


			if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_nature))
			{
				eltNature.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_nature, ""));
			}
			mList.add(eltNature);


			/**
			 * 
			 * autres infos - création de notice
			 * 
			 */
			ContribElementList eltAutre= new ContribElementList();
			eltAutre.setTitre(getResources().getString(R.string.Autres_infos));
			eltAutre.setChampAModifier(ajout_autre);


			if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_autre))
			{
				eltAutre.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_autre, ""));
			}
			mList.add(eltAutre);

			/**
			 * 
			 * ETAT CONSERVATION - création de notice
			 * 
			 */
			ContribElementList eltEtat= new ContribElementList();
			eltEtat.setTitre(getResources().getString(R.string.Etat_de_conservation));
			eltEtat.setChampAModifier(ajout_etat);


			if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_etat))
			{
				eltEtat.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_etat, ""));
			}
			mList.add(eltEtat);

			/**
			 * 
			 * precision etat - création de notice
			 * 
			 */
			ContribElementList eltPEtat= new ContribElementList();
			eltPEtat.setTitre(getResources().getString(R.string.Precision_sur_l_etat_de_conservation));
			eltPEtat.setChampAModifier(ajout_petat);


			if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_petat))
			{
				eltPEtat.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_petat, ""));
			}
			mList.add(eltPEtat);

			/**
			 * 
			 * accesibilite PMR - création de notice
			 * 
			 */
			ContribElementList eltpmr= new ContribElementList();
			eltpmr.setTitre(getResources().getString(R.string.accessibilite_pmr));
			eltpmr.setChampAModifier(ajout_pmr);


			if(ListChampsNoticeModif.cPref.contains(ListChampsNoticeModif.ajout_pmr))
			{
				eltpmr.setValue(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_pmr, ""));
			}
			mList.add(eltpmr);
		}//fin traitement creation de notice

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
			Contribution.createContributionAjout(c1, this.notice_id, this.IDLocal, Contribution.TITRE, ajoutTitreValue);

			mContribXml.listContributionEnCours.add(c1);
			valueModif= true;

		}
		if(!TitreValue.equals(""))
		{
			Contribution c2 = new Contribution();
			addInfoNoticeToContribution(c2);
			Contribution.createContributionRemplacement(c2,this.notice_id, this.IDLocal, Contribution.TITRE, TitreValue);
			mContribXml.listContributionEnCours.add(c2);
			valueModif= true;
		}
		Log.d(DEBUG_TAG+"/photo ajout", "value photo ="+ajoutPhotoValue);
		if(!ajoutPhotoValue.equals(""))
		{
			Contribution c3 = new Contribution();
			addInfoNoticeToContribution(c3);
			Contribution.createContributionAjout(c3, this.notice_id, this.IDLocal, Contribution.PHOTO, ajoutPhotoValue);
			mContribXml.listContributionEnCours.add(c3);
			valueModif= true;
		}
		if(!PhotoValue.equals(""))
		{
			Contribution c4 = new Contribution();
			addInfoNoticeToContribution(c4);
			Contribution.createContributionRemplacement(c4,this.notice_id, this.IDLocal, Contribution.PHOTO, PhotoValue);
			mContribXml.listContributionEnCours.add(c4);
			valueModif= true;
		}
		if(!ajoutCouleurValue.equals(""))
		{
			Contribution c5 = new Contribution();
			addInfoNoticeToContribution(c5);
			Contribution.createContributionAjout(c5, this.notice_id, this.IDLocal, Contribution.COULEUR, ajoutCouleurValue);
			mContribXml.listContributionEnCours.add(c5);
			valueModif= true;
		}
		if(!CouleurValue.equals(""))
		{
			Contribution c16 = new Contribution();
			addInfoNoticeToContribution(c16);
			Contribution.createContributionRemplacement(c16, this.notice_id, this.IDLocal, Contribution.COULEUR, CouleurValue);
			mContribXml.listContributionEnCours.add(c16);
			valueModif= true;
		}
		if(!ajoutDateValue.equals(""))
		{
			Contribution c6 = new Contribution();
			addInfoNoticeToContribution(c6);
			Contribution.createContributionAjout(c6, this.notice_id, this.IDLocal, Contribution.DATE_INAUGURATION, ajoutDateValue);
			mContribXml.listContributionEnCours.add(c6);
			valueModif= true;
		}
		if(!modif_dateValue.equals(""))
		{
			Contribution c7 = new Contribution();
			addInfoNoticeToContribution(c7);
			Contribution.createContributionRemplacement(c7, this.notice_id, this.IDLocal, Contribution.DATE_INAUGURATION, modif_dateValue);
			mContribXml.listContributionEnCours.add(c7);
			valueModif= true;
		}

		if(!ajoutMateriauxValue.equals(""))
		{
			Contribution c10 = new Contribution();
			addInfoNoticeToContribution(c10);
			Contribution.createContributionAjout(c10, this.notice_id, this.IDLocal, Contribution.MATERIAUx, ajoutMateriauxValue);
			mContribXml.listContributionEnCours.add(c10);
			valueModif= true;
		}
		if(!modif_materiauxValue.equals(""))
		{
			Contribution c11 = new Contribution();
			addInfoNoticeToContribution(c11);
			Contribution.createContributionRemplacement(c11, this.notice_id, this.IDLocal, Contribution.MATERIAUx, modif_materiauxValue);
			mContribXml.listContributionEnCours.add(c11);
			valueModif= true;
		}
		if(!ajout_descriptionValue.equals(""))
		{
			Contribution c12 = new Contribution();
			addInfoNoticeToContribution(c12);
			Contribution.createContributionAjout(c12, this.notice_id, this.IDLocal, Contribution.DESCRIPTION, ajout_descriptionValue);
			mContribXml.listContributionEnCours.add(c12);
			valueModif= true;
		}
		if(!modif_descriptionValue.equals(""))
		{
			Contribution c13 = new Contribution();
			addInfoNoticeToContribution(c13);
			Contribution.createContributionRemplacement(c13, this.notice_id, this.IDLocal, Contribution.DESCRIPTION, modif_descriptionValue);
			mContribXml.listContributionEnCours.add(c13);
			valueModif= true;
		}
		if(!ajoutArtisteValue.equals(""))
		{
			Contribution c14 = new Contribution();
			addInfoNoticeToContribution(c14);
			Contribution.createContributionAjout(c14, this.notice_id, this.IDLocal, Contribution.ARTISTE, ajoutArtisteValue);
			mContribXml.listContributionEnCours.add(c14);
			valueModif= true;
		}
		if(!ArtisteValue.equals(""))
		{
			Contribution c15 = new Contribution();
			addInfoNoticeToContribution(c15);//rajoute les infos notice pour identification, puis on ajoute les modif de l"utilisateur
			Contribution.createContributionRemplacement(c15, this.notice_id, this.IDLocal, Contribution.ARTISTE, ArtisteValue);
			mContribXml.listContributionEnCours.add(c15);
			valueModif= true;
		}
		if(!modif_NomSiteValue.equals(""))
		{
			Contribution c15 = new Contribution();
			addInfoNoticeToContribution(c15);//rajoute les infos notice pour identification, puis on ajoute les modif de l"utilisateur
			Contribution.createContributionRemplacement(c15, this.notice_id, this.IDLocal, Contribution.NOM_SITE, modif_NomSiteValue);
			mContribXml.listContributionEnCours.add(c15);
			valueModif= true;
		}
		if(!ajoutNomSiteValue.equals(""))
		{
			Contribution c14 = new Contribution();
			addInfoNoticeToContribution(c14);
			Contribution.createContributionAjout(c14, this.notice_id, this.IDLocal, Contribution.NOM_SITE, ajoutNomSiteValue);
			mContribXml.listContributionEnCours.add(c14);
			valueModif= true;
		}
		if(!ajoutNatureValue.equals(""))
		{
			Contribution c14 = new Contribution();
			addInfoNoticeToContribution(c14);
			Contribution.createContributionAjout(c14, this.notice_id, this.IDLocal, Contribution.NATURE, ajoutNatureValue);
			mContribXml.listContributionEnCours.add(c14);
			valueModif= true;
		}
		if(!modif_NatureValue.equals(""))
		{
			Contribution c15 = new Contribution();
			addInfoNoticeToContribution(c15);//rajoute les infos notice pour identification, puis on ajoute les modif de l"utilisateur
			Contribution.createContributionRemplacement(c15, this.notice_id, this.IDLocal, Contribution.NATURE, modif_NatureValue);
			mContribXml.listContributionEnCours.add(c15);
			valueModif= true;
		}
		if(!ajoutAutreValue.equals(""))
		{
			Contribution c14 = new Contribution();
			addInfoNoticeToContribution(c14);
			Contribution.createContributionAjout(c14, this.notice_id, this.IDLocal, Contribution.AUTRE, ajoutAutreValue);
			mContribXml.listContributionEnCours.add(c14);
			valueModif= true;
		}
		if(!ajoutEtatValue.equals(""))
		{
			Contribution c14 = new Contribution();
			addInfoNoticeToContribution(c14);
			Contribution.createContributionAjout(c14, this.notice_id, this.IDLocal, Contribution.ETAT, ajoutEtatValue);
			mContribXml.listContributionEnCours.add(c14);
			valueModif= true;
		}
		if(!ajoutPetatValue.equals(""))
		{
			Contribution c14 = new Contribution();
			addInfoNoticeToContribution(c14);
			Contribution.createContributionAjout(c14, this.notice_id, this.IDLocal, Contribution.PETAT, ajoutPetatValue);
			mContribXml.listContributionEnCours.add(c14);
			valueModif= true;
		}
		if(!ajoutPmrValue.equals(""))
		{
			Contribution c14 = new Contribution();
			addInfoNoticeToContribution(c14);
			Contribution.createContributionAjout(c14, this.notice_id, this.IDLocal, Contribution.PMR, ajoutPmrValue);
			mContribXml.listContributionEnCours.add(c14);
			valueModif= true;
		}

		if(!ajoutDetailSiteValue.equals(""))
		{
			Contribution c14 = new Contribution();
			addInfoNoticeToContribution(c14);
			Contribution.createContributionAjout(c14, this.notice_id, this.IDLocal, Contribution.DETAIL_SITE, ajoutDetailSiteValue);
			mContribXml.listContributionEnCours.add(c14);
			valueModif= true;
		}

		if(!modif_DetailSiteValue.equals(""))
		{
			Contribution c14 = new Contribution();
			addInfoNoticeToContribution(c14);
			Contribution.createContributionRemplacement(c14, this.notice_id, this.IDLocal, Contribution.DETAIL_SITE, modif_DetailSiteValue);
			mContribXml.listContributionEnCours.add(c14);
			valueModif= true;
		}
		if(modifLocalisationValue )
		{
			Contribution c15 = new Contribution();
			addInfoNoticeToContribution(c15);//rajoute les infos notice pour identification, puis on ajoute les modif de l"utilisateur
			Contribution.createContributionRemplacementCoordonnee(c15, this.notice_id, this.IDLocal, Contribution.COORDONNEES, this.latitude, this.longitude);
			mContribXml.listContributionEnCours.add(c15);
			valueModif= true;
		}
		if(ajoutLocalisationValue)
		{
			Contribution c15 = new Contribution();
			addInfoNoticeToContribution(c15);//rajoute les infos notice pour identification, puis on ajoute les modif de l"utilisateur
			Contribution.createContributionAjoutCoordonnee(c15, this.notice_id, this.IDLocal, Contribution.COORDONNEES, this.latitude, this.longitude);
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
