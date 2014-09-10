package fr.atlasmuseum.search.module;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import fr.atlasmuseum.R;
import fr.atlasmuseum.contribution.ContribElementList;

/**
 * Permet l'affichage de notice dans la listView
 * sans affichage d'images
 */
public class ContribListAdapter extends BaseAdapter  {
	@SuppressWarnings("unused")
	private static final String DEBUG_TAG = "AtlasMuseum/ContribListAdapter";

	public Typeface mFontRegular;
	public Typeface mFontBold;
	
	// Une liste des contributions
	private List<ContribElementList> mListElement;
	    	
	//Le contexte dans lequel est présent notre adapter
	private Context mContext;
	    	
	//Un mécanisme pour gérer l'affichage graphique depuis un layout XML
	private LayoutInflater mInflater;
	
	@Override
	public int getCount() {
		return mListElement.size();
	}

	@Override
	public Object getItem(int i) {
		return mListElement.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layoutItem;
		//(1) : Réutilisation des layouts
		layoutItem = (LinearLayout) mInflater.inflate(R.xml.my_item_list_contrib, parent, false);
		
		//(2) : Récupération des TextView de notre layout      
		TextView titre_textv = (TextView)layoutItem.findViewById(R.id.contrib_titre);
		TextView value_textv = (TextView)layoutItem.findViewById(R.id.contrib_value);
		
		titre_textv.setTypeface(mFontRegular);
		value_textv.setTypeface(mFontRegular);
		
		ContribElementList elt = mListElement.get(position);//élément courant de la liste
		titre_textv.setText(elt.getTitre());
		if(elt.getValue().equals(""))
		{
			if(elt.getOriginalValue()==null || elt.getOriginalValue().equals(""))
			{
				value_textv.setVisibility(View.GONE);
			}
			else
			{
				value_textv.setText(elt.getOriginalValue());
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
		  mListElement = alistN;
		  mInflater = LayoutInflater.from(mContext);
		  mFontRegular = Typeface.createFromAsset(mContext.getAssets(), "RobotoCondensed-Regular.ttf");
		  mFontBold = Typeface.createFromAsset(mContext.getAssets(), "RobotoCondensed-Bold.ttf");
	}

	
}
 
