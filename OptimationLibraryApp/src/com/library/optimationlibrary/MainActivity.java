package com.library.optimationlibrary;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.zbar.Symbol;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlo.book.Book;

/**
 * @author Michael Lo Using code from
 *         http://mobile.tutsplus.com/tutorials/android
 *         /android-sdk-create-a-book-scanning-app-part-3/ and
 *         https://github.com/DushyanthMaguluru/ZBarScanner
 */
public class MainActivity extends Activity implements OnClickListener {

	static final String WEBAPP_URL = "http://1.optimation-library-db.appspot.com/librarydbforgoogleapps";
	private static final String API_KEY = "AIzaSyBiYyZhPC3K2eTUYTHjmo3LN0-F7CQKfo0";
	private static final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:";

	private static String currentBookIsbn = null;
	private static String currentBookId = null;

	private static final int CHOOSE_USERNAME = 1;
	private static final int CHOOSE_COPY = 2;
	private static final int SCAN_BARCODE = 3;

	private static final int[] ALLOWED_FORMATS = new int[] { Symbol.ISBN10,
			Symbol.ISBN13, Symbol.EAN8, Symbol.EAN13 };

	public static final String LIBRARY_USERNAME = "_library";

	private Button scanBtn, borrowBtn, returnBtn, addBtn, deleteBtn,
			savedUsername;

	private TextView[] currentlyBorrowed;

	private TextView authorText, titleText, descriptionText, dateText,
			ratingCountText, isConnected, dbIdText, dbId,
			currentlyBorrowedTitle;

	static SharedPreferences preferences;

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
		thumbView = (ImageView) findViewById(R.id.thumb);
		setupListView();

