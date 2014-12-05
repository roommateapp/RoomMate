package il.ac.huji.roommate;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

@SuppressLint("NewApi")
public class HomeBoardFragment extends ListFragment {


	HomeBoardAdapter adapter;
	ArrayList<HomeBoardModel> models = new ArrayList<HomeBoardModel>();
	private String houseId;
	private String writer;
	private String newNoteName;
	private EditText newNoteEdit;
	private MainActivity mainActivity;
	private List<String> notes;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mainActivity = (MainActivity)activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		houseId = mainActivity.homeIdParse;
		writer = mainActivity.userName;

		adapter = new HomeBoardAdapter(getActivity(), models, houseId);
		setListAdapter(adapter);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		models.clear();
		ParseObject house = mainActivity.getHomeObject();
		if (house != null){
			notes = house.getList("notes");
			for (final String noteId : notes){
				Log.i("RR", "note id: " + noteId);
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Note");
				query.getInBackground(noteId, new GetCallback<ParseObject>() {
					@Override
					public void done(ParseObject note, ParseException e) {
						if (e == null){
							Log.i("RR", "adding to models");
							Bitmap image = null;
							String writerId = note.getString("writerId");
							if (writerId == null){
								image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_round);
							} else {
								image = mainActivity.getHomePeopleMap()
										.get(writerId).getPicture();
							}
							
							models.add(0, 
									new HomeBoardModel(note.getString("content"), 
											note.getString("writer"), 
											note.getBoolean("isNotification"), 
											noteId,
											image
									)
							);
							adapter.notifyDataSetChanged();
						} else{
							notes.remove(noteId);
						}
					}
				});
			}
		}
	}

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.home_board_fragment, container, false);

		newNoteEdit = (EditText)rootView.findViewById(R.id.new_note);
		
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "SinkinSans-400Regular.otf");
		newNoteEdit.setTypeface(font);
		
		Button addNoteBtn = (Button)rootView.findViewById(R.id.add_note);
		addNoteBtn.setOnClickListener (new View.OnClickListener() {
			private ParseObject newNoteObj;

			public void onClick(View v) {
				Log.i("HOME", "on click");
				newNoteName = newNoteEdit.getText().toString();
				if (!newNoteName.equals("")){
					Log.i("HOME", "add");
					newNoteObj = new ParseObject("Note");
					newNoteObj.put("content", newNoteName);
					newNoteObj.put("writer", writer);
					
					newNoteObj.put("writerId", mainActivity.parsePersonObject.getObjectId());
					newNoteObj.put("isNotification", false);
					newNoteObj.saveInBackground(new SaveCallback() {
						@Override
						public void done(ParseException e) {
							adapter.add(
									new HomeBoardModel(
											newNoteName, 
											writer, 
											false, 
											newNoteObj.getObjectId(),
											mainActivity.getHomePeopleMap()
												.get(mainActivity.parsePersonObject
													.getObjectId()).getPicture()
									)
							);
							adapter.notifyDataSetChanged();
							mainActivity.parseHomeObject.add("notes", newNoteObj.getObjectId());
							mainActivity.parseHomeObject.saveInBackground();
						}
					});

					newNoteEdit.setText("");
					InputMethodManager inputManager = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					View view = getActivity().getCurrentFocus();
					if (view == null)
						return;
					inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		});

		RelativeLayout rel = (RelativeLayout)rootView.findViewById(R.id.rel_home_board);
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

	@Override
	public void onStart() 
	{
		super.onStart();

		setBackground();
	}


	public void setBackground()
	{
		//		ParseFile fileObject = (ParseFile)mainActivity.parseHomeObject.get("homePicture");
		//		if (fileObject != null )
		//		{
		//			fileObject.getDataInBackground(new GetDataCallback() {
		//				public void done(byte[] imgbytes, ParseException e) {
		//					if (e == null) {
		//						ImageView i = (ImageView)getActivity().findViewById(R.id.home_board_image);
		//						if (i != null)
		//						{
		//							BitmapFactory.Options options=new BitmapFactory.Options();
		//							// Create object of bitmapfactory's option method for further option use
		//
		//							options.inPurgeable = true; 
		//							// inPurgeable is used to free up memory while required
		//
		//							Bitmap bitmap1 = BitmapFactory.decodeByteArray(imgbytes,0, imgbytes.length,options);
		//							//Decode image, "thumbnail" is the object of image file
		//
		//							Bitmap bitmap = Bitmap.createScaledBitmap(bitmap1, 150 , 50 , true);
		//							// convert decoded bitmap into well scalled Bitmap format.
		//
		//
		//							//Bitmap  = BitmapFactory.decodeByteArray(imgbytes, 0, imgbytes.length);
		//
		//							i.setImageBitmap(bitmap);
		//
		//							bitmap1.recycle();
		//
		//
		//						}
		//					}
		//				}
		//			});	
		//		}
	}
}
