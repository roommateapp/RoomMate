package il.ac.huji.roommate;

import il.ac.huji.roommate.AddRoomatesDialog.AddRoommatesListener;
import il.ac.huji.roommate.AddRoommatesSugestDialog.AddRoommatesSugestListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.internal.hp;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.Image;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseException;
import com.parse.SaveCallback;


public class MainActivity extends Activity implements
ConnectionCallbacks, OnConnectionFailedListener,
AddRoommatesSugestListener, AddRoommatesListener
{
	public final static int SIGN_IN_ACTIVITY_RQST_CODE = 2;
	public final static int NO_EXISTING_HOME_RQST_CODE = 3;
	public final static int NEW_HOME_CREATED_RESULT_CODE = 4;
	public final static int HOME_CODE_ENTERED_RESULT_CODE = 6;
	public final static int SEND_INVITATION_RQST_CODE = 5;
	public final static int ACCOUNT_CHANGED_RESULT_CODE = 7;

	public static final String CLASS_PERSON = "Person";
	public static final String PERSON_FIELD_ID = "personGoogleId";
	public static final String PERSON_FIELD_HOUSE = "house";
	public static final String PERSON_FIELD_USERNAME = "userName";
	public static final String PERSON_FIELD_PHOTO_URL = "photoUrl";
	public static final String PERSON_FIELD_DEBT = "debt";
	public static final String PERSON_FIELD_CREDIT = "credit";
	public static final String PERSON_FIELD_AT_HOME = "atHome";

	public static final String CLASS_HOUSE = "House";
	public static final String HOUSE_FIELD_PENDING_ENTRY_CODES = "pendingEntryCodes";
	public static final String HOUSE_FIELD_PERSONS = "persons";

	public static final String CLASS_PENDING_ENTRY_CODES = "PendingEntryCodes";
	public static final String PENDING_ENTRY_CODES_FIELD_HOUSE = "house";


	public LocationTracker locationTracker;

	String[] menutitles;
	TypedArray menuIcons;

	// nav drawer title
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private List<SlideMenuRowItem> rowItems;
	private SlideMenuCustomAdapter adapter;

	private GoogleApiClient mGoogleApiClient;

	/* A flag indicating that a PendingIntent is in progress and prevents
	 * us from starting further intents.
	 */
	private boolean mIntentInProgress;
	private boolean wasConnected;

	public static String userName;
	public String personGoogleId;
	public static String homeIdParse;

	public List<ParseObject> homePeopleList;
	public HashMap<String, PersonData> homePeopleMap;

	public ParseObject parsePersonObject;
	public ParseObject parseHomeObject;
	public ParseObject parseUUIDObject;
	private Person currentPerson;

	private String currentEntryCode;
	private String newUuid;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		// Here we initialize connection with Google API
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(Plus.API)
		.addScope(Plus.SCOPE_PLUS_PROFILE) // we don't post on google+, just get user data
		.build();
		//-------------------------------
		wasConnected = false;

		//Parse.initialize(this, "exAWKD0hh8xWzri3FbLi9EMQAWOBNjblEpKRNSpu", "Wmk9gVhud0DQ2W0tJcDHvQIpi1fecNfbHyXTI80w");

		ParsePush.subscribeInBackground("", new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
				} else {
					Log.e("com.parse.push", "failed to subscribe for push", e);
				}
			}
		});

		mTitle = mDrawerTitle = getTitle();

		menutitles = getResources().getStringArray(R.array.titles);
		menuIcons = getResources().obtainTypedArray(R.array.icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.slider_list);

		rowItems = new ArrayList<SlideMenuRowItem>();

		for (int i = 0; i < menutitles.length; i++) {
			SlideMenuRowItem items = new SlideMenuRowItem(menutitles[i], menuIcons.getResourceId(
					i, -1));
			rowItems.add(items);
		}

		menuIcons.recycle();

		adapter = new SlideMenuCustomAdapter(getApplicationContext(), rowItems);

		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(new SlideitemListener());

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_launcher_square, R.string.app_name, R.string.app_name) {

			public void onDrawerClosed(View view) {

				invalidateOptionsMenu();
				android.support.v4.widget.DrawerLayout rel = (android.support.v4.widget.DrawerLayout)findViewById(R.id.drawer_layout);
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

			}

			public void onDrawerOpened(View drawerView) {

				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
				android.support.v4.widget.DrawerLayout rel = (android.support.v4.widget.DrawerLayout)findViewById(R.id.drawer_layout);
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
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	@Override
	protected void onStart() 
	{
		super.onStart();
		
		Intent intent = getIntent();
		Log.d("tt", "-1");
		if (intent != null)
		{
			Bundle extras = intent.getExtras();
			if ( extras != null){
				if (extras.containsKey("com.parse.Data")){
					
					showMessageDialog(this, extras.getString("com.parse.Data", ""));
				}
			}
		}

		// Here we try to connect to google API via Google+ user
		// This method returns immediately, and connects to the service in the background.
		// If the connection is successful, onConnected(Bundle) is called 
		// and enqueued items are executed. On a failure, 
		// onConnectionFailed(ConnectionResult) is called.
		mGoogleApiClient.connect();


		if (!wasConnected)
		{
			updateDisplay(100);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	}


	public void onConnectionFailed(ConnectionResult result) 
	{
		// If we got here it means that onStart was called but
		// the connection to Google Plus was unsuccessful so
		// we need to go to SignInActivity.
		if ( !mIntentInProgress )
		{
			mIntentInProgress = true;
			Intent i = new Intent(getApplicationContext(), SignInActivity.class);
			startActivityForResult(i, SIGN_IN_ACTIVITY_RQST_CODE);
		}
	}

	public void onConnected(Bundle connectionHint) 
	{
		// We've resolved any connection errors.  mGoogleApiClient can be used to
		// access Google APIs on behalf of the user.
		if (!wasConnected)
		{
			wasConnected = true;
			currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
			if (currentPerson != null) 
			{
				personGoogleId = currentPerson.getId();
				ParseQuery<ParseObject> query = ParseQuery.getQuery(CLASS_PERSON);
				query.whereEqualTo(PERSON_FIELD_ID, personGoogleId);
				query.findInBackground(new FindCallback<ParseObject>() {
					public void done(List<ParseObject> personList, ParseException e) 
					{
						if (e == null)
						{
							if ( personList.size() == 0 )
							{
								// This is a new user
								parsePersonObject = new ParseObject(CLASS_PERSON);
								parsePersonObject.put(PERSON_FIELD_ID, personGoogleId);
								//parsePersonObject.put(PERSON_FIELD_HOUSE, "");
								if (currentPerson.hasName())
								{
									userName = currentPerson.getDisplayName();
								}
								else
								{
									userName = "";
								}
								if (userName.length() == 0)
								{
									userName = Plus.AccountApi.getAccountName(mGoogleApiClient).split("@")[0];
								}
								else
								{
									userName = userName.split(" ")[0];
								}
								parsePersonObject.put(PERSON_FIELD_USERNAME, userName);
								parsePersonObject.put(PERSON_FIELD_CREDIT, 0.0);
								parsePersonObject.saveInBackground();
							}
							else
							{
								// The user already exists in database
								parsePersonObject = personList.get(0);
								try{
									parsePersonObject = parsePersonObject.fetchIfNeeded();
								}
								catch (ParseException e1){
									e1.printStackTrace();
								}
								userName = parsePersonObject.getString(PERSON_FIELD_USERNAME);
							}

							// Update this person picture 
							Image personPhoto = currentPerson.getImage();
							String personPhotoUrl = "";
							if (personPhoto != null){
								personPhotoUrl = personPhoto.getUrl();
							}
							parsePersonObject.put(PERSON_FIELD_PHOTO_URL, personPhotoUrl);
							parsePersonObject.saveInBackground();

							parseHomeObject = parsePersonObject.getParseObject(PERSON_FIELD_HOUSE);

							if (parseHomeObject == null)
							{
								homeIdParse = "";
								Intent i = new Intent(getApplicationContext(), 
										NoExistingHomeActivity.class);
								startActivityForResult(i, NO_EXISTING_HOME_RQST_CODE);
							}
							else
							{
								// Home already exists for this person
								homeIdParse = parseHomeObject.getObjectId();
								try{
									parseHomeObject = parseHomeObject.fetch();
								} catch (ParseException e1){
									e1.printStackTrace();
								}
								onHouseFound();
							}
						} 
						else 
						{
							Log.i("DEBUG", "Error: " + e.getMessage());
						}
					}
				});
			}
		}

	}

	protected void onActivityResult(int requestCode, int responseCode, Intent intent) 
	{
		switch (requestCode)
		{
		case SIGN_IN_ACTIVITY_RQST_CODE:
			if (responseCode == RESULT_OK)
			{
				mIntentInProgress = false;
				if (!mGoogleApiClient.isConnecting()) 
				{
					mGoogleApiClient.connect();
				}
			}

			break;

		case NO_EXISTING_HOME_RQST_CODE:
			switch (responseCode) 
			{
			case NEW_HOME_CREATED_RESULT_CODE:
				homePeopleList = new ArrayList<ParseObject>();
				homePeopleList.add(parsePersonObject);

				parseHomeObject = new ParseObject(CLASS_HOUSE);
				parseHomeObject.put(HOUSE_FIELD_PERSONS, homePeopleList);
				parseHomeObject.put(HOUSE_FIELD_PENDING_ENTRY_CODES, new ArrayList<String>());
				parseHomeObject.put("notes", new ArrayList<String>());
				parseHomeObject.put("bills", new ArrayList<ParseObject>());
				parseHomeObject.put("balanceInterval", 31);
				parseHomeObject.put("billsNotificationInterval", 3); // cleaning interval
				//				parseHomeObject.put("cleaningNotificationInterval", 7);
				parseHomeObject.put("alarmCode", 0);
				parseHomeObject.put("groceryLists", new ArrayList<ParseObject>());
				parseHomeObject.put("spendings", 0.0);
				parseHomeObject.put("startDate", new Date());

				parseHomeObject.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) 
					{
						if ( e == null )
						{
							onHouseFound();

							//**************************** new *******************
							DialogFragment newFragment = new AddRoommatesSugestDialog();
							newFragment.show(getFragmentManager(), "aaa");
							//******************************

							homeIdParse = parseHomeObject.getObjectId();
							parsePersonObject.put(PERSON_FIELD_HOUSE, parseHomeObject);
							parsePersonObject.put(PERSON_FIELD_CREDIT, 0.0); 
							parsePersonObject.saveInBackground(new SaveCallback() {

								@Override
								public void done(ParseException e) {
									Intent myIntent = new Intent(getApplication(), BalanceReceiver.class);
									myIntent.putExtra("homeId", parseHomeObject.getObjectId());
									PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplication(), 0, myIntent,0);

									Calendar calendar = Calendar.getInstance();
									calendar.set(Calendar.HOUR_OF_DAY, 13);
									calendar.set(Calendar.MINUTE, 00);
									calendar.set(Calendar.SECOND, 00);
									calendar.add(Calendar.DAY_OF_YEAR, 31);

									AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
									alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 
											calendar.getTimeInMillis(), 1000*60*60*24*31, pendingIntent);
								}
							});
						}
						else
						{
							e.printStackTrace();
						}
					}
				});



				break;

			case HOME_CODE_ENTERED_RESULT_CODE:
				currentEntryCode = intent.getStringExtra("CODE");
				ParseQuery<ParseObject> query = ParseQuery.getQuery(CLASS_PENDING_ENTRY_CODES);
				query.getInBackground(currentEntryCode, new GetCallback<ParseObject>() {
					public void done(ParseObject entryCodeObject, ParseException e) 
					{
						if (e == null) 
						{
							if ( entryCodeObject != null )
							{
								// Entry Code  exists
								parseHomeObject = entryCodeObject.getParseObject(PENDING_ENTRY_CODES_FIELD_HOUSE);
								if (parseHomeObject != null)
								{
									// HOME FOUND
									try {
										parseHomeObject = parseHomeObject.fetch();
									} catch (ParseException e1) {
										e1.printStackTrace();
									}

									homeIdParse = parseHomeObject.getObjectId();

									// Delete used entry code:
									List<String> codesList = parseHomeObject.getList(HOUSE_FIELD_PENDING_ENTRY_CODES);

									if (codesList != null){
										codesList.remove(currentEntryCode);
									}
									else
									{
										Log.d("tt", "no codes list in home object");
									}
									parseHomeObject.put(HOUSE_FIELD_PENDING_ENTRY_CODES, codesList);

									//Add this person to house people list
									homePeopleList = (List<ParseObject>)(List<?>)parseHomeObject.getList(HOUSE_FIELD_PERSONS);
									homePeopleList.add(parsePersonObject);
									parseHomeObject.put(HOUSE_FIELD_PERSONS, homePeopleList);

									parseHomeObject.saveInBackground(new SaveCallback() {

										@Override
										public void done(ParseException e) {
											onHouseFound();
											parsePersonObject.put(PERSON_FIELD_HOUSE, parseHomeObject);
											parsePersonObject.put(PERSON_FIELD_CREDIT, 0); 
											parsePersonObject.saveInBackground();
										}
									});


								}

								entryCodeObject.deleteInBackground();
							}
							else
							{
								//no such entry code found
								Intent i = new Intent(getApplicationContext(), 
										NoExistingHomeActivity.class);
								startActivityForResult(i, NO_EXISTING_HOME_RQST_CODE);
							}
						} 
						else 
						{
							Log.i("DEBUG", "Error: " + e.getMessage());
						}
					}
				});

				break;

			case ACCOUNT_CHANGED_RESULT_CODE:
				wasConnected = false;
				if (!mGoogleApiClient.isConnecting()) 
				{
					mGoogleApiClient.connect();
				}
				break;

			default:
				break;
			}


			break;

		case SEND_INVITATION_RQST_CODE:
			//Invitation sent
			DialogFragment newFragment = new AddAdditionalPersonDialog();
			newFragment.show(getFragmentManager(), "aaa");
			break;

		default:
			break;
		}
	}

	public void onDisconnected() 
	{
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionSuspended(int cause) 
	{
		if (cause == CAUSE_NETWORK_LOST)
		{
			//TODO: network lost 
			Toast toast = Toast.makeText(getApplicationContext(), 
					"No internet connection :(",
					Toast.LENGTH_LONG);
			toast.show();
		}
		mGoogleApiClient.connect();		
	}


	@Override
	protected void onStop() 
	{
		super.onStop();
		mGoogleApiClient.disconnect();
	}

	class SlideitemListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			updateDisplay(position);
		}

	}

	private void updateDisplay(int position) {
		Fragment fragment = null;
		switch (position) {
		case 100:
			fragment = new LoadingFragment();
			break;
		case 0:
			if (locationTracker == null)
			{
				locationTracker = new LocationTracker(parsePersonObject, parseHomeObject.getParseGeoPoint("homeLocation"));

			}
			locationTracker.resume(getApplicationContext());
			fragment = new HomeBoardActivity();
			break;
		case 1:
			fragment = new GroceryListsActivity();
			break;
		case 2:
			fragment = new CleaningTasksActivity();
			break;
		case 3:
			fragment = new BillsAcitivity();
			Bundle bundleBill = new Bundle();
			bundleBill.putString("houseId", homeIdParse);
			fragment.setArguments(bundleBill);
			break;
		case 4:
			fragment = new BalanceActivity();
			Bundle bundleBalance = new Bundle();
			bundleBalance.putString("houseId", parseHomeObject.getObjectId());
			bundleBalance.putString("userName", userName);
			fragment.setArguments(bundleBalance);
			break;	
		case 5:
			fragment = new WhosHomeActivity();
			break;
		case 6:
			fragment = new HomeSettingsActivity();
			break;
		case 7:
			//Sign out
			signOut();

		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			try{
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
				getActionBar().show();
			}catch ( Exception e )
			{
				e.printStackTrace();
			}
			// update selected item and title, then close the drawer
			if (position < menutitles.length)
			{
				setTitle(menutitles[position]);
			}
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@SuppressLint("InflateParams")
	@Override
	public void setTitle(CharSequence title) 
	{
		//		mTitle = title;
		//		getActionBar().setTitle(mTitle);

		mTitle = title;
		//		getActionBar().setTitle(mTitle);

		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
		TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);

		Typeface fontBold = Typeface.createFromAsset(getAssets(), "SinkinSans-600SemiBold.otf");
		mTitleTextView.setTypeface(fontBold);

		mTitleTextView.setText(mTitle);

		//		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);

		getActionBar().setCustomView(mCustomView);
		getActionBar().setDisplayShowCustomEnabled(true);

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.home_board, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) 
		{
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default :
			return super.onOptionsItemSelected(item);
		}
	}

	/***
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) 
	{
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onAddRoommatesSuggestionPositiveClick(DialogFragment dialog) 
	{
		DialogFragment newFragment = new AddRoomatesDialog();
		newFragment.show(getFragmentManager(), "ddd");
	}

	@Override
	public void onAddRoommates(DialogFragment dialog)
	{
		parseUUIDObject = new ParseObject(CLASS_PENDING_ENTRY_CODES);
		parseUUIDObject.put(PENDING_ENTRY_CODES_FIELD_HOUSE, parseHomeObject );
		parseUUIDObject.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e == null)
				{
					newUuid = parseUUIDObject.getObjectId();
					try{
						List<String> entryCodes = parseHomeObject.getList(HOUSE_FIELD_PENDING_ENTRY_CODES);
						entryCodes.add(newUuid);
						parseHomeObject.put(HOUSE_FIELD_PENDING_ENTRY_CODES, entryCodes);
						parseHomeObject.saveInBackground( new SaveCallback() {

							@Override
							public void done(ParseException e) {

								Intent intent = new Intent(Intent.ACTION_SEND);
								intent.setType("text/html");
								intent.putExtra(Intent.EXTRA_SUBJECT, "Invitation to RoomMate");
								intent.putExtra(Intent.EXTRA_TEXT, "Hi!\n" +
										"You are invited to use the RoomMate application!\n" +
										"Step 1: Download the application here: <link>\n" +
										"Step 2: Open the RoomMate and sign in with your google account\n" +
										"Step 3: Copy/paste this code into the app:\n" +
										newUuid + 
										"\nStep 4: Have fun!");

								startActivityForResult(Intent.createChooser(intent, "Send Email"), SEND_INVITATION_RQST_CODE);
							}
						});


					}
					catch(Exception e1)
					{
						e1.printStackTrace();

					}

				}
				else
				{
					e.printStackTrace();
				}

			}
		});
	}

	private void onHouseFound (){
		// Get home data

		ParseInstallation pi = ParseInstallation.getCurrentInstallation();
		pi.put(PERSON_FIELD_HOUSE, homeIdParse);
		pi.put("userId", parsePersonObject.getObjectId());
		pi.saveInBackground();

		homePeopleList = parseHomeObject.getList(HOUSE_FIELD_PERSONS);

		homePeopleMap = new HashMap<String, PersonData>();

		for (ParseObject person : homePeopleList){
			homePeopleMap.put(person.getObjectId(), new PersonData(person));
		}

		updateDisplay(0);
	}


	public HashMap<String, PersonData> getHomePeopleMap(){
		parseHomeObject = getHomeObject();
		if (parseHomeObject != null)
		{
			homePeopleList = parseHomeObject.getList(HOUSE_FIELD_PERSONS);
			HashMap<String, PersonData> newHomePeopleMap = new HashMap<String, PersonData>();

			for (ParseObject person : homePeopleList){
				String id = person.getObjectId();
				if (homePeopleMap.containsKey(id)){
					//this person already exists in the map
					newHomePeopleMap.put(id, homePeopleMap.get(id));
				}
				else{
					newHomePeopleMap.put(id, new PersonData(person));
				}
			}
			homePeopleMap = newHomePeopleMap;
		}

		return homePeopleMap;

	}

	public ParseObject getHomeObject(){
		try{
			if (parseHomeObject != null){
				parseHomeObject = parseHomeObject.fetch();
			}
		} catch(ParseException e){
			e.printStackTrace();
		}
		return parseHomeObject;
	}

	public void onHomeLocationChanged(ParseGeoPoint l) {
		locationTracker.setHomeLocation(l);
	}

	public void signOut()
	{
		if (locationTracker != null)
		{
			locationTracker.pause();
		}
		if (mGoogleApiClient.isConnected()) 
		{
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			wasConnected = false;
			mGoogleApiClient.connect();
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (locationTracker != null)
		{
			//locationTracker.pause();
		}
	}


	public void showMessageDialog(Context context, String data){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		// set title
		alertDialogBuilder.setTitle("Roommate message");
		// set dialog message
		
		try {
			alertDialogBuilder.setMessage((new JSONObject(data)).getString("alert"));
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		alertDialogBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}

