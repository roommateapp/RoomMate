package il.ac.huji.roommate;


public class GroceryListModel {
	private String grocery_list_name;
	private String grocery_list_id;

	public GroceryListModel(String name, String groceryParseId) {
		super();
		this.setName(name);
		this.setGroceryListParseId(groceryParseId);
	}

	public String getName() {
		return grocery_list_name;
	}
	public void setName(String name) {
		this.grocery_list_name = name;
	}

	public String getGroceryListParseId() {
		return grocery_list_id;
	}

	public void setGroceryListParseId(String grocery_parse_id) {
		this.grocery_list_id = grocery_parse_id;
	}
}
