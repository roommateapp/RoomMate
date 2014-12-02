package il.ac.huji.roommate;

import java.util.Comparator;

import android.util.Log;

public class GroceryComparator implements Comparator<GroceryModel>
{

	@Override
	public int compare(GroceryModel a, GroceryModel b) {
		Log.i("COMP", "1");
		// TODO Auto-generated method stub
		//Return +1 if a>b, -1 if a<b, 0 if equal
		if (a.getChecked() && !b.getChecked()){
			return 1;
		} else if (a.getChecked() && b.getChecked()){
			return 0;
		} else {
			return -1;
		}
	}
}
