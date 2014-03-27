package com.unidevel.logo.engine.cmd;

import java.util.Random;

import com.unidevel.logo.engine.LogoCmd;
import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.LogoEvalException;
import com.unidevel.logo.script.LogoWord;

public class LogoRandom extends LogoCmd {
	public static final String NAME = "RANDOM";
	static Random rand = new Random();
	static {
		rand.setSeed(System.currentTimeMillis());
	}
	
	public LogoRandom(LogoContext context) {
		super(context);
	}

	@Override
	public int eval() throws LogoEvalException {
		fillArgs(1);
		int max = (Integer)getContext().getValue(-1);
		getContext().popup(1);
		LogoWord word = new LogoWord("X");
		word.setValue(rand.nextInt(max));
		getContext().push(word);
		return 1;
	}

	@Override
	public String getName() {
		return NAME;
	}	
}
