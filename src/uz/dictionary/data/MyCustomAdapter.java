package uz.dictionary.data;

import uz.dictionary.sozluk.MainActivity;
import uz.dictionary.sozluk.R;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyCustomAdapter extends SimpleCursorAdapter {

	private int layout;

	public MyCustomAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.layout = layout;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(layout, parent, false);
		return v;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		int orig = cursor.getColumnIndex(MainActivity.DIC_ORIGININAL);
		int trans = cursor.getColumnIndex(MainActivity.DIC_TRANSLATED);

		TextView text1 = (TextView) view.findViewById(R.id.text1);
		TextView text2 = (TextView) view.findViewById(R.id.text2);

		// text1.setText(EDHelper.decrypt(cursor.getString(orig)));
		// text2.setText(EDHelper.decrypt(cursor.getString(trans)));
		text1.setText(cursor.getString(orig));
		text2.setText(cursor.getString(trans));
	}
}
