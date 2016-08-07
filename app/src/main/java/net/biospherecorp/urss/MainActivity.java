package net.biospherecorp.urss;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements ListAdapter.ArticleLoader{

	static boolean IS_WIDESCREEN = false;
	static boolean IS_JAVASCRIPT_ENABLED = false;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		IS_JAVASCRIPT_ENABLED = isJavascriptEnabled(getApplicationContext());

		setContentView(R.layout.activity_main);

		// check once to see if the device is a tablet
		// (has wide screen >700dp)
		if(findViewById(R.id.mainScreen) != null){
			IS_WIDESCREEN = true;
		}

		// checks if a fragment has already been created
		// if it hasn't been created yet, create it
		if (savedInstanceState == null) {

			ListFragment fragment = new ListFragment();

			getFragmentManager().beginTransaction()
					.add(R.id.list, fragment)
					.commit();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// sets the title here so it's reset
		// to default every time a user presses
		// the back button from a webview (on phone)
		setTitle(R.string.app_name);
	}

	// This method is from the ArticleLoader Interface
	//
	// It delegates the loading of the article to this activity
	// since it's the only one to "know" whether
	// the device is a tablet (wide screen) or a phone
	@Override
	public void load(Article article) {

		if (article != null){

			String title = article.getTitle();
			String url = article.getUrl();

			// If it's a tablet or TV (wide screen)
			if(IS_WIDESCREEN){

				// set the title
				setTitle(title);

				// Create a webViewFragment object
				WebViewFrag _wvFrag = WebViewFrag.create(article.getUrl(), article.getTitle());

				// load this fragment in the mainScreen view
				getFragmentManager()
						.beginTransaction()
						.replace(R.id.mainScreen, _wvFrag)
						.addToBackStack(null)
						.commit();

			// if it's a phone
			}else{
				// Create an intent and start a new activity
				Intent intent = new Intent(this, WebViewActivity.class);
				intent.putExtra("url", url);
				intent.putExtra("title", title);

				startActivity(intent);
			}
		}
	}

	// Checks the sharedPreferences to see if JS has been enabled
	static boolean isJavascriptEnabled(Context applicationContext){
		return PreferenceManager
				.getDefaultSharedPreferences(applicationContext)
				.getBoolean("activateJS", false);
	}
}
