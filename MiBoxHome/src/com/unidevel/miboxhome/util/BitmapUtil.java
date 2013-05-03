package com.unidevel.miboxhome.util;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import com.unidevel.miboxhome.*;
import java.io.*;

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
	
	public static Bitmap getBotmap(Context context, int resId){
		Resources res= context.getResources(); 

		Bitmap bitmap=BitmapFactory.decodeResource(res, resId); 
		return bitmap;
	}

	public static void save(Bitmap bitmap, OutputStream out){
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); 
	}
}
