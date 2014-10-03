package fr.atlasmuseum.search.module;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.atlasmuseum.R;
import fr.atlasmuseum.contribution.Contribution;

@SuppressLint("DefaultLocale")
public class NoticeAdapter extends BaseAdapter {

    private class ViewHolder {
    	TextView textTitle;
    	TextView textAuthor;
    	TextView textYear;
    	TextView textCity;
    	TextView textDistance;
    	ImageView viewThumb;
    }

	@SuppressWarnings("unused")
	private static final String DEBUG_TAG = "AtlasMuseum/NoticeAdapter";
	
	private Activity mContext;
	private LayoutInflater mInflater;
	private List<Contribution> mNotices;
	
	public Typeface mFontBold;
	public Typeface mFontRegular;
	public Typeface mFontLight;
	
	public NoticeAdapter(Activity context, List<Contribution> alistN) {
		mContext = context;
		mNotices = alistN;
		mInflater = LayoutInflater.from(mContext);
		mFontLight = Typeface.createFromAsset(mContext.getAssets(), "RobotoCondensed-Light.ttf");
		mFontRegular = Typeface.createFromAsset(mContext.getAssets(), "RobotoCondensed-Regular.ttf");
		mFontBold = Typeface.createFromAsset(mContext.getAssets(), "RobotoCondensed-Bold.ttf");
	}

	@Override
	public View getView( final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		Contribution contribution = mNotices.get(position);
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.xml.list_item_search_autour, parent, false);
			holder = new ViewHolder();
            
    		holder.textTitle = (TextView)convertView.findViewById(R.id.text_title);
    		holder.textAuthor = (TextView)convertView.findViewById(R.id.text_author);
    		holder.textYear = (TextView)convertView.findViewById(R.id.text_year);
    		holder.textCity = (TextView) convertView.findViewById(R.id.text_city);
            holder.textDistance = (TextView)convertView.findViewById(R.id.text_distance);
    		holder.viewThumb = (ImageView) convertView.findViewById(R.id.view_thumb);

            convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}

		holder.textTitle.setTypeface(mFontRegular);
		holder.textTitle.setText(contribution.getProperty(Contribution.TITRE).getValue());
		
		holder.textAuthor.setTypeface(mFontLight);
		holder.textAuthor.setText(contribution.getProperty(Contribution.ARTISTE).getValue());
		
		holder.textYear.setTypeface(mFontLight);
		holder.textYear.setText(contribution.getProperty(Contribution.DATE_INAUGURATION).getValue());
		
		holder.textCity.setTypeface(mFontLight);
		holder.textCity.setText(contribution.getProperty(Contribution.VILLE).getValue() + " - " + contribution.getProperty(Contribution.PAYS).getValue());
		
        holder.textDistance.setTypeface(mFontBold);
		int dist = contribution.getDistance();
		if(dist <0)	{
			holder.textDistance.setVisibility(View.GONE);
		}
		else {
			if(dist < 1000)	{
				holder.textDistance.setText(dist + " m");
			}
			else {
				holder.textDistance.setText(String.format("%.1f", dist/1000.0) + " km");
			}
		}

		String filename = contribution.getProperty(Contribution.PHOTO).getValue();
		if( ! filename.equals("") ) {
			File file = new File(filename);
			File thumbFile = new File( Contribution.getPhotoDir(), "thumb_" + file.getName() );
			if( thumbFile.exists() ) {
				Bitmap bitmap = BitmapFactory.decodeFile(thumbFile.getAbsolutePath());
				holder.viewThumb.setImageBitmap(bitmap);
				holder.viewThumb.setOnClickListener(null);
			}
			else {
				Drawable imgic = (Drawable)mContext.getApplicationContext().getResources().getDrawable(R.drawable.ic_load_thumb);
				holder.viewThumb.setImageDrawable(imgic);
				holder.viewThumb.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						mNotices.get(position).loadThumb(mContext);
					}
				});
			}
		}
		else {
			// TODO: clear the picture if no picture in notice
		}

		return convertView;
	}	

	@Override
	public int getCount() {
		return mNotices.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mNotices.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
}
 
