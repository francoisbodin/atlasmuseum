package fr.atlasmuseum.search.module;

import java.util.Comparator;


/**
 * classe qui permet de ranger les notices, suivant distance ou par ordre alphabetique suivant diff�rent keys
 * 
 *
 */
public class NoticeCompar implements Comparable<NoticeCompar>{
	


	private int index; //index de l'oeuvre
	private NoticeOeuvre oeuvre;//va contenir le nom de l'artiste, nb distance
	
	
	public NoticeCompar(NoticeOeuvre notice, int j) {
		this.index=j;
		this.oeuvre=notice;
	}



	public int getIndex() {
		return index;
	}



	public void setIndex(int index) {
		this.index = index;
	}



	public NoticeOeuvre getOeuvre() {
		return oeuvre;
	}



	public void setOeuvre(NoticeOeuvre oeuvre) {
		this.oeuvre = oeuvre;
	}


	//compare les titres des NoticeCompar
	@Override
	public int compareTo(NoticeCompar nComp) {
		return (int) (getOeuvre().getDistance() - nComp.getOeuvre().getDistance());
		
	}
	

	public static Comparator<NoticeCompar> NoticeDateComparator 
                          = new Comparator<NoticeCompar>() {
 
	    public int compare(NoticeCompar n1, NoticeCompar n2) {
 
	      //ascending order
	      return n1.getOeuvre().getAnnee() -n2.getOeuvre().getAnnee();
 
	      //descending order
	      //return fruitName2.compareTo(fruitName1);
	    }
	};

}
