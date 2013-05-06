package com.unidevel.mibox.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class BitmapUtil
{
	public static Bitmap getBitmap(Drawable d){
		Bitmap bitmap=null;
		if (d instanceof BitmapDrawable){
			bitmap = ((BitmapDrawable)d).getBitmap();
		}
		else {
			Bitmap.Config config=d.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 
				: Bitmap.Config.RGB_565;
			bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), config);
			Canvas canvas = new Canvas(bitmap); 
			d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			d.draw(canvas);

			return bitmap;
		}
		return bitmap;
	}
	
	public static Bitmap getBitmap(Context context, int resId){
		Resources res= context.getResources(); 

		Bitmap bitmap=BitmapFactory.decodeResource(res, resId); 
		return bitmap;
	}

	public static void save(Bitmap bitmap, OutputStream out){
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); 
	}

	public static byte[] toBytes( Drawable drwable, int ratio )
	{
		Bitmap bitmap = getBitmap( drwable );
		if ( bitmap == null )
			return null;
		return toBytes( bitmap, ratio );
	}
	
	public static byte[] toBytes( Bitmap bitmap, int ratio )
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress( Bitmap.CompressFormat.PNG, ratio, out );
		return out.toByteArray();
	}

	public static Bitmap toBitmap( byte[] data )
	{
		if ( data != null )
		{
			return BitmapFactory.decodeByteArray( data, 0, data.length );
		}
		return null;
	}

	public static Drawable toDrawable( Bitmap bitmap )
	{
		if ( bitmap == null )
			return null;
		return new BitmapDrawable( bitmap );
	}

	public static Drawable toDrawable( byte[] data )
	{
		Bitmap bitmap = toBitmap( data );
		return toDrawable( bitmap );
	}
}
