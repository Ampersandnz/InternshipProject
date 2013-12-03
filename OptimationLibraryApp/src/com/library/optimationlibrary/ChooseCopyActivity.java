package com.library.optimationlibrary;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChooseCopyActivity extends Activity implements OnItemClickListener{

	private ListView theList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
}
