package fr.atlasmuseum.main;

import fr.atlasmuseum.AtlasmuseumActivity;
import fr.atlasmuseum.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * this class is define to manage error
 * 
 *
 */
public class AtlasError extends Activity {
	public AtlasmuseumActivity m;
	final String IMG_NOT_SEND = this.getString(R.string.id_non_valide);
	
//AtlasError.showErrorDialog(SignUPActivity.this, "2.1", COMPTE_EXISTANT);
	
	@SuppressWarnings("deprecation")
	public static void showErrorDialog( final Context mcontext, String error, String ch)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(mcontext).create();
		
		String[] chError= errorCoorespondance(mcontext, error, ch);
		
		// Le titre
		alertDialog.setTitle(chError[0]);//titre de la boite de dialogue
		 
		// Le message
		alertDialog.setMessage(chError[1]);
		 
		// L'ic�ne
		alertDialog.setIcon(android.R.drawable.btn_star);
		 
		// Ajout du bouton "OK"
		alertDialog.setButton(mcontext.getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
		    public void onClick(final DialogInterface arg0, final int arg1) {
		        // Le code à exécuter apres le clique sur le bouton
		        //Toast.makeText(mcontext, "Tu as cliqué sur le bouton 'OK'",Toast.LENGTH_SHORT).show();
		    }

		});
		 
		// Affichage
		alertDialog.show();
	}
	
	public static void showErrorToast( final Context mcontext, int error, String ch)
	{
		Toast.makeText(mcontext, "ch",Toast.LENGTH_SHORT).show();
	}
	

	/**
	 * m�thode pour la gestion des erreurs
	 * @param mContext le context de l'activit� dans laquelle on traite l'erreur
	 * @param i le code d'erreur
	 * @param ch chaine d'erreur spécifique
	 * @return tableau de 2 chaines: le premier contenant le titre de l'erreur, et l'autre la chaine erreur
	 */
	public static String[] errorCoorespondance(Context mContext,String i, String ch)
	{
		String[] res = new String[2];
		switch (i)
		{
		  case "0.1": //uploader - echec de la contribution
			  res[0]=  mContext.getResources().getString(R.string.ECHEC_ENVOI_OEUVRE);
			  res[1]= mContext.getResources().getString(R.string.upload_failed);
		    break;
		    
		  case "1.1": //authentification - verifier vos identifiants et mdp - connexionActivity
			  res[0]=  mContext.getResources().getString(R.string.ECHEC_AUTHENTIFICATION);
			  res[1]= mContext.getResources().getString(R.string.PLEASE_CHECK_ID);
		    break;
		    
		  case "2.1"://inscription - compte deja existant
			  res[0]=  mContext.getResources().getString(R.string.ECHEC_INSCRIPTION);
			  res[1]= mContext.getResources().getString(R.string.COMPTE_EXISTANT);
			  break;
			  
		  case "2.2"://inscription - champs mdp et confirmation ne concordent pas
			  res[0]=  mContext.getResources().getString(R.string.ECHEC_CREATION_COMPTE);
			  res[1]= mContext.getResources().getString(R.string.CHAMPS_MDP_DIFFERENT);
			  break;
			  
		  case "2.3"://inscription - champs mdp et confirmation ne concordent pas
			  res[0]=  mContext.getResources().getString(R.string.ECHEC_CREATION_COMPTE);
			  res[1]= mContext.getResources().getString(R.string.CHAMPS_VIDES);
			  break;
			  
		  case "2.4"://inscription - Echec lors de la cr�ation du compte + flag
			  res[0]=  mContext.getResources().getString(R.string.ECHEC_CREATION_COMPTE);
			  res[1]= mContext.getResources().getString(R.string.ECHEC_REQUETE);;
			  break;
			  
		  case "3.1"://AutoCheckerAsync - Authentification �chou�e. V�rifiez que vous �tes bien connect� � internet.
			  res[0]=  mContext.getResources().getString(R.string.ECHEC_AUTHENTIFICATION);
			  res[1]= mContext.getResources().getString(R.string.ECHEC_AUTHENT_CHECK_INTERNET);;
			  break;
			  
		  case "4.1": //mapActivity - entr�e non trouv� (to do)

			  res[0]=  mContext.getResources().getString(R.string.ECHEC_MAP);
			  res[1]= mContext.getResources().getString(R.string.MAP_ENTREE_NOT_FOUND);
			  break;
			  
		  case "4.2": //MapAcitivity - oncreate - "Issue with double conversion" - prob de conversion type double pour longitude/latitude

			  res[0]=  mContext.getResources().getString(R.string.ECHEC_MAP);
			  res[1]= mContext.getResources().getString(R.string.ECHEC_MAP_DOUBLE_CONVERSION);
			  
			  break;
			  
		  case "4.3": //MapAcitivity - initilizeMap() - Sorry! unable to create maps
			  res[0]=  mContext.getResources().getString(R.string.ECHEC_MAP);
			  res[1]= mContext.getResources().getString(R.string.ECHEC_MAP_CREATION);
			  break;
			  
		  case "5.1": //ObjectFragment - showExistingPhoto() - L'image de cette notice n'a pu �tre visualis�
			  res[0]=  mContext.getResources().getString(R.string.ECHEC_NOTICE);
			  res[1]= mContext.getResources().getString(R.string.ECHEC_SHOW_NOTICE);
			  break;
			  
		  case "5.2": //ObjectFragment - showPhoto() - L'image de cette notice n'a pu �tre visualis�
			  res[0]=  mContext.getResources().getString(R.string.ECHEC_NOTICE);
			  res[1]= mContext.getResources().getString(R.string.ECHEC_SHOW_NOTICE);
			  break;
			  
		  case "5.3": //ObjectFragment - showPhoto() - "Cette notice n'a pas d'image"
			  res[0]=  mContext.getResources().getString(R.string.ECHEC_NOTICE);
			  res[1]= mContext.getResources().getString(R.string.NOTICE_NO_IMG);
			  break;
			  
		  case "5.4": //ObjectFragment - showPhoto() - "L'image de cette notice n'a pu �tre charg�"
			  res[0]=  mContext.getResources().getString(R.string.ECHEC_NOTICE);
			  res[1]= mContext.getResources().getString(R.string.NOTICE_IMG_LOADING_FAILED);
			  break;
			  
		  case "6.1": //MainActivity - showMap() - "impossible de charger la bdd"
			  res[0]=  mContext.getResources().getString(R.string.ECHEC_LOADING);
			  res[1]= mContext.getResources().getString(R.string.DB_LOADING_FAILED);
			  break;
		  case "7.1": //MaincontribActivity -  "pas de connexion internet"
			  res[0]=  mContext.getResources().getString(R.string.prob_connexion);//"Problème de connexion";
			  res[1]=  mContext.getResources().getString(R.string.pas_de_connexion_internet);//"Pas de connexion internet détectée.";
			  break;
		  case "7.2": //MaincontribActivity -  "pas de connexion internet"
			  res[0]=  mContext.getResources().getString(R.string.echec_send_contrib);//"Ech�c envoi";
			  res[1]= mContext.getResources().getString(R.string.no_contrib_saved);//"Vous n'avez pas de contribution sauvegardée.";
			  break;
		  case "7.3": //MaincontribActivity -  "compte utilisateur requis pour pouvoir envoyer"
			  res[0]=   mContext.getResources().getString(R.string.echec_send_contrib);
			  res[1]=  mContext.getResources().getString(R.string.connexion_requis);//"Vous devez vous connecter � votre compte pour pouvoir envoyer vos contributions.";
			  break;
		  case "8.1": //SearchAutourList -  "imppossible de r�cup�rer votre position pour le moment."
			  res[0]=  mContext.getResources().getString(R.string.echec);//"Echec";
			  res[1]= mContext.getResources().getString(R.string.no_user_position);//"impossible de r�cup�rer votre position pour le moment. Veuillez r�essayer ult�rieurement";
			  break;
			  
		  case "9.1": //ConnexionAsync -WebServiceNoId : webserviceid vide
			  res[0]= mContext.getResources().getString(R.string.echec_connexion);// "Echec connexion";
			  res[1]= mContext.getResources().getString(R.string.erreur_interne)+" "+"9.1" ;//"webserviceid vide";
			  break;
		  case "9.2": //ConnexionAsync - WebServiceWrongId : webserviceid incorrect
			  res[0]=  mContext.getResources().getString(R.string.echec_connexion);// "Echec connexion";
			  res[1]=  mContext.getResources().getString(R.string.erreur_interne)+" "+"9.2" ;//"webserviceid incorrect";
			  break;
		  case "9.3": //ConnexionAsync - WebServiceNoPass : webservicepass vide
			  res[0]=  mContext.getResources().getString(R.string.echec_connexion);// "Echec connexion";
			  res[1]=  mContext.getResources().getString(R.string.erreur_interne)+" "+"9.3" ;//"webservicepass vide";
			  break;
		  case "9.4": //ConnexionAsync - WebServiceWrongPass : webservicepass incorrect
			  res[0]=  mContext.getResources().getString(R.string.echec_connexion);// "Echec connexion";
			  res[1]=  mContext.getResources().getString(R.string.erreur_interne)+" "+"9.4" ;//"webservicepass incorrect";
			  break;
		  case "9.5": //ConnexionAsync - NoName : username vide
			  res[0]=  mContext.getResources().getString(R.string.echec_connexion);// "Echec connexion";
			  res[1]= mContext.getResources().getString(R.string.login_vide);//"Le champ login ne doit pas être vide";
			  break;
		  case "9.6": //ConnexionAsync - Illegal : username incorrect
			  res[0]=  mContext.getResources().getString(R.string.echec_connexion);// "Echec connexion";
			  res[1]= mContext.getResources().getString(R.string.verif_login);//"Veuillez vérifier votre login.";
			  break;
		  case "9.7": //ConnexionAsync - NotExists : username inexistant
			  res[0]=  mContext.getResources().getString(R.string.echec_connexion);// "Echec connexion";
			  res[1]= mContext.getResources().getString(R.string.verif_login2);//"Vérifier que vous avez bien entrer votre login";
			  break;
		  case "9.8": //ConnexionAsync - EmptyPass : mot de passe vide
			  res[0]= mContext.getResources().getString(R.string.echec_connexion);// "Echec connexion";
			  res[1]= mContext.getResources().getString(R.string.verif_mdp);//"Veuillez entrer votre mot de passe";
			  break;
		  case "9.9": //ConnexionAsync - WrongPass : mot de passe incorrect
			  res[0]= mContext.getResources().getString(R.string.echec_connexion);// "Echec connexion";
			  res[1]= mContext.getResources().getString(R.string.mdp_incorrect);//"Le mot de passe que vous avez fourni est incorrect.";
			  break;  
		  default://error
			  res[0]=  "ERROR";
			  res[1]= "unknow";
			  break;            
		}
		
		return res;
			
	}
	
	
	/**
	 * Methode TEMPORAIRE:Utiliser pour afficher les messages de reussite
	 * @param mcontext
	 * @param ch
	 */
	public static void showDialog( final Context mcontext, String ch)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(mcontext).create();
		 
		// Le titre
		alertDialog.setTitle(mcontext.getResources().getString(R.string.app_name));//titre l'application
		 
		// Le message
		alertDialog.setMessage(ch);
		 
		// L'ic�ne
		alertDialog.setIcon(android.R.drawable.btn_star);
		 
		// Ajout du bouton "OK"
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		    public void onClick(final DialogInterface arg0, final int arg1) {
		        // Le code � ex�cuter apr�s le clique sur le bouton
		        //Toast.makeText(mcontext, "Tu as cliqu� sur le bouton 'OK'",Toast.LENGTH_SHORT).show();
		    }

		});
		 
		// Affichage
		alertDialog.show();
	}
	
}
