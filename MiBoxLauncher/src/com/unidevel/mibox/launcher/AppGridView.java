package com.unidevel.mibox.launcher;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class AppGridView extends GridView
{

	public AppGridView( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );
	}

	public AppGridView( Context context, AttributeSet attrs )
	{
		super( context, attrs );
	}

	public AppGridView( Context context )
	{
		super( context );
	}

	//
	// @Override
	// protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
	// {
	// super.onMeasure( widthMeasureSpec, widthMeasureSpec );
	// }

	@Override
	protected void measureChildren( int widthMeasureSpec, int heightMeasureSpec )
	{
		// TODO Auto-generated method stub
		super.measureChildren( widthMeasureSpec, widthMeasureSpec );
	}
}
