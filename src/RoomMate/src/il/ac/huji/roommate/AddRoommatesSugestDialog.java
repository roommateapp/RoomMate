package il.ac.huji.roommate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class AddRoommatesSugestDialog extends DialogFragment 
{
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface AddRoommatesSugestListener 
	{
        public void onAddRoommatesSuggestionPositiveClick(DialogFragment dialog);
    }
	
	// Use this instance of the interface to deliver action events
    public AddRoommatesSugestListener mListener;
    
    // Override the Fragment.onAttach() method to instantiate
    // the AddRoommatesSugestListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try 
        {
        	// Instantiate the AddRoommatesSugestListener 
        	//so we can send events to the host
            mListener = (AddRoommatesSugestListener) activity;
        } 
        catch (ClassCastException e) 
        {
        	// The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
    
	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    
//	    TextView content = new TextView(getActivity());
//	    Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "SinkinSans-400Regular.otf");
//	    content.setText(R.string.add_roommates_suggest_text);
//        content.setTypeface(font);

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    View v = inflater.inflate(R.layout.add_roommates_suggest_dialog, null);
	    builder.setView(v);
	    builder.setTitle(R.string.add_roommates_suggest_title);
	    builder.setMessage(R.string.add_roommates_suggest_text);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) 
            {
            	mListener.onAddRoommatesSuggestionPositiveClick(AddRoommatesSugestDialog.this);
                getDialog().dismiss();
            }
        });
        
	    builder.setNegativeButton(R.string.later, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
	    return builder.create();
	    
	}
}