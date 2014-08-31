package il.ac.huji.roommate;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class WhosHomeAdapter extends ArrayAdapter<WhosHomeModel> {

	private final Context context;
	private final ArrayList<WhosHomeModel> modelsArrayList;

	public WhosHomeAdapter(Context context, ArrayList<WhosHomeModel> modelsArrayList) {

		super(context, R.layout.whos_home_list_item, modelsArrayList);
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

		rowView = inflater.inflate(R.layout.whos_home_list_item, parent, false);

		// 3. Get icon,title & counter views from the rowView
		TextView nameView = (TextView) rowView.findViewById(R.id.is_home_name);
		//ImageView imgView = (ImageView) rowView.findViewById(R.id.is_home_img);

		// 4. Set the text for textView 
		nameView.setText(modelsArrayList.get(position).getName());
		//imgView.setBackgroundDrawable(modelsArrayList.get(position).getImage());
		
		Typeface font = Typeface.createFromAsset(getContext().getAssets(), "Montserrat-Regular.ttf");
		nameView.setTypeface(font);

		// 5. retrn rowView
		return rowView;
	}
}