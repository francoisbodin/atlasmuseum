package fr.atlasmuseum.search.module;

import java.io.File;
import java.io.IOException;
import java.util.List;

import fr.atlasmuseum.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Permet l'affichage de notice dans la listView
 * Avec affichage d'images (TODO)
 *Utilisé dans ResulActivity
 */
public class NoticeAdapter extends BaseAdapter  {
	// Une liste de personnes
	private List<NoticeOeuvre> listNotice;
	private static final String ATLASMUSEUM_ALBUM = "atlasmuseum"; //TODO FBO to go in one file...
	public static final String ATLASMUSEUM_IMAGE_SUFFIX = ".jpg";

	//Le contexte dans lequel est pr�sent notre adapter
	private Context mContext;
	    	
	//Un mécanisme pour gérer l'affichage graphique depuis un layout XML
	private LayoutInflater mInflater;
	
	@Override
	public int getCount() {
		return listNotice.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listNotice.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layoutItem;
		//(1) : Réutilisation des layouts
		if (convertView == null) {
			//Initialisation de notre item � partir du  layout XML "personne_layout.xml"
			layoutItem = (LinearLayout) mInflater.inflate(R.xml.my_item_result_list, parent, false);
		} else 
		{
			layoutItem = (LinearLayout) convertView;
		}
	  
		//(2) : R�cup�ration des TextView de notre layout      
		TextView titre = (TextView)layoutItem.findViewById(R.id.notice_Titre);
		TextView auteur = (TextView)layoutItem.findViewById(R.id.notice_auteur);
		TextView annee = (TextView)layoutItem.findViewById(R.id.notice_annee);
		ImageView imgview= (ImageView) layoutItem.findViewById(R.id.icon);
		
		//(3) : Renseignement des valeurs       
		titre.setText(listNotice.get(position).getTitre());
		auteur.setText(listNotice.get(position).getAuteur());
		annee.setText(listNotice.get(position).getAnnee());
		String photoName = listNotice.get(position).getPhoto();
		File fimage;
		try {
			fimage = checkIfImageFileExists(photoName);
			String dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
			String photopath = dcimDir + "/" + ATLASMUSEUM_ALBUM + "/" + photoName + ATLASMUSEUM_IMAGE_SUFFIX;
			if (fimage != null)
			{
				Bitmap bm = BitmapFactory.decodeFile(photopath);
				imgview.setImageBitmap(bm);
			}
			else
			{
				Log.d("listPhoto", "file img null: "+photopath+listNotice.get(position).getPhoto());
			}
		} catch (Exception e) {
			Log.d("listPhoto", "file img does not exist: "+listNotice.get(position).getPhoto());

		}
		
		

		//On retourne l'item créé.
		return layoutItem;
	}	
	//utiliser dans ObjectFragmentActivity
	public static File checkIfImageFileExists(String filename) throws IOException {
		String dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
		File imageF = new File(dcimDir + "/" + ATLASMUSEUM_ALBUM + "/" +filename);
		if (imageF.exists()) return imageF;
		else return null;
	}
	
	public NoticeAdapter(Context context, List<NoticeOeuvre> alistN) {
		  mContext = context;
		  this.listNotice = alistN;
		  mInflater = LayoutInflater.from(mContext);
	}

	
}

