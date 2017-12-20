package com.example.giftgenerator;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.app.Activity;
import android.content.Intent;


public class MainActivity extends Activity {
	
	// Declare Variables
	String gender, lowerAge, higherAge, orderBy;
	RadioGroup genderGroup;
	RadioButton genderButton;
	Spinner ageSpinner, sortSpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		// initialise the variables to their views
		ageSpinner = (Spinner) findViewById(R.id.ageSpinnerMain);
		sortSpinner = (Spinner) findViewById(R.id.sortSpinnerMain);
		genderGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);
		
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapterAge = ArrayAdapter.createFromResource(this,
		        R.array.age_range_array, android.R.layout.simple_spinner_dropdown_item);
		ArrayAdapter<CharSequence> adapterSort = ArrayAdapter.createFromResource(this,
		        R.array.sort_by_array, android.R.layout.simple_spinner_dropdown_item);

		// Specify the layout to use when the list of choices appears
		adapterAge.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapterSort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Apply the adapter to the spinner
		ageSpinner.setAdapter(adapterAge);
		sortSpinner.setAdapter(adapterSort);
			
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    // called when settings is clicked in option menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

	if(item.getItemId()==R.id.action_settings)
	{
		Intent i = new Intent(MainActivity.this, Preferences.class);
		startActivity(i);
		return true;
	}
	else
		return super.onOptionsItemSelected(item);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// change background to the one selected
		RelativeLayout parentView = (RelativeLayout) findViewById(R.id.mainWrapper);
		Preferences.setBackground(parentView, this);

	}
	

	// function called when the find a present button is pressed set in the onclick attribute in xml
	public void findGifts(View v){
        // initialise the intent to open the next next activity
		Intent openResults = new Intent(MainActivity.this, SearchResults.class);
       // function to get the user inputs from the radio buttons and spinners and store them in variables
		getValues();
		Bundle values = new Bundle();				// put the variables in a bundle
		values.putString("gender", gender);
		values.putString("lowerAge", lowerAge);
		values.putString("higherAge", higherAge);
		values.putString("orderBy", orderBy);
		openResults.putExtras(values);			// put the bundle into the intent
		startActivity(openResults);				// open the SearchResults activity
	}
	
	private void getValues() {
		// Get user choice of gender
		int selectedGender = genderGroup.getCheckedRadioButtonId();
		if (selectedGender == R.id.male)
			gender = "male";
		else
			gender = "female";
		
		// Get user choice of sort order
		String orderChoice = sortSpinner.getSelectedItem().toString();
		if (orderChoice.equals("Price: High to Low"))
			orderBy =  "-price";
		else if (orderChoice.equals("Price: Low to High"))
			orderBy = "price";
		else if (orderChoice.equals("Highest Rating") )
			orderBy = "psrank";
		
		// Get user choice of Age range from the spinner
		String ageRange = ageSpinner.getSelectedItem().toString();
		if (ageRange.startsWith("0 ")){
			lowerAge = "0"; 
			higherAge= "3";
		}
		else if (ageRange.startsWith("4 ")){
			lowerAge = "4"; 
			higherAge= "6";
		}
		
		else if (ageRange.startsWith("7 ")){
			lowerAge = "7"; 
			higherAge= "9";
		}
		
		else if (ageRange.startsWith("10")){
			lowerAge = "10"; 
			higherAge= "14";
		}
		
		else if (ageRange.startsWith("15")){
			lowerAge = "15"; 
			higherAge= "18";
		}
		
		else if (ageRange.startsWith("19")){
			lowerAge = "19"; 
			higherAge= "24";
		}
		
		else if (ageRange.startsWith("25")){
			lowerAge = "25"; 
			higherAge= "30";
		}
		
		else if (ageRange.startsWith("31")){
			lowerAge = "31"; 
			higherAge= "40";
		}
		
		else if (ageRange.startsWith("41")){
			lowerAge = "41"; 
			higherAge= "55";
		}
		
		else if (ageRange.startsWith("55")){
			lowerAge = "55"; 
			higherAge= "150";
		}			
	}
}