package il.ac.huji.roommate;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class CleaningAdapter extends ArrayAdapter<CleaningModel> {

	private final Context context;
	private final ArrayList<CleaningModel> modelsArrayList;

	public CleaningAdapter(Context context, ArrayList<CleaningModel> modelsArrayList) {

		super(context, R.layout.cleaning_task_list_item, modelsArrayList);
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

		rowView = inflater.inflate(R.layout.cleaning_task_list_item, parent, false);

		// 3. Get icon,title & counter views from the rowView
		EditText taskView = (EditText) rowView.findViewById(R.id.task_name);
		TextView personView = (TextView) rowView.findViewById(R.id.person_for_task);

		// 4. Set the text for textView 
		taskView.setText(modelsArrayList.get(position).getTask());
		personView.setText(modelsArrayList.get(position).getPerson());
		
		Typeface font = Typeface.createFromAsset(getContext().getAssets(), "Montserrat-Regular.ttf");
		taskView.setTypeface(font);
		personView.setTypeface(font);

		// 5. retrn rowView
		return rowView;
	}
}