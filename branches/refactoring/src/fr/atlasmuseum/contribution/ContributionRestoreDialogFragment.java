package fr.atlasmuseum.contribution;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import fr.atlasmuseum.R;

public class ContributionRestoreDialogFragment extends DialogFragment {
    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ContributionRestoreDialogListener {
        public void onRestoreSavedModifications();
        public void onDiscardSavedModifications();
    }
    
    // Use this instance of the interface to deliver action events
    ContributionRestoreDialogListener mListener;
    
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ContributionRestoreDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement ContributionRestoreDialogListener");
        }
    }

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_restore_contribution_modif_title)
               .setMessage(R.string.dialog_restore_contribution_modif_message)
               .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   mListener.onRestoreSavedModifications();
                   }
               })
               .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   mListener.onDiscardSavedModifications();
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
