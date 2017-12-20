package com.example.giftgenerator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import com.example.giftgenerator.AmazonXmlParser.Entry;

public class SearchResults extends Activity {
	
	// declare global variables

	String gender, lowerAge, higherAge, orderBy, jsonString;
    List<Entry> entryResults;

    ArrayList<HashMap<String, String>> giftList = new ArrayList<HashMap<String, String>>();	// used in populating listview

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);	// set the layout to be used
		
		Bundle values = getIntent().getExtras();	// get the values from the bundle
		gender = values.getString("gender");		// gender chosen
		lowerAge = values.getString("lowerAge");	// lower age range
		higherAge = values.getString("higherAge");	// higher age range
		orderBy = values.getString("orderBy");		// order by 

		new XMLParse().execute();					// execute the Asynchronous task, needed for progress bar. Query database, populate listview
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId()==R.id.action_settings)
		{
			Intent i = new Intent(SearchResults.this, Preferences.class);
			startActivity(i);
			return true;
		}
		else
			return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		RelativeLayout parentView = (RelativeLayout) findViewById(R.id.resultsWrapper);
		Preferences.setBackground(parentView, this);
	}

	// class which performs database query in background while displaying a progressDialog then populates the listview
	private class XMLParse extends AsyncTask<String, String, Integer> {
        
		private ProgressDialog pDialog;		// declare the progress dialog variable
       
		@Override
       protected void onPreExecute() {							// function called before the task executes
           super.onPreExecute();	
           pDialog = new ProgressDialog(SearchResults.this);	// set the context
           pDialog.setMessage("Finding Gifts...");				// set the message to be shown
           pDialog.setIndeterminate(false);						// the progress not shown in percent
           pDialog.setCancelable(true);							// if the user presses back, the process is canceled
           pDialog.show();										// show the dialog box
       }

       @Override
       protected Integer doInBackground(String... args) {	// called when the previous function finishes
           entryResults = queryDatabase();						// query the database and store the json string in a global variable
    	   downloadImages(entryResults);
    	   int r=0;												// return an Integer object that is not used
    	   return r;
       }

		@Override
        protected void onPostExecute(Integer r) {				// called when the previous function finishes
            pDialog.dismiss();									// dismiss the dialog box
            populateListView(entryResults);						// function to parce the json string and populate listview
        }
		
        private void downloadImages(List<Entry> entryResults) {
    		try {
    			for (Entry x : entryResults) {
                    String imagePath = getFilesDir() + "/" + x.id + ".jpg";
                    File file = new File(imagePath);

                    if (!file.exists()) {                                    // if it doesnt exist: download and store it

                        String imageUrl = x.imgURL;        					// initialise the url on the server
                        URL newUrl = null;                                  // encode theurl
                        try {
                            newUrl = new URL(imageUrl);                     // parse the image url
                        } catch (MalformedURLException e) {
                            Log.e("error", "DisplayProduct.java doInBackground");
                            e.printStackTrace();
                        }

                        try {
                            BitmapFactory.Options options = new BitmapFactory.Options();    // change options for the download
                            //options.inSampleSize = 3;                        // makes the download one fifth the actual image size
                            Bitmap giftImg = BitmapFactory.decodeStream(newUrl.openConnection().getInputStream(), null, options); // download the image
                            bitmapToFile(giftImg, x.id);                        // call the function to save the file to internal memory
                        } catch (IOException e) {
                            Log.e("error", "downloadImages() doInBackground");
                            e.printStackTrace();
                        }
                    }
                }
    		} catch (Exception e) {
    			// TODO: handle exception
    			Log.e("log_tag", "Error in download Images "+e.toString());
    		}
        }
	}

	// function to query the database and return results as a Entry object values
	private List<Entry> queryDatabase() {
		int i=0;

		if(gender.equals("male")) {
			if (lowerAge.equals("0") && higherAge.equals("3"))
				i = 1;
			else if (lowerAge.equals("4") && higherAge.equals("6"))
				i = 2;
			else if (lowerAge.equals("7") && higherAge.equals("9"))
				i = 3;
			else if (lowerAge.equals("10") && higherAge.equals("14"))
				i = 4;
			else if (lowerAge.equals("15") && higherAge.equals("18"))
				i = 5;
			else if (lowerAge.equals("19") && higherAge.equals("24"))
				i = 6;
			else if (lowerAge.equals("25") && higherAge.equals("30"))
				i = 7;
			else if (lowerAge.equals("31") && higherAge.equals("40"))
				i = 8;
			else if (lowerAge.equals("41") && higherAge.equals("55"))
				i = 9;
			else
				i = 10;
		}
		else{
			if (lowerAge.equals("0") && higherAge.equals("3"))
				i = 1;
			else if (lowerAge.equals("4") && higherAge.equals("6"))
				i = 2;
			else if (lowerAge.equals("7") && higherAge.equals("9"))
				i = 11;
			else if (lowerAge.equals("10") && higherAge.equals("14"))
				i = 12;
			else if (lowerAge.equals("15") && higherAge.equals("18"))
				i = 13;
			else if (lowerAge.equals("19") && higherAge.equals("24"))
				i = 14;
			else if (lowerAge.equals("25") && higherAge.equals("30"))
				i = 15;
			else if (lowerAge.equals("31") && higherAge.equals("40"))
				i = 16;
			else if (lowerAge.equals("41") && higherAge.equals("55"))
				i = 17;
			else
				i = 18;
		}

		String uri = AmazonURIGenerator.generateURI(i,orderBy);
        List<Entry> entries = null;
        try {
            entries = loadXmlFromNetwork(uri);
        }
        catch( Exception e)
        {
            Log.e("log_tag", "Error in http connection  "+e.toString());
        }

        return entries;
}


    // Downloads XML from amazon.com and parses it
	public static List<Entry> loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        AmazonXmlParser amazonXmlParser = new AmazonXmlParser();
        List<Entry> entries = null;
        try {
            stream = downloadUrl(urlString);

            entries = amazonXmlParser.parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return entries;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private static InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }
	
	// parse the json string and populate the list view
	private void populateListView(List<Entry> entryResults) {
		
		String id, name, price, description, link, totalRating, ratingCount, imgPath;
		try {
			for(Entry x:entryResults){

				id = x.id;					// store the rest of the values in variables
                price = x.price;
                totalRating = "0";
                ratingCount = "0";
                imgPath = getFilesDir() + "/" + x.id + ".jpg";
                                
                
                // Adding value HashMap key => value, used in the list adapter
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", id);		
                map.put("name", x.title);
                map.put("price", price);
                map.put("description", x.description);
                map.put("link", x.link);
                map.put("totalRating", totalRating);
                map.put("ratingCount", ratingCount);
                map.put("imgPath", imgPath);
                map.put("imageURL", x.imgURL);

                giftList.add(map);										// add the hash map to ArrayList
                ListView list=(ListView)findViewById(R.id.listView);	// assign the variable to the listview in the xml layout 

                ListAdapter adapter = new SimpleAdapter(				// set up the list adapter to populate the listview
                			SearchResults.this,							// context
                			giftList,									// ArrayList
                			R.layout.list_item,							// xml file for the listview
                			new String[] {"name", "price", "imgPath" }, 			// array of the values to put in
                			new int[] {R.id.listName ,R.id.listPrice, R.id.listImage});	// array of the textViews to populate

                list.setAdapter(adapter);								// set the adapter to the ListView

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override											// when a list item is clicked
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
                        Intent openDisplay = new Intent(SearchResults.this, DisplayProduct.class);	// initialise the intent to open the next next activity
                        
                        Bundle itemValues = new Bundle();				// put the needed values in a bundle
						itemValues.putString("id", giftList.get(+position).get("id"));
						itemValues.putString("name", giftList.get(+position).get("name"));
						itemValues.putString("price", giftList.get(+position).get("price"));
						itemValues.putString("description", giftList.get(+position).get("description"));
						itemValues.putString("link", giftList.get(+position).get("link"));
						itemValues.putString("totalRating", giftList.get(+position).get("totalRating"));
						itemValues.putString("ratingCount", giftList.get(+position).get("ratingCount"));
                        itemValues.putString("imageURL", giftList.get(+position).get("imageURL"));
						
						openDisplay.putExtras(itemValues);				// put the bundle in the intent
						startActivity(openDisplay);						// start the display product
					}
                });
			}			   
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("log_tag", "Error Parsing Data "+e.toString());
			}
	}
	
	public void bitmapToFile(Bitmap bmp, String id) {		// function to save the small bitmaps to internal memory
		try
		{
	     int size = 1;
	     ByteArrayOutputStream bos = new ByteArrayOutputStream(size);
	     bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos);
	     byte[] bArr = bos.toByteArray();
	     bos.flush();
	     bos.close();
	 
	     FileOutputStream fos = openFileOutput( id + ".jpg", Context.MODE_PRIVATE);
	     fos.write(bArr);
	     fos.flush();
	     fos.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
}