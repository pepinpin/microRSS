package net.biospherecorp.urss;

// A very simple class that represents a feed
public class OneFeed {

	private String _url;
	private String _name;

	public OneFeed(String url, String name) {
		this._url = url;
		this._name = name;
	}

	public String getName() {
		return _name;
	}

	public String getUrl() {
		return _url;
	}
}
