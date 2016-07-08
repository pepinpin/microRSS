package net.biospherecorp.urss;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import static net.biospherecorp.urss.MainActivity.isJavascriptEnabled;

// Simple Activity
public class SettingsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		setTitle(R.string.settings);

		android.support.v7.app.ActionBar actionBar = getSupportActionBar();

		if (actionBar != null){
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		// instantiates the settings fragment
		SettingsSideFragment side_fragment = new SettingsSideFragment();
		SettingsFeedsFragment fragment = new SettingsFeedsFragment();

		if(MainActivity.IS_WIDESCREEN){

			getFragmentManager().beginTransaction()
					.replace(R.id.list, side_fragment)
					.replace(R.id.mainScreen, fragment)
					.commit();
		}else{
			getFragmentManager().beginTransaction()
					.replace(R.id.list, side_fragment)
					.commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		switch (id){
			case android.R.id.home :
				returnToMain();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		returnToMain();
	}

	private void returnToMain(){

		this.finish();

		// checks to see if JS has been enabled in the settings
		// we do it here, so it's only checked when we leave the settings
		MainActivity.IS_JAVASCRIPT_ENABLED = isJavascriptEnabled(getApplicationContext());

	}
}
