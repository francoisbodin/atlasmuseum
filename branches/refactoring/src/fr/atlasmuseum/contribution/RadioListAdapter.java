package fr.atlasmuseum.contribution;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import fr.atlasmuseum.R;

public class RadioListAdapter extends BaseAdapter
{

	@SuppressWarnings("unused")
	private static final String DEBUG_TAG = "AtlasMuseum/RadioListAdapter";
	private  String[] listRadio;

	
	private Context mContext;
	String valueSelected;
	//Un mécanisme pour gérer l'affichage graphique depuis un layout XML
	private LayoutInflater mInflater;

	public ArrayList<ViewHolder> listView;
	
    private RadioButton mSelectedRB;
    private int mSelectedPosition = -1;
	
	public RadioListAdapter(Context context, String[] listElt) 
	{
		valueSelected="";//valeur selectionner par l'utilisateur
	   mContext = context;
	   listRadio = listElt;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder;
		if (convertView == null) 
		{
			convertView = mInflater.inflate(R.xml.list_radio_contrib, parent, false);
			 holder = new ViewHolder();

            holder.name = (TextView)convertView.findViewById(R.id.valeur);
            holder.radioBtn = (RadioButton)convertView.findViewById(R.id.radioButton1);

            convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.name.setText(listRadio[position]);
		
		
		
        holder.radioBtn.setOnClickListener(new OnClickListener() {


			@Override
            public void onClick(View v) {

                if(position != mSelectedPosition && mSelectedRB != null)
                {
                    mSelectedRB.setChecked(false);//decoche le bouton
                }

                mSelectedPosition = position;
                mSelectedRB = (RadioButton)v;
            }
        });


        if(mSelectedPosition != position){
            holder.radioBtn.setChecked(false);
        }else{
            holder.radioBtn.setChecked(true);
            if(mSelectedRB != null && holder.radioBtn != mSelectedRB){
                mSelectedRB = holder.radioBtn;
            }
        }




        holder.name.setText(listRadio[position]);


        return convertView;
	 
	}

	@Override
	public int getCount() {
		return listRadio.length;
	}
	
	@Override
	public Object getItem(int arg0) {
		return listRadio[arg0];
	}
	
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	

    private class ViewHolder{
        TextView        name;
        RadioButton     radioBtn;
    }
    
   public String getValue()
    {
    	if(mSelectedPosition != -1)
    	{
    		return listRadio[mSelectedPosition];
    	}
    	return "";
    }
}
