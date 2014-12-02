package il.ac.huji.roommate;

import java.util.List;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class GroceryListsAdapter extends ArrayAdapter<GroceryListModel>{

	private Context context;
	private List<GroceryListModel> modelsArrayList;

	public GroceryListsAdapter(Context context, List<GroceryListModel> items){
		super(context, R.layout.grocery_lists_item, items);
		this.context = context;
		this.modelsArrayList = items;
	}

	public void updateModelList(GroceryListModel item){
		if (item==null)
			return;
		modelsArrayList.add(item);
	}

	@Override
	public int getCount() {
		if (modelsArrayList==null)
			return 0;
		// TODO Auto-generated method stub
		return modelsArrayList.size();
	}

	@Override
	public GroceryListModel getItem(int arg0) {
		// TODO Auto-generated method stub
		return modelsArrayList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		// first check to see if the view is null. if so, we have to inflate it.
		// to inflate it basically means to render, or show, the view.
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.grocery_lists_item, parent, false);
		}

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 * 
		 * Therefore, i refers to the current Item object.
		 */
		GroceryListModel i = modelsArrayList.get(position);
		final String listItemText = i.getName();

		if (i != null) {
			TextView nameView = (TextView) convertView.findViewById(R.id.list_name);
			nameView.setText(i.getName());

			Typeface font = Typeface.createFromAsset(getContext().getAssets(), "SinkinSans-600SemiBold.otf");
			nameView.setTypeface(font);
			convertView.setClickable(true);
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view)
				{
					// get the clicked item name
					Intent intent = new Intent(view.getContext(), SingleGroceryListActivity.class);//view.getContext()
					intent.putExtra("listname", listItemText);
					Log.i("PARSE", "liddt id in adapter: " + modelsArrayList.get(position).getGroceryListParseId());
					intent.putExtra("listId", modelsArrayList.get(position).getGroceryListParseId());
					view.getContext().startActivity(intent);
				}

			});

			convertView.setOnLongClickListener(new View.OnLongClickListener(){
				@Override
				public boolean onLongClick(View v) {
					{
						//pop a dialog, if user wants to remove the list: remove the list and all it's items
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
						// set title
						alertDialogBuilder.setTitle("Delete intire list");
						// set dialog message
						alertDialogBuilder
						.setMessage("Are you sure you want to delete " + listItemText + " list and all the items in it?")
						.setCancelable(false)
						.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								ParseQuery<ParseObject> groceriesQuery = ParseQuery.getQuery("SingleGroceryList");
								groceriesQuery.getInBackground(modelsArrayList.get(position).getGroceryListParseId(), 
										new GetCallback<ParseObject>() {
									@Override
									public void done(ParseObject list, ParseException e) {
										try {
											list = list.fetch();
										} catch (ParseException e1) {
											e1.printStackTrace();
											list.deleteInBackground();
										}	
										List<ParseObject> groceriesParse = list.getList("groceries");
										for (ParseObject g : groceriesParse){
											try {
												g = g.fetch();
											} catch (ParseException e1) {
												e1.printStackTrace();
											}
											g.deleteInBackground();
										}
										list.deleteInBackground(new DeleteCallback() {
											@Override
											public void done(ParseException e) {
												Log.i("MM", "DONE REMOVING!");
												modelsArrayList.remove(position);
												notifyDataSetChanged();
											}
										});


									}
								});
							}
						})
						.setNegativeButton("No",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});

						// create alert dialog
						AlertDialog alertDialog = alertDialogBuilder.create();

						// show it
						alertDialog.show();
					}

					return true;
				}
			});
		}
		return convertView;
	}
}