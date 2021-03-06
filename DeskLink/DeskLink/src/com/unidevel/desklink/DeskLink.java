package com.unidevel.desklink;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;

public class DeskLink extends Activity implements OnClickListener {
	String URL_PATTERN = "((https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])";
	/** Called when the activity is first created. */
	EditText editUrl, editLabel;
	ImageView imgIcon;
	Button btnOk, btnCancel;
	File favFile;
	boolean autoUpdateTitle;
	boolean autoUpdateIcon;
	public boolean isNetworkAvailable() {
		Context context = getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		String title1 = null;
		String title2 = null;
		String title = null;
		String url = null;
		super.onCreate(savedInstanceState);
		Pattern pattern = Pattern.compile(URL_PATTERN);
		favFile = new File(getFilesDir(),"favicon.ico");
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.main);
		getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
				R.drawable.link);

		editUrl = (EditText) findViewById(R.id.editUrl);
		editLabel = (EditText) findViewById(R.id.editLabel);
		imgIcon = (ImageView)findViewById(R.id.imgIcon);

		btnOk = (Button) findViewById(R.id.btnOk);
		btnCancel = (Button) findViewById(R.id.btnCancel);

		btnOk.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		autoUpdateTitle = pref.getBoolean("keyUpdateTitle", false);
		autoUpdateIcon = pref.getBoolean("keyUpdateIcon", false);

		Intent intent = getIntent();
		if (Intent.ACTION_SEND.equals(intent.getAction())) {
			Bundle extras = intent.getExtras();
			String keys = extras.keySet() == null ? "" : extras.keySet()
					.toString();
			Log.i("Send", keys);
//			StringBuffer buf = new StringBuffer();
			for (Iterator it = extras.keySet().iterator(); it.hasNext();) {
				Object key = it.next();
				Object value = extras.get(key.toString());
				String sValue = value == null ? "" : value.toString();
//				buf.append(""
//						+ key
//						+ ":"
//						+ (value == null ? "null" : "("
//								+ value.getClass().getName() + ")\n  " + value
//								+ "\n\n"));
//				Log.i("Send", key + ":" + value);
				Matcher m = pattern.matcher(sValue);
				if (m.find()) {
					url = m.group(1);
					title1 = sValue.substring(0, m.start());
					title2 = sValue.substring(m.end());
					title = title1 + title2;
//					buf.append("URL: " + url + "\n\n");

					editUrl.setText(url);
					editUrl.setEnabled(false);
					editLabel.setText(title);
				}
//				if (key.toString().toUpperCase().contains("SUBJECT")) {
//					if (value != null) {
//						title = value.toString();
//						editLabel.setText(title);
//					}
//				}
			}

			if (url != null && isNetworkAvailable() ) {
				if (autoUpdateTitle || autoUpdateIcon){
					updateFromLink(url);
				}
			}
			
			if ( url == null ) {
				Toast.makeText(this, getString(R.string.noUrl), 3).show();
				this.finish();
			}
			
			// TextView txtDump = (TextView)findViewById(R.id.txtDump);
			// txtDump.setText(buf.toString());
			// if ( url != null ){
			// Uri uri = Uri.parse(url);
			// Intent shortcutIntent = new Intent(Intent.ACTION_VIEW);
			// shortcutIntent.setData(uri);
			// Intent createIntent = new Intent();
			// createIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
			// shortcutIntent);
			// createIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
			// createIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
			// Intent.ShortcutIconResource.fromContext(this, R.drawable.icon));
			// createIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			// this.sendBroadcast(createIntent);
			// // finish();
			// }
			// if (extras != null && extras.containsKey(Intent.EXTRA_STREAM)) {
			// Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
			// if (uri == null) return;
			// try {
			// Log.i("Send", uri==null?"":uri.toString());
			// Intent shortcutIntent = new Intent(Intent.ACTION_VIEW);
			// shortcutIntent.setData(uri);
			// // shortcutIntent.setType("image/jpeg");
			// Intent createIntent = new Intent();
			// createIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
			// shortcutIntent);
			// createIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "TEST");
			// createIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
			// Intent.ShortcutIconResource.fromContext(this, R.drawable.icon));
			// createIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			// this.sendBroadcast(createIntent);
			// finish();
			// this.startActivity(shortcutIntent);
			// }catch(Throwable ex){
			// Log.e("Send", ex.getMessage(), ex);
			// Toast.makeText(this, ex.getMessage(), 3).show();
			// }
			// }
		}
	}

	ProgressDialog dialog;
	Handler handler;
	boolean isCanceled;

	public void updateFromLink(final String url) {
		if (dialog != null)
			dialog.dismiss();
		isCanceled = false;
		dialog = ProgressDialog.show(this, null, getString(R.string.updating));
		dialog.setCancelable(true);
		handler = new Handler();
		Thread thread = new Thread() {
			public void run() {
				StringBuffer buf = new StringBuffer();
				String title = null;
				try {
					String encoding = null;
					URL urlLink = new URL(url);
					URL favLink = new URL(urlLink.getProtocol(), urlLink.getHost(), urlLink.getPort(), "/favicon.ico");
					HttpURLConnection conn = (HttpURLConnection) urlLink
							.openConnection();
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);
					conn.setDoOutput(true);
					conn.setRequestMethod("GET");
					conn.setRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 5.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1");
					InputStream in = conn.getInputStream();
					encoding = conn.getContentEncoding();
					
					InputStreamReader reader = null;
					if( encoding == null ) reader = new InputStreamReader(in, "ISO8859-1");
					else reader = new InputStreamReader(in, encoding);
					int len;
					char cbuf[] = new char[1024];
					String htmlEncoding = null;
					Pattern pattern = Pattern.compile("<title>([^<]+)", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);
					Pattern charsetPattern = Pattern.compile("\\bcharset\\s*=\\s*\"?([^\"]+)\"");
					while ((len = reader.read(cbuf)) > 0) {
						if ( encoding == null ){
							if (htmlEncoding == null){
								String s = new String(cbuf);
								Matcher m = charsetPattern.matcher(s);
								if (m.find()){
									htmlEncoding = m.group(1).trim();
									String s2 = buf.toString();
									buf = new StringBuffer();
									buf.append(new String(s.getBytes("ISO8859-1"),htmlEncoding));
								}
							}
						}
						if(encoding != null)
							buf.append(cbuf,0,len);
						else{
							buf.append(new String(new String(cbuf).getBytes("ISO8859-1"),htmlEncoding));
						}
						String body = buf.toString();
						Matcher m = pattern.matcher(body);
						if ( m.find()){
							title = m.group(1).trim();
							int pos = title.indexOf('\n');
							if( pos >= 0 )title = title.substring(0,pos).trim();
							break;
						}
					}
					in.close();
					conn.disconnect();
					
					conn = (HttpURLConnection)favLink.openConnection();
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);
					conn.setDoOutput(true);
					conn.setRequestMethod("GET");
					conn.setRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 5.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1");
					in = conn.getInputStream();
					File favFile = new File(getFilesDir(),"favicon.ico");
					FileOutputStream out = new FileOutputStream(favFile);
					byte[] data = new byte[1024];
					while ( (len = in.read(data)) > 0 ) {
						out.write(data, 0, len);
					}
					in.close();
					out.close();
					conn.disconnect();
				} catch (Throwable ex) {
					Log.e("ToDesktop", ex.getMessage(),ex);
				}
				final String theTitle = title;
				handler.post(new Runnable() {
					@Override
					public void run() {
						if ( autoUpdateTitle && theTitle != null ) {
							editLabel.setText(theTitle);
						}
						if ( autoUpdateIcon )
						try {
							Bitmap bitmap = BitmapFactory.decodeFile(favFile.getPath());
							Bitmap source = BitmapFactory.decodeResource(getResources(), R.drawable.link);
							source = source.copy(Bitmap.Config.ARGB_8888, true);
							Canvas canvas = new Canvas(source);
							Paint paint = new Paint();
							int x = source.getWidth()*4/7;
							int y = source.getHeight()*4/7;
							int w = source.getWidth()/4;
							int h = source.getHeight()/4;
//							canvas.drawBitmap(bitmap, w, h, paint);
							canvas.drawBitmap(bitmap, new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()), new Rect(x,y,x+w,y+h), paint);
							imgIcon.setImageBitmap(source);
						}catch(Throwable ex){
							Log.w("ToDesktop", ex.getMessage(),ex);
						}finally{
							favFile.delete();							
						}
						
						dialog.dismiss();
					}
				});
			}
		};
		thread.start();
	}

	void showError(String s) {
		Log.e("ToDesktop", s);
		Toast.makeText(this, s, 3);
	}

	@Override
	public void onClick(View view) {
		if (view == btnOk) {
			String title = editLabel.getText().toString();
			String link = editUrl.getText().toString();
			if (title.trim().length() == 0) {
				showError(getString(R.string.errorTitle));
				return;
			}
			if (link.trim().length() == 0) {
				showError(getString(R.string.errorLink));
				return;
			}
			Uri uri = null;
			try {
				uri = Uri.parse(link);
			} catch (Throwable ex) {
				showError(getString(R.string.errorLinkForm));
				return;
			}
			
			Intent shortcutIntent = new Intent(Intent.ACTION_VIEW);
			shortcutIntent.setData(uri);
			Intent createIntent = new Intent();
			createIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
			createIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
			try {
				BitmapDrawable drawable = (BitmapDrawable)imgIcon.getDrawable();
				createIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, drawable.getBitmap());
			}catch(Throwable ex){
				Log.w("ToDesktop", ex.getMessage(),ex);
			}
			createIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
					Intent.ShortcutIconResource.fromContext(this,
							R.drawable.link));
			createIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			this.sendBroadcast(createIntent);
			MobclickAgent.onEvent(this, "addlink");
		} else if (view == btnCancel) {
			MobclickAgent.onEvent(this, "cancellink");
		}
		this.finish();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPause(this);
	}
}