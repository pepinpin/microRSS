package net.biospherecorp.urss;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

// I didn't use the abstract modifier as I wanted to try out another way
// of controlling instantiation and inheritance (see constructor)
//
// This Class only has static methods
// It's meant to take care of everything related to feeds

class Feeds{

	// ArrayList containing all the OneFeed object that the app
	// can use in order to retrieve articles
	final static ArrayList<OneFeed> LIST = new ArrayList<>();

	// Name of the SharedPreference for RSS feeds
	private final static String SHARED_PREFS_RSS = "rssFeeds";

	// Fields used to setup the default RSS feeds, based on the locale language
	private static String[] default_rss_feed_1, default_rss_feed_2, default_rss_feed_3;

	// Default RSS feeds if none present in the SharedPreference
	private final static String[] RSS_FEED_1_FRA = {"http://cdn1-lejdd.ladmedia.fr/var/exports/rss/rss.xml", "Le JDD"};
	private final static String[] RSS_FEED_2_FRA = {"http://lemonde.fr/rss/une.xml", "La une du Monde"};
	private final static String[] RSS_FEED_3_FRA = {"http://www.generation-nt.com/export/rss.xml", "GNT"};

	private final static String[] RSS_FEED_1_SPA = {"http://www.elespectador.com/rss.xml", "El Espectador - Colombia"};
	private final static String[] RSS_FEED_2_SPA = {"http://ep00.epimg.net/rss/elpais/portada.xml", "El Paìs - España"};
	private final static String[] RSS_FEED_3_SPA = {"http://rss.informador.com.mx/informador-internacional?format=xml", "Informador - Mexico"};

	private final static String[] RSS_FEED_1_ENG = {"https://www.theguardian.com/world/rss", "The Guardian - US"};
	private final static String[] RSS_FEED_2_ENG = {"http://feeds.bbci.co.uk/news/world/rss.xml?edition=uk", "BBC - UK"};
	private final static String[] RSS_FEED_3_ENG = {"http://globalnews.ca/feed/", "GlobalNews - Canada"};


	// to avoid inheritance and instantiation
	// of this class (even by reflection)
	private Feeds(){
		throw new RuntimeException();
	}

	// Instantiate an URL object
	// in order to validate the given url
	static boolean isValid(String url){
		try{
			new URL(url);
		}catch(IOException e){
			return false;
		}
		return true;
	}

	// adds a feed to the sharedPrefs and to the list
	private static void add(Context context, String url, String name){

		// Gets the SharedPreferences
		SharedPreferences.Editor prefs = getSharedPrefs(context).edit();

		// add the url as key and the name as value
		// (so name can "" and no duplicates will be allowed)
		prefs.putString(url,name);
		prefs.apply();

		LIST.add(new OneFeed(url,name));
	}

	// adds a feed to the sharedPrefs and to the list
	// and calls the notifyDataSetChanged() method
	// to update the recyclerView in the SettingsFeedsFragment
	static void add(Context context, FeedsAdapter adapter, String url, String name){
		add(context, url, name);
		adapter.notifyDataSetChanged();
	}

	// removes a feed from the sharedPrefs and from the list
	private static void remove(Context context, OneFeed feed){

		// Gets the SharedPreferences
		SharedPreferences.Editor prefs = getSharedPrefs(context).edit();

		// add the url as key and the name as value
		// (so name can "" and no duplicates will be allowed)
		prefs.remove(feed.getUrl());
		prefs.apply();

		LIST.remove(feed);
	}

	// removes a feed from the sharedPrefs and from the list,
	// then calls the notifyDataSetChanged() method
	// to update the recyclerView in the SettingsFeedsFragment
	static void remove(Context context, FeedsAdapter adapter, OneFeed feed){
		remove(context, feed);
		adapter.notifyDataSetChanged();
	}

	// Called when you want to empty all the feeds
	// from the LIST and SharedPreferences
	private static void clearAll(Context context){

		// call to the getSharedPrefs method, see bellow
		SharedPreferences.Editor editPrefs = getSharedPrefs(context).edit();

		// clears the SharedPreference
		editPrefs.clear();
		editPrefs.apply();

		// clears the LIST Map holding all the urls feed in the app
		LIST.clear();

	}

	// Called when you want to empty all the feeds
	// from the LIST and SharedPreferences,
	// then calls the notifyDataSetChanged() method
	// to update the recyclerView in the SettingsFeedsFragment
	static void clearAll(Context context, FeedsAdapter adapter){
		clearAll(context);
		adapter.notifyDataSetChanged();
	}

	// updates a feed (removes and adds a new one)
	// then calls the notifyDataSetChanged() method
	// to update the recyclerView in the SettingsFeedsFragment
	static void update(Context context, FeedsAdapter adapter, OneFeed oldFeed, OneFeed newFeed){
		remove(context, oldFeed);
		add(context, newFeed.getUrl(), newFeed.getName());
		adapter.notifyDataSetChanged();
	}

	@SuppressWarnings("unchecked") // to suppress the unnecessary warning about the Map<> cast
	static void resetAll(Context context){

		// Gets the SharedPreferences
		SharedPreferences prefs = getSharedPrefs(context);

		// Get all the entries from the SharedPreferences
		Map<String,String> urlEntries = (Map<String, String>) prefs.getAll();

		// Clears the ArrayList
		LIST.clear();

		// If the SharedPreferences aren't empty
		if (!urlEntries.isEmpty()){

			// For each entries in the SharedPreferences,
			// create an OneFeed object and add it to the ArrayList
			for (Map.Entry<String,String> entry : urlEntries.entrySet()){
				LIST.add(new OneFeed(entry.getKey(), entry.getValue()));
			}
		// if the SharedPreferences are empty
		}else{

			// get the system language
			String localeLanguage = Locale.getDefault().getISO3Language();

			switch (localeLanguage){
				case "fra":
					default_rss_feed_1 = RSS_FEED_1_FRA;
					default_rss_feed_2 = RSS_FEED_2_FRA;
					default_rss_feed_3 = RSS_FEED_3_FRA;
					break;
				case "spa":
					default_rss_feed_1 = RSS_FEED_1_SPA;
					default_rss_feed_2 = RSS_FEED_2_SPA;
					default_rss_feed_3 = RSS_FEED_3_SPA;
					break;
				default:
					default_rss_feed_1 = RSS_FEED_1_ENG;
					default_rss_feed_2 = RSS_FEED_2_ENG;
					default_rss_feed_3 = RSS_FEED_3_ENG;
					break;
			}

			// Add the default feeds to the SharedPreferences and to the ArrayList
			add(context, default_rss_feed_1[0], default_rss_feed_1[1]);
			add(context, default_rss_feed_2[0], default_rss_feed_2[1]);
			add(context, default_rss_feed_3[0], default_rss_feed_3[1]);

			// Displays a Toast informing the user that his feed list is empty and that
			// some default feeds have been added to the app
			Toast.makeText(context, context.getString(R.string.empty_list_toast_1) + "\n"
							+ Feeds.default_rss_feed_1[1]
							+ ", " + Feeds.default_rss_feed_2[1]
							+ ", " + Feeds.default_rss_feed_3[1]
							+ context.getString(R.string.empty_list_toast_2),
					Toast.LENGTH_LONG).show();
		}
	}

	// Gets the sharedPreference on "SHARED_PREFS_RSS"
	// and open it in private mode
	private static SharedPreferences getSharedPrefs(Context context){
		return context.getSharedPreferences(SHARED_PREFS_RSS, Activity.MODE_PRIVATE);
	}
}
