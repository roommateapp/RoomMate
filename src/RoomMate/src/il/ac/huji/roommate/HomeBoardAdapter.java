package il.ac.huji.roommate;

import java.util.ArrayList;
import java.util.List;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeBoardAdapter extends ArrayAdapter<HomeBoardModel>{

	private Context context;
	private ArrayList<HomeBoardModel> modelsArrayList;
	private String houseId;
	private ViewHolder holder;
	private LayoutInflater inflater;

	public HomeBoardAdapter(Context context, ArrayList<HomeBoardModel> modelsArrayList, String houseId) {
		super(context, R.layout.homeboard_note_item, modelsArrayList);
		this.context = context;
		this.modelsArrayList = modelsArrayList;
		this.houseId = houseId;
	}

	@Override
	public int getCount() {
		if (modelsArrayList==null)
			return 0;
		// TODO Auto-generated method stub
		return modelsArrayList.size();
	}

	@Override
	public HomeBoardModel getItem(int arg0) {
		// TODO Auto-generated method stub
		return modelsArrayList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public void add(HomeBoardModel object) {
		// TODO Auto-generated method stub
		//super.add(object);
		modelsArrayList.add(0, object);

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		holder = null;

		if (convertView == null) {
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.homeboard_note_item, parent, false);

			holder = new ViewHolder();

			holder.noteContentEdit = (TextView) convertView.findViewById(R.id.note_content);
			holder.deleteNoteBtn = (Button)convertView.findViewById(R.id.delete_item);
			holder.writerName = (TextView)convertView.findViewById(R.id.writer_name);
			holder.img = (ImageView)convertView.findViewById(R.id.writer_img);
			
			convertView.setTag(holder);
		} else {
			// View recycled !
			holder = (ViewHolder) convertView.getTag();
		}

		Typeface font = Typeface.createFromAsset(context.getAssets(), "SinkinSans-400Regular.otf");

		holder.noteContentEdit.setTypeface(font);
		if (holder.noteContentEdit != null) {
			if (modelsArrayList.get(position).isNotification()){
				holder.noteContentEdit.setTextColor(Color.parseColor("#006837"));
			} else {
				holder.noteContentEdit.setTextColor(Color.BLACK);
			}
		}
		holder.writerName.setTypeface(font);
		holder.noteContentEdit.setTypeface(font);
		
		

		holder.deleteNoteBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (modelsArrayList.get(position).isNotification()){
					Toast.makeText(context, "Balance notification cannot be removed!", 
							Toast.LENGTH_LONG).show(); 
					return;
				}
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Note");
				query.getInBackground(modelsArrayList.get(position).getNoteId(), new GetCallback<ParseObject>() {
					@Override
					public void done(ParseObject object, ParseException e) {
						if (object!=null)
							object.deleteInBackground();

						ParseQuery<ParseObject> query = ParseQuery.getQuery("House");
						query.getInBackground(houseId, new GetCallback<ParseObject>() {

							@Override
							public void done(ParseObject house,
									ParseException e) {
								List<String> notes = house.getList("notes");
								notes.remove(modelsArrayList.get(position).getNoteId());
								house.put("notes", notes);
								house.saveInBackground();
								modelsArrayList.remove(position);
								notifyDataSetChanged();
							}
						});
					}
				});
			}
		});

		HomeBoardModel note = getItem(position);
		holder.noteContentEdit.setText(note.getNoteContent());
		holder.noteContentEdit.setTag(note);
		holder.writerName.setText(note.getWriter());
		
		holder.img.setImageBitmap(modelsArrayList.get(position).getImg());

		// 5. retrn rowView
		return convertView;
	}

	static class ViewHolder{
		TextView noteContentEdit;
		TextView writerName;
		Button deleteNoteBtn;
		ImageView img;
		;
	}
}
