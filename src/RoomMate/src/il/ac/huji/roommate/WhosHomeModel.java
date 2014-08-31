package il.ac.huji.roommate;

import android.media.Image;

public class WhosHomeModel {

	private String name;
	//private Image img;

	//public WhosHomeModel(String name, Image image){
	public WhosHomeModel(String name){

		this.name = name;
		//this.img = image;
	}



	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	//	public Image getImage() {
	//		return img;
	//	}
	//	public void setImage(Image img) {
	//		this.img = img;
	//	}


}
