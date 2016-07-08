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
public class DownloadArticlesTask extends AsyncTask<OneFeed, Void, ArrayList<Article>> {

	private ArrayList<Article> _list;
	private ListFragment _listFrag;
	private String _siteName;

	// Constructor that takes a fragment containing a recyclerView
	// will be used in the onPostExecute() method
	public DownloadArticlesTask(ListFragment listFrag) {
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
					// Get the title
					title = getElementsText(node, "title");
					// Get the description
					description = getElementsText(node, "description");


				// Cleaning up the description text

					// check to see if there is a <img> tag in the node
					if (node.getElementsByTagName("img") != null){

						// if there is one in the description, replace it with empty string
						description = description.replaceAll("<img .*?></img>","");
						description = description.replaceAll("<img .*?>","");
					}

					// check to see if there is a <strong> tag in the node
					if (node.getElementsByTagName("strong") != null){

						// if there is one in the description, replace it with empty string
						description = description.replaceAll("<strong.*?>","");
						description = description.replaceAll("</strong>","");
					}

					// check to see if there is a <p> tag in the node
					if (node.getElementsByTagName("p") != null){

						// if there is one in the description, replace it with empty string
						description = description.replaceAll("<p.*?>","");
						description = description.replaceAll("</p>","");
					}

					// check to see if there is a <a> tag in the node
					if (node.getElementsByTagName("a") != null){

						// if there is one in the description, replace it with empty string
						description = description.replaceAll("<a.*?>","");
						description = description.replaceAll("</a>","");
					}

					// check to see if there is a <ul> tag in the node
					if (node.getElementsByTagName("ul") != null){

						// if there is one in the description, replace it with empty string
						description = description.replaceAll("<ul.*?>","");
						description = description.replaceAll("</ul>","");
					}

					// check to see if there is a <li> tag in the node
					if (node.getElementsByTagName("li") != null){

						// if there is one in the description, replace it with empty string
						description = description.replaceAll("<li.*?>","");
						description = description.replaceAll("</li>","");
					}

					// check to see if there is a <br> tag in the node
					if (node.getElementsByTagName("br") != null){

						// if there is one in the description, replace it with empty string
						description = description.replaceAll("<br>","");
					}

				// end of cleaning up



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

	// When done with this, update the recyclerView
	@Override
	protected void onPostExecute(ArrayList<Article> articles) {
		super.onPostExecute(articles);

		_listFrag.updateRecyclerView(_siteName, articles, this);

	}
}
