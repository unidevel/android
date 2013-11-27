
package com.unidvel.grayreference;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import com.jess.ui.TwoWayGridView;
import com.jess.ui.TwoWayGridView.OnItemClickListener;
import android.widget.*;
import com.jess.ui.*;

public class MainActivity extends Activity implements SurfaceHolder.Callback,OnItemClickListener
{

	public void onItemClick(TwoWayAdapterView<?> parent, View view, int position, long id)
	{
		// TODO: Implement this method
	}
	
	SurfaceView cardView;
	SurfaceHolder cardHolder;
	TwoWayGridView cardList;
	CardAdapter adapter;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );
		View view = this.findViewById( R.id.gridView );
		this.cardList = (TwoWayGridView)view;
		this.adapter = new CardAdapter(this.cardList);
		this.cardList.setAdapter( this.adapter );
		this.cardList.setOnItemClickListener(this);
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
	
	public void drawGrid(int block)
	{
		Canvas canvas = this.cardHolder.lockCanvas( null );
		float dx = ((float)canvas.getWidth())/(float)block; 
		float dy = ((float)canvas.getHeight())/(float)block;
		float x = 0.0f, y = 0.0f;
		Paint linePaint = new Paint();
		linePaint.setColor( Color.BLACK );
		linePaint.setAntiAlias( false );
		linePaint.setStrokeWidth( 1.0f );
		linePaint.setStyle( Style.STROKE );
		linePaint.setPathEffect( new DashPathEffect( new float[]{10,20}, 0 ) );

		Paint paint = new Paint();
		int count = block * block;
		float dl = 256.0f/(float)count;
		float level = 0;
		for ( int i = 0; i < count; ++ i  ) {
			int l = (int)level;
			int color = (l & 0xFF) | ((l << 8) & 0xFF00) | ((l<< 16)& 0xFF0000) | 0xFF000000;
			level += dl;
			paint.setColor( color );
			canvas.drawRect( x, y, x+dx, y+dy, paint );
			if ( (i+1) %block == 0 ){
				x = 0;
				y += dy;
			}
			else {
				x += dx;
			}
		}
		x = dx; y = dy;
		float w = canvas.getWidth();
		float h = canvas.getHeight();
		for ( int i = 0; i < block-1; ++ i ) 
		{
			canvas.drawLine( 0, y, w, y, linePaint );
			canvas.drawLine( x, 0, x, h, linePaint );
			x+=dx; y+=dy;
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
		this.drawGrid(4);		
	}

	@Override
	public void surfaceDestroyed( SurfaceHolder holder )
	{
		
	}

}
