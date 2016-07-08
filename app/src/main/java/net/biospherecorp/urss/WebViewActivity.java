package net.biospherecorp.urss;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


public class WebViewActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Gets the url and title from the intent's extras
		String url = getIntent().getStringExtra("url");
		String title = getIntent().getStringExtra("title");

		// sets the title of the activity
		setTitle(title);

		// instantiate a webview fragment
		WebViewFrag wv = WebViewFrag.create(url, title);

		// launch the fragment
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, wv)
				.commit();
	}
}
