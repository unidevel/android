package com.unidevel.logo.engine.cmd.control;

import com.unidevel.logo.engine.LogoCmd;
import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.LogoEvalException;
import com.unidevel.logo.script.LogoList;

public class LogoIfElse extends LogoCmd {
	public static final String NAME = "IFELSE";

	public LogoIfElse(LogoContext context) {
		super(context);
	}

	@Override
	public int eval() throws LogoEvalException {
		boolean cond = evalCondition();
		LogoList list;
		fillArgs(2);
		if ( cond ) list = getContext().toList(-2);
		else list = getContext().toList(-1);
		popup(2);
		getContext().eval(list);
		return 0;
	}

	@Override
	public String getName() {
		return NAME;
	}

}
