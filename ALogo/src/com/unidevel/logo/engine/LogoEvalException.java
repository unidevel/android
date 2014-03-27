package com.unidevel.logo.engine;

import com.unidevel.logo.script.LogoAtom;

public class LogoEvalException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public LogoEvalException(String msg){
		super(msg);
	}
	
	public LogoEvalException(String msg, LogoAtom atom){
		super(msg + " "+atom.toString());
	}
}
