package com.example.facebook_api_sample;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public class FullAvatarActivity extends FragmentActivity {

	public static String JSON;
	private static String APP_ID = "343214545779491"; // Replace with your App ID
	private final Facebook facebook = new Facebook(APP_ID);
	private AsyncFacebookRunner mAsyncRunner;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	ImageView imageView;

	/** @param key */
	@SuppressWarnings("deprecation")
	public void getBigImage(String key) {

		mAsyncRunner.request(key + "/picture?width=500&height=500&redirect=false&", new RequestListener() {

			@Override
			public void onComplete(final String response, Object state) {

				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						try {
							JSONObject profile = new JSONObject(response);
							JSONObject data = profile.getJSONObject("data");
							String url = data.getString("url");
							Log.v("", "" + url);
							imageLoader.init(ImageLoaderConfiguration.createDefault(FullAvatarActivity.this));
							imageLoader.displayImage(url, imageView);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
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

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.big_image);
		mAsyncRunner = new AsyncFacebookRunner(facebook);
		imageView = (ImageView) findViewById(R.id.userimage);
		Bundle extras = getIntent().getExtras();
		String key = extras.getString(JSON);
		getBigImage(key);
	}
}
