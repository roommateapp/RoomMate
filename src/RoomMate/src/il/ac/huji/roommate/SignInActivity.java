package il.ac.huji.roommate;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class SignInActivity extends Activity implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {
	
	/* Request code used to invoke sign in user interactions. */
	public static final int RC_SIGN_IN = 0;
	
	/* Track whether the sign-in button has been clicked so that we know to resolve
	 * all issues preventing sign-in without waiting.
	 */
	private boolean mSignInClicked;
	
	/* A flag indicating that a PendingIntent is in progress and prevents
	   * us from starting further intents.
	   */
	public boolean mIntentInProgress;
	
	private GoogleApiClient mGoogleApiClient;
	

	/* Store the connection result from onConnectionFailed callbacks so that we can
	 * resolve them when the user clicks sign-in.
	 */
	private ConnectionResult mConnectionResult;
	
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		
		getActionBar().show();
		
		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
		TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);

		Typeface fontBold = Typeface.createFromAsset(getAssets(), "SinkinSans-600SemiBold.otf");
		mTitleTextView.setTypeface(fontBold);

		mTitleTextView.setText("Sign in to RoomMate");

		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		
		getActionBar().setCustomView(mCustomView);
		getActionBar().setDisplayShowCustomEnabled(true);
		
		TextView txtView = (TextView)findViewById(R.id.sign_in_text);
		Typeface font = Typeface.createFromAsset(getAssets(), "SinkinSans-600SemiBold.otf");
		txtView.setTypeface(font);
		
		// Here we initialize connection with Google API
		mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API)
        .addScope(Plus.SCOPE_PLUS_PROFILE) // we don't post on google+, just get user data
        .build();
		//-------------------------------
		
		findViewById(R.id.sign_in_button).setOnClickListener(this);
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
	

	
	/* A helper method to resolve the current ConnectionResult error. */
	private void resolveSignInError() 
	{
		if (mConnectionResult.hasResolution()) 
		{
			try {
		      mIntentInProgress = true;
		      startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
		    		  RC_SIGN_IN, null, 0, 0, 0);
		    } catch (SendIntentException e) {
		      // The intent was canceled before it was sent.  Return to the default
		      // state and attempt to connect to get an updated ConnectionResult.
		      mIntentInProgress = false;
		      mGoogleApiClient.connect();
		    }
		}
	}

	

	public void onConnectionFailed(ConnectionResult result) 
	{
		if (!mIntentInProgress) 
		{
			// Store the ConnectionResult so that we can use it later when the user clicks
			// 'sign-in'.
			mConnectionResult = result;

			if (mSignInClicked) 
			{
				// The user has already clicked 'sign-in' so we attempt to resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}
	}
	
	@Override
	public void onClick(View view) 
	{
		if (view.getId() == R.id.sign_in_button && !mGoogleApiClient.isConnecting()) 
		{
			mSignInClicked = true;
			resolveSignInError();
		}
	}
	
	
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) 
	{
		if (requestCode == RC_SIGN_IN) 
		{
			if (responseCode != RESULT_OK) 
			{
				mSignInClicked = false;
		    }

		    mIntentInProgress = false;

		    if (!mGoogleApiClient.isConnecting()) 
		    {
		    	mGoogleApiClient.connect();
		    }
		}
	}
	
	
	public void onConnected(Bundle connectionHint) 
	{
		mSignInClicked = false;
		Intent intent = new Intent();
        setResult(MainActivity.RESULT_OK ,intent);  
        finish();//finishing activity  
	}


	@Override
	public void onConnectionSuspended(int arg0) 
	{

	}

}
