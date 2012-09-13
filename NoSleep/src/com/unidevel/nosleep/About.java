package com.unidevel.nosleep;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.admob.android.ads.AdManager;

public class About extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
        TextView text = (TextView)findViewById(R.id.labelLink);
        text.setText(Html.fromHtml(getString(R.string.about)));
        text.setClickable(true);
        text.setLinksClickable(true);
        text.setMovementMethod(LinkMovementMethod.getInstance());
        
        AdManager.setPublisherId("a14cb24a873b927");
//		AdManager.setTestDevices(new String[] { AdManager.TEST_EMULATOR, // Android
//		// emulator
//				"E83D20734F72FB3108F104ABC0FFC738", // My T-Mobile G1 Test Phone
//		});
	}
}
