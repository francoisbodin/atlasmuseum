package fr.atlasmuseum.contribution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.jdom2.Text;

import fr.atlasmuseum.R;
import fr.atlasmuseum.contribution.Contribution.type_contrib;
import fr.atlasmuseum.search.JsonRawData;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ModifActivity extends Activity {
	Bundle bundle;
	TextView titre_contrib;
	EditText edit;
	Button buttonOKBottom;
	Button buttonOK;//bouton ok dans le relativMain
	Button delete;
	final static String DEBUG_TAG = "ModifActivity";
	
	String value;//valeur donn� par l'utilisateur
	ListView listV; //utiliser pour afficher
	LinearLayout relativMain;
	type_contrib typeContrib;//definit le type de contribution en cours
	private ArrayList<CheckBoxElt> CheckList;
	private BaseAdapter adapter;
	private String champsAModifier;
	private String[] radioList;
	private TextView infos;
	private TextView date;

	
    public void onCreate(Bundle savedInstanceState)
    {
    	
    	 super.onCreate(savedInstanceState);
         requestWindowFeature(Window.FEATURE_ACTION_BAR);
         setContentView(R.layout.contrib_layout_modif);
         
         buttonOKBottom = (Button) findViewById(R.id.button1);
         buttonOK= (Button) findViewById(R.id.button2);
         titre_contrib = (TextView) findViewById(R.id.titre_contrib);
         edit = (EditText) findViewById(R.id.editText1);
         listV = (ListView) findViewById(R.id.listView1); //sert � afficher les liste radio et checkboxes
         infos = (TextView) findViewById(R.id.infos); 
         date = (TextView) findViewById(R.id.date); 
         //relativMain utilis� pour cacher a la fois la zone de saisie et aussi le bouton OK du haut
         relativMain = (LinearLayout) findViewById(R.id.linearMain);//zone de saisie de texte normal+buttonOK
         
        
         bundle = getIntent().getExtras();
         bundle.toString();
         Log.d(DEBUG_TAG+"/onCreate", bundle.toString());

         //String ch =  bundle.getString(ListChampsNoticeModif.CHAMPS_ITEM);//pour savoir le champs qu'on va modifier
       champsAModifier = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.champ_a_modifier, "");
        titre_contrib.setText(champsAModifier);
         
         getActionBar().setTitle(getResources().getString(R.string.Contribuer));
         
    	 //gestion bundle
      	gestionBundle();
      	
      	buttonOKBottom.setOnClickListener(new OnClickListener() 
     		{
				@Override
				public void onClick(View arg0) {
				     onClickOk();
				}
     		});

      	buttonOK.setOnClickListener(new OnClickListener() 
 		{
			@Override
			public void onClick(View arg0) {
			     onClickOk();
			}
 		});
      	
     	Log.d(DEBUG_TAG, "bundle = "+bundle.toString());
     	
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


	private void gestionBundle() {
    	String ch = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.champ_a_modifier, "");
    	//String ch =  bundle.getString(ListChampsNoticeModif.CHAMPS_ITEM);//pour savoir le champs qu'on va modifier
    	titre_contrib.setText(ch);
		switch(ch)
		{
		case ListChampsNoticeModif.modif_titre: //"Modifier Titre"

			buttonOKBottom.setVisibility(View.GONE);
			infos.setVisibility(View.GONE);
			String val = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_titre, "");
			if (val.equals(""))
			{
				edit.setText(bundle.getString(Contribution.TITRE));
			}
			else
			{
				edit.setText(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_titre, ""));
			}
			
			break;
		case ListChampsNoticeModif.ajout_titre://"Ajouter Titre"

			buttonOKBottom.setVisibility(View.GONE);
			infos.setVisibility(View.GONE);
			edit.setText(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_titre, ""));
			
			break;
		case ListChampsNoticeModif.ajout_artiste : //"Ajouter un artiste"
			buttonOKBottom.setVisibility(View.GONE);
			infos.setVisibility(View.GONE);
			edit.setText(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_artiste, ""));
			
			break;
		case ListChampsNoticeModif.modif_artiste://"Modifier un artiste"
			buttonOKBottom.setVisibility(View.GONE);
			infos.setVisibility(View.GONE);
			String val2 = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_artiste, "");
			if (val2.equals(""))
			{
				edit.setText(bundle.getString(Contribution.ARTISTE));
			}
			else
			{
				edit.setText(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_artiste, ""));
			}
		
			break;
			
		case ListChampsNoticeModif.modif_date://"Modifier date inauguration"
			buttonOKBottom.setVisibility(View.GONE);
			infos.setVisibility(View.VISIBLE);
			infos.setText(getResources().getString(R.string.date_txt));
			
			
			
			
			edit.setVisibility(View.GONE);
			edit.setVisibility(View.GONE);
			date.setVisibility(View.VISIBLE);
			
		    
			
			String valdatemodif = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_date, "");
			if (valdatemodif.equals(""))
			{
				date.setText(bundle.getString(Contribution.DATE_INAUGURATION));
			}
			else
			{
				date.setText(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_date, ""));
			}
			
			break;
		case ListChampsNoticeModif.ajout_date ://"Ajouter date inauguration"
			buttonOKBottom.setVisibility(View.GONE);
			infos.setVisibility(View.VISIBLE);
			infos.setText(getResources().getString(R.string.date_txt));
			edit.setVisibility(View.GONE);
			date.setVisibility(View.VISIBLE);
			date.setText(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_date, ""));
	
			break;
		case ListChampsNoticeModif.modif_description:// "Modifier description"
			buttonOKBottom.setVisibility(View.GONE);
			infos.setVisibility(View.GONE);
			String valddesmodif = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_description, "");
			if (valddesmodif.equals(""))
			{
				edit.setText(bundle.getString(Contribution.DESCRIPTION));
			}
			else
			{
				edit.setText(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_description, ""));
			}
			
			break;
		case ListChampsNoticeModif.ajout_description://"Ajouter description"
			buttonOKBottom.setVisibility(View.GONE);
			infos.setVisibility(View.GONE);
			edit.setText(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_description, ""));
			
			break;
		case ListChampsNoticeModif.modif_couleur ://"Modifier couleur"
			
			
			this.afficheListCheckBox(JsonRawData.listeCouleurs);
			
			String valmatmodif = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_couleur, "");
			if (valmatmodif.equals(""))
			{
				//cocher les elt par defaut, cad ceux de la notice de reference
				String eltDefaut  = bundle.getString(Contribution.COULEUR);
				this.setCheckFromString(eltDefaut);
			}
			else
			{

				this.setCheckFromString(valmatmodif);
			}
			
			
			break;
		case ListChampsNoticeModif.ajout_couleur ://"Ajouter couleur"
			
			this.afficheListCheckBox(JsonRawData.listeCouleurs);
			
			String ajoutcouleur = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_couleur, "");

			this.setCheckFromString(ajoutcouleur);
			
			break;
		case ListChampsNoticeModif.modif_materiaux: //"Modifier des mat�riaux"
		
			this.afficheListCheckBox(JsonRawData.listeMateriaux);
			
			String valmodif_materiaux = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_materiaux, "");
			if (valmodif_materiaux.equals(""))
			{
				//cocher les elt par defaut, cad ceux de la notice de reference
				String eltDefaut  = bundle.getString(Contribution.MATERIAUx);
				this.setCheckFromString(eltDefaut);
			}
			else
			{

				this.setCheckFromString(valmodif_materiaux);
			}
			
			break;
		case ListChampsNoticeModif.ajout_materiaux://"Ajouter des matériaux"
			
			
			this.afficheListCheckBox(JsonRawData.listeMateriaux);
			
			String ajoutmat = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_materiaux, "");

			this.setCheckFromString(ajoutmat);
			
			break;
		case ListChampsNoticeModif.ajout_nomsite://"Ajouter nom du site"
			buttonOKBottom.setVisibility(View.GONE);
			infos.setVisibility(View.GONE);
			edit.setText(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_nomsite, ""));
			
			break;
		case ListChampsNoticeModif.modif_nomsite:
			buttonOKBottom.setVisibility(View.GONE);
			infos.setVisibility(View.GONE);
			String valnomsitemodif = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_nomsite, "");
			if (valnomsitemodif.equals(""))
			{
				edit.setText(bundle.getString(Contribution.NOM_SITE));
			}
			else
			{
				edit.setText(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_nomsite, ""));
			}
			
			break;
		case ListChampsNoticeModif.ajout_nature://"Ajouter nature"
			
			this.afficheListRadio(JsonRawData.listeNatures);
			
			String ajoutnat = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_nature, "");

			this.setSelectedFromString(ajoutnat);
			
			//edit.setText(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_nature, ""));
			
			break;
		case ListChampsNoticeModif.modif_nature:
			this.afficheListRadio(JsonRawData.listeNatures);
			
			String valmodif_modif_nature = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_nature, "");
			if (valmodif_modif_nature.equals(""))
			{
				//cocher les elt par defaut, cad ceux de la notice de reference
				String eltDefaut  = bundle.getString(Contribution.NATURE);
				this.setSelectedFromString(eltDefaut);
			}
			else
			{

				this.setSelectedFromString(valmodif_modif_nature);
			}
			
			
			break;
		case ListChampsNoticeModif.ajout_autre://"Ajouter autres infos"
			buttonOKBottom.setVisibility(View.GONE);
			infos.setVisibility(View.GONE);
			edit.setText(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_autre, ""));
			
			break;
		case ListChampsNoticeModif.ajout_etat://"Ajouter des mat�riaux"
			
			this.afficheListRadio(JsonRawData.listePrecision_etat_conservation);
			
			String ajout_etat = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_etat, "");

			this.setSelectedFromString(ajout_etat);
			
			
			break;
			
			case ListChampsNoticeModif.modif_etat://"Ajouter des mat�riaux"
			
				this.afficheListRadio(JsonRawData.listePrecision_etat_conservation);
				
				String valmodif_etat = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_etat, "");
				if (valmodif_etat.equals(""))
				{
					//cocher les elt par defaut, cad ceux de la notice de reference
					String eltDefaut  = bundle.getString(Contribution.ETAT);
					this.setSelectedFromString(eltDefaut);
				}
				else
				{

					this.setSelectedFromString(valmodif_etat);
				}
			
			break;
			
			
		case ListChampsNoticeModif.ajout_petat://"Ajouter ^precision etat de conservation"
			this.afficheListCheckBox(JsonRawData.listeAutre_precision_etat_conservation);
			
			String valmodif_petat1 = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_petat, "");
			this.setCheckFromString(valmodif_petat1);			
			break;
		case ListChampsNoticeModif.modif_petat://"Modifier precision etat de conservation"
			
			this.afficheListCheckBox(JsonRawData.listeAutre_precision_etat_conservation);
			
			String valmodif_petat = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif_petat, "");
			if (valmodif_petat.equals(""))
			{
				//cocher les elt par defaut, cad ceux de la notice de reference
				String eltDefaut  = bundle.getString(Contribution.PETAT);
				this.setCheckFromString(eltDefaut);
			}
			else
			{

				this.setCheckFromString(valmodif_petat);
			}
			
			
			break;
		case ListChampsNoticeModif.ajout_pmr://"personne mobilité reduite"

			this.afficheListRadio(JsonRawData.listePmr);
			
			String ajoutpmr = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_pmr, "");

			this.setSelectedFromString(ajoutpmr);
			
			break;
			
		case ListChampsNoticeModif.ajout_detailsite:
			buttonOKBottom.setVisibility(View.GONE);
			infos.setVisibility(View.GONE);
			edit.setText(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.ajout_detailsite, ""));
			
			break;
		case ListChampsNoticeModif.modif__detailsite://"Modifier detail site"
			buttonOKBottom.setVisibility(View.GONE);
			infos.setVisibility(View.GONE);
			String val3 = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif__detailsite, "");
			if (val3.equals(""))
			{
				edit.setText(bundle.getString(Contribution.DETAIL_SITE));
			}
			else
			{
				edit.setText(ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.modif__detailsite, ""));
			}
		}
		
		
		//old_value = edit.getText().toString();
	}


