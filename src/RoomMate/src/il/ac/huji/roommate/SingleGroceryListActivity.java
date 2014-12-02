package il.ac.huji.roommate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class SingleGroceryListActivity extends ListActivity{

	EditText newGrocery;
	ArrayList<GroceryModel> models = new ArrayList<GroceryModel>();
	ArrayList<GroceryModel> temp;
	SingleGroceryListAdapter adapter;
	String newGroceryName;
	Bundle extras;
	private String listName;
	private String listId;
	String objId = "";

	public void setListName(String name){
		this.listName = name;
	}

	public String getListName(){
		return listName;
	}

	@SuppressLint("InflateParams")
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_single_grocery_list);

		extras = getIntent().getExtras(); 
		setListName(extras.getString("listname"));

		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
		TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);

		Typeface fontBold = Typeface.createFromAsset(getAssets(), "SinkinSans-600SemiBold.otf");
		mTitleTextView.setTypeface(fontBold);

		mTitleTextView.setText("grocery list: " + getListName());

		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);

		getActionBar().setCustomView(mCustomView);
		getActionBar().setDisplayShowCustomEnabled(true);

		listId = extras.getString("listId");

		newGrocery = (EditText)findViewById(R.id.new_grocery);
		Typeface font = Typeface.createFromAsset(getAssets(), "SinkinSans-400Regular.otf");
		newGrocery.setTypeface(font);

		// find all the groceries in the list with the listId
		ParseQuery<ParseObject> groceriesQuery = ParseQuery.getQuery("SingleGroceryList");
		groceriesQuery.getInBackground(listId, new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject list, ParseException e) {
				try {
					list  = list.fetch();
				}
				catch (ParseException e1) {
					e1.printStackTrace();
				}
				List<ParseObject> groceriesParse = list.getList("groceries");
				adapter = new SingleGroceryListAdapter(getApplicationContext(), models);
				setListAdapter(adapter);

				Iterator<ParseObject> iter = groceriesParse.iterator();

				while (iter.hasNext()) {
					ParseObject groceryFetch = iter.next();

					try{
						groceryFetch = groceryFetch.fetch();
						if (groceryFetch != null)
						{
							Log.i("LIST", "3. adding to 'models' grocery: " + groceryFetch.getString("name") + " checked: " + groceryFetch.getBoolean("checked") + 
									" with id: " + groceryFetch.getObjectId());
							if (groceryFetch.getBoolean("checked")){
								Log.i("LIST", "3.1 checked!");
								models.add(models.size(), new GroceryModel(groceryFetch.getString("name"), groceryFetch.getBoolean("checked"), 
										groceryFetch.getObjectId()));
								adapter.notifyDataSetChanged();
							} else {
								Log.i("LIST", "3.1 not checked!");
								models.add(0, new GroceryModel(groceryFetch.getString("name"), groceryFetch.getBoolean("checked"), groceryFetch.getObjectId()));
								adapter.notifyDataSetChanged();
							}
						}

					} catch (ParseException e1) {
						Log.i("LIST", "-------- exeption: REMOVING");
						iter.remove();
					}
				}
				list.put("groceries", groceriesParse);
				list.saveInBackground();
			}
		});

		Button addGroceryBtn = (Button)findViewById(R.id.add_grocery);
		addGroceryBtn.setOnClickListener (new View.OnClickListener() {
			private ParseObject newGroceryObj;

			public void onClick(final View v) {
				newGroceryName = newGrocery.getText().toString();
				Log.i("LIST", "4. adding grocery: " + newGroceryName);
				newGrocery.setText("");
				if (!newGroceryName.equals("")){
					// grocery already exists
					for (GroceryModel g : models){
						if (g.getGrocery().equals(newGroceryName)){
							
							InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(v.getWindowToken(), 0);	
							return;
						}
					}
					// adding new grocery
					newGroceryObj = new ParseObject("Grocery");
					newGroceryObj.put("name", newGroceryName);
					newGroceryObj.put("checked", false);
					//					newGroceryObj.put("list", listName);
					//					newGroceryObj.put("houseId", extras.get("houseId"));
					newGroceryObj.saveInBackground(new SaveCallback() {			
						@Override
						public void done(ParseException e) {
							ParseQuery<ParseObject> groceriesQuery = ParseQuery.getQuery("SingleGroceryList");
							groceriesQuery.getInBackground(listId, new GetCallback<ParseObject>() {
								@Override
								public void done(ParseObject list, ParseException e) {
									try {
										list = list.fetch();
									} catch (ParseException e1) {
										e1.printStackTrace();
									}
									list.add("groceries", newGroceryObj);
									list.saveInBackground(new SaveCallback() {
										@Override
										public void done(ParseException e) {
											adapter.updateModelList(new GroceryModel(newGroceryObj.getString("name"), false, newGroceryObj.getObjectId()));
											adapter.notifyDataSetChanged();
											newGrocery.setText("");											
										}
									});

								}
							});
						}
					});
				}
				InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

			}
		});
		Button backToListsButton = (Button)findViewById(R.id.back_to_lists_button);
		backToListsButton.setTypeface(font);

		backToListsButton.setOnClickListener (new View.OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});

		final RelativeLayout rel = (RelativeLayout) findViewById(R.id.rel_single_grocey_list);
		rel.requestFocus();
		rel.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent ev)
			{
				InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
				rel.requestFocus();
				return false;
			}
		});
	}
}