package il.ac.huji.roommate;

import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

/**
 * Class for representing if a roommate is home right now.
 **/
public class WhosHomeModel {

	private String name;
	private Bitmap img;
	private boolean atHome;

	public WhosHomeModel(String pName, boolean pAtHome, Bitmap pImage){
		setName(name);
		setImage(img);
		setAtHome(pAtHome);
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
	
	public void setAtHome(boolean isAtHome){
		atHome = isAtHome;
	}
	
	public boolean isAtHome()
	{
		return atHome;
	}

}
