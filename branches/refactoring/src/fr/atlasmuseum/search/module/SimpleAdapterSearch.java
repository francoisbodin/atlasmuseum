package fr.atlasmuseum.search.module;


	import java.util.List;

	import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fr.atlasmuseum.R;

	/**
	 * Permet l'affichage de notice dans la listView
	 * sans affichage d'images
	 * SearchActivity et ListActivity
	 */
	public class SimpleAdapterSearch  extends BaseAdapter  {
		@SuppressWarnings("unused")
		private static final String DEBUG_TAG = "AltasMuseum/SimpleAdapterSearch";

		public Typeface font_regular;
		
		// Une liste des contributions
		private List<String> listElement;
		    	
		//Le contexte dans lequel est pr�sent notre adapter
		private Context mContext;
		    	
		//Un m�canisme pour g�rer l'affichage graphique depuis un layout XML
		private LayoutInflater mInflater;
		
		@Override
		public int getCount() {
			return listElement.size();
		}

		@Override
		public Object getItem(int arg0) {
			return listElement.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView layoutItem;
			
			layoutItem = (TextView) mInflater.inflate(R.xml.my_item_list, parent, false);
			
			//(2) : R�cup�ration des TextView de notre layout      
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
	 
