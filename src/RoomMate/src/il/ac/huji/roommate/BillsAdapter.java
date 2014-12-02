package il.ac.huji.roommate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.android.gms.internal.ho;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BillsAdapter extends ArrayAdapter<BillsModel>{

	private Context context;
	private ArrayList<BillsModel> modelsArrayList = new ArrayList<BillsModel>();
	private String houseId;
	private TextView nameView;
	private TextView ammountView;
	private TextView dateView;
	private View allView;
	private String userName;


	public BillsAdapter(Context context, ArrayList<BillsModel> modelsArrayList, String houseId, String userName) {

		super(context, R.layout.bills_item, modelsArrayList);
		this.context = context;
		this.modelsArrayList = modelsArrayList;
		this.houseId = houseId;
		this.userName = userName;
	}

	public void updateModelList(BillsModel item){
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
	public BillsModel getItem(int arg0) {
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

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.bills_item, parent, false);
		}

		allView = convertView;

		BillsModel i = modelsArrayList.get(position);

		if (i != null) {
			nameView = (TextView) convertView.findViewById(R.id.bill_name);
			ammountView = (TextView) convertView.findViewById(R.id.bill_ammount);
			dateView = (TextView) convertView.findViewById(R.id.bill_due_date);

			nameView.setText(modelsArrayList.get(position).getName());
			ammountView.setText(String.valueOf(modelsArrayList.get(position).getAmmoumt()));
			dateView.setText(modelsArrayList.get(position).getDueDate());

			Typeface font = Typeface.createFromAsset(getContext().getAssets(), "SinkinSans-400Regular.otf");
			nameView.setTypeface(font);
			ammountView.setTypeface(font);
			dateView.setTypeface(font);

			if (!i.isNotified()){
				if (dateView.getText().toString()==null || dateView.getText().toString().equals("")){
					convertView.setBackgroundColor(Color.WHITE);//
					nameView.setTextColor(Color.BLACK);
					ammountView.setTextColor(Color.BLACK);
					dateView.setTextColor(Color.BLACK);

				}
				else{
					convertView.setBackgroundColor(Color.WHITE);
					nameView.setTextColor(Color.parseColor("#006837"));
					ammountView.setTextColor(Color.parseColor("#006837"));
					dateView.setTextColor(Color.parseColor("#006837"));

				}
			} else{
				convertView.setBackgroundColor(Color.WHITE);
				nameView.setTextColor(Color.BLACK);
				ammountView.setTextColor(Color.BLACK);
				dateView.setTextColor(Color.BLACK);
			}

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view)
				{
					Intent intent = new Intent(view.getContext(), SingleBillActivty.class);//view.getContext()
					//					oldPos = position;
					intent.putExtra("billname", modelsArrayList.get(position).getName());
					intent.putExtra("houseId", houseId);
					intent.putExtra("existingBill", true);
					intent.putExtra("notified", modelsArrayList.get(position).isNotified());
					intent.putExtra("userName", userName);
					intent.putExtra("billParseId", modelsArrayList.get(position).getParseId());
					//					intent.putExtra("oldPos", oldPos);
					allView.getContext().startActivity(intent);
				}
			});

			if (!i.isNotified()){
				convertView.setOnLongClickListener(new View.OnLongClickListener(){
					@Override
					public boolean onLongClick(View v) {
						{
							//pop a dialog, if user wants to remove the list: remove the list and all it's items
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
							// set title
							alertDialogBuilder.setTitle("Delete intire bill");
							// set dialog message
							alertDialogBuilder
							.setMessage("Are you sure you want to delete " + modelsArrayList.get(position).getName() + 
									" bill and all it's details?")
									.setCancelable(false)
									.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
										private double amountToDelete;

										public void onClick(DialogInterface dialog,int id) {
											// remove all SingleGroceryList's items
											amountToDelete = modelsArrayList.get(position).getAmmoumt();
											ParseQuery<ParseObject> query = ParseQuery.getQuery("SingleBill");
											query.getInBackground(modelsArrayList.get(position).getParseId(), new GetCallback<ParseObject>() {

												@Override
												public void done(ParseObject bill, ParseException e) {
													// TODO Auto-generated method stub
													if (e == null) {
														try {
															bill = bill.fetch();
														} catch (ParseException e1) {
															e1.printStackTrace();
														}
														
														final String paidBy = bill.getString("paidBy");
														
														modelsArrayList.remove(position);
														notifyDataSetChanged();
														
														
														// if already paid remove from house-spendings and from person's-credit
														ParseQuery<ParseObject> queryHouse = ParseQuery.getQuery("House");
														queryHouse.getInBackground(houseId, new GetCallback<ParseObject>() {
															@Override
															public void done(ParseObject house, ParseException e) {
																try {
																	house = house.fetch();
																} catch (ParseException e1) {
																	e1.printStackTrace();
																}
																Double spendings = house.getDouble("spendings");
																house.put("spendings", spendings-amountToDelete);
																house.saveInBackground();
																List<ParseObject> persons = house.getList("persons");
																for (ParseObject person : persons){
																	try {
																		person = person.fetch();
																	} catch (ParseException e1) {
																		e1.printStackTrace();
																	}
																	if (person.get("userName").equals(paidBy)){
																		person.put("credit", person.getDouble("credit")-amountToDelete);
																		person.saveInBackground();
																	}
																}
															}
														});
														//														ParseQuery<ParseObject> queryPerson = ParseQuery.getQuery("Person");
														//														queryPerson.whereEqualTo("house", houseId);
														//														queryPerson.whereEqualTo("userName", paidBy);
														//														queryPerson.getFirstInBackground(new GetCallback<ParseObject>() {
														//															@Override
														//															public void done(ParseObject person, ParseException e) {
														//																Double credit = person.getDouble("credit");
														//																person.put("credit", credit-amountToDelete);
														//																person.saveInBackground();
														//															}
														//														});
														bill.deleteInBackground();
													}
												}
											});
											//											}
										}
									})
									.setNegativeButton("No",new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,int id) {
											dialog.cancel();
										}
									});
							AlertDialog alertDialog = alertDialogBuilder.create();
							alertDialog.show();
						}

						return true;
					}
				});
			}
		}
		// 5. retrn rowView
		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		Log.i("CHANGED", "notify");
		Collections.sort(modelsArrayList, new BillsComperator());
		super.notifyDataSetChanged();
	}
}