private void setSelectedFromString(String ajoutnat) {
		// TODO Auto-generated method stub
		RadioListAdapter a = (RadioListAdapter) adapter;
		
		//A revoir pour setSelected une valeur
		Boolean finish=false;
		/**for(int i=0;i<radioList.length-1 && !finish;i++)
		{
			if(radioList[i].equals(ajoutnat))
			{
				a.listView.get(i).radioBtn
				a.valueSelected=radioList[i];
				finish=true;
			}
		}**/
	}


private void afficheListRadio(String[] listElt) {
		// TODO Auto-generated method stub
	cacheZoneSaisie();
	Arrays.sort(listElt);//tri la liste
	radioList = listElt;
	//cree l'adapter
	adapter = new RadioListAdapter(getApplicationContext(), listElt);
	listV.setAdapter(adapter);
	
	//listV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	/**listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id)
		{
			adapter.notifyDataSetChanged();
		}
	});**/
	}


private void setCheckFromString(String eltDefaut)
	{
		//cocher les elt par defaut, cad ceux de la notice de reference
		for(int i=0;i<CheckList.size();i++)
		{
			if(eltDefaut.contains(CheckList.get(i).getValue()))
					{
						CheckList.get(i).setBool(true); //coche les cases si la notice possede les proprietes value
					}
		}
		adapter.notifyDataSetChanged();
	}


