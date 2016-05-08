package com.example.android4earthquake;

import com.example.android4earthquake.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

public class PreferencesActivity extends Activity {

	public static final String USER_PREFERENCE = "USER_PREFERENCES";
	public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";
	public static final String PRE_MIN_MAG_INDEX = "PRE_MIN_MAG_INDEX";
	public static final String PRE_UPDATE_FREQ_INDEX = "PRE_UPDATE_FREQ_INDEX";

	CheckBox autoUpdate;
	Spinner updateFreqSpinner;
	Spinner magnitubeSpinner;

	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);

		updateFreqSpinner = (Spinner) findViewById(R.id.spinner_update_freq);
		magnitubeSpinner = (Spinner) findViewById(R.id.spinner_quake_mag);
		autoUpdate = (CheckBox) findViewById(R.id.checkbox_auto_update);

		populateSpinners();
		Context context = getApplicationContext();
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		updateUIFromPreferences();
		Button okButton = (Button) findViewById(R.id.okButton);
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				savePreferences();
				PreferencesActivity.this.setResult(RESULT_OK);
				finish();
			}
		});
		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PreferencesActivity.this.setResult(RESULT_CANCELED);
				finish();
			}
		});

	}

	protected void savePreferences() {
		boolean autoUpChecked = prefs.getBoolean(PREF_AUTO_UPDATE, false);
		int updateFreqIndex = prefs.getInt(PRE_UPDATE_FREQ_INDEX, 2);
		int minMagIndex = prefs.getInt(PRE_MIN_MAG_INDEX, 0);

		Editor editor = prefs.edit();
		editor.putBoolean(PREF_AUTO_UPDATE, autoUpChecked);
		editor.putInt(PRE_UPDATE_FREQ_INDEX, updateFreqIndex);
		editor.putInt(PRE_MIN_MAG_INDEX, minMagIndex);
		editor.commit();
	}

	private void updateUIFromPreferences() {
		boolean autoUpChecked = prefs.getBoolean(PREF_AUTO_UPDATE, false);
		int updateFreqIndex = prefs.getInt(PRE_UPDATE_FREQ_INDEX, 2);
		int minMagIndex = prefs.getInt(PRE_MIN_MAG_INDEX, 0);
		updateFreqSpinner.setSelection(updateFreqIndex);
		magnitubeSpinner.setSelection(minMagIndex);
		autoUpdate.setChecked(autoUpChecked);
	}

	private void populateSpinners() {
		// 填充更新频率微调框
		ArrayAdapter<CharSequence> fAdapter;
		fAdapter = ArrayAdapter.createFromResource(this, R.array.update_freq_options,
				android.R.layout.simple_spinner_dropdown_item);
		int spinner_dd_item = android.R.layout.simple_spinner_dropdown_item;
		fAdapter.setDropDownViewResource(spinner_dd_item);
		updateFreqSpinner.setAdapter(fAdapter);

		// 填充最小震级微调框
		ArrayAdapter<CharSequence> mAdapter;
		mAdapter = ArrayAdapter.createFromResource(this, R.array.magnitube_options,
				android.R.layout.simple_spinner_dropdown_item);
		mAdapter.setDropDownViewResource(spinner_dd_item);
		magnitubeSpinner.setAdapter(mAdapter);

	}
}
