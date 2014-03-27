package com.unidevel.logo.engine.cmd;

import com.unidevel.logo.engine.LogoCmd;
import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.LogoEvalException;
import com.unidevel.logo.engine.cmd.control.LogoStopException;
import com.unidevel.logo.script.LogoList;
import com.unidevel.logo.script.LogoWord;

public class LogoScriptCmd extends LogoCmd {
	String name;
	LogoList args;
	LogoList body;
	public LogoScriptCmd(LogoContext context, String name, LogoList args, LogoList body){
		super(context);
		this.name = name;
		this.args = args;
		this.body = body;
	}

	@Override
	public int eval() throws LogoEvalException {
		fillArgs(args.count());
		LogoList body = this.body.clone();
		for ( int i = args.count()-1; i >=0; --i ) {
			body.insert(0, getContext().pop());
			body.insert(0, makeWord("\""+((LogoWord)args.getAtom(i)).getWord()));
			body.insert(0, makeWord("MAKE"));
		}
		try {
			getContext().eval(body);
		}
		catch(LogoStopException ex){
			
		}
		return 0;
	}
	
	protected LogoWord makeWord(String cmd){
		LogoWord word = new LogoWord(cmd);
		word.setValue(word.getWord());
		return word;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public LogoCmd clone() {
		LogoScriptCmd newCmd = new LogoScriptCmd(getContext(), name, args, body);
		return newCmd;
	}
}