/**
 * cache la zone de saisie, et affiche la liste view
 * pour materiaux, etat conservation, 
 * @param listElt issue de la BDD
 */
	private void afficheListCheckBox(String[] listElt) {
		//cacher la zone de saisie normal
		//les données de la BDD
		cacheZoneSaisie();
		Arrays.sort(listElt);//tri la liste
		//on cree les element CheckBox
		CheckList = new ArrayList<CheckBoxElt>();
		for(int i=0;i<listElt.length-1;i++)
		{
			CheckBoxElt ch1 = new CheckBoxElt(listElt[i]);
			CheckList.add(ch1);
		}
		//cree l'adapter
		adapter = new CheckBoxAdapter(getApplicationContext(), CheckList);
		listV.setAdapter(adapter);
	}

	public void cacheZoneSaisie()
	{
		relativMain.setVisibility(View.GONE);
		//afficher la liste View
		listV.setVisibility(View.VISIBLE);
		//charger depuis la BDD les valeurs possibles
	}
	
	public void onClickOk()
    {//2 boutons 
		//Toast.makeText(this, "create_contrib", Toast.LENGTH_SHORT).show();
		//String id = bundle.getString(Contribution.IDNOTICE);
		int i = this.getTypeOfContribution();
		String val="";
		switch(i)
		{
		case 0: //par defaut, recupere le texte zone de saisie
			Log.d(DEBUG_TAG, "onClickOK 0:zone de saisie");
			val = edit.getText().toString();//recupere l'entree utilisateur
			
	    	break;
		case 1: //pour ajout/modification qui utilise checkBox
			//recupere les champs checked
			Log.d(DEBUG_TAG, "onClickOK 1:checkBox");
			Log.d(DEBUG_TAG, "onClickOK: checklist size= "+CheckList.size());
			val="";
			for(int j=0; j<CheckList.size();j++)
			{
				CheckBoxElt chek = (CheckBoxElt) adapter.getItem(j);
				Log.d(DEBUG_TAG, "onClickOK: checklist("+j+")= "+chek.getValue()+" bool = "+chek.getBool());
				Boolean b =chek.getBool();
				if(b)
				{
					val= val+", "+CheckList.get(j).getValue();
				}
			}
			if(val.startsWith(", "))
			{
				val = val.substring(2);
			}
			break;
		case 2: //pour RadioButton List
			RadioListAdapter rdAdapt = (RadioListAdapter) adapter;
			val = rdAdapt.getValue();
			Log.d(DEBUG_TAG, "getValue ="+val);
			break;
		case 3:
			val = date.getText().toString();
			break;
		}
		
		String champModifie = ListChampsNoticeModif.cPref.getString(ListChampsNoticeModif.champ_a_modifier, "");
		Log.d(DEBUG_TAG, "champs modifier = "+champModifie);
		boolean f= ListChampsNoticeModif.cPref.edit().putString(champModifie, val).commit();
		Log.d(DEBUG_TAG+"/cpref", "ajout val ="+f );
		ListChampsNoticeModif.cPref.edit().commit();
    	Log.d(DEBUG_TAG, "modif bundle =" +bundle.toString());
    	
    	setResult(ListChampsNoticeModif.REQUEST_FINISH, null);//pour fermer l'activite precedente
    	
    	Intent intent = new Intent(this, ListChampsNoticeModif.class);
    	intent.putExtras(bundle);
        startActivity(intent);
        
        finish();
    }
	
	private int getTypeOfContribution() {
		if(champsAModifier.equals(ListChampsNoticeModif.modif_materiaux) || champsAModifier.equals(ListChampsNoticeModif.ajout_materiaux))
		{
			return 1;//vue avec checkBox
		}
		if(champsAModifier.equals(ListChampsNoticeModif.ajout_couleur) || champsAModifier.equals(ListChampsNoticeModif.modif_couleur))
		{
			return 1;
		}
		if(champsAModifier.equals(ListChampsNoticeModif.ajout_petat) || champsAModifier.equals(ListChampsNoticeModif.modif_petat))
		{
			return 1;
		}
		if(champsAModifier.equals(ListChampsNoticeModif.ajout_nature) || champsAModifier.equals(ListChampsNoticeModif.modif_nature))
		{
			return 2; //radioList
		}
		if(champsAModifier.equals(ListChampsNoticeModif.ajout_etat) || champsAModifier.equals(ListChampsNoticeModif.modif_etat))
		{
			return 2;
		}
		if(champsAModifier.equals(ListChampsNoticeModif.ajout_pmr))
		{
			return 2;
		}
		if(champsAModifier.equals(ListChampsNoticeModif.ajout_date) || champsAModifier.equals(ListChampsNoticeModif.modif_date))
		{
			return 3; //pour afficher l editText pour les dates
		}
		else
		{
			return 0;
		}
		
	}


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(ListChampsNoticeModif.REQUEST_FINISH, null);//pour fermer l'activite precedente
		Intent intent = new Intent(this, ListChampsNoticeModif.class);
    	intent.putExtras(bundle);
        startActivity(intent);
        finish();
		//super.onBackPressed();
	}
	

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		
		if(itemId == android.R.id.home)
		{
			setResult(ListChampsNoticeModif.REQUEST_FINISH, null);//pour fermer l'activite precedente
			Intent intent = new Intent(this, ListChampsNoticeModif.class);
	    	intent.putExtras(bundle);
	        startActivity(intent);
			finish();
			return true;
		}
    	
	
		else return false;
		    
    }
	
}
