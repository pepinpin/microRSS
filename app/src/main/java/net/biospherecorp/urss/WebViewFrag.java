package net.biospherecorp.urss;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebViewFrag extends Fragment {

	private static String _url, _title;

	// A fragment can only have a constructor with NO arguments
	// So we use a static factory method to go around this
	static WebViewFrag create(String url, String title){

		_url = url;
		_title = title;

		WebViewFrag frag = new WebViewFrag();
		frag.setHasOptionsMenu(true);

		return frag;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// instantiate the webview
		WebView webView = new WebView(getActivity());

		// by default, disables javascript for faster loading and better security
		webView.getSettings().setJavaScriptEnabled(MainActivity.IS_JAVASCRIPT_ENABLED);

		// by default, disables the DOM's local storage (save storage and increases security)
		webView.getSettings().setDomStorageEnabled(MainActivity.IS_JAVASCRIPT_ENABLED);

		// enables overview mode : zooms out to show the whole page
		webView.getSettings().setLoadWithOverviewMode(true);

		// uses the viewport HTML tag (if exists) to correctly display webpages
		webView.getSettings().setUseWideViewPort(true);

		// enforce the activation of the zoom mechanism (true by default, but still...^^)
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setDisplayZoomControls(false); // hides the zoom overlays

		// set the webview client to use ... just use the default
		webView.setWebViewClient(new WebViewClient());

		if(MainActivity.IS_JAVASCRIPT_ENABLED){
			// no need for javascript alerts, favicon
			// so no need for the WebChromeClient
			webView.setWebChromeClient(new WebChromeClient());
		}

		// Loads the url in the webview
		webView.loadUrl(_url);

		L.m("is JS Enabled : " + MainActivity.IS_JAVASCRIPT_ENABLED);

		// return the webview
		return webView;
	}


	// The menu

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// Inflate the menu layout
		inflater.inflate(R.menu.webview_fragment_menu, menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//super.onOptionsItemSelected(item);

		// enable the "Share" and "View in Browser" events
		// if the url doesn't start with "file:///"
		if (!_url.startsWith("file:///")){

			Intent intent;

			switch(item.getItemId()){

				// "Share" button
				case R.id.shareButton:

					// Instantiate an implicit intent
					intent = new Intent(Intent.ACTION_SEND);

					// sets the MIME type
					intent.setType("text/plain");

					// add the "Check this out" localized string to the url
					intent.putExtra(Intent.EXTRA_TEXT,
							getResources().getString(R.string.shareText) +
									"\n" + _title +
									"\n \n" + _url);

					// displays an app chooser to share the link with
					// (any app that can handle text/plain, like sms, email... )
					startActivity(Intent.createChooser(intent, getResources().getString(R.string.shareButton)));
					return true;

				// "view in the Browser" button
				case R.id.viewInBrowserButton:

					// Instantiate an implicit intent
					// that's gonna open up the browser
					intent = new Intent(Intent.ACTION_VIEW, Uri.parse(_url));

					// start the Activity "open browser with URL"
					startActivity(intent);
					return true;
			}
		}

		return false;
	}
}
