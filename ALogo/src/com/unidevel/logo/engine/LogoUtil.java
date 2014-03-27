package com.unidevel.logo.engine;

import com.unidevel.logo.script.LogoWord;

public class LogoUtil {
	public static long toLong(LogoWord value) throws LogoEvalException{
//		if ( value instanceof LogoConstant ) {
//			Object val = ((LogoConstant)value).getValue();
//			if ( val instanceof Long ){
//				return (Long)val;
//			}
//			else if ( val instanceof Double ){
//				return ((Double)val).longValue();
//			}
//		}
		throw new LogoEvalException("Can't cast "+value+" to long");
	}
}
