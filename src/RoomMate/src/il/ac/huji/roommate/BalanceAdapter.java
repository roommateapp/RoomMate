package il.ac.huji.roommate;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


/*
* Custum adapter for Balance activity list, extends ArrayAdapter. Used for adapting the
* view of the Balance-List items.
*/
public class BalanceAdapter extends ArrayAdapter<BalanceModel>{

	private Context context;
	private ArrayList<BalanceModel> modelsArrayList;
	private String userName;

	/**
	 * Constructor for BalanceAdapter
	 * @param context - the context in wich the adapter is called
	 * @param modelsArrayList - the Array-List storing the data (BalanceModels) to be desplayed
	 * @param userName - app's current user
	*/
	public BalanceAdapter(Context context, ArrayList<BalanceModel> modelsArrayList, String userName) {
		super(context, R.layout.balance_list_item, modelsArrayList);
		this.context = context;
		this.modelsArrayList = modelsArrayList;
		this.userName = userName;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// view requested for the first time
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.balance_list_item, parent, false);
		}
		// get the BalanceModel object in the specific position
		BalanceModel i = modelsArrayList.get(position);

		// if position exists
		if (i != null) {
			// get roommate's name, debt and credit views from the rowView
			TextView nameView = (TextView) convertView.findViewById(R.id.balance_name);
			TextView debtAmmountView = (TextView) convertView.findViewById(R.id.debt_ammount);
			TextView creditAmmountView = (TextView) convertView.findViewById(R.id.credit_ammount);

			// Set the text for textView 
			if (i.getName()!= userName){
				nameView.setText(i.getName());
				debtAmmountView.setText("-"+String.valueOf(i.getDebt()));
				creditAmmountView.setText("+"+String.valueOf(i.getCredit()));
			}
			// set the font
			Typeface font = Typeface.createFromAsset(getContext().getAssets(), "SinkinSans-400Regular.otf");
			nameView.setTypeface(font);
			debtAmmountView.setTypeface(font);
			creditAmmountView.setTypeface(font);
		}
		// retrn rowView
		return convertView;
	}
}
