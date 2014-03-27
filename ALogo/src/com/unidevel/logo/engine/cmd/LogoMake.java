package com.unidevel.logo.engine.cmd;

import com.unidevel.logo.engine.LogoCmd;
import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.LogoEvalException;
import com.unidevel.logo.script.LogoAtom;
import com.unidevel.logo.script.LogoWord;

public class LogoMake extends LogoCmd {
	public static final  String NAME = "MAKE";
	public LogoMake(LogoContext context) {
		super(context);
	}

	@Override
	public int eval() throws LogoEvalException {
		fillArgs(2);
		LogoWord var = getContext().toWord(-2);
		LogoAtom val = getContext().toAtom(-1);
		getContext().popup(2);
		getContext().setValue(var.getWord(), val);
		return 0;
	}

	@Override
	public String getName() {
		return NAME;
	}	
}
