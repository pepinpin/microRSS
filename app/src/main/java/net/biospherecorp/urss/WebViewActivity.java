package net.biospherecorp.urss;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class WebViewActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Gets the url and title from the intent's extras
		String url = getIntent().getStringExtra("url");
		String title = getIntent().getStringExtra("title");

		// sets the title of the activity
		// using the app name
		// (articles titles are too long and
		// don't look good on small screens)
		setTitle(R.string.app_name);

		android.support.v7.app.ActionBar actionBar = getSupportActionBar();

		if (actionBar != null){
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		// instantiate a webview fragment
		WebViewFrag wv = WebViewFrag.create(url, title);

		// launch the fragment
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, wv)
				.commit();
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home){
			this.finish();
			return true;
		}

		return false;
	}
}
