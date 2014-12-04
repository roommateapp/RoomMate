package il.ac.huji.roommate;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BillImageActivity extends Activity{

	final static int IMAGE_RQST_CODE = 1;
	private Bitmap bitmap;
	private ParseFile file;
	private String billId;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{

		Log.i("IMAGE" , "here 1");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bill_img);

		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
		TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);

		Typeface fontBold = Typeface.createFromAsset(getAssets(), "SinkinSans-600SemiBold.otf");
		mTitleTextView.setTypeface(fontBold);

		mTitleTextView.setText("Bill's image");

		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);

		getActionBar().setCustomView(mCustomView);
		getActionBar().setDisplayShowCustomEnabled(true);

		Intent intent = getIntent();
		billId = intent.getExtras().getString("billId");

		// get bill from Parse.com
		Toast.makeText(getApplicationContext(), "Loading image", 
				Toast.LENGTH_SHORT).show(); 
		ParseQuery<ParseObject> queryBill = ParseQuery.getQuery("SingleBill");
		queryBill.getInBackground(billId, new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject bill, ParseException e) {
				try {
					bill = bill.fetch();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				ParseFile imgFile = bill.getParseFile("billPicture");
				imgFile.getDataInBackground(new GetDataCallback() {

					@Override
					public void done(byte[] data, ParseException e) {
						if (e == null) {
							// Decode the Byte[] into
							// Bitmap
							Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
							// Get the ImageView from main.xml
							//ImageView image = (ImageView) findViewById(R.id.ad1);
							ImageView ad1=(ImageView) findViewById(R.id.bill_img);
							// Set the Bitmap into the
							// ImageView
							ad1.setImageBitmap(bmp);
						}
					}
				});
			}
		});

		Typeface font = Typeface.createFromAsset(getAssets(), "SinkinSans-400Regular.otf");
		Button differentBtn = (Button)findViewById(R.id.pick_different_img);
		differentBtn.setTypeface(font);

		differentBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent,
						"Select Picture"), IMAGE_RQST_CODE);
			}
		});


	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) 
		{
		case IMAGE_RQST_CODE:
			if (data != null )
			{
				if (resultCode == Activity.RESULT_OK)
					try {
						// We need to recycle unused bitmaps
						if (bitmap != null) {
							bitmap.recycle();
						}
						InputStream inputStream = getApplication().getContentResolver().openInputStream(
								data.getData());
						bitmap = BitmapFactory.decodeStream(inputStream);
						inputStream.close();

						ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream);
						// get byte array here
						byte[] bytearray = outputStream.toByteArray();

						if (bytearray != null){
							file = new ParseFile("billPicture"+".jpg", bytearray);
							Log.i("IMAGE", bytearray.toString());

							file.saveInBackground(new SaveCallback() {
								@Override
								public void done(ParseException e) {
									Log.i("IMAGE", " FILE NOT NULL");

									ParseQuery<ParseObject> queryBill = ParseQuery.getQuery("SingleBill");
									queryBill.getInBackground(billId, new GetCallback<ParseObject>() {
										@Override
										public void done(ParseObject bill, ParseException e) {
											try {
												bill = bill.fetch();
											} catch (ParseException e1) {
												e1.printStackTrace();
											}
											bill.put("billPicture", file);
											bill.saveInBackground(new SaveCallback() {
												@Override
												public void done(ParseException e) {
													onBackPressed();
												}
											});
										}
									});
								}
							});
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			break;
		}
	}
}
