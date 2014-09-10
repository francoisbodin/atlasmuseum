package fr.atlasmuseum.contribution;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import fr.atlasmuseum.contribution.Contribution.champ_status;
import fr.atlasmuseum.contribution.Contribution.type_contrib;
import fr.atlasmuseum.main.MainActivity;
import android.content.Context;
import android.util.Log;
//http://cynober.developpez.com/tutoriel/java/xml/jdom/


/**
 * classe qui gere la sauvegarde et cr�ation du xml pour la contribution
 */
public class ContribXml
{
	
	//utilis� dans les balises xml
	//public final static String CONTRIBUTION = "contribution";
	//public final static String CHAMP = "CHAMP";
	//public final static String VALUE = "value";
	//public final static String ID = "ID";
	
   final static String DEBUG_TAG = "contribXml/" ;
   final static String nomFichier = "contribproject2.txt";//nom de fichier xml pour sauvegarder les contribs
   //static Element racine = new Element("Liste_contribution");//racine XML   
   //static Document document = new Document(racine);//Document JDOM
   
   static Element racine ;
   static Document document;//utiliser dans toute l'application
   
   List<Contribution> listContributionEnCours;//liste de contribution en cours d'�dition
   public List<Contribution> listContrib;//liste des contributions
   public int nbSave; //nombre de contributions sauvegard�es
  
   
   Contribution.Location location;
   
  Document docEnvoi;//pour envoi en cours
    Element racineEnvoi;
   
   private Boolean mainContrib; //d�fini quel document de la classe utilis�
   //si true: document - racine, si false: docEnvoi -racineEnvoi
   /**
    * 
    */
private String url_sendAPhoto ="http://atlasmuseum.irisa.fr/scripts/storeContribImage.php";
private String url_sendXML ="http://atlasmuseum.irisa.fr/scripts/receiveContributionFile.php";

   
public int getNbContrib()
{
	return nbSave;
}
   
   public ContribXml()
   {
	   racine = new Element("xml");
		document= new Document(racine);
   }
   
	public ContribXml(String chaine_xml)
	{
		nbSave =0;
		listContributionEnCours = new ArrayList<Contribution>();
		listContrib = new ArrayList<Contribution>();
		mainContrib = true;
		racine = new Element("xml");
		document= new Document(racine);
		
		
		//g�n�re la liste de contribution
		//parcour du fichier pour extraire la liste des contributions
		recupListContrib(chaine_xml);
		
	}
	
	
	public String createContribToSend(List<Contribution> list)
	{
		racineEnvoi = new Element("xml");
		docEnvoi= new Document(racine);
		mainContrib=false;
		for(int i=0;i<list.size();i++)
		{
			list.get(i);
		}
		return exportXmlEnvoi();
	}
	
	private String exportXmlEnvoi() {
		// TODO Auto-generated method stub
		XMLOutputter xmOut=new XMLOutputter(); 
		Log.d(DEBUG_TAG+"/Contrb/xmlToString","----"+xmOut.outputString(docEnvoi));
		Log.d(DEBUG_TAG+"/Contrb/xmlToString","---- fin");
		return xmOut.outputString(document);
	}

