package com.unidevel.logo.engine.cmd.graphic;

import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.cmd.LogoSimpleCmd;

public class LogoBack extends LogoSimpleCmd {
	public static final String NAME = "BACK";
	public static final String ABBRNAME = "BK";
	
	public LogoBack(LogoContext context) {
		super(context, 1);
	}
	
	@Override
	public String getName() {
		return NAME;
	}
}
