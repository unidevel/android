package com.unidevel.logo.engine;

import java.lang.reflect.Constructor;

import com.unidevel.logo.script.LogoAtom;
import com.unidevel.logo.script.LogoList;
import com.unidevel.logo.script.LogoWord;

public abstract class LogoCmd  {
	LogoContext context;
	public LogoCmd(LogoContext context){
		this.context = context;
		setTop(context.getTop());
	}
	
	public LogoContext getContext() {
		return context;
	}
	
	public abstract String getName();

	public abstract int eval() throws LogoEvalException;
		
	public LogoWord toWord(LogoAtom atom) throws LogoEvalException{
		if ( atom instanceof LogoWord ) return (LogoWord)atom;
		throw new LogoEvalException("Not a word", atom);
	}
	
	public LogoList toList(LogoAtom atom) throws LogoEvalException{
		if ( atom instanceof LogoList ) return (LogoList)atom;
		throw new LogoEvalException("Not a list", atom);
	}


	public int toInt(int index) throws LogoEvalException {
		LogoWord word = getContext().toWord(index);
		Object val = getContext().getValue(word);
		if ( val instanceof Integer ) return (Integer)val;
		try {
			return Integer.valueOf(val.toString());
		}
		catch(Throwable ex){
			throw new LogoEvalException(ex.getMessage());
		}
	}
	
	public boolean toBool(int index) throws LogoEvalException {
		LogoWord word = getContext().toWord(index);
		Object val = getContext().getValue(word);
		if ( val instanceof Boolean ) return (Boolean)val;
		else try {
			return Boolean.valueOf(String.valueOf(val));
		}
		catch(Throwable ex){
			throw new LogoEvalException("Value "+val+" can't convert to boolean");
		}
	}
	
	public float toFloat(int index) throws LogoEvalException {
		LogoWord word = getContext().toWord(index);
		Object val = getContext().getValue(word);
		if ( val instanceof Float ) return (Float)val;
		try {
			return Float.valueOf(val.toString());
		}
		catch(Throwable ex){
			throw new LogoEvalException(ex.getMessage());
		}
	}
	public LogoCmd clone(){
		try {
			Constructor<? extends LogoCmd> ctor = this.getClass().getConstructor(LogoContext.class);
			return ctor.newInstance(getContext());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public void fillArgs(int n) throws LogoEvalException {
		while ( getTop()+n > context.getTop() ) {
			LogoAtom atom = context.getList().pop();
			if ( atom instanceof LogoList ) context.push(atom);
			else if ( atom instanceof LogoWord ){
				LogoWord word = (LogoWord)atom;
				if ( word.isColon() || word.isQuote() || word.isNumber() ) {
					context.push(word);
				}
				else {
					LogoCmd cmd = context.getCmd(word.getWord());
					if ( cmd == null ) throw new LogoEvalException("Not known how to", atom);
					cmd.eval();
				}
			}
		}
	}
	
	public int evalCmd() throws LogoEvalException {
		LogoAtom atom = context.getList().pop();
		LogoWord word = (LogoWord)atom;
		LogoCmd cmd = context.getCmd(word.getWord());
		if ( cmd == null ) return -1;
		return cmd.eval();
	}
	
	public boolean evalCondition() throws LogoEvalException {
		LogoAtom atom = context.getList().pop();
		if (!( atom instanceof LogoWord )) throw new LogoEvalException("Condition can't be a list");
		LogoWord word = (LogoWord)atom;
		if ( !word.isColon() ){
			LogoCmd cmd = context.getCmd(word.getWord());
			int top = context.getTop();
			if ( cmd != null ) {
				cmd.eval();
				if ( context.getTop() > top ) {
					try {
						return toBool(top+1);
					}
					finally {
						popup(context.getTop()-top);
					}
				}
			}
		}
		if ( word.isBool() ){
			return word.toBool();
		}
		else {
			word = (LogoWord)context.getList().pop();
			context.getList().push(atom);
			LogoCmd cmd = context.getCmd(word.getWord());
			top = context.getTop();
			cmd.eval();
			if ( context.getTop() > top ) {
				boolean result = toBool(top);
				popup(context.getTop()-top);
				return result;
			}
		}
		return false;
	}
	
	int top;
	public void setTop(int top){
		this.top = top;
	}
	
	public int getTop() {
		return top;
	}

	public void popup(int n){
		getContext().popup(n);
	}
	
	public void pushWord(Object value){
		LogoWord word = new LogoWord(String.valueOf(value));
		word.setValue(value);
		getContext().push(word);
	}
//	protected void eval(LogoList list) throws LogoEvalException {
//		context.enterList(list);
//		try {
//			while ( !list.isEmpty() ) {
//				LogoAtom atom = list.pop();
//				if (!( atom instanceof LogoWord )) throw new LogoEvalException("Not a word", atom);
//				LogoWord word = (LogoWord)atom;
//				LogoCmd cmd = context.getCmd(word.getWord());
//				if ( cmd == null ) throw new LogoEvalException("Not a command", atom);
//				cmd.eval();
//			}
//		}
//		finally {
//			context.leaveList();
//		}
//	}
	
//	protected void eval(LogoWord word) throws LogoEvalException {
//		LogoCmd cmd = context.getCmd(word.getWord());
//		if ( cmd == null ) throw new LogoEvalException("Not a command", word);
//		cmd.eval();
//	}
}
