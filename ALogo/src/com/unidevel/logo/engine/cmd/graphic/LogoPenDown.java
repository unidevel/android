package com.unidevel.logo.engine.cmd.graphic;

import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.cmd.LogoSimpleCmd;

public class LogoPenDown extends LogoSimpleCmd {
	public static final String NAME = "PENDOWN";
	public static final String ABBRNAME = "PD";
	
	public LogoPenDown(LogoContext context) {
		super(context, 0);
	}
	
	@Override
	public String getName() {
		return NAME;
	}
}