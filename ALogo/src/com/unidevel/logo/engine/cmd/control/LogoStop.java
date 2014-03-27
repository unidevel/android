package com.unidevel.logo.engine.cmd.control;

import com.unidevel.logo.engine.LogoCmd;
import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.LogoEvalException;

public class LogoStop extends LogoCmd {
	public static final String NAME = "STOP";
	
	public LogoStop(LogoContext context) {
		super(context);
	}

	@Override
	public int eval() throws LogoEvalException {
		throw new LogoStopException();
//		return 0;
	}
	
	@Override
	public String getName() {
		return NAME;
	}
}
