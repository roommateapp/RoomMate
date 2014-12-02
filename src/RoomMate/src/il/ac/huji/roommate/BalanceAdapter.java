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
	private String userName;

	public BalanceAdapter(Context context, ArrayList<BalanceModel> modelsArrayList, String userName) {

		super(context, R.layout.balance_list_item, modelsArrayList);
		this.context = context;
		this.modelsArrayList = modelsArrayList;
		this.userName = userName;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.balance_list_item, parent, false);
		}
		BalanceModel i = modelsArrayList.get(position);

		if (i != null) {
			// 3. Get icon,title & counter views from the rowView
			TextView nameView = (TextView) convertView.findViewById(R.id.balance_name);
			TextView debtAmmountView = (TextView) convertView.findViewById(R.id.debt_ammount);
			TextView creditAmmountView = (TextView) convertView.findViewById(R.id.credit_ammount);

			// 4. Set the text for textView 
			if (i.getName()!= userName){
				nameView.setText(i.getName());
				debtAmmountView.setText("-"+String.valueOf(i.getDebt()));
				creditAmmountView.setText("+"+String.valueOf(i.getCredit()));
			}
			Typeface font = Typeface.createFromAsset(getContext().getAssets(), "SinkinSans-400Regular.otf");
			nameView.setTypeface(font);
			debtAmmountView.setTypeface(font);
			creditAmmountView.setTypeface(font);
		}
		// 5. retrn rowView
		return convertView;
	}
}
