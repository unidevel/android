package com.unidevel.tools.unlocker;

import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;

public class ViewUtil
{
	ViewGroup root;
	Context context;
	public ViewUtil(ViewGroup root){
		this.root=root;
		this.context=root.getContext();
	}
	
	public ViewUtil(Activity activity){
		this.context=activity;
	}
	
	public LinearView addScrollView(boolean vertical){
		ScrollView view = new ScrollView(context);
		if(root==null){
			((Activity)context).setContentView(view);
			root=view;
		}
		else
			root.addView(view);
		return addLinearLayout(vertical);
	}
	
	public LinearView addLinearLayout(boolean vertical){
		int gravity=Gravity.CENTER;
		if(vertical)
			gravity=Gravity.NO_GRAVITY;
		else
			gravity=Gravity.CENTER_VERTICAL;		
		return addLinearLayout(vertical,gravity);
	}
	
	public LinearView addLinearLayout(boolean vertical, int gravity){
		LinearLayout layout=new LinearLayout(context);
		layout.setGravity(gravity);
		layout.setOrientation(vertical?LinearLayout.VERTICAL:LinearLayout.HORIZONTAL);
		if(root==null){
			((Activity)context).setContentView(layout);
			root=layout;
		}
		else
			root.addView(layout);
		LinearView view = new LinearView(layout);
		view.parent=this;
		return view;
	}
	
	public class LinearView extends ViewUtil {
		LinearLayout layout;
		ViewUtil parent;
		public LinearView(LinearLayout layout){
			super(layout);
			this.layout=layout;
		}
		
		public LinearView addChild(View child,int w,int h){
			LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(w,h);
			this.layout.addView(child,param);
			return this;
		}
		
		public LinearView addChild(View child,int v,boolean fill){
			int w,h;
			if(layout.getOrientation()==LinearLayout.HORIZONTAL){
				if(fill)h=LinearLayout.LayoutParams.MATCH_PARENT;
				else h=LinearLayout.LayoutParams.WRAP_CONTENT;
				w=v;
			}
			else{
				if(fill)w=LinearLayout.LayoutParams.MATCH_PARENT;
				else w=LinearLayout.LayoutParams.WRAP_CONTENT;
				h=v;				
			}
			return addChild(child,w,h);
		}
		
		public LinearView addChild(View child){
			return addChild(child,
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		}
		
		public LinearLayout layout(){
			return layout;
		}
		
		public ViewUtil parent(){
			return parent;
		}
	}
}
