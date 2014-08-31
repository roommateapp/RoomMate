package il.ac.huji.roommate;

public class GroceryModel {

	private String task;
	private String person;

	public GroceryModel(String task, String person) {
		super();
		this.setTask(task);
		this.setPerson(person);
	}

	public String getTask() {
		return task;
	}
	public void setTask(String task) {
		this.task = task;
	}
	public String getPerson() {
		return person;
	}
	public void setPerson(String counter) {
		this.person = counter;
	}

}