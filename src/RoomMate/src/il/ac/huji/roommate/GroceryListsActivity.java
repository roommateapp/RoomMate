package il.ac.huji.roommate;

import java.util.ArrayList;

import android.app.ListFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class GroceryListsActivity extends ListFragment {

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View rootView = inflater.inflate(R.layout.activity_grocery_lists, container, false);
		GroceryListAdapter adapter = new GroceryListAdapter(getActivity().getBaseContext(), generateData());

		TextView lastModifyTxt = (TextView)rootView.findViewById(R.id.last_modified_txt);
		TextView lastModifyBy = (TextView)rootView.findViewById(R.id.last_modified_by);
		TextView lastModifyOnTxt = (TextView)rootView.findViewById(R.id.last_modified_on_txt);
		TextView lastModifyOn = (TextView)rootView.findViewById(R.id.last_modified_on);
		EditText newList = (EditText)rootView.findViewById(R.id.new_list);

		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Montserrat-Regular.ttf");

		newList.setTypeface(font);
		lastModifyBy.setTypeface(font);
		lastModifyOn.setTypeface(font);
		lastModifyOnTxt.setTypeface(font);
		lastModifyTxt.setTypeface(font);
		setListAdapter(adapter);
		return rootView;
	}


	private ArrayList<GroceryListModel> generateData(){
		ArrayList<GroceryListModel> models = new ArrayList<GroceryListModel>();
		models.add(new GroceryListModel("Supermarket"));
		models.add(new GroceryListModel("Market"));
		models.add(new GroceryListModel("Electricity store"));

		return models;
	}


}
