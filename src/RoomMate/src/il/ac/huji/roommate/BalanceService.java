package il.ac.huji.roommate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BalanceService extends Service 
{

	private NotificationManager mManager;
	private String startDateStr;
	protected Date startDate;
	private String endDateStr;
	protected ParseObject newNoteObj;
	private List<ParseObject> allRoommates;
	//	private ParseObject house;
	private List<ParseObject> bills;

	@Override
	public void onCreate() 
	{
		// TODO Auto-generated method stub  
		super.onCreate();
	}

	@SuppressWarnings({ "deprecation", "static-access" })
	@Override
	public void onStart(Intent intent, int startId)
	{
		final String homeId = intent.getStringExtra("homeId");
		super.onStart(intent, startId);
		Log.i("BROAD" , "onStart in AlarmService");
		Log.i("SET", "house id: " + homeId);
		ParseQuery<ParseObject> query = ParseQuery.getQuery("House");
		query.getInBackground(homeId, new GetCallback<ParseObject>() {
			private String balanceNotePart2;
			@Override
			public void done(ParseObject house, ParseException e) {
				Double totalSpendings = house.getDouble("spendings");
				startDate = house.getDate("startDate");

				DateFormat df = new SimpleDateFormat("dd/MM/yy");
				startDateStr = df.format(startDate);

				final Date today = Calendar.getInstance().getTime(); 
				endDateStr = df.format(today);

				String balanceNotePart1 = "BALANCE FOR:\n" + startDateStr + " - " + endDateStr + "\n\nTotal spendings: " +  totalSpendings + "\n\n" ;
				allRoommates = house.getList("persons");
				final Double eachRoommateSahre = totalSpendings/allRoommates.size();
				balanceNotePart1 = balanceNotePart1 + "Each roommate's share in the total is: " + String.valueOf(eachRoommateSahre) + "\n\n";

				//					newNotePart1 = new ParseObject("Note");
				//					newNotePart1.put("content", balanceNotePart1);
				//					newNotePart1.put("writer", "RM");
				//					newNotePart1.put("isNotification", true);
				//					newNotePart1.saveInBackground(new SaveCallback() {
				//						@Override
				//						public void done(ParseException e) {
				//							final String noteId = newNotePart1.getObjectId();
				// for each id get the debt and credit from Person Parse-object and save it to models
				for (ParseObject person : allRoommates){
					//Log.i("TIME", "roommate id: " + s);
					try {
						person = person.fetch();
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
					Double roommateCredit = person.getDouble("credit");
					balanceNotePart2 = "";
					//Log.i("TIME", "roommate credit: " + roommateCredit);
					if (roommateCredit < eachRoommateSahre){
						balanceNotePart2 = "- " + person.getString("userName") +" needs to give back " + 
								String.valueOf(eachRoommateSahre-roommateCredit);
					}
					else if (roommateCredit > eachRoommateSahre){
						balanceNotePart2 = "- " + person.getString("userName") +" needs to get a refund of " + 
								String.valueOf(roommateCredit-eachRoommateSahre);
					}
					else{
						balanceNotePart2 = "- " + person.getString("userName") +" is even";					
					}

					person.put("credit", 0.0);
					person.saveInBackground();
					//						ParseQuery<ParseObject> queryNote = ParseQuery.getQuery("Note");
					//						queryNote.getInBackground(noteId, new GetCallback<ParseObject>() {
					//							@Override
					//							public void done(final ParseObject note, ParseException e) {
					//								String temp = note.getString("content");
					//								temp = temp + balanceNotePart2;
				}
				newNoteObj = new ParseObject("Note");
				String allMessage = balanceNotePart1 + balanceNotePart2;
				newNoteObj.put("content", allMessage);
				newNoteObj.put("writer", "RM");
				newNoteObj.put("isNotification", true);
				newNoteObj.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e1) {
						if (e1!=null)
							e1.getStackTrace();
						else{
							Log.i("RR", "HERE");
							ParseQuery<ParseObject> query = ParseQuery.getQuery("House");
							query.getInBackground(homeId, new GetCallback<ParseObject>() {
								@Override
								public void done(ParseObject house, ParseException e) {
									try {
										house = house.fetch();
									} catch (ParseException e2) {
										// TODO Auto-generated catch block
										e2.printStackTrace();
									}
									house.add("notes", newNoteObj.getObjectId());
									Log.i("RR", "ID: " + newNoteObj.getObjectId());
									Log.i("RR", "home ID: " + homeId);

									//							List<String> notesIds = house.getList("notes");
									//							notesIds.add(newNoteObj.getObjectId());
									//
									//							house.put("notes", notesIds);
									house.put("startDate", today);
									house.put("spendings", 0.0);
									bills = house.getList("bills");
									house.saveInBackground();
									for (ParseObject bill : bills){
										try {
											bill = bill.fetch();
										} catch (ParseException e1) {
											e1.printStackTrace();
										}
										bill.put("notified", true);
										bill.saveInBackground();
									}
									mManager = (NotificationManager)getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

									Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);

									Notification notification = new Notification(R.drawable.notification_icon, "Balance period ended", System.currentTimeMillis());
									intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

									PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getApplicationContext(),0, intent1, 
											PendingIntent.FLAG_UPDATE_CURRENT);
									notification.flags |= Notification.FLAG_AUTO_CANCEL;
									notification.setLatestEventInfo(getApplicationContext(), "RoomMate", "It's time to get even with your roommates!", 
											pendingNotificationIntent);

									mManager.notify(0, notification);							
								}
							});
						}
					}
				});
			}
		});


		// change all bills to notified!
		//				ParseQuery<ParseObject> queryBill = ParseQuery.getQuery("SingleBill");
		//				queryBill.whereEqualTo("paid", true);
		//				queryBill.whereEqualTo("notified", false);
		//				queryBill.whereEqualTo("houseId", homeId);
		//				queryBill.findInBackground(new FindCallback<ParseObject>() {
		//					@Override
		//					public void done(List<ParseObject> bills, ParseException e) {
		//						for (ParseObject p: bills){
		//							p.put("notified", true);
		//							p.saveInBackground();
		//							Log.i("TIME", "save2");
		//						}
		//					}
		//				});
		//				//				}
		//			});




		//		}
	}

	@Override
	public void onDestroy() 
	{
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
