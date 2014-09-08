package fr.atlasmuseum.contribution;

//classe qui gere l'affichage dans la vue contribution list
public class ContribElementList {
	
	String ChampsAModifier; //ex: ajout_titre, ou modif_titre
	String titre;//ex:Titre
	String value; //nouvelle valeur donnée par l'utilisateur
	String oldValue; //valeur issu de la notice de référence
	
	
	public String getTitre() {
		return titre;
	}
	public void setTitre(String titre) {
		this.titre = titre;
	}
	public String getChampsAModifier() {
		return ChampsAModifier;
	}
	public void setChampsAModifier(String champsAModifier) {
		ChampsAModifier = champsAModifier;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	
	public ContribElementList()
	{
		titre="";
		ChampsAModifier ="";
		value ="";
		oldValue="";
	}
}
