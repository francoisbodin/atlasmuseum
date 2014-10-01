package fr.atlasmuseum.contribution;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import fr.atlasmuseum.R;

public class RadioListAdapter extends BaseAdapter {

    private class ViewHolder {
        TextView name;
        RadioButton button;
    }
    
	@SuppressWarnings("unused")
	private static final String DEBUG_TAG = "AtlasMuseum/RadioListAdapter";
	
	private Context mContext;
	private LayoutInflater mInflater;
	private String[] mListElement;
	private RadioButton mLastButton;
    private int mLastPosition = -1;
	
	public RadioListAdapter(Context context, String[] listElement) {
		mContext = context;
		mListElement = listElement;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.xml.item_radio_list, parent, false);
			holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.name);
            holder.button = (RadioButton)convertView.findViewById(R.id.button);
            convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		holder.name.setText(mListElement[position]);
        holder.button.setOnClickListener(new OnClickListener() {
			@Override
            public void onClick(View v) {
                if(position != mLastPosition && mLastButton != null) {
                    mLastButton.setChecked(false);
                }
                mLastPosition = position;
                mLastButton = (RadioButton)v;
            }
        });

        if(mLastPosition != position) {
            holder.button.setChecked(false);
        }
        else {
            holder.button.setChecked(true);
            if(mLastButton != null && holder.button != mLastButton) {
                mLastButton = holder.button;
            }
        }

        holder.name.setText(mListElement[position]);

        return convertView;
	}

	@Override
	public int getCount() {
		return mListElement.length;
	}
	
	@Override
	public Object getItem(int arg0) {
		return mListElement[arg0];
	}
	
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	public String getValue() {
    	if(mLastPosition != -1)	{
    		return mListElement[mLastPosition];
    	}
    	return "";
    }
}
