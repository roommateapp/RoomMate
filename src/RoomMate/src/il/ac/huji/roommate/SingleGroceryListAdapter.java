package il.ac.huji.roommate;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

public class SingleGroceryListAdapter extends ArrayAdapter<GroceryModel> {

	private final Context context;
	private final ArrayList<GroceryModel> modelsArrayList;

	public SingleGroceryListAdapter(Context context, ArrayList<GroceryModel> modelsArrayList) {

		super(context, R.layout.grocery_list_item, modelsArrayList);

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

		rowView = inflater.inflate(R.layout.grocery_list_item, parent, false);

		// 3. Get icon,title & counter views from the rowView
		EditText taskView = (EditText) rowView.findViewById(R.id.grocery_item);
	//	TextView personView = (TextView) rowView.findViewById(R.id.person_for_task);

		// 4. Set the text for textView 
		taskView.setText(modelsArrayList.get(position).getTask());
	//	personView.setText(modelsArrayList.get(position).getPerson());

		// 5. retrn rowView
		return rowView;
	}
}