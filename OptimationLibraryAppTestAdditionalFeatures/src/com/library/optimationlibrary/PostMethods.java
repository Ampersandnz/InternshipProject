package com.library.optimationlibrary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.mlo.book.Book;

public class PostMethods {

	private static final String _add = "ADD";
	private static final String _delete = "DELETE";
	private static final String _borrow = "BORROW";
	private static final String _return = "RETURN";
	private static final String _getBorrowed = "GETBORROWED";
	private static final String _getBookFromIsbn = "GETBOOKFROMISBN";

	public PostMethods() {

	}

	public static String POSTAdd(String url, Book book) {
		InputStream inputStream = null;
		String result = "";

		try {
			HttpClient httpclient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(url);

			JSONObject jsonObject = new JSONObject();

			String json = "";

			jsonObject.accumulate("isbn", book.getIsbn());
			jsonObject.accumulate("title", book.getTitle());
			jsonObject.accumulate("inPossessionOf", book.getInPossessionOf());

			json = jsonObject.toString();

			json = _add + json;

			StringEntity se = new StringEntity(json);

			httpPost.setEntity(se);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse httpResponse = httpclient.execute(httpPost);

			inputStream = httpResponse.getEntity().getContent();

			if(inputStream != null) {
				result = convertInputStreamToString(inputStream);
			} else {
				result = "Did not work!";
			}
			
		} catch (Exception e) {
		}

		return result;
	}

	public static String POSTDelete(String url, Book book) {
		InputStream inputStream = null;
		String result = "";

		try {
			HttpClient httpclient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(url);

			String json = "";

			json = _delete + book.getId();

			StringEntity se = new StringEntity(json);

			httpPost.setEntity(se);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse httpResponse = httpclient.execute(httpPost);

			inputStream = httpResponse.getEntity().getContent();

			if(inputStream != null) {
				result = convertInputStreamToString(inputStream);
			} else {
				result = "Did not work!";
			}

		} catch (Exception e) {
		}

		return result;
	}

	public static String POSTBorrow(String url, Book book) {
		InputStream inputStream = null;
		String result = "";

		try {
			HttpClient httpclient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(url);

			JSONObject jsonObject = new JSONObject();
			
			String json = "";

			jsonObject.accumulate("isbn", book.getIsbn());
			jsonObject.accumulate("title", book.getTitle());
			jsonObject.accumulate("inPossessionOf", book.getInPossessionOf());

			json = jsonObject.toString();
			
			//TODO: Send "BORROW" + book ID + username
			json = _borrow + json;
			
			StringEntity se = new StringEntity(json);

			httpPost.setEntity(se);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse httpResponse = httpclient.execute(httpPost);

			inputStream = httpResponse.getEntity().getContent();

			if(inputStream != null) {
				result = convertInputStreamToString(inputStream);
			} else {
				result = "Did not work!";
			}

		} catch (Exception e) {
		}

		return result;
	}

	public static String POSTReturn(String url, Book book) {
		InputStream inputStream = null;
		String result = "";

		try {
			HttpClient httpclient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(url);
			
			String json = "";
			
			json = _return + book.getId();

			StringEntity se = new StringEntity(json);

			httpPost.setEntity(se);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse httpResponse = httpclient.execute(httpPost);

			inputStream = httpResponse.getEntity().getContent();

			if(inputStream != null) {
				result = convertInputStreamToString(inputStream);
			} else {
				result = "Did not work!";
			}

		} catch (Exception e) {
		}

		return result;
	}

	public static String POSTGetBorrowed(String url, Book book) {
		InputStream inputStream = null;
		String result = "";

		try {
			HttpClient httpclient = new DefaultHttpClient();
			
			HttpPost httpPost = new HttpPost(url);
			
			String json = "";
			
			//TODO: Send "GETBORRWED" + username, get list of books borrowed by username
			json = _getBorrowed + json;

			StringEntity se = new StringEntity(json);

			httpPost.setEntity(se);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse httpResponse = httpclient.execute(httpPost);

			inputStream = httpResponse.getEntity().getContent();

			if(inputStream != null) {
				result = convertInputStreamToString(inputStream);
			} else {
				result = "Did not work!";
			}

		} catch (Exception e) {
		}

		return result;
	}

	public static String POSTGetBookFromIsbn(String url, Book book) {
		InputStream inputStream = null;
		String result = "";

		try {
			HttpClient httpclient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(url);

			String json = "";

			//TODO: Send "GETBOOKFROMISBN" + isbn, get [list of] book[s] matching that isbn. If one, display its details. If more than one, prompt user to select the correct one.
			json = _getBookFromIsbn + book.getIsbn();

			StringEntity se = new StringEntity(json);

			httpPost.setEntity(se);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse httpResponse = httpclient.execute(httpPost);

			inputStream = httpResponse.getEntity().getContent();

			if(inputStream != null) {
				result = convertInputStreamToString(inputStream);
			} else {
				result = "Did not work!";
			}

		} catch (Exception e) {
		}

		return result;
	}

	/**
	 * @param inputStream
	 * @return result
	 * @throws IOException
	 * 
	 * Temporary method to use with Book class and webserver until I have sorted all functionality properly.
	 */
	private static String convertInputStreamToString(InputStream inputStream) throws IOException {
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while((line = bufferedReader.readLine()) != null) {
			result += line;
		}
		inputStream.close();
		return result;

	}   
}
