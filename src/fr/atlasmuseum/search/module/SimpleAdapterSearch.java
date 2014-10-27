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

public class SimpleAdapterSearch  extends BaseAdapter  {
	
    private class ViewHolder {
    	TextView text;
    }

	@SuppressWarnings("unused")
	private static final String DEBUG_TAG = "AltasMuseum/SimpleAdapterSearch";

	private Context mContext;
	private LayoutInflater mInflater;
	private List<String> mList;
	public Typeface mFontRegular;

	public SimpleAdapterSearch(Context context, List<String> alistN) {
		mContext = context;
		mList = alistN;
		mInflater = LayoutInflater.from(mContext);
		mFontRegular = Typeface.createFromAsset(mContext.getAssets(), "RobotoCondensed-Regular.ttf");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.xml.my_item_list, parent, false);
			holder = new ViewHolder();
            holder.text = (TextView)convertView.findViewById(R.id.text);
    		convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}

		holder.text.setTypeface(mFontRegular);
		holder.text.setText(mList.get(position));

		return convertView;
	}	

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}



}

