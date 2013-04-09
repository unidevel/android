
package com.unidevel.unshorturl;

import android.graphics.drawable.Drawable;

public class AppInfo
{
	public Drawable icon;
	public String packageName;
	public String name;
	public String label;
	public boolean selected;

	public AppInfo()
	{
		this.label = ""; //$NON-NLS-1$
		this.selected = false;
	}
}
