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
	
	// собственно ID приложения из facebook dev tools
	// измените на свой
	private static String APP_ID = "343214545779491"; 
	private static final String FIRST = "name";
	private static final String IMAGE = "image";
	private static final String LAST = "id";
	//массив в котором будем хранить список друзей с фотографиями
	private static ArrayList<HashMap<String, Object>> myUsers; 
	// Инициализируем класс фейсбук
	private Facebook facebook;
	//запускаем его для работы
	private AsyncFacebookRunner mAsyncRunner;
	private SharedPreferences mPrefs;
	private String FILENAME = "AndroidSSO_data";
	private int i = 0;
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		listView = (ListView) findViewById(R.id.listView1);
		//инициализируем наши объекты
		facebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(facebook);
		
		mPrefs = getPreferences(MODE_PRIVATE);
		//проверяем залогинен пользователь или нет
		//если залогинен то сразу выводим список друзей
		//если нет то выводим окно логина
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);
		if (access_token != null) {
			facebook.setAccessToken(access_token);
			Log.d("FB Sessions", "" + facebook.isSessionValid());
		}
		if (expires != 0) {
			facebook.setAccessExpires(expires);
			getProfileInformation();
		}
		if (expires == 0) {
			loginToFacebook();
		}
		myUsers = new ArrayList<HashMap<String, Object>>();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {

				HashMap<String, Object> item = myUsers.get(position);
				String key = item.get(LAST).toString();
				
					Intent intent = new Intent(MainActivity.this, DetalsActivity.class);
					intent.putExtra(DetalsActivity.JSON, key);
					startActivity(intent);
				
			}
		});
	}

		/** Забираем информацию списка друзей при помощи graph anpi */
		@SuppressWarnings("deprecation")
		public void getProfileInformation() {

			Bundle params = new Bundle();
			params.putString("fields", "name, picture");
			//посылаем запрос на вывод всех друзей с картинками и именами
			mAsyncRunner.request("me/friends", params, new RequestListener() {

				@Override
				public void onComplete(String response, Object state) {

					//json который приходит с сервера
					String json = response;
					try {
						JSONObject profile = new JSONObject(json);
						JSONArray data = profile.getJSONArray("data");
						for (i = 0; i < data.length(); i++) {
							// забираем данные из json
							final String name = data.getJSONObject(i).getString("name");
							final String id = data.getJSONObject(i).getString("id");
							JSONObject picture = data.getJSONObject(i).getJSONObject("picture");
							JSONObject picdata = picture.getJSONObject("data");
							final String url = picdata.getString("url");
							runOnUiThread(new Runnable() {

								@Override
								public void run() {

									//закидываем в массив все что получили
									HashMap<String, Object> hm;
									hm = new HashMap<String, Object>();
									hm.put(FIRST, name);
									hm.put(LAST, id);
									hm.put(IMAGE, url);
									myUsers.add(hm);
									//вытаскиваем для передачи в адаптер (так лучше не делать, мой косяк)
									String[] urls = new String[myUsers.size()];
									String[] names = new String[myUsers.size()];
									String[] indexcode = new String[myUsers.size()];
									for (int i = 0; i < myUsers.size(); i++) {
										urls[i] = (String) myUsers.get(i).get(IMAGE);
										names[i] = (String) myUsers.get(i).get(FIRST);
										indexcode[i] = (String) myUsers.get(i).get(LAST);
									}
									//передаем в адаптер для распечатки
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

		/** Функция логина в фейсбук*/
		@SuppressWarnings("deprecation")
		public void loginToFacebook() {

			if (!facebook.isSessionValid()) {
				facebook.authorize(this, new String[] { "email", "publish_stream" },
						new DialogListener() {

							@Override
							public void onCancel() {

								//функция отмены действия
							}

							@Override
							public void onComplete(Bundle values) {

								// сохраняем данные что бы не вводить по триста раз на день
								// при входе заполняем 
								SharedPreferences.Editor editor = mPrefs.edit();
								editor.putString("access_token", facebook.getAccessToken());
								editor.putLong("access_expires", facebook.getAccessExpires());
								editor.commit();
								getProfileInformation();
							}

							@Override
							public void onError(DialogError error) {

								// функция ошибки
							}

							@Override
							public void onFacebookError(FacebookError fberror) {

								// функция фейсбучной ошибки
							}
						});
			}
		}
	}
