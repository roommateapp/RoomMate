package il.ac.huji.roommate;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BalanceAdapter extends ArrayAdapter<BalanceModel>{

	private Context context;
	private ArrayList<BalanceModel> modelsArrayList;

	public BalanceAdapter(Context context, ArrayList<BalanceModel> modelsArrayList) {

		super(context, R.layout.balance_list_item, modelsArrayList);
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

		rowView = inflater.inflate(R.layout.balance_list_item, parent, false);

		// 3. Get icon,title & counter views from the rowView
		TextView nameView = (TextView) rowView.findViewById(R.id.balance_name);
		TextView debtAmmountView = (TextView) rowView.findViewById(R.id.debt_ammount);
		TextView creditAmmountView = (TextView) rowView.findViewById(R.id.credit_ammount);
		//		EditText dateView = (EditText) rowView.findViewById(R.id.bill_due_date);

		//	Log.i("DATE VIEW IS:" + (modelsArrayList.get(position).getDueDate()).toString(),"eeeeee");

		// 4. Set the text for textView 
		nameView.setText(modelsArrayList.get(position).getName());
		debtAmmountView.setText("-"+String.valueOf(modelsArrayList.get(position).getDebt()));
		creditAmmountView.setText("+"+String.valueOf(modelsArrayList.get(position).getCredit()));
		

		Typeface font = Typeface.createFromAsset(getContext().getAssets(), "Montserrat-Regular.ttf");
		nameView.setTypeface(font);
		debtAmmountView.setTypeface(font);
		creditAmmountView.setTypeface(font);
		//		if (dateView != null){
		//			dateView.setText((modelsArrayList.get(position).getDueDate()).toString());
		//		} else{
		//			dateView.setText("");
		//		}



		// 5. retrn rowView
		return rowView;
	}


}
