package com.unidevel.logo.engine.cmd.graphic;

import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.cmd.LogoSimpleCmd;

public class LogoLeft extends LogoSimpleCmd {
	public static final String NAME = "LEFT";
	public static final String ABBRNAME = "LT";
	
	public LogoLeft(LogoContext context) {
		super(context, 1);
	}
	
	@Override
	public String getName() {
		return NAME;
	}
}
