package il.ac.huji.roommate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import com.google.android.gms.internal.in;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SingleBillActivty extends Activity{

	private String houseId;
	private String billName;
	private boolean existingBill = true;
	private ParseObject newBillObj;
	private Double oldBillAmount;
	private String userName;

	final static int IMAGE_RQST_CODE = 1;
	private Bitmap bitmap;
	private ParseFile file;

	int dayToSave;
	int monthToSave;
	int yearToSave;

	public static EditText newBillName;
	public static EditText newBillamount;
	public static DatePicker dueDatePicker;
	CheckBox paidCheckbox;
	DatePicker paidDatePicker;
	Button setReciptImageBtn;
	ImageView billImage;
	Button saveBillBtn;
	//View seperator;

	TextView dueDateTxt;
	TextView isPayedTxt;
	TextView payDateTxt;
	TextView imageTxt;
	private Bundle bundle;
	private boolean isNotified;
	private String billParseId = null;
	protected boolean ready = false;

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_bill);

		bundle = getIntent().getExtras();
		houseId = bundle.getString("houseId");
		billName = bundle.getString("billname");
		existingBill = bundle.getBoolean("existingBill");
		isNotified = bundle.getBoolean("notified");
		setTitle("Bill: " + billName);
		userName = bundle.getString("userName");
		if (bundle.getString("billParseId")!=null)
			billParseId = bundle.getString("billParseId");

		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
		TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);

		Typeface fontBold = Typeface.createFromAsset(getAssets(), "SinkinSans-600SemiBold.otf");
		mTitleTextView.setTypeface(fontBold);

		mTitleTextView.setText("bill: " + billName);

		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);

		getActionBar().setCustomView(mCustomView);
		getActionBar().setDisplayShowCustomEnabled(true);

		newBillName = (EditText)findViewById(R.id.new_bill_name);
		newBillamount = (EditText)findViewById(R.id.new_bill_amount);
		dueDatePicker = (DatePicker)findViewById(R.id.datePicker1);
		paidCheckbox = (CheckBox)findViewById(R.id.is_paid);
		paidDatePicker = (DatePicker)findViewById(R.id.datePicker2);
		//	billImage = (ImageView)findViewById(R.id.bill_image);
		saveBillBtn = (Button)findViewById(R.id.save_bill);
		//	seperator = (View)findViewById(R.id.view3);

		dueDateTxt = (TextView)findViewById(R.id.due_date_txt);
		isPayedTxt = (TextView)findViewById(R.id.is_payed_txt);
		payDateTxt = (TextView)findViewById(R.id.pay_date_txt);
		imageTxt = (TextView)findViewById(R.id.image_txt);		

		Typeface font = Typeface.createFromAsset(getAssets(), "SinkinSans-400Regular.otf");
		newBillName.setTypeface(font);
		newBillamount.setTypeface(font);
		saveBillBtn.setTypeface(font);
		dueDateTxt.setTypeface(font);
		isPayedTxt.setTypeface(font);
		payDateTxt.setTypeface(font);
		imageTxt.setTypeface(font);

		newBillName.setText(billName);

		imageTxt.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				//				try {
				//					newBillObj = newBillObj.fetch();
				//				} catch (ParseException e) {
				//					e.printStackTrace();
				//				}
				if (newBillObj != null){
					Boolean hasImg = newBillObj.getBoolean("hasImage");
					if (!hasImg){
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(Intent.createChooser(intent,
								"Select Picture"), IMAGE_RQST_CODE);
					}
					else {
						// open the BillImageActivity, put billID in extras
						Intent intent = new Intent(getApplication(), BillImageActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("billId", billParseId);
						getApplication().startActivity(intent);
					}
				} else {
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent,
							"Select Picture"), IMAGE_RQST_CODE);
				}
			}
		});

		final DatePicker.OnDateChangedListener myLisener = new DatePicker.OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker picker, int year, int monthOfYear,
					int dayOfMonth) {
				dayToSave = dayOfMonth;
				monthToSave = monthOfYear + 1;
				yearToSave = year;
			}
		};

		if (existingBill){
			if (billParseId!=null){
				ParseQuery<ParseObject> queryBill = ParseQuery.getQuery("SingleBill");
				queryBill.getInBackground(billParseId, new GetCallback<ParseObject>() {
					@Override
					public void done(ParseObject bill, ParseException e) {
						if (bill != null){
							try{
								newBillObj = bill.fetch();
							} catch (ParseException e1){}
							Log.i("Bigg"," old bill");
							newBillamount.setText(String.valueOf(bill.getDouble("amount")));
							oldBillAmount = bill.getDouble("amount");
							// if paid set paidOn date, set isPaid
							if (bill.getString("paidDate")!=null && !bill.getString("paidDate").equals("")){
								paidCheckbox.setChecked(true);
								dueDatePicker.setVisibility(View.GONE);
								dueDateTxt.setVisibility(View.GONE);
								//	seperator.setVisibility(View.GONE);

								paidDatePicker.setVisibility(View.VISIBLE);
								payDateTxt.setVisibility(View.VISIBLE);
								String paidOnDate = bill.getString("paidDate");
								paidDatePicker.init(Integer.valueOf(paidOnDate.split("/")[2]), 
										Integer.valueOf(paidOnDate.split("/")[1]), Integer.valueOf(paidOnDate.split("/")[0]), myLisener);

								dayToSave = Integer.valueOf(paidOnDate.split("/")[0]);
								monthToSave = Integer.valueOf(paidOnDate.split("/")[1]);
								yearToSave = Integer.valueOf(paidOnDate.split("/")[2]);

							}else { // else set dueDate date
								String dueDate = bill.getString("dueDate");
								dueDatePicker.init(Integer.valueOf(dueDate.split("/")[2]), 
										Integer.valueOf(dueDate.split("/")[1]), Integer.valueOf(dueDate.split("/")[0]), myLisener);

							}
							// if there's an image set the image
						}

					}
				});
			}
		}

		RelativeLayout rel = (RelativeLayout)findViewById(R.id.rel);
		rel.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent ev)
			{
				InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

				return false;
			}
		});

		final Calendar c = Calendar.getInstance();
		int yearCurrent = c.get(Calendar.YEAR);
		int monthCurrent = c.get(Calendar.MONTH);
		int dayCurrent = c.get(Calendar.DAY_OF_MONTH);

		if (!existingBill){
			dayToSave = dayCurrent;
			monthToSave = monthCurrent + 1;
			yearToSave = yearCurrent;
		}

		// set current date into datepicker
		if (!existingBill){
			paidDatePicker.init(yearCurrent, monthCurrent, dayCurrent, myLisener);
			dueDatePicker.init(yearCurrent, monthCurrent, dayCurrent, myLisener);
		}

		if (paidCheckbox.isChecked()){
			dueDatePicker.setVisibility(View.GONE);
			dueDateTxt.setVisibility(View.GONE);

			paidDatePicker.setVisibility(View.VISIBLE);
			payDateTxt.setVisibility(View.VISIBLE);
		} else{ 
			dueDatePicker.setVisibility(View.VISIBLE);
			dueDateTxt.setVisibility(View.VISIBLE);

			paidDatePicker.setVisibility(View.GONE);
			payDateTxt.setVisibility(View.GONE);
		}

		if (!isNotified){ // can be changed only if not already notified
			paidCheckbox.setOnClickListener(new View.OnClickListener() {  
				public void onClick(final View v) {  
					if (paidCheckbox.isChecked()){
						dueDatePicker.setVisibility(View.GONE);
						dueDateTxt.setVisibility(View.GONE);

						paidDatePicker.setVisibility(View.VISIBLE);
						payDateTxt.setVisibility(View.VISIBLE);
					} else{
						dueDatePicker.setVisibility(View.VISIBLE);
						dueDateTxt.setVisibility(View.VISIBLE);

						paidDatePicker.setVisibility(View.GONE);
						payDateTxt.setVisibility(View.GONE);
					}
				}
			});

			// save bill button, save all the changes to Parse
			saveBillBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!existingBill)
					{ // create a new bill
						newBillObj = new ParseObject("SingleBill");
					} 
					else
					{ // update existing bill					
						ParseQuery<ParseObject> queryBill = ParseQuery.getQuery("SingleBill");
						queryBill.getInBackground(billParseId, new GetCallback<ParseObject>() {
							@Override
							public void done(ParseObject bill, ParseException e) {
								try {
									newBillObj = bill.fetch();
								} catch (ParseException e1) {
									e1.printStackTrace();
								}
								Log.i("Bigg"," old bill");
							}
						});
					}
					if (newBillName.getText().toString().equals(""))
						return;
					else
						newBillObj.put("name", newBillName.getText().toString());

					if (newBillamount.getText().toString().equals("")){
						newBillObj.put("amount", 0.0);
						newBillamount.setText("0.0");
					}
					else
						newBillObj.put("amount", Double.valueOf(newBillamount.getText().toString()));

					newBillObj.put("paid", paidCheckbox.isChecked());

					if (paidCheckbox.isChecked()){
						String dayToSaveStr = String.valueOf(dayToSave);
						String monthToSaveStr = String.valueOf(monthToSave);

						if (dayToSaveStr.length()==1)
							dayToSaveStr = "0"+dayToSaveStr;
						if (monthToSaveStr.length()==1)
							monthToSaveStr = "0"+monthToSaveStr;

						newBillObj.put("paidDate", dayToSaveStr +"/"+monthToSaveStr+"/"+String.valueOf(yearToSave));

						newBillObj.put("paidBy", userName);
						newBillObj.put("dueDate", "");

						// update spendings in House Parse-object and update balance
						ParseQuery<ParseObject> query = ParseQuery.getQuery("House");
						query.getInBackground(houseId, new GetCallback<ParseObject>() {
							@Override
							public void done(ParseObject house, ParseException e) {

								Double spendings = house.getDouble("spendings");
								if (existingBill && newBillObj.getString("dueDate").equals("")) // and dueDate is ""
									spendings = spendings - oldBillAmount;									
								spendings = spendings + Double.valueOf(newBillamount.getText().toString());
								house.put("spendings", spendings);
								List<ParseObject> persons = house.getList("persons");
								for (ParseObject person : persons){
									try {
										person = person.fetch();
									} catch (ParseException e1) {
										e1.printStackTrace();
									}
									if (person.get("userName").equals(userName)){
										Double roommateCredit = person.getDouble("credit"); // all the bills paid by that roommate
										if (existingBill && newBillObj.getString("dueDate").equals(""))
											roommateCredit = roommateCredit - oldBillAmount;
										person.put("credit", roommateCredit + Double.valueOf(newBillamount.getText().toString()));
										person.saveInBackground();	
									}
								}
								house.saveInBackground();
							}
						});
					} else{
						String dayToSaveStr = String.valueOf(dayToSave);
						String monthToSaveStr = String.valueOf(monthToSave);

						if (dayToSaveStr.length()==1)
							dayToSaveStr = "0"+dayToSaveStr;
						if (monthToSaveStr.length()==1)
							monthToSaveStr = "0"+monthToSaveStr;

						String dateToSave = dayToSaveStr +"/"+monthToSaveStr+"/"+String.valueOf(yearToSave);

						newBillObj.put("dueDate", dateToSave);
					}

					newBillObj.put("notified", false);
					newBillObj.put("hasImage", false);

					newBillObj.saveInBackground(new SaveCallback() {
						@Override
						public void done(ParseException e) {

							Toast.makeText(getApplicationContext(), "Saving bill", 
									Toast.LENGTH_LONG).show(); 

							ParseQuery<ParseObject> query = ParseQuery.getQuery("House");
							query.getInBackground(houseId, new GetCallback<ParseObject>() {
								private ParseObject houseFetch;
								@Override
								public void done(ParseObject house, ParseException e) {
									try {
										houseFetch = house.fetch();
									} catch (ParseException e1) {
										e1.printStackTrace();
									}
									if (!existingBill){
										house.add("bills", newBillObj);
									}
									house.saveInBackground(new SaveCallback() {
										@Override
										public void done(ParseException e) {

											onBackPressed();

											if (ready){
												newBillObj.put("billPicture", file);
												newBillObj.put("hasImage", true);
												newBillObj.saveInBackground();
												ready = false;
											}

											// set notification 3 days before bill due-date
											int billNotification = houseFetch.getInt("billsNotificationInterval");
											Calendar calNotification = Calendar.getInstance();
											Log.i("BILL", "here 1. bill Notification every: " + String.valueOf(billNotification) + " days");

											calNotification.set(Calendar.YEAR, yearToSave);
											calNotification.set(Calendar.MONTH, monthToSave);
											calNotification.set(Calendar.DAY_OF_MONTH, dayToSave);
											calNotification.set(Calendar.HOUR_OF_DAY, 21);
											calNotification.set(Calendar.MINUTE, 7);
											calNotification.set(Calendar.SECOND, 00);

											calNotification.add(Calendar.DAY_OF_YEAR, -billNotification);

											Log.i("BILL", "here 2. " + calNotification.get(Calendar.DAY_OF_MONTH));

											Intent myIntent = new Intent(getApplication(), BillNotificationReciever.class);
											myIntent.putExtra("billName", newBillObj.getString("name"));
											myIntent.putExtra("notificationDays", billNotification);

											Log.i("BILL", "here 3.");

											PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplication(), 1, myIntent, 0);
											AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
											alarmManager.set(AlarmManager.RTC_WAKEUP, calNotification.getTimeInMillis(), pendingIntent);

											Log.i("BILL", "here 4.");
											onBackPressed();
										}
									});
								}
							});
						}
					});
				}
			});
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
						Log.i("IMG", "here 1");
						InputStream inputStream = getApplication().getContentResolver().openInputStream(
								data.getData());
						bitmap = BitmapFactory.decodeStream(inputStream);
						inputStream.close();

						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream);
						// get byte array here
						byte[] bytearray = outputStream.toByteArray();

						if (bytearray != null){
							file = new ParseFile("billPicture"+".jpg", bytearray);
							Log.i("IMAGE", bytearray.toString());

							file.saveInBackground(new SaveCallback() {
								@Override
								public void done(ParseException e) {
									Log.i("IMAGE", " FILE NOT NULL");
									ready = true;

									//									if (newBillObj == null){
									//										newBillObj = new ParseObject("SingleBill");
									//									} else {
									//										try {
									//											newBillObj = newBillObj.fetch();
									//										} catch (ParseException e1) {
									//											e1.printStackTrace();
									//										}
									//									}
									//									newBillObj.put("billPicture", file);
									//									newBillObj.saveInBackground();
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
		}
	}
}