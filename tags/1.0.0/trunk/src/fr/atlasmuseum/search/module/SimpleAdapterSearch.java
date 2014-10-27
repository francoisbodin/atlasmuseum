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
	 * SearchActivity et ListActivity
	 */
	public class SimpleAdapterSearch  extends BaseAdapter  {
		private static final String DEBUG_TAG = "SimpleAdapter";

		public Typeface font_regular;
		
		// Une liste des contributions
		private List<String> listElement;
		    	
		//Le contexte dans lequel est présent notre adapter
		private Context mContext;
		    	
		//Un mécanisme pour gérer l'affichage graphique depuis un layout XML
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
			TextView layoutItem;
			
			layoutItem = (TextView) mInflater.inflate(R.xml.my_item_list, parent, false);
			
			//(2) : Récupération des TextView de notre layout      
			//TextView titre_textv = (TextView)mInflater.inflate(R.xml.my_item_list, parent, false).findViewById(R.id.contrib_titre);
			layoutItem.setTypeface(font_regular);
			layoutItem.setText(listElement.get(position));
			
			
			return layoutItem;
		}	


		public SimpleAdapterSearch(Context context, List<String> alistN) {
			  mContext = context;
			  this.listElement = alistN;
			  mInflater = LayoutInflater.from(mContext);
			  font_regular = Typeface.createFromAsset(mContext.getAssets(), "RobotoCondensed-Regular.ttf");
			  
		}

		
	}
	 
