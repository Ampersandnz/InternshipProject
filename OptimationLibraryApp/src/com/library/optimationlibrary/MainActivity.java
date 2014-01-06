package com.library.optimationlibrary;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mlo.book.Book;

/**
 * 
 * @author Michael Lo
 * Using code from http://mobile.tutsplus.com/tutorials/android/android-sdk-create-a-book-scanning-app-part-3/
 * 
 */
public class MainActivity extends Activity implements OnClickListener{

	static final String WEBAPP_URL = "http://1.optimation-library-db.appspot.com/librarydbforgoogleapps";
	private static final String API_KEY = "AIzaSyBiYyZhPC3K2eTUYTHjmo3LN0-F7CQKfo0";
	private static final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:";

	private static final int _chooseUsername = 1;
	private static final int _chooseCopy = 2;

	public static final String LIBRARY_USERNAME = "_library";

	private Button scanBtn, borrowBtn, returnBtn, addBtn, deleteBtn, savedUsername;

	private TextView[] currentlyBorrowed;

	private TextView authorText, titleText, descriptionText, dateText, ratingCountText, isConnected, dbIdText, dbId, currentlyBorrowedTitle;

	private static SharedPreferences preferences;

	private LinearLayout starLayout, currentlyBorrowedList;

	private ImageView[] starViews;

	private ImageView thumbView;

	private Bitmap thumbImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupButtons();
		setupTextViews();
		setupStars();
		thumbView = (ImageView)findViewById(R.id.thumb);
		setupListView();

