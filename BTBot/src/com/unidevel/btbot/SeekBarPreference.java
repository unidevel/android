package com.unidevel.btbot;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SeekBarPreference extends Preference {
	SeekBar seekBar;
	TextView titleView;
	int max;
	int progress;
	public SeekBarPreference(Context context) {
		super(context);
		setLayoutResource(R.layout.seekbar_pref);
	}

	public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setLayoutResource(R.layout.seekbar_pref);
	}

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setLayoutResource(R.layout.seekbar_pref);
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		seekBar=(SeekBar)view.findViewById(R.id.seekBar);
		titleView = (TextView)view.findViewById(R.id.text);
		titleView.setText(getTitle());
		seekBar.setMax(getMax());
		seekBar.setProgress(getProgress());
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekbar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekbar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekbar, int progress, boolean flag) {
				setProgress(progress);
				callChangeListener(progress);
			}
		});
	}
	
	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		if( titleView != null) titleView.setText(title); 
	}
	
	@Override
	public void setTitle(int titleResId) {
		super.setTitle(titleResId);
		if( titleView != null) titleView.setText(titleResId);
	}
	
	public void setProgress(int progress){
		this.progress = progress;
		persistInt(progress);
		if ( seekBar != null ) {
			seekBar.setProgress(progress);
		}
	}
	
	public int getProgress(){
		return progress;
	}

	public void setMax(int max){
		this.max = max;
		if ( seekBar != null ) {
			seekBar.setMax(max);
		}
	}

	public int getMax(){
		return max;
	}
	
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue,
			Object defaultValue) {
		super.onSetInitialValue(restorePersistedValue, defaultValue);
		if ( restorePersistedValue ) {
			int progress = getPersistedInt(0);
			seekBar.setProgress(progress);
		}
		else {
			int progress = defaultValue==null?0:(Integer)defaultValue;
			seekBar.setProgress(progress);			
		}
	}
	
	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getInt(index, 0);
	}
}

