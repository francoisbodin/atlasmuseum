package fr.atlasmuseum.compte;

import java.util.HashMap;

import android.util.Log;
/**
 * Gere le statut de l'utilisateur dans l'application
 * Permet de récuperer aussi les informations de l'utilisateur
 * @author Expert
 *
 */
public class Authentification {

	private static boolean isConnected=false; 
	private static HashMap<String, String> infoUser =new HashMap<String, String>();
	private static final String Debug_TAG="atlasmuseum/Authentification";

	public static boolean getisConnected(){
		Log.d(Debug_TAG, "Connexion? "+isConnected+".");
		return isConnected;
	}
	
	/**
	 * Set the user status, connected or not.
	 * @param b
	 */
	public synchronized static void setisConnected(boolean b){
		
		isConnected=b;
		System.out.println("Auto set to:"+isConnected);
		
		if(!b) infoUser.clear();
	}
	public static HashMap<String, String> getinfoUser(){
		if (isConnected)
			return infoUser;
		else {
			infoUser.clear();
			return infoUser;
		
		}
	}
	
	public static String getUsername(){
		if(isConnected)
			return infoUser.get("username");
		else
			return "Unkown User";
	}
	
	public static void setUsername(String name)
	{
		infoUser.put("username", name);
	}
	public static void setPassword(String name)
	{
		infoUser.put("password", name);
	}
	public static String getPassword(){
		if(isConnected)
			return infoUser.get("password");
		else
			return "Unkown User";
	}
	
	public static void Deconnexion(){
		setisConnected(false);
		infoUser.clear();
	}
}
