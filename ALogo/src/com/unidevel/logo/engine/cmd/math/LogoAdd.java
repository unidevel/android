package com.unidevel.logo.engine.cmd.math;

import com.unidevel.logo.engine.LogoCmd;
import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.LogoEvalException;

public class LogoAdd extends LogoCmd {
	public static final String NAME = "ADD";
	public static final String ABBRNAME = "+";
	
	public LogoAdd(LogoContext context) {
		super(context);
	}

	@Override
	public int eval() throws LogoEvalException {
		fillArgs(2);
		float v1 = toFloat(-2);
		float v2 = toFloat(-1);
		popup(2);
		pushWord(v1+v2);
		return 0;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
