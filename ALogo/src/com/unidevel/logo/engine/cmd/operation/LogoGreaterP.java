package com.unidevel.logo.engine.cmd.operation;

import com.unidevel.logo.engine.LogoCmd;
import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.LogoEvalException;

public class LogoGreaterP extends LogoCmd {
	public static final String NAME = "GREATERP";
	public static final String ABBRNAME = ">";
	public LogoGreaterP(LogoContext context) {
		super(context);
	}
	
	@Override
	public int eval() throws LogoEvalException {
		float v1, v2;
		fillArgs(2);
		v1 = toFloat(-2);
		v2 = toFloat(-1);
		popup(2);	
		pushWord(v1 > v2);
		return 1;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
