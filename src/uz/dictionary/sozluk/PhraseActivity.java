package uz.dictionary.sozluk;

import uz.dictionary.data.RecordsDbHelper;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ListView;

public class PhraseActivity extends Activity {

	private RecordsDbHelper mDbHelper;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		overridePendingTransition(R.anim.pull_in_from_left, R.anim.hold);

		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		Intent in = getIntent();
		Bundle extras = in.getExtras();

		if (extras != null) {
			String pk = extras.getString("pk");

			// Создаем экземпляр БД
			mDbHelper = new RecordsDbHelper(this);

			// Открываем БД для записи
			try {
				mDbHelper.openDataBase(true);
			} catch (SQLException sqle) {
				throw sqle;
			}
			String clm = "name_" + MainActivity.LANG;
			String html_view = "";
			Cursor c = mDbHelper.getCategoryByPk(pk);

			String[] columnNames = c.getColumnNames();
			String ss = "";
			for (String s : columnNames) {
				ss += s + ":";
			}

			if (c.getCount() > 0) {
				c.moveToFirst();
				setTitle(c.getString(c.getColumnIndex(clm)));
				html_view = c.getString(c.getColumnIndex("html_page"));

				c.close();
			}

			Cursor cursor = mDbHelper.getPhrases(pk);
			String clm1, clm2;

			if (MainActivity.LANG.equals("tr")) {
				clm1 = "turkish";
				clm2 = "uzbek";
			} else {
				clm1 = "uzbek";
				clm2 = "turkish";
			}

			// int i = 0;
			String[] from = new String[] { clm1, clm2 };
			int[] to = new int[] { R.id.text1, R.id.text2 };
			SimpleCursorAdapter ca = new SimpleCursorAdapter(this,
					R.layout.phrases_list_item, cursor, from, to);
			// // // Обновляем адаптер
			setContentView(R.layout.phrases_view);
			ListView lv = (ListView) findViewById(R.id.phrase_list_view);
			WebView wv = (WebView) findViewById(R.id.phrase_web_view);

			if (html_view != null) {
				lv.setVisibility(View.INVISIBLE);
				wv.setWebChromeClient(new WebChromeClient());
				wv.getSettings().setJavaScriptEnabled(true);
				wv.addJavascriptInterface(new AudioInterface(this), "AndAud");
				wv.loadUrl("file:///android_asset/pages/" + html_view);
			} else {
				wv.setVisibility(View.INVISIBLE);
				lv.setAdapter(ca);
			}
		}
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
}
