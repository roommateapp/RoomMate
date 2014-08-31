package il.ac.huji.roommate;


import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class BillsAcitivity extends ListFragment {

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.activity_bills, container, false);
		BillsAdapter adapter = new BillsAdapter(getActivity().getBaseContext(), generateData());
		setListAdapter(adapter);

		EditText newBillEdit = (EditText) rootView.findViewById(R.id.new_bill);
		//	Button addBillBtn = (Button)rootView.findViewById(R.id.add_bill);
		TextView billNameTitleTxt = (TextView)rootView.findViewById(R.id.bill_name_title);
		TextView billAmmountTitleTxt = (TextView)rootView.findViewById(R.id.bill_ammount_title);
		TextView billDateTitleTxt = (TextView)rootView.findViewById(R.id.bill_due_date_title);

		TextView lastModifyTxt = (TextView)rootView.findViewById(R.id.last_modified_txt);
		TextView lastModifyBy = (TextView)rootView.findViewById(R.id.last_modified_by);
		TextView lastModifyOnTxt = (TextView)rootView.findViewById(R.id.last_modified_on_txt);
		TextView lastModifyOn = (TextView)rootView.findViewById(R.id.last_modified_on);


		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Montserrat-Regular.ttf");
		Typeface fontBold = Typeface.createFromAsset(getActivity().getAssets(), "Montserrat-Bold.ttf");
		newBillEdit.setTypeface(font);
		billNameTitleTxt.setTypeface(fontBold);
		billAmmountTitleTxt.setTypeface(fontBold);
		billDateTitleTxt.setTypeface(fontBold);

		lastModifyBy.setTypeface(font);
		lastModifyOn.setTypeface(font);
		lastModifyOnTxt.setTypeface(font);
		lastModifyTxt.setTypeface(font);


		return rootView;
	}

	private ArrayList<BillsModel> generateData(){
		ArrayList<BillsModel> models = new ArrayList<BillsModel>();
		models.add(new BillsModel("Arnona", 1457.8));
		models.add(new BillsModel("Market", 100));
		models.add(new BillsModel("Supermarketrrrrrrrrrrrrrrrr", 800));

		return models;
	}
}
