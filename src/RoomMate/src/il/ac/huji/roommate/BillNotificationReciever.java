package il.ac.huji.roommate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BillNotificationReciever extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		String billName = intent.getStringExtra("billName"); 
		String notificationDays = String.valueOf(intent.getIntExtra("notificationDays", 3));
		String houseId = intent.getStringExtra("houseId");
		String userId = intent.getStringExtra("userId");
		
		Log.i("BILL", "receiver! bill name: " + billName + " days: " + notificationDays);
		
		Intent service1 = new Intent(context, BillNotificationService.class);
		service1.putExtra("billName", billName);
		service1.putExtra("notificationDays", notificationDays);
		service1.putExtra("houseId", houseId);
		service1.putExtra("userId", userId);
		context.startService(service1);
	}   
}
