package com.unidevel.logo.engine.cmd.math;

import com.unidevel.logo.engine.LogoCmd;
import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.LogoEvalException;

public class LogoNOT extends LogoCmd{
	public static final String NAME="NOT";
	public LogoNOT(LogoContext context) {
		super(context);
	}

	@Override
	public int eval() throws LogoEvalException {
		fillArgs(1);
		boolean var1 = toBool(-1);
		popup(1);
		pushWord(!var1);
		return 0;
	}

	@Override
	public String getName() {
		return NAME;
	}

}
