package net.biospherecorp.urss;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

// Adapter for the recyclerView located in the SettingsFeedsFragment
// nothing special here
class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.FeedViewHolder> {

	// Doesn't use it's own instantiation of an ArrayList,
	// uses the Feeds one instead, passed by the constructor
	private static List<OneFeed> LIST;
	private Activity _activity;

	FeedsAdapter(Activity activity, ArrayList<OneFeed> list) {
		_activity = activity;
		LIST = list;
	}

	@Override
	public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View view = inflater.inflate(R.layout.settings_cell,parent,false);

		return new FeedViewHolder(this, view);
	}

	@Override
	public void onBindViewHolder(FeedViewHolder holder, int position) {
		OneFeed cell = LIST.get(position);
		holder.configure(cell);
	}

	@Override
	public int getItemCount() {
		return LIST.size();
	}

	class FeedViewHolder extends RecyclerView.ViewHolder{

		private OneFeed _currentFeed;
		private TextView _name, _url;

		FeedViewHolder(final FeedsAdapter adapter, View view) {
			super(view);

			_name = (TextView) view.findViewById(R.id.feedName);
			_url = (TextView) view.findViewById(R.id.feedUrl);

			// This OnclickListener will only be triggered when editing a feed
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					// Inflate the layout that will be used for the AlertDialog
					final View dialogView = LayoutInflater.from(_activity).inflate(R.layout.settings_add_edit_dialog, null);

					// Retrieves the Url TextView from the AlertDialog
					final EditText urlTextView = (EditText) dialogView.findViewById(R.id.urlEditText);

					// Retrieves the Name TextView from the AlertDialog
					final EditText nameTextView = (EditText) dialogView.findViewById(R.id.nameEditText);

					// Store the current url value from the OneFeed Object
					// retrieved from the List
					final String oldUrl = _currentFeed.getUrl();

					// Store the current name value from the OneFeed Object
					// retrieved from the List
					final String oldName = _currentFeed.getName();

					// Sets the EditText
					urlTextView.setText(oldUrl);

					// Sets the EditText
					nameTextView.setText(oldName);

					// Instantiate a Dialog Builder
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(_activity);

					// Sets up the AlertDialog
					alertDialog.setView(dialogView)
							//sets the title
							.setTitle(R.string.edit_feed_title)
							// sets the message
							.setMessage(R.string.edit_feed_message)
							// sets the OK button
							.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {

									// Retrieves the new url value from the EditText view
									String url = urlTextView.getText().toString();

									// If the url is valid
									if (Feeds.isValid(url)){
										// Retrieve the new name value from the EditText view
										String name = nameTextView.getText().toString();
										// Check the new name value to see if it's empty
										// if it is, store an empty string "" in the name variable
										name = name.isEmpty() ?"":name;
										// Update the feed
										Feeds.update(_activity, adapter, _currentFeed, new OneFeed(url, name));

									// If the url IS NOT valid
									}else{
										// Warn the User that the url isn't valid,
										// and close the AlertDialog
										Toast.makeText(_activity, R.string.valid_url_toast, Toast.LENGTH_LONG).show();
									}
								}
							})
							// sets the Cancel button
							.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									//just close the AlertDialog and do nothing
								}
							})
							// sets the Delete button
							.setNeutralButton(R.string.delete_feed_message, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {

									// removes the current feed from the Feed list
									// and from the SharedPreferences
									Feeds.remove(_activity,adapter, _currentFeed);
								}
							})
							// displays the AlertDialog
							.show();
				}
			});
		}

		// Called by onBindViewHolder
		void configure(OneFeed currentCell){

			// stores the current feed
			_currentFeed = currentCell;

			// sets the name TextView in the ViewHolder
			// with the current feed name
			_name.setText(_currentFeed.getName());

			// sets the url TextView in the ViewHolder
			// with the current feed url
			_url.setText(_currentFeed.getUrl());
		}
	}
}
