package il.ac.huji.roommate;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.Image;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseBroadcastReceiver;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;
import com.parse.SaveCallback;


public class MainActivity extends Activity implements
ConnectionCallbacks, OnConnectionFailedListener
{

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
	
	public final static int SIGN_IN_ACTIVITY_CODE = 2;
	public final static int NO_EXISTING_HOME_RQST_CODE = 3;
	public final static int NEW_HOME_CREATED_RESULT_CODE = 4;
	
	public static final String CLASS_PERSON = "Person";
	public static final String PERSON_FIELD_ID = "personGoogleId";
	public static final String PERSON_FIELD_HOUSE = "house";
	
	public static final String CLASS_HOUSE = "House";
	public static final String HOUSE_FIELD_ID = "houseId";
	public static final String HOUSE_FIELD_PENDING_ENTRY_CODES = "pendingEntryCodes";
	public static final String HOUSE_FIELD_PERSONS_IDS = "personsIds";
	
	public String userName;
	private String personGoogleId;
	public ArrayList<String> homePeopleList;
	public ParseObject parsePersonObject;
	public ParseObject parseHomeObject;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Here we initialize connection with Google API
		mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API)
        .addScope(Plus.SCOPE_PLUS_PROFILE) // we don't post on google+, just get user data
        .build();
		//-------------------------------
		
		Parse.initialize(this, "exAWKD0hh8xWzri3FbLi9EMQAWOBNjblEpKRNSpu", "Wmk9gVhud0DQ2W0tJcDHvQIpi1fecNfbHyXTI80w");
		
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

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				android.R.drawable.ic_menu_more, R.string.app_name, R.string.app_name) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			updateDisplay(0);
		}
	}
	
	@Override
	protected void onStart() 
	{
		super.onStart();
		
		// Here we try to connect to google API via Google+ user
		// This method returns immediately, and connects to the service in the background.
		// If the connection is successful, onConnected(Bundle) is called 
		// and enqueued items are executed. On a failure, 
		// onConnectionFailed(ConnectionResult) is called.
		mGoogleApiClient.connect();
	}
	
	
	public void onConnectionFailed(ConnectionResult result) 
	{
		// If we got here it means that onStart was called but
		// the connection to Google Plus was unsuccessful so
		// we need to go to SignInActivity.
		if (!mIntentInProgress) 
		{
			mIntentInProgress = true;
			Intent i = new Intent(getApplicationContext(), SignInActivity.class);
			startActivityForResult(i, SIGN_IN_ACTIVITY_CODE);
		}
	}

	public void onConnected(Bundle connectionHint) 
	{
		// We've resolved any connection errors.  mGoogleApiClient can be used to
		// access Google APIs on behalf of the user.
		
		Log.i("DEBUG", "2");
				
		Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
		if (currentPerson != null) 
		{
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
				userName = Plus.AccountApi.getAccountName(mGoogleApiClient);
			}
			
			personGoogleId = currentPerson.getId();
			Image personPhoto = currentPerson.getImage();
		
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
							Log.i("DEBUG", "3");
							parsePersonObject = new ParseObject(CLASS_PERSON);
							parsePersonObject.put(PERSON_FIELD_ID, personGoogleId);
							parsePersonObject.put(PERSON_FIELD_HOUSE, "");
							parsePersonObject.saveInBackground();
						}
						else
						{
							// The user already exists in database
							parsePersonObject = personList.get(0);
						}
						if (parsePersonObject.getString(PERSON_FIELD_HOUSE).length() == 0)
						{
							Log.i("DEBUG", "4");
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
		}
	}
	
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) 
	{
		switch (responseCode) 
		{
		case SIGN_IN_ACTIVITY_CODE:
			Log.i("DEBUG", "sss");
			mIntentInProgress = false;
			if (!mGoogleApiClient.isConnecting()) 
		    {
		    	mGoogleApiClient.connect();
		    	Log.i("DEBUG", "1");
		    }
			break;
			
		case NEW_HOME_CREATED_RESULT_CODE:
			Log.i("DEBUG", "new home");
			homePeopleList = new ArrayList<String>();
			homePeopleList.add(personGoogleId);
			
			parseHomeObject = new ParseObject(CLASS_HOUSE);
			parseHomeObject.put(HOUSE_FIELD_PERSONS_IDS, homePeopleList);
			parseHomeObject.saveInBackground(new SaveCallback() {
				
				@Override
				public void done(ParseException e) 
				{
					if (e==null)
					{
						parsePersonObject.put(PERSON_FIELD_HOUSE, 
								parseHomeObject.getObjectId());
						parsePersonObject.saveInBackground();
					}
				}
			});
					
			
		default:
			break;
		}
		
		{
		    
		}
	}
	
	public void onDisconnected() {
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionSuspended(int cause) 
	{
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
		case 0:
			fragment = new HomeBoardActivity();
			break;
		case 1:
			fragment = new GroceryListsActivity();
			break;
		case 2:
			//listFragment = new CleaningTasksActivity();
			//cleaning = true;
			fragment = new CleaningTasksActivity();
			break;
		case 3:
			fragment = new BillsAcitivity();
			break;
		case 4:
			fragment = new BalanceActivity();
			break;	
		case 5:
			fragment = new WhosHomeActivity();
			break;
		case 6:
			fragment = new HomeSettingsActivity();
			break;
		case 7:
			//Sign out
		    if (mGoogleApiClient.isConnected()) 
		    {
		      Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
		      mGoogleApiClient.disconnect();
		      mGoogleApiClient.connect();
		    }
				
		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
			// update selected item and title, then close the drawer
			setTitle(menutitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home_board, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
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
	public boolean onPrepareOptionsMenu(Menu menu) {
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


}