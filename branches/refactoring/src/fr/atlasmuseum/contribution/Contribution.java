package fr.atlasmuseum.contribution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import fr.atlasmuseum.R;
import fr.atlasmuseum.search.JsonRawData;
import fr.atlasmuseum.search.SearchActivity;

public class Contribution implements Serializable {

	private static final long serialVersionUID = 7388740279075848884L;

	private static final String DEBUG_TAG = "AtlasMuseum/Contribution2";
	
	static final String SAVE_FILE = "contributions.xml";

	static final String CONTRIBUTION = "contribution";
	static final String VALUE = "value";

	static final String MODIFICATION = "modification";

	// Contribution type
	static final String TYPE = "type";
	static final String CREER = "creer";
	static final String AJOUTER = "ajouter";
	static final String REMPLACER = "remplacer";

	// Contribution status
	static final String STATUT = "statut";
	static final String ENATTENTE = "enattente";
	static final String ACCEPTED = "acceptee";
	static final String CANCELED = "annulee";


	static final String NOTICE_ID = "id";
	static final String LOCAL_ID = "localid";
	
	static final String AUTEUR = "auteur";
	static final String PASSWORD = "passwd";

	static final String DATECONTRIBUTION = "date";
	static final String HEURECONTRIBUTION = "heure";

	public static final String URL = "url";
	public static final String CREDIT_PHOTO = "creditphoto";
	public static final String PHOTO = "photo";
	public static final String TITRE= "titre";
	public static final String ARTISTE= "artiste";
	public static final String MATERIAUX = "materiaux";
	public static final String DATE_INAUGURATION = "inauguration";
	public static final String DESCRIPTION = "description";
	public static final String LONGITUDE = "longitude";
	public static final String LATITUDE = "latitude";
	public static final String COULEUR = "couleur";
	public static final String ETAT = "etat";
	public static final String PETAT = "petat";
	public static final String PMR = "pmr";
	public static final String NOM_SITE = "nomsite";
	public static final String DETAIL_SITE = "detailsite";
	public static final String AUTRE = "autre";
	public static final String NATURE = "nature";
	public static final String MOT_CLE = "mot_cle";
	public static final String CONTEXTE_PRODUCTION = "contexte_production";
	public static final String VILLE = "Siteville";
	public static final String REGION = "Siteregion";
	public static final String PAYS = "Sitepays";
	public static final String MOUVEMENT = "mouvement_artistes";

	int mNoticeId;
	String mLocalId;
	String mLogin;
	String mPassword;
	String mDate;
	String mTime;
	String mStatus;
	HashMap<String,ContributionProperty> mProperties;
	
	public static enum StatusType {
		SENT, enattente, EDITING, UNKNOW;
	}
	
	public static enum PropertyType	{
		titre, photo, couleur, date, materiaux, description, artiste, nature, nomsite, autre, etat, petat, pmr, localisation
	}

	// <contribution>
	String xmlTypes[] = {
			"type" /*remplacer/creer/ajout*/,
			"statut" /*enattente/acceptee/annulee*/,
			"modification" /*champ modifié en cas de modification*/,
			"id",
			"localid",
			"auteur",
			"passwd",
			"latitude",
			"longitude",
			"photo",
			"artiste",
			"titre",
			"description",
			"materiaux",
			"inauguration",
			"etat",
			"petat",
			"nature",
			"nomsite",
			"detailsite",
			"pmr",
			"autre",
			"date",
			"heure"
	};
	//</contribution>
	
//	listeMateriaux
//	listeNatures
//	listeCouleurs
//	listePrecision_etat_conservation
//	listeAutre_precision_etat_conservation
//	listePmr
	
	//String pour la ListView Ajouter/Modifier _ Utiliser pour récupérer les valeurs associées à l'aide de cPref
	//exemple: String ajoutTitreValue = cPref.getString(ajout_titre, "");
	final static String ajout_titre = "Ajouter un titre" ;
	final static String ajout_photo = "Ajouter une photo";
	final static String ajout_couleur = "Ajouter une couleur";
	final static String ajout_date = "Ajouter la date d'inauguration";
	final static String ajout_materiaux = "Ajouter des matériaux";
	final static String ajout_description = "Ajouter une description";
	final static String ajout_artiste = "Ajouter un artiste";
	final static String ajout_nature = "Ajouter la nature de l'oeuvre";
	final static String ajout_nomsite = "Ajouter le nom du site";
	final static String ajout_autre = "Autres informations";
	final static String ajout_etat = "Ajouter l'état de conservation";
	final static String ajout_petat = "Précision sur l'état de conservation";
	final static String  ajout_detailsite = "Ajouter detail site";
	final static String ajout_pmr = "Définir l'accessibilité du site";
	final static String ajout_localisation = "Ajouter localisation";
	static String champs_a_modifier = "champs a modifier ";//utilisé dans Activity, détermine quelle valeur de cPref est modifiée

