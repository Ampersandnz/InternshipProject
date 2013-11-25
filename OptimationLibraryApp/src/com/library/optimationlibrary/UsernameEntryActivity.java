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

	private void setupDim() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.dimAmount=0.75f;
		getWindow().setAttributes(lp);
		getWindow().setBackgroundDrawable(new ColorDrawable(0x7f000000));
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	}

	private void setupEditText() {
		enterUsername = (EditText)findViewById(R.id.enter_username);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String username = preferences.getString("username", null);
		
		if (!(null == username)) {
			enterUsername.setText(username);
		}
	}
	
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

	@Override
	public void onClick(View v) {
		Intent returnIntent = new Intent();	
		if (v.getId() == R.id.saveUsername_button) {
			String username = enterUsername.getText().toString();
			returnIntent.putExtra("username", username);
			setResult(RESULT_OK,returnIntent); 
			finish();
		} else if (v.getId() == R.id.cancelUsername_button) {
			setResult(RESULT_CANCELED, returnIntent);  
			finish();
		}
	}

}
