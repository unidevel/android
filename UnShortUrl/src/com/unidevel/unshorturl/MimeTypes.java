package com.unidevel.unshorturl;

import java.util.HashMap;
import java.util.Map;
import android.annotation.SuppressLint;

public class MimeTypes {
	static Map<String, String> mimeTypes;
	static {
		mimeTypes = new HashMap<String, String>();
		mimeTypes.put( "htm", "text/html" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "html", "text/html" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "xml", "application/xml" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "xhtml", "application/xhtml+xml" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "js", "text/javascript" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "txt", "text/plain" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "css", "text/css" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "zip", "application/zip" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "jpg", "image/jpeg" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "jpeg", "image/jpeg" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "png", "image/png" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "tif", "image/tiff" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "tiff", "image/tiff" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "gif", "image/gif" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "ico", "image/vnd.microsoft.icon" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "icon", "image/vnd.microsoft.icon" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "svg", "image/svg+xml" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "wav", "audio/vnd.wave" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "mp3", "audio/mpeg" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "mpeg", "video/mpeg" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "mpg", "video/mpeg" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "mp4", "video/mp4" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "flv", "video/flv" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "pdf", "application/pdf" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "xls", "application/vnd.ms-excel" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "ppt", "application/vnd.ms-powerpoint" ); //$NON-NLS-1$ //$NON-NLS-2$
		mimeTypes.put( "doc", "application/vnd.ms-word" ); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@SuppressLint ("DefaultLocale")
	public static String getType(String uri) {
		int pos = uri.indexOf('?');
		String ext = null;
		String name = null;
		if (pos < 0) {
			pos = uri.lastIndexOf('/');
			if (pos > 0) {
				name = uri.substring(pos + 1);
			}
		} else {
			int pos2 = uri.substring(0, pos).lastIndexOf('/');
			if (pos2 > 0) {
				name = uri.substring(pos2 + 1, pos);
			}
		}
		if ( name == null )
			return null;
		pos = name.lastIndexOf('.');
		if ( pos >= 0 ) {
			ext = name.substring(pos+1).toLowerCase();
		}
		if (ext == null || !mimeTypes.containsKey(ext)) {
			return null;
		} else {
			return mimeTypes.get(ext);
		}
	}
}