	public Contribution() {
		mNoticeId = 0;
		mLocalId = "";
		mLogin = "";
		mPassword = "";
		mDate = "";
		mTime = "";
		mStatus = "";
		mProperties = new HashMap<String, ContributionProperty>();
		
		mProperties.put( URL, new ContributionProperty(
				/* dbField */ URL,
				/* jsonField */ "url",
				/* title */ 0,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0,
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ 0,
				/* showViewToHide */ 0,
				/* dumpInXML */ false) );
		
		mProperties.put( CREDIT_PHOTO, new ContributionProperty(
				/* dbField */ CREDIT_PHOTO,
				/* jsonField */ "creditphoto",
				/* title */ 0,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0,
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.credit_photo,
				/* showViewToHide */ 0,
				/* dumpInXML */ false) );
		
		mProperties.put( PHOTO, new ContributionProperty(
				/* dbField */ PHOTO,
				/* jsonField */ "image_principale",
				/* title */ 0,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0,
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ 0,
				/* showViewToHide */ 0,
				/* dumpInXML */ true) );
		
		mProperties.put(TITRE, new ContributionProperty(
				/* dbField */ TITRE,
				/* jsonField */ "titre",
				/* title */ R.string.Titre,
				/* value */ "",
				/* defaultValue */ "Pas de titre", // TODO : resourcify
				/* info */ 0,
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.notice_titre,
				/* showViewToHide */ 0,
				/* dumpInXML */ true) );
		
		mProperties.put(ARTISTE, new ContributionProperty(
				/* dbField */ ARTISTE,
				/* jsonField */ "artiste",
				/* title */ R.string.Artiste,
				/* value */ "",
				/* defaultValue */ "Unknown", // TODO : resourcify
				/* info */ 0, // TODO: ajouter les infos
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.notice_artiste,
				/* showViewToHide */ 0,
				/* dumpInXML */ true) );

		mProperties.put(COULEUR, new ContributionProperty(
				/* dbField */ COULEUR,
				/* jsonField */ "couleur",
				/* title */ R.string.Couleurs,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0,
				/* type */ ContributionProperty.ContribType.check,
				/* choices */ JsonRawData.listeCouleurs,
				/* showViewText */ R.id.oeuvre_couleur_value,
				/* showViewToHide */ R.id.relativ_oeuvre_couleur,
				/* dumpInXML */ true) );
		
		mProperties.put( DATE_INAUGURATION, new ContributionProperty(
				/* dbField */ DATE_INAUGURATION,
				/* jsonField */ "inauguration",
				/* title */ R.string.Date,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ R.string.contrib_date_infos,
				/* type */ ContributionProperty.ContribType.date,
				/* choices */ null,
				/* showViewText */ R.id.notice_annee,
				/* showViewToHide */ R.id.notice_annee,
				/* dumpInXML */ true) );
		
		mProperties.put(DESCRIPTION, new ContributionProperty(
				/* dbField */ DESCRIPTION,
				/* jsonField */ "description",
				/* title */ R.string.Description,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0,
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.oeuvre_description_value,
				/* showViewToHide */ R.id.relativ_oeuvre_description,
				/* dumpInXML */ true) );
		
		mProperties.put( MATERIAUX, new ContributionProperty(
				/* dbField */ MATERIAUX,
				/* jsonField */ "materiaux",
				/* title */ R.string.Materiaux,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0,
				/* type */ ContributionProperty.ContribType.check,
				/* choices */ JsonRawData.listeMateriaux,
				/* showViewText */ R.id.oeuvre_materiauw_value,
				/* showViewToHide */ R.id.relativ_oeuvre_materiaux,
				/* dumpInXML */ true) );
		
		mProperties.put( NOM_SITE, new ContributionProperty(
				/* dbField */ NOM_SITE,
				/* jsonField */ "Sitenom",
				/* title */ R.string.Nom_du_site,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0, // TODO: ajouter les infos
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.site_nomsite_value,
				/* showViewToHide */ R.id.relativ_site_nomsite,
				/* dumpInXML */ true) );
		
		mProperties.put( DETAIL_SITE, new ContributionProperty(
				/* dbField */ DETAIL_SITE,
				/* jsonField */ "Sitedetails",
				/* title */ R.string.Detail_site,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0, // TODO: ajouter les infos
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.site_detailsite_value,
				/* showViewToHide */ R.id.relativ_site_detailsite,
				/* dumpInXML */ true) );
		
		mProperties.put( NATURE, new ContributionProperty(
				/* dbField */ NATURE,
				/* jsonField */ "nature",
				/* title */ R.string.Nature,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0,
				/* type */ ContributionProperty.ContribType.radio,
				/* choices */ JsonRawData.listeNatures,
				/* showViewText */ R.id.oeuvre_nature_value,
				/* showViewToHide */ R.id.relativ_oeuvre_nature,
				/* dumpInXML */ true) );
		
		mProperties.put( LATITUDE, new ContributionProperty(
				/* dbField */ LATITUDE,
				/* jsonField */ "latitude",
				/* title */ R.string.Latitude,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0,
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ 0,
				/* showViewToHide */ 0,
				/* dumpInXML */ true) );
		
		mProperties.put( LONGITUDE, new ContributionProperty(
				/* dbField */ LONGITUDE,
				/* jsonField */ "longitude",
				/* title */ R.string.Longitude,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0,
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ 0,
				/* showViewToHide */ 0,
				/* dumpInXML */ true) );
		
		mProperties.put( AUTRE, new ContributionProperty(
				/* dbField */ AUTRE,
				/* jsonField */ "autre",
				/* title */ R.string.Autres_infos,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0, // TODO: ajouter les infos
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ 0,
				/* showViewToHide */ 0,
				/* dumpInXML */ true) );
		
		mProperties.put( ETAT, new ContributionProperty(
				/* dbField */ ETAT,
				/* jsonField */ "precision_etat_conservation",
				/* title */ R.string.Etat_de_conservation,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0,
				/* type */ ContributionProperty.ContribType.radio,
				/* choices */ JsonRawData.listePrecision_etat_conservation,
				/* showViewText */ 0,
				/* showViewToHide */ 0,
				/* dumpInXML */ true) );
		
		mProperties.put( PETAT, new ContributionProperty(
				/* dbField */ PETAT,
				/* jsonField */ "autre_precision_etat_conservation",
				/* title */ R.string.Precision_sur_l_etat_de_conservation,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0,
				/* type */ ContributionProperty.ContribType.check,
				/* choices */ JsonRawData.listeAutre_precision_etat_conservation,
				/* showViewText */ 0,
				/* showViewToHide */ 0,
				/* dumpInXML */ true) );
		
		mProperties.put( PMR, new ContributionProperty(
				/* dbField */ PMR,
				/* jsonField */ "",
				/* title */ R.string.accessibilite_pmr,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0, // TODO: ajouter les infos
				/* type */ ContributionProperty.ContribType.radio,
				/* choices */ JsonRawData.listePmr,
				/* showViewText */ R.id.site_pmr_value,
				/* showViewToHide */ R.id.relativ_site_pmr,
				/* dumpInXML */ true) );

		mProperties.put( MOT_CLE, new ContributionProperty(
				/* dbField */ MOT_CLE,
				/* jsonField */ "mot_cle",
				/* title */ R.string.Mots_cles,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0,
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.oeuvre_mots_cles_value,
				/* showViewToHide */ R.id.relativ_oeuvre_mots_cles,
				/* dumpInXML */ false) );

		mProperties.put( CONTEXTE_PRODUCTION, new ContributionProperty(
				/* dbField */ CONTEXTE_PRODUCTION,
				/* jsonField */ "contexte_production",
				/* title */ R.string.Contexte,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0,
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.oeuvre_contexte_value,
				/* showViewToHide */ R.id.relativ_oeuvre_contexte,
				/* dumpInXML */ false) );

		mProperties.put( VILLE, new ContributionProperty(
				/* dbField */ VILLE,
				/* jsonField */ "Siteville",
				/* title */ R.string.ville,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0,
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.site_ville_value,
				/* showViewToHide */ R.id.relativ_site_ville,
				/* dumpInXML */ false) );

		mProperties.put( REGION, new ContributionProperty(
				/* dbField */ REGION,
				/* jsonField */ "Siteregion",
				/* title */ R.string.Region,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0,
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.site_region_value,
				/* showViewToHide */ R.id.relativ_site_region,
				/* dumpInXML */ false) );

		mProperties.put( PAYS, new ContributionProperty(
				/* dbField */ PAYS,
				/* jsonField */ "Sitepays",
				/* title */ R.string.Pays,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0,
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.site_pays_value,
				/* showViewToHide */ R.id.relativ_site_pays,
				/* dumpInXML */ false) );

		mProperties.put( MOUVEMENT, new ContributionProperty(
				/* dbField */ MOUVEMENT,
				/* jsonField */ "mouvement_artistes",
				/* title */ R.string.Mouvement,
				/* value */ "",
				/* defaultValue */ "",
				/* info */ 0,
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.artiste_mouvement_value,
				/* showViewToHide */ R.id.relativ_artiste_mouvement,
				/* dumpInXML */ false) );

	}

