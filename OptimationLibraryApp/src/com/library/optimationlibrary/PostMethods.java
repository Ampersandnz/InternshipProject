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

/**
 * @author Michael Lo
 * Helper class containing the HTTP POST methods used to send and receive data with the server.
 * Minimises size and complexity of MainActivity and helps with project maintainability.
 */
public class PostMethods {

	private static final String ADD = "ADD";
	private static final String DELETE = "DELETE";
	private static final String BORROW = "BORROW";
	private static final String RETURN = "RETURN";
	private static final String GETBORROWEDBYUSER = "GETBORROWED";
	private static final String GETBOOKBYISBN = "GETBOOKFROMISBN";
	private static final String CHECKNAME = "ISALLOWEDNAME";

	PostMethods() {
	}

	/**
	 * @param url
	 * @param book
	 * @return response
	 * Add a new copy of the scanned book to the library.
	 */
	static String POSTAdd(String url, Book book) {		
		String result = "";
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			JSONObject jsonObject = new JSONObject();
			
			jsonObject.accumulate("isbn", book.getIsbn());
			jsonObject.accumulate("title", book.getTitle());
			jsonObject.accumulate("inPossessionOf", book.getInPossessionOf());

			String json = jsonObject.toString();

			json = ADD + json;

			StringEntity stringEntity = new StringEntity(json);

			httpPost.setEntity(stringEntity);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse httpResponse = httpClient.execute(httpPost);

			InputStream inputStream = httpResponse.getEntity().getContent();
			
			if(inputStream != null) {
				result = convertInputStreamToString(inputStream);
			} else {
			}

		} catch (Exception e) {
		}

		return result;
	}

	/**
	 * @param url
	 * @param book
	 * @return response
	 * Delete the selected copy of the scanned book from the system.
	 */
	static String POSTDelete(String url, Book book) {
		String result = "";
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			
			String json = DELETE + book.getId();

			StringEntity stringEntity = new StringEntity(json);

			httpPost.setEntity(stringEntity);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse httpResponse = httpClient.execute(httpPost);

			InputStream inputStream = httpResponse.getEntity().getContent();

			if(inputStream != null) {
				result = convertInputStreamToString(inputStream);
			} else {
			}

		} catch (Exception e) {
		}

		return result;
	}

	/**
	 * @param url
	 * @param book
	 * @return response
	 * Assign the selected copy of the scanned book to the chosen username.
	 */
	static String POSTBorrow(String url, Book book) {
		String result = "";
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			JSONObject jsonObject = new JSONObject();
			
			jsonObject.accumulate("id", book.getId());
			jsonObject.accumulate("isbn", book.getIsbn());
			jsonObject.accumulate("title", book.getTitle());
			jsonObject.accumulate("inPossessionOf", book.getInPossessionOf());

			String json = jsonObject.toString();

			json = BORROW + json;

			StringEntity stringEntity = new StringEntity(json);

			httpPost.setEntity(stringEntity);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse httpResponse = httpClient.execute(httpPost);

			InputStream inputStream = httpResponse.getEntity().getContent();

			if(inputStream != null) {
				result = convertInputStreamToString(inputStream);
			} else {
			}

		} catch (Exception e) {
		}

		return result;
	}

	/**
	 * @param url
	 * @param book
	 * @return response
	 * Assign the selected copy of the scanned book to the library.
	 */
	static String POSTReturn(String url, Book book) {
		String result = "";
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			
			String json = RETURN + book.getId();

			StringEntity stringEntity = new StringEntity(json);

			httpPost.setEntity(stringEntity);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse httpResponse = httpClient.execute(httpPost);

			InputStream inputStream = httpResponse.getEntity().getContent();

			if(inputStream != null) {
				result = convertInputStreamToString(inputStream);
			} else {
			}

		} catch (Exception e) {
		}

		return result;
	}

	/**
	 * @param url
	 * @param book
	 * @return response
	 * Request from the server the list of books that are currently listed as in the possession of the chosen username.
	 */
	static String POSTGetBorrowed(String url, Book book) {
		String result = "";
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			
			String json = GETBORROWEDBYUSER + book.getInPossessionOf();

			StringEntity stringEntity = new StringEntity(json);

			httpPost.setEntity(stringEntity);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse httpResponse = httpClient.execute(httpPost);

			InputStream inputStream = httpResponse.getEntity().getContent();

			if(inputStream != null) {
				result = convertInputStreamToString(inputStream);
			} else {
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	/**
	 * @param url
	 * @param name
	 * @return response
	 * Check whether or not the chosen username is on the server's list of authenticated user accounts.
	 */
	static String POSTIsAllowedName(String url, String name) {
		String result = "FALSE";
		
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			
			String json = CHECKNAME + name;

			StringEntity stringEntity = new StringEntity(json);

			httpPost.setEntity(stringEntity);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse httpResponse = httpClient.execute(httpPost);

			InputStream inputStream = httpResponse.getEntity().getContent();
			
			if(inputStream != null) {
				result = convertInputStreamToString(inputStream);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * @param url
	 * @param book
	 * @return response
	 * Requests from the server the list of copies of the scanned book.
	 */
	static String POSTGetBookFromIsbn(String url, Book book) {
		String result = "";
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			
			String json = GETBOOKBYISBN + book.getIsbn();

			StringEntity stringEntity = new StringEntity(json);

			httpPost.setEntity(stringEntity);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse httpResponse = httpClient.execute(httpPost);

			InputStream inputStream = httpResponse.getEntity().getContent();

			if(inputStream != null) {
				result = convertInputStreamToString(inputStream);
			} else {
			}

		} catch (Exception e) {
		}

		return result;
	}

	/**
	 * @param inputStream
	 * @return response
	 * @throws IOException
	 * Converts the BufferedInputStream received from the server to a String.
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

	/**
	 * @param url
	 * @param name
	 * @return response
	 * Check whether or not the chosen admin account's password has been correctly entered.
	 */
	static boolean POSTCheckPassword(String url, String name, String password) {
		boolean result = false;
		/*
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			
			String json = CHECKNAME + name;

			StringEntity stringEntity = new StringEntity(json);

			httpPost.setEntity(stringEntity);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse httpResponse = httpClient.execute(httpPost);

			InputStream inputStream = httpResponse.getEntity().getContent();
			
			if(inputStream != null) {
				result = convertInputStreamToString(inputStream);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		return result;
	}
}
