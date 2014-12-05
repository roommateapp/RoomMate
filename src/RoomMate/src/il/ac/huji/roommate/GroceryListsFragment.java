package il.ac.huji.roommate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class GroceryListsFragment extends ListFragment {

	EditText newList;
	GroceryListsAdapter adapter;
	ArrayList<GroceryListModel> models = new ArrayList<GroceryListModel>();
	private MainActivity mainActivity;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mainActivity = (MainActivity) getActivity();

		ParseObject house = mainActivity.getHomeObject();
		List<ParseObject> allLists = house.getList("groceryLists");

		Iterator<ParseObject> iter = allLists.iterator();
		
		while(iter.hasNext()){
			ParseObject list = iter.next();
			try {
				list = list.fetch();
				models.add(new GroceryListModel(list.getString("name"), list.getObjectId()));
			} catch (ParseException e1) {
				Log.i("MM", "removing!");
				iter.remove();
				e1.printStackTrace();
			}
		}
		mainActivity.parseHomeObject.put("groceryLists", allLists);
		mainActivity.parseHomeObject.saveInBackground();
		
		adapter = new GroceryListsAdapter(getActivity(), models);
		setListAdapter(adapter);
	}

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.grocery_lists_fragment, container, false);
		rootView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		newList = (EditText)rootView.findViewById(R.id.new_list);

		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "SinkinSans-400Regular.otf");
		newList.setTypeface(font);

		Button addListBtn = (Button)rootView.findViewById(R.id.add_list);
		addListBtn.setOnClickListener (new View.OnClickListener() {
			public void onClick(View v) {

				final String newListName = newList.getText().toString();
				newList.setText("");
				if (!newListName.equals("")){
					for (GroceryListModel g : models){
						if (g.getName().equals(newListName)){
							
							return;
						}
					}
					final ParseObject newListObj = new ParseObject("SingleGroceryList");
					newListObj.put("name", newListName);
					newListObj.put("groceries", new ArrayList<ParseObject>());
					newListObj.saveInBackground(new SaveCallback() {

						@Override
						public void done(ParseException e) {
							ParseObject house = mainActivity.getHomeObject();
							house.add("groceryLists", newListObj);
							house.saveInBackground(new SaveCallback() {

								@Override
								public void done(ParseException e) {
									Log.i("PARSE", "saving id: " +  newListObj.getObjectId());
									adapter.updateModelList(new GroceryListModel(newListName, newListObj.getObjectId()));
									newList.setText("");
									adapter.notifyDataSetChanged();

									Intent intent = new Intent(getActivity(), SingleGroceryListActivity.class);//view.getContext()
									intent.putExtra("listname", newListName);
									intent.putExtra("listId", newListObj.getObjectId());
									getActivity().startActivity(intent);
								}
							});
						}
					});

					InputMethodManager inputManager = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					//check if no view has focus:
					View view = getActivity().getCurrentFocus();
					if (view == null)
						return;
					inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		});

		RelativeLayout rel = (RelativeLayout)rootView.findViewById(R.id.rel_grocery_lists);
		rel.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent ev)
			{
				InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

				return false;
			}
		});
		return rootView;
	}
}
