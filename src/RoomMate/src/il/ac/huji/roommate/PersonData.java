package il.ac.huji.roommate;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.ParseException;
import com.parse.ParseObject;

public class PersonData {
	
	private ParseObject parsePersonObject;
	private Bitmap photoBitmap;
	
	private static int PHOTO_BITMAP_SIZE_X = 70;
	private static int PHOTO_BITMAP_SIZE_Y = 70;
	
	
	public PersonData (ParseObject  perObject){
		
		try {
			parsePersonObject = perObject.fetch();
		} catch (ParseException e) {
			e.printStackTrace();
			parsePersonObject = perObject;
		}
		
		Bitmap tempBitmap = null;
	    HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) new URL(
					parsePersonObject.getString(
							MainActivity.PERSON_FIELD_PHOTO_URL))
					.openConnection();
			connection.connect();
			InputStream input = connection.getInputStream();
			tempBitmap = BitmapFactory.decodeStream(input);
			
			// convert decoded bitmap into well scalled Bitmap format.
			photoBitmap = Bitmap.createScaledBitmap(tempBitmap, 
					PHOTO_BITMAP_SIZE_X, PHOTO_BITMAP_SIZE_Y , true);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ParseObject getPersonParseObject(){
		try {
			parsePersonObject = parsePersonObject.fetch();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return parsePersonObject;
	}
	
	public Bitmap getPicture(){
		return photoBitmap;
	}
	
	public boolean atHome(){
		return getPersonParseObject().getBoolean(MainActivity.PERSON_FIELD_AT_HOME);
	}

}
