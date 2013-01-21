package com.unidevel.bmnhticket;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.text.*;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		TextView text=(TextView)this.findViewById(R.id.hello);
		text.setMarqueeRepeatLimit(-1);
		text.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		text.setSelected(true);
		text.setText("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvfvvvvvvvvvvvvvvgvggggggggvvgvvvv vvvv");
		text.setSingleLine(true);
    }
}
