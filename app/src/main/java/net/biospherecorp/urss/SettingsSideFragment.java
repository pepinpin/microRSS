package net.biospherecorp.urss;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class SettingsSideFragment extends Fragment {

	private int accentColor;
	private int bkgColor;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.settings_side_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		accentColor = view.getResources().getColor(R.color.colorAccent);
		bkgColor = view.getSolidColor();

		final LinearLayout feedSettings = (LinearLayout) view.findViewById(R.id.settings_feeds_button);
		final LinearLayout browserSettings = (LinearLayout) view.findViewById(R.id.settings_browser_button);

		// if the device is a tablet, get the mainScreen's id
		// otherwise get the list's id
		//
		// to be used in the OnClickListener
		final int targetId = MainActivity.IS_WIDESCREEN ? R.id.mainScreen:R.id.list;

		feedSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				feedSettings.setBackgroundColor(accentColor);
				browserSettings.setBackgroundColor(bkgColor);

				getFragmentManager().beginTransaction()
						.replace(targetId, new SettingsFeedsFragment())
						.commit();
			}
		});

		browserSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				feedSettings.setBackgroundColor(bkgColor);
				browserSettings.setBackgroundColor(accentColor);

				getFragmentManager().beginTransaction()
						.replace(targetId, new SettingsBrowserFragment())
						.commit();

			}
		});


		// if the device is a tablet, launch the settingsFeedsFragment
		// in the mainScreen
		if (MainActivity.IS_WIDESCREEN){

			getFragmentManager().beginTransaction()
					.replace(R.id.mainScreen, new SettingsFeedsFragment())
					.commit();

			// set the background color of the feeds section "button"
			feedSettings.setBackgroundColor(accentColor);
		}
	}
}
