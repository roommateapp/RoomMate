package il.ac.huji.roommate;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("NewApi")
public class WhosHomeActivity extends ListFragment {

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.activity_whos_home, container, false);
		WhosHomeAdapter adapter = new WhosHomeAdapter(getActivity().getBaseContext(), generateData());
		setListAdapter(adapter);

		TextView whosHomeTxt = (TextView)rootView.findViewById(R.id.whos_home_txt);
		Button changeLocationBtn = (Button)rootView.findViewById(R.id.whos_home_change_location);
		EditText msgEdit = (EditText)rootView.findViewById(R.id.whos_home_msg);
		Button sendMessageBtn = (Button)rootView.findViewById(R.id.whos_home_send_msg);

		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Montserrat-Regular.ttf");
		Typeface fontBold = Typeface.createFromAsset(getActivity().getAssets(), "Montserrat-Bold.ttf");

		whosHomeTxt.setTypeface(fontBold);

		changeLocationBtn.setTypeface(font);
		msgEdit.setTypeface(font);
		sendMessageBtn.setTypeface(font);

		return rootView;
	}

	private ArrayList<WhosHomeModel> generateData(){
		ArrayList<WhosHomeModel> models = new ArrayList<WhosHomeModel>();
		models.add(new WhosHomeModel("Natasha"));
		models.add(new WhosHomeModel("Yael"));
		models.add(new WhosHomeModel("Hadas"));

		return models;
	}
}
