package fr.atlasmuseum.search.module;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fr.atlasmuseum.R;
import fr.atlasmuseum.contribution.ContribElementList;
import fr.atlasmuseum.main.MainActivity;
import fr.atlasmuseum.search.SearchActivity;

/**
 * Permet l'affichage de notice dans la listView
 * sans affichage d'images
 */
public class ContribListAdapter extends BaseAdapter  {
	private static final String DEBUG_TAG = "ContribListAdapter";

	public Typeface font_regular;
	public Typeface font_bold;
	
	// Une liste des contributions
	private List<ContribElementList> listElement;
	    	
	//Le contexte dans lequel est pr�sent notre adapter
	private Context mContext;
	    	
	//Un m�canisme pour g�rer l'affichage graphique depuis un layout XML
	private LayoutInflater mInflater;
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listElement.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return listElement.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layoutItem;
		//(1) : R�utilisation des layouts
		//if (convertView == null) {
			//Initialisation de notre item � partir du  layout XML "personne_layout.xml"
			layoutItem = (LinearLayout) mInflater.inflate(R.xml.my_item_list_contrib, parent, false);
		/**} else 
		{
			layoutItem = (LinearLayout) convertView;
		}**/
	  
		//(2) : R�cup�ration des TextView de notre layout      
		TextView titre_textv = (TextView)layoutItem.findViewById(R.id.contrib_titre);
		TextView value_textv = (TextView)layoutItem.findViewById(R.id.contrib_value);
		
		titre_textv.setTypeface(font_regular);
		value_textv.setTypeface(font_regular);
		
		ContribElementList elt = listElement.get(position);//element courant de la liste
		titre_textv.setText(elt.getTitre());
		if(elt.getValue().equals(""))
		
		{
			if(elt.getOldValue()==null || elt.getOldValue().equals(""))
			{
				value_textv.setVisibility(View.GONE);
			}
			else
			{
				value_textv.setText(elt.getOldValue());
			}
		}
		else
		{
			titre_textv.setText(titre_textv.getText().toString()+"*");
			value_textv.setText(elt.getValue());
			value_textv.setTextColor(Color.parseColor("#0EBABA"));
			
		}
		
		
		return layoutItem;
	}	


	public ContribListAdapter(Context context, List<ContribElementList> alistN) {
		  mContext = context;
		  this.listElement = alistN;
		  mInflater = LayoutInflater.from(mContext);
		  font_regular = Typeface.createFromAsset(mContext.getAssets(), "RobotoCondensed-Regular.ttf");
		  font_bold = Typeface.createFromAsset(mContext.getAssets(), "RobotoCondensed-Bold.ttf");
	}

	
}
 
