package uz.dictionary.sozluk;

import java.util.Locale;

import uz.dictionary.data.MyCustomAdapter;
import uz.dictionary.data.RecordsDbHelper;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

@SuppressLint("NewApi")
public class MainActivity extends ListActivity {

	private ListView lv;
	private RecordsDbHelper mDbHelper;
	Context cx = this;

	public static SharedPreferences sp;
	public static String sp_name = "settings";

	public static String DIC_ORIGININAL;
	public static String DIC_TRANSLATED;
	public static String current_DB;

	public static String LANG;

	private MenuItem m_history, m_phrases, m_about;
	private EditText search_box;
	private LinearLayout turk_let;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Создаем экземпляр БД
		mDbHelper = new RecordsDbHelper(this);

		// Открываем БД для записи;
		mDbHelper.createDataBase();

		try {
			mDbHelper.openDataBase(true);
		} catch (SQLException sqle) {
			throw sqle;
		}

		// get shared preferences, check if app is first time openened
		sp = getSharedPreferences(sp_name, Context.MODE_PRIVATE);
		boolean hasVisited = sp.getBoolean("hasVisited", false);

		// check if app is first time opened, then save language and
		// hasVisited
		// to SharedPreferences.

		if (!hasVisited) {
			Editor e = sp.edit();
			e.putBoolean("hasVisited", true);
			e.putString("lang", "uz");
			e.commit();
			LANG = "uz";
		} else {
			LANG = sp.getString("lang", "uz");
		}

		Locale locale = new Locale(LANG);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());

		// check language and set fields value according to language
		if (LANG.equals("uz")) {
			DIC_ORIGININAL = "uz_latin";
			DIC_TRANSLATED = "turkish";
			current_DB = "uzb_turk";
		} else {
			DIC_ORIGININAL = "turkish";
			DIC_TRANSLATED = "uz_latin";
			current_DB = "turk_uzb";
		}

		setTitle(R.string.title);

		setContentView(R.layout.activity_main);
		search_box = (EditText) findViewById(R.id.inputSearch);
		turk_let = (LinearLayout) findViewById(R.id.turkish_letters);

		if (LANG.equals("uz")) {
			turk_let.setVisibility(View.GONE);
		}

		lv = (ListView) findViewById(android.R.id.list);

		this.showResults("");

		// click the item on the words list
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				hideKeyboard(view);

				Intent wordIntent = new Intent(cx, WordActivity.class);
				wordIntent.putExtra("pk", String.valueOf(id));
				wordIntent.putExtra("query", search_box.getText());
				startActivity(wordIntent);
			}
		});

		// changing search text box
		search_box.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				MainActivity.this.showResults(cs.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});

		LinearLayout layout = (LinearLayout) findViewById(R.id.main_layout);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		layout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent ev) {
				hideKeyboard(v);
				return false;
			}
		});
	}

	public void changeLang() {
		Locale locale = new Locale(LANG);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());

		updateTexts();
	}

	private void updateTexts() {
		// MenuItem menu_phrases = (MenuItem) findViewById(R.id.action_phrases);
		this.m_history.setTitle(R.string.history);
		this.m_phrases.setTitle(R.string.phrases);
		this.m_about.setTitle(R.string.about);
		this.search_box.setHint(R.string.search_hint);
	}

	private void hideKeyboard(View v) {
		InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		in.hideSoftInputFromWindow(v.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
		search_box.clearFocus();
	}

	public void click_turkish_special_letters(View v) {
		switch (v.getId()) {
		case R.id.button_i:
			search_box.setText(search_box.getText() + "ı");
			search_box.setSelection(search_box.getText().length());
			break;

		case R.id.button_s:
			search_box.setText(search_box.getText() + "ş");
			search_box.setSelection(search_box.getText().length());
			break;
		case R.id.button_g:
			search_box.setText(search_box.getText() + "ğ");
			search_box.setSelection(search_box.getText().length());
			break;
		case R.id.button_c:
			search_box.setText(search_box.getText() + "ç");
			search_box.setSelection(search_box.getText().length());
			break;
		case R.id.button_o:
			search_box.setText(search_box.getText() + "ö");
			search_box.setSelection(search_box.getText().length());
			break;
		case R.id.button_u:
			search_box.setText(search_box.getText() + "ü");
			search_box.setSelection(search_box.getText().length());
			break;
		default:
			break;
		}
	}

	private void showResults(String query) {

		// Ищем совпадения
		Cursor cursor = mDbHelper.fetchRecordsByQuery(query);
		startManagingCursor(cursor);

		String[] from = new String[] { DIC_ORIGININAL, DIC_TRANSLATED };

		int[] to = new int[] { R.id.text1, R.id.text2 };

		// SimpleCursorAdapter records = new SimpleCursorAdapter(this,
		// R.layout.list_item, cursor, from, to);

		MyCustomAdapter records = new MyCustomAdapter(this, R.layout.list_item,
				cursor, from, to);
		// Обновляем адаптер
		setListAdapter(records);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem mi = menu.findItem(R.id.action_change_lang);
		m_history = menu.findItem(R.id.action_history);
		m_phrases = menu.findItem(R.id.action_phrases);
		m_about = menu.findItem(R.id.action_about);

		if (LANG.equals("uz"))
			mi.setIcon(R.drawable.refresh);
		else
			mi.setIcon(R.drawable.refresh);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_change_lang:
			Editor e = sp.edit();

			if (LANG.equals("uz")) {
				turk_let.setVisibility(View.VISIBLE);
				e.putString("lang", "tr");
				LANG = "tr";
				current_DB = "turk_uzb";
				DIC_ORIGININAL = RecordsDbHelper.ROW_TURK;
				DIC_TRANSLATED = RecordsDbHelper.ROW_UZL;
				setTitle("Türkçe-Özbekçe");
				// item.setIcon(R.drawable.turk_uzb);
			} else {
				turk_let.setVisibility(View.GONE);
				e.putString("lang", "uz");
				LANG = "uz";
				current_DB = "uzb_turk";
				DIC_ORIGININAL = RecordsDbHelper.ROW_UZL;
				DIC_TRANSLATED = RecordsDbHelper.ROW_TURK;
				setTitle("O'zbekcha-Turkcha");
				// item.setIcon(R.drawable.uzb_turk);
			}

			changeLang();
			e.commit();
			if (search_box.getText().toString().isEmpty())
				this.showResults("");
			else
				this.showResults(search_box.getText().toString());

			return true;
		case R.id.action_history:
			Intent historyIntent = new Intent(cx, HistoryActivity.class);
			startActivity(historyIntent);
			return true;
		case R.id.action_phrases:
			Intent phrIntent = new Intent(cx, PhrasesActivity.class);
			startActivity(phrIntent);
			return true;
		case R.id.action_about:
			Intent aboutIntent = new Intent(cx, AboutActivity.class);
			startActivity(aboutIntent);
			return true;
		default:
			return false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}
}