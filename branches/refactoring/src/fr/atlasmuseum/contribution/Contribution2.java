package fr.atlasmuseum.contribution;

import java.io.Serializable;
import java.util.HashMap;

import android.app.Activity;
import android.util.Log;
import fr.atlasmuseum.R;
import fr.atlasmuseum.search.JsonRawData;
import fr.atlasmuseum.search.SearchActivity;

public class Contribution2 implements Serializable {

	private static final long serialVersionUID = -8784868336205052452L;

	private static final String DEBUG_TAG = "AtlasMuseum/Contribution2";
	
	int mId;
	HashMap<String, ContributionProperty> mProperties;
	
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
		mId = -1;

		mProperties = new HashMap<String, ContributionProperty>();
		
		mProperties.put( Contribution.PHOTO, new ContributionProperty(
				/* dbField */ Contribution.PHOTO,
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
				/* title */ context.getResources().getString(R.string.Titre),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ R.id.notice_titre,
				/* showViewToHide */ 0) );
		
		mProperties.put(Contribution.ARTISTE, new ContributionProperty(
				/* dbField */ Contribution.ARTISTE,
				/* title */ context.getResources().getString(R.string.Artiste),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "", // TODO: ajouter les infos
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ 0,
				/* showViewToHide */ 0) );

		mProperties.put(Contribution.COULEUR, new ContributionProperty(
				/* dbField */ Contribution.COULEUR,
				/* title */ context.getResources().getString(R.string.Couleurs),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.check,
				/* choices */ JsonRawData.listeCouleurs,
				/* showViewText */ 0,
				/* showViewToHide */ 0) );
		
		mProperties.put( Contribution.DATE_INAUGURATION, new ContributionProperty(
				/* dbField */ Contribution.DATE_INAUGURATION,
				/* title */ context.getResources().getString(R.string.Date),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ context.getResources().getString(R.string.contrib_date_infos),
				/* type */ ContributionProperty.ContribType.date,
				/* choices */ null,
				/* showViewText */ 0,
				/* showViewToHide */ 0) );
		
		mProperties.put(Contribution.DESCRIPTION, new ContributionProperty(
				/* dbField */ Contribution.DESCRIPTION,
				/* title */ context.getResources().getString(R.string.Description),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ 0,
				/* showViewToHide */ 0) );
		
		mProperties.put( Contribution.MATERIAUX, new ContributionProperty(
				/* dbField */ Contribution.MATERIAUX,
				/* title */ context.getResources().getString(R.string.Materiaux),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.check,
				/* choices */ JsonRawData.listeMateriaux,
				/* showViewText */ 0,
				/* showViewToHide */ 0) );
		
		mProperties.put( Contribution.NOM_SITE, new ContributionProperty(
				/* dbField */ Contribution.NOM_SITE,
				/* title */ context.getResources().getString(R.string.Nom_du_site),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "", // TODO: ajouter les infos
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ 0,
				/* showViewToHide */ 0) );
		
		mProperties.put( Contribution.DETAIL_SITE, new ContributionProperty(
				/* dbField */ Contribution.DETAIL_SITE,
				/* title */ context.getResources().getString(R.string.Detail_site),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "", // TODO: ajouter les infos
				/* type */ ContributionProperty.ContribType.text,
				/* choices */ null,
				/* showViewText */ 0,
				/* showViewToHide */ 0) );
		
		mProperties.put( Contribution.NATURE, new ContributionProperty(
				/* dbField */ Contribution.NATURE,
				/* title */ context.getResources().getString(R.string.Nature),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "",
				/* type */ ContributionProperty.ContribType.radio,
				/* choices */ JsonRawData.listeNatures,
				/* showViewText */ 0,
				/* showViewToHide */ 0) );
		
		mProperties.put( Contribution.LATITUDE, new ContributionProperty(
				/* dbField */ Contribution.LATITUDE,
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
				/* title */ context.getResources().getString(R.string.accessibilite_pmr),
				/* value */ "",
				/* defaultValue */ "",
				/* info */ "", // TODO: ajouter les infos
				/* type */ ContributionProperty.ContribType.radio,
				/* choices */ JsonRawData.listePmr,
				/* showViewText */ 0,
				/* showViewToHide */ 0) );

	}

	public void updateFromDb(int index) {
		mId = Integer.parseInt(SearchActivity.extractDataFromDb(index,"id"));

		mProperties = new HashMap<String, ContributionProperty>();
		
		mProperties.get("photo").resetValue(SearchActivity.extractDataFromDb(index,"image_principale"));
		mProperties.get(Contribution.TITRE).resetValue(SearchActivity.extractDataFromDb(index,"titre"));
		mProperties.get(Contribution.ARTISTE).resetValue(SearchActivity.extractDataFromDb(index,"artiste"));
		mProperties.get(Contribution.COULEUR).resetValue(SearchActivity.extractDataFromDb(index,"couleur"));
		mProperties.get(Contribution.DATE_INAUGURATION).resetValue(SearchActivity.extractDataFromDb(index,"inauguration"));
		mProperties.get(Contribution.DESCRIPTION).resetValue(SearchActivity.extractDataFromDb(index,"description"));
		mProperties.get(Contribution.MATERIAUX).resetValue(SearchActivity.extractDataFromDb(index,"materiaux"));
		mProperties.get(Contribution.NOM_SITE).resetValue(SearchActivity.extractDataFromDb(index,"Sitenom"));
		mProperties.get(Contribution.DETAIL_SITE).resetValue(SearchActivity.extractDataFromDb(index,"Sitedetails"));
		mProperties.get(Contribution.NATURE).resetValue(SearchActivity.extractDataFromDb(index,"nature"));
		mProperties.get(Contribution.LATITUDE).resetValue(SearchActivity.extractDataFromDb(index,"latitude"));
		mProperties.get(Contribution.LONGITUDE).resetValue(SearchActivity.extractDataFromDb(index,"longitude"));
		mProperties.get(Contribution.AUTRE).resetValue(SearchActivity.extractDataFromDb(index,"autre"));
		mProperties.get(Contribution.ETAT).resetValue(SearchActivity.extractDataFromDb(index,"precision_etat_conservation"));
		mProperties.get(Contribution.PETAT).resetValue(SearchActivity.extractDataFromDb(index,"autre_precision_etat_conservation"));
		mProperties.get(Contribution.PMR).resetValue(SearchActivity.extractDataFromDb(index,"Sitepmr"));
	}

	public ContributionProperty getProperty(String property) {
		if( mProperties.containsKey(property) ) {
			return mProperties.get(property);
		}
		else {
			return null;
		}
	}
	
	public void setProperty( String dbField, ContributionProperty prop ) {
		mProperties.put(dbField,  prop);
	}
	
	public void dumpDebug() {
		//Log.d(DEBUG_TAG, "Dump contribution: id = " + mId + " - Titre = " + mProperties.get("titre").getValue());
		Log.d(DEBUG_TAG, "Dump contribution");
	}
}
