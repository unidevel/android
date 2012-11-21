package com.unidevel.tools.unlocker;
import android.content.*;
import android.preference.*;
import java.util.*;
import java.security.*;

public class PrefUtil
{
	Context context;
	SharedPreferences pref;
	boolean autoCommit=true;
	public PrefUtil(Context context){
		this.context = context;
		this.pref = PreferenceManager.getDefaultSharedPreferences(context);
	}
	public PrefUtil(Context context, String name){
		this(context,name,false);
	}
	public PrefUtil(Context context, String name, boolean isShared){
		this.context = context;
		int flag = Context.MODE_MULTI_PROCESS|Context.MODE_PRIVATE;
		if(isShared)
			flag=Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE|Context.MODE_MULTI_PROCESS;
		this.pref = context.getSharedPreferences(name,flag);
	}
	
	SharedPreferences.Editor editor=null;
	private SharedPreferences.Editor edit(){
		if(editor==null){
			this.editor = pref.edit();
		}
		return this.editor;
	}
	
	public PrefUtil put(String key, String value){
		edit().putString(key,value);
		internalCommit();
		return this;
	}
	
	public PrefUtil put(String key, int value){
		edit().putInt(key,value);
		internalCommit();
		return this;
	}
	
	public PrefUtil put(String key, Set<String> value){
		edit().putStringSet(key,value);
		internalCommit();
		return this;
	}
	
	public PrefUtil put(String key, long value){
		edit().putLong(key,value);
		internalCommit();
		return this;
	}
	
	public PrefUtil put(String key, boolean value){
		edit().putBoolean(key,value);
		internalCommit();
		return this;
	}
	
	public int get(String key, int defval){
		return this.pref.getInt(key,defval);
	}
	
	public long get(String key, long defval){
		return this.pref.getLong(key, defval);
	}
	
	public String get(String key){
		return this.pref.getString(key,null);
	}
	
	public Set<String> getSet(String key){
		return this.pref.getStringSet(key,null);
	}
	
	public boolean get(String key, boolean defval){
		return this.pref.getBoolean(key,defval);
	} 
	
	private void internalCommit(){
		if(autoCommit){
			this.editor.commit();
			this.editor=null;
		}
	}
	
	public void setAutoCommit(boolean value){
		this.autoCommit=value;
	}
	
	public PrefUtil commit(){
		if(editor!=null)
			editor.commit();
		editor=null;
		return this;
	}
}
