package com.library.optimationlibrary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
public class AdminPasswordEntryActivity extends Activity implements
		OnClickListener {

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
	 * saved password, if any.
	 */
	private void setupEditText() {
		enterPassword = (EditText) findViewById(R.id.enter_password);
	}

	/**
	 * Method to map the Button objects to views in the layout and set their
	 * listener as the activity.
	 */
	private void setupButtons() {
		saveButton = (Button) findViewById(R.id.savePassword_button);
		cancelButton = (Button) findViewById(R.id.cancelPassword_button);

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
	 * PasswordEntryActivity implements OnClickListener. When one of the buttons
	 * is clicked, this method is called and acts appropriately.
	 */
	public void onClick(View v) {
		Intent returnIntent = new Intent();
		if (v.getId() == R.id.savePassword_button) {
			password = enterPassword.getText().toString();

			// Check attempted password against selected username's password in
			// webapp.
			if (null == password || "".equals(password)) {
				notifyServerResponded(false);
			} else {
				String[] data = { MainActivity.WEBAPP_URL,
						getIntent().getStringExtra("username"), password };

				new CheckPassword().execute(data);
			}

		} else if (v.getId() == R.id.cancelPassword_button) {
			// Cancel and return to MainActivity. Old name, if any, will be
			// kept.
			setResult(RESULT_CANCELED, returnIntent);
			finish();
		}
	}

	private void notifyServerResponded(boolean correctPassword) {
		Intent returnIntent = new Intent();
		if (correctPassword) {
			setResult(RESULT_OK, returnIntent);
			finish();
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Incorrect password.", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	/**
	 * @author michaello AsyncTask to query the entered admin account password
	 *         against the server. A valid password will cause both this and the
	 *         username entry activity to finish, saving the username. An
	 *         invalid password will be rejected.
	 */
	private class CheckPassword extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... data) {
			Boolean result = PostMethods.POSTCheckPassword(data);
			return result.toString();
		}

		protected void onPostExecute(String result) {
			notifyServerResponded(Boolean.parseBoolean(result));
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
