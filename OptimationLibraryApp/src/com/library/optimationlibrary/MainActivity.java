package com.library.optimationlibrary;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * 
 * @author Michael Lo
 * Using code from http://mobile.tutsplus.com/tutorials/android/android-sdk-create-a-book-scanning-app-part-3/
 * 
 */
public class MainActivity extends Activity implements OnClickListener {

	private Button scanBtn;
	private Button borrowBtn;
	private Button returnBtn;
	private Button savedUsername;

	private TextView authorText;
	private TextView titleText;
	private TextView descriptionText;
	private TextView dateText;
	private TextView ratingCountText;

	private static SharedPreferences preferences;

	private LinearLayout starLayout;

	private ImageView[] starViews;

	private ImageView thumbView;

	private Bitmap thumbImg;
	
	private static final String LIBRARY_USERNAME = "_library";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupButtons();
		setupTextViews();
		setupStars();
		setupImageViews();

		if (savedInstanceState != null) {
			retrieveSavedState(savedInstanceState);
		}
	}

	/**
	 * Method to map all the Button objects to views in the layout, set their listener to the MainActivity and print the text on the set username button.
	 */
	public void setupButtons() {
		scanBtn = (Button)findViewById(R.id.scan_button);
		borrowBtn = (Button)findViewById(R.id.borrow_btn);
		returnBtn = (Button)findViewById(R.id.return_btn);
		savedUsername = (Button)findViewById(R.id.saved_username);

		borrowBtn.setVisibility(View.GONE);
		returnBtn.setVisibility(View.GONE);

		scanBtn.setOnClickListener(this);
		borrowBtn.setOnClickListener(this);
		returnBtn.setOnClickListener(this);
		savedUsername.setOnClickListener(this);

		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String username = preferences.getString("username", "Choose a username");
		savedUsername.setText(username);
	}

	/**
	 * Method to map all the TextView objects to views in the layout.
	 */
	public void setupTextViews() {
		authorText = (TextView)findViewById(R.id.book_author);
		titleText = (TextView)findViewById(R.id.book_title);
		descriptionText = (TextView)findViewById(R.id.book_description);
		dateText = (TextView)findViewById(R.id.book_date);
		starLayout = (LinearLayout)findViewById(R.id.star_layout);
		ratingCountText = (TextView)findViewById(R.id.book_rating_count);
	}

	/**
	 * Method to set up the five ImageViews for the rating stars.
	 */
	public void setupStars() {
		starViews=new ImageView[5];
		for(int s=0; s < starViews.length; s++) {
			starViews[s]=new ImageView(this);
		}
	}

	/**
	 * Method to map the ImageView object to the book cover thumbnail view in the layout.
	 */
	public void setupImageViews() {
		thumbView = (ImageView)findViewById(R.id.thumb);
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
	}

	/**
	 * MainActivity implements OnClickListener, and OnClick() is called when one of its associated notifiers is clicked. This method then responds appropriately.
	 */
	public void onClick(View v) {
		if (v.getId()==R.id.scan_button) {
			//TODO DISABLED THE ACTUAL SCAN FOR TESTING PURPOSES
			//TODO IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			//TODO scanIntegrator.initiateScan();
			String bookSearchString = "https://www.googleapis.com/books/v1/volumes?q=isbn:9780756404079&key=AIzaSyBiYyZhPC3K2eTUYTHjmo3LN0-F7CQKfo0";
			new GetBookInfo().execute(bookSearchString);

		} else if (v.getId()==R.id.return_btn) {
			//TODO: Talk to DB, return book.
				//Set inPossessionOf to LIBRARY_USERNAME, [update local list of borrowed books]
			String username = preferences.getString("username", null);
			if (null == username) {
				Intent i = new Intent(this, UsernameEntryActivity.class);
				startActivityForResult(i, 1);
			} else {
				Toast toast = Toast.makeText(getApplicationContext(), "Book \"" + titleText.getText().toString().substring(7) + "\" returned by " + username, Toast.LENGTH_SHORT);
				toast.show();
			}
		} else if (v.getId()==R.id.borrow_btn) {
			//TODO: Talk to DB, borrow book.
				//Set inPossessionOf to username, [update local list of borrowed books]
			String username = preferences.getString("username", null);
			if (null == username) {
				Intent i = new Intent(this, UsernameEntryActivity.class);
				startActivityForResult(i, 1);
			} else {
				Toast toast = Toast.makeText(getApplicationContext(), "Book \"" + titleText.getText().toString().substring(7) + "\" borrowed by " + username, Toast.LENGTH_SHORT);
				toast.show();
			}
		} else if (v.getId()==R.id.saved_username) {
			Intent i = new Intent(this, UsernameEntryActivity.class);
			startActivityForResult(i, 1);
		}
	}

	/**
	 * Method to retrieve data and take actions as appropriate when a method called with StartActivityForResult() finishes.
	 * Will either retrieve the data from a scanned barcode and use it to pull book information from the Google Books API, or get the chosen username and save it in StoredPreferences.
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
			if (scanningResult != null) {
				String scanContent = scanningResult.getContents();
				String scanFormat = scanningResult.getFormatName();
				Log.v("SCAN", "content: " + scanContent + " - format: " + scanFormat);
				if (scanContent!=null && scanFormat!=null && scanFormat.equalsIgnoreCase("EAN_13")) {
					borrowBtn.setTag(scanContent);
					// Uses my Google Books API key, and substitutes the ISBN pulled from the barcode.
					String bookSearchString = "https://www.googleapis.com/books/v1/volumes?"+"q=isbn:" + scanContent + "&key=AIzaSyBiYyZhPC3K2eTUYTHjmo3LN0-F7CQKfo0";
					new GetBookInfo().execute(bookSearchString);
				} else {
					Toast toast = Toast.makeText(getApplicationContext(), "Not a valid book!", Toast.LENGTH_SHORT);
					toast.show();
				}
			} else {
				Toast toast = Toast.makeText(getApplicationContext(), "No book scan data received!", Toast.LENGTH_SHORT);
				toast.show();
			}

		} else if (requestCode == 1) {
			if(resultCode == RESULT_OK){
				String username=data.getStringExtra("username");
				Editor edit = preferences.edit();
				edit.putString("username", username);
				edit.apply();
				savedUsername.setText(username);
			} else if (resultCode == RESULT_CANCELED) {
				
			}
		}
	}

	/**
	 * 
	 * @author Michael Lo
	 * Class to asynchronously download all book data from Google Books.
	 * 
	 */
	private class GetBookInfo extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... bookURLs) {
			StringBuilder bookBuilder = new StringBuilder();

			for (String bookSearchURL : bookURLs) {
				HttpClient bookClient = new DefaultHttpClient();
				try {
					HttpGet bookGet = new HttpGet(bookSearchURL);
					HttpResponse bookResponse = bookClient.execute(bookGet);
					StatusLine bookSearchStatus = bookResponse.getStatusLine();

					if (bookSearchStatus.getStatusCode()==200) {
						HttpEntity bookEntity = bookResponse.getEntity();
						InputStream bookContent = bookEntity.getContent();
						InputStreamReader bookInput = new InputStreamReader(bookContent);
						BufferedReader bookReader = new BufferedReader(bookInput);
						String lineIn;

						while ((lineIn=bookReader.readLine())!=null) {
							bookBuilder.append(lineIn);
						}
					}
				} catch(Exception e) { 
					e.printStackTrace(); 
				}
			}
			Log.d("JSON", bookBuilder.toString());
			return bookBuilder.toString();
		}

		protected void onPostExecute(String result) {
			try {
				borrowBtn.setVisibility(View.VISIBLE);
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
					returnBtn.setVisibility(View.VISIBLE);
				} catch (JSONException jse) { 
					returnBtn.setVisibility(View.GONE);
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
	 * 
	 * @author Michael Lo
	 * Class to asynchronously download book cover image from Google Books.
	 * 
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
	}
}
