package fr.atlasmuseum.contribution;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.irisa.unpourcent.location.LocationStruct;

import fr.atlasmuseum.contribution.Contribution.champ_status;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class Contribution implements Serializable {
private static final long serialVersionUID = 1L;
private static final String pathToFile = "/mnt/sdcard/dcim/AtlasMuseum/";
	public static class Location implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private Double mLatitude;
		private Double mLongitude;
		
		public Location() {
			mLatitude = 0.0;
			mLongitude = 0.0;
		}
		
		public Location( android.location.Location loc ) {
			mLatitude = loc.getLatitude();
			mLongitude = loc.getLongitude();
		}
		

		public Double getLatitude() {
			return mLatitude;
		}
		public void setLatitude(Double l) {
			mLatitude = l;
		}
		public Double getLongitude() {
			return mLongitude;
		}
		public void setLongitude(Double l) {
			mLongitude = l;
		}
	}

	//utilisé pour les bundle, utilisé dans MainActivity

	//public final static String CHAMPS = "champs"; remplacer par modification
	public final static String IDNOTICE = "id";
	public final static String MATERIAUx = "materiaux";
	public final static String DATE_INAUGURATION = "inauguration";
	public final static String DESCRIPTION = "description";
	//public final static String IDLOCAL = "id_local";
	public final static String PHOTO = "photo";
	
	final static String DEBUG_TAG="Contribution";
	
	//copie-colle de ConstantTagName
	public static final String LONGITUDE = "longitude";
	public static final String LATITUDE = "latitude";
	static final String INAUGURATION = "inauguration";
	public static final String TITRE= "titre";
	public static final String ARTISTE= "artiste";
	static final String CONTRIBUTION= "contribution";
	static final String VALUE = "value";
	static final String IDWIKI = "id";
	public static final String LOCALID = "localid";
	static final String DATECONTRIBUTION = "date";
	static final String HEURECONTRIBUTION = "heure";
	static final String AUTEUR = "auteur";
	public static final String COULEUR = "couleur";
	static final String TYPE = "type";
	static final String STATUT = "statut";
	static final String ENATTENTE = "enattente";
	static final String ACCEPTED = "acceptee";
	static final String CANCELED = "annulee";
	static final String MODIFICATION = "modification"; //modification apport� par l'utilisateur
	static final String COORDONNEES = "coordonnees";
	
	//type de contribution
	static final String AJOUTER = "ajouter";
	static final String REMPLACER = "remplacer";
	static final String SUPPRIMER = "supprimer";
	public static final String PASSWORD = "passwd";
	
	public static final String ETAT = "etat";
	public static final String PETAT = "petat";
	public static final String PMR = "pmr";
	public static final String NOM_SITE = "nomsite";
	public static final String DETAIL_SITE = "detailsite";
	public static final String AUTRE = "autre";
	public static final String NATURE = "nature";
	
	
	
	public static enum type_contrib
	{
		supprimer, ajouter, remplacer, creer, unknow
	}


	public static String lOCALISATION="Localisation";

	
	//Valeur de contribution
	String password;//mdp de l'utilisateur
	type_contrib  type; //type de contribution
	String champModifier; //champ sur lequel on souhaite apporté une modification, ajout ou suppression, table
	String artiste=""; //artiste donné par l'utilisateur
	String titre=""; //titre donné par l'utilisateur
	//String value; //la valeur de cette modification
	int valueType; // type de valeur, chaine de caractéres, fichier, éléments de liste
	Date d; //date+heure é laquelle on a effectuer la contribution
	String auteur;//auteur de la contribution - login wiki
	Boolean auteurConnue;  // indique si auteur connu
	int idNotice; //id de la notice Wiki si existante
	String idLocal;//id pour ttes les données d'une mm oeuvre
	String date;//format dd/mm/yyyy
	String heure;//format hh:mm:ss
	champ_status statut;
	String couleur="";
	Location cLocation;
	String photoPath="";//chemin complet vers la photo
	
	
	//*****************************************************
	String etat;//etat de conservation: bonne état ou dégradé
	String petat;//Précision état de conservation
	String pmr;//personne a mobilité reduite (accessible ou non)
	String nomsite; //nom du site
	String detailsite;//Détails sur le site
	String autre;//autre information
	public String nature;//perenne ou ephemere
	//*******************************************************
	
	public Double latitude=0.0;
	public Double longitude=0.0;
	public String description="";
	public String materiaux="";
	public String dateinauguration="";
	
	public static enum champ_status
	{
		SENT, enattente, EDITING, UNKNOW
	}
	
	
	public  static void createContributionRemplacement(Contribution c, int idNotice, String idlocal, String champs, String value)
	{
		
		c.type= Contribution.type_contrib.remplacer;//type de contribution remplacement
		Date date = new Date();//instancie la date de la contribution
		c.d = date;
		
		c.champModifier = champs;
		setValueFromChamps(c, champs, value);
		//Log.d(DEBUG_TAG+"/creation", "champs = "+c.champModifier+"value= "+c.value);
		if(idNotice != 0)
		{
			c.idNotice = idNotice;//la contribution se rapporte à l'oeuvre id
		}
		c.idLocal=idlocal;
		c.statut= champ_status.UNKNOW;
		
	}
	/**
	 * Attribue la valeur donnée par l'utilisateur en fonction du champ. Appeler dans le cas d'un remplacement, ajout, suppression
	 * @param c
	 * @param champs
	 */
	private static void setValueFromChamps(Contribution c, String champs, String value) {
		// TODO Auto-generated method stub
		switch(champs)
		{
		case Contribution.ARTISTE:
			c.artiste = value;
			break;
		case Contribution.COULEUR:
			c.couleur = value;
			break;
		case Contribution.DATE_INAUGURATION:
			c.dateinauguration = value;
			break;
		case Contribution.MATERIAUx:
			c.materiaux = value;
			break;
		case Contribution.TITRE:
			c.titre = value;
			break;
		case Contribution.DESCRIPTION:
			c.description =value;
			break;
		case Contribution.PHOTO:
			String[] namePhoto = value.split("/");
			if (namePhoto.length != 0)
			{
				String n = namePhoto[namePhoto.length -1];
				c.photoPath = n;
			}
			else
			{
				c.photoPath = value;
			}
			break;
		case Contribution.ETAT:
			c.etat =value;
			break;
		case Contribution.PETAT:
			c.petat =value;
			break;
		case Contribution.DETAIL_SITE:
			c.detailsite =value;
			break;
		case Contribution.NATURE:
			c.nature =value;
			break;
		case Contribution.PMR:
			c.pmr =value;
			break;
		case Contribution.AUTRE:
			c.autre =value;
			break;
		case Contribution.NOM_SITE:
			c.nomsite =value;
			break;
		}
	}
	
	/**
	 * Attribue la valeur donnée par l'utilisateur en fonction du champ.
	 * @param c
	 * @param champs
	 */
	static String getValueFromChamps(Contribution c,String champs) {
		// TODO Auto-generated method stub
		switch(champs)
		{
		case Contribution.ARTISTE:
			return c.artiste;
		case Contribution.COULEUR:
			return c.couleur;
		case Contribution.DATE_INAUGURATION:
			return c.dateinauguration ;
		case Contribution.MATERIAUx:
			return c.materiaux;
		case Contribution.TITRE:
			return c.titre;
		case Contribution.DESCRIPTION:
			return c.description;
		case Contribution.PHOTO:
			return c.photoPath;
		case Contribution.ETAT:
			return c.etat;
		case Contribution.PETAT:
			return c.petat;
		case Contribution.NATURE:
			return c.nature;
		case Contribution.AUTRE:
			return c.autre;
		case Contribution.PMR:
			return c.pmr;
		case Contribution.DETAIL_SITE:
			return c.detailsite;
		case Contribution.NOM_SITE:
			return c.nomsite;
		}
		return "error";
	}
	
	public static void createContribDelete(Contribution c, int idNotice, String idLocal)
	{
		
		c.type = Contribution.type_contrib.supprimer;
		Date date = new Date();//instancie la date de la contribution
		c.d = date;
		if(idNotice != 0)
		{
			c.idNotice = idNotice;//id de la notice qu'on veut supprimer
		}
		c.idLocal = idLocal;
		c.statut= champ_status.UNKNOW;
		//Log.d(DEBUG_TAG+"/creation", "champs = "+c.champModifier+"value= "+c.value);
		
	}
	
	public static void createContributionAjout(Contribution c, int idNotice, String idLocal, String champ, String value)
	{
		
		c.type= Contribution.type_contrib.ajouter;//type de contribution: ajout
		Date date = new Date();//instancie la date de la contribution
		c.d = date;
		if(idNotice != 0)
		{
			c.idNotice = idNotice;//id de la notice qu'on veut supprimer
		}
		c.champModifier=champ;
		setValueFromChamps(c, champ, value);
		//sLog.d(DEBUG_TAG+"/creation", "champs = "+c.champModifier+"value= "+c.value);
		c.idLocal = idLocal;
		c.statut= champ_status.UNKNOW;
		
	}
	
	public static void createContributionRemplacementCoordonnee(Contribution c, int idNotice, String idLocal, String champAModifier, Double lat, Double longi)
	{
		
		c.type= Contribution.type_contrib.remplacer;//type de contribution: ajout
		Date date = new Date();//instancie la date de la contribution
		c.d = date;
		if(idNotice != 0)
		{
			c.idNotice = idNotice;//id de la notice qu'on veut supprimer
		}
		c.champModifier=champAModifier;
		c.latitude=lat;
		Log.d(DEBUG_TAG, "latitude ="+c.latitude);
		c.longitude = longi;
		Log.d(DEBUG_TAG, "longitude ="+c.longitude);
		c.idLocal = idLocal;
		c.statut= champ_status.UNKNOW;
		
	}
	
	public static void createContributionAjoutCoordonnee(Contribution c, int idNotice, String idLocal, String champAModifier, Double lat, Double longi)
	{
		
		c.type= Contribution.type_contrib.ajouter;//type de contribution: ajout
		Date date = new Date();//instancie la date de la contribution
		c.d = date;
		if(idNotice != 0)
		{
			c.idNotice = idNotice;//id de la notice qu'on veut supprimer
		}
		c.champModifier=champAModifier;
		c.latitude=lat;
		Log.d(DEBUG_TAG, "latitude ="+c.latitude);
		c.longitude = longi;
		Log.d(DEBUG_TAG, "longitude ="+c.longitude);
		c.idLocal = idLocal;
		c.statut= champ_status.UNKNOW;
		
	}
	
	public String getHeure()
	{
		return heure;
	}
	
	public String getDate()
	{
		return date;
	}
	
	/**
	 * utiliser pour ContribXml.recupListContrib pour r�cup�rer le type de contribution 
	 * @param attributeValue
	 * @return
	 */
	public static type_contrib getTypeContribFromString(String attributeValue) {
		// TODO Auto-generated method stub
		switch (attributeValue)
		{
		case "creer":
				return type_contrib.creer;
		case "remplacer":
				return type_contrib.remplacer;
		
		case "ajouter":
			return type_contrib.ajouter;
		
		case "supprimer":
			return type_contrib.supprimer;		
		default:
			return type_contrib.unknow;	
		}
	}

	/**
	 * utiliser dans ContribXml.recupListActivity
	 * @param contrib_status, le status r�cup�r� � partir du XMl
	 * @return le status de la contribution, du type champ_status
	 */
	public static champ_status getStatusContribFromString(String contrib_status) {
		// TODO Auto-generated method stub
		if (contrib_status == null)
		{
			return champ_status.UNKNOW;
		}
		switch (contrib_status)
		{
		case "enattente":
			return champ_status.enattente;
		case "SENT":
				return champ_status.SENT;
		case "SAVE":
			return champ_status.enattente;
			
		default:
			 return champ_status.EDITING;
		}
	}
	
	
	/**
	 * methode pour convertir l'image en string
	 * @return l'image sous forme de chaine
	 */
	public String photoToString() {
		if( this.photoPath == "" ) {
			return "";
		}
		
		File graffity_file = new File(pathToFile+this.photoPath);
		InputStream graffity_is;
		try {
			graffity_is = new FileInputStream(graffity_file);
		} catch (FileNotFoundException e) {
			
			Log.i(DEBUG_TAG, "Can't read " + pathToFile+this.photoPath);
			e.printStackTrace();
			return "";
		}
		ByteArrayOutputStream graffity_os = new ByteArrayOutputStream(1000);
		Bitmap graffity_bitmap = BitmapFactory.decodeStream(graffity_is, null, null);
		graffity_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, graffity_os);  
		byte[] graffity_bytearray = graffity_os.toByteArray();
		String graffity_string = Base64.encodeToString(graffity_bytearray, Base64.DEFAULT);
		return graffity_string;
	}
	



 }