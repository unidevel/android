
package com.unidevel.sharetomm;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class SendActivity extends Activity implements OnClickListener
{
	static final String MMPKG = "com.tencent.mm";
	static final String MMCLS = "com.tencent.mm.ui.tools.ShareToTimeLineUI";
	boolean showDialog = false;
	Intent shareIntent;
	protected void isShowDialog()
	{
		Random rand = new Random();
		rand.setSeed( System.currentTimeMillis() );
		float f = rand.nextFloat();
		if ( f > 0.80f ) this.showDialog = true;
		else this.showDialog = false;
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );
		Button button = (Button)this.findViewById( R.id.btn_share );
		button.setOnClickListener( this );
		this.isShowDialog();
		Intent intent = getIntent();
		if( intent.getAction() == Intent.ACTION_VIEW ) 
		{
			Uri uri = intent.getData();
			if ( uri != null )
			{
				share(uri.toString(), intent.getStringExtra( Intent.EXTRA_SUBJECT ));
			}
		}
		else if( intent.getAction() == Intent.ACTION_SEND ) 
		{
			String text=intent.getStringExtra(Intent.EXTRA_TEXT);
			share(text, intent.getStringExtra( Intent.EXTRA_SUBJECT ));
		}
		if ( this.showDialog )
		{
			AdView adView = new AdView( this, AdSize.BANNER, "a15295781f962ce" ); //$NON-NLS-1$
			LinearLayout layout = (LinearLayout)findViewById( R.id.adLayout );
			layout.addView( adView );
			AdRequest req = new AdRequest();
			adView.loadAd( req );
		}
	}
		
	public void share( String shareContent, String subject )
	{
		Intent intent = new Intent( Intent.ACTION_SEND );
		intent.setType( "image/*" );
		String shareText = shareContent;
		if ( subject != null )
		{
			if (! shareContent.contains( subject ) )
			{
				shareText = subject +"\n"+shareContent;
			}
		}

		File f=createBitmap(shareText);
		Uri u = Uri.fromFile(f);
		intent.putExtra( Intent.EXTRA_STREAM, u);
		f.deleteOnExit();
		
		intent.putExtra( Intent.EXTRA_TEXT, shareText );
		intent.putExtra( "Kdescription", shareText );
		
		//intent.putExtra( "Ksnsupload_empty_img", true );
		intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		intent.setClassName( MMPKG, MMCLS );
		//startActivity( Intent.createChooser( intent, getTitle() ) );
		this.shareIntent = intent;
		if ( !this.showDialog )
		{
			try
			{
				startActivity(intent);
			}
			catch(Throwable ex){
				failedOpenMM();
			}
			this.finish();
		}
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
	
	@SuppressLint ("NewApi")
	private File createBitmap( String text )
	{
		text += "\n\n"+getString(R.string.app_logo);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2 )
		{
			display.getSize(size);
		}
		else
		{
			size.x = display.getWidth();
			size.y = display.getHeight(); 
		}
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

	private void failedOpenMM()
	{
		Toast.makeText( this, getString(R.string.install_mm), Toast.LENGTH_LONG ).show();
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+MMPKG));
		try { 
			startActivity(intent); 
		}
		catch(Throwable ex){}
	}
	
	@Override
	public void onClick( View v )
	{
		try 
		{
			this.startActivity( this.shareIntent );
		}
		catch(Throwable ex)
		{
			failedOpenMM();
		}
	}
}
