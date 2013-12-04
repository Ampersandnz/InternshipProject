package com.library.optimationlibrary;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChooseCopyActivity extends Activity implements OnItemClickListener{

	private ListView theList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Make us non-modal, so that others can receive touch events.
		getWindow().setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL, LayoutParams.FLAG_NOT_TOUCH_MODAL);

		// ...but notify us that it happened.
		getWindow().setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

		setContentView(R.layout.activity_choose_copy);
		setupDim();
		setupListView();
	}

	private void setupDim() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.dimAmount=0.75f;
		getWindow().setAttributes(lp);
		getWindow().setBackgroundDrawable(new ColorDrawable(0x7f000000));
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	}

	private void setupListView() {
		theList = (ListView)findViewById(R.id.chooseCopyList);

		ArrayList<String> nameList = getIntent().getStringArrayListExtra("inPossessionOf");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nameList);

		theList.setAdapter(adapter);
		theList.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.username_entry, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent returnIntent = new Intent();
		ArrayList<String> inPossessionOf = getIntent().getStringArrayListExtra("inPossessionOf");
		ArrayList<String> ids = getIntent().getStringArrayListExtra("ids");
		Log.d("DEBUG", "Position clicked: " + position);
		Log.d("DEBUG", "Name clicked: " + inPossessionOf.get(position));
		Log.d("DEBUG", "Id returned: " + ids.get(position));
		returnIntent.putExtra("chosenId", getIntent().getStringArrayListExtra("ids").get(position));
		setResult(RESULT_OK,returnIntent); 
		finish();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// If we've received a touch notification that the user has touched
		// outside the app, do nothing. This forces the user to select a copy of the book before the activity can close.
		if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
		}
		
		// Delegate everything else to Activity.
		return super.onTouchEvent(event);
	}
}
