package uz.dictionary.sozluk;

import uz.dictionary.data.RecordsDbHelper;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

public class HistoryActivity extends ListActivity {

	private ListView lv;
	EditText inputSearch;
	private RecordsDbHelper mDbHelper;
	Context cx = this;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.pull_in_from_left, R.anim.hold);

		setTitle(R.string.history);
		setContentView(R.layout.history_view);

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
		mDbHelper.openDataBase(true);

		lv = (ListView) findViewById(android.R.id.list);

		showResults();

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent wordIntent = new Intent(cx, WordActivity.class);
				wordIntent.putExtra("pk", String.valueOf(id));
				startActivity(wordIntent);
				// overridePendingTransition(android.R.anim.slide_out_right, 0);
			}
		});

	}

	private void showResults() {

		// Ищем совпадения
		Cursor cursor = mDbHelper.getWordsFromHistory();
		startManagingCursor(cursor);
		String[] from = new String[] { MainActivity.DIC_ORIGININAL };

		int[] to = new int[] { R.id.text1 };

		SimpleCursorAdapter records = new SimpleCursorAdapter(this,
				R.layout.history_list_item, cursor, from, to);

		// Обновляем адаптер
		setListAdapter(records);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
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
}
