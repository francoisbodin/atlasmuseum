package fr.atlasmuseum.contribution;



import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;














import fr.atlasmuseum.compte.Authentification;
import fr.atlasmuseum.main.MainActivity;
import android.content.Context;
import android.util.Log;
//http://cynober.developpez.com/tutoriel/java/xml/jdom/


/**
 * classe qui gere la sauvegarde et cr�ation du xml pour la contribution
 */
public class ContribXmlEnCours
{
	
	//utilis� dans les balises xml
	//public final static String CONTRIBUTION = "contribution";
	//public final static String CHAMP = "CHAMP";
	//public final static String VALUE = "value";
	//public final static String ID = "ID";
	
   final static String DEBUG_TAG = "contribXml/" ;
   final static String nomFichier = "contribproject2.txt";//nom de fichier xml pour sauvegarder les contribs
 
   Element racineEnvoi ;
   Document docEnvoi;
   
   
   Contribution.Location location;
   
   /**
    * 
    */
private String url_sendAPhoto ="http://atlasmuseum.irisa.fr/scripts/storeContribImage.php";
private String url_sendXML ="http://atlasmuseum.irisa.fr/scripts/receiveContributionFile.php";

   
   
	public ContribXmlEnCours(List<Contribution> list)
	{
		racineEnvoi = new Element("xml");
		docEnvoi= new Document(racineEnvoi);

		for(int i=0;i<list.size();i++)
		{
			addContributionToXml(list.get(i));
		}
		
	}
	
	
	
	
	/**
	 * m�thode pour �crire une contribution dans le XML
	 * @param c
	 */
	public void addContributionToXml(Contribution c)
	{
      Element cv = new Element(Contribution.CONTRIBUTION);
      racineEnvoi.addContent(cv);
      
      Attribute classe = new Attribute(Contribution.TYPE,c.type.toString());
      cv.setAttribute(classe);
      
      Attribute classe2 = new Attribute(Contribution.STATUT,c.statut.toString());
      cv.setAttribute(classe2);
      
      if(c.idNotice != 0)
      {
    	  Element nom5 = new Element(Contribution.IDNOTICE);
    	  Attribute classeNotice = new Attribute(Contribution.VALUE,String.valueOf(c.idNotice));
    	  nom5.setAttribute(classeNotice);
          cv.addContent(nom5); 
      }
      if(!c.auteur.equals(""))
      {
    	  Element nom65 = new Element(Contribution.AUTEUR);
    	  Attribute classeNotice2 = new Attribute(Contribution.VALUE,String.valueOf(c.auteur));
    	  nom65.setAttribute(classeNotice2);
          cv.addContent(nom65);
          
          Element nom651 = new Element(Contribution.PASSWORD);
    	  Attribute classeNotice21 = new Attribute(Contribution.VALUE,String.valueOf(c.password));
    	  nom651.setAttribute(classeNotice21);
          cv.addContent(nom651); 
      }
      
      
      
      if(c.latitude != null && c.latitude != 0.0)
      {
    	  Element locale = new Element(Contribution.LATITUDE);
          Attribute classelocalide = new Attribute(Contribution.VALUE,String.valueOf(c.latitude));
         
          locale.setAttribute(classelocalide);
          cv.addContent(locale);
          
          Element localed = new Element(Contribution.LONGITUDE);
          Attribute classelocalided = new Attribute(Contribution.VALUE,String.valueOf(c.longitude));
       
          localed.setAttribute(classelocalided);
          cv.addContent(localed); 
      }
      
      Element local = new Element(Contribution.LOCALID);
      Attribute classelocalid = new Attribute(Contribution.VALUE,String.valueOf(c.idLocal));
      Log.d(DEBUG_TAG, "value localId ="+c.idLocal);
      local.setAttribute(classelocalid);
      cv.addContent(local); 
      
      if(c.champModifier != null) //si cest une modification , suppression, ajout
      {
    	  Element modif = new Element(Contribution.MODIFICATION);
    	  Attribute classemodif = new Attribute(Contribution.VALUE,String.valueOf(c.champModifier));
    	  modif.setAttribute(classemodif);
          cv.addContent(modif);

      }
	  if(c.photoPath != null && !c.photoPath.equals(""))
	  {
		  Log.d(DEBUG_TAG+"/photo", "value photo ="+c.photoPath);
			 String[] namePhoto = c.photoPath.split("/");
			if (namePhoto.length != 0)
			{
				String n = namePhoto[namePhoto.length -1];
				c.photoPath = n;
			}
		
		  Element localphoto1 = new Element(Contribution.PHOTO);
          Attribute valueModif1 = new Attribute(Contribution.VALUE,c.photoPath);
          localphoto1.setAttribute(valueModif1);
          cv.addContent(localphoto1);  
	  }
	  if(c.couleur != null)
	  {
		  if (!c.couleur.equals(""))
		  {
			  Element localphoto2 = new Element(Contribution.COULEUR);
              Attribute valueModif52 = new Attribute(Contribution.VALUE,c.couleur);
              localphoto2.setAttribute(valueModif52);
              cv.addContent(localphoto2); 
		  }
		  
	  }
	  if(!c.artiste.equals(""))
	  {
		  Element localphoto3 = new Element(Contribution.ARTISTE);
          Attribute valueModif53 = new Attribute(Contribution.VALUE,c.artiste);
          localphoto3.setAttribute(valueModif53);
          cv.addContent(localphoto3); 
	  }
	  if( !c.titre.equals(""))
	  {
		  Element localphoto4 = new Element(Contribution.TITRE);
          Attribute valueModif54 = new Attribute(Contribution.VALUE,c.titre);
          localphoto4.setAttribute(valueModif54);
          cv.addContent(localphoto4); 
	  }
	  if( !c.description.equals(""))
	  {
		  Element localphoto5 = new Element(Contribution.DESCRIPTION);
          Attribute valueModif55 = new Attribute(Contribution.VALUE,c.description);
          localphoto5.setAttribute(valueModif55);
          cv.addContent(localphoto5); 
	  }
	  if(!c.materiaux.equals(""))
	  {
		  Element localphoto6 = new Element(Contribution.MATERIAUX);
          Attribute valueModif56 = new Attribute(Contribution.VALUE,c.materiaux);
          localphoto6.setAttribute(valueModif56);
          cv.addContent(localphoto6); 
	  }
	  if( !c.dateinauguration.equals(""))
	  {
		  Element localphoto7 = new Element(Contribution.DATE_INAUGURATION);
          Attribute valueModif57 = new Attribute(Contribution.VALUE,c.dateinauguration);
          localphoto7.setAttribute(valueModif57);
          cv.addContent(localphoto7); 
	  }
	  
	  if(c.etat != null)
	  {
		  if (!c.etat.equals(""))
		  {
			  Element localphoto2 = new Element(Contribution.ETAT);
              Attribute valueModif52 = new Attribute(Contribution.VALUE,c.etat);
              localphoto2.setAttribute(valueModif52);
              cv.addContent(localphoto2); 
		  }
		  
	  }
	  if(c.petat != null)
	  {
		  if (!c.petat.equals(""))
		  {
			  Element localphoto2 = new Element(Contribution.PETAT);
              Attribute valueModif52 = new Attribute(Contribution.VALUE,c.petat);
              localphoto2.setAttribute(valueModif52);
              cv.addContent(localphoto2); 
		  }
		  
	  }
	  if(c.nature != null)
	  {
		  if (!c.nature.equals(""))
		  {
			  Element localphoto2 = new Element(Contribution.NATURE);
              Attribute valueModif52 = new Attribute(Contribution.VALUE,c.nature);
              localphoto2.setAttribute(valueModif52);
              cv.addContent(localphoto2); 
		  }
		  
	  }
	  if(c.nomsite != null)
	  {
		  if (!c.nomsite.equals(""))
		  {
			  Element localphoto2 = new Element(Contribution.NOM_SITE);
              Attribute valueModif52 = new Attribute(Contribution.VALUE,c.nomsite);
              localphoto2.setAttribute(valueModif52);
              cv.addContent(localphoto2); 
		  }
		  
	  }
	  if(c.detailsite != null)
	  {
		  if (!c.detailsite.equals(""))
		  {
			  Element localphoto2 = new Element(Contribution.DETAIL_SITE);
              Attribute valueModif52 = new Attribute(Contribution.VALUE,c.detailsite);
              localphoto2.setAttribute(valueModif52);
              cv.addContent(localphoto2); 
		  }
		  
	  }
	  if(c.pmr != null)
	  {
		  if (!c.pmr.equals(""))
		  {
			  Element localphoto2 = new Element(Contribution.PMR);
              Attribute valueModif52 = new Attribute(Contribution.VALUE,c.pmr);
              localphoto2.setAttribute(valueModif52);
              cv.addContent(localphoto2); 
		  }
		  
	  }
	  if(c.autre != null)
	  {
		  if (!c.autre.equals(""))
		  {
			  Element localphoto2 = new Element(Contribution.AUTRE);
              Attribute valueModif52 = new Attribute(Contribution.VALUE,c.autre);
              localphoto2.setAttribute(valueModif52);
              cv.addContent(localphoto2); 
		  }
	  }
	  
      Date date1 = new Date();//definition de la date
	  SimpleDateFormat  simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
	  Element dte = new Element(Contribution.DATECONTRIBUTION);
	  Attribute classedate = new Attribute(Contribution.VALUE,simpleFormat.format(date1));
	  dte.setAttribute(classedate);
	  
	  
     
      c.date = simpleFormat.format(date1);
      cv.addContent(dte);
	  
      Element d2 = new Element(Contribution.HEURECONTRIBUTION);//definition de lheure
	  SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
	  String s = f.format(date1);
	  Attribute classeheure = new Attribute(Contribution.VALUE,f.format(date1));
	  d2.setAttribute(classeheure);
	  
	 
	  c.heure = f.format(date1);
	  cv.addContent(d2);
	  
	}
	

	public String xmlToString() {
		// TODO Auto-generated method stub
		XMLOutputter xmOut=new XMLOutputter(); 
		Log.d(DEBUG_TAG+"/Contrb/xmlToString","----"+xmOut.outputString(docEnvoi));
		Log.d(DEBUG_TAG+"/Contrb/xmlToString","---- fin");
		return xmOut.outputString(docEnvoi);
	}


}
