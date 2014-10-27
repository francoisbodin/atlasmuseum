package fr.atlasmuseum.compte;


import fr.atlasmuseum.R;
import fr.atlasmuseum.main.MainActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ConnexionActivity extends Activity 
{
	private static final String DEBUG_TAG = "ConnexionActivity";
		private  EditText user;
		private  EditText password;
		private Button loginBtn;

		//private MemoEtAuto mmat;	
		private SharedPreferences cPref; //pr�f�rence utilisateur - utiliser aussi dans MainActivity
		boolean memo,auto;
		public UserChecker uck;
		private CheckBox memo_check;	//checkbox pour "se souvenir" enregistrer les identifiant
		private CheckBox auto_check;	//checkbox pour auto connexion de l'utilisateur
		
		public CheckBox getAuto_check() {
			return auto_check;
		}

		public void setAuto_check(CheckBox auto_check) {
			this.auto_check = auto_check;
		}



		public String ULR_LOGIN;
		
		@Override
		public void onBackPressed()
		{
			Log.d("connexion activity/onback", "on back pressed");
			memorisermdp();
	    	connexionauto();
			MainActivity.getCPrefs().edit().putBoolean("AUTO_ISCHECK", false).commit();
			auto_check.setChecked(false);//decoche la checkBox "auto_connexion"
			
			super.onBackPressed();
			
		}
		 public void connexionauto(){
			 if(auto_check.isChecked())
			 {
				
				    cPref.edit().putBoolean("AUTO_ISCHECK", true).commit();
					memo_check.setChecked(true);//automatiquement coche la case "m�moriser mdp"
					cPref.edit().putBoolean("ISCHECK", true).commit();
					String username=getUser();//recupere la valeur du champs login
			    	String pass=getPassword();//recupere la valeur du champs mdp
			    	Log.d("memoetauto", "user=" +username+", pass = "+pass);
			    	Editor editor = cPref.edit();
					editor.putString("user", username);
					editor.putString("password", pass);
					editor.commit();

			 }
		 }
		 public void memorisermdp(){	 
			 if(memo_check.isChecked()) // si la case "m�moriser mdp" est coch�
			 	{
					Editor editor = MainActivity.getCPrefs().edit();
					String username=getUser();
			    	String pass=getPassword();
			    	Log.d("enregistrement memo =", "user=" +username+", pass = "+pass);
					editor.putString("user", username);
					editor.putString("password", pass);
					editor.commit();
			 	}
		 }
		 
		 
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        ULR_LOGIN = this.getResources().getString(R.string.url_login);
	        setContentView(R.layout.ecran_co);
	        cPref=getSharedPreferences("UserInfo", Context.MODE_WORLD_READABLE);
	        memo_check=(CheckBox) findViewById(R.id.checkBox1);
			auto_check=(CheckBox) findViewById(R.id.checkBox2);
			memo_check.setChecked(cPref.getBoolean("ISCHECK", false));
			
			
			user=(EditText)findViewById(R.id.editTextUserNameToLogin);
	        password=(EditText)findViewById(R.id.editTextPasswordToLogin);
			
			OnCheckedChangeListener Listen_Memo=new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					actionMemo();
				}
			}; 
			
			OnCheckedChangeListener Listen_Auto=new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					actionAuto();
				}
			};
			memo_check.setOnCheckedChangeListener(Listen_Memo);
			auto_check.setOnCheckedChangeListener(Listen_Auto);
			
			Log.d(DEBUG_TAG, ""+cPref.getBoolean("AUTO_ISCHECK", false));
			
			//coche ou pas l'auto-connexion en fonction que l'utilisateur l'avait deja coch� ou pas
			auto_check.setChecked(cPref.getBoolean("AUTO_ISCHECK", false));
	        //mmat = new MemoEtAuto(this);
	        
	        ActionBar bar = getActionBar();
	        bar.setDisplayHomeAsUpEnabled(true);
	      /**
	        user=(EditText)findViewById(R.id.editTextUserNameToLogin);
	        password=(EditText)findViewById(R.id.editTextPasswordToLogin);**/
	        
	        loginBtn=(Button)findViewById(R.id.buttonSignIn);
	        loginBtn.setOnClickListener(loginClick);
	        
	        
	        
	       
	        //Verifier le checkbox memorisation
	         memo=cPref.getBoolean("ISCHECK", false);
	         auto=cPref.getBoolean("AUTO_ISCHECK", false);
	         checkmemoState( memo);
	         
	         System.out.println("ISCHECK:"+memo+" AUTO: "+auto);
	        
	    }
	    
	   
		private void actionMemo(){
			if(memo_check.isChecked()){
				System.out.println("Le mot de passe a été mémorisé!");
				cPref.edit().putBoolean("ISCHECK", true).commit();
				String username=getUser();
		    	String pass=getPassword();
		    	Editor editor = cPref.edit();
				editor.putString("user", username);
				editor.putString("password", pass);
				editor.commit();

			}else{
				System.out.println("Le mot de passe n'a pas été mémorisé!");
				cPref.edit().putBoolean("ISCHECK", false).commit();
				cPref.edit().putString("user", "").commit();
				cPref.edit().putString("password", "").commit();
				
				}
		}
		
		private void actionAuto(){
			if(auto_check.isChecked())
			{
				System.out.println("Se connecter automatiquement la prochaine fois");
				cPref.edit().putBoolean("AUTO_ISCHECK", true).commit();
				memo_check.setChecked(true);
				cPref.edit().putBoolean("ISCHECK", true).commit();
			
				String username=getUser();
		    	String pass=getPassword();
		    	Editor editor = cPref.edit();
				editor.putString("user", username);
				editor.putString("password", pass);
				editor.commit();

			}
			else
			{
				System.out.println("La connexion n'est pas automatique!");
				cPref.edit().putBoolean("AUTO_ISCHECK", false).commit();
				cPref.edit().putString("user", "").commit();
				cPref.edit().putString("password", "").commit();
				
			}
		}
		

		/*private void loginCheck(ProgChecker pck) {
			if(pck.checkUser()){
				Intent intent=new Intent(this,MainActivity.class);
				startActivity(intent);
				String name=Authentification.getPseudo();
				Toast.makeText(getBaseContext(), "Welcome Back "+name, Toast.LENGTH_SHORT).show();
				finish();
			}
			else{
				user.setText("");
				password.setText("");
				Toast.makeText(getBaseContext(), "Auto Login Failed,try again", Toast.LENGTH_SHORT).show();
			}
		}*/

		
		private void checkmemoState(boolean memo) {
			String username=cPref.getString("user", "");
			user.setText(username);
			String mdp=cPref.getString("password", "");
			password.setText(mdp);
			System.out.println("user:"+username+" mdp: "+mdp);
			
		}
		
		


		OnClickListener loginClick=new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				memorisermdp();
		    	connexionauto();
				ConnexionAsync co = new ConnexionAsync(ConnexionActivity.this);
				co.execute();
				/**String username=user.getText().toString();
		    	String pass=password.getText().toString();
		    	mmat.memorisermdp();
		    	mmat.connexionauto();
				uck=new UserChecker(username,pass,ULR_LOGIN);
				//uck apel� dans progChecker
				ProgCheckerAsync chk=new ProgCheckerAsync(ConnexionActivity.this,auto_check);
				chk.execute();	
				**/
				
				
			}
		};
	    
	
	    
		
		public void jumpAct(Intent intent){
			startActivity(intent);
		}
	    
		 public String getUser(){
			 return user.getText().toString().trim();
		 }
		 public String getPassword(){
			 return password.getText().toString().trim();
		 }
		 
		

		    @Override
		    public boolean onOptionsItemSelected(MenuItem item) {
		        int itemId = item.getItemId();
				
				if(itemId == android.R.id.home)
				{
					Intent intent= new Intent(this,MainActivity.class);
					
					startActivity(intent);
					finish();
					return true;
				}
				else return false;
		    	
		    }
		    
		    
		    //utiliser pour envoyer une reponse a lactivit� appelante
			public void giveGoodResult() {
				setResult(RESULT_OK);
				finish();
			}
		
		
}
