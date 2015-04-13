package com.unidevel.alibtest;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.unidevel.widget.AppListView;

public class MainActivity extends FragmentActivity
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
