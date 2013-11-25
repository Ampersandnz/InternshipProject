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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

	private TextView authorText;
	private TextView titleText;
	private TextView descriptionText;
	private TextView dateText;
	private TextView ratingCountText;

	private LinearLayout starLayout;

	private ImageView[] starViews;

	private ImageView thumbView;

	private Bitmap thumbImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupButtons();
		setupTextViews();
		setupStars();

		if (savedInstanceState != null) {
			retrieveSavedState(savedInstanceState);
		}
	}

	public void setupButtons() {
		scanBtn = (Button)findViewById(R.id.scan_button);
		borrowBtn = (Button)findViewById(R.id.borrow_btn);
		returnBtn = (Button)findViewById(R.id.return_btn);

		borrowBtn.setVisibility(View.GONE);
		returnBtn.setVisibility(View.GONE);

		scanBtn.setOnClickListener(this);
		borrowBtn.setOnClickListener(this);
		returnBtn.setOnClickListener(this);
	}

	public void setupTextViews() {
		authorText = (TextView)findViewById(R.id.book_author);
		titleText = (TextView)findViewById(R.id.book_title);
		descriptionText = (TextView)findViewById(R.id.book_description);
		dateText = (TextView)findViewById(R.id.book_date);
		starLayout = (LinearLayout)findViewById(R.id.star_layout);
		ratingCountText = (TextView)findViewById(R.id.book_rating_count);
		thumbView = (ImageView)findViewById(R.id.thumb);
	}

	public void setupStars() {
		starViews=new ImageView[5];
		for(int s=0; s<starViews.length; s++) {
			starViews[s]=new ImageView(this);
		}
	}

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

	public void onClick(View v){
		if (v.getId()==R.id.scan_button) {
			//TODO DISABLED THE ACTUAL SCAN FOR TESTING PURPOSES
			//TODO IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			//TODO scanIntegrator.initiateScan();
			String bookSearchString = "https://www.googleapis.com/books/v1/volumes?q=isbn:9780756404079&key=AIzaSyBiYyZhPC3K2eTUYTHjmo3LN0-F7CQKfo0";
			new GetBookInfo().execute(bookSearchString);

		} else if (v.getId()==R.id.return_btn) {
			//TODO: Talk to DB, return book.
				//Set inPossessionOf to "Library", set inLibrary=true, [update local list of borrowed books]
		} else if (v.getId()==R.id.borrow_btn) {
			//TODO: Talk to DB, borrow book.
				//Set inPossessionOf to username, set inLibrary=false, [update local list of borrowed books]
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanningResult != null) {
			String scanContent = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();
			Log.v("SCAN", "content: " + scanContent + " - format: " + scanFormat);
			if (scanContent!=null && scanFormat!=null && scanFormat.equalsIgnoreCase("EAN_13")) {
				borrowBtn.setTag(scanContent);
				String bookSearchString = "https://www.googleapis.com/books/v1/volumes?"+"q=isbn:" + scanContent + "&key=AIzaSyBiYyZhPC3K2eTUYTHjmo3LN0-F7CQKfo0";
				new GetBookInfo().execute(bookSearchString);
			} else {
				Toast toast = Toast.makeText(getApplicationContext(), "Not a valid book!", Toast.LENGTH_SHORT);
				toast.show();
			}
		} else {
			Toast toast = Toast.makeText(getApplicationContext(), 
					"No book scan data received!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

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
					titleText.setText("TITLE: "+volumeObject.getString("title")); 
				} catch (JSONException jse) { 
					titleText.setText("");
					jse.printStackTrace(); 
				}

				StringBuilder authorBuild = new StringBuilder("");
				try {
					JSONArray authorArray = volumeObject.getJSONArray("authors");
					for(int a=0; a<authorArray.length(); a++){
						if(a>0) authorBuild.append(", ");
						authorBuild.append(authorArray.getString(a));
					}
					authorText.setText("Author(s): "+authorBuild.toString());
				} catch (JSONException jse) { 
					authorText.setText("");
					jse.printStackTrace(); 
				}

				try { dateText.setText("Date of publication: "+volumeObject.getString("publishedDate")); 
				} catch (JSONException jse) { 
					dateText.setText("");
					jse.printStackTrace(); 
				}

				try { 
					descriptionText.setText("Description: "+volumeObject.getString("description")); 
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
