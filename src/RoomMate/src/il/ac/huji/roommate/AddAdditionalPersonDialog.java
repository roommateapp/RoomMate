package il.ac.huji.roommate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class AddAdditionalPersonDialog extends DialogFragment 
{
	// Use this instance of the interface to deliver action events
    public AddRoomatesDialog.AddRoommatesListener mListener;
    
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
            mListener = (AddRoomatesDialog.AddRoommatesListener) activity;
        } 
        catch (ClassCastException e) 
        {
        	// The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    View v = inflater.inflate(R.layout.add_additional_person_dialog, null);
	    builder.setView(v);
	    builder.setTitle(R.string.add_additional_person_title);
	    builder.setMessage(R.string.add_additional_person_text);
	    builder.setNegativeButton(R.string.later, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				getDialog().dismiss();
			}
		});
	    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) 
            {
            	mListener.onAddRoommates(AddAdditionalPersonDialog.this);      	
            	getDialog().dismiss();
            }
        });
	    return builder.create();
	    
	}
}
