package com.unidevel.logo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

public class Editor extends Activity{
	static final int MENU_NEW=Menu.FIRST+1;
	static final int MENU_RUN=Menu.FIRST+2;
	static final int MENU_SAVE=Menu.FIRST+3;
	static final int MENU_DELETE=Menu.FIRST+4;
	public static final String SCRIPT="scirpt";
	public static final String FILE="file";
	public static final String LOGO_EXT = ".lgo";
	protected static final CharSequence TITLE_NEW_SCRIPT = "New Script";
	MultiAutoCompleteTextView textScript; 
	AlertDialog alertQuitDialog;
	AlertDialog alertClearDialog;
	AlertDialog inputPathDialog;
	SharedPreferences prefs;
	File file;
	boolean modified;
	Object mappings[][] = new Object[][]{
			{R.id.btnCOLON, " :"}, 
			{R.id.btnFD, "FD "}, 
			{R.id.btnLBRACE, "[ "}, 
			{R.id.btnRBRACE, "] "}, 
			{R.id.btnNL, "\n"}, 
			{R.id.btnQUOTE, " \""}, 
			{R.id.btnRT, "RT "},
			{R.id.btnREPEAT, "REPEAT "}, 
			{R.id.btnTO, "TO  END"}, 
	};
	
	void saveFile(File file){
		Writer out = null;
		try {
			out = new FileWriter(file);
			out.write(textScript.getText().toString());
			modified = false;
		}
		catch(Throwable ex){
			
		}
		finally {
			try { out.close(); } catch(Throwable ex){}
		}
	}
	
	String loadFile(File file){
		StringBuffer buf = new StringBuffer();
		Reader in = null;
		char data[] = new char[1024];
		try {
			in = new BufferedReader(new FileReader(file));
			int len = in.read(data);
			while ( len > 0 ) {
				buf.append(data, 0, len);
				len = in.read(data);
			}
		} catch (Exception e) {
			Log.e("loadFile", e.getMessage(), e);
		} finally {
			try { in.close(); } catch(Throwable ex){}
		}
		return buf.toString();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editor);
		textScript = (MultiAutoCompleteTextView)findViewById(R.id.textScript);
		textScript.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if ( !modified ) {
					modified = true;
					setTitle(getTitle()+"*");
				}
			}
			
		});
		prefs= this.getPreferences(MODE_PRIVATE);
		String path = getIntent().getStringExtra(FILE);
		
		if ( path!= null && path.length() > 0 ) {
			file = new File(path);
			textScript.setText(loadFile(file));
			setTitle(file.getName());
			modified = false;
		}
		else {
			file = null;
			String script = prefs.getString(SCRIPT, "");
			if ( script != null && script.length() > 0 ) {
				prefs.edit().remove(SCRIPT).commit();
				setTitle(TITLE_NEW_SCRIPT);
				textScript.setText(script);			
				modified = true;
			}
			else {
				textScript.setText(script);			
				setTitle(TITLE_NEW_SCRIPT);
				modified = false;
			}
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.msgQuit))
			.setPositiveButton(getString(R.string.btnSave), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int id) {
//					dialog.dismiss();
//					Editor.this.finish();
					dialog.dismiss();
					if ( file != null ) saveFile(file);
					else inputPathDialog.show();
				}
			})
			.setNegativeButton(getString(R.string.btnQuit), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					Editor.this.finish();
				}
			});
		alertQuitDialog = builder.create();
		
		builder.setMessage(getString(R.string.msgClearScript))
			.setPositiveButton(getString(R.string.btnYes), new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				file = null;
				textScript.setText("");
				setTitle(TITLE_NEW_SCRIPT);
				modified = false;
			}
		})
		.setNegativeButton(getString(R.string.btnNo), new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		alertClearDialog = builder.create();
		
		for ( int i = 0; i < mappings.length; ++ i ) {
			Object[] pair = mappings[i];
			final int id = (Integer)pair[0]; 
			final String text = (String)pair[1]; 
			Button button = (Button)findViewById(id);
			button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					textScript.getEditableText().replace(textScript.getSelectionStart(), textScript.getSelectionEnd(), text);
				}
				
			});
		}
		
		builder = new AlertDialog.Builder(this);  
		  
		builder.setTitle(getString(R.string.msgSaveTitle));  
		builder.setMessage(R.string.msgSaveName);  
		final EditText input = new EditText(this);  
		builder.setView(input);  
		  
		builder.setPositiveButton(getString(R.string.btnOk), new DialogInterface.OnClickListener() {  
		public void onClick(DialogInterface dialog, int whichButton) {  
			String value = input.getText().toString();
			if ( !value.endsWith(LOGO_EXT )) {
				value += LOGO_EXT;
			}
			file = new File(getFilesDir(), value);
			if ( file.exists() ) { 
				dialog.dismiss();
				Toast.makeText(inputPathDialog.getContext(), getString(R.string.msgExists), Toast.LENGTH_SHORT).show();
			}
			else {
				saveFile(file);
				Editor.this.setTitle(file.getName());
				dialog.dismiss();
			}
		  }  
		});  
		builder.setNegativeButton(getString(R.string.btnCancel), new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {  
				dialog.dismiss();
			}  
		});  
		inputPathDialog = builder.create();
//		AdManager.setTestDevices( new String[] {                  
//			      AdManager.TEST_EMULATOR,             // Android emulator 
//			      "E83D20734F72FB3108F104ABC0FFC738",  // My T-Mobile G1 Test Phone 
//		});		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuItem item = menu.add(0, MENU_RUN, Menu.NONE, getString(R.string.menuRun));
		item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(Editor.this, LogoConsole.class);
				intent.putExtra(SCRIPT, textScript.getText().toString());
				startActivity(intent);
				return true;
			}
		});
		
		item = menu.add(0, MENU_NEW, Menu.NONE, getString(R.string.menuNew));
		item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if ( !modified && file == null ) {return true; }
				alertClearDialog.show();
				return true;
			}
		});
		
		item = menu.add(0, MENU_SAVE, Menu.NONE, getString(R.string.menuSave));
		item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if ( file == null ) {
					inputPathDialog.show();
				}
				else saveFile(file);
				return true;
			}
		});
		
		if ( file != null ){
			item = menu.add(0, MENU_DELETE, Menu.NONE, getString(R.string.menuDelete));
			item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					file.delete();
					Editor.this.finish();
					return true;
				}
			});			
		}
		return true;
	}
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if ( modified ) {
				alertQuitDialog.show();
				return false;
			}
			else return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}
	
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		outState.putCharSequence(SCRIPT, textScript.getText());
//	}
//	
//	@Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//		super.onRestoreInstanceState(savedInstanceState);
//		CharSequence savedScript = savedInstanceState.getCharSequence(SCRIPT);
//		if ( savedScript != null ){
//			textScript.setText(savedScript);
//		}
//	}
	
//	@Override
//	protected void onPause() {
//		super.onPause();
//		SharedPreferences.Editor edit = prefs.edit();
//		edit.putString(SCRIPT, textScript.getText().toString());
//		edit.commit();
//	}
	
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		super.onConfigurationChanged(newConfig);
//		if ( newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_NO || newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_YES ){
//			System.err.println(newConfig.keyboardHidden);
//		}
//	}
}
