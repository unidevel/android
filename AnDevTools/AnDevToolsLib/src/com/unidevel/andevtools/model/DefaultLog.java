package com.unidevel.andevtools.model;

public class DefaultLog implements ILog {

	@Override
	public void trace(String fmt, Object... args) {
		String msg = String.format(fmt, args);
		System.out.print("[trace]: ");
		System.out.println(msg);
	}

	@Override
	public void warn(String fmt, Object... args) {
		String msg = String.format(fmt, args);
		System.out.print("[warn]: ");
		System.out.println(msg);		
	}

	@Override
	public void error(String fmt, Object... args) {
		String msg = String.format(fmt, args);
		System.err.print("[error]: ");
		System.err.println(msg);
	}

	@Override
	public void info(String fmt, Object... args) {
		String msg = String.format(fmt, args);
		System.out.print("[info]: ");
		System.out.println(msg);
	}

	@Override
	public void error(Throwable e) {
		e.printStackTrace(System.err);
	}
}
