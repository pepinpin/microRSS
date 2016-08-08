package net.biospherecorp.urss;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Adapter for the recyclerView located in the ListFragment
// nothing special here
//
// Uses the Delegation Design Pattern
class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder>{

	// Interface to delegate the loading
	// of the article
	interface ArticleLoader{
		void load(Article article);
	}

	// The delegate object
	private final ArticleLoader _loader;

	// Uses a Synchronized List to avoid concurrent access
	private List<Article> _list = Collections.synchronizedList(new ArrayList<Article>());

	ListAdapter(ArticleLoader loader) {
		// Gets the delegate Object
		this._loader = loader;
	}

	void addArrayList(ArrayList<Article> articles){

		// add all elements from articles
		// to the list of articles
		_list.addAll(articles);

		// Sorts the Articles by date,
		// hence the override of the compareTo() method
		// in the Article class
		Collections.sort(_list);
		notifyDataSetChanged();

		if (_list.size() > 0){
			autoLoadArticle(getLatestArticle());
		}
	}

	// called only if there is no articles retrieved
	// from the net (no connection or no news with selected filter)
	//
	// called by the ListAdapter updateRecyclerView() method
	void addArticle(Article article){
		_list.add(article);
		notifyDataSetChanged();

		autoLoadArticle(article);
	}

	private void autoLoadArticle(Article articleToLoad){

		// autoload latest article
		if(MainActivity.IS_WIDESCREEN // if it's a tablet
				&& ListFragment._listTasks.isEmpty()){ // and there is no more Asynctask running (to really get the latest article)

			// load the article in webview
			_loader.load(articleToLoad);
		}

	}

	void clearList(){
		_list.clear();
		notifyDataSetChanged();
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View view = inflater.inflate(R.layout.list_cell, parent, false);
		return new MyViewHolder(view);
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {

		holder.configure(_list.get(position));
	}

	@Override
	public int getItemCount() {
		return _list.size();
	}

	// Retrieves the latest Article from the list
	private Article getLatestArticle(){
		return _list.get(0);
	}


	class MyViewHolder extends RecyclerView.ViewHolder{

		private TextView _site, _date, _title, _description;
		private Article _article;

		MyViewHolder(final View itemView) {
			super(itemView);

			_site = (TextView) itemView.findViewById(R.id.siteTV);
			_date = (TextView) itemView.findViewById(R.id.dateTV);
			_title = (TextView) itemView.findViewById(R.id.titleTV);
			_description = (TextView) itemView.findViewById(R.id.descriptionTV);

			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(_loader != null){

						// delegates the loading and display of
						// the article to the ArticleLoader (MainActivity here)
						_loader.load(_article);
					}
				}
			});

		}

		void configure(Article art){

			_article = art;

			String date = _article.getDate();
			String time = _article.getTime();

			String dateString = time != null ?
					date + " @ " + time :
					date;

			_site.setText(_article.getSite());
			_date.setText(dateString);
			_title.setText(_article.getTitle());
			_description.setText(_article.getDescription());
		}
	}
}
