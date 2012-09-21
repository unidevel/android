package com.unidevel.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class DialogUtil {
	private Context context;
	private final String TAG_ERROR="ERROR";
	private final CancelCallback cancelCallback = new CancelCallback();

	class CancelCallback implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener {

		@Override
		public void onCancel(DialogInterface dialog) {
			dialog.dismiss();
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
		
	}

	public DialogUtil(Context context){
		this.context = context;
	}

	public void alert(String message){
		alert(context.getString(android.R.string.dialog_alert_title), message, null);
	}

	public void alert(String title, String message){
		alert(title, message, null);
	}
	
	public void alert(String title, String message, Throwable ex){
		if (message != null )
		{
			if ( ex == null )
				Log.e(TAG_ERROR, message);
			else
				Log.e(TAG_ERROR, message, ex);
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		if ( title != null ) builder.setTitle(title);
		if ( message != null ) builder.setMessage(message);
		builder.setPositiveButton(android.R.string.ok, this.cancelCallback).setOnCancelListener(this.cancelCallback);
		builder.create().show();
	}

	public void confirm(String message, Runnable callback){
		this.confirm(null, message, callback);
	}
	
	public void confirm(String title, String message, final Runnable callback){
		AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		if ( title != null ) builder.setTitle(title);
		if ( message != null ) builder.setMessage(message);
		builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				callback.run();
			}
		})
		.setNegativeButton(android.R.string.no, this.cancelCallback).setOnCancelListener(this.cancelCallback);
		builder.create().show();
	}
	
	public static interface OnPromptCallback {
		public void onResult(String value);
	}

	public void prompt(String title, final OnPromptCallback callback) {
		this.prompt(title, null, InputType.TYPE_CLASS_TEXT, callback);
	}
	
	public void prompt(String title, int inputType, final OnPromptCallback callback) {
		this.prompt(title, null, inputType, callback);
	}

	public void prompt(String title, String initialValue, final OnPromptCallback callback) {
		this.prompt(title, initialValue, InputType.TYPE_CLASS_TEXT, callback);
	}

	public void prompt(String title, String initialValue, int inputType, final OnPromptCallback callback) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		if ( title != null ) builder.setTitle(title);
		final EditText text = new EditText(this.context);
		text.setInputType(inputType);
		if ( initialValue != null ) text.setText(initialValue);
		builder.setView(text);
		
		builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String value = text.getText().toString();
				dialog.dismiss();
				callback.onResult(value);
			}
		})
		.setNegativeButton(android.R.string.no, this.cancelCallback).setOnCancelListener(this.cancelCallback);
		builder.create().show();
	}
	
	public void toast(String message){
		Toast.makeText(this.context, message, Toast.LENGTH_LONG).show();
	}
}
