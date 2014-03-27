package com.unidevel.logo.engine.cmd.math;

import com.unidevel.logo.engine.LogoCmd;
import com.unidevel.logo.engine.LogoContext;
import com.unidevel.logo.engine.LogoEvalException;

public class LogoSum extends LogoCmd{
	public static final String NAME = "SUM";
	
	public LogoSum(LogoContext context){
		super(context);
	}
	
	@Override
	public int eval() throws LogoEvalException {
		fillArgs(2);
		Object val1 = getContext().getValue(-2);
		Object val2 = getContext().getValue(-1);
		popup(2);
		if ( !isNumber(val1) || !isNumber(val2) ) throw new LogoEvalException("Need number to sum");
		if ( val1 instanceof Integer && val2 instanceof Integer){
			pushWord(toInt(val1)+toInt(val2));
		}
		else {
			pushWord(toFloat(val1)+toFloat(val2));
		}
		return 1;
	}
	
	public boolean isNumber(Object val){
		if ( val instanceof Integer || val instanceof Float ) return true;
		return false;
	}
	
	public int toInt(Object val){
		if ( val instanceof Integer ) {
			return ((Integer)val).intValue();
		}
		else if ( val instanceof Float ) {
			return ((Float)val).intValue();
		}
		return 0;
	}
	
	public float toFloat(Object val){
		if ( val instanceof Integer ) {
			return ((Integer)val).floatValue();
		}
		else if ( val instanceof Float ) {
			return ((Float)val).floatValue();
		}
		return 0.0f;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
