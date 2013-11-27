
package com.unidevel.sharetomm;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.Paint.Align;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import java.io.*;
import java.util.*;

public class SendActivity extends Activity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		Intent intent = getIntent();
		String p="/sdcard/DCIM/1.jpg";
		if( intent.getAction() == Intent.ACTION_VIEW ) 
		{
			Uri uri = intent.getData();
			share(uri.toString(), null);
		}
		else if( intent.getAction() == Intent.ACTION_SEND ) 
		{
			String text=intent.getStringExtra(Intent.EXTRA_TEXT);
			share(text, null);
		}
	}
		
	public void share( String shareContent, String imgPath )
	{
		Intent intent = new Intent( Intent.ACTION_SEND );
		if ( imgPath == null || imgPath.equals( "" ) )
		{
//			intent.setType( "text/plain" );
			intent.setType( "image/*" );
			File f=createBitmap(shareContent);
			Uri u = Uri.fromFile(f);
			intent.putExtra( Intent.EXTRA_STREAM, u);
			f.deleteOnExit();
		}
		else
		{
			File f = new File( imgPath );
			if ( f != null && f.exists() && f.isFile() )
			{
				intent.setType( "image/*" );
				Uri u = Uri.fromFile( f );
				intent.putExtra( Intent.EXTRA_STREAM, u );
			}
		}
		intent.putExtra( Intent.EXTRA_SUBJECT, "Share" );
		intent.putExtra( Intent.EXTRA_TEXT, shareContent );
		intent.putExtra( "Kdescription", shareContent );
		
		//intent.putExtra( "Ksnsupload_empty_img", true );
		intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		intent.setClassName( "com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI" );
		//startActivity( Intent.createChooser( intent, getTitle() ) );
		startActivity(intent);
		this.finish();
	}

	private File getPictureDir()
	{
		File d = new File(Environment.getExternalStorageDirectory(), "ShareToMM");
		d.mkdirs();
		File[] files = d.listFiles();
		for (File f: files)
		{
			f.delete();
		}
		return d;
	}
	
	private File createBitmap( String text )
	{
		text += "\n\n"+getString(R.string.app_logo);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int maxW = (int)((float)size.x/metrics.scaledDensity);
		float fontSize = 16;
		Paint p=new Paint();
		p.setTextSize(fontSize);
		p.setSubpixelText(true);
		p.setTextAlign( Align.LEFT );
		p.setAntiAlias( true );
		Typeface tf = Typeface.defaultFromStyle( Typeface.NORMAL );
		p.setTypeface(tf);
		
		List<String> l=new ArrayList<String>();
		int p1=0;
		int p2=text.length();
		int h=2;
		do{
			int n = p.breakText(text, p1,p2,true,maxW,null);
			if (n>0){
				String s=text.substring(p1,p1+n);
				int p3 = s.indexOf( '\n' );
				if ( p3 >= 0 )
				{
					s=s.substring(0,p3); 
					l.add(s);
					p1 += p3+1;
				}
				else
				{
					l.add(s);
					p1+=n;
				}
				h+=fontSize+2;
			}
			else break;
		}while(p1<p2);

		Bitmap localBitmap = Bitmap.createBitmap( maxW, h, Bitmap.Config.RGB_565 );
		Canvas c = new Canvas( localBitmap );
		{
			c.drawColor( 0xFFFFFFFF );
			float y=fontSize;
			for(String s:l){
				c.drawText(s,0,y,p);
				y+=fontSize+2;
			}
			File d = this.getPictureDir();
			File f=new File(d,""+System.currentTimeMillis()+".jpg");
			f.getParentFile().mkdirs();
			try
			{
				FileOutputStream os = new FileOutputStream(f);
				localBitmap.compress( Bitmap.CompressFormat.JPEG, 100, os);
				localBitmap.recycle();
				os.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return f;
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
}
