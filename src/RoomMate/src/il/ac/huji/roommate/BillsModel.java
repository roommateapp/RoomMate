package il.ac.huji.roommate;

public class BillsModel {
	private String name;
	private double ammoumt;
	private String dueDate;
	private boolean notified;
	private String parseId;

	public BillsModel(String name, double ammount, String dueDate, boolean notified, String parseId) {
		super();
		this.setName(name);
		this.setAmmoumt(ammount);
		this.setDueDate(dueDate);
		this.setNotified(notified);
		this.setParseId(parseId);
	}

//	public BillsModel(String name, double d) {
//		super();
//		this.name = name;
//		this.ammoumt = d;
//		this.setDueDate("");
//		this.setNotified(false);
//	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getAmmoumt() {
		return ammoumt;
	}

	public void setAmmoumt(double ammoumt) {
		this.ammoumt = ammoumt;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public boolean isNotified() {
		return notified;
	}

	public void setNotified(boolean notified) {
		this.notified = notified;
	}

	public String getParseId() {
		return parseId;
	}

	public void setParseId(String parseId) {
		this.parseId = parseId;
	}
}
