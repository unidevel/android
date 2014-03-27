package com.unidevel.logo.engine.cmd.graphic;

import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.cmd.LogoSimpleCmd;

public class LogoRight extends LogoSimpleCmd {
	public static final String NAME = "RIGHT";
	public static final String ABBRNAME = "RT";
	public LogoRight(LogoContext context) {
		super(context, 1);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String getName() {
		return NAME;
	}



}

