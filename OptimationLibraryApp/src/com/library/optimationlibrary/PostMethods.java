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
 * 
 * @author Michael Lo
 * Class containing the HTTP POST methods used to send and receive data with the server.
 *
 */
public class PostMethods {

	private static final String _add = "ADD";
	private static final String _delete = "DELETE";
	private static final String _borrow = "BORROW";
	private static final String _return = "RETURN";
	private static final String _getBorrowed = "GETBORROWED";
	private static final String _getBookFromIsbn = "GETBOOKFROMISBN";
	private static final String _isAllowedName = "ISALLOWEDNAME";

	static InputStream inputStream = null;
	static String result = "";
	static HttpClient httpClient = null;
	static HttpPost httpPost = null;
	static JSONObject jsonObject = null;
	static String json = "";
	static StringEntity stringEntity = null;
	static HttpResponse httpResponse = null;

	PostMethods() {
	}

	static void commonActions(String url) {
		httpClient = new DefaultHttpClient();
		httpPost = new HttpPost(url);
		jsonObject = new JSONObject();
	}

	static String POSTAdd(String url, Book book) {		
		try {
			commonActions(url);
			jsonObject.accumulate("isbn", book.getIsbn());
			jsonObject.accumulate("title", book.getTitle());
			jsonObject.accumulate("inPossessionOf", book.getInPossessionOf());

			json = jsonObject.toString();

			json = _add + json;

			stringEntity = new StringEntity(json);

			httpPost.setEntity(stringEntity);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			httpResponse = httpClient.execute(httpPost);

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

	static String POSTDelete(String url, Book book) {
		try {
			commonActions(url);

			json = _delete + book.getId();

			stringEntity = new StringEntity(json);

			httpPost.setEntity(stringEntity);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			httpResponse = httpClient.execute(httpPost);

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

	static String POSTBorrow(String url, Book book) {
		try {
			commonActions(url);

			jsonObject.accumulate("id", book.getId());
			jsonObject.accumulate("isbn", book.getIsbn());
			jsonObject.accumulate("title", book.getTitle());
			jsonObject.accumulate("inPossessionOf", book.getInPossessionOf());

			json = jsonObject.toString();

			json = _borrow + json;

			stringEntity = new StringEntity(json);

			httpPost.setEntity(stringEntity);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse httpResponse = httpClient.execute(httpPost);

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

	static String POSTReturn(String url, Book book) {
		try {
			commonActions(url);

			json = _return + book.getId();

			stringEntity = new StringEntity(json);

			httpPost.setEntity(stringEntity);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse httpResponse = httpClient.execute(httpPost);

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

	static String POSTGetBorrowed(String url, Book book) {
		try {
			commonActions(url);

			json = _getBorrowed + book.getInPossessionOf();

			stringEntity = new StringEntity(json);

			httpPost.setEntity(stringEntity);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse httpResponse = httpClient.execute(httpPost);

			inputStream = httpResponse.getEntity().getContent();

			if(inputStream != null) {
				result = convertInputStreamToString(inputStream);
			} else {
				result = "Did not work!";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	static String POSTIsAllowedName(String url, String name) {
		try {
			commonActions(url);
			
			json = _isAllowedName + name;

			stringEntity = new StringEntity(json);

			httpPost.setEntity(stringEntity);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse httpResponse = httpClient.execute(httpPost);

			inputStream = httpResponse.getEntity().getContent();
			
			if(inputStream != null) {
				result = convertInputStreamToString(inputStream);
			} else {
				result = "Did not work!";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	static String POSTGetBookFromIsbn(String url, Book book) {
		try {
			commonActions(url);

			json = _getBookFromIsbn + book.getIsbn();

			stringEntity = new StringEntity(json);

			httpPost.setEntity(stringEntity);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			httpResponse = httpClient.execute(httpPost);

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
