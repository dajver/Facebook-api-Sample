package com.example.facebook_api_sample;


import adapter.FriendsAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {
	
	// Your Facebook APP ID
	private static String APP_ID = "343214545779491"; // Replace with your App ID
	private static final String FIRST = "name";
	private static final String IMAGE = "image";
	private static final String LAST = "id";
	static ArrayList<HashMap<String, Object>> myBooks;
	// Instance of Facebook Class
	private Facebook facebook;
	private AsyncFacebookRunner mAsyncRunner;
	private SharedPreferences mPrefs;
	// Buttons
	String FILENAME = "AndroidSSO_data";
	int i = 0;
	ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		listView = (ListView) findViewById(R.id.listView1);
		// check user logged or not
		facebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(facebook);
		
		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);
		if (access_token != null) {
			facebook.setAccessToken(access_token);
			// btnFbLogin.setVisibility(View.INVISIBLE);
			Log.d("FB Sessions", "" + facebook.isSessionValid());
		}
		if (expires != 0) {
			// btnFbLogin.setVisibility(View.INVISIBLE);
			facebook.setAccessExpires(expires);
			getProfileInformation();
		}
		if (expires == 0) {
			loginToFacebook();
		}
		myBooks = new ArrayList<HashMap<String, Object>>();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {

				HashMap<String, Object> item = myBooks.get(position);
				String key = item.get(LAST).toString();
				
					Intent intent = new Intent(MainActivity.this, DetalsActivity.class);
					intent.putExtra(DetalsActivity.JSON, key);
					startActivity(intent);
				
			}
		});
	}

		/** Get Profile information by making request to Facebook Graph API */
		@SuppressWarnings("deprecation")
		public void getProfileInformation() {

			Bundle params = new Bundle();
			params.putString("fields", "name, picture");
			mAsyncRunner.request("me/friends", params, new RequestListener() {

				@Override
				public void onComplete(String response, Object state) {

					String json = response;
					try {
						JSONObject profile = new JSONObject(json);
						JSONArray data = profile.getJSONArray("data");
						for (i = 0; i < data.length(); i++) {
							// getting name of the user
							final String name = data.getJSONObject(i).getString("name");
							final String id = data.getJSONObject(i).getString("id");
							JSONObject picture = data.getJSONObject(i).getJSONObject("picture");
							JSONObject picdata = picture.getJSONObject("data");
							final String url = picdata.getString("url");
							runOnUiThread(new Runnable() {

								@Override
								public void run() {

									HashMap<String, Object> hm;
									hm = new HashMap<String, Object>();
									hm.put(FIRST, name);
									hm.put(LAST, id);
									hm.put(IMAGE, url);
									myBooks.add(hm);
									String[] urls = new String[myBooks.size()];
									String[] names = new String[myBooks.size()];
									String[] indexcode = new String[myBooks.size()];
									for (int i = 0; i < myBooks.size(); i++) {
										urls[i] = (String) myBooks.get(i).get(IMAGE);
										names[i] = (String) myBooks.get(i).get(FIRST);
										indexcode[i] = (String) myBooks.get(i).get(LAST);
									}
									listView.setAdapter(new FriendsAdapter(MainActivity.this, indexcode, names, urls));
								}
							});
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onFacebookError(FacebookError e, Object state) {

				}

				@Override
				public void onFileNotFoundException(FileNotFoundException e, Object state) {

				}

				@Override
				public void onIOException(IOException e, Object state) {

				}

				@Override
				public void onMalformedURLException(MalformedURLException e, Object state) {

				}
			});
		}

		/** Function to login into facebook */
		@SuppressWarnings("deprecation")
		public void loginToFacebook() {

			if (!facebook.isSessionValid()) {
				facebook.authorize(this, new String[] { "email", "publish_stream" },
						new DialogListener() {

							@Override
							public void onCancel() {

								// Function to handle cancel event
							}

							@Override
							public void onComplete(Bundle values) {

								// Function to handle complete event
								// Edit Preferences and update facebook acess_token
								SharedPreferences.Editor editor = mPrefs.edit();
								editor.putString("access_token", facebook.getAccessToken());
								editor.putLong("access_expires", facebook.getAccessExpires());
								editor.commit();
								// Making Login button invisible
								// btnFbLogin.setVisibility(View.INVISIBLE);
								getProfileInformation();
							}

							@Override
							public void onError(DialogError error) {

								// Function to handle error
							}

							@Override
							public void onFacebookError(FacebookError fberror) {

								// Function to handle Facebook errors
							}
						});
			}
		}
	}
