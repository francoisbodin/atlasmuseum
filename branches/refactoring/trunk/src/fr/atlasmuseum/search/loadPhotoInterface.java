package fr.atlasmuseum.search;
import fr.atlasmuseum.search.module.NoticeAdapterWithDistance;
import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Impl�ment� par ListChampsNoticeModif, ResultListActivity
 * @author Expert
 *
 */
public interface loadPhotoInterface {
	Context getContext();
	ImageView getImageView();
	BaseAdapter getNoticeAdapter();

}
