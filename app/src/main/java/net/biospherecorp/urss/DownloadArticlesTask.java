package net.biospherecorp.urss;

import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

// AsyncTask used to retrieve a xml file from a RSS feed
// and to create an ArrayList of Articles from it
//
// It is called every time the list of article is refreshed
// or when a filter is applied (Today, Yesterday, All)
class DownloadArticlesTask extends AsyncTask<OneFeed, Void, ArrayList<Article>> {

	private ArrayList<Article> _list;
	private ListFragment _listFrag;
	private String _siteName;

	// Constructor that takes a fragment containing a recyclerView
	// will be used in the onPostExecute() method
	DownloadArticlesTask(ListFragment listFrag) {
		this._listFrag = listFrag;
	}

	// Right before the AsyncTask enters the doInBackground() method,
	// instantiate the ArrayList
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		_list = new ArrayList<>();
	}

	@Override
	protected ArrayList<Article> doInBackground(OneFeed... params) {

		// the Map.Entry key is the url
		// the Map.Entry value is the name of the website

		Document document;
		Element node;
		String date, title, description, url;
		Article article;

		// I'll only pass 1 OneFeed object by call to this AsyncTask,
		// no need to iterate over the params vararg
		//
		// get the url
		String urlFeed = params[0].getUrl();

		// Get the name
		_siteName = params[0].getName();

		try {
			// Creates a DOM document from a RSS field
			document = DocumentBuilderFactory
					.newInstance().newDocumentBuilder()
					.parse(urlFeed);

			// Extract the title (Name of the feed) from the document
			String rssSiteName = document.getElementsByTagName("title").item(0).getTextContent();

			// Extract the link (url to the main site) from the document
			String rssUrl = document.getElementsByTagName("link").item(0).getTextContent();

			// Found the siteName somewhere
			if(!params[0].getName().equals("")){ // if OneFeed arguments name isn't empty
				 _siteName =  params[0].getName();

			}else if(!rssSiteName.equals("")){ //if rssSiteName (Name extracted from the XML document) isn't empty
				_siteName = rssSiteName;

			}else if (rssUrl != null && !rssUrl.equals("")){ // if rssUrl (Url extracted from the XML document) isn't empty
				_siteName = rssUrl;

			}else{ // if we can't get a name, use the OneFeed url as name
				_siteName = urlFeed;
			}

			// Retrieves a List of all the "item" nodes from the document
			NodeList elements = document.getElementsByTagName("item");

			// for each elements in the NodeList
			for (int i = 0; i < elements.getLength(); i++) {

				// check if the AsyncTask has been canceled
				if (!isCancelled()) {

					// Extracts the current Element from the NodeList
					node = (Element) elements.item(i);

					// Get the date
					date = getElementsText(node, "pubDate");

					// Get the title and clean it
					title = cleanupTextFromHtml(
							getElementsText(node, "title"));

					// Get the description and clean it
					description = cleanupTextFromHtml(
							getElementsText(node, "description"));

					// Get the link(url) to the Article
					url = getElementsText(node, "link");

					// Instantiate an Article object
					article = new Article.Builder(_listFrag.getActivity())
							.setTitle(title)
							.setDate(date)
							.setDescription(description)
							.setUrl(url)
							.setSiteName(_siteName)
							.build();

					// Checks if the filter is set to "ALL" or if the date of the
					// article matches the filter
					if(_listFrag.getFilter().equals("") ||
							_listFrag.getFilter().equals(article.getDate())){

						// adds the object to the ArrayList
						_list.add(article);
					}
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException | RuntimeException e) {
			Log.e(">> DownloadArticlesTask", e.getLocalizedMessage());
			return null;
		}

		return _list;
	}

	// This methods takes an element and a tagName (String),
	// look through the element and returns the text
	// that belongs to the tag "tagName"
	private String getElementsText(Element el, String tagName) {
		return el.getElementsByTagName(tagName).item(0).getTextContent();
	}


	// Clean up a text from html entities and tags
	private String cleanupTextFromHtml(String txt){

		// Cleaning up the description text by entities
		//
		txt = txt.replaceAll("&quot;", "\"");
		txt = txt.replaceAll("&nbsp;", " ");
		txt = txt.replaceAll("&lt;", "<");
		txt = txt.replaceAll("&gt;", ">");

		txt = txt.replaceAll("&amp;", "&");
		txt = txt.replaceAll("&cent;", "¢");
		txt = txt.replaceAll("&pound;", "£");
		txt = txt.replaceAll("&yen;", "¥");
		txt = txt.replaceAll("&euro;", "€");
		txt = txt.replaceAll("&copy;", "©");
		txt = txt.replaceAll("&reg;", "®");

		txt = txt.replaceAll("&atilde;", "ã");
		txt = txt.replaceAll("&agrave;", "à");
		txt = txt.replaceAll("&aacute;", "á");
		txt = txt.replaceAll("&acirc;", "â");

		txt = txt.replaceAll("&egrave;", "è");
		txt = txt.replaceAll("&eacute;", "é");
		txt = txt.replaceAll("&ecirc;", "ê");

		txt = txt.replaceAll("&ccedil;", "ç");
		txt = txt.replaceAll("&ntilde;", "ñ");
		txt = txt.replaceAll("&ugrave;", "ù");
		txt = txt.replaceAll("&aelig;", "æ");


		// Cleaning up the description text by tag
		//
		txt = txt.replaceAll("<img .*?></img>","");
		txt = txt.replaceAll("<img .*?>","");

		txt = txt.replaceAll("<strong.*?>","");
		txt = txt.replaceAll("</strong>","");

		txt = txt.replaceAll("<span.*?>","");
		txt = txt.replaceAll("</span>","");

		txt = txt.replaceAll("<p.*?>","");
		txt = txt.replaceAll("</p>","");

		txt = txt.replaceAll("<a.*?>","");
		txt = txt.replaceAll("</a>","");

		txt = txt.replaceAll("<ul.*?>","");
		txt = txt.replaceAll("</ul>","");

		txt = txt.replaceAll("<li.*?>","");
		txt = txt.replaceAll("</li>","");

		txt = txt.replaceAll("<br>","");
		txt = txt.replaceAll("<br.*?>","");

		return txt;
	}

	// When done with this, update the recyclerView
	@Override
	protected void onPostExecute(ArrayList<Article> articles) {
		super.onPostExecute(articles);

		_listFrag.updateRecyclerView(_siteName, articles, this);

	}
}
