package uz.dictionary.sozluk;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_view);
		setTitle(R.string.about_software);
		
		WebView webView = (WebView) findViewById(R.id.webView);
		webView.loadUrl("file:///android_asset/pages/about.html");
	}
}
