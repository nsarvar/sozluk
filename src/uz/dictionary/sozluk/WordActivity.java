/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uz.dictionary.sozluk;

import uz.dictionary.data.RecordsDbHelper;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * Displays a word and its definition.
 */
public class WordActivity extends Activity {

	private RecordsDbHelper mDbHelper;
	private TextView word;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.pull_in_from_left, R.anim.hold);

		if (MainActivity.LANG.equals("uz")) {
			setTitle("O'zbekcha-Turkcha");
		} else {
			setTitle("Türkçe-Özbekçe");
		}

		setContentView(R.layout.word);
		word = (TextView) findViewById(R.id.word);
		WebView wb = (WebView) findViewById(R.id.word_web_view);
		wb.loadUrl("file:///android_asset/pages/footer.html");

		// check the version of sdk, actionBar is pointed from honeycom
		// version(11)
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		// Создаем экземпляр БД
		mDbHelper = new RecordsDbHelper(this);

		// Открываем БД для записи
		mDbHelper.openDataBase(false);

		Intent in = getIntent();
		Bundle extras = in.getExtras();
		if (extras != null) {
			String pk = extras.getString("pk");
			extras.getString("query");

			// insert into history
			mDbHelper.insertToHistory(Integer.parseInt(pk));
			Cursor cursor = mDbHelper.getByPk(Integer.parseInt(pk));

			if (cursor == null) {
				finish();
				// overridePendingTransition(0,android.R.anim.fade_out);
			} else {
				cursor.moveToFirst();
				
				TextView definition = (TextView) findViewById(R.id.definition);

				int orig = cursor
						.getColumnIndexOrThrow(MainActivity.DIC_ORIGININAL);
				int transl = cursor
						.getColumnIndexOrThrow(MainActivity.DIC_TRANSLATED);
				word.setText(cursor.getString(orig));
				definition.setText(cursor.getString(transl));
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.options_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			overridePendingTransition(R.anim.hold, R.anim.pull_out_to_left);
			return true;
		default:
			return false;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mDbHelper.close();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.hold, R.anim.pull_out_to_left);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}