package com.unidevel.tochrome;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class LinkAdapter extends ArrayAdapter<String> {
	public LinkAdapter(Context context, int textViewResourceId,
			List<String> objects) {
		super(context, textViewResourceId, objects);
	}
}
