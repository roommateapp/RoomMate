package il.ac.huji.roommate;

import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

public class WhosHomeModel {

	private String name;
	private Bitmap img;
	private boolean atHome;

	public WhosHomeModel(String pName, boolean pAtHome, Bitmap pImage){
		Log.d("tt", "2");
		name = pName;
		img = pImage;
		atHome = pAtHome;
	}

	public String getName() {
		return name;
	}
	public void setName(String pName) {
		name = pName;
	}
	public Bitmap getImage() {
		return img;
	}
	public void setImage(Bitmap pImage) {
		img = pImage;
	}

	public boolean isAtHome()
	{
		return atHome;
	}

}
