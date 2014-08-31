package il.ac.huji.roommate;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ListFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("NewApi")
public class BalanceActivity extends ListFragment {

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.activity_balance, container, false);
		BalanceAdapter adapter = new BalanceAdapter(getActivity().getBaseContext(), generateData());
		setListAdapter(adapter);
		
		TextView balanceTxt1 = (TextView)rootView.findViewById(R.id.balance_period_txt);
		TextView balanceStartTxt = (TextView)rootView.findViewById(R.id.balance_start);
		TextView balanceTxt2 = (TextView)rootView.findViewById(R.id.balance_between);
		TextView balanceEndTxt = (TextView)rootView.findViewById(R.id.balance_end);
		
		TextView myNameTxt = (TextView)rootView.findViewById(R.id.my_name);
		TextView myDebtTxt = (TextView)rootView.findViewById(R.id.my_bebt);
		TextView myCreditTxt = (TextView)rootView.findViewById(R.id.my_credit);
		
		TextView billTitleTxt = (TextView)rootView.findViewById(R.id.bill_name_title);
		TextView billAmmountTitleTxt = (TextView)rootView.findViewById(R.id.bill_ammount_title);
		TextView billDateTitleTxt = (TextView)rootView.findViewById(R.id.bill_due_date_title);
		
		Button loadErlierBtn = (Button)rootView.findViewById(R.id.load_earlier_balance);
		
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Montserrat-Regular.ttf");
		Typeface fontBold = Typeface.createFromAsset(getActivity().getAssets(), "Montserrat-Bold.ttf");
		balanceTxt1.setTypeface(font);
		balanceStartTxt.setTypeface(font);
		balanceTxt2.setTypeface(font);
		balanceEndTxt.setTypeface(font);
		
		myNameTxt.setTypeface(font);
		myDebtTxt.setTypeface(font);
		myCreditTxt.setTypeface(font);
		
		billTitleTxt.setTypeface(fontBold);
		billDateTitleTxt.setTypeface(fontBold);
		billAmmountTitleTxt.setTypeface(fontBold);
		
		loadErlierBtn.setTypeface(font);
		
		
		return rootView;
	}

	private ArrayList<BalanceModel> generateData(){
		ArrayList<BalanceModel> models = new ArrayList<BalanceModel>();
		models.add(new BalanceModel("Natasha", 1457.8, 700));
		models.add(new BalanceModel("Yael", 100, 888));
		models.add(new BalanceModel("Hadas", 800, 6));

		return models;
	}
}

/**


**/