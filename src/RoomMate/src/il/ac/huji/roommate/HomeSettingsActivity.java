package il.ac.huji.roommate;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.internal.ho;
import com.google.android.gms.internal.ma;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint("NewApi")
public class HomeSettingsActivity extends Fragment implements OnClickListener {

	private MainActivity mainActivity;
	private Bitmap bitmap;
	private ParseFile file;
	private EditText balanceEdit;
	private EditText billsEdit;

	final static int IMAGE_RQST_CODE = 1;
	final static int LOCATION_RQST_CODE = 2;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mainActivity = ((MainActivity)activity); 
	}
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater .inflate(R.layout.activity_home_settings, container, false); 

		TextView inviteBtn = (TextView)rootView.findViewById(R.id.invite_roommates);
		//		TextView cleaningTxt = (TextView)rootView.findViewById(R.id.cleaning_interval_txt);
		//		cleaningEdit = (EditText)rootView.findViewById(R.id.cleaning_interval);
		TextView balanceTxt = (TextView)rootView.findViewById(R.id.balance_interval_txt);
		TextView balanceTxtEnd = (TextView)rootView.findViewById(R.id.balance_interval_txt_end);
		balanceEdit = (EditText)rootView.findViewById(R.id.balnce_interval);
		TextView billsTxt = (TextView)rootView.findViewById(R.id.bills_annonce_txt);
		TextView billsTxtEnd = (TextView)rootView.findViewById(R.id.bills_notification_txt_end);
		billsEdit = (EditText)rootView.findViewById(R.id.bills_notification_edit);
		TextView locationTxt = (TextView)rootView.findViewById(R.id.home_location_txt);
		//		Button locationBtn = (Button)rootView.findViewById(R.id.set_location);
		TextView imageTxt = (TextView)rootView.findViewById(R.id.image_txt);
		//		Button immageBtn = (Button)rootView.findViewById(R.id.set_image);
		Button saveChanges = (Button)rootView.findViewById(R.id.save_changes);

		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "SinkinSans-400Regular.otf");

		inviteBtn.setTypeface(font);
		//		cleaningTxt.setTypeface(font);
		//		cleaningEdit.setTypeface(font);
		balanceTxt.setTypeface(font);
		balanceTxtEnd.setTypeface(font);
		balanceEdit.setTypeface(font);
		billsEdit.setTypeface(font);
		billsTxt.setTypeface(font);
		billsTxtEnd.setTypeface(font);
		locationTxt.setTypeface(font);
		imageTxt.setTypeface(font);
		saveChanges.setTypeface(font);

		inviteBtn.setOnClickListener(this);
		imageTxt.setOnClickListener(this);
		locationTxt.setOnClickListener(this);
		saveChanges.setOnClickListener(this);

		// load existing data from parse

		ParseObject house = mainActivity.getHomeObject();
		if (house != null){
			balanceEdit.setText(String.valueOf(house.getInt("balanceInterval")));
			billsEdit.setText(String.valueOf(house.getInt("billsNotificationInterval")));
		}
		



		RelativeLayout rel = (RelativeLayout)rootView.findViewById(R.id.rel_home_settings);
		rel.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent ev)
			{
				InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

				return false;
			}
		});

		return rootView;
	}

	@Override
	public void onClick(View view) 
	{
		switch (view.getId())
		{
		case R.id.invite_roommates:
			DialogFragment newFragment = new AddRoomatesDialog();
			newFragment.show(mainActivity.getFragmentManager(), "ds");
			break;
		case R.id.home_location_txt:
			Intent i = new Intent(getActivity().getApplicationContext(), ChooseHomeLocationActivity.class);
			ParseGeoPoint homeLocation = mainActivity.getHomeObject()
					.getParseGeoPoint("homeLocation");
			if (homeLocation != null){
				// Home location already exists
				i.putExtra("Lat", homeLocation.getLatitude());
				i.putExtra("Lng", homeLocation.getLongitude());
				i.putExtra("PrevLocationExists", true);
			}
			else
			{
				// pass current user location instead
				Location userLocation = mainActivity.locationTracker.getCurrentLocation();
				if (userLocation != null){
					i.putExtra("Lat", userLocation.getLatitude());
					i.putExtra("Lng", userLocation.getLongitude());
				}

				i.putExtra("PrevLocationExists", false);
			}
			startActivityForResult(i, LOCATION_RQST_CODE);
			break;
		case R.id.image_txt:
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			//intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
			//intent.addCategory(Intent.CATEGORY_OPENABLE);
			startActivityForResult(Intent.createChooser(intent,
					"Select Picture"), IMAGE_RQST_CODE);
			break;
			//		case R.id.balance:
			//			mainActivity.parseHomeObject.put("balanceInterval", balanceEdit.getText().toString());
			//			break;
		case R.id.save_changes: // save and go back to home-board
			ParseObject house = mainActivity.getHomeObject();
			house.put("billsNotificationInterval", Integer.valueOf(billsEdit.getText().toString())); // cleaning interval
			house.saveInBackground();
			int prevInterval = house.getInt("balanceInterval");
			if (prevInterval!=Integer.valueOf(balanceEdit.getText().toString())){
				house.put("balanceInterval", Integer.valueOf(balanceEdit.getText().toString()));
				
				//	house.put("cleaningNotificationInterval", Integer.valueOf(cleaningEdit.getText().toString())); 
				//			house.saveInBackground();

				// cancel prev alarm
				Intent intentAlarm = new Intent(getActivity(), BalanceReceiver.class);
				int alarmCode = house.getInt("alarmCode");
				PendingIntent sender = PendingIntent.getBroadcast(getActivity(), alarmCode, intentAlarm, 0);

				house.put("alarmCode", alarmCode+1);
				house.saveInBackground();

				AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
				alarmManager.cancel(sender);

				// set a new one
				Date startDate = house.getDate("startDate");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(startDate);
				calendar.set(Calendar.HOUR_OF_DAY, 13);
				calendar.set(Calendar.MINUTE, 00);
				calendar.set(Calendar.SECOND, 00);
				calendar.add(Calendar.DAY_OF_YEAR, Integer.valueOf(balanceEdit.getText().toString()));
				Log.i("SET", "here1, ID: " + house.getObjectId());

				Intent myIntent = new Intent(getActivity(), BalanceReceiver.class);
				myIntent.putExtra("homeId", house.getObjectId());
				intentAlarm.putExtra("homeId", house.getObjectId());

				PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), alarmCode+1, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

				alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 
						calendar.getTimeInMillis(), 1000*60*60*24*Integer.valueOf(balanceEdit.getText().toString()), pendingIntent);

				Intent startIntent = new Intent(getActivity(), MainActivity.class);
				startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);        
				getActivity().startActivity(startIntent);


				Toast.makeText(getActivity(), "New Home-Setting were saved successfuly", 
						Toast.LENGTH_LONG).show();
			} else{ 
				Log.i("SET", "nothingchanged");
			}
			break;
		}		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) 
		{
		case IMAGE_RQST_CODE:
			if (data != null )
			{
				if (resultCode == Activity.RESULT_OK)
					try {
						// We need to recycle unused bitmaps
						if (bitmap != null) {
							bitmap.recycle();
						}
						InputStream inputStream = mainActivity.getContentResolver().openInputStream(
								data.getData());
						bitmap = BitmapFactory.decodeStream(inputStream);
						inputStream.close();

						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
						// get byte array here
						byte[] bytearray = outputStream.toByteArray();

						if (bytearray != null){
							file = new ParseFile("homePicture"+".jpg", bytearray);
							file.saveInBackground(new SaveCallback() {

								@Override
								public void done(ParseException e) {
									if (e==null)
									{
										ParseObject house = mainActivity.getHomeObject();
										house.put("homePicture", file);
										house.saveInBackground();
									}
								}
							});
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			break;

		case LOCATION_RQST_CODE:
			if (resultCode == Activity.RESULT_OK)
			{
				if (data != null){
					Double lat = data.getDoubleExtra("Lat", 0);
					Double lng = data.getDoubleExtra("Lng", 0);
					ParseGeoPoint l = new ParseGeoPoint(lat, lng);
					ParseObject house = mainActivity.getHomeObject();
					house.put("homeLocation", l);
					house.saveInBackground();
					Toast.makeText(getActivity(), "The new home location set", Toast.LENGTH_LONG).show();
					mainActivity.onHomeLocationChanged(l);
				}
			}

		default:
			break;
		}


	}
}
