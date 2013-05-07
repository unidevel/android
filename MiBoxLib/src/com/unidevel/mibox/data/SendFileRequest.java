package com.unidevel.mibox.data;

public class SendFileRequest extends MiBoxRequest
{
	private static final long serialVersionUID = 1L;
	public String name;
	public long size;
	public long offset;
	public byte[] block;
}
