package com.unidevel.logo.engine.cmd;

import com.unidevel.logo.engine.LogoCmd;
import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.LogoEvalException;
import com.unidevel.logo.script.LogoAtom;
import com.unidevel.logo.script.LogoList;
import com.unidevel.logo.script.LogoWord;

public class LogoRepeat extends LogoCmd{
	public LogoRepeat(LogoContext context) {
		super(context);
	}

	public static final String NAME = "REPEAT";

	@Override
	public int eval() throws LogoEvalException {
		fillArgs(2);
		LogoAtom atom = getContext().toAtom(-1);
		int n = (Integer)getContext().getValue(-2);
		for ( ; n > 0; n-- ){
			if ( atom instanceof LogoList ) {
				getContext().eval((LogoList)atom);
			}
			else {
				getContext().eval((LogoWord)atom);
			}
		}
		getContext().popup(2);
		return 0;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