	public void updateFromDb(int index) {
		mNoticeId = Integer.parseInt(SearchActivity.extractDataFromDb(index,NOTICE_ID));

		for (ContributionProperty value : mProperties.values()) {
		    value.updateFromDb(index);
		}
	}

	static void updateFromXml(Contribution contribution, Element elemContrib) {
		Element elemNoticeId = elemContrib.getChild(NOTICE_ID); 
		if( elemNoticeId != null) {
			contribution.mNoticeId = Integer.parseInt(elemNoticeId.getAttributeValue(VALUE));
		}
		
		Element elemLocalId = elemContrib.getChild(LOCAL_ID); 
		if( elemLocalId != null) {
			contribution.mLocalId = elemNoticeId.getAttributeValue(VALUE);
		}
		
		Element elemLogin = elemContrib.getChild(AUTEUR); 
		if( elemLogin != null) {
			contribution.mLogin = elemLogin.getAttributeValue(VALUE);
		}
		
		Element elemPassword = elemContrib.getChild(PASSWORD); 
		if( elemPassword != null) {
			contribution.mPassword = elemLogin.getAttributeValue(VALUE);
		}

		Element elemDate = elemContrib.getChild(DATECONTRIBUTION); 
		if( elemDate != null) {
			contribution.mDate = elemDate.getAttributeValue(VALUE);
		}

		Element elemTime= elemContrib.getChild(HEURECONTRIBUTION); 
		if( elemTime != null) {
			contribution.mTime = elemDate.getAttributeValue(VALUE);
		}

		Element elemStatus = elemContrib.getChild(STATUT); 
		if( elemStatus != null) {
			contribution.mStatus = elemLogin.getAttributeValue(VALUE);
		}

		for (ContributionProperty value : contribution.mProperties.values()) {
		    value.updateFromXml(elemContrib);
		}

		// If there is an modified property, set its original value to "" to make it different from value
		Element elemModification = elemContrib.getChild(MODIFICATION); 
		if( elemModification != null) {
			String modification = elemNoticeId.getAttributeValue(VALUE);
			ContributionProperty prop = contribution.getProperty(modification);
			if( prop != null ) {
				prop.setOriginalValue("");
			}
		}
	}
	