		if (savedInstanceState != null) {
			retrieveSavedState(savedInstanceState);
		}
	}

	/**
	 * Map all Buttons in the layout to objects and assign this activity as a
	 * listener.
	 */
	private void setupButtons() {
		scanBtn = (Button) findViewById(R.id.scan_button);
		borrowBtn = (Button) findViewById(R.id.borrow_btn);
		returnBtn = (Button) findViewById(R.id.return_btn);
		addBtn = (Button) findViewById(R.id.add_btn);
		deleteBtn = (Button) findViewById(R.id.delete_btn);
		savedUsername = (Button) findViewById(R.id.saved_username);

		setButtonVisibility();

		scanBtn.setOnClickListener(this);
		borrowBtn.setOnClickListener(this);
		returnBtn.setOnClickListener(this);
		addBtn.setOnClickListener(this);
		deleteBtn.setOnClickListener(this);
		savedUsername.setOnClickListener(this);

		savedUsername.setText(preferences.getString("username", null));
	}

	/**
	 * Map all TextViews in the layout to objects.
	 */
	private void setupTextViews() {
		authorText = (TextView) findViewById(R.id.book_author);
		titleText = (TextView) findViewById(R.id.book_title);
		descriptionText = (TextView) findViewById(R.id.book_description);
		dateText = (TextView) findViewById(R.id.book_date);
		starLayout = (LinearLayout) findViewById(R.id.star_layout);
		ratingCountText = (TextView) findViewById(R.id.book_rating_count);

		currentlyBorrowedTitle = (TextView) findViewById(R.id.currentlyBorrowed_list_title);
		if (!(savedUsername.getText().toString().equals(null))) {
			currentlyBorrowedTitle.setVisibility(View.VISIBLE);
		} else {
			currentlyBorrowedTitle.setVisibility(View.GONE);
		}

		isConnected = (TextView) findViewById(R.id.isConnected);
		isConnected.setOnClickListener(this);

		dbIdText = (TextView) findViewById(R.id.dbId_text);
		dbId = (TextView) findViewById(R.id.dbId_id);
		dbIdText.setVisibility(View.GONE);
		dbId.setText(null);
		currentBookId = null;
		dbId.setVisibility(View.GONE);

		this.checkConnection();
	}

	/**
	 * Prepare the list of ImageViews that will display the five rating stars
	 * for a book.
	 */
	private void setupStars() {
		starViews = new ImageView[5];
		for (int s = 0; s < starViews.length; s++) {
			starViews[s] = new ImageView(this);
		}
	}

	/**
	 * Prepare the ListView displaying the list of books currently assigned to
	 * the chosen username.
	 */
	private void setupListView() {
		currentlyBorrowedList = (LinearLayout) findViewById(R.id.currentlyBorrowed);
		new GetCurrentlyBorrowed().execute(WEBAPP_URL);
	}

	/**
	 * @param savedInstanceState
	 *            Retrieves all saved data. Is called to redraw the window when
	 *            the device is rotated, resumed etc.
	 */
	public void retrieveSavedState(Bundle savedInstanceState) {
		authorText.setText(savedInstanceState.getString("author"));
		titleText.setText(savedInstanceState.getString("title"));
		descriptionText.setText(savedInstanceState.getString("description"));
		dateText.setText(savedInstanceState.getString("date"));
		ratingCountText.setText(savedInstanceState.getString("ratings"));
		int numStars = savedInstanceState.getInt("stars");

		for (int s = 0; s < numStars; s++) {
			starViews[s].setImageResource(R.drawable.star);
			starLayout.addView(starViews[s]);
		}

		starLayout.setTag(numStars);
		thumbImg = (Bitmap) savedInstanceState.getParcelable("thumbPic");
		thumbView.setImageBitmap(thumbImg);

		setButtonVisibility();

		dbId.setText(savedInstanceState.getString("id"));
		currentBookId = (savedInstanceState.getString("id"));
		currentBookIsbn = (savedInstanceState.getString("isbn"));
	}

	/**
	 * When a View (usually a Button) that this activity listens to is clicked,
	 * perform various required actions in response.
	 */
	public void onClick(View v) {
		String username = preferences.getString("username", null);

		switch (v.getId()) {

		case R.id.scan_button:
			// Start a new book scan.
			if (isCameraAvailable()) {
				if (checkConnection()) {
					Intent intent = new Intent(this, ZBarScannerActivity.class);
					intent.putExtra(ZBarConstants.SCAN_MODES, ALLOWED_FORMATS);
					startActivityForResult(intent, SCAN_BARCODE);
				}
			} else {
				Toast.makeText(this, "Rear Facing Camera Unavailable",
						Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.borrow_btn:
			// Assign the current copy of the currently scanned book to the
			// chosen username.
			if (null == username) {
				Intent i = new Intent(this, UsernameEntryActivity.class);
				startActivityForResult(i, CHOOSE_USERNAME);
			} else {
				if (checkConnection()) {
					if (dbId.getText().toString()
							.equals("No copy of this book found in library.")) {
						Toast noBookToast = Toast.makeText(
								getApplicationContext(),
								"No copies of this book exist in the library.",
								Toast.LENGTH_SHORT);
						noBookToast.show();
					} else {
						new HttpBorrowAsyncTask().execute(WEBAPP_URL);
					}
				}
			}
			break;

		case R.id.return_btn:
			// Assign the current copy of the currently scanned book to the
			// library system.
			if (null == username) {
				Intent i = new Intent(this, UsernameEntryActivity.class);
				startActivityForResult(i, CHOOSE_USERNAME);
			} else {
				if (checkConnection()) {
					if (dbId.getText().toString()
							.equals("No copy of this book found in library.")) {
						Toast noBookToast = Toast.makeText(
								getApplicationContext(),
								"No copies of this book exist in the library.",
								Toast.LENGTH_SHORT);
						noBookToast.show();
					} else {
						new HttpReturnAsyncTask().execute(WEBAPP_URL);
					}
				}
			}
			break;

		case R.id.add_btn:
			// Add a new copy of the currently scanned book to the library.
			if (checkConnection()) {
				new HttpAddAsyncTask().execute(WEBAPP_URL);
				Toast addToast = Toast.makeText(getApplicationContext(),
						"Book \"" + titleText.getText().toString().substring(7)
								+ "\" added to library.", Toast.LENGTH_SHORT);
				addToast.show();
			}
			break;

		case R.id.delete_btn:
			// Delete the currently selected copy of the currently scanned book
			// from the library.
			if (checkConnection()) {
				if (dbId.getText().toString()
						.equals("No copy of this book found in library.")) {
					Toast noBookToast = Toast.makeText(getApplicationContext(),
							"No copies of this book exist in the library.",
							Toast.LENGTH_SHORT);
					noBookToast.show();
				} else {
					new HttpDeleteAsyncTask().execute(WEBAPP_URL);
					Toast deleteToast = Toast.makeText(
							getApplicationContext(),
							"Book \""
									+ titleText.getText().toString()
											.substring(7)
									+ "\" deleted from library.",
							Toast.LENGTH_SHORT);
					deleteToast.show();
				}
			}
			break;

		case R.id.saved_username:
			// Open the UsernameEntry Activity.
			Intent i = new Intent(this, UsernameEntryActivity.class);
			startActivityForResult(i, CHOOSE_USERNAME);
			break;

		case R.id.isConnected:
			// Recheck the connection to the server.
			if (checkConnection()) {
				Toast.makeText(MainActivity.this, "Connection reestablished!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(MainActivity.this, "Connection failed.",
						Toast.LENGTH_SHORT).show();
			}
			break;
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
		case SCAN_BARCODE:
			if (resultCode == RESULT_OK) {
				// Scan result is retrieved by making a call to
				// data.getStringExtra(ZBarConstants.SCAN_RESULT)
				// Barcode format is retrieved by making a call to
				// data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE)
				String scanContent = data
						.getStringExtra(ZBarConstants.SCAN_RESULT);
				if (scanContent != null) {
					getBook(scanContent);
					currentBookIsbn = scanContent;
					findViewById(R.id.bookDetailsList).setBackgroundColor(Color.WHITE);
				} else {
					currentBookIsbn = null;
					Toast.makeText(getApplicationContext(),
							"Not a valid book!", Toast.LENGTH_SHORT).show();
				}

			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Scan canceled!", Toast.LENGTH_SHORT)
						.show();
			}
			break;

		case CHOOSE_USERNAME:
			if (resultCode == RESULT_OK) {
				String username = data.getStringExtra("username");
				boolean isAdmin = data.getBooleanExtra("isAdmin", false);
				Editor edit = preferences.edit();

				if (null == username || "".equals(username)) {
					savedUsername.setText("Choose a username");
					username = null;
				} else {
					savedUsername.setText(username);
				}

				edit.putString("username", username);
				edit.putBoolean("admin", isAdmin);
				edit.apply();
				new GetCurrentlyBorrowed().execute(WEBAPP_URL);

				currentlyBorrowedTitle.setVisibility(View.VISIBLE);
				setButtonVisibility();
			} else if (resultCode == RESULT_CANCELED) {
			}
			break;

		case CHOOSE_COPY:
			if (resultCode == RESULT_OK) {
				String chosenId = data.getStringExtra("chosenId");
				if (null != chosenId && !("".equals(chosenId))) {
					String id = "" + Long.parseLong(chosenId);
					dbId.setText(Long.parseLong(chosenId) + "");
					dbIdText.setVisibility(View.VISIBLE);
					dbId.setVisibility(View.VISIBLE);

					currentBookId = id;
					setButtonVisibility();
				}
			} else if (resultCode == RESULT_CANCELED) {
			}
			break;
		}
	}

	/**
	 * @return hasCamera Method to confirm that the device has a camera with
	 *         which to scan barcodes.
	 */
	public boolean isCameraAvailable() {
		PackageManager pm = getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}

	/**
	 * @return isConnected Method to check if a network connection has been
	 *         established
	 */
	public boolean checkConnection() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			isConnected.setVisibility(View.GONE);
			return true;
		} else {
			isConnected.setVisibility(View.VISIBLE);
			isConnected
					.setText("Not connected to network!\nClick here to rescan.");
			return false;
		}
	}

	/**
	 * Method to save all currently displayed book data. Called when device is
	 * rotated, suspended etc.
	 */
	protected void onSaveInstanceState(Bundle savedBundle) {
		savedBundle.putString("title", "" + titleText.getText());
		savedBundle.putString("author", "" + authorText.getText());
		savedBundle.putString("description", "" + descriptionText.getText());
		savedBundle.putString("date", "" + dateText.getText());
		savedBundle.putString("ratings", "" + ratingCountText.getText());
		savedBundle.putParcelable("thumbPic", thumbImg);

		if (starLayout.getTag() != null) {
			savedBundle.putInt("stars",
					Integer.parseInt(starLayout.getTag().toString()));
		}

		savedBundle.putString("isbn", currentBookIsbn);

		savedBundle.putString("id", currentBookId);

		savedBundle.putBoolean("admin", preferences.getBoolean("admin", false));
	}

	private void getBook(String isbn) {
		// Uses my Google Books API key, and substitutes the ISBN pulled from
		// the barcode.
		String bookSearchString = GOOGLE_BOOKS_URL + isbn + "&key=" + API_KEY;
		new GetBookInfo().execute(bookSearchString);
		new GetBookId().execute(WEBAPP_URL, isbn);
	}

	private void setButtonVisibility() {
		if (currentBookIsbn != null) {
			if (preferences.getBoolean("admin", false)) {
				addBtn.setVisibility(View.VISIBLE);
				if (currentBookId != null && !(currentBookId.equals(""))) {
					deleteBtn.setVisibility(View.VISIBLE);
				}
			} else {
				addBtn.setVisibility(View.GONE);
				deleteBtn.setVisibility(View.GONE);
			}

			if (currentBookId != null && !(currentBookId.equals(""))) {
				borrowBtn.setVisibility(View.VISIBLE);
				returnBtn.setVisibility(View.VISIBLE);
			} else {
				borrowBtn.setVisibility(View.GONE);
				returnBtn.setVisibility(View.GONE);
			}
		} else {
			addBtn.setVisibility(View.GONE);
			deleteBtn.setVisibility(View.GONE);
			borrowBtn.setVisibility(View.GONE);
			returnBtn.setVisibility(View.GONE);
		}
	}

	/**
	 * @author Michael Lo Class to asynchronously download book data from Google
	 *         Books.
	 */
	private class GetBookInfo extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... bookdata) {
			StringBuilder bookBuilder = new StringBuilder();

			for (String bookSearchURL : bookdata) {
				HttpClient bookClient = new DefaultHttpClient();
				try {
					HttpResponse bookResponse = bookClient.execute(new HttpGet(
							bookSearchURL));
					StatusLine bookSearchStatus = bookResponse.getStatusLine();

					if (bookSearchStatus.getStatusCode() == 200) {
						BufferedReader bookReader = new BufferedReader(
								new InputStreamReader(bookResponse.getEntity()
										.getContent()));
						String lineIn;

						while ((lineIn = bookReader.readLine()) != null) {
							bookBuilder.append(lineIn);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return bookBuilder.toString();
		}

		protected void onPostExecute(String result) {
			if (result.equals("")) {
				checkConnection();
			} else {
				try {
					JSONObject resultObject = new JSONObject(result);
					JSONArray bookArray = resultObject.getJSONArray("items");
					JSONObject bookObject = bookArray.getJSONObject(0);
					JSONObject volumeObject = bookObject
							.getJSONObject("volumeInfo");

					try {
						titleText.setText("Title: "
								+ volumeObject.getString("title"));
					} catch (JSONException jse) {
						titleText.setText("");
						jse.printStackTrace();
					}

					StringBuilder authorBuild = new StringBuilder("");
					try {
						JSONArray authorArray = volumeObject
								.getJSONArray("authors");
						for (int a = 0; a < authorArray.length(); a++) {
							if (a > 0)
								authorBuild.append(", ");
							authorBuild.append(authorArray.getString(a));
						}
						authorText.setText("Author(s): "
								+ authorBuild.toString());
					} catch (JSONException jse) {
						authorText.setText("");
						jse.printStackTrace();
					}

					try {
						dateText.setText("Date of publication: "
								+ volumeObject.getString("publishedDate"));
					} catch (JSONException jse) {
						dateText.setText("");
						jse.printStackTrace();
					}

					try {
						descriptionText.setText("Description: "
								+ volumeObject.getString("description"));
					} catch (JSONException jse) {
						descriptionText.setText("");
						jse.printStackTrace();
					}

					try {
						double decNumStars = Double.parseDouble(volumeObject
								.getString("averageRating"));
						int numStars = (int) decNumStars;
						starLayout.setTag(numStars);
						starLayout.removeAllViews();

						for (int s = 0; s < numStars; s++) {
							starViews[s].setImageResource(R.drawable.star);
							starLayout.addView(starViews[s]);
						}
					} catch (JSONException jse) {
						starLayout.removeAllViews();
						jse.printStackTrace();
					}

					try {
						ratingCountText.setText(" - "
								+ volumeObject.getString("ratingsCount")
								+ " ratings");
					} catch (JSONException jse) {
						ratingCountText.setText("");
						jse.printStackTrace();
					}

					try {
						returnBtn.setTag(volumeObject.getString("infoLink"));
					} catch (JSONException jse) {
						jse.printStackTrace();
					}

					setButtonVisibility();

					try {
						JSONObject imageInfo = volumeObject
								.getJSONObject("imageLinks");
						new GetBookThumb().execute(imageInfo
								.getString("smallThumbnail"));
					} catch (JSONException jse) {
						thumbView.setImageBitmap(null);
						jse.printStackTrace();
					}

				} catch (Exception e) {
					// Default to empty values
					e.printStackTrace();
					titleText.setText("NOT FOUND");
					authorText.setText("");
					descriptionText.setText("");
					dateText.setText("");
					starLayout.removeAllViews();
					ratingCountText.setText("");
					thumbView.setImageBitmap(null);
					currentBookIsbn = null;
					currentBookId = null;

					setButtonVisibility();

					Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);

					if (v.hasVibrator()) {
						v.vibrate(250);
					}
				}
			}
		}
	}

	/**
	 * @author Michael Lo Class to asynchronously find books in the database
	 *         matching the ISBN of the scanned book. If more than one copy
	 *         exists in the database, the user will be prompted to select the
	 *         copy they wish to interact with.
	 */
	private class GetBookId extends AsyncTask<String, Void, String> {
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
			if (result.equals("")) {
				checkConnection();
			} else {
				ObjectMapper mapper = new ObjectMapper();
				List<Book> booksMatchingId = null;

				try {
					booksMatchingId = mapper.readValue(result,
							new TypeReference<List<Book>>() {
							});
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (null == booksMatchingId || booksMatchingId.isEmpty()) {
					dbId.setText("No copy of this book found in library.");
					dbId.setVisibility(View.VISIBLE);
					dbIdText.setVisibility(View.GONE);
					dbId.setText(null);
					currentBookId = null;
					setButtonVisibility();
				} else if (booksMatchingId.size() == 1) {
					String id = booksMatchingId.get(0).getId().toString();
					dbId.setText(id);
					dbIdText.setVisibility(View.VISIBLE);
					dbId.setVisibility(View.VISIBLE);
					currentBookId = id;
					setButtonVisibility();
				} else {
					Intent i = new Intent(MainActivity.this,
							ChooseCopyActivity.class);
					ArrayList<String> inPossessionOf = new ArrayList<String>();
					ArrayList<String> ids = new ArrayList<String>();
					for (Book b : booksMatchingId) {
						inPossessionOf.add(b.getInPossessionOf());
						ids.add(b.getId().toString());
					}
					i.putStringArrayListExtra("inPossessionOf", inPossessionOf);
					i.putStringArrayListExtra("ids", ids);

					startActivityForResult(i, CHOOSE_COPY);
				}
			}
		}
	}

	/**
	 * @author Michael Lo Class to asynchronously download book cover image from
	 *         Google Books.
	 */
	private class GetBookThumb extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... thumbdata) {
			try {
				URL thumbURL = new URL(thumbdata[0]);
				URLConnection thumbConn = thumbURL.openConnection();
				thumbConn.connect();
				InputStream thumbIn = thumbConn.getInputStream();
				BufferedInputStream thumbBuff = new BufferedInputStream(thumbIn);
				thumbImg = BitmapFactory.decodeStream(thumbBuff);

				thumbBuff.close();
				thumbIn.close();
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
			return "completed";
		}

		protected void onPostExecute(String result) {
			thumbView.setImageBitmap(thumbImg);
		}
	}

	/**
	 * @author Michael Lo Class to asynchronously add a copy of the scanned book
	 *         to the library.
	 */
	private class HttpAddAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... data) {

			Book book = new Book(currentBookIsbn, titleText.getText()
					.toString().substring(6), LIBRARY_USERNAME);

			String returnValue = "";

			returnValue = PostMethods.POSTAdd(data[0], book);

			return returnValue;
		}

		@Override
		protected void onPostExecute(String result) {
			dbId.setText(result);
			dbIdText.setVisibility(View.VISIBLE);
			dbId.setVisibility(View.VISIBLE);
			currentBookId = result;
			setButtonVisibility();
			new GetCurrentlyBorrowed().execute(WEBAPP_URL);
		}
	}

	/**
	 * @author Michael Lo Class to asynchronously delete the currently selected
	 *         copy of the scanned book from the library.
	 */
	private class HttpDeleteAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... data) {
			Book book = new Book();

			try {
				book = new Book(Long.parseLong(dbId.getText().toString()),
						currentBookIsbn, titleText.getText().toString()
								.substring(6), preferences.getString(
								"username", LIBRARY_USERNAME));
			} catch (Exception e) {
				e.printStackTrace();
			}

			String returnValue = "";

			returnValue = PostMethods.POSTDelete(data[0], book);

			return returnValue;
		}

		@Override
		protected void onPostExecute(String result) {
			dbIdText.setVisibility(View.GONE);
			dbId.setVisibility(View.GONE);
			dbId.setText(null);
			currentBookId = null;
			setButtonVisibility();
			new GetBookId().execute(WEBAPP_URL, currentBookIsbn);
			new GetCurrentlyBorrowed().execute(WEBAPP_URL);
		}
	}

	/**
	 * @author Michael Lo Class to asynchronously assign the currently selected
	 *         copy of the scanned book to the chosen username.
	 */
	private class HttpBorrowAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... data) {

			Book book = new Book(Long.parseLong(dbId.getText().toString()),
					currentBookIsbn, titleText.getText().toString()
							.substring(6), preferences.getString("username",
							LIBRARY_USERNAME));

			String returnValue = "";

			returnValue = PostMethods.POSTBorrow(data[0], book);

			return returnValue;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d("MyDEBUG", "In OnPostExecute of Borrow, result is " + result);
			if (result.equals("\"ALREADYBORROWED\"")) {
				Log.d("MyDEBUG",
						"in alreadyBorrowed, dialog should be created.");
				new AlertDialog.Builder(MainActivity.this)
						.setMessage(
								"This book has been borrowed by another user.")
						.setPositiveButton(R.string.confirm,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
									}
								}).show();
			} else {
				new GetCurrentlyBorrowed().execute(WEBAPP_URL);
				Toast toast = Toast.makeText(
						getApplicationContext(),
						"Book \""
								+ titleText.getText().toString().substring(7)
								+ "\" borrowed by "
								+ preferences.getString("username",
										"No username") + ".",
						Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

	/**
	 * @author Michael Lo Class to asynchronously assign the currently selected
	 *         copy of the scanned book to the library.
	 */
	private class HttpReturnAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... data) {

			Book book = new Book(Long.parseLong(dbId.getText().toString()),
					currentBookIsbn, titleText.getText().toString()
							.substring(6), preferences.getString("username",
							LIBRARY_USERNAME));

			String returnValue = "";

			returnValue = PostMethods.POSTReturn(data[0], book);

			return returnValue;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d("MyDEBUG", "In OnPostExecute of Return, result is " + result);
			if (result.equals("\"ALREADYBORROWED\"")) {
				Log.d("MyDEBUG",
						"in alreadyBorrowed, dialog should be created.");
				new AlertDialog.Builder(MainActivity.this)
						.setMessage(
								"This book has been borrowed by another user.")
						.setPositiveButton(R.string.confirm,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
									}
								}).show();
			} else {
				new GetCurrentlyBorrowed().execute(WEBAPP_URL);
				Toast toast = Toast.makeText(
						getApplicationContext(),
						"Book \""
								+ titleText.getText().toString().substring(7)
								+ "\" returned by "
								+ preferences.getString("username",
										"No username") + ".",
						Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

	/**
	 * @author Michael Lo Class to asynchronously find books in the database
	 *         matching the ISBN of the scanned book. If more than one copy
	 *         exists in the database, the user will be prompted to select the
	 *         copy they wish to interact with.
	 */
	private class GetCurrentlyBorrowed extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... data) {
			String url = data[0];
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
			if (result.equals("")) {
				checkConnection();
			} else {
				ObjectMapper mapper = new ObjectMapper();
				List<Book> borrowedByUser = null;

				try {
					borrowedByUser = mapper.readValue(result,
							new TypeReference<List<Book>>() {
							});
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

					for (int t = 0; t < currentlyBorrowed.length; t++) {
						TextView text = currentlyBorrowed[t];
						text = new TextView(MainActivity.this);
						text.setText(borrowedByUser.get(t).toString());
						currentlyBorrowedList.addView(text);
					}
				}
			}
		}
	}
}
