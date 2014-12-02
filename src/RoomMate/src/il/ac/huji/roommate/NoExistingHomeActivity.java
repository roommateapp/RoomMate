package il.ac.huji.roommate;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.internal.ct;
import com.google.android.gms.plus.Plus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NoExistingHomeActivity extends Activity 
		implements ConnectionCallbacks, OnConnectionFailedListener, OnClickListener 
{
	private GoogleApiClient mGoogleApiClient;
	public final static int SIGN_IN_ACTIVITY_CODE = 2;
	
	/* A flag indicating that a PendingIntent is in progress and prevents
	   * us from starting further intents.
	   */
	public boolean mIntentInProgress;
	
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_no_existing_home);
		
		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
		TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);

		Typeface fontBold = Typeface.createFromAsset(getAssets(), "SinkinSans-600SemiBold.otf");
		mTitleTextView.setTypeface(fontBold);

		mTitleTextView.setText("Sign in to a home");

		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		
		getActionBar().setCustomView(mCustomView);
		getActionBar().setDisplayShowCustomEnabled(true);
		
		Typeface font = Typeface.createFromAsset(getAssets(), "SinkinSans-400Regular.otf");
		
		TextView t1 = (TextView)findViewById(R.id.no_existing_home_message);
		t1.setTypeface(fontBold);
		
		TextView t2 = (TextView)findViewById(R.id.no_existing_home_text);
		t2.setTypeface(fontBold);
		
		Button createHomeButton = (Button)findViewById(R.id.create_new_home_button);
		EditText home_text = (EditText)findViewById(R.id.no_existing_home_home_code);
		Button confirmButton = (Button)findViewById(R.id.no_existing_home_confirm_button);
		Button signoutButton = (Button)findViewById(R.id.no_existing_home_sign_out_button);
		
		createHomeButton.setTypeface(font);
		home_text.setTypeface(font);
		confirmButton.setTypeface(font);
		signoutButton.setTypeface(font);
		
		// Here we initialize connection with Google API
		mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API)
        .addScope(Plus.SCOPE_PLUS_PROFILE) // we don't post on google+, just get user data
        .build();
		//-------------------------------
		
		createHomeButton.setOnClickListener(this);
		confirmButton.setOnClickListener(this);
		signoutButton.setOnClickListener(this);
	}

	@Override
	protected void onStart() 
	{
		super.onStart();
		mGoogleApiClient.connect();
		
	}

	@Override
	public void onConnected(Bundle arg0) 
	{
		
	}


	protected void onActivityResult(int requestCode, int responseCode, Intent intent) 
	{
		if (requestCode == SIGN_IN_ACTIVITY_CODE ) 
		{
			if (responseCode == RESULT_OK)
			{
				mIntentInProgress = false;
			    Intent intent3 = new Intent();
				setResult( MainActivity.ACCOUNT_CHANGED_RESULT_CODE, intent3 );
		        finish();
			    /*
		        if (!mGoogleApiClient.isConnecting()) 
			    {
			    	mGoogleApiClient.connect();
			    }
			    */
			}
		    
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
	protected void onStop() {
		super.onStop();
		mGoogleApiClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// If we got here it means that onStart was called but
		// the connection to Google Plus was unsuccessful so
		// we need to go to SignInActivity.
		if (!mIntentInProgress) 
		{
			mIntentInProgress = true;
			//startIntentSenderForResult( result.getResolution().getIntentSender(),
			//      RC_SIGN_IN, null, 0, 0, 0);
			Intent i = new Intent(this, SignInActivity.class);
			startActivityForResult(i, SIGN_IN_ACTIVITY_CODE);
		}
	}

	@Override
	public void onClick(View view) 
	{
		switch (view.getId()) 
		{
		case R.id.create_new_home_button:
			Intent intent1 = new Intent();
	        setResult( MainActivity.NEW_HOME_CREATED_RESULT_CODE, intent1 );
	        finish();
			break;
			
		case R.id.no_existing_home_confirm_button:
			Intent intent2 = new Intent();
			EditText edt = (EditText)findViewById(R.id.no_existing_home_home_code);
			if (edt != null)
			{
				String code = edt.getText().toString();
				intent2.putExtra("CODE", code);
		        setResult( MainActivity.HOME_CODE_ENTERED_RESULT_CODE, intent2 );
		        finish();
			}
			
			break;
			
		case R.id.no_existing_home_sign_out_button:
			/*
			Intent intent3 = new Intent();
			setResult( MainActivity.SIGN_OUT_CLICKED_RESULT_CODE, intent3 );
	        finish();
	        */
			if (mGoogleApiClient.isConnected()) 
			{
				Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
				mGoogleApiClient.disconnect();
				mGoogleApiClient.connect();
			}
			break;

		default:
			break;
		}
	}
}