	public int getNoticeId() {
		return mNoticeId;
	}
	public void setNoticeId(int id) {
		mNoticeId = id;
	}
	
	public String getLocalId() {
		return mLocalId;
	}
	public void setLocalId(String id) {
		mLocalId = id;
	}
	
	public String getLogin() {
		return mLogin;
	}
	public void setLogin(String login) {
		mLogin = login;
	}
	
	public String getPassword() {
		return mPassword;
	}
	public void setPassword(String password) {
		mPassword = password;
	}
	

	public ContributionProperty getProperty(String name) {
		if( mProperties.containsKey(name) ) {
			return mProperties.get(name);
		}
		else {
			return null;
		}
	}
	
	public void setProperty(String name, ContributionProperty property) {
		mProperties.put(name, property);
	}
	
	public Collection<ContributionProperty> getProperties() {
		return mProperties.values();
	}
	
	public void dumpDebug() {
		//Log.d(DEBUG_TAG, "Dump contribution: id = " + mId + " - Titre = " + mProperties.get("titre").getValue());
		Log.d(DEBUG_TAG, "Dump contribution");
	}
	
	private void addCommonXML( Element elemContrib ) {
		// Add a version number to identify the XML format
		Element elemVersion = new Element("version");
		Attribute attrVersion = new Attribute(VALUE, "2");
		elemVersion.setAttribute(attrVersion);
		elemContrib.addContent(elemVersion);
		
		// Add notice id
		if( mNoticeId != 0 ) {
			Element elemNoticeId = new Element(NOTICE_ID);
			Attribute attrNoticeId = new Attribute(VALUE, String.valueOf(mNoticeId));
			elemNoticeId.setAttribute(attrNoticeId);
			elemContrib.addContent(elemNoticeId);
		}
		
		// Add local id
		Element elemLocalId = new Element(LOCAL_ID);
        Attribute attrLocalId = new Attribute(VALUE, mLocalId);
        elemLocalId.setAttribute(attrLocalId);
        elemContrib.addContent(elemLocalId);
        
	    // Add status (enattente/acceptee/annulee)
	    Attribute attrStatus = new Attribute(STATUT, ENATTENTE);
	    elemContrib.setAttribute(attrStatus);
	    
		// Add login
        if( ! mLogin.equals("") ) {
        	Element elemLogin = new Element(AUTEUR);
        	Attribute attrLogin = new Attribute(VALUE, mLogin);
        	elemLogin.setAttribute(attrLogin);
        	elemContrib.addContent(elemLogin);
        }
        
        // Add password
        if( ! mPassword.equals("") ) {
        	Element elemPassword = new Element(PASSWORD);
        	Attribute attrPassword = new Attribute(VALUE, mPassword);
        	elemPassword.setAttribute(attrPassword);
        	elemContrib.addContent(elemPassword);
        }

        // Add date
        Date date = new Date();
  	  	SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
  	  	Element elemDate = new Element(DATECONTRIBUTION);
  	  	Attribute attrDate = new Attribute(VALUE, formatDate.format(date));
  	  	elemDate.setAttribute(attrDate);
  	  	elemContrib.addContent(elemDate);
  	  
  	  	// Add time
        Element elemTime = new Element(HEURECONTRIBUTION);
  	  	SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
  	  	Attribute attrTime = new Attribute(VALUE, formatTime.format(date));
  	  	elemTime.setAttribute(attrTime);
  	  	elemContrib.addContent(elemTime);
  	  
	}
	
