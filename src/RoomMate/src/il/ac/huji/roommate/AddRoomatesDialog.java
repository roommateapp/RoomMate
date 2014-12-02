package il.ac.huji.roommate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;


public class AddRoomatesDialog extends DialogFragment 
{
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface AddRoommatesListener 
	{
        public void onAddRoommates(DialogFragment dialog);
    }
	
	// Use this instance of the interface to deliver action events
    public AddRoommatesListener mListener;
    
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
            mListener = (AddRoommatesListener) activity;
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

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    View v = inflater.inflate(R.layout.add_roommates_dialog, null);
	    builder.setView(v);
	    
	    builder.setTitle(R.string.add_roommates_title);
	    builder.setMessage(R.string.add_roommates_text);
        builder.setPositiveButton(R.string.add_roommates_send_first_button,
        		new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int id) 
            {
               	mListener.onAddRoommates(AddRoomatesDialog.this);      	
                getDialog().dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel,
        		new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int id) 
            {
                getDialog().dismiss();
            }
        });
         
	    return builder.create();
	    
	}
	
}
