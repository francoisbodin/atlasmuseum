package fr.atlasmuseum.account;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import fr.atlasmuseum.AtlasmuseumActivity;
import fr.atlasmuseum.R;
import fr.atlasmuseum.helper.AtlasError;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class ConnectionActivity extends Activity implements ConnectionAsync.ConnectionListener
{
	@SuppressWarnings("unused")
	private static final String DEBUG_TAG = "AtlasMuseum/ConnectionActivity";
	
	public static final String SHARED_PREFERENCES = "fr.atlasmuseum.account.ConnectionActivity.SHARED_PREFERENCES";
	public static final String PREF_KEY_USERNAME = "username";
	public static final String PREF_KEY_PASSWORD = "password";
	public static final String PREF_KEY_REMEMBER_ME = "remember_me";
	public static final String PREF_KEY_AUTO_LOGIN = "auto_login";
	//static final String PREF_KEY_CONNECTED = "connected";

	private SharedPreferences mPrefs;

	private EditText mTextUsername;
	private EditText mTextPassword;
	private Button mButtonLogin;
	private CheckBox mCheckboxRememberMe;
	private CheckBox mCheckboxAutoLogin;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connection_activity);

		mPrefs = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

		mCheckboxRememberMe = (CheckBox) findViewById(R.id.checkbox_remember_me);
		mCheckboxRememberMe.setChecked(mPrefs.getBoolean(PREF_KEY_REMEMBER_ME, false));

		mTextUsername = (EditText)findViewById(R.id.text_username);
		mTextPassword = (EditText)findViewById(R.id.text_password);

		if( mCheckboxRememberMe.isChecked() ) {
			mTextUsername.setText(mPrefs.getString(PREF_KEY_USERNAME, ""));
			mTextPassword.setText(mPrefs.getString(PREF_KEY_PASSWORD, ""));
		}

		mCheckboxAutoLogin = (CheckBox) findViewById(R.id.checkbox_auto_login);
		mCheckboxAutoLogin.setChecked(mPrefs.getBoolean(PREF_KEY_AUTO_LOGIN, false));

		mButtonLogin = (Button)findViewById(R.id.button_confirm);
		mButtonLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
	    		if( ! AtlasmuseumActivity.checkInternetConnection(ConnectionActivity.this)) {
	    			AtlasError.showErrorDialog(ConnectionActivity.this, "7.1", "pas de connexion internet");
	    			return;
	    		}
	    		String encodediduser = null;
	    		String encodedpwd = null;
	    		String iduser = mTextUsername.getText().toString().trim();
	    		String pwd = mTextPassword.getText().toString().trim();
	    		Log.d(DEBUG_TAG, "Connecting with id: \"" + iduser +"\"");
	    		Log.d(DEBUG_TAG, "Connecting with pwd: \"" + pwd +"\"");
	    		try {
	    			encodediduser = URLEncoder.encode(iduser,"UTF-8");
	    			encodedpwd = URLEncoder.encode(pwd,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					encodediduser = iduser;
					encodedpwd = pwd;
				}
	    		Log.d(DEBUG_TAG, "Connecting with encoded id: \"" + encodediduser +"\"");
	    		Log.d(DEBUG_TAG, "Connecting with encoded pwd: \"" + encodedpwd +"\"");
				ConnectionAsync connection = new ConnectionAsync(
						ConnectionActivity.this,
						encodediduser,
						encodedpwd
						);
				connection.execute();
			};
		});

		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
	}


	@Override
	public void onBackPressed() {
		mPrefs.edit().putString(PREF_KEY_USERNAME, "").commit();
        mPrefs.edit().putString(PREF_KEY_PASSWORD, "").commit();
        mPrefs.edit().putBoolean(PREF_KEY_REMEMBER_ME, false).commit();
        mPrefs.edit().putBoolean(PREF_KEY_AUTO_LOGIN, false).commit();
		setResult(RESULT_CANCELED);
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();

		if(itemId == android.R.id.home) {
			mPrefs.edit().putString(PREF_KEY_USERNAME, "").commit();
	        mPrefs.edit().putString(PREF_KEY_PASSWORD, "").commit();
	        mPrefs.edit().putBoolean(PREF_KEY_REMEMBER_ME, false).commit();
	        mPrefs.edit().putBoolean(PREF_KEY_AUTO_LOGIN, false).commit();
			setResult(RESULT_CANCELED);
			finish();
			return true;
		}
		return false;
	}


	@Override
	public void onConnectionOk() {
		mPrefs.edit().putString(PREF_KEY_USERNAME, mTextUsername.getText().toString().trim()).commit();
        mPrefs.edit().putString(PREF_KEY_PASSWORD, mTextPassword.getText().toString().trim()).commit();
        mPrefs.edit().putBoolean(PREF_KEY_REMEMBER_ME, mCheckboxRememberMe.isChecked()).commit();
        mPrefs.edit().putBoolean(PREF_KEY_AUTO_LOGIN, mCheckboxAutoLogin.isChecked()).commit();
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onConnectionFailed() {
	}
}
