package com.unidevel.unshorturl;

import android.net.*;
import android.os.*;

public class ToChromeBeta extends MainActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Uri uri = getIntent().getData();
		if (uri != null) {
			addLink(this, uri.toString());
			startChromeBeta(uri);
			this.finish();
			return;
		}
	}
	
	public void startChromeBeta(Uri uri) {
		startChromeInternal("com.chrome.beta",
							"com.google.android.apps.chrome.Main",uri);
	}
	
}

