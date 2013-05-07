package com.unidevel.mibox.data;

import java.io.Serializable;

public class FileInfo implements Serializable
{

	private static final long serialVersionUID = 1L;

	public String name;
	public boolean isDirectory;
	public long size;
	public long lastModified;
}
