package com.unidevel.logo.engine.cmd;

import com.unidevel.logo.engine.LogoCmd;
import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.LogoEvalException;

public abstract class LogoSimpleCmd extends LogoCmd {
	int nArgs ;
	protected LogoSimpleCmd(LogoContext context, int nArgs) {
		super(context);
		this.nArgs = nArgs;
	}

	@Override
	public int eval() throws LogoEvalException {
		if ( nArgs <= 0 ) {
			getContext().run(getName());
			return 0;
		}
		Object[] args = new Object[nArgs];
		fillArgs(nArgs);
		for ( int i = 0; i < nArgs ; ++ i ){
			args[i] = getContext().getValue(i-nArgs); 
		}
		popup(nArgs);
		getContext().run(getName(), args);
		return 0;
	}

}
