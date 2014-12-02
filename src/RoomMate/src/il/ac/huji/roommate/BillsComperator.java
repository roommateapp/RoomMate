package il.ac.huji.roommate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import com.google.android.gms.internal.el;

import android.util.Log;

public class BillsComperator implements Comparator<BillsModel> {

	@Override
	public int compare(BillsModel a, BillsModel b) {
		

		String aDueDate = a.getDueDate();
		String bDueDate = b.getDueDate();
		boolean aNotified = a.isNotified();
		boolean bNotified = b.isNotified();

		//Return +1 if a>b, -1 if a<b, 0 if equal

		if (!aNotified && bNotified) // if one is notified, then it's smaller
			return -1;
		else if (!aDueDate.equals("") && bDueDate.equals("") && !aNotified && !bNotified) // if one isn't paid yet, then it's smalle
			return -1;
		else if (aNotified && !bNotified)
			return 1;
		else if (aDueDate.equals("") && !bDueDate.equals("") && !aNotified && !bNotified)
			return 1;
		else if ((!aNotified && !bNotified && aDueDate.equals("") && bDueDate.equals("")) ||
				(aNotified && bNotified && aDueDate.equals("") && bDueDate.equals("")) || 
				(!aNotified && !bNotified && !aDueDate.equals("") && !bDueDate.equals("")) ||
				(aNotified && bNotified && !aDueDate.equals("") && !bDueDate.equals(""))){
			
			Log.i("BILL", "COMPARING");
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

			Date dateA, dateB;
			try {
				dateA = formatter.parse(aDueDate);
				dateB = formatter.parse(bDueDate);
				if (dateA.compareTo(dateB)<0)
					return -1;
				else 
					return 1;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
}
