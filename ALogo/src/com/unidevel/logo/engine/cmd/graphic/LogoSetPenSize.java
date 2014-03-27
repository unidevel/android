package com.unidevel.logo.engine.cmd.graphic;

import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.cmd.LogoSimpleCmd;

public class LogoSetPenSize extends LogoSimpleCmd {
	public static final String NAME = "SETPENSIZE";
	public LogoSetPenSize(LogoContext context) {
		super(context, 1);
	}
	
	@Override
	public String getName() {
		return NAME;
	}
}
