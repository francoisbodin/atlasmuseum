package fr.atlasmuseum.contribution;
import java.util.List;

import fr.atlasmuseum.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SavedContributionAdapter extends BaseAdapter {
	@SuppressWarnings("unused")
	private static final String DEBUG_TAG = "AtlasMuseum/ContributionPropertyAdapter";

	private Context mContext;
	private List<Contribution> mContributions;
	
	public Typeface mFontRegular;
	public Typeface mFontBold;
	
	private LayoutInflater mInflater;

	public SavedContributionAdapter(Context context, List<Contribution> contributions) {
		  mContext = context;
		  mContributions = contributions;
		  mInflater = LayoutInflater.from(mContext);
		  mFontRegular = Typeface.createFromAsset(mContext.getAssets(), "RobotoCondensed-Regular.ttf");
		  mFontBold = Typeface.createFromAsset(mContext.getAssets(), "RobotoCondensed-Bold.ttf");
	}

	@Override
	public int getCount() {
		return mContributions.size();
	}

	@Override
	public Object getItem(int i) {
		return mContributions.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layout = (LinearLayout) mInflater.inflate(R.xml.list_item_saved_contribution, parent, false);
		
		TextView textTitle = (TextView)layout.findViewById(R.id.saved_contribution_title);
		TextView textAuthor = (TextView)layout.findViewById(R.id.saved_contribution_author);
		TextView textDate = (TextView)layout.findViewById(R.id.saved_contribution_date);
		
		textTitle.setTypeface(mFontBold);
		textAuthor.setTypeface(mFontRegular);
		textDate.setTypeface(mFontRegular);
		
		Contribution contribution = mContributions.get(position);

		String title = contribution.getProperty(Contribution.TITRE).getValue();
		if( title.equals("") ) {
			title = contribution.getProperty(Contribution.TITRE).getDefaultValue();
		}
		textTitle.setText(title);

		String author = contribution.getProperty(Contribution.ARTISTE).getValue();
		if( author.equals("") ) {
			author = contribution.getProperty(Contribution.ARTISTE).getDefaultValue();
		}
		textAuthor.setText(author);
		
		textDate.setText(contribution.getDate() + " " + contribution.getTime());
		
		return layout;
	}	

}
