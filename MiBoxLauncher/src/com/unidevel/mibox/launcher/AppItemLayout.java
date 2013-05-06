package com.unidevel.mibox.launcher;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class AppItemLayout extends LinearLayout
{
	public AppItemLayout( Context context, AttributeSet attrs )
	{
		super( context, attrs );
	}

	public AppItemLayout( Context context )
	{
		super( context );
	}

	@TargetApi (Build.VERSION_CODES.HONEYCOMB)
	public AppItemLayout( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );
	}

	@Override
	protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
	{
		super.onMeasure( widthMeasureSpec, widthMeasureSpec );
	}
}
