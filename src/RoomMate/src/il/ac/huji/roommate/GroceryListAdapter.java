package il.ac.huji.roommate;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;


public class GroceryListAdapter extends ArrayAdapter<GroceryListModel>{

	private final Context context;
	private final List<GroceryListModel> modelsArrayList;

	public GroceryListAdapter(Context context, List<GroceryListModel> items) {
		super(context, R.layout.grocery_lists_item, items);
		this.context = context;
		this.modelsArrayList = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// 1. Create inflater 
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// 2. Get rowView from inflater

		View rowView = null;

		rowView = inflater.inflate(R.layout.grocery_lists_item, parent, false);

		// 3. Get icon,title & counter views from the rowView
		TextView nameView = (TextView) rowView.findViewById(R.id.list_name);
		

		// 4. Set the text for textView 
		nameView.setText(modelsArrayList.get(position).getName());
		
		Typeface font = Typeface.createFromAsset(getContext().getAssets(), "Montserrat-Regular.ttf");

		nameView.setTypeface(font);

		// 5. retrn rowView
		return rowView;
	}
}
