package il.ac.huji.roommate;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LoadingFragment extends Fragment {
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.loading_fragment, container, false);
		
		Typeface fontBold = Typeface.createFromAsset(getActivity().getAssets(), "SinkinSans-600SemiBold.otf");
		TextView loadingTxt = (TextView)rootView.findViewById(R.id.loading_txt);
		
		loadingTxt.setTypeface(fontBold);
		
		
		getActivity().getActionBar().hide();
		
		return rootView;
	}
}
