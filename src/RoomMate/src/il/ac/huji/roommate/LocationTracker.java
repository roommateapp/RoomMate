package il.ac.huji.roommate;

import org.apache.http.util.LangUtils;

import android.R.bool;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

public class LocationTracker implements 
ConnectionCallbacks,
OnConnectionFailedListener,
LocationListener{

	private LocationClient mLocationClient;
	private ParseObject parsePersonObject;
	private ParseGeoPoint homeLocation;

	// These settings are the same as the settings for the map. 
	// They will in fact give you updates
	// at the maximal rates currently possible.
	private static LocationRequest REQUEST = LocationRequest.create()
			.setInterval(60000)         // 60 seconds = 1 minute
			.setFastestInterval(10000)    // 10s
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
	public LocationTracker(ParseObject personObject, ParseGeoPoint hL){
		parsePersonObject = personObject;
		homeLocation = hL;
	}
	
	public void resume(Context context) {
		setUpLocationClientIfNeeded(context);
		mLocationClient.connect();
	}

	public void pause() {
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}

	private void setUpLocationClientIfNeeded(Context context) {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(
					context,
					this,  // ConnectionCallbacks
					this); // OnConnectionFailedListener
		}
	}

	/**
	 * Implementation of {@link LocationListener}.
	 */
	@Override
	public void onLocationChanged(Location location) {
		Log.d("tt", "changed to "+location.getLatitude() + ", " + location.getLongitude());
		userIsAtHome(location);
	}

	/**
	 * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(
				REQUEST,
				this);  // LocationListener
		Log.d("tt", "connected");
	}

	/**
	 * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
	 */
	@Override
	public void onDisconnected() {
		// Do nothing
	}

	/**
	 * Implementation of {@link OnConnectionFailedListener}.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Do nothing
	}
	
	public Location getCurrentLocation(){
		if (mLocationClient != null && mLocationClient.isConnected()) {
			Location l = mLocationClient.getLastLocation();
			return l;
		}
		return null;
	}
	
	public void setHomeLocation(ParseGeoPoint l){
		homeLocation = l;
		userIsAtHome(mLocationClient.getLastLocation());
	}
	
	private boolean userIsAtHome(Location newUserLocation){
		boolean result = false;
		if (homeLocation != null && newUserLocation != null){
			float [] results = new float[1];
			Location.distanceBetween(
					homeLocation.getLatitude(), 
					homeLocation.getLongitude(), 
					newUserLocation.getLatitude(), 
					newUserLocation.getLongitude(), results);
			if (results[0] < 300){
				result = true;
			}
		}
		parsePersonObject.put(MainActivity.PERSON_FIELD_AT_HOME, result);
		parsePersonObject.saveInBackground();
		
		ParseInstallation pi = ParseInstallation.getCurrentInstallation();
		pi.put(MainActivity.PERSON_FIELD_AT_HOME, result);
		pi.saveInBackground();
	  
		return result;
	}

}
