package il.ac.huji.roommate;

import java.text.Collator;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;

public class RoommateParseReceiver extends ParsePushBroadcastReceiver {
	@Override
    public void onPushOpen(Context context, Intent intent) {
		/*
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		// set title
		alertDialogBuilder.setTitle("Roommate message");
		// set dialog message
		Bundle ob = intent.getExtras();
		if (ob != null){
			Log.d("tt", ob.keySet().toString());
			//12-01 12:37:46.997: D/tt(9846): [com.parse.Data, com.parse.Channel]

		}
		alertDialogBuilder.setMessage("...");
		alertDialogBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		*/
		
		
        Intent i = new Intent(context, MainActivity.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        
    }
}
