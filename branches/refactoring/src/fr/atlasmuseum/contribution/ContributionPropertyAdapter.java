package fr.atlasmuseum.contribution;

import java.util.List;

import fr.atlasmuseum.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContributionPropertyAdapter extends BaseAdapter {
	@SuppressWarnings("unused")
	private static final String DEBUG_TAG = "AtlasMuseum/ContributionPropertyAdapter";

	public Typeface mFontRegular;
	public Typeface mFontBold;
	
	// Une liste des contributions
	private List<ContributionProperty> mListElement;
	    	
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
		TextView titleView = (TextView)layoutItem.findViewById(R.id.contrib_titre);
		TextView valueView = (TextView)layoutItem.findViewById(R.id.contrib_value);
		
		titleView.setTypeface(mFontRegular);
		valueView.setTypeface(mFontRegular);
		
		ContributionProperty elt = mListElement.get(position);//élément courant de la liste

		String title = mContext.getResources().getString(elt.getTitle());
		if( elt.getValue() != elt.getOriginalValue() ) {
			title += "*";
			valueView.setTextColor(Color.parseColor("#0EBABA"));				
		}
		titleView.setText(title);
		
		if(elt.getValue().equals("")) {
			valueView.setVisibility(View.GONE);
		}
		else {
			valueView.setText(elt.getValue());
		}
		
		return layoutItem;
	}	


	public ContributionPropertyAdapter(Context context, List<ContributionProperty> alistN) {
		  mContext = context;
		  mListElement = alistN;
		  mInflater = LayoutInflater.from(mContext);
		  mFontRegular = Typeface.createFromAsset(mContext.getAssets(), "RobotoCondensed-Regular.ttf");
		  mFontBold = Typeface.createFromAsset(mContext.getAssets(), "RobotoCondensed-Bold.ttf");
	}

	
}
