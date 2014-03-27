package com.unidevel.logo.engine.cmd.control;

import com.unidevel.logo.engine.LogoCmd;
import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.LogoEvalException;
import com.unidevel.logo.script.LogoList;

public class LogoIf extends LogoCmd {
	public static final String NAME = "IF";

	public LogoIf(LogoContext context) {
		super(context);
	}

	@Override
	public int eval() throws LogoEvalException {
		boolean cond = evalCondition();
		fillArgs(1);
		LogoList list = getContext().toList(-1);
		popup(1);
		if (cond) getContext().eval(list);
		return 0;
	}

	@Override
	public String getName() {
		return NAME;
	}
	
}
