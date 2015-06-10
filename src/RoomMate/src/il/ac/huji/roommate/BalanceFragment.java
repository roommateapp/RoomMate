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
public class BalanceFragment extends ListFragment {

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
	
	private final String USER_NAME = "userName";
	private final String SPENDINGS = "spendings";
	private final String START_DATE = "startDate";
	private final String DATE_FORMAT = "dd/MM/yy";
	private final String BALANCE_INTERVAL = "balanceInterval";
	private final String PERSONS = "persons";
	private final double INIT_MONEY = 0.0;
	private final String FONT_REG = "SinkinSans-400Regular.otf";
	private final String FONT_BOLD = "SinkinSans-600SemiBold.otf";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args.getString(USER_NAME)!=null)
			userName = args.getString(USER_NAME);
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
		rootView = inflater.inflate(R.layout.balance_fragment, container, false);

		myDebtTxt = (TextView)rootView.findViewById(R.id.my_bebt);
		myCreditTxt = (TextView)rootView.findViewById(R.id.my_credit);
		balanceStartTxt = (TextView)rootView.findViewById(R.id.balance_start);

		ParseObject house = mainActivity.getHomeObject();
		Date startDate = house.getDate(START_DATE);
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		String startDateStr = formatter.format(startDate);

		int balIn = house.getInt(BALANCE_INTERVAL);
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DAY_OF_YEAR, balIn);
		Date endDate = cal.getTime();
		String endDateStr = formatter.format(endDate);

		balanceStartTxt.setText(startDateStr + " - " + endDateStr);
		allRoommates = house.getList(PERSONS);

		houseSpendings = house.getDouble(SPENDINGS);
		eachRoommateSahre = houseSpendings/allRoommates.size();

		// for each id get the debt and credit from Person Parse-object and save it to models
		for (ParseObject person : allRoommates){
			try {
				person = person.fetch();
			} catch (ParseException e1) {
				e1.printStackTrace();
			}				
			Double roommateCredit = person.getDouble("credit");
			if (person.getString(USER_NAME).equals(userName)){
				if (roommateCredit < eachRoommateSahre){
					myDebt = eachRoommateSahre-roommateCredit;
					myCredit = INIT_MONEY;	
				}
				else if (roommateCredit > eachRoommateSahre){
					myDebt = INIT_MONEY;
					myCredit = roommateCredit-eachRoommateSahre;
				}
				else{
					myDebt = INIT_MONEY;
					myCredit = INIT_MONEY;
				}
				myDebtTxt.setText(String.valueOf(myDebt));
				myCreditTxt.setText(String.valueOf(myCredit));
			} else {
				if (roommateCredit < eachRoommateSahre)
					models.add(new BalanceModel(person.getString(USER_NAME), eachRoommateSahre-roommateCredit, 0));
				else if (roommateCredit > eachRoommateSahre)
					models.add(new BalanceModel(person.getString(USER_NAME), 0, roommateCredit-eachRoommateSahre));
				else
					models.add(new BalanceModel(person.getString(USER_NAME), 0, 0));
			}
		}
		adapter = new BalanceAdapter(getActivity().getBaseContext(), models, userName);
		setListAdapter(adapter);

		// get all views of fragment
		TextView balanceTxt1 = (TextView)rootView.findViewById(R.id.balance_period_txt);
		TextView myNameTxt = (TextView)rootView.findViewById(R.id.my_name);
		TextView infoText = (TextView)rootView.findViewById(R.id.info_text);
		TextView billTitleTxt = (TextView)rootView.findViewById(R.id.bill_name_title);
		TextView billAmmountTitleTxt = (TextView)rootView.findViewById(R.id.bill_ammount_title);
		TextView billDateTitleTxt = (TextView)rootView.findViewById(R.id.bill_due_date_title);
		
		// set font
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), FONT_REG);
		Typeface fontBold = Typeface.createFromAsset(getActivity().getAssets(), FONT_BOLD);
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

