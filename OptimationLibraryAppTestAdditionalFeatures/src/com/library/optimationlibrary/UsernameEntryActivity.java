package com.library.optimationlibrary;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * @author Michael Lo
 * Activity containing an EditText, that is brought up for users to enter their name. LIBRARY_USERNAME is reserved, as books in possession of that name are considered in the library.
 * Acts as a dialog, and returns data to MainActivity when the save button is clicked.
 *
 */
public class UsernameEntryActivity extends Activity implements OnClickListener {
	
	private EditText enterUsername;
	
	private Button saveButton;
	private Button cancelButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_username_entry);
		setupDim();
		setupEditText();
		setupButtons();
	}

	/**
	 * Method to dim the background and make the window more easily readable, as well as looking better.
	 */
	private void setupDim() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.dimAmount=0.75f;
		getWindow().setAttributes(lp);
		getWindow().setBackgroundDrawable(new ColorDrawable(0x7f000000));
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	}

	/**
	 * Method to map the EditText object to the view in the layout and load the saved username, if any.
	 */
	private void setupEditText() {
		enterUsername = (EditText)findViewById(R.id.enter_username);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String username = preferences.getString("username", null);
		
		if (!(null == username)) {
			enterUsername.setText(username);
		}
	}
	
	/**
	 * Method to map the Button objects to views in the layout and set their listener as the activity.
	 */
	private void setupButtons() {
		saveButton = (Button)findViewById(R.id.saveUsername_button);
		cancelButton = (Button)findViewById(R.id.cancelUsername_button);

		saveButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.username_entry, menu);
		return true;
	}

	/**
	 * UsernameEntryActivity implements OnClickListener. When one of the buttons is clicked, this method is called and acts appropriately.
	 */
	public void onClick(View v) {
		Intent returnIntent = new Intent();	
		if (v.getId() == R.id.saveUsername_button) {
			String username = enterUsername.getText().toString();
			//LIBRARY_USERNAME is reserved just in case, although it's unlikely that allowing it would cause issues or that anyone would attempt to set their name to it.
			if (username.equals(MainActivity.LIBRARY_USERNAME)) {
				Toast toast = Toast.makeText(getApplicationContext(), "This username is reserved. Please choose another.", Toast.LENGTH_SHORT);
				toast.show();
			} else {
				//Return chosen name.
				returnIntent.putExtra("username", username);
				setResult(RESULT_OK,returnIntent); 
				finish();
			}
			//Cancel and return to MainActivity. Old name, if any, will be kept.
		} else if (v.getId() == R.id.cancelUsername_button) {
			setResult(RESULT_CANCELED, returnIntent);
			finish();
		}
	}

}
