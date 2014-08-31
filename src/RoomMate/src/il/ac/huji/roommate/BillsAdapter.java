package il.ac.huji.roommate;

import java.sql.Date;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class BillsAdapter extends ArrayAdapter<BillsModel>{

	private Context context;
	private ArrayList<BillsModel> modelsArrayList;

	public BillsAdapter(Context context, ArrayList<BillsModel> modelsArrayList) {

		super(context, R.layout.bills_item, modelsArrayList);
		this.context = context;
		this.modelsArrayList = modelsArrayList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// 1. Create inflater 
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// 2. Get rowView from inflater

		View rowView = null;

		rowView = inflater.inflate(R.layout.bills_item, parent, false);

		// 3. Get icon,title & counter views from the rowView
		TextView nameView = (TextView) rowView.findViewById(R.id.bill_name);
		TextView ammountView = (TextView) rowView.findViewById(R.id.bill_ammount);
		TextView dateView = (TextView) rowView.findViewById(R.id.bill_due_date);

		//	Log.i("DATE VIEW IS:" + (modelsArrayList.get(position).getDueDate()).toString(),"eeeeee");

		// 4. Set the text for textView 
		nameView.setText(modelsArrayList.get(position).getName());
		ammountView.setText(String.valueOf(modelsArrayList.get(position).getAmmoumt()));

		//		if (dateView != null){
		//			dateView.setText((modelsArrayList.get(position).getDueDate()).toString());
		//		} else{
		//			dateView.setText("");
		//		}

		Typeface font = Typeface.createFromAsset(getContext().getAssets(), "Montserrat-Regular.ttf");
		nameView.setTypeface(font);
		ammountView.setTypeface(font);
		dateView.setTypeface(font);

		// 5. retrn rowView
		return rowView;
	}
}