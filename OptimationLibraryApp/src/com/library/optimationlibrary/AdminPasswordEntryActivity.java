package com.library.optimationlibrary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Michael Lo
 * Activity containing an EditText, that is brought up for users to enter their name. LIBRARY_USERNAME is reserved, as books in possession of that name are considered in the library.
 * Acts like a dialog, and returns data to MainActivity when the save button is clicked.
 */
public class AdminPasswordEntryActivity extends Activity implements OnClickListener {

	private EditText enterPassword;

	private Button saveButton;
	private Button cancelButton;

	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_password_entry);
		setupDim();
		setupEditText();
		setupButtons();
	}

	/**
	 * Method to dim the background and make the window more easily readable.
	 */
	private void setupDim() {
		Window window = getWindow();
		LayoutParams lp = getWindow().getAttributes();
		lp.dimAmount=0.75f;
		window.setAttributes(lp);
		window.setBackgroundDrawable(new ColorDrawable(0x7f000000));
		window.addFlags(LayoutParams.FLAG_DIM_BEHIND);
	}

	/**
	 * Method to map the EditText object to the view in the layout and load the saved password, if any.
	 */
	private void setupEditText() {
		enterPassword = (EditText)findViewById(R.id.enter_password);
		enterPassword.setText("password");
	}

	/**
	 * Method to map the Button objects to views in the layout and set their listener as the activity.
	 */
	private void setupButtons() {
		saveButton = (Button)findViewById(R.id.savePassword_button);
		cancelButton = (Button)findViewById(R.id.cancelPassword_button);

		saveButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.admin_password_entry, menu);
		return true;
	}

	/**
	 * PasswordEntryActivity implements OnClickListener. When one of the buttons is clicked, this method is called and acts appropriately.
	 */
	public void onClick(View v) {
		Intent returnIntent = new Intent();
		if (v.getId() == R.id.savePassword_button) {
			password = enterPassword.getText().toString();
			
			// Check attempted password against selected username's password in webapp.
			if (null == password || "".equals(password)) {
				notifyServerResponded(false);
			} else {
				String url = MainActivity.WEBAPP_URL;
				String attemptedPassword = password;
				String username = data.getStringExtra("username");//get from intent putextra
				
				boolean result = PostMethods.POSTCheckPassword(url, username, attemptedPassword);

				if (result) {
					notifyServerResponded(true);
				} else {
					notifyServerResponded(false);
				}
			}
			
		} else if (v.getId() == R.id.cancelPassword_button) {
			// Cancel and return to MainActivity. Old name, if any, will be kept.
			setResult(RESULT_CANCELED, returnIntent);
			finish();
		}
	}

	private void notifyServerResponded(boolean correctPassword) {
		Intent returnIntent = new Intent();
		if (correctPassword) {
			setResult(RESULT_OK,returnIntent); 
			finish();
		} else {
			Toast toast = Toast.makeText(getApplicationContext(), "Incorrect password.", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
}
