package com.unidevel.www;

import java.util.HashMap;
import java.util.Map;

public class MimeTypes {
	static Map<String, String> mimeTypes;
	static {
		mimeTypes = new HashMap<String, String>();
		mimeTypes.put("htm", "text/html");
		mimeTypes.put("html", "text/html");
		mimeTypes.put("xml", "application/xml");
		mimeTypes.put("xhtml", "application/xhtml+xml");
		mimeTypes.put("js", "text/javascript");
		mimeTypes.put("txt", "text/plain");
		mimeTypes.put("css", "text/css");
		mimeTypes.put("zip", "application/zip");
		mimeTypes.put("jpg", "image/jpeg");
		mimeTypes.put("jpeg", "image/jpeg");
		mimeTypes.put("png", "image/png");
		mimeTypes.put("tif", "image/tiff");
		mimeTypes.put("tiff", "image/tiff");
		mimeTypes.put("gif", "image/gif");
		mimeTypes.put("ico", "image/vnd.microsoft.icon");
		mimeTypes.put("icon", "image/vnd.microsoft.icon");
		mimeTypes.put("svg", "image/svg+xml");
		mimeTypes.put("wav", "audio/vnd.wave");
		mimeTypes.put("mp3", "audio/mpeg");
		mimeTypes.put("mpeg", "video/mpeg");
		mimeTypes.put("mpg", "video/mpeg");
		mimeTypes.put("mp4", "video/mp4");
		mimeTypes.put("flv", "video/flv");
		mimeTypes.put("pdf", "application/pdf");
		mimeTypes.put("xls", "application/vnd.ms-excel");
		mimeTypes.put("ppt", "application/vnd.ms-powerpoint");
		mimeTypes.put("doc", "application/vnd.ms-word");
	}

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
