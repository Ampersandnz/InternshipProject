package com.library.optimationlibrary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Michael Lo Activity containing an EditText, that is brought up for
 *         users to enter their name. LIBRARY_USERNAME is reserved, as books in
 *         possession of that name are considered in the library. Acts like a
 *         dialog, and returns data to MainActivity when the save button is
 *         clicked.
 */
public class UsernameEntryActivity extends Activity implements OnClickListener {

	private EditText enterUsername;

	private Button saveButton;
	private Button cancelButton;

	private String username;

	private static final int ADMINPASSWORDENTRY = 0;

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
		lp.dimAmount = 0.75f;
		window.setAttributes(lp);
		window.setBackgroundDrawable(new ColorDrawable(0x7f000000));
		window.addFlags(LayoutParams.FLAG_DIM_BEHIND);

		// Allows interception of screen touches that are outside the activity.
		window.setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL,
				LayoutParams.FLAG_NOT_TOUCH_MODAL);
		window.setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
	}

	/**
	 * Method to map the EditText object to the view in the layout and load the
	 * saved username, if any.
	 */
	private void setupEditText() {
		enterUsername = (EditText) findViewById(R.id.enter_username);
		String username = PreferenceManager.getDefaultSharedPreferences(this)
				.getString("username", null);

		if (!(null == username)) {
			enterUsername.setText(username);
		}
	}

	/**
	 * Method to map the Button objects to views in the layout and set their
	 * listener as the activity.
	 */
	private void setupButtons() {
		saveButton = (Button) findViewById(R.id.saveUsername_button);
		cancelButton = (Button) findViewById(R.id.cancelUsername_button);

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
	 * UsernameEntryActivity implements OnClickListener. When one of the buttons
	 * is clicked, this method is called and acts appropriately.
	 */
	public void onClick(View v) {
		Intent returnIntent = new Intent();
		if (v.getId() == R.id.saveUsername_button) {
			username = enterUsername.getText().toString();

			// Check attempted username against webapp's list of allowed
			// usernames.
			if (null == username || "".equals(username)) {
				notifyServerResponded(false, false);
			} else {
				String[] data = { MainActivity.WEBAPP_URL, username };
				new CheckUsername().execute(data);
			}

		} else if (v.getId() == R.id.cancelUsername_button) {
			// Cancel and return to MainActivity. Old name, if any, will be
			// kept.
			setResult(RESULT_CANCELED, returnIntent);
			finish();
		}
	}

	private void notifyServerResponded(boolean validName, boolean isAdmin) {
		Intent returnIntent = new Intent();
		if (validName) {
			returnIntent.putExtra("username", username);
			returnIntent.putExtra("isAdmin", isAdmin);
			setResult(RESULT_OK, returnIntent);
			finish();
		} else {
			Toast toast = Toast.makeText(getApplicationContext(), "Name \'"
					+ username + "\' is not allowed.", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	/**
	 * Method to retrieve data and take actions as appropriate when a method
	 * called with StartActivityForResult() finishes. Will either retrieve the
	 * data from a scanned barcode and use it to pull book information from the
	 * Google Books API, or get the chosen username and save it in
	 * StoredPreferences.
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ADMINPASSWORDENTRY:
			if (resultCode == RESULT_OK) {
				// Correct password entered. Perform same actions as save button
				// on non-admin.
				notifyServerResponded(true, true);
			} else if (resultCode == RESULT_CANCELED) {
				// Password entry canceled. Do nothing. Allow user to attempt a
				// different username.
			}
			break;
		}
	}

	/**
	 * @author michaello AsyncTask to query the entered username against the
	 *         server. A valid username will be saved, an invalid username will
	 *         be rejected and the username of an admin account will cause the
	 *         password entry activity to be displayed.
	 */
	private class CheckUsername extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... data) {
			String url = data[0];
			String username = data[1];

			String result = PostMethods.POSTIsAllowedName(url, username);

			return result;
		}

		protected void onPostExecute(String result) {
			if (result.equals("TRUE")) {
				notifyServerResponded(true, false);
			} else if (result.equals("FALSE")) {
				notifyServerResponded(false, false);
			} else if (result.equals("ADMIN")) {
				Intent intent = new Intent(UsernameEntryActivity.this,
						AdminPasswordEntryActivity.class);
				intent.putExtra("username", username);
				startActivityForResult(intent, ADMINPASSWORDENTRY);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// If a touch notification is received that is outside the activity, do
		// nothing.
		// This forces the user to use the buttons.
		if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
		}

		// Otherwise delegate action to Activity.class.
		return super.onTouchEvent(event);
	}
}
