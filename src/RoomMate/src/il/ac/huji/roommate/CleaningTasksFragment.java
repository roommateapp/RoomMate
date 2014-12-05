package il.ac.huji.roommate;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("NewApi")
public class CleaningTasksFragment extends ListFragment {

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.cleaning_tasks_fragment, container, false);
		CleaningAdapter adapter = new CleaningAdapter(getActivity().getBaseContext(), generateData());
		setListAdapter(adapter);

		EditText newTaskEdit = (EditText)rootView.findViewById(R.id.new_task);
		TextView existingTitleTxt = (TextView)rootView.findViewById(R.id.existing_tasks);
		TextView donebyTitleTxt = (TextView)rootView.findViewById(R.id.done_by);

		/*
		TextView lastModifyTxt = (TextView)rootView.findViewById(R.id.last_modified_txt);
		TextView lastModifyBy = (TextView)rootView.findViewById(R.id.last_modified_by);
		TextView lastModifyOnTxt = (TextView)rootView.findViewById(R.id.last_modified_on_txt);
		TextView lastModifyOn = (TextView)rootView.findViewById(R.id.last_modified_on);
*/
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "SinkinSans-400Regular.otf");
		Typeface fontBold = Typeface.createFromAsset(getActivity().getAssets(), "SinkinSans-600SemiBold.otf");

		newTaskEdit.setTypeface(font);
		existingTitleTxt.setTypeface(fontBold);
		donebyTitleTxt.setTypeface(fontBold);
/*
		lastModifyBy.setTypeface(font);
		lastModifyOn.setTypeface(font);
		lastModifyOnTxt.setTypeface(font);
		lastModifyTxt.setTypeface(font);
*/
		return rootView;
	}

	private ArrayList<CleaningModel> generateData(){
		ArrayList<CleaningModel> models = new ArrayList<CleaningModel>();
		models.add(new CleaningModel("Clean bathroom","Natasha"));
		models.add(new CleaningModel("Clean kitchen","Yael"));
		return models;
	}
}

