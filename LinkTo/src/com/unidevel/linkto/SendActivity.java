
package com.unidevel.linkto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

public class SendActivity extends Activity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		Intent intent = getIntent();
		if( intent.getAction() == Intent.ACTION_VIEW ) 
		{
			Uri uri = intent.getData();
			share(uri.toString(), null);
		}
	}
	
	public void share( String shareContent, String imgPath )
	{
		Intent intent = new Intent( Intent.ACTION_SEND );
		if ( imgPath == null || imgPath.equals( "" ) )
		{
//			intent.setType( "text/plain" );
			intent.setType( "image/*" );
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
		
		intent.putExtra( "Ksnsupload_empty_img", true );
		intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		startActivity( Intent.createChooser( intent, getTitle() ) );
	}

	private static byte[] getBitmapBytes( Bitmap bitmap, boolean paramBoolean )
	{
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Bitmap localBitmap = Bitmap.createBitmap( w, h, Bitmap.Config.RGB_565 );
		Canvas localCanvas = new Canvas( localBitmap );
		{
			localCanvas.drawBitmap( bitmap, new Rect( 0, 0, w, h ), new Rect( 0, 0, w, h ), null );
			if ( paramBoolean )
				bitmap.recycle();
			ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
			localBitmap.compress( Bitmap.CompressFormat.JPEG, 100, localByteArrayOutputStream );
			localBitmap.recycle();
			byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
			try
			{
				localByteArrayOutputStream.close();
				return arrayOfByte;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
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
