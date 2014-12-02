package il.ac.huji.roommate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class BalanceReceiver extends BroadcastReceiver
{
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Log.i("BROAD" , "onRecive in MyReciever");
		String s = intent.getStringExtra("homeId");
		Log.i("SET", "RECEIVER house id: " + s);
		Intent service1 = new Intent(context, BalanceService.class);
		service1.putExtra("homeId", s);
		context.startService(service1);
	}   
}