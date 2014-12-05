package il.ac.huji.roommate;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;

public class BillNotificationService  extends Service 
{

	private NotificationManager mManager;

	@Override
	public void onCreate() 
	{
		super.onCreate();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId)
	{
		
		String billName = intent.getStringExtra("billName");
		String notificationDays = intent.getStringExtra("notificationDays");
		String houseId = intent.getStringExtra("houseId");
		String userId = intent.getStringExtra("userId");
		
		String notificationMessage = billName + " bill's deadline is " + notificationDays + " days away!";

		Log.i("BILL" ," in service!");
		mManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

		Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
		
		Notification notification = new Notification(R.drawable.notification_icon, 
				notificationMessage, System.currentTimeMillis());

		intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent1, 
				PendingIntent.FLAG_UPDATE_CURRENT);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(getApplicationContext(), "RoomMate", "It's time to pay an upcoming bill!", 
				pendingNotificationIntent);
		
		ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
		pushQuery.whereEqualTo(MainActivity.PERSON_FIELD_HOUSE, houseId );
		pushQuery.whereNotEqualTo("userId", userId);

		// Send push notification to query
		ParsePush push = new ParsePush();
		push.setChannel("");
		push.setQuery(pushQuery);
		push.setMessage(notificationMessage);
		push.sendInBackground();

		mManager.notify(0, notification);							
	}


	@Override
	public void onDestroy() 
	{
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}