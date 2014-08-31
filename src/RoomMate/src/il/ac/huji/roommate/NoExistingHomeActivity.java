package il.ac.huji.roommate;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class NoExistingHomeActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, OnClickListener 
{
	private GoogleApiClient mGoogleApiClient;
	public final static int SIGN_IN_ACTIVITY_CODE = 2;
	
	/* A flag indicating that a PendingIntent is in progress and prevents
	   * us from starting further intents.
	   */
	public boolean mIntentInProgress;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_no_existing_home);
		
		// Here we initialize connection with Google API
		mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API)
        .addScope(Plus.SCOPE_PLUS_PROFILE) // we don't post on google+, just get user data
        .build();
		//-------------------------------
		
		findViewById(R.id.create_new_home_button).setOnClickListener(this);
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
		    mIntentInProgress = false;
		
		    if (!mGoogleApiClient.isConnecting()) 
		    {
		    	mGoogleApiClient.connect();
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
			Intent intent = new Intent();
	        setResult( MainActivity.NEW_HOME_CREATED_RESULT_CODE, intent );
	        finish();
			break;

		default:
			break;
		}

	}

}