package net.biospherecorp.urss;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

// This fragment takes care of all the settings
public class SettingsFeedsFragment extends Fragment {
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Inflates the view
		return inflater.inflate(R.layout.settings_feeds_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// instantiate a feed adapter
		final FeedsAdapter _adapter = new FeedsAdapter(getActivity(), Feeds.LIST);

		// Sets up the recycler view
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.feedsRecyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.setAdapter(_adapter);

		// gets the add button from the view
		LinearLayout addButton = (LinearLayout) view.findViewById(R.id.add_feeds_button);

		// gets the delete button from the view
		Button deleteAllButton = (Button) view.findViewById(R.id.deleteAllButton);

		// on click "Add a Feed" button
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {

				// Inflates the view for the "Add a Feed" AlertDialog
				final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.settings_add_edit_dialog, null);

				// Build and sets up the AlertDialog
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
				alertDialog.setView(dialogView);

				// Sets up the AlertDialog
				alertDialog.setView(dialogView)
						//sets the title
						.setTitle(R.string.add_feed_title)
						// sets the message
						.setMessage(R.string.add_feed_message)
						// sets up the OK buttons
						.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {

								// get the EditText views
								EditText urlTextView = (EditText) dialogView.findViewById(R.id.urlEditText);
								EditText nameTextView = (EditText) dialogView.findViewById(R.id.nameEditText);

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
									Feeds.add(getActivity(), _adapter, url, name);

									// If the url IS NOT valid
								}else{
									// Warn the User that the url isn't valid,
									// and close the AlertDialog
									Toast.makeText(getActivity(), R.string.valid_url_toast, Toast.LENGTH_LONG).show();
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
						// displays the AlertDialog
						.show();
			}
		});

		// on click "Delete ALL Feeds" button
		deleteAllButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Sets up the AlertDialog "Are you sure ...?"
				new AlertDialog.Builder(getActivity())
					.setTitle(R.string.delete_all_feeds_title)
					.setMessage(R.string.delete_all_feeds_message)
					// the NO button
					.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							// do nothing
						}
					})
					// The YES button
					.setNeutralButton(R.string.yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {

							// delete all the feeds from the SharedPreferences
							// and the Feeds list
							Feeds.clearAll(getActivity(), _adapter);
							Toast.makeText(getActivity(), R.string.delete_all_feeds_toast_cleared, Toast.LENGTH_SHORT).show();
						}
					})
					.show();
			}
		});
	}
}
