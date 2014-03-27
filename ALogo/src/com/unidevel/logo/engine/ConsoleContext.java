package com.unidevel.logo.engine;

import com.unidevel.logo.engine.cmd.LogoPrint;

public class ConsoleContext extends LogoContext {

	public void run(String cmd, Object... args) {
		if ( cmd == LogoPrint.NAME ) {
			System.err.println(args[0]);
		}
	}
}
