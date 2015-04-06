package com.unidevel.alibtest;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.unidevel.widget.*;

public class MainActivity extends Activity
{
	AppListView list;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		this.list = (AppListView) this.findViewById(R.id.listView);
		this.list.listApp();
    }
}