		if (savedInstanceState != null) {
			retrieveSavedState(savedInstanceState);
		}
	}

	private void setupButtons() {
		scanBtn = (Button)findViewById(R.id.scan_button);
		borrowBtn = (Button)findViewById(R.id.borrow_btn);
		returnBtn = (Button)findViewById(R.id.return_btn);
		addBtn = (Button)findViewById(R.id.add_btn);
		deleteBtn = (Button)findViewById(R.id.delete_btn);
		savedUsername = (Button)findViewById(R.id.saved_username);
		
		borrowBtn.setVisibility(View.GONE);
		returnBtn.setVisibility(View.GONE);
		addBtn.setVisibility(View.GONE);
		deleteBtn.setVisibility(View.GONE);
		
		scanBtn.setOnClickListener(this);
		borrowBtn.setOnClickListener(this);
		returnBtn.setOnClickListener(this);
		addBtn.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);
		savedUsername.setOnClickListener(this);
		
		savedUsername.setText(preferences.getString("username", "Choose a username"));
	}

	private void setupTextViews() {
		authorText = (TextView)findViewById(R.id.book_author);
		titleText = (TextView)findViewById(R.id.book_title);
		descriptionText = (TextView)findViewById(R.id.book_description);
		dateText = (TextView)findViewById(R.id.book_date);
		starLayout = (LinearLayout)findViewById(R.id.star_layout);
		ratingCountText = (TextView)findViewById(R.id.book_rating_count);

		currentlyBorrowedTitle = (TextView)findViewById(R.id.currentlyBorrowed_list_title);
		currentlyBorrowedTitle.setVisibility(View.GONE);

		isConnected = (TextView) findViewById(R.id.isConnected);
		isConnected.setOnClickListener(this);

		dbIdText = (TextView)findViewById(R.id.dbId_text);
		dbId = (TextView)findViewById(R.id.dbId_id);
		dbIdText.setVisibility(View.GONE);
		dbId.setVisibility(View.GONE);

		this.checkConnection();
	}

	private void setupStars() {
		starViews=new ImageView[5];
		for(int s=0; s < starViews.length; s++) {
			starViews[s]=new ImageView(this);
		}
	}

	private void setupListView() {
		currentlyBorrowedList = (LinearLayout)findViewById(R.id.currentlyBorrowed);
		new GetCurrentlyBorrowed().execute(WEBAPP_URL);
	}

	/**
	 * @param savedInstanceState
	 * Retrieves all saved data. Is called to redraw the window when the device is rotated, resumed etc.
	 */
	public void retrieveSavedState (Bundle savedInstanceState) {
		authorText.setText(savedInstanceState.getString("author"));
		titleText.setText(savedInstanceState.getString("title"));
		descriptionText.setText(savedInstanceState.getString("description"));
		dateText.setText(savedInstanceState.getString("date"));
		ratingCountText.setText(savedInstanceState.getString("ratings"));
		int numStars = savedInstanceState.getInt("stars");

		for(int s=0; s<numStars; s++) {
			starViews[s].setImageResource(R.drawable.star);
			starLayout.addView(starViews[s]);
		}

		starLayout.setTag(numStars);
		thumbImg = (Bitmap)savedInstanceState.getParcelable("thumbPic");
		thumbView.setImageBitmap(thumbImg);
		borrowBtn.setTag(savedInstanceState.getString("isbn"));

		borrowBtn.setVisibility(View.VISIBLE);
		returnBtn.setVisibility(View.VISIBLE);
		addBtn.setVisibility(View.VISIBLE);
		deleteBtn.setVisibility(View.VISIBLE); 

		dbId.setText(savedInstanceState.getString("id"));
	}

	public void onClick(View v) {
		String username = preferences.getString("username", null);

		switch(v.getId()) {

		case R.id.scan_button:
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
			break;

		case R.id.borrow_btn:
			if (null == username) {
				Intent i = new Intent(this, UsernameEntryActivity.class);
				startActivityForResult(i, _chooseUsername);
			} else {
				if (checkConnection()) {
					if (dbId.getText().toString().equals("No copy of this book found in library.")) {
						Toast noBookToast = Toast.makeText(getApplicationContext(), "No copies of this book exist in the library.", Toast.LENGTH_SHORT);
						noBookToast.show();
					} else {
						new HttpBorrowAsyncTask().execute(WEBAPP_URL);
						Toast toast = Toast.makeText(getApplicationContext(), "Book \"" + titleText.getText().toString().substring(7) + "\" borrowed by " + username + ".", Toast.LENGTH_SHORT);
						toast.show();
					}
				}
			}
			break;

		case R.id.return_btn:
			if (null == username) {
				Intent i = new Intent(this, UsernameEntryActivity.class);
				startActivityForResult(i, _chooseUsername);
			} else {
				if (checkConnection()) {
					if (dbId.getText().toString().equals("No copy of this book found in library.")) {
						Toast noBookToast = Toast.makeText(getApplicationContext(), "No copies of this book exist in the library.", Toast.LENGTH_SHORT);
						noBookToast.show();
					} else {
						new HttpReturnAsyncTask().execute(WEBAPP_URL);
						Toast toast = Toast.makeText(getApplicationContext(), "Book \"" + titleText.getText().toString().substring(7) + "\" returned by " + username + ".", Toast.LENGTH_SHORT);
						toast.show();
					}
				}
			}
			break;

		case R.id.add_btn:
			if (checkConnection()) {
				new HttpAddAsyncTask().execute(WEBAPP_URL);
				Toast addToast = Toast.makeText(getApplicationContext(), "Book \"" + titleText.getText().toString().substring(7) + "\" added to library.", Toast.LENGTH_SHORT);
				addToast.show();
			}
			break;

		case R.id.delete_btn:
			if (checkConnection()) {
				if (dbId.getText().toString().equals("No copy of this book found in library.")) {
					Toast noBookToast = Toast.makeText(getApplicationContext(), "No copies of this book exist in the library.", Toast.LENGTH_SHORT);
					noBookToast.show();
				} else {
					new HttpDeleteAsyncTask().execute(WEBAPP_URL);
					Toast deleteToast = Toast.makeText(getApplicationContext(), "Book \"" + titleText.getText().toString().substring(7) + "\" deleted from library.", Toast.LENGTH_SHORT);
					deleteToast.show();
				}
			}
			break;

		case R.id.saved_username:
			Intent i = new Intent(this, UsernameEntryActivity.class);
			startActivityForResult(i, _chooseUsername);
			break;

		case R.id.isConnected:
			checkConnection();
			break;
		}
	}

	/**
	 * Method to retrieve data and take actions as appropriate when a method called with StartActivityForResult() finishes.
	 * Will either retrieve the data from a scanned barcode and use it to pull book information from the Google Books API, 
	 * 															or get the chosen username and save it in StoredPreferences.
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (scanningResult != null) {
			String scanContent = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();
			if (scanContent!=null && scanFormat!=null && scanFormat.equalsIgnoreCase("EAN_13")) {
				if (checkConnection()) {
					borrowBtn.setTag(scanContent);
					getBook(scanContent);
				} else { 					
					Toast toast = Toast.makeText(getApplicationContext(), "No network connection!", Toast.LENGTH_SHORT);
					toast.show();
				}
			} else {
				Toast toast = Toast.makeText(getApplicationContext(), "Not a valid book!", Toast.LENGTH_SHORT);
				toast.show();
			}
		}

		switch (requestCode) {

		case _chooseUsername:
			if(resultCode == RESULT_OK) {
				String username = data.getStringExtra("username");
				Editor edit = preferences.edit();
				
				/*if (result.equals("TRUE")) {
				} else if (result.equals("FALSE")) {
					String chosenName = "";
					Toast toast = Toast.makeText(getApplicationContext(), "Name " + chosenName + "is not allowed.", Toast.LENGTH_SHORT);
					toast.show();
				}*/
				
				if (null == username || "".equals(username)) {
					savedUsername.setText("Username");
					username = null;
				} else {
					savedUsername.setText(username);
				}
				edit.putString("username", username);
				edit.apply();
				new GetCurrentlyBorrowed().execute(WEBAPP_URL);
			} else if (resultCode == RESULT_CANCELED) {
			}

		case _chooseCopy:
			if(resultCode == RESULT_OK) {
				String chosenId = data.getStringExtra("chosenId");
				if (null != chosenId && !("".equals(chosenId))) {
					dbId.setText(Long.parseLong(chosenId) + "");
					dbIdText.setVisibility(View.VISIBLE);
					dbId.setVisibility(View.VISIBLE);
					borrowBtn.setVisibility(View.VISIBLE);
					returnBtn.setVisibility(View.VISIBLE);
				}
			} else if (resultCode == RESULT_CANCELED) {
			}
			break;
		}
	}

	/**
	 * @return isConnected
	 * Method to check if a network connection has been established
	 */
	public boolean checkConnection() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			isConnected.setVisibility(View.GONE);
			return true;
		} else {
			isConnected.setVisibility(View.VISIBLE);
			isConnected.setText("#Not connected to network!# \n#Click here to rescan.#");
			return false;
		}
	}

	/**
	 * Method to save all currently displayed book data. Called when device is rotated, suspended etc.
	 */
	protected void onSaveInstanceState(Bundle savedBundle) {
		savedBundle.putString("title", "" + titleText.getText());
		savedBundle.putString("author", "" + authorText.getText());
		savedBundle.putString("description", "" + descriptionText.getText());
		savedBundle.putString("date", "" + dateText.getText());
		savedBundle.putString("ratings", "" + ratingCountText.getText());
		savedBundle.putParcelable("thumbPic", thumbImg);

		if(starLayout.getTag()!=null) {
			savedBundle.putInt("stars", Integer.parseInt(starLayout.getTag().toString()));
		}

		if(borrowBtn.getTag()!=null) {
			savedBundle.putString("isbn", borrowBtn.getTag().toString());
		}

		savedBundle.putString("id", dbId.getText().toString());
	}
	
	/**
	 * @author Michael Lo
	 * Class to asynchronously download book data from Google Books.
	 */
	private class GetBookInfo extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... bookURLs) {
			StringBuilder bookBuilder = new StringBuilder();

			for (String bookSearchURL : bookURLs) {
				HttpClient bookClient = new DefaultHttpClient();
				try {
					HttpResponse bookResponse = bookClient.execute(new HttpGet(bookSearchURL));
					StatusLine bookSearchStatus = bookResponse.getStatusLine();

					if (bookSearchStatus.getStatusCode() == 200) {
						BufferedReader bookReader = new BufferedReader(new InputStreamReader(bookResponse.getEntity().getContent()));
						String lineIn;

						while ((lineIn = bookReader.readLine()) != null) {
							bookBuilder.append(lineIn);
						}
					}
				} catch(Exception e) { 
					e.printStackTrace(); 
				}
			}
			return bookBuilder.toString();
		}
		
		protected void onPostExecute(String result) {
			try {
				JSONObject resultObject = new JSONObject(result);
				JSONArray bookArray = resultObject.getJSONArray("items");
				JSONObject bookObject = bookArray.getJSONObject(0);
				JSONObject volumeObject = bookObject.getJSONObject("volumeInfo");

				try { 
					titleText.setText("Title: " + volumeObject.getString("title")); 
				} catch (JSONException jse) { 
					titleText.setText("");
					jse.printStackTrace(); 
				}

				StringBuilder authorBuild = new StringBuilder("");
				try {
					JSONArray authorArray = volumeObject.getJSONArray("authors");
					for(int a=0; a<authorArray.length(); a++) {
						if(a>0) authorBuild.append(", ");
						authorBuild.append(authorArray.getString(a));
					}
					authorText.setText("Author(s): " + authorBuild.toString());
				} catch (JSONException jse) { 
					authorText.setText("");
					jse.printStackTrace(); 
				}

				try { dateText.setText("Date of publication: " + volumeObject.getString("publishedDate")); 
				} catch (JSONException jse) { 
					dateText.setText("");
					jse.printStackTrace(); 
				}

				try { 
					descriptionText.setText("Description: " + volumeObject.getString("description")); 
				} catch (JSONException jse) {  
					descriptionText.setText("");
					jse.printStackTrace(); 
				}

				try { 
					double decNumStars = Double.parseDouble(volumeObject.getString("averageRating"));
					int numStars = (int)decNumStars;
					starLayout.setTag(numStars);
					starLayout.removeAllViews();

					for(int s=0; s<numStars; s++) {
						starViews[s].setImageResource(R.drawable.star);
						starLayout.addView(starViews[s]);
					}
				} catch (JSONException jse) { 
					starLayout.removeAllViews();
					jse.printStackTrace(); 
				}

				try { 
					ratingCountText.setText(" - " + volumeObject.getString("ratingsCount")+" ratings"); 
				} catch (JSONException jse) {
					ratingCountText.setText("");
					jse.printStackTrace();
				}

				try {
					returnBtn.setTag(volumeObject.getString("infoLink"));
					addBtn.setVisibility(View.VISIBLE);
					deleteBtn.setVisibility(View.VISIBLE);
				} catch (JSONException jse) { 
					addBtn.setVisibility(View.GONE);
					deleteBtn.setVisibility(View.GONE);
					jse.printStackTrace(); 
				}

				try { 
					JSONObject imageInfo = volumeObject.getJSONObject("imageLinks");
					new GetBookThumb().execute(imageInfo.getString("smallThumbnail"));
				} catch(JSONException jse) { 
					thumbView.setImageBitmap(null);
					jse.printStackTrace();
				}

			} catch (Exception e) {
				//Default to empty values
				e.printStackTrace();
				titleText.setText("NOT FOUND");
				authorText.setText("");
				descriptionText.setText("");
				dateText.setText("");
				starLayout.removeAllViews();
				ratingCountText.setText("");
				thumbView.setImageBitmap(null);
			}
		}
	}

	/**
	 * @author Michael Lo
	 * Class to asynchronously find books in the database matching the ISBN of the scanned book.
	 * If more than one copy exists in the database, the user will be prompted to select the copy they wish to interact with.
	 */
	private class GetBookIds extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... ISBNs) {
			String url = ISBNs[0];
			String isbn = ISBNs[1];

			Book book = new Book();
			book.setIsbn(isbn);

			String returnValue = PostMethods.POSTGetBookFromIsbn(url, book);
			return returnValue;
		}

		protected void onPostExecute(String result) {
			ObjectMapper mapper = new ObjectMapper();
			List<Book> booksMatchingId = null;

			try {
				booksMatchingId = mapper.readValue(result, new TypeReference<List<Book>>(){});
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (null == booksMatchingId || booksMatchingId.isEmpty()) {
				dbId.setText("No copy of this book found in library.");
				dbId.setVisibility(View.VISIBLE);
				dbIdText.setVisibility(View.GONE);
				borrowBtn.setVisibility(View.GONE);
				returnBtn.setVisibility(View.GONE);
				deleteBtn.setVisibility(View.GONE);
			} else if (booksMatchingId.size() == 1) {
				dbId.setText(booksMatchingId.get(0).getId().toString());
				dbIdText.setVisibility(View.VISIBLE);
				dbId.setVisibility(View.VISIBLE);
				borrowBtn.setVisibility(View.VISIBLE);
				returnBtn.setVisibility(View.VISIBLE);
				deleteBtn.setVisibility(View.VISIBLE);
			} else {
				Intent i = new Intent(MainActivity.this, ChooseCopyActivity.class);
				ArrayList<String> inPossessionOf = new ArrayList<String>();
				ArrayList<String> ids = new ArrayList<String>();
				for (Book b: booksMatchingId) {
					inPossessionOf.add(b.getInPossessionOf());
					ids.add(b.getId().toString());
				}
				i.putStringArrayListExtra("inPossessionOf", inPossessionOf);
				i.putStringArrayListExtra("ids", ids);

				startActivityForResult(i, _chooseCopy);
			}
		}
	}

	/**
	 * @author Michael Lo
	 * Class to asynchronously download book cover image from Google Books.
	 */
	private class GetBookThumb extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... thumbURLs) {
			try {
				URL thumbURL = new URL(thumbURLs[0]);
				URLConnection thumbConn = thumbURL.openConnection(); 
				thumbConn.connect(); 
				InputStream thumbIn = thumbConn.getInputStream(); 
				BufferedInputStream thumbBuff = new BufferedInputStream(thumbIn); 
				thumbImg = BitmapFactory.decodeStream(thumbBuff);

				thumbBuff.close(); 
				thumbIn.close(); 
			} catch(Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		protected void onPostExecute(String result) {
			thumbView.setImageBitmap(thumbImg);
		}
	}

	private class HttpAddAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... URLs) {

			Book book = new Book(borrowBtn.getTag().toString(), titleText.getText().toString().substring(6), LIBRARY_USERNAME);

			String returnValue = "";

			returnValue = PostMethods.POSTAdd(URLs[0], book);

			return returnValue;
		}

		@Override
		protected void onPostExecute(String result) {
			dbId.setText(result);
			dbIdText.setVisibility(View.VISIBLE);
			dbId.setVisibility(View.VISIBLE);
			borrowBtn.setVisibility(View.VISIBLE);
			returnBtn.setVisibility(View.VISIBLE);
			deleteBtn.setVisibility(View.VISIBLE);
			new GetCurrentlyBorrowed().execute(WEBAPP_URL);
		}
	}	

	private class HttpDeleteAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... URLs) {
			Book book = new Book();

			try {
				book = new Book(Long.parseLong(dbId.getText().toString()), borrowBtn.getTag().toString(), titleText.getText().toString().substring(6), preferences.getString("username", LIBRARY_USERNAME));
			} catch (Exception e) {
				e.printStackTrace();
			}

			String returnValue = "";

			returnValue = PostMethods.POSTDelete(URLs[0], book);

			return returnValue;
		}

		@Override
		protected void onPostExecute(String result) {
			dbIdText.setVisibility(View.GONE);
			dbId.setVisibility(View.GONE);
			new GetBookIds().execute(WEBAPP_URL, borrowBtn.getTag().toString());
			new GetCurrentlyBorrowed().execute(WEBAPP_URL);
		}
	}	

	private class HttpBorrowAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... URLs) {

			Book book = new Book(Long.parseLong(dbId.getText().toString()), borrowBtn.getTag().toString(), titleText.getText().toString().substring(6), preferences.getString("username", LIBRARY_USERNAME));

			String returnValue = "";

			returnValue = PostMethods.POSTBorrow(URLs[0], book);

			return returnValue;
		}

		@Override
		protected void onPostExecute(String result) {
			new GetCurrentlyBorrowed().execute(WEBAPP_URL);
		}
	}

	private class HttpReturnAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... URLs) {

			Book book = new Book(Long.parseLong(dbId.getText().toString()), borrowBtn.getTag().toString(), titleText.getText().toString().substring(6), preferences.getString("username", LIBRARY_USERNAME));

			String returnValue = "";

			returnValue = PostMethods.POSTReturn(URLs[0], book);

			return returnValue;
		}

		@Override
		protected void onPostExecute(String result) {
			new GetCurrentlyBorrowed().execute(WEBAPP_URL);
		}
	}

	private void getBook(String isbn) {
		// Uses my Google Books API key, and substitutes the ISBN pulled from the barcode.
		String bookSearchString = GOOGLE_BOOKS_URL + isbn + "&key=" + API_KEY;
		new GetBookInfo().execute(bookSearchString);
		new GetBookIds().execute(WEBAPP_URL, isbn);
		new GetCurrentlyBorrowed().execute(WEBAPP_URL);
	}

	/**
	 * @author Michael Lo
	 * Class to asynchronously find books in the database matching the ISBN of the scanned book.
	 * If more than one copy exists in the database, the user will be prompted to select the copy they wish to interact with.
	 */
	private class GetCurrentlyBorrowed extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... URLs) {
			String url = URLs[0];
			String returnValue = "";
			String username = preferences.getString("username", "");

			Book book = new Book();
			if (username != null) {
				book.setInPossessionOf(username);
				returnValue = PostMethods.POSTGetBorrowed(url, book);
			}
			return returnValue;
		}

		protected void onPostExecute(String result) {
			ObjectMapper mapper = new ObjectMapper();
			List<Book> borrowedByUser = null;

			try {
				borrowedByUser = mapper.readValue(result, new TypeReference<List<Book>>(){});
			} catch (Exception e) {
				e.printStackTrace();
			}

			currentlyBorrowedList.removeAllViews();

			if (!(null == borrowedByUser) && !(borrowedByUser.isEmpty())) {

				currentlyBorrowed = new TextView[borrowedByUser.size()];

				for (int i = 0; i < currentlyBorrowedList.getChildCount(); i++) {
					View v = currentlyBorrowedList.getChildAt(i);
					v.setVisibility(View.GONE);
				}

				for(int t = 0; t < currentlyBorrowed.length; t++) {
					TextView text = currentlyBorrowed[t];
					text = new TextView(MainActivity.this);
					text.setText(borrowedByUser.get(t).toString());
					currentlyBorrowedList.addView(text);
				}

				currentlyBorrowedTitle.setVisibility(View.VISIBLE);
			} else {
				currentlyBorrowedTitle.setVisibility(View.GONE);
			}
		}
	}
}
