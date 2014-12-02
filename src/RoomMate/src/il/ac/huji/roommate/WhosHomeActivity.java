package il.ac.huji.roommate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SendCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class WhosHomeActivity extends ListFragment implements OnClickListener {

	private MainActivity mainActivity;
	private ArrayList<WhosHomeModel> models = new ArrayList<WhosHomeModel>();
	private WhosHomeAdapter adapter;
	private EditText msgEdit;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mainActivity = (MainActivity) activity;

		adapter = new WhosHomeAdapter(activity.getBaseContext(), models);
		setListAdapter(adapter);
	}

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.activity_whos_home, container, false);

		TextView whosHomeTxt = (TextView)rootView.findViewById(R.id.whos_home_txt);
		Button changeLocationBtn = (Button)rootView.findViewById(R.id.whos_home_change_location);
		msgEdit = (EditText)rootView.findViewById(R.id.whos_home_msg);
		Button sendMessageBtn = (Button)rootView.findViewById(R.id.whos_home_send_msg);

		try{
			Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "SinkinSans-400Regular.otf");
			Typeface fontBold = Typeface.createFromAsset(getActivity().getAssets(), "SinkinSans-600SemiBold.otf");

			whosHomeTxt.setTypeface(fontBold);

			changeLocationBtn.setTypeface(font);
			msgEdit.setTypeface(font);
			sendMessageBtn.setTypeface(font);

		} catch (Exception e){
			e.printStackTrace();
		}

		sendMessageBtn.setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		generateData();
	}

	private void generateData(){
		models.clear();
		Map<String, PersonData> m = mainActivity.getHomePeopleMap();
		if (m != null){
			Collection<PersonData> people = m.values();	
			for (PersonData person : people ){
				models.add(new WhosHomeModel(
						person.getPersonParseObject().getString(MainActivity.PERSON_FIELD_USERNAME),
						person.atHome(),
						person.getPicture())
						);
			}
		}

	}

	@Override
	public void onClick(View arg0) {
		String messageText = msgEdit.getText().toString();
		msgEdit.setText("");

		ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
		pushQuery.whereEqualTo(MainActivity.PERSON_FIELD_AT_HOME, true);
		pushQuery.whereEqualTo(MainActivity.PERSON_FIELD_HOUSE, mainActivity.homeIdParse);

		// Send push notification to query
		ParsePush push = new ParsePush();
		push.setChannel("");
		push.setQuery(pushQuery);
		push.setMessage(mainActivity.userName + " says: " + messageText);
		push.sendInBackground( new SendCallback() {

			@Override
			public void done(ParseException e) {
				if (e == null){
					Toast.makeText(mainActivity, "The message was sent!", Toast.LENGTH_LONG).show();
				}
				else
				{
					e.printStackTrace();
					Toast.makeText(mainActivity, "A problem occured. Please try again later", Toast.LENGTH_LONG).show();
				}
			}
		});


	}
}
