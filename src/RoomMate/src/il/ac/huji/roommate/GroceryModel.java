package il.ac.huji.roommate;

public class GroceryModel {

	private String grocery;
	private boolean checked;
	private String groceryParseId;

	public GroceryModel(String grocery, boolean checked, String groceryParseId) {
		super();
		this.setGrocery(grocery);
		this.setChecked(checked);
		this.setGroceryParseId(groceryParseId);
	}

	public String getGrocery() {
		return grocery;
	}
	public void setGrocery(String grocery) {
		this.grocery = grocery;
	}
	public boolean getChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getGroceryParseId() {
		return groceryParseId;
	}

	public void setGroceryParseId(String groceryParseId) {
		this.groceryParseId = groceryParseId;
	}

}