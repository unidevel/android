package com.unidevel.devicemod;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.google.ads.*;
import java.io.*;
import java.util.*;

public class TestActivity extends Activity 
{
	TextView view;
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		test1();
	}

	private void test1()
	{
		this.view = new TextView(this);
		this.setContentView(view);
		List<String> outputs=new ArrayList<String>();
		try
		{
			int code=RootUtil.runWithResult("ls -l", outputs);
			this.printf("return "+code+"\n");
			this.printf("Output:\n"+outputs.get(0)+"\n");
			this.printf("Stderr:\n"+outputs.get(1)+"\n");
			this.printf("BUSYBOX:"+RootUtil.hasBusybox());
		}
		catch (Exception e)
		{}
		//this.printf(String.valueOf(RootUtil.isRooted()));
	}

	public void printf(String fmt, Object...args)
	{
		String value = String.format(fmt, args);
		this.view.append(value);
	}
}
