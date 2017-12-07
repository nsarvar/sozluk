package uz.dictionary.sozluk;

import uz.dictionary.data.RecordsDbHelper;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class PhrasesActivity extends ListActivity {

	private RecordsDbHelper mDbHelper;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.pull_in_from_left, R.anim.hold);
		setTitle(R.string.phrases);

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
		try {
			mDbHelper.openDataBase(true);
		} catch (SQLException sqle) {
			throw sqle;
		}

		setContentView(R.layout.phrases_cat_list_item);

		Cursor cursor = mDbHelper.getCategories();

		int i = 0;
		String text1 = "name_" + MainActivity.LANG;
		String aa = (MainActivity.LANG.equals("uz")) ? "tr" : "uz";
		String text2 = "name_" + aa;
		String[] from = new String[] { text1, text2 };
		int[] to = new int[] { R.id.text1, R.id.text2 };
		SimpleCursorAdapter ca = new SimpleCursorAdapter(this,
				R.layout.list_item, cursor, from, to);
		// // Обновляем адаптер
		this.setListAdapter(ca);
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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		Intent phraseIntent = new Intent(this, PhraseActivity.class);
		phraseIntent.putExtra("pk", String.valueOf(id));
		// wordIntent.putExtra("query", search_box.getText());
		startActivity(phraseIntent);
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
