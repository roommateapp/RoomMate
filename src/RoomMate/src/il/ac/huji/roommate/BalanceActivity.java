package il.ac.huji.roommate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("NewApi")
public class BalanceActivity extends ListFragment {

	private String userName;
	private ArrayList<BalanceModel> models = new ArrayList<BalanceModel>();
	private View rootView;
	private BalanceAdapter adapter;
	private List<ParseObject> allRoommates;
	protected double houseSpendings;
	protected double eachRoommateSahre;
	private Double myDebt;
	private Double myCredit;
	protected TextView myDebtTxt;
	protected TextView myCreditTxt;
	private TextView balanceStartTxt;
	private MainActivity mainActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		//getActivity();
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args.getString("userName")!=null)
			userName = args.getString("userName");
		Log.i("BALANCE", "oncreate username " + userName);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mainActivity = (MainActivity) activity;
	}

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater.inflate(R.layout.activity_balance, container, false);

		myDebtTxt = (TextView)rootView.findViewById(R.id.my_bebt);
		myCreditTxt = (TextView)rootView.findViewById(R.id.my_credit);

		balanceStartTxt = (TextView)rootView.findViewById(R.id.balance_start);

		// load existing data to models array
		// get all people's googleId from House Parse-object
		//		ParseQuery<ParseObject> queryHouse = ParseQuery.getQuery("House");
		//		queryHouse.getInBackground(houseId, new GetCallback<ParseObject>() {
		//			@Override
		//			public void done(ParseObject house, ParseException e) {
		ParseObject house = mainActivity.getHomeObject();
		Date startDate = house.getDate("startDate");
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
		String startDateStr = formatter.format(startDate);

		int balIn = house.getInt("balanceInterval");
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DAY_OF_YEAR, balIn);
		Date endDate = cal.getTime();
		String endDateStr = formatter.format(endDate);

		balanceStartTxt.setText(startDateStr + " - " + endDateStr);
		Log.i("BALANCE", "HOUSE FOUND");
		allRoommates = house.getList("persons");
		Log.i("BALANCE", "ALLrOOMMATES LENGTH: " + allRoommates.size());

		houseSpendings = house.getDouble("spendings");
		eachRoommateSahre = houseSpendings/allRoommates.size();

		// for each id get the debt and credit from Person Parse-object and save it to models
		for (ParseObject person : allRoommates){
			//					Log.i("BALANCE", "roommate id: " + s);
			//					ParseQuery<ParseObject> queryPerson = ParseQuery.getQuery("Person");
			//					queryPerson.whereEqualTo("personGoogleId", s);
			//					queryPerson.whereEqualTo("house", houseId);
			//					ParseObject person;
			//					try {
			try {
				person = person.fetch();
			} catch (ParseException e1) {
				e1.printStackTrace();
			}				
			Log.i("BALANCE", "PERSON FOUND : " + person.getString("userName"));
			Double roommateCredit = person.getDouble("credit");
			Log.i("BALANCE", "CREDIT : " + person.getDouble("credit"));
			Log.i("BALANCE", "EACH SHARE " + eachRoommateSahre);
			Log.i("BALANCE", "person.(username) : " + person.getString("userName"));
			Log.i("BALANCE", "username : " + userName);
			if (person.getString("userName").equals(userName)){
				Log.i("BALANCE", "UPDATING MY CREDIT");
				if (roommateCredit < eachRoommateSahre){
					myDebt = eachRoommateSahre-roommateCredit;
					myCredit = 0.0;	
				}
				else if (roommateCredit > eachRoommateSahre){
					myDebt = 0.0;
					myCredit = roommateCredit-eachRoommateSahre;
				}
				else{
					myDebt = 0.0;
					myCredit = 0.0;
				}
				myDebtTxt.setText(String.valueOf(myDebt));
				myCreditTxt.setText(String.valueOf(myCredit));
			} else {
				if (roommateCredit < eachRoommateSahre)
					models.add(new BalanceModel(person.getString("userName"), eachRoommateSahre-roommateCredit, 0));
				else if (roommateCredit > eachRoommateSahre)
					models.add(new BalanceModel(person.getString("userName"), 0, roommateCredit-eachRoommateSahre));
				else
					models.add(new BalanceModel(person.getString("userName"), 0, 0));
			}
		}
		Log.i("BALANCE", "models size: " + String.valueOf(models.size()));
		adapter = new BalanceAdapter(getActivity().getBaseContext(), models, userName);
		setListAdapter(adapter);
		//	}
		//		});

		TextView balanceTxt1 = (TextView)rootView.findViewById(R.id.balance_period_txt);
		TextView myNameTxt = (TextView)rootView.findViewById(R.id.my_name);
		TextView infoText = (TextView)rootView.findViewById(R.id.info_text);

		//		ParseQuery<ParseObject> queryHouse = ParseQuery.getQuery("House");
		//		queryHouse.getInBackground(houseId, new GetCallback<ParseObject>() {
		//			
		//		}


		// BALANCE START - get start date from House ParseObject and set it to the "balanceStartTxt" view

		TextView billTitleTxt = (TextView)rootView.findViewById(R.id.bill_name_title);
		TextView billAmmountTitleTxt = (TextView)rootView.findViewById(R.id.bill_ammount_title);
		TextView billDateTitleTxt = (TextView)rootView.findViewById(R.id.bill_due_date_title);

		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "SinkinSans-400Regular.otf");
		Typeface fontBold = Typeface.createFromAsset(getActivity().getAssets(), "SinkinSans-600SemiBold.otf");
		balanceTxt1.setTypeface(font);
		infoText.setTypeface(font);
		balanceStartTxt.setTypeface(font);

		myNameTxt.setTypeface(font);
		myDebtTxt.setTypeface(font);
		myCreditTxt.setTypeface(font);

		billTitleTxt.setTypeface(fontBold);
		billDateTitleTxt.setTypeface(fontBold);
		billAmmountTitleTxt.setTypeface(fontBold);

		return rootView;
	}
}

