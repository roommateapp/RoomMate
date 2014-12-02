package il.ac.huji.roommate;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class RoommateApplication extends Application {

  public RoommateApplication() {
	  
  }

  @SuppressWarnings("deprecation")
@Override
  public void onCreate() {
    super.onCreate();
    
	// Initialize the Parse SDK.
	Parse.initialize(this,  "exAWKD0hh8xWzri3FbLi9EMQAWOBNjblEpKRNSpu", "Wmk9gVhud0DQ2W0tJcDHvQIpi1fecNfbHyXTI80w"); 

	// Specify an Activity to handle all pushes by default.
	PushService.setDefaultPushCallback(this, MainActivity.class);
//	ParseInstallation.getCurrentInstallation().saveInBackground();
//	
//	//PushService.setDefaultPushCallback(this, MainActivity.class);
//	ParseInstallation pi = ParseInstallation.getCurrentInstallation();
//    
//    //Register a channel to test push channels
//    PushService.subscribe(this.getApplicationContext(), "ch1", MainActivity.class);
//    Log.i("INIT","1");
//    pi.saveEventually();
  }
}