	public void addToXML( Element parent ) {
		if( mNoticeId == 0 ) {
			// It's a creation, just dump all known info
			Element elemContrib = new Element(CONTRIBUTION);
			parent.addContent(elemContrib);
			
			// Add type
			Attribute attr_type = new Attribute(TYPE, CREER);
		    elemContrib.setAttribute(attr_type);
		    
			addCommonXML(elemContrib);
			
	  	 	for (ContributionProperty prop : mProperties.values()) {
				prop.addXML(elemContrib, false);
			}
		}
		else {
			// It's a modification, iterate over all modified properties
	  	 	for (ContributionProperty prop : mProperties.values()) {
	  	 		if( prop.isModified() ) {
	  	 			Element elemContrib = new Element(CONTRIBUTION);
	  				parent.addContent(elemContrib);

	  				// Add type
	  				Attribute attrType = new Attribute(TYPE, prop.getOriginalValue().equals("") ? AJOUTER : REMPLACER);
	  			    elemContrib.setAttribute(attrType);
	  			      
	  		  	  	// Add modified property
	  		        Element elemModif = new Element(MODIFICATION);
	  		  	  	Attribute attrModif = new Attribute(VALUE, prop.getDbField());
	  		  	  	elemModif.setAttribute(attrModif);
	  		  	  	elemContrib.addContent(elemModif);

	  				addCommonXML(elemContrib);
	  				
	  		  	 	for (ContributionProperty prop2 : mProperties.values()) {
	  		  	 		prop2.addXML( elemContrib, prop.getDbField() != prop2.getDbField() );
	  				}
	  	 		}
	  	 	}			
		}
	}
	/////////////////////////////
	// Static helper functions //
	/////////////////////////////
	
	static public String readSaveFile( Context context ) {
		String contenu = "";
		FileInputStream fIn;
		try {
			fIn = context.openFileInput(SAVE_FILE);

			InputStreamReader isr = new InputStreamReader ( fIn ) ;
			BufferedReader buffreader = new BufferedReader ( isr ) ;

			String line;
			while ((line=buffreader.readLine())!=null) {
				contenu = contenu+line;
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

//		try {
//			File file = new File(ContribXml.nomFichier);
//		    FileInputStream is;
//			is = new FileInputStream(file);	    
//			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//			StringBuilder sb = new StringBuilder();
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//			  sb.append(line).append("\n");
//			}
//			reader.close();
//			return sb.toString();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return "";
//		}
	}
	
	static public List<Contribution> getContributionsFromXmlString( String xmlString ) {
		List<Contribution> contributions = new ArrayList<Contribution>();

		SAXBuilder saxBuilder = new SAXBuilder();
		Reader stringReader = new StringReader(xmlString);
		try {
			Document document = saxBuilder.build(stringReader);
			Element root = document.getRootElement();
			List<?> list = root.getChildren(CONTRIBUTION);

			Iterator<?> i = list.iterator();
			while(i.hasNext()) {
				Element elemContrib = (Element)i.next();
				Contribution contribution = new Contribution();
				updateFromXml(contribution, elemContrib);
				contributions.add(contribution);
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return contributions;
	}

	static public File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			String dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
			storageDir = new File( dcimDir + "/atlasmuseum" );
			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()) {
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

}
