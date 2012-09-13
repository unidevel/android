package com.unidevel.logo;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.Writer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FileList extends Activity {
    /** Called when the activity is first created. */
	TextView titleView;
	FileListAdapter adapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filelist);
        Button button;
        button = (Button)findViewById(R.id.btnNew);
        button.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v) {
        		Intent intent = new Intent();
        		intent.setClass(FileList.this, Editor.class);
        		startActivity(intent);
        	}
        });
        
        /* 
        button = (Button)findViewById(R.id.btnLast);
        button.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v) {
        		Intent intent = new Intent();
        		intent.setClass(FileList.this, Editor.class);
        		startActivity(intent);
        	}
        });
        */
        
        ListView list = (ListView)findViewById(R.id.fileList);
        adapter = new FileListAdapter(getFilesDir());
        list.setAdapter(adapter);
        
        list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> view, View itemView, int position,
					long time) {
				Intent intent = new Intent();
        		intent.setClass(FileList.this, Editor.class);
        		intent.putExtra(Editor.FILE, adapter.getFile(position).getPath());
        		startActivity(intent);
			}
        });
    }
    
    public class FileListAdapter extends BaseAdapter {
    	File dir;
    	File[] files;
    	LayoutInflater inflater;
    	public File getFile(int position){
    		return files[position];
    	}
    	
    	public FileListAdapter(File dir){
    		this.dir = dir;
    		inflater = LayoutInflater.from(FileList.this);
    		Log.d("FileListAdapter", "Dir="+dir);
    		refresh();
    	}
    	
    	private void saveFile(String name, String s){
    		Writer out = null;
    		try {
    			out = new FileWriter(new File(getFilesDir(), name));
    			out.write(s);
    		}
    		catch(Throwable ex){
    			
    		}
    		finally {
    			try { out.close(); } catch(Throwable ex){}
    		}
    	}
    	
    	public void refresh(){
    		files = dir.listFiles(new FileFilter(){
				@Override
				public boolean accept(File file) {
					if ( file.isFile() && file.getName().endsWith(".lgo") ) {
						return true;
					}
					return false;
				}
    		});
    		if ( files == null || files.length == 0 ) {
    			saveFile("rect.lgo", "repeat 4 [ fd 100 rt 90 ]");
    			saveFile("spiral.lgo", "to spiral :size :angle if :size > 160 [stop] forward :size right :angle spiral sum :size 2 :angle end\n spiral 0 91");
    		}
    		files = dir.listFiles(new FileFilter(){
				@Override
				public boolean accept(File file) {
					if ( file.isFile() && file.getName().endsWith(".lgo") ) {
						return true;
					}
					return false;
				}
    		});
    	}
    	
		@Override
		public int getCount() {
			return files.length;
		}

		@Override
		public Object getItem(int position) {
			return files[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView view;
			if ( convertView == null ) {
				convertView = inflater.inflate(R.layout.fileitem, null);
				view = (TextView)convertView.findViewById(R.id.labelName);
				view.setText(files[position].getName());
				view = (TextView)convertView.findViewById(R.id.labelSize);
				view.setText(String.valueOf(files[position].length())+"b");
			}
			else {
				view = (TextView)convertView.findViewById(R.id.labelName);
				view.setText(files[position].getName());
				view = (TextView)convertView.findViewById(R.id.labelSize);
				view.setText(String.valueOf(files[position].length())+"b");
			}
			return convertView;
		}
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	adapter.refresh();
    	adapter.notifyDataSetChanged();
    }
}