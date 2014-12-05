package il.ac.huji.roommate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class BillsAcitivity extends ListFragment {

	private BillsAdapter adapter;
	private EditText newBillEdit;
	View rootView;
	ArrayList<BillsModel> models = new ArrayList<BillsModel>();
	private MainActivity mainActivity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mainActivity = ((MainActivity)activity);
	}

	@Override
	public void onStart() {
		models.clear();
		ParseObject house = mainActivity.getHomeObject();
		if (house != null){
			List<ParseObject> bills = house.getList("bills");

			Iterator<ParseObject> iter = bills.iterator();

			while (iter.hasNext()) {
				ParseObject bill = iter.next();

				try {
					bill = bill.fetchIfNeeded();

					if (bill.getString("dueDate")==null || bill.getString("dueDate").equals("")){
						models.add(new BillsModel(bill.getString("name"), bill.getDouble("amount"),
								"", bill.getBoolean("notified"), bill.getObjectId()));
					} else {
						models.add(new BillsModel(bill.getString("name"), bill.getDouble("amount"),
								bill.getString("dueDate"), bill.getBoolean("notified"), bill.getObjectId()));
					}

				} catch (Exception e) {
					iter.remove();
					e.printStackTrace();
				}
			}

			adapter.notifyDataSetChanged();

			house.put("bills", bills);
			house.saveInBackground();

		}

		super.onStart();
	}

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater.inflate(R.layout.activity_bills, container, false);

		//		ParseQuery<ParseObject> query = ParseQuery.getQuery("SingleBill");
		//		query.whereEqualTo("houseId", house.getObjectId());
		//		query.findInBackground(new FindCallback<ParseObject>() {
		//			public void done(List<ParseObject> bills, ParseException e) {
		//				if (e == null) {
		//					if ( bills.size() != 0 ){
		//						for (int i=0; i<bills.size(); i++){
		//							if (bills.get(i).getString("dueDate")==null || bills.get(i).getString("dueDate").equals(""))
		//								models.add(new BillsModel(bills.get(i).getString("name"), bills.get(i).getDouble("amount"),
		//										"", bills.get(i).getBoolean("notified")));
		//							else
		//								models.add(new BillsModel(bills.get(i).getString("name"), bills.get(i).getDouble("amount"),
		//										bills.get(i).getString("dueDate"), bills.get(i).getBoolean("notified")));
		//						}
		//						adapter = new BillsAdapter(getActivity(), models, house.getObjectId());
		//						adapter.notifyDataSetChanged();
		//						setListAdapter(adapter);
		//					}
		//				} else {
		//					Log.i("DEBUG", "Error: " + e.getMessage());
		//				}
		//			}
		//		});

		newBillEdit = (EditText) rootView.findViewById(R.id.new_bill);
		Button addBillBtn = (Button)rootView.findViewById(R.id.add_bill);
		TextView billNameTitleTxt = (TextView)rootView.findViewById(R.id.bill_name_title);
		TextView billAmmountTitleTxt = (TextView)rootView.findViewById(R.id.bill_ammount_title);
		TextView billDateTitleTxt = (TextView)rootView.findViewById(R.id.bill_due_date_title);

		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "SinkinSans-400Regular.otf");
		Typeface fontBold = Typeface.createFromAsset(getActivity().getAssets(), "SinkinSans-600SemiBold.otf");
		newBillEdit.setTypeface(font);
		billNameTitleTxt.setTypeface(fontBold);
		billAmmountTitleTxt.setTypeface(fontBold);
		billDateTitleTxt.setTypeface(fontBold);

		/**
		 * On add-button click, open a SingleBillActivity, pass it the new name, the house id.
		 * Same name bills are allowed
		 */
		addBillBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SingleBillActivty.class);//view.getContext()
				intent.putExtra("billname", newBillEdit.getText().toString());
				intent.putExtra("houseId", mainActivity.homeIdParse);
				intent.putExtra("existingBill", false);
				intent.putExtra("notified", false);
				intent.putExtra("userName", mainActivity.userName);
				intent.putExtra("userId", mainActivity.parsePersonObject.getObjectId());
				startActivity(intent);
				newBillEdit.setText("");
			}
		});

		RelativeLayout rel = (RelativeLayout)rootView.findViewById(R.id.hhh);
		rel.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent ev)
			{
				InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
				return false;
			}
		});

		adapter = new BillsAdapter(getActivity(), models, 
				mainActivity.homeIdParse, 
				mainActivity.userName, 
				mainActivity.parsePersonObject.getObjectId());
		setListAdapter(adapter);


		return rootView;
	}
}


