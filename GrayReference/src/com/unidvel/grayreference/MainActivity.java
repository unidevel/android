
package com.unidvel.grayreference;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.GridView;

public class MainActivity extends Activity implements SurfaceHolder.Callback
{
	class Card
	{
		String name;
		int level; // 0 - 255
	}
	
	SurfaceView cardView;
	SurfaceHolder cardHolder;
	GridView cardList;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );
		this.cardList = (GridView)this.findViewById( R.id.gridView );
		this.cardView = (SurfaceView)this.findViewById( R.id.surfaceView );
		this.cardHolder = this.cardView.getHolder(); 
		this.cardHolder.addCallback( this );
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}
	
	public void drawCard(int level)
	{
		Canvas canvas = this.cardHolder.lockCanvas( null );
		int color = (level & 0xFF) | ((level << 8) & 0xFF00) | ((level << 16)& 0xFF0000) | 0xFF000000;
		canvas.drawColor( color );
		this.cardHolder.unlockCanvasAndPost( canvas );
	}
	
	public void drawGrid()
	{
		Canvas canvas = this.cardHolder.lockCanvas( null );
		float dx = ((float)canvas.getWidth())/16.0f; 
		float dy = ((float)canvas.getHeight())/16.0f;
		float x = 0.0f, y = 0.0f;
		Paint paint = new Paint();
		for ( int i = 0; i < 256; ++ i  ) {
			int color = (i & 0xFF) | ((i << 8) & 0xFF00) | ((i<< 16)& 0xFF0000) | 0xFF000000;
			paint.setColor( color );
			canvas.drawRect( x, y, x+dx, y+dy, paint );
			if ( (i+1) %16 == 0 ){
				x = 0;
				y += dy;
			}
			else {
				x += dx;
			}
		}
		this.cardHolder.unlockCanvasAndPost( canvas );		
	}
	
	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		getMenuInflater().inflate( R.menu.main, menu );
		return true;
	}

	@Override
	public void surfaceChanged( SurfaceHolder holder, int arg1, int arg2, int arg3 )
	{
		
	}

	@Override
	public void surfaceCreated( SurfaceHolder holder )
	{
		this.drawGrid();		
	}

	@Override
	public void surfaceDestroyed( SurfaceHolder holder )
	{
		
	}

}
