package com.library.optimationlibrary;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChooseCopyActivity extends Activity implements OnItemClickListener {

	private ListView theList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_choose_copy);
		setupDim();
		setupListView();
	}

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

	private void setupListView() {
		theList = (ListView) findViewById(R.id.chooseCopyList);
		ArrayList<String> nameList = getIntent().getStringArrayListExtra(
				"inPossessionOf");
		theList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, nameList));
		theList.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.username_entry, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent returnIntent = new Intent();
		returnIntent.putExtra("chosenId",
				getIntent().getStringArrayListExtra("ids").get(position));
		setResult(RESULT_OK, returnIntent);
		finish();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// If a touch notification is received that is outside the activity, do
		// nothing.
		// This forces the user to select a copy of the book before the activity
		// can close.
		if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
		}

		// Otherwise delegate action to Activity.class.
		return super.onTouchEvent(event);
	}
}
