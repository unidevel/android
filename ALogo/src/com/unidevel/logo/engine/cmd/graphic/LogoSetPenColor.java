package com.unidevel.logo.engine.cmd.graphic;

import com.unidevel.logo.engine.LogoCmd;
import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.LogoEvalException;

public class LogoSetPenColor extends LogoCmd {

	public static final String NAME = "SETPENCOLOR";
	public static final String ABBRNAME = "SETPC";
	
	static int colors[] = new int[]{
		0xFF000000 /*0 black*/,
		0xFF0000FF /*blue*/,
		0xFF00FF00 /*lime*/,
		0xFF00FFFF /*cyan*/,
		0xFFFF0000 /*red */,
		0xFFFF00FF /*5 Magenta */,
		0xFFFFFF00 /* yellow */,
		0xFFFFFFFF /* white */,
		0xFF9B603B /* Brown */,
		0xFFC58812 /* Tan */,
		0xFF64A240 /* Olive */,
		0xFF78BBBB /* Sky blue */,
		0xFFFF9577 /* Salmon */,
		0xFF9071D0 /* Medium Purple */,
		0xFFFFA300 /* Orange */,
		0xFFB7B7B7 /* Dark Gray */
	};
	
	public LogoSetPenColor(LogoContext context) {
		super(context);
	}

	@Override
	public int eval() throws LogoEvalException {
		fillArgs(1);
		int color = toInt(-1);
		popup(1);
		getContext().run(NAME, colors[color%colors.length]);
		return 0;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
