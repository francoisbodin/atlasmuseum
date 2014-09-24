package fr.atlasmuseum.contribution;
 
import java.util.ArrayList;
 




import fr.atlasmuseum.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Utilisé dans ModifActivity pour gérer la vue avec checkBox listView
 *
 */
public  class CheckBoxAdapter extends BaseAdapter 
{
	private static final String DEBUG_TAG = "CheckBoxAdapter";
	private ArrayList<CheckBoxElt> listCheckBox;
	private Context mContext;
	//Un mécanisme pour gérer l'affichage graphique depuis un layout XML
	private LayoutInflater mInflater;
	
	public CheckBoxAdapter(Context context, ArrayList<CheckBoxElt> l) 
	{
	   mContext = context;
		listCheckBox=l;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		RelativeLayout layoutItem;
		layoutItem = (RelativeLayout) mInflater.inflate(R.xml.list_checkbox_contrib, parent, false);
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
		return listCheckBox.size();
	}
	
	@Override
	public Object getItem(int arg0) {
		return listCheckBox.get(arg0);
	}
	
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
}
 