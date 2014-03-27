package com.unidevel.logo.engine.cmd;

import com.unidevel.logo.engine.LogoCmd;
import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.LogoEvalException;
import com.unidevel.logo.script.LogoAtom;
import com.unidevel.logo.script.LogoList;
import com.unidevel.logo.script.LogoWord;

public class LogoPrint extends LogoCmd{
	public static final String NAME = "PRINT";
	public static final String ABBRNAME = "PR"; 
	
	LogoAtom arg;
	
	public LogoPrint(LogoContext context) {
		super(context);
	}

	@Override
	public int eval() throws LogoEvalException {
		fillArgs(1);
		LogoAtom arg = getContext().toAtom(-1);
		getContext().popup(1);
		if ( arg instanceof LogoList ) {
			getContext().run(getName(), arg.toString());
		}
		else if ( arg instanceof LogoWord) {
			LogoWord word = (LogoWord)arg;
			getContext().run(getName(), getContext().getValue(word));
		}
		return 0;
	}
	
	
	@Override
	public String getName() {
		return NAME;
	}
}
