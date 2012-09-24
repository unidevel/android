package com.unidevel.util;

import android.content.Context;

public class UnitUtil {
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static float px2dip(Context context, int pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (float)pxValue / scale + 0.5f;
	}
}
