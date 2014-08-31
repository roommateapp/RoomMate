package il.ac.huji.roommate;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("NewApi")
public class HomeSettingsActivity extends Fragment {

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater .inflate(R.layout.activity_home_settings, container, false); 

		Button inviteBtn = (Button)rootView.findViewById(R.id.invite_roommates);
		TextView cleaningTxt = (TextView)rootView.findViewById(R.id.cleaning_interval_txt);
		EditText cleaningEdit = (EditText)rootView.findViewById(R.id.cleaning_interval);
		TextView balanceTxt = (TextView)rootView.findViewById(R.id.balance_interval_txt);
		EditText balanceEdit = (EditText)rootView.findViewById(R.id.balnce_interval);
		TextView billsTxt = (TextView)rootView.findViewById(R.id.bills_annonce_txt);
		EditText billsEdit = (EditText)rootView.findViewById(R.id.bills_notification);
		TextView locationTxt = (TextView)rootView.findViewById(R.id.home_location_txt);
		TextView imageTxt = (TextView)rootView.findViewById(R.id.image_txt);
		TextView lastModifyTxt = (TextView)rootView.findViewById(R.id.last_modified_txt);
		TextView lastModifyBy = (TextView)rootView.findViewById(R.id.last_modified_by);
		TextView lastModifyOnTxt = (TextView)rootView.findViewById(R.id.last_modified_on_txt);
		TextView lastModifyOn = (TextView)rootView.findViewById(R.id.last_modified_on);

		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Montserrat-Regular.ttf");

		inviteBtn.setTypeface(font);
		cleaningTxt.setTypeface(font);
		cleaningEdit.setTypeface(font);
		balanceTxt.setTypeface(font);
		balanceEdit.setTypeface(font);
		billsEdit.setTypeface(font);
		billsTxt.setTypeface(font);
		locationTxt.setTypeface(font);
		imageTxt.setTypeface(font);
		lastModifyBy.setTypeface(font);
		lastModifyOn.setTypeface(font);
		lastModifyOnTxt.setTypeface(font);
		lastModifyTxt.setTypeface(font);

		return rootView;
	}
}
