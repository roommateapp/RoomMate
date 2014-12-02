package il.ac.huji.roommate;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WhosHomeAdapter extends ArrayAdapter<WhosHomeModel> {

	private final Context context;
	private final ArrayList<WhosHomeModel> modelsArrayList;
	private CheckBox checkBox;
	private TextView nameView;
	private ImageView imgView;

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

		// 3. Get checkBox, icon, title & counter views from the rowView
		checkBox = (CheckBox) rowView.findViewById(R.id.is_home);
		nameView = (TextView) rowView.findViewById(R.id.is_home_name);
		imgView = (ImageView) rowView.findViewById(R.id.is_home_img);

		// 4. Set the text, image, checked if at home
		if ( !modelsArrayList.get(position).isAtHome()){
			checkBox.setChecked(false);
			checkBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					checkBox.setChecked(false);
					Toast.makeText(getContext(), "This roommate is not at home", Toast.LENGTH_SHORT).show();
				}
			});
		}
		else{
			checkBox.setChecked(true);
			checkBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					checkBox.setChecked(true);
				}
			});
		}
		nameView.setText(modelsArrayList.get(position).getName());
		
		imgView.setImageBitmap(modelsArrayList.get(position).getImage());
		
	    Typeface font = Typeface.createFromAsset(getContext().getAssets(), "SinkinSans-400Regular.otf");
		nameView.setTypeface(font);

		// 5. retrn rowView
		return rowView;
	}
}