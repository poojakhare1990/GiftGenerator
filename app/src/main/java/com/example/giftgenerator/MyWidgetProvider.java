package com.example.giftgenerator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.RemoteViews;

public class MyWidgetProvider extends AppWidgetProvider {
	// initialise global variables
	RemoteViews v;
	String title, id, name, price,  description, link, totalRating, ratingCount;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		//allow network on main thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		// get the number of widgets on the device
		final int N = appWidgetIds.length;
		
		// for each widget on the device
		for(int i = 0; i < N; i++){
			
			int awId = appWidgetIds[i];						// store the appWidget id
			
			List<AmazonXmlParser.Entry> entryResults= queryDatabase();			// query the database and store the returned json string in a global variable

			AmazonXmlParser.Entry x= entryResults.get(0);
			try {
				id = x.id; // get the data from the random row in the database
				price = x.price;				// store all the strings in global variables
				totalRating = "0";
				ratingCount = "0";
				title = "Gift of The Day";
				String imageUrl = x.imgURL;  // set the url for the image

				URL newUrl = null;
				Bitmap giftImg = null;
				try {
			   		newUrl = new URL(imageUrl);							// parse the image url
			   		} 
			    catch (MalformedURLException e) {
			    	Log.e("error", "DisplayProduct.java doInBackground");
			   		e.printStackTrace();
			   		}
			   			
			    try {
			   		giftImg = BitmapFactory.decodeStream(newUrl.openConnection() .getInputStream()); // download the image
			       	} 
			    catch (IOException e) {
			   		Log.e("error", "DisplayProduct.java doInBackground");
			   		e.printStackTrace();
			       	} 
			
			    Intent openDisplay = new Intent(context, DisplayProduct.class);	// initialise the intent to open the next next activity
                
                Bundle itemValues = new Bundle();				// put the needed values in a bundle
				itemValues.putString("id", x.id);
				itemValues.putString("name", x.title);
				itemValues.putString("price", x.price);
				itemValues.putString("description", x.description);
				itemValues.putString("link", x.link);
				itemValues.putString("totalRating", totalRating);
				itemValues.putString("ratingCount", ratingCount);
				openDisplay.putExtras(itemValues);				// put the bundle in the intent
				
				// pending intent which calls the intent when clicked - passed context, unique identifier, intent, and null
				PendingIntent pendingIntent = PendingIntent.getActivity(context, Integer.parseInt(id), openDisplay, 0);	// declare the pending intent, calls intent onclick of button			    
				
			    v = new RemoteViews(context.getPackageName(),R.layout.activity_my_widget_provider);
			    v.setOnClickPendingIntent(R.id.widgetImage, pendingIntent);  // set the onclick listener for the button to call the pending intent
			
			    v.setImageViewBitmap(R.id.widgetImage, giftImg);			// set the image of the product
			    v.setTextViewText(R.id.widgetProductName, name);			// set the name of the product in the textview
				v.setTextViewText(R.id.widgetTitle, title);					// set the title of the widget	
				} 
			catch (Exception e) {
					e.printStackTrace();
				}
			
			appWidgetManager.updateAppWidget(awId,v);						// update the widget, set to happen once a day
		}		
	}
	
	// function to query the database and return results as a json String
		private List<AmazonXmlParser.Entry> queryDatabase() {
			String uri = AmazonURIGenerator.generateURI(19,"relevancerank");
			List<AmazonXmlParser.Entry> entries = null;
			try {
				entries = SearchResults.loadXmlFromNetwork(uri);
			}
			catch( Exception e)
			{
				Log.e("log_tag", "Error in http connection  "+e.toString());
			}

			return entries;
	}
}
