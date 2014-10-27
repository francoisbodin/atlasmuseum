package fr.atlasmuseum.contribution;

//classe qui gere l'affichage dans la vue contribution list
public class ContribElementList {
	
	private String mChampAModifier; // ex: ajout_titre, ou modif_titre
	private String mTitre;           // ex:Titre
	private String mValue;           // nouvelle valeur donnée par l'utilisateur
	private String mOriginalValue;   // valeur issue de la notice de référence
	
	
	public String getTitre() {
		return mTitre;
	}
	public void setTitre(String titre) {
		this.mTitre = titre;
	}
	public String getChampAModifier() {
		return mChampAModifier;
	}
	public void setChampAModifier(String champsAModifier) {
		mChampAModifier = champsAModifier;
	}
	public String getValue() {
		return mValue;
	}
	public void setValue(String value) {
		this.mValue = value;
	}
	public String getOriginalValue() {
		return mOriginalValue;
	}
	public void setOriginalValue(String orginalValue) {
		this.mOriginalValue = orginalValue;
	}
	
	public ContribElementList()
	{
		mTitre = "";
		mChampAModifier = "";
		mValue = "";
		mOriginalValue = "";
	}
}
