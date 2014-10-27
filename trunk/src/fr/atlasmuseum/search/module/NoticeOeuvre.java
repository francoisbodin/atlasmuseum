package fr.atlasmuseum.search.module;


import java.util.ArrayList;
/**
 * Classe qui décrit une notice
 * 
 * Utilisé dans ResultActivity
 */
public class NoticeOeuvre {
	
	private String titre ;
	private String auteur ;
	private int annee ;
	private int id;//id dans la BDD interne de l'application
	private String photo; //contient le nom de la photo
	private String pays;
	private String ville;
	
	public String getPays() {
		return pays;
	}

	public void setPays(String pays) {
		this.pays = pays;
	}

	public String getVille() {
		return ville;
	}

	public void setVille(String ville) {
		this.ville = ville;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}
	private int distance;//distance par rapport a l'utilisateur
	
	private Double longitude;
	private Double latitude;


	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public NoticeOeuvre(String titre, String auteur, int idxloc) {
		this.titre = titre;
		this.auteur = auteur;
		this.id= idxloc;
		this.ville="";
		this.pays="";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getAuteur() {
		return auteur;
	}

	public void setAuteur(String auteur) {
		this.auteur = auteur;
	}



	public int getAnnee() {
		return annee;
	}

	public void setAnnee(int annee) {
		this.annee = annee;
	}

	public int getDistance() {
		// TODO Auto-generated method stub
		return distance;
	}
	public void setDistance(int distance2) {
		// TODO Auto-generated method stub
		distance =distance2;
	}

}