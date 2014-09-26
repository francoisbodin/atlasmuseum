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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SavedContributionsActivity extends Activity {
	
	private static final String DEBUG_TAG = "AtlasMuseum/SavedContributionActivity";

	static final int REQUEST_EDIT_CONTRIBUTION = 1;

	ListView mListViewNew;
	TextView mTextViewNew;
	View mLineViewNew;

	ListView mListViewModif;
	TextView mTextViewModif;
	View mLineViewModif;

	List<Contribution> mListModif;
	List<Contribution> mListNew;
	
	SavedContributionAdapter mAdapterNew;
	SavedContributionAdapter mAdapterModif;

	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_saved_contributions);
        Log.d(DEBUG_TAG, "onCreate()");
        
		mListViewNew = (ListView) findViewById(R.id.list_new_notice);
		mListViewNew.setScrollContainer(false);
		mTextViewNew = (TextView) findViewById(R.id.text_new_notice);
		mLineViewNew = (View) findViewById(R.id.line_new_notice);

		mListViewModif = (ListView) findViewById(R.id.list_modif_notice);
		mTextViewModif = (TextView) findViewById(R.id.text_modif_notice);
		mLineViewModif = (View) findViewById(R.id.line_modif_notice);

		mListNew = new ArrayList<Contribution>();
		mAdapterNew = new SavedContributionAdapter(this, mListNew);
		mListViewNew.setAdapter(mAdapterNew);
		mListViewNew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
		mListViewNew.setOnCreateContextMenuListener( new AdapterView.OnCreateContextMenuListener() {			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
				Contribution contribution = (Contribution) mAdapterNew.getItem(info.position);
				
				menu.setHeaderTitle(contribution.getDate() + " " + contribution.getTime());
				menu.add(Menu.NONE, 0, 0, "Supprimer");
			}
		});
		
		mListModif = new ArrayList<Contribution>();
		mAdapterModif = new SavedContributionAdapter(this, mListModif);
		mListViewModif.setAdapter(mAdapterModif);
		mListViewModif.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
		mListViewModif.setOnCreateContextMenuListener( new AdapterView.OnCreateContextMenuListener() {			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
				Contribution contribution = (Contribution) mAdapterNew.getItem(info.position);
				
				menu.setHeaderTitle(contribution.getDate() + " " + contribution.getTime());
				menu.add(Menu.NONE, 0, 0, "Supprimer");
			}
		});
		

		updateSavedContributionLists();

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
			switch(resultCode) {
			case ListChampsNoticeModif.RESULT_SAVED:
				Toast.makeText(this, getResources().getString(R.string.contrib_save), Toast.LENGTH_SHORT).show();
				updateSavedContributionLists();
				break;
			case ListChampsNoticeModif.RESULT_SENT:
    			Toast.makeText(this, getResources().getString(R.string.contrib_envoi_success), Toast.LENGTH_SHORT).show();
				updateSavedContributionLists();
				break;
			case RESULT_CANCELED:
			default:
				break;
			}
		}
		else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void updateSavedContributionLists() {
		// Handle new notices
		File saveDir = new File( Contribution.getSaveDir(this) );

		FilenameFilter newFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("new_");
			}
		};
		String[] newNoticeFilenames = saveDir.list(newFilter);
		mListNew.clear();
		for( String filename: newNoticeFilenames ) {
			Contribution contribution = Contribution.restoreFromFile(new File(saveDir, filename).getAbsolutePath());
			if( contribution != null ) {
				mListNew.add(contribution);
			}
		}
		mAdapterNew.notifyDataSetChanged();
		

		// Handle modified notices
		FilenameFilter modifiedFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("modif_");
			}
		};
		String[] modifiedNoticeFilenames = saveDir.list(modifiedFilter);
		mListModif.clear();
		for( String filename: modifiedNoticeFilenames ) {
			Contribution contribution = Contribution.restoreFromFile(new File(saveDir, filename).getAbsolutePath());
			if( contribution != null ) {
				mListModif.add(contribution);
			}
		}
		mAdapterModif.notifyDataSetChanged();
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		Contribution contribution = mListModif.get(info.position);
		String filename = contribution.getSavedFilename();
		File file = new File(filename);
		if(filename != null && ! filename.equals("") && file.exists()) {
			file.delete();
			updateSavedContributionLists();
		}
		return true;
	}

}
