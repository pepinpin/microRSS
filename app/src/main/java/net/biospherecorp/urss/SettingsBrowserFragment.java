package net.biospherecorp.urss;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsBrowserFragment extends Fragment {
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.settings_browser_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Switch jsSwitch = (Switch) view.findViewById(R.id.activate_JS_switch);

		// set the toggle switch state according to the
		// value of the SharedPreferences
		jsSwitch.setChecked(MainActivity.IS_JAVASCRIPT_ENABLED);

		jsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
				if(isChecked){
					getSharedPreferences().edit().putBoolean("activateJS", true).apply();
				}else{
					getSharedPreferences().edit().putBoolean("activateJS", false).apply();
				}
			}
		});
	}

	private SharedPreferences getSharedPreferences(){

		return PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
	}
}
