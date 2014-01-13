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
public class UsernameEntryActivity extends Activity implements OnClickListener {

	private EditText enterUsername;

	private Button saveButton;
	private Button cancelButton;

	private String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_username_entry);
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
	 * Method to map the EditText object to the view in the layout and load the saved username, if any.
	 */
	private void setupEditText() {
		enterUsername = (EditText)findViewById(R.id.enter_username);
		String username = PreferenceManager.getDefaultSharedPreferences(this).getString("username", null);

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
			username = enterUsername.getText().toString();

			// Check attempted username against webapp's list of allowed usernames.
			String[] data = {MainActivity.WEBAPP_URL, username};
			
			if (null == username || "".equals(username)) {
				notifyServerResponded(true);
			} else {
				new CheckIsAllowedName().execute(data);
			}
			
		} else if (v.getId() == R.id.cancelUsername_button) {
			// Cancel and return to MainActivity. Old name, if any, will be kept.
			setResult(RESULT_CANCELED, returnIntent);
			finish();
		}
	}

	private void notifyServerResponded(boolean validName) {
		Intent returnIntent = new Intent();
		if (validName) {
			returnIntent.putExtra("username", username);
			setResult(RESULT_OK,returnIntent); 
			finish();
		} else {
			Toast toast = Toast.makeText(getApplicationContext(), "Name \'" +  username + "\' is not allowed.", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	/**
	 * @author Michael Lo
	 * Class to asynchronously check whether or not the name entered by the user is in the list allowed in the system.
	 */
	private class CheckIsAllowedName extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... URLs) {
			String url = URLs[0];
			String attemptedUsername = URLs[1];

			String returnValue = PostMethods.POSTIsAllowedName(url, attemptedUsername);

			return returnValue;
		}

		protected void onPostExecute(String result) {
			if (result.equals("TRUE")) {
				notifyServerResponded(true);
			} else if (result.equals("FALSE")) {
				notifyServerResponded(false);
			}
		}
	}
}
