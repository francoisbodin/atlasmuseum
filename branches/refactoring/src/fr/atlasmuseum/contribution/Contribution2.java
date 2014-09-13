package fr.atlasmuseum.contribution;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

import android.app.Activity;
import android.util.Log;
import fr.atlasmuseum.R;
import fr.atlasmuseum.search.JsonRawData;
import fr.atlasmuseum.search.SearchActivity;

public class Contribution2 implements Serializable {

	private static final long serialVersionUID = 7388740279075848884L;

	private static final String DEBUG_TAG = "AtlasMuseum/Contribution2";
	
	int mNoticeId;
	String mLocalId;
	String mLogin;
	String mPassword;
	HashMap<String,ContributionProperty> mProperties;
	
	public static enum PropertyType
	{
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

	public Contribution2(Activity context) {
		mNoticeId = 0;
		mLocalId = "";
		mLogin = "";
		mPassword = "";
		mProperties = new HashMap<String, ContributionProperty>();
		
		mProperties.put( Contribution.URL, new ContributionProperty(
				/* dbField */ Contribution.URL,
				/* jsonField */ "url",
				/* title */ "",
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ 0,
				/* showViewToHide */ 0) );
		
		mProperties.put( Contribution.CREDIT_PHOTO, new ContributionProperty(
				/* dbField */ Contribution.CREDIT_PHOTO,
				/* jsonField */ "creditphoto",
				/* title */ "",
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.credit_photo,
				/* showViewToHide */ 0) );
		
		mProperties.put( Contribution.PHOTO, new ContributionProperty(
				/* dbField */ Contribution.PHOTO,
				/* jsonField */ "image_principale",
				/* title */ "",
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ 0,
				/* showViewToHide */ 0) );
		
		mProperties.put(Contribution.TITRE, new ContributionProperty(
				/* dbField */ Contribution.TITRE,
				/* jsonField */ "titre",
				/* title */ context.getResources().getString(R.string.Titre),
				/* value */ "",
				/* defaultValue */ "Pas de titre", // TODO : resourcify
				/* info */ "",
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.notice_titre,
				/* showViewToHide */ 0) );
		
		mProperties.put(Contribution.ARTISTE, new ContributionProperty(
				/* dbField */ Contribution.ARTISTE,
				/* jsonField */ "artiste",
				/* title */ context.getResources().getString(R.string.Artiste),
				/* value */ "",
				/* defaultValue */ "Unknown", // TODO : resourcify
				/* info */ "", // TODO: ajouter les infos
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.notice_artiste,
				/* showViewToHide */ 0) );

		mProperties.put(Contribution.COULEUR, new ContributionProperty(
				/* dbField */ Contribution.COULEUR,
				/* jsonField */ "couleur",
				/* title */ context.getResources().getString(R.string.Couleurs),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.check,
				/* choices */ JsonRawData.listeCouleurs,
				/* showViewText */ R.id.oeuvre_couleur_value,
				/* showViewToHide */ R.id.relativ_oeuvre_couleur) );
		
		mProperties.put( Contribution.DATE_INAUGURATION, new ContributionProperty(
				/* dbField */ Contribution.DATE_INAUGURATION,
				/* jsonField */ "inauguration",
				/* title */ context.getResources().getString(R.string.Date),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ context.getResources().getString(R.string.contrib_date_infos),
				/* type */ ContributionProperty.ContribType.date,
				/* choices */ null,
				/* showViewText */ R.id.notice_annee,
				/* showViewToHide */ R.id.notice_annee) );
		
		mProperties.put(Contribution.DESCRIPTION, new ContributionProperty(
				/* dbField */ Contribution.DESCRIPTION,
				/* jsonField */ "description",
				/* title */ context.getResources().getString(R.string.Description),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.oeuvre_description_value,
				/* showViewToHide */ R.id.relativ_oeuvre_description) );
		
		mProperties.put( Contribution.MATERIAUX, new ContributionProperty(
				/* dbField */ Contribution.MATERIAUX,
				/* jsonField */ "materiaux",
				/* title */ context.getResources().getString(R.string.Materiaux),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.check,
				/* choices */ JsonRawData.listeMateriaux,
				/* showViewText */ R.id.oeuvre_materiauw_value,
				/* showViewToHide */ R.id.relativ_oeuvre_materiaux) );
		
		mProperties.put( Contribution.NOM_SITE, new ContributionProperty(
				/* dbField */ Contribution.NOM_SITE,
				/* jsonField */ "Sitenom",
				/* title */ context.getResources().getString(R.string.Nom_du_site),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "", // TODO: ajouter les infos
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.site_nomsite_value,
				/* showViewToHide */ R.id.relativ_site_nomsite) );
		
		mProperties.put( Contribution.DETAIL_SITE, new ContributionProperty(
				/* dbField */ Contribution.DETAIL_SITE,
				/* jsonField */ "Sitedetails",
				/* title */ context.getResources().getString(R.string.Detail_site),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "", // TODO: ajouter les infos
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.site_detailsite_value,
				/* showViewToHide */ R.id.relativ_site_detailsite) );
		
		mProperties.put( Contribution.NATURE, new ContributionProperty(
				/* dbField */ Contribution.NATURE,
				/* jsonField */ "nature",
				/* title */ context.getResources().getString(R.string.Nature),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.radio,
				/* choices */ JsonRawData.listeNatures,
				/* showViewText */ R.id.oeuvre_nature_value,
				/* showViewToHide */ R.id.relativ_oeuvre_nature) );
		
		mProperties.put( Contribution.LATITUDE, new ContributionProperty(
				/* dbField */ Contribution.LATITUDE,
				/* jsonField */ "latitude",
				/* title */ "Latitude",
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ 0,
				/* showViewToHide */ 0) );
		
		mProperties.put( Contribution.LONGITUDE, new ContributionProperty(
				/* dbField */ Contribution.LONGITUDE,
				/* jsonField */ "longitude",
				/* title */ "Longitude",
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ 0,
				/* showViewToHide */ 0) );
		
		mProperties.put( Contribution.AUTRE, new ContributionProperty(
				/* dbField */ Contribution.AUTRE,
				/* jsonField */ "autre",
				/* title */ context.getResources().getString(R.string.Autres_infos),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "", // TODO: ajouter les infos
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ 0,
				/* showViewToHide */ 0) );
		
		mProperties.put( Contribution.ETAT, new ContributionProperty(
				/* dbField */ Contribution.ETAT,
				/* jsonField */ "precision_etat_conservation",
				/* title */ context.getResources().getString(R.string.Etat_de_conservation),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.radio,
				/* choices */ JsonRawData.listePrecision_etat_conservation,
				/* showViewText */ 0,
				/* showViewToHide */ 0) );
		
		mProperties.put( Contribution.PETAT, new ContributionProperty(
				/* dbField */ Contribution.PETAT,
				/* jsonField */ "autre_precision_etat_conservation",
				/* title */ context.getResources().getString(R.string.Precision_sur_l_etat_de_conservation),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.check,
				/* choices */ JsonRawData.listeAutre_precision_etat_conservation,
				/* showViewText */ 0,
				/* showViewToHide */ 0) );
		
		mProperties.put( Contribution.PMR, new ContributionProperty(
				/* dbField */ Contribution.PMR,
				/* jsonField */ "",
				/* title */ context.getResources().getString(R.string.accessibilite_pmr),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "", // TODO: ajouter les infos
				/* type */ ContributionProperty.ContribType.radio,
				/* choices */ JsonRawData.listePmr,
				/* showViewText */ R.id.site_pmr_value,
				/* showViewToHide */ R.id.relativ_site_pmr) );

		mProperties.put( Contribution.MOT_CLE, new ContributionProperty(
				/* dbField */ Contribution.MOT_CLE,
				/* jsonField */ "mot_cle",
				/* title */ context.getResources().getString(R.string.Mots_cles),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.oeuvre_mots_cles_value,
				/* showViewToHide */ R.id.relativ_oeuvre_mots_cles) );

		mProperties.put( Contribution.CONTEXTE_PRODUCTION, new ContributionProperty(
				/* dbField */ Contribution.CONTEXTE_PRODUCTION,
				/* jsonField */ "contexte_production",
				/* title */ context.getResources().getString(R.string.Contexte),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.oeuvre_contexte_value,
				/* showViewToHide */ R.id.relativ_oeuvre_contexte) );

		mProperties.put( Contribution.VILLE, new ContributionProperty(
				/* dbField */ Contribution.VILLE,
				/* jsonField */ "Siteville",
				/* title */ context.getResources().getString(R.string.ville),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.site_ville_value,
				/* showViewToHide */ R.id.relativ_site_ville) );

		mProperties.put( Contribution.REGION, new ContributionProperty(
				/* dbField */ Contribution.REGION,
				/* jsonField */ "Siteregion",
				/* title */ context.getResources().getString(R.string.Region),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.site_region_value,
				/* showViewToHide */ R.id.relativ_site_region) );

		mProperties.put( Contribution.PAYS, new ContributionProperty(
				/* dbField */ Contribution.PAYS,
				/* jsonField */ "Sitepays",
				/* title */ context.getResources().getString(R.string.Pays),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.site_pays_value,
				/* showViewToHide */ R.id.relativ_site_pays) );

		mProperties.put( Contribution.MOUVEMENT, new ContributionProperty(
				/* dbField */ Contribution.MOUVEMENT,
				/* jsonField */ "mouvement_artistes",
				/* title */ context.getResources().getString(R.string.Mouvement),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.artiste_mouvement_value,
				/* showViewToHide */ R.id.relativ_artiste_mouvement) );

	}

	public void updateFromDb(int index) {
		mNoticeId = Integer.parseInt(SearchActivity.extractDataFromDb(index,Contribution.IDNOTICE));

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
}
