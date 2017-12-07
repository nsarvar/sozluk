package uz.dictionary.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import uz.dictionary.sozluk.MainActivity;
import uz.dictionary.sozluk.R;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

public class RecordsDbHelper extends SQLiteOpenHelper {

	private static String DB_PATH = "/data/data/uz.dictionary.sozluk/databases/";
	private static String DB_NAME = "data.sqlite";
	private static int DB_VERSION = 2;

	public static String TABLE_CATEGORY = "category";

	public static String KEY_ROWID = "_id";
	public static String ROW_TURK = "turkish";
	public static String ROW_UZL = "uz_latin";
	public static String ROW_UZC = "uz_cyrill";

	public static String LIMIT = "20";

	private SQLiteDatabase myDataBase;
	private final Context myContext;

	public RecordsDbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.myContext = context;
	}

	public void createDataBase() {

		boolean dbExist = checkDataBase();

		if (dbExist) {
			this.getWritableDatabase();
		}
		dbExist = checkDataBase();

		if (!dbExist) {
			this.getReadableDatabase();
			copyDataBase();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {}

	public void updating(SQLiteDatabase db) {
		// String add_category = "insert into "
		// + TABLE_CATEGORY
		// +
		// " (name_tr, name_uz, t_order, html_page) values('Vücut Bölümleri', 'Tana a''zolari', '105', 'body.html')";
		// db.execSQL(add_category);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		copyDataBase();
	}

	private void copyDataBase() {

		final ProgressDialog pd = ProgressDialog.show(myContext, "",
				"Loading data...", false);
		pd.setIcon(R.drawable.loading);
		pd.show();

		// Open your local db as the input stream
		InputStream myInput;
		try {
			myInput = myContext.getAssets().open(DB_NAME);
			// Path to the just created empty db
			String outFileName = DB_PATH + DB_NAME;

			// Open the empty db as the output stream
			OutputStream myOutput = new FileOutputStream(outFileName);

			// transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}

			// Close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pd.dismiss();

	}

	private boolean checkDataBase() {
		File dbFile = myContext.getDatabasePath(DB_NAME);
		return dbFile.exists();
	}

	public void openDataBase(boolean readOnly) throws SQLException {
		// Open the database
		String myPath = DB_PATH + DB_NAME;
		if (readOnly)
			myDataBase = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
		else
			myDataBase = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READWRITE);
	}

	@Override
	protected void finalize() throws Throwable {
		if (null != myDataBase)
			myDataBase.close();
		super.finalize();
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}

	// CRUD operations
	public void addCategory(Category c) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("name_tr", c.name_tr);
		values.put("name_uz", c.name_uz);
		values.put("t_order", c.t_order);
		values.put("html_page", c.html_page);

		db.insert(TABLE_CATEGORY, null, values);

	}

	public Cursor fetchRecordsByQuery(String query) {

		if (query.isEmpty()) {
			return myDataBase.query(true, MainActivity.current_DB,
					new String[] { KEY_ROWID, ROW_UZL, ROW_TURK }, null, null,
					null, null, MainActivity.DIC_ORIGININAL, LIMIT);
		}

		String[] selectionArgs = { query + "%" };
		return myDataBase.query(true, MainActivity.current_DB, new String[] {
				KEY_ROWID, ROW_UZL, ROW_TURK }, MainActivity.DIC_ORIGININAL
				+ " LIKE ?", selectionArgs, null, null,
				MainActivity.DIC_ORIGININAL, LIMIT);
	}

	public Cursor getByPk(long pk) {
		return myDataBase.query(true, MainActivity.current_DB, new String[] {
				KEY_ROWID, ROW_UZL, ROW_TURK }, KEY_ROWID + "=" + pk, null,
				null, null, MainActivity.DIC_ORIGININAL, "1");
	}

	public long insertToHistory(int pk) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("word_id", pk);
		initialValues.put("lang", MainActivity.LANG);
		myDataBase.delete("history", "word_id=" + pk, null);
		return myDataBase.insert("history", null, initialValues);
	}

	public Cursor getWordsFromHistory() {
		// Create new querybuilder
		SQLiteQueryBuilder QB = new SQLiteQueryBuilder();
		QB.setTables("history" + " LEFT OUTER JOIN " + MainActivity.current_DB
				+ " ON history.word_id=" + MainActivity.current_DB
				+ "._id where history.lang='" + MainActivity.LANG + "'");

		String _OrderBy = "history._id DESC";

		return QB.query(myDataBase, null, null, null, null, null, _OrderBy);
	}

	public Cursor getCategoryByPk(String pk) {
		return myDataBase.query("category", new String[] { "_id", "name_tr",
				"name_uz", "html_page" }, "_id=" + pk, null, null, null,
				"t_order");
	}

	public Cursor getCategories() {
		return myDataBase.query("category", new String[] { "_id", "name_tr",
				"name_uz" }, null, null, null, null, "t_order");
	}

	public Cursor getPhrases(String pk) {
		return myDataBase.query("phrases", new String[] { "_id", "turkish",
				"uzbek" }, "category=" + pk, null, null, null, null, null);
	}
}