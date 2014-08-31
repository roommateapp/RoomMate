package il.ac.huji.roommate;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.EditText;

@SuppressLint("NewApi")
public class SingleGroceryListActivity extends ListActivity {

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		// if extending Activity
		//setContentView(R.layout.activity_main);

		// 1. pass context and data to the custom adapter
		SingleGroceryListAdapter adapter = new SingleGroceryListAdapter(this, generateData());

		EditText newItem = (EditText)findViewById(R.id.new_grocery);
		Typeface font = Typeface.createFromAsset(getAssets(), "Montserrat-Regular.ttf");
		newItem.setTypeface(font);


		// 3. setListAdapter
		setListAdapter(adapter);
	}

	private ArrayList<GroceryModel> generateData(){
		ArrayList<GroceryModel> models = new ArrayList<GroceryModel>();
		models.add(new GroceryModel("Apples", ""));
		models.add(new GroceryModel("Chicken", ""));
		models.add(new GroceryModel("Milk", ""));

		return models;
	}
}

/**

public class MainActivity extends ListActivity {

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // if extending Activity
        //setContentView(R.layout.activity_main);

        // 1. pass context and data to the custom adapter
        MyAdapter adapter = new MyAdapter(this, generateData());

        // if extending Activity 2. Get ListView from activity_main.xml
        //ListView listView = (ListView) findViewById(R.id.listview);

        // 3. setListAdapter
        //listView.setAdapter(adapter); if extending Activity
        setListAdapter(adapter);
    }

 **/