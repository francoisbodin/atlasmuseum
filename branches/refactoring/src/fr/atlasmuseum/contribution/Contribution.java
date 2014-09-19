package fr.atlasmuseum.contribution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import org.jdom2.Attribute;
import org.jdom2.Element;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import fr.atlasmuseum.R;
import fr.atlasmuseum.search.JsonRawData;
import fr.atlasmuseum.search.SearchActivity;

public class Contribution implements Serializable {

	private static final long serialVersionUID = 7388740279075848884L;

	private static final String DEBUG_TAG = "AtlasMuseum/Contribution2";
	
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
	
	public String getDate() {
		return mDate;
	}
	public void setDate(String date) {
		mDate = date;
	}
	
	public String getTime() {
		return mTime;
	}
	public void setTime(String time) {
		mTime = time;
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
	
	private void addCommonToXML( Element elemContrib ) {
		// Add a version number to identify the XML format
		Attribute attrVersion = new Attribute("version", "2");
	    elemContrib.setAttribute(attrVersion);
	    
	    // Add status (enattente/acceptee/annulee)
	    Attribute attrStatus = new Attribute(STATUT, ENATTENTE);
	    elemContrib.setAttribute(attrStatus);
	    
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
		    
			addCommonToXML(elemContrib);
			
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

	  				addCommonToXML(elemContrib);
	  				
	  		  	 	for (ContributionProperty prop2 : mProperties.values()) {
	  		  	 		prop2.addXML( elemContrib, prop.getDbField() != prop2.getDbField() );
	  				}
	  	 		}
	  	 	}			
		}
	}
	
	void save( Context context ) {
        Date date = new Date();
  	  	SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
  	  	mDate = formatDate.format(date);
  	  	SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
  	  	mTime = formatTime.format(date);

		File saveDir = new File( getSaveDir(context) );
		String filename;
		if( mNoticeId == 0 ) {
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.startsWith("new_");
				}
			};
			filename = "new_" + Integer.toString(saveDir.list(filter).length);
		}
		else {
			filename = "modif_" + Integer.toString(mNoticeId);
		}
		File file = new File(saveDir, filename );

		try {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(this);
			os.close();
			fos.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/////////////////////////////
	// Static helper functions //
	/////////////////////////////
	
	static public String getSaveDir(Context context) {
		File saveDir = new File( context.getFilesDir(), "save" );
		if( saveDir.exists() && !saveDir.isDirectory()) {
			saveDir.delete();
		}
		if( !saveDir.exists() ) {
			saveDir.mkdir();
		}
		return saveDir.getAbsolutePath();
	}
	
	static public File getPhotoDir() {
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

	@SuppressWarnings("resource")
	static public Contribution restoreFromFile( String filename ) {
		File file = new File(filename);
		if( ! file.exists() || ! file.isFile() ) {
			return null;
		}
		
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		ObjectInputStream is;
		try {
			is = new ObjectInputStream(fis);
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		Contribution contribution = null;
		try {
			contribution = (Contribution) is.readObject();
		} catch (OptionalDataException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return contribution;
	}
}
