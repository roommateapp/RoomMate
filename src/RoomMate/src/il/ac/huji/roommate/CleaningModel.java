package il.ac.huji.roommate;

public class CleaningModel {
	 
    private String task;
    private String person;
 
    public CleaningModel(String task, String person) {
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
