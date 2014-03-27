package com.unidevel.logo.engine.cmd.graphic;

import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.cmd.LogoSimpleCmd;

public class LogoPenUp extends LogoSimpleCmd {
	public static final String NAME = "PENUP";
	public static final String ABBRNAME = "PU";
	
	public LogoPenUp(LogoContext context) {
		super(context, 0);
	}
	
	@Override
	public String getName() {
		return NAME;
	}
}