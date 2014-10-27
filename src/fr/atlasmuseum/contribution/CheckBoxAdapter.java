package fr.atlasmuseum.contribution;
 
import java.util.ArrayList;
 
import fr.atlasmuseum.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public  class CheckBoxAdapter extends BaseAdapter  {
	
    private class ViewHolder {
        TextView name;
        CheckBox checkbox;
    }

	@SuppressWarnings("unused")
	private static final String DEBUG_TAG = "AtlasMuseum/CheckBoxAdapter";

	private ArrayList<CheckBoxListElement> mListCheckbox;
	private Context mContext;
	private LayoutInflater mInflater;
	
	public CheckBoxAdapter(Context context, ArrayList<CheckBoxListElement> l) {
		mContext = context;
		mListCheckbox = l;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.xml.item_checkbox_list, parent, false);
			holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.name);
            holder.checkbox = (CheckBox)convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}

		holder.name.setText(mListCheckbox.get(position).getValue());
		holder.checkbox.setChecked(mListCheckbox.get(position).getBool());
		holder.checkbox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	CheckBox checkbox = (CheckBox)v;
                if(checkbox.isChecked()) {
                	mListCheckbox.get(position).setBool(true);
                }
                else {
                	mListCheckbox.get(position).setBool(false);
                }
            }
        });
		
		return convertView;
	}
	
	@Override
	public int getCount() {
		return mListCheckbox.size();
	}
	
	@Override
	public Object getItem(int arg0) {
		return mListCheckbox.get(arg0);
	}
	
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
}
 