	/**
	 * recupere la liste des contributions depuis le contenu du fichier xml:chaine_xml
	 * @param  le contenu du fichier xml
	 */
	public void recupListContrib(String chaine_xml)
	{
		Set<String> listLocalId = new HashSet<String>() ;//list contenant les localid sans doublon
		//permet de compter le nombre de contribution sauvegard�e
		
		listContrib = new ArrayList<Contribution>();
		SAXBuilder saxBuilder = new SAXBuilder();
		Reader stringReader=new StringReader(chaine_xml);
		try {
			document=saxBuilder.build(stringReader);
			//On initialise un nouvel �l�ment racine avec l'�l�ment racine du document.
		    racine = document.getRootElement();
			List<?> list = racine.getChildren(Contribution.CONTRIBUTION);

		   //On cr�e un Iterator sur notre liste
		   Iterator<?> i = list.iterator();
		   
		   while(i.hasNext())
		   {
			   
		      //On recr�e l'Element courant � chaque tour de boucle afin de
		      //pouvoir utiliser les m�thodes propres aux Element comme :
		      //s�lectionner un n�ud fils, modifier du texte, etc...
		      Element courant = (Element)i.next();
		      
		      Contribution c = new Contribution();
		      c.statut = Contribution.getStatusContribFromString(courant.getAttributeValue(Contribution.STATUT));
		      Log.d(DEBUG_TAG,"status contrib ="+courant.getAttributeValue(Contribution.STATUT));
		      if( c.statut.toString().equals(champ_status.enattente.toString()))
		      {
		    	  Log.d(DEBUG_TAG+"/save", "+1 save");
		    	  nbSave ++;
		      }
		      c.type = Contribution.getTypeContribFromString(courant.getAttributeValue(Contribution.TYPE));
		      
		      if ( courant.getChild(Contribution.IDNOTICE) != null)
		      {
		    	  c.idNotice=Integer.parseInt(courant.getChild(Contribution.IDNOTICE).getAttributeValue(Contribution.VALUE));
		      }
		      if ( courant.getChild(Contribution.AUTEUR) != null)
		      {
		    	  c.auteur=courant.getChild(Contribution.AUTEUR).getAttributeValue(Contribution.VALUE);
		      }
		      if ( courant.getChild(Contribution.PASSWORD) != null)
		      {
		    	  c.password=courant.getChild(Contribution.PASSWORD).getAttributeValue(Contribution.VALUE);
		      }
		      else
		      {
		    	  c.auteur="";
		    	  c.password="";
		      }
		      if ( courant.getChild(Contribution.LATITUDE) != null)
		      {
		    	  c.latitude= Double.valueOf(courant.getChild(Contribution.LATITUDE).getAttributeValue(Contribution.VALUE));
		    	  c.longitude= Double.valueOf(courant.getChild(Contribution.LONGITUDE).getAttributeValue(Contribution.VALUE));	
		      }
		      if(!courant.getChild(Contribution.LOCALID).getAttributeValue(Contribution.VALUE).equals(""))
		      {
		    	  //c.idLocal=Integer.parseInt(courant.getChild(Contribution.LOCALID).getAttributeValue(Contribution.VALUE));
		    	  c.idLocal=courant.getChild(Contribution.LOCALID).getAttributeValue(Contribution.VALUE);
		    	  listLocalId.add(c.idLocal);
		      }
		      
		     
		      // else //dans le cas ou si c'est une contribution de type cr�e
			  //   {
	    	  //parcour de tous les champs pour savoir si existant et possede une valeur
	  		  if( courant.getChild(Contribution.PHOTO) != null)
		  		{
		  			if(!courant.getChild(Contribution.PHOTO).getAttributeValue(Contribution.VALUE).equals(""))
				      {
				    	  c.photoPath=courant.getChild(Contribution.PHOTO).getAttributeValue(Contribution.VALUE);
				      }
		  		}
	  		  if( courant.getChild(Contribution.ARTISTE) != null)  
		  		{
		    	  if(!courant.getChild(Contribution.ARTISTE).getAttributeValue(Contribution.VALUE).equals(""))
			      {
			    	  c.artiste=courant.getChild(Contribution.ARTISTE).getAttributeValue(Contribution.VALUE);
			      }
	      		}
  			  if( courant.getChild(Contribution.MATERIAUX) != null) 
	  			{
	  				 if(!courant.getChild(Contribution.MATERIAUX).getAttributeValue(Contribution.VALUE).equals(""))
				      {
				    	  c.materiaux=courant.getChild(Contribution.MATERIAUX).getAttributeValue(Contribution.VALUE);
				      }
	  			}
  			
  			if( courant.getChild(Contribution.DESCRIPTION) != null)
  			{
  				
		    	  if(!courant.getChild(Contribution.DESCRIPTION).getAttributeValue(Contribution.VALUE).equals(""))
			      {
			    	  c.description=courant.getChild(Contribution.DESCRIPTION).getAttributeValue(Contribution.VALUE);
			      }
  			}
  			
  			if( courant.getChild(Contribution.DATE_INAUGURATION) != null)
  			{
	    	  if(!courant.getChild(Contribution.DATE_INAUGURATION).getAttributeValue(Contribution.VALUE).equals(""))
		      {
		    	  c.dateinauguration=courant.getChild(Contribution.DATE_INAUGURATION).getAttributeValue(Contribution.VALUE);
		      }
  			}
  			if( courant.getChild(Contribution.TITRE) != null)
  			{
		    	  if(!courant.getChild(Contribution.TITRE).getAttributeValue(Contribution.VALUE).equals(""))
			      {
			    	  c.titre=courant.getChild(Contribution.TITRE).getAttributeValue(Contribution.VALUE);
			      }
  			}
  			if( courant.getChild(Contribution.COULEUR) != null)
  			{
		    	  if(!courant.getChild(Contribution.COULEUR).getAttributeValue(Contribution.VALUE).equals(""))
			      {
			    	  c.couleur=courant.getChild(Contribution.COULEUR).getAttributeValue(Contribution.VALUE);
			      }
	      			
		    }
  			
  			if( courant.getChild(Contribution.ETAT) != null)
  			{
		    	  if(!courant.getChild(Contribution.ETAT).getAttributeValue(Contribution.VALUE).equals(""))
			      {
			    	  c.etat=courant.getChild(Contribution.ETAT).getAttributeValue(Contribution.VALUE);
			      }
	      			
		    }
  			if( courant.getChild(Contribution.PETAT) != null)
  			{
		    	  if(!courant.getChild(Contribution.PETAT).getAttributeValue(Contribution.VALUE).equals(""))
			      {
			    	  c.petat=courant.getChild(Contribution.PETAT).getAttributeValue(Contribution.VALUE);
			      }
	      			
		    }
  			if( courant.getChild(Contribution.NATURE) != null)
  			{
		    	  if(!courant.getChild(Contribution.NATURE).getAttributeValue(Contribution.VALUE).equals(""))
			      {
			    	  c.nature=courant.getChild(Contribution.NATURE).getAttributeValue(Contribution.VALUE);
			      }
	      			
		    }
  			if( courant.getChild(Contribution.DETAIL_SITE) != null)
  			{
		    	  if(!courant.getChild(Contribution.DETAIL_SITE).getAttributeValue(Contribution.VALUE).equals(""))
			      {
			    	  c.detailsite=courant.getChild(Contribution.DETAIL_SITE).getAttributeValue(Contribution.VALUE);
			      }
	      			
		    }
  			if( courant.getChild(Contribution.NOM_SITE) != null)
  			{
		    	  if(!courant.getChild(Contribution.NOM_SITE).getAttributeValue(Contribution.VALUE).equals(""))
			      {
			    	  c.nomsite=courant.getChild(Contribution.NOM_SITE).getAttributeValue(Contribution.VALUE);
			      }
	      			
		    }
  			if( courant.getChild(Contribution.PMR) != null)
  			{
		    	  if(!courant.getChild(Contribution.PMR).getAttributeValue(Contribution.VALUE).equals(""))
			      {
			    	  c.pmr=courant.getChild(Contribution.PMR).getAttributeValue(Contribution.VALUE);
			      }
	      			
		    }
  			if( courant.getChild(Contribution.AUTRE) != null)
  			{
		    	  if(!courant.getChild(Contribution.AUTRE).getAttributeValue(Contribution.VALUE).equals(""))
			      {
			    	  c.autre=courant.getChild(Contribution.AUTRE).getAttributeValue(Contribution.VALUE);
			      }
	      			
		    }
		     // }
	      if (!c.type.equals(Contribution.type_contrib.creer)) //si la contribution est de type ajout, suppression, remplacement
		  {
			  c.champModifier= courant.getChild(Contribution.MODIFICATION).getAttributeValue(Contribution.VALUE);
			  
			  if (c.champModifier.equals(Contribution.PHOTO))
			  {
				  c.photoPath =courant.getChild(Contribution.PHOTO).getAttributeValue(Contribution.VALUE);
			  }
			  if (c.champModifier.equals(Contribution.ARTISTE))
			  {
				  c.artiste =courant.getChild(Contribution.ARTISTE).getAttributeValue(Contribution.VALUE);
			  }
			  if (c.champModifier.equals(Contribution.TITRE))
			  {
				  c.titre =courant.getChild(Contribution.TITRE).getAttributeValue(Contribution.VALUE);
			  }
			  if (c.champModifier.equals(Contribution.MATERIAUX))
			  {
				  c.materiaux =courant.getChild(Contribution.MATERIAUX).getAttributeValue(Contribution.VALUE);
			  }
			  if (c.champModifier.equals(Contribution.DESCRIPTION))
			  {
				  c.description =courant.getChild(Contribution.DESCRIPTION).getAttributeValue(Contribution.VALUE);
			  }
			  if (c.champModifier.equals(Contribution.DATE_INAUGURATION))
			  {
				  c.dateinauguration =courant.getChild(Contribution.DATE_INAUGURATION).getAttributeValue(Contribution.VALUE);
			  }
			  if (c.champModifier.equals(Contribution.COULEUR))
			  {
				  c.couleur =courant.getChild(Contribution.COULEUR).getAttributeValue(Contribution.VALUE);
			  }
		  }
	     
		  
		  
		  
			  c.date= courant.getChild(Contribution.DATECONTRIBUTION).getAttributeValue(Contribution.VALUE);
			  c.heure= courant.getChild(Contribution.HEURECONTRIBUTION).getAttributeValue(Contribution.VALUE);
			  this.listContrib.add(c);
		   }//fin while
		   
		   nbSave = listLocalId.size();
		   Log.d(DEBUG_TAG, "***** nb save ="+nbSave); // 
		   
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Log.d(DEBUG_TAG+"JDOMExecption", "error recupListContrib");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Log.d(DEBUG_TAG+"recupList IOException", "error");
		}

	     
	}
	
	/**
	 * 
	 */
	public static void newDocumentXml()
	{
		racine = new Element("xml");
		document= new Document(racine);
	}
	/**
	 * m�thode pour �crire une contribution dans le XML
	 * @param c
	 */
	public static void addContributionToXml(Contribution c)
	{
      Element cv = new Element(Contribution.CONTRIBUTION);
      racine.addContent(cv);
      
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
	

	public Boolean getMainContrib() {
		return mainContrib;
	}

	public static String xmlToString() {
		// TODO Auto-generated method stub
		XMLOutputter xmOut=new XMLOutputter(); 
		Log.d(DEBUG_TAG+"/Contrb/xmlToString","----"+xmOut.outputString(document));
		Log.d(DEBUG_TAG+"/Contrb/xmlToString","---- fin");
		return xmOut.outputString(document);
	}


}
