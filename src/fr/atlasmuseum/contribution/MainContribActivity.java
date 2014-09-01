package fr.atlasmuseum.contribution;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.atlasmuseum.R;
import fr.atlasmuseum.compte.Authentification;
import fr.atlasmuseum.compte.ConnexionActivity;
import fr.atlasmuseum.contribution.Contribution.champ_status;
import fr.atlasmuseum.main.AtlasError;
import fr.atlasmuseum.main.MainActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.irisa.unpourcent.location.LocationProvider;
import com.irisa.unpourcent.location.LocationStruct;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.ContactsContract.DeletedContacts;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainContribActivity extends Activity {
	/*******************************
	 * DEBUG MODE
	 *
	 */
	
	Boolean debug=false;
	
	static final int  IDUtil = 709;
	private static final String DEBUG_TAG = "MainContribAcitivity";
	Bundle bundle;
	Button create;
	Button affichXML;
	Button erase;
	Button mButtonEnvoyer;
	int nbContrib=1;
	ListView listViewContrib;
	int nbContribSave; //nombre de contribution sauvegard�s
	int nbContribSent; //nombre de contribution envoy�s
	private String url_sendAPhoto ="http://atlasmuseum.irisa.fr/scripts/storeContribImage.php";
	private String url_sendXML ="http://atlasmuseum.irisa.fr/scripts/receiveContributionFile.php";
	private int REQUEST_CONNEXION=1245;
	private TextView text_contribsavetitre;

	
	public static int idGroupContrib = 5;//id pour le groupe id
	
	static ContribXml contXml;//contient la liste de contribution + gere le xml
	public static int REQUEST_FINISH=154213;

    public void onCreate(Bundle savedInstanceState)
    {
    	
    	 super.onCreate(savedInstanceState);
         requestWindowFeature(Window.FEATURE_ACTION_BAR);
         setContentView(R.layout.contrib_main_layout);
         
         
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
         
         create = (Button) findViewById(R.id.button1);
         affichXML = (Button) findViewById(R.id.button2);
         listViewContrib = (ListView) findViewById(R.id.list_view);
         erase = (Button) findViewById(R.id.erase);
         
         TextView infocontrib = (TextView) findViewById(R.id.infoContrib);
         TextView infocontribtitre = (TextView) findViewById(R.id.infoContribtitre);
         
         //titres des item de contributions
         TextView titre_contribuer = (TextView) findViewById(R.id.text_contribuertitre);
         TextView text_contribuer = (TextView) findViewById(R.id.text_contribuer);
         
         TextView text_contribenvoytitre = (TextView) findViewById(R.id.text_contribenvoytitre);
         TextView text_contribenvoy = (TextView) findViewById(R.id.text_contribenvoy);
         
      text_contribsavetitre = (TextView) findViewById(R.id.text_contribsavetitre);
         TextView text_contribsave = (TextView) findViewById(R.id.text_contribsave);
         
         Typeface font = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Bold.ttf");
         titre_contribuer.setTypeface(font);
         text_contribenvoytitre.setTypeface(font);
         text_contribsavetitre.setTypeface(font);
         infocontribtitre.setTypeface(font);
         
         Typeface font2 = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Light.ttf");
         text_contribuer.setTypeface(font2);
         text_contribenvoy.setTypeface(font2);
         text_contribsave.setTypeface(font2);
         infocontrib.setTypeface(font2);
         
     
         String chaine_xml = readContrib();
         contXml = new ContribXml(chaine_xml);//cree/charge les donn�es depuis le XML
         mButtonEnvoyer = (Button) findViewById(R.id.mButtonEnvoyer);
         if(contXml.getNbContrib() <= 1)
         {
        	 text_contribsavetitre.setText(contXml.getNbContrib()+" "+getResources().getString(R.string.contrib_save));
         }
         else
         {
        	 text_contribsavetitre.setText(contXml.getNbContrib()+" "+getResources().getString(R.string.contrib_saves));
        	 
         }
         
         //Toast.makeText(this, "vous avez "+contXml.getNbContrib()+ " contribution(s).", Toast.LENGTH_SHORT).show();
         //cree un bundle, avec des infos existantes de notice
         afficheContrib();//affiche la liste des contrib
        
        createBundle();
        
        create.setOnClickListener(new OnClickListener() {
 	        @Override
 	        public void onClick(View v) {
 	        	idGroupContrib++;
 	        	onClickCreateContrib();
 	        }
 	    });
        
        //Suivi des contributions de l'utilisateur
        RelativeLayout RelativEtatContrib = (RelativeLayout) findViewById(R.id.RelativEtatContrib);

        RelativEtatContrib.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            	boolean internetDispo = checkInternetConnection();
 	        	
 	        	if(Authentification.getisConnected())
 	        	{
 	        		String name =Authentification.getUsername();
 	        		Log.d(DEBUG_TAG+"/auteur",name );
 	        	
	 	        	if(internetDispo)
	 	        	{
	 	        		gotoSuiviContribActivity();
	 	        	}
	 	        	else //afficher une erreur car pas de connexion internet
	 	        	{
	 	        		AtlasError.showErrorDialog(MainContribActivity.this, "7.1", "pas internet connexion");
	 	        	}
 	        	}//en authentificationisconneted
 	        	else//erreur car pas connect�
 	        	{
 	        		AtlasError.showErrorDialog(MainContribActivity.this, "7.3", "compte util requis");
 	        	}
 	        	
            	
             	
 	        }
            
        });
        
        
        RelativeLayout relativ_envoi = (RelativeLayout) findViewById(R.id.RelativEnvoye);
     
        relativ_envoi.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            	//test connexion internet
 	        	boolean internetDispo = checkInternetConnection();
 	        	
 	        	if(Authentification.getisConnected())
 	        	{
 	        		String name =Authentification.getUsername();
 	        		Log.d(DEBUG_TAG+"/auteur",name );
 	        	
	 	        	if(internetDispo)
	 	        	{
	 	        		ContribXml.newDocumentXml();//on doit r� initialiser l'objet DOM
	 	        		if (contXml.listContrib.size() >0) //boucle pour envoyer les photos
	 	        		{
	 	        			//1 efface le fichier
	 	        			boolean f= deleteFile(ContribXml.nomFichier);
	 	        			Log.d("envoiPhoto modif", "SUPPRESSION FICHIER: "+f);
	 	        			Log.d("envoiPhoto modif", "contenu apres effacer ="+readContrib());
	 	        			//2 parcours la liste de contribution pou check si il ya un auteur
	 	        			//3 on enregistre le fichier
	 	        			
	 	        			// create an async task to send the graffity
	 	 	 	        	List<Contribution> SetImage = new ArrayList<Contribution>();
	 	 	 	        	for(int i=0;i<contXml.listContrib.size();i++)
	 	 	 	        	{
	 	 		 	   			Contribution c = contXml.listContrib.get(i);
	 	 		 	   			Log.d(DEBUG_TAG+"/envoyer ...", "envoi en cours .................");
	 	 		 	   			if (!c.photoPath.equals(""))
	 	 		 	   			{
	 	 		 	   				SetImage.add(c);
	 	 		 	   				Log.d("image trouv�e", "name ="+c.photoPath);
	 	 		 	   			}
	 	 		 	   			
	 	 		 	   			if((c.auteur ==null) || (c.auteur.equals("")))
	 	 		 	   			{
	 	 		 	   				Log.d(DEBUG_TAG,"pas d'auteur sur la contribution...");
	 	 		 	   				c.auteur = Authentification.getUsername();
	 	 		 	   				c.password = Authentification.getPassword();
	 	 		 	   			}
	 	 		 	   			Log.d(DEBUG_TAG+"/envoi", "contribution c.autor="+c.auteur);
	 	 		 	   			//contXml.listContributionEnCours.add(c);
	 	 		 	   			ContribXml.addContributionToXml(c);
	 	 		 	   		}
	 	 	 	        	enregistreXml();//on enregistre les modifications apport�s
	 	 	 	        	String chaine_xml = readContrib();
	 	 	 	        	//afficheAlerte(chaine_xml);
	 	 	 	        	contXml = new ContribXml(chaine_xml);//remet a zero contXml
	 	 	 	        	
	 	 	 	        	
	 	 	 	        	Log.d(DEBUG_TAG+"/contenu final", "contenu apres save ="+readContrib());
	 	 	 	        	if(SetImage.size()==0)
	 	 	 	        	{
	 	 	 	        		Log.d("Pas de photo", "NO PHOTO SAVE");
	 	 	 	        	}
	 	 	 	        	if (contXml.listContrib.size() != 0) 
	 	 	 	        	{
	 	 	 	        		Log.d(DEBUG_TAG, "list-Contrib size ="+contXml.listContrib.size());
	 	 	 	        		sendPhoto(SetImage);
	 	 	 	        	}
	 	        		}
	 	        		else
	 	        		{
	 	        			AtlasError.showErrorDialog(MainContribActivity.this, "7.2", "pas de contribution sauvegard�e");
	 	        		}
	 	        	
	 	        	}
	 	        	else //afficher une erreur car pas de connexion internet
	 	        	{
	 	        		AtlasError.showErrorDialog(MainContribActivity.this, "7.1", "pas internet connexion");
	 	        	}
 	        	}//en authentificationisconneted
 	        	else//erreur car pas connect�
 	        	{
 	        		AtlasError.showErrorDialog(MainContribActivity.this, "7.3", "compte util requis");
 	        	}
	 	        	
 	        	
 	        }
            
        });
        
        RelativeLayout relativ_contribuer = (RelativeLayout) findViewById(R.id.RelativContribuer);
        
        relativ_contribuer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            	onClickCreateContrib();
            }
        });
        
        affichXML.setText("afficheXML");
        affichXML.setOnClickListener(new OnClickListener() {
  	        @Override
  	        public void onClick(View v) {
  	        	onClickContribute();
  	        	recreate();
  	        }
  	    });
         
         
         erase.setOnClickListener(new OnClickListener() {
   	        @Override
   	        public void onClick(View v) {
   	        	onClickErase();
   	        }
   	    });
         
         if(!debug)
         {
        	 affichXML.setVisibility(View.GONE);
        	 erase.setVisibility(View.GONE);
         }
 
    }
    
    protected void gotoSuiviContribActivity() {
		// TODO Auto-generated method stub
    	Intent intent= new Intent(this, SuiviContribActivity.class); //lance la webview pour visualiser les contributions
		startActivity(intent);
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
    
		
	/**protected void sendXml() {
		// TODO Auto-generated method stub
    	
    	Log.d(DEBUG_TAG, "contenu XML avant envoi ="+readContrib());
    	EnvoiXml upl = new EnvoiXml(this,readContrib(),url_sendXML);
     	upl.execute();
	}**/
    protected void sendPhoto(List<Contribution> setImage) {
		// TODO Auto-generated method stub
    	
    	//methode pour rajouter auteur+password
    	
    	
    	EnvoiPhoto uphoto = new EnvoiPhoto(this, setImage, this.readContrib(), true);
    	uphoto.execute();
	}
	public void onClickErase()
    {
    	deleteFile(ContribXml.nomFichier);
    	//afficheAlerte(readContrib());
    	//eraseXml();
    	contXml = new ContribXml("");//cree/charge les donn�es depuis le XML
    	recreate();
    }
    private void afficheContrib()
    {
    	List<String> lcont = new ArrayList<String>();
    	for(int i=0;i<contXml.listContrib.size();i++)
    	{
    		lcont.add(contXml.listContrib.get(i).getDate()+ " "+ contXml.listContrib.get(i).getHeure());
    	}
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.xml.my_item_list, lcont);
    	listViewContrib.setAdapter(adapter);
	}

	private void createBundle() {
    	bundle = new Bundle();
    	
	}

	public void onClickCreateContrib()
    {
		String uniqueID = UUID.randomUUID().toString();
		Log.d(DEBUG_TAG, "uniq id ="+uniqueID);
		bundle.putString(Contribution.LOCALID, uniqueID);
		
    	Intent intent= new Intent(this, ListChampsNoticeModif.class);
    	
    	bundle.putBoolean(ListChampsNoticeModif.erasepref, true); //va permettre de erase les preferences de listnoticemodif
    	//dans le cas ou des valeurs ont �t� sauv�es suite a un retour vers la notice de reference
    	
    	intent.putExtras(bundle);
		startActivityForResult(intent, MainContribActivity.REQUEST_FINISH);
    }
    public void onClickContribute()
    {
    	//Contribution c = Contribution.createContributionRemplacement(12,1, Contribution.COULEUR, "Rouge");
    	//c.status = champ_status.SENT;
    	//ContribXml.addContributionToXml(c);
    	//enregistreXml();
    	//Toast.makeText(this, ContribXml.xmlToString(), Toast.LENGTH_SHORT).show();
    	//afficheAlerte(ContribXml.xmlToString());
    	afficheAlerte(readContrib());
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
		  //e.printStackTrace();
		  Log.d(DEBUG_TAG+"ECHEC", "echec enregistrement");
		}
	}



	
	
	public void afficheAlerte(String ch)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(ch);
       /** builder.setPositiveButton("Sauvegarder", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int id) {
        			
        		}
        	});**/
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
 		public void onClick(DialogInterface dialog, int id) {
 		}
 	});
    builder.create().show();
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
			//e.printStackTrace();
		} catch (IOException e) {
			Log.d(DEBUG_TAG+"ECHEC ioException", "echec enregistrement");
			//e.printStackTrace();
		}
		return contenu;
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // TODO Auto-generated method stub
	    Log.d(DEBUG_TAG, "result code="+requestCode);
	    if(requestCode == MainContribActivity.REQUEST_FINISH)
	    {
	    	
	    	this.recreate();
	    }
	    if(requestCode == REQUEST_CONNEXION )
	    {
	    	
	    }
	    else
	    {
	    	super.onActivityResult(requestCode, resultCode, data);
	    }
	    
	}
	

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		
		if(itemId == android.R.id.home)
		{
			//super.onBackPressed();//forcement on vient de l'activit� MAIN
			/**Intent intent= new Intent(this,MainActivity.class);
			startActivity(intent);**/
			finish();
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
