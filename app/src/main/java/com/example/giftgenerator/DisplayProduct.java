package com.example.giftgenerator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class DisplayProduct extends Activity {
	//Declare global variables
	String id, name, description, price, link, imageUrl;
	double averageRating, totalRating; 
	int ratingCount;
	Bitmap giftImg;
	TextView nameView, descriptionView, priceView, linkView;
	ImageView giftView;
	URL newurl = null;
	RatingBar ratingView, rating;
	Button rateButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_product);
		
		getValues();								// get the values from the bundle and store in variables
		initialiseViews();							// initialise views
		checkPreferences();							// check to see if the product has been rated by the user already
		populateViews();							// set the name, description, price and rating in the views
		new XMLParse().execute();					// execute asynchronous task, onPreExecute, doInBackground, onPostExecute
		
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)	// set up intents for what option is selected
	{

		if(item.getItemId()==R.id.action_settings)
		{
			Intent i = new Intent(DisplayProduct.this, Preferences.class);
			startActivity(i);
			return true;
		}
		else
			return super.onOptionsItemSelected(item);
		
	}

	@Override
	protected void onResume() {							// called when an activity is brought back to front screen
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {									// called when the activity is canceled
		super.onDestroy();
		giftImg.recycle();											// call garbage collector for the bitmap
		giftImg = null;												// initialise giftImg to null
	}
		
	
		// function to open the link to the gift
	public void goToGift(View v) {									
			
		Intent goToGiftIntent = new Intent(Intent.ACTION_VIEW);
		goToGiftIntent.setData(Uri.parse(link));					// set the url
		startActivity(goToGiftIntent);
	}


		// class which performs database query in background while displaying a progressDialog then populates the listview
		private class XMLParse extends AsyncTask<String, String, Integer> {
	        
			private ProgressDialog pDialog;		// declare the progress dialog variable
	       
			@Override
	       protected void onPreExecute() {							// function called before the task executes
				super.onPreExecute();
				pDialog = new ProgressDialog(DisplayProduct.this);	// set the context
				pDialog.setMessage("Getting Product Info...");		// set the message to be shown
				pDialog.setIndeterminate(false);					// the progress not shown in percent
				pDialog.setCancelable(true);						// if the user presses back, the process is canceled
				pDialog.show();										// show the dialog box
	   			
	   			}

	       @Override
	       protected Integer doInBackground(String... args) {	// called when the previous function finishes
	    	   	try {
	   			newurl = new URL(imageUrl);							// parse the image url
	   			} 
	    	   	catch (MalformedURLException e) {
	   			Log.e("error", "DisplayProduct.java doInBackground");
	   			e.printStackTrace();
	   			}
	   			
	    	   	try {
	   			giftImg = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream()); // download the image
	    	   	} 
	    	   	catch (IOException e) {
	   			Log.e("error", "DisplayProduct.java doInBackground");
	   			e.printStackTrace();
	    	   	} 
	    	   	
	    	   	int r = 0;								// return an Integer that is not used
	    	   	return r;
	       }
	       
	        @Override
	        protected void onPostExecute(Integer r) {			// called when the previous function finishes
	    		giftView.setImageBitmap(giftImg);					// set the image to the imageView.
	        	pDialog.dismiss();									// dismiss the dialog box
	            
	        }

			@Override
			protected void onCancelled() {							// called when the dialog box is canceled		
				// TODO Auto-generated method stub
				super.onCancelled();
				pDialog.dismiss();									// dismis the dialog box to avoid memory leakage issues
			}
	        
		}
		
		public void getValues() {
			Bundle itemValues = getIntent().getExtras();		// get the values from the bundle
   			id = itemValues.getString("id");					// id in the database
   			name = itemValues.getString("name");				// name of the product
   			price = itemValues.getString("price");				// price estimate
   			description = itemValues.getString("description");	// brief description of the product
   			link = itemValues.getString("link");				// url link to the product
   			totalRating = Float.parseFloat(itemValues.getString("totalRating"));	// total rating 
   			ratingCount = Integer.parseInt(itemValues.getString("ratingCount"));	// number of ratings
   			imageUrl = itemValues.getString("imageURL");
   			if (totalRating>0 & ratingCount>0)					// don't want to divde by zero if there is no rating yet
   				averageRating = totalRating / ratingCount;		// calcuate the average rating
   			else
   				averageRating = (float)0;						// if no ratings set to zero
		}
		
		public void initialiseViews() {
			// initialise the variables to their corresponding views
   			nameView = (TextView) findViewById(R.id.full_product_name);
   			descriptionView = (TextView) findViewById(R.id.full_product_description_value);
   			priceView = (TextView) findViewById(R.id.full_product_price_value);
   			giftView = (ImageView) findViewById(R.id.giftImgDispGift);
   			ratingView = (RatingBar) findViewById(R.id.ratingBar1);
   			rateButton = (Button) findViewById(R.id.ratingButton);
		}
		
		public void populateViews() {
			// set the text of the text views as appropriate
   			nameView.setText(name);
   			descriptionView.setText(description);
   			priceView.setText(price);
   			ratingView.setRating((float) averageRating);
		}
		
		private void checkPreferences() {						// check for users previous ratings of the product
			SharedPreferences previousRating = this.getSharedPreferences("previousRating", MODE_PRIVATE); // get reference to the previous rating,
			if (previousRating.contains(id)) {										// if the user has already rated this product, identified by id
				averageRating = previousRating.getFloat(id, (float) averageRating);	// change to that rating
				rateButton.setEnabled(false);
			}	
		}
		
		// function to show the rating dialog box, called when button is clicked
		public void showDialog(View v)
		{
			final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
			//Set the layout for the dialog box, from xml file
			View dialogLayout = getLayoutInflater().inflate(R.layout.rating_dialog, (ViewGroup) getCurrentFocus());
			popDialog.setView(dialogLayout);
			rating = (RatingBar) dialogLayout.findViewById(R.id.dialogRating);	// store the rating bar in a variable
			popDialog.setIcon(android.R.drawable.btn_star_big_on);				// set the icon for dialog box
			popDialog.setTitle("Rate this Product");							// set the title
		 
			// Set what happens when ok is clicked
			popDialog.setPositiveButton(android.R.string.ok,
			new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			float newRating = rating.getProgress()/(float)2;						// get the current rating in the dialog box
			ratingView.setRating(newRating);								// set the rating  to the new rating
			SharedPreferences previousRating = getSharedPreferences("previousRating", MODE_PRIVATE); // get reference to the previous rating,
			Editor editor = previousRating.edit(); 	// create an editor
			editor.putFloat(id, newRating); 		// put the rating in the shared prefs, using id as the key
			editor.commit(); 						// commit the changes
			dialog.dismiss();						// dismiss the dialog box
			rateButton.setEnabled(false);			// disable the rating button, can only be rated once each by each user
		}
		 
		});
		 
		// Set what happens when cancel is clicked
		popDialog.setNegativeButton("Cancel",
		new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int id) {
		dialog.cancel();							// cancel the dialog box
		}
		});
		
		//
		popDialog.create();							// create the dialog box
		popDialog.show();							// show the dialog box
		}
		
}