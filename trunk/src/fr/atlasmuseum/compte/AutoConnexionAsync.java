package fr.atlasmuseum.compte;












import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import fr.atlasmuseum.R;
import fr.atlasmuseum.main.MainActivity;




/**
 * utiliser dans le main activity pour la connexion automatique
 * @author Expert
 *
 */
public class AutoConnexionAsync extends AsyncTask<String, String, Boolean> {
	private ProgressDialog mProgress;
	private final MainActivity mContext;
	//private Graffiti mGraffity; 
	String login;
	String mdp;
	
	private UserChecker uck;
	
	private static final String DEBUG_TAG = "Compte/ProgressBarChecker";

	public AutoConnexionAsync(MainActivity mainActivity, String login, String mdp) {
		//mGraffity = graffiti;
		mContext = mainActivity;
		this.mdp=mdp;
		this.login=login;
	}

	@Override
	protected void onPreExecute() {
		Log.d(DEBUG_TAG, "onPreExecute");
	    super.onPreExecute();

		mProgress = new ProgressDialog(mContext);
		mProgress.setMessage(mContext.getResources().getString(R.string.PB_UserChecker));
		mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgress.setCancelable(false);
		mProgress.show();
		
	}

	@Override
	protected Boolean doInBackground(String... args) {
		Log.i(DEBUG_TAG, "doInBackground");
		uck=new UserChecker(login, mdp,mContext.getResources().getString(R.string.url_login));
		
		if(uck.checkUser()){
			
			return true;
		}
		else 
			return false;
		
		
	}

	@Override
	protected void onPostExecute(Boolean result) {     
		Log.i(DEBUG_TAG, "onPostExecute");
		
		//Authentification.setisConnected(result);
		if( mProgress != null ) {
			mProgress.hide();
			mProgress.cancel();
		}
		if(result){
			Authentification.setisConnected(true);//mettre verrou true
			String name=Authentification.getUsername();
			Toast.makeText(mContext.getBaseContext(), mContext.getResources().getString(R.string.CONNECTED_AS)+" "+name, Toast.LENGTH_SHORT).show();
			mContext.recreate();
		
		}
		else{
			Authentification.setisConnected(false);//mettre verrou false
			//AtlasError.showErrorDialog(this.mContext, "3.1", "");
			Toast.makeText(mContext.getBaseContext(), mContext.getResources().getString(R.string.authentification_echouee), Toast.LENGTH_SHORT).show();
			mContext.cPref.edit().putBoolean("AUTO_ISCHECK", false).commit();
		}
		
		
	}

	
}

