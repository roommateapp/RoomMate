package il.ac.huji.roommate;

public class GroceryListModel {
	private String grocery_name;


	public GroceryListModel(String name) {
		super();
		this.setName(name);
	}

	public String getName() {
		return grocery_name;
	}
	public void setName(String name) {
		this.grocery_name = name;
	}
}
