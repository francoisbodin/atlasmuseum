package fr.atlasmuseum.contribution;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import fr.atlasmuseum.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class SavedContributionsActivity extends Activity {
	
	private static final String DEBUG_TAG = "AtlasMuseum/SavedContributionActivity";

	static final int REQUEST_EDIT_CONTRIBUTION = 1;

	SavedContributionAdapter mAdapterNew;
	SavedContributionAdapter mAdapterModif;

	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_saved_contributions);
        Log.d(DEBUG_TAG, "onCreate()");
        
		File saveDir = new File( Contribution.getSaveDir(this) );

		// Handle new notices
		FilenameFilter newFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("new_");
			}
		};
		String[] newNoticeFilenames = saveDir.list(newFilter);

		ListView listViewNew = (ListView) findViewById(R.id.list_new_notice);
		TextView textViewNew = (TextView) findViewById(R.id.text_new_notice);
		View lineViewNew = (View) findViewById(R.id.line_new_notice);

		if( newNoticeFilenames.length != 0 ) {
			List<Contribution> list = new ArrayList<Contribution>();
			
			mAdapterNew = new SavedContributionAdapter(this, list);
			listViewNew.setAdapter(mAdapterNew);
			listViewNew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id)
				{
					Contribution contribution = (Contribution) mAdapterNew.getItem(position);
					Bundle bundle = new Bundle();
					bundle.putSerializable("contribution", contribution);
					Intent intent = new Intent(SavedContributionsActivity.this, ListChampsNoticeModif.class);
					intent.putExtras(bundle);
					startActivityForResult(intent, REQUEST_EDIT_CONTRIBUTION);
				}
			});	

			for( String filename: newNoticeFilenames ) {
				Contribution contribution = Contribution.restoreFromFile(new File(saveDir, filename).getAbsolutePath());
				if( contribution != null ) {
					list.add(contribution);
				}
			}
		}
		else {
			listViewNew.setVisibility(View.GONE);
			textViewNew.setVisibility(View.GONE);
			lineViewNew.setVisibility(View.GONE);
		}

		
		// Handle modified notices
		FilenameFilter modifiedFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("modif_");
			}
		};
		String[] modifiedNoticeFilenames = saveDir.list(modifiedFilter);

		ListView listViewModif = (ListView) findViewById(R.id.list_modif_notice);
		TextView textViewModif = (TextView) findViewById(R.id.text_modif_notice);
		View lineViewModif = (View) findViewById(R.id.line_modif_notice);

		if( modifiedNoticeFilenames.length != 0 ) {
			List<Contribution> list = new ArrayList<Contribution>();
			
			mAdapterModif = new SavedContributionAdapter(this, list);
			listViewModif.setAdapter(mAdapterModif);
			listViewModif.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id)
				{
					Contribution contribution = (Contribution) mAdapterModif.getItem(position);
					Bundle bundle = new Bundle();
					bundle.putSerializable("contribution", contribution);
					Intent intent = new Intent(SavedContributionsActivity.this, ListChampsNoticeModif.class);
					intent.putExtras(bundle);
					startActivityForResult(intent, REQUEST_EDIT_CONTRIBUTION);
				}
			});	

			for( String filename: modifiedNoticeFilenames ) {
				Contribution contribution = Contribution.restoreFromFile(new File(saveDir, filename).getAbsolutePath());
				if( contribution != null ) {
					list.add(contribution);
				}
			}
		}
		else {
			listViewModif.setVisibility(View.GONE);
			textViewModif.setVisibility(View.GONE);
			lineViewModif.setVisibility(View.GONE);
		}


		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.show();
			actionBar.setTitle(getResources().getString(R.string.title_saved_contributions));
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowTitleEnabled(true);
			//actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
		}
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_EDIT_CONTRIBUTION) {
			if(resultCode == RESULT_OK) {
				// TODO
			}
		}
		else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
