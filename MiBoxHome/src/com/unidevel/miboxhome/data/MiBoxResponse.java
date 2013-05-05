package com.unidevel.miboxhome.data;

import java.io.Serializable;

public class MiBoxResponse implements Serializable
{
	private static final long serialVersionUID = 1L;
	public boolean failed = false;
	public String failedMessage;
}
