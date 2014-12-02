package il.ac.huji.roommate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ChooseHomeLocationActivity extends Activity
implements 
OnMapLongClickListener, 
OnClickListener {

	private GoogleMap mMap;
	private Marker currentMarker;
	private boolean prevLocationExists;
	private LatLng currentLocation;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent i = getIntent();
		if (i != null){
			prevLocationExists = i.getBooleanExtra("PrevLocationExists", false);
			double lat = i.getDoubleExtra("Lat", 0);
			double lng = i.getDoubleExtra("Lng", 0);
			if (lat != 0 || lng != 0)
			{
				currentLocation = new LatLng(lat, lng);
			}
			
		}
		setContentView(R.layout.activity_choose_home_location);
		setUpMapIfNeeded();
		
		Button button = (Button)findViewById(R.id.choose_home_location_confirm_button);
		button.setOnClickListener(this);
		
		TextView tapText = (TextView)findViewById(R.id.tap_text);
		
		Typeface font = Typeface.createFromAsset(getAssets(), "SinkinSans-400Regular.otf");
		button.setTypeface(font);
		tapText.setTypeface(font);
		
	}

	@Override
	protected void onResume() {
		
		super.onResume();
		setUpMapIfNeeded();
		
		if (currentMarker != null){
			currentMarker.remove();
		}
		
		if (currentLocation != null){
			mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
					.target(currentLocation)
                    .zoom(15.5f)
                    .bearing(300)
                    .tilt(50)
                    .build()), null);
			if (prevLocationExists){
				currentMarker = mMap.addMarker(new MarkerOptions()
				.position(currentLocation).title("Current Home Location"));
			}
		}
		

		
	}

	

	/**
	 * Button to get current Location. This demonstrates how to get the current Location as required
	 * without needing to register a LocationListener.
	 */
	public void showMyLocation(View view) {
		/*
		if (mLocationClient != null && mLocationClient.isConnected()) {
			String msg = "Location = " + mLocationClient.getLastLocation();
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		}
		*/
	}

	

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				mMap.setMyLocationEnabled(true);
				mMap.setOnMapLongClickListener(this);
				mMap.setBuildingsEnabled(true);
				UiSettings mUiSettings = mMap.getUiSettings();
				mUiSettings.setMyLocationButtonEnabled(true);
			}
		}
	}

	@Override
	public void onMapLongClick(LatLng point) 
	{
		if (currentMarker != null){
			currentMarker.remove();
		}
		currentMarker = mMap.addMarker(new MarkerOptions().position(point).title("New Home Location"));
		//mTapTextView.setText("long pressed, point=" + point);
	}

	@Override
	public void onClick(View arg0) {
		if (currentMarker != null){
			Intent intent = new Intent();
			intent.putExtra("Lat", currentMarker.getPosition().latitude);
			intent.putExtra("Lng", currentMarker.getPosition().longitude);
			setResult( Activity.RESULT_OK, intent );
		}
		
		finish();
	}
}