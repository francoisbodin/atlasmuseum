package fr.atlasmuseum.search.module;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fr.atlasmuseum.R;
import fr.atlasmuseum.search.SearchActivity;
import fr.atlasmuseum.search.ShowNoticeActivity;
import fr.atlasmuseum.search.loadPhotoInterface;

/**
 * Permet l'affichage de notice dans la listView
 * sans affichage d'images
 */
@SuppressLint("DefaultLocale") public class NoticeAdapterWithDistance extends BaseAdapter  {
	private static final String DEBUG_TAG = "NoticeAdapterWithDistance";
	public Typeface font_bold;
	public Typeface font_regular;
	public Typeface font_light;
	
	// Une liste de personnes
	private List<NoticeOeuvre> listNotice;
	    	
	//Le contexte dans lequel est présent notre adapter
	private loadPhotoInterface mContext;
	    	
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
	public View getView( final int position, View convertView, ViewGroup parent) {
		LinearLayout layoutItem;
		//(1) : Réutilisation des layouts
		if (convertView == null) {
			//Initialisation de notre item à partir du  layout XML "personne_layout.xml"
			layoutItem = (LinearLayout) mInflater.inflate(R.xml.list_item_search_autour, parent, false);
		} else 
		{
			layoutItem = (LinearLayout) convertView;
		}
	  
		//(2) : Récupération des TextView de notre layout      
		TextView titre = (TextView)layoutItem.findViewById(R.id.notice_Titre);
		TextView auteur = (TextView)layoutItem.findViewById(R.id.notice_auteur);
		TextView annee = (TextView)layoutItem.findViewById(R.id.notice_annee);
		TextView distance = (TextView)layoutItem.findViewById(R.id.notice_distance);
		final ImageView img = (ImageView) layoutItem.findViewById(R.id.icon);
		TextView ville_pays = (TextView) layoutItem.findViewById(R.id.notice_ville);
		LinearLayout champs_clickable= (LinearLayout) layoutItem.findViewById(R.id.champs_clickable);
		//(3) : Renseignement des valeurs
		int dist = listNotice.get(position).getDistance();
		
		if(dist <0)//pour affichage de resultat sans distance
		{
			distance.setVisibility(View.GONE);
		}
		else
		{
			if(dist < 1000)
			{
				distance.setText(dist+" m");
			}
			else
			{
				double distkm = dist/1000.0;
				String val =String.format("%.1f", distkm);
				distance.setText(val+" km");
			}
		}
		//onClick sur RelativLayout
	    
		champs_clickable.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            	
        		Bundle bundle = new Bundle();
        		int i = position;
        		int idfrag = listNotice.get(i).getId();
        		Log.d(DEBUG_TAG, "**** idBDD = "+ idfrag);
        		bundle.putInt(ShowNoticeActivity.ARG_FRAGMENT,idfrag );//idfrag, c'est l'id dans la BDD
        		Intent intent = new Intent(mContext.getContext(), ShowNoticeActivity.class);
        		intent.putExtras(bundle);
        		mContext.getContext().startActivity(intent);
            }
        });
        
		/****/
		
		distance.setTypeface(font_bold);
		
		
		titre.setText(listNotice.get(position).getTitre());
		titre.setTypeface(font_regular);
		
		
		auteur.setText(listNotice.get(position).getAuteur());
		auteur.setTypeface(font_light);
		annee.setText(String.valueOf(listNotice.get(position).getAnnee()));
		annee.setTypeface(font_light);
		
		String ville = listNotice.get(position).getVille() ;
		String pays = listNotice.get(position).getPays() ;
		ville_pays.setText(ville+" - "+pays);
		ville_pays.setTypeface(font_light);
		
		String photoNotice=listNotice.get(position).getPhoto();//recupere le nom de la photo
		
		File image;
		Drawable imgic = (Drawable)mContext.getContext().getApplicationContext().getResources().getDrawable(R.drawable.ic_load_thumb);
		
		try 
		{
			image = SearchActivity.checkIfImageFileExists("thumb_"+photoNotice);
			/* Decode the JPEG file into a Bitmap */
			if(image != null)
			{
				Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
				img.setImageBitmap(bitmap);
				Log.d(DEBUG_TAG, "photoname ="+photoNotice);
				Log.d(DEBUG_TAG, "setImagebitmap");
				img.setOnClickListener(null);
			}
			
			else
			{
				img.setImageDrawable(imgic);
				img.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// appel a une méthode pour charger la photo
						Log.d(DEBUG_TAG, "Vous avez cliqué sur l'image....");
						LoadAPhotoAsync upl = new LoadAPhotoAsync(mContext,"thumb_"+listNotice.get(position).getPhoto(), img);
						upl.execute();
					}

					
				});
				
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
	
		//On retourne l'item créé.
		return layoutItem;
	}	

	


	public NoticeAdapterWithDistance(loadPhotoInterface context, List<NoticeOeuvre> alistN) {
		  mContext = context;
		  this.listNotice = alistN;
		  mInflater = LayoutInflater.from(mContext.getContext());
		  
		  font_light = Typeface.createFromAsset(mContext.getContext().getAssets(), "RobotoCondensed-Light.ttf");
		  font_regular = Typeface.createFromAsset(mContext.getContext().getAssets(), "RobotoCondensed-Regular.ttf");
		  font_bold = Typeface.createFromAsset(mContext.getContext().getAssets(), "RobotoCondensed-Bold.ttf");
	}


	

}
 
