package com.library.optimationlibrary;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class UsernameEntryActivity extends Activity implements OnClickListener {

	private EditText enterUsername;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_username_entry);
		setupDim();
		setupEditText();
	}

	private void setupDim() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();  
		lp.dimAmount=0.0f;
		getWindow().setAttributes(lp);
		getWindow().setBackgroundDrawable(new ColorDrawable(0x7f000000));
	}

	private void setupEditText() {
		enterUsername = (EditText)findViewById(R.id.enter_username);
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
			returnIntent.putExtra("username", enterUsername.getText());
			setResult(RESULT_OK,returnIntent);     
			finish();
		} else if (v.getId() == R.id.cancelUsername_button) {
			setResult(RESULT_CANCELED, returnIntent);  
			finish();
		}
	}

}
