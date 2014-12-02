package il.ac.huji.roommate;

import java.util.ArrayList;
import java.util.Collections;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class SingleGroceryListAdapter extends ArrayAdapter<GroceryModel>{

	private Context context;
	private ArrayList<GroceryModel> modelsArrayList;
	private LayoutInflater mLayoutInflater;
	private ViewHolder holder;

	public SingleGroceryListAdapter(Context context, ArrayList<GroceryModel> modelsArrayList) {

		super(context, R.layout.grocery_list_item, modelsArrayList);
		this.context = context;
		this.modelsArrayList = modelsArrayList;
	}

	@Override
	public int getCount() {
		if (modelsArrayList == null)
			return 0;
		else 
			return modelsArrayList.size();
	}

	@Override
	public GroceryModel getItem(int position) {
		return modelsArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void add(GroceryModel object) {
		modelsArrayList.add(0, object);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null){
			//The view is not a recycled one: we have to inflate
			mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mLayoutInflater.inflate(R.layout.grocery_list_item, parent, false);
			holder = new ViewHolder();

			holder.itemName = (EditText) convertView.findViewById(R.id.grocery_item);
			holder.itemchecked = (CheckBox) convertView.findViewById(R.id.check_item);
			holder.deleteItem = (Button)convertView.findViewById(R.id.delete_item);

			convertView.setTag(holder);
		} else {
			// View recycled !
			holder = (ViewHolder) convertView.getTag();
		}

		//RelativeLayout rel = findViewById(R.id.rel_single_grocey_list).requestFocus();

		GroceryModel item = getItem(position);
		holder.itemName.setText(item.getGrocery());
		holder.itemchecked.setChecked(item.getChecked());
		holder.itemName.setTag(item);

//		holder.itemName.clearFocus();
		RelativeLayout rel = (RelativeLayout)convertView.findViewById(R.id.grocery_list_item);
		rel.requestFocus();

		holder.itemchecked.setOnClickListener(new View.OnClickListener() {  
			public void onClick(final View v) {  
				modelsArrayList.get(position).setChecked((((CheckBox) v.findViewById(R.id.check_item))).isChecked());
				notifyDataSetChanged();
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Grocery");
				query.getInBackground(modelsArrayList.get(position).getGroceryParseId(), new GetCallback<ParseObject>() {
					@Override
					public void done(ParseObject grocery, ParseException e) {
						try {
							grocery= grocery.fetch();
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
						grocery.put("checked", ((CheckBox) v.findViewById(R.id.check_item)).isChecked());//cb.isChecked());
						grocery.saveInBackground();

						InputMethodManager imm =  (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);	
					}
				});
			}  
		}); 

		holder.itemName.setOnFocusChangeListener(new OnFocusChangeListener() {
			private String editedItem;

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					Log.i("LIST", "5. lost focus");
					editedItem = ((EditText) v.findViewById(R.id.grocery_item)).getText().toString();
					String prev = modelsArrayList.get(position).getGrocery();

					Log.i("LIST", "PREV: " + prev + " NEW: " + editedItem);
					if (editedItem.equals(prev) || (editedItem.equals(""))){
						return;
					} 
					if (!(editedItem.equals(prev)) && !(editedItem.equals(""))){
						// check if another grocery with that name exists
						for (GroceryModel g : modelsArrayList){
							// same as another grocery, this is removed
							if (g.getGrocery().equals(editedItem)){
								ParseQuery<ParseObject> query = ParseQuery.getQuery("Grocery");
								query.getInBackground(modelsArrayList.get(position).getGroceryParseId(), new GetCallback<ParseObject>() {
									@Override
									public void done(ParseObject grocery, ParseException e) {
										try {
											ParseObject groceryFetch = grocery.fetch();
											groceryFetch.deleteInBackground();
											modelsArrayList.remove(position);
											notifyDataSetChanged();
										} catch (ParseException e1) {
											e1.printStackTrace();
										}
									}
								});
								InputMethodManager imm =  (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(v.getWindowToken(), 0);	
								return;
							}
						}
						ParseQuery<ParseObject> query = ParseQuery.getQuery("Grocery");
						Log.i("LIST", "updating grocery: " + modelsArrayList.get(position).getGroceryParseId());
						query.getInBackground(modelsArrayList.get(position).getGroceryParseId(), new GetCallback<ParseObject>() {
							@Override
							public void done(ParseObject grocery, ParseException e) {
								try {
									grocery = grocery.fetch();
								} catch (ParseException ex){}
								grocery.put("name", editedItem);
								grocery.saveInBackground(new SaveCallback() {

									@Override
									public void done(ParseException e) {
										modelsArrayList.get(position).setGrocery(editedItem);
										notifyDataSetChanged();											
									}
								});
							}
						});
					}
				}
				// close soft keyboard
				InputMethodManager imm =  (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);	
			}
		});

		holder.deleteItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Grocery");
				query.getInBackground(modelsArrayList.get(position).getGroceryParseId(), new GetCallback<ParseObject>() {
					@Override
					public void done(ParseObject grocery, ParseException e) {
						try {
							grocery = grocery.fetch();
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
						grocery.deleteInBackground();
						modelsArrayList.remove(position);
						notifyDataSetChanged();
					}
				});
			}
		});

		Typeface font = Typeface.createFromAsset(getContext().getAssets(), "SinkinSans-400Regular.otf");
		holder.itemName.setTypeface(font);

		if (holder.itemchecked.isChecked()){
			convertView.setBackgroundColor(Color.parseColor("#DADADC"));

		} else {
			convertView.setBackgroundColor(Color.WHITE);
		}
		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		Collections.sort(modelsArrayList, new GroceryComparator());
		super.notifyDataSetChanged();
	}

	public void updateModelList(GroceryModel item) {
		if (item==null)
			return;
		modelsArrayList.add(item);
	}

	static class ViewHolder{
		EditText itemName;
		CheckBox itemchecked;
		Button deleteItem;
	}
}