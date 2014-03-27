package com.unidevel.logo.engine.cmd.math;

import com.unidevel.logo.engine.LogoCmd;
import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.LogoEvalException;

public class LogoOR extends LogoCmd {
	public static final String NAME = "OR";
	public LogoOR(LogoContext context) {
		super(context);
	}
	@Override
	public int eval() throws LogoEvalException {
		fillArgs(2);
		boolean var2 = toBool(-2);
		boolean var1 = toBool(-1);
		popup(2);
		pushWord(var1 || var2);
		return 1;
	}
	@Override
	public String getName() {
		return NAME;
	}

}
