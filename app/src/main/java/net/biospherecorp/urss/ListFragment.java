package net.biospherecorp.urss;


import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class ListFragment extends Fragment{

	static ArrayList<DownloadArticlesTask> _listTasks = new ArrayList<>();
	private ListAdapter _adapter;
	private SwipeRefreshLayout _swipeRefresh;

	// used by the startActivityForResult
	// and onActivityResult methods
	private static final int RETURN_FROM_SETTINGS = 1;

	// The filter, can have 3 values :
	// - "" (means All)
	// - Today
	// - Yesterday
	static String _filter ="";

	// Very simple Fragment, no need to override
	// the default constructor nor to implement a static factory method

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		L.m(getActivity(), ">> onCreateView()");

		// To prepare the fragment to use a menu
		setHasOptionsMenu(true);

		return inflater.inflate(R.layout.list_fragment, container, false);
	}

	@Override
	public void onViewCreated(final View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		L.m(getActivity(), ">> onViewCreated()");

		_swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
		_swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				_refreshData();
			}
		});

		final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.listFragmentRecyclerView);

		if (recyclerView != null) {

		// set the Recycler view
			// instantiation of the adapter needed by the recyclerView
			_adapter = new ListAdapter((ListAdapter.ArticleLoader) getActivity());

			// sets the recyclerView layout
			recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

			// sets the recyclerView adapter
			recyclerView.setAdapter(_adapter);


		// set the OnClickListener on the button
			//
			// on click on the "back to the top" button
			final FloatingActionButton backTop = (FloatingActionButton) view.findViewById(R.id.backToTopButton);

			backTop.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// move to the top of the recycler view (latest article)
					recyclerView.scrollToPosition(0);
				}
			});

		// set the onScrollListener on the recyclerView
			//
			// on scroll
			recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
				@Override
				public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
					super.onScrolled(recyclerView, dx, dy);

					int posInRecyclerView = recyclerView.getChildLayoutPosition(recyclerView.findChildViewUnder(dx,dy));
					L.m("position in recyclerView = " + posInRecyclerView);

					// if position > 0 (1st article isn't visible anymore)
					if (posInRecyclerView > 0){
						// show the "back to the top" button
						backTop.setVisibility(View.VISIBLE);
					}else{
						// hide the "back to the top" button
						backTop.setVisibility(View.GONE);
					}
				}
			});
		}
		_refreshData();
	}

	// Refreshes ALL the app's data ...
	// Articles AND Feeds
	private void _refreshData(){

		if(!_swipeRefresh.isRefreshing()){
			_swipeRefresh.setRefreshing(true);
		}

		// clears tasks & adapter list
		_clearAll();
		// clears the list of feeds
		Feeds.resetAll(getActivity());

		// execute as many AsyncTasks as there is feeds
		// in the feed list
		for (OneFeed site : Feeds.LIST) {
			DownloadArticlesTask task = new DownloadArticlesTask(this);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, site);

			// add the task to the list of task...
			// to keep track of tasks states of completion
			_listTasks.add(task);
		}
	}

	private void _clearAll(){
		_clearTasks();
		_clearRecyclerView();
	}

	private void _clearTasks(){

		if (!_listTasks.isEmpty()){

			for (AsyncTask task : _listTasks) {

				// Calls for cancellation
				// it's done asynchronously so you never
				// know exactly when the task is really gonna end,
				// so in the Asynctask we need to check periodically
				// for a cancel() call with isCancelled(), to act asap
				task.cancel(true);
			}
		}

		_listTasks.clear();
	}

	// Called from the AsyncTasks onPostExecute() methods
	//
	// removes the calling tasks from the tasksList and
	// add all the articles retrieved by the AsyncTasks to the
	// adapter's list or creates an empty article if no news at all
	// in the adapter's list
	void updateRecyclerView(String siteName, ArrayList<Article> articles, DownloadArticlesTask task){

		// removes from the _listTask the asynctask whose calling this method
		_listTasks.remove(task);

		// If the return object by the AsyncTask isn't null
		if (articles != null){
			// add it to the adapter's list
			_adapter.addArrayList(articles);

		// if the object return by the AsyncTask is null,
		// display a Toast to the user saying that there
		// is no news for the a feed or the selected filter
		}else{
			Toast.makeText(getActivity(), getString(R.string.NoNews_part1) + " "+
					(_filter.equals("") ? "" : _filter + " " ) +
					getString(R.string.NoNews_part2) + " " + siteName, Toast.LENGTH_SHORT)
					.show();
		}

		// if no more task in _tasksList
		if (_listTasks.isEmpty()){

			// hide the progressBar
			_swipeRefresh.setRefreshing(false);

			// if no articles in the adapter's List
			if(_adapter.getItemCount() == 0){

				// create an empty article to add to the adapter
				Article emptyArticle = new Article.Builder(getActivity())
						.setDate(_filter)
						.setTitle(getString(R.string.NoNews_part1))
						.setUrl("file:///android_asset/error.html")
						.setDescription(getString(R.string.NoNews_part3))
						.build();

				// add this article to the adapter
				_adapter.addArticle(emptyArticle);
			}
		}
	}

	private void _clearRecyclerView(){
		_adapter.clearList();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Clear the task and the recycler view
		_clearAll();
	}


	String getFilter() {
		return _filter;
	}



	// The Menu

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.list_fragment_menu, menu);
		menu.findItem(R.id.allButton).setChecked(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//super.onOptionsItemSelected(item);

		switch (item.getItemId()){
			case R.id.todayButton:
				item.setChecked(!item.isChecked());
				_filter = getResources().getString(R.string.today);
				_refreshData();
				return true;
			case R.id.yesterdayButton:
				item.setChecked(!item.isChecked());
				_filter = getResources().getString(R.string.yesterday);
				_refreshData();
				return true;
			case R.id.allButton:
				item.setChecked(!item.isChecked());
				_filter = "";
				_refreshData();
				return true;
			case R.id.settingsButton:
				Intent intent = new Intent(getActivity(), SettingsActivity.class);
				startActivityForResult(intent, RETURN_FROM_SETTINGS);
				return true;
		}
		return false;
	}


	// refresh the data when coming back from the Settings activity
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RETURN_FROM_SETTINGS){
			if (resultCode == RESULT_OK){
				_refreshData();
			}
		}
	}
}
