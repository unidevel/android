package com.unidevel.logo.engine.cmd;

import com.unidevel.logo.engine.LogoCmd;
import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.LogoEvalException;
import com.unidevel.logo.script.LogoAtom;
import com.unidevel.logo.script.LogoList;
import com.unidevel.logo.script.LogoWord;

public class LogoTo extends LogoCmd {
	public static final String NAME = "TO";
	public LogoTo(LogoContext context) {
		super(context);
	}
	
	@Override
	public int eval() throws LogoEvalException {
		LogoList args = new LogoList();
		LogoList body = new LogoList();
		LogoWord nameWord = (LogoWord)getContext().getList().pop();
		String name = nameWord.getWord();
		boolean isArgs = true;
		while ( !getContext().getList().isEmpty()  ) {
			LogoAtom atom = getContext().getList().pop();
			if ( atom instanceof LogoWord ){
				LogoWord word = (LogoWord)atom;
				if (!word.isQuote() && !word.isColon() && "end".equalsIgnoreCase(word.getWord())) break;
				if ( isArgs && word.isColon() ) args.append(atom);
				else isArgs = false;
			}
			else isArgs = false;
			if (!isArgs) body.append(atom);
		}
		getContext().addCmd(name, new LogoScriptCmd(getContext(), name, args, body));
		return 0;
	}
	@Override
	public String getName() {
		return NAME;
	}
	
}
