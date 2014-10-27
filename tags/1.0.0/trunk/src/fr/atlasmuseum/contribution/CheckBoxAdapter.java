package fr.atlasmuseum.contribution;
 
import java.util.ArrayList;
 




import java.util.Collection;

import fr.atlasmuseum.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Utilisé dans ModifActivity pour gérer la vue avec checkBox listView
 *
 */
public  class CheckBoxAdapter extends BaseAdapter 
{
	private static final String DEBUG_TAG = "CheckBoxAdapter";
	private ArrayList<CheckBoxElt> listCheckBox;
	private Context mContext;
	private LinearLayout layoutItem;
	//Un mécanisme pour gérer l'affichage graphique depuis un layout XML
	private LayoutInflater mInflater;
	
	public CheckBoxAdapter(Context context, ArrayList<CheckBoxElt> l) 
	{
	   mContext = context;
		listCheckBox=l;
		mInflater = LayoutInflater.from(mContext);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		RelativeLayout layoutItem;
		//(1) : Réutilisation des layouts
		/**if (convertView == null) 
		{**/
			//Initialisation de notre item à partir du  layout XML "personne_layout.xml"
			layoutItem = (RelativeLayout) mInflater.inflate(R.xml.list_checkbox_contrib, parent, false);
		/**}
		else 
		{
			layoutItem = (RelativeLayout) convertView;
		}
		**/
		TextView valeurtxt = (TextView) layoutItem.findViewById(R.id.valeur);
		final CheckBox checkb = (CheckBox) layoutItem.findViewById(R.id.checkBox1);
		
		valeurtxt.setText(listCheckBox.get(position).getValue());
		checkb.setChecked(listCheckBox.get(position).getBool());
		if(checkb.isChecked())
		{
			
			Log.d(DEBUG_TAG	, listCheckBox.get(position).getValue()+"is checked");
		}
		checkb.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(checkb.isChecked()){
                	listCheckBox.get(position).setBool(true);
                    System.out.println("Checked");
                    Log.d(DEBUG_TAG, listCheckBox.get(position).getValue()+" was checked");
                }else{
                	listCheckBox.get(position).setBool(false);
                    Log.d(DEBUG_TAG, listCheckBox.get(position).getValue()+" was unchecked");
                }
            }
        });
		   
		
		
		return layoutItem;
	 
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listCheckBox.size();
	}
	
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return listCheckBox.get(arg0);
	}
	
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
}
 