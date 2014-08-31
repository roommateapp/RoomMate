package il.ac.huji.roommate;

import java.sql.Date;

import android.util.Log;

public class BillsModel {
	private String name;
	private double ammoumt;
	//  private Date due_date;

	public BillsModel(String name, float ammount, Date due_date) {
		super();
		this.name = name;
		this.ammoumt = ammount;
		//     this.due_date = due_date;
	}

	public BillsModel(String name, double d) {
		super();
		this.name = name;
		this.ammoumt = d;
		//    this.due_date = null;
		Log.i("IN MINI","SSSS");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getAmmoumt() {
		return ammoumt;
	}

	public void setAmmoumt(float ammoumt) {
		this.ammoumt = ammoumt;
	}

	//	public Date getDueDate() {
	//		return due_date;
	//	}
	//
	//	public void setDueDate(Date due_date) {
	//		this.due_date = due_date;
	//	}
	//
	//	
}
