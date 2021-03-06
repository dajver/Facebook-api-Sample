package com.example.facebook_api_sample;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

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

public class DetalsActivity extends Activity {

	// строчичка которая принимает айдишник из главного класса
	public static String JSON;
	// собственно ID приложения из facebook dev tools
	// измените на свой
	private static String APP_ID = "343214545779491"; 
	private TextView data;
	// Инициализируем класс фейсбук
	private final Facebook facebook = new Facebook(APP_ID);
	//запускаем его для работы
	private AsyncFacebookRunner mAsyncRunner;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	ImageView imageView;
	String url;

	/** @param key */
	@SuppressWarnings("deprecation")
	public void getAvatar(String key) {

		mAsyncRunner.request(key + "/picture?type=large&redirect=false&", new RequestListener() {

			@Override
			public void onComplete(final String response, Object state) {

				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						try {
							JSONObject profile = new JSONObject(response);
							JSONObject data = profile.getJSONObject("data");
							String url = data.getString("url");
							//по заданому url скачиваем фотографию
							imageLoader.init(ImageLoaderConfiguration.createDefault(DetalsActivity.this));
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

	/** @param key 
	 * Получаем информацию о пользователе по его айди*/
	@SuppressWarnings("deprecation")
	public void getFriendInformation(String key) {

		//id + всеп поля которые мы хотим видеть
		mAsyncRunner.request(key + "?fields=id,name,gender,locale,birthday,hometown,languages,timezone&",
				new RequestListener() {

					@Override
					public void onComplete(final String response, Object state) {

						runOnUiThread(new Runnable() {

							@Override
							public void run() {

								//снова json который возвращает сервер
								String json = response;
								try {
									JSONObject profile = new JSONObject(json);
									// getting name of the user
									String json_name = profile.getString("name");
									String json_male = profile.getString("gender");
									String json_locale = profile.getString("locale");
									//
									data.setText("Name: " + json_name + "\nMale: " + json_male + "\nLocale: "
											+ json_locale);
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
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			finish();
			return;
		}
		setContentView(R.layout.details_activity_layout);
		mAsyncRunner = new AsyncFacebookRunner(facebook);
		data = (TextView) findViewById(R.id.textView2);
		//
		Bundle extras = getIntent().getExtras();
		final String key = extras.getString(JSON);
		//
		imageView = (ImageView) findViewById(R.id.imageView1);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//по клику передаем снова ключ в следующий класс 
				Intent intent = new Intent(DetalsActivity.this, FullAvatarActivity.class);
				intent.putExtra(FullAvatarActivity.JSON, key);
				startActivity(intent);
			}
		});
		//ключ для функции получения иформации
		getFriendInformation(key);
		//ключ для получения аватара
		getAvatar(key);
	}
}