package com.unidevel.logo.engine.cmd.graphic;

import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.cmd.LogoSimpleCmd;

public class LogoForward extends LogoSimpleCmd{
	public static final String NAME = "FORWARD";
	public static final String ABBRNAME = "FD";
	
	public LogoForward(LogoContext context) {
		super(context, 1);
	}
	
	@Override
	public String getName() {
		return NAME;
	}
}
