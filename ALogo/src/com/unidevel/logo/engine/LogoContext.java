package com.unidevel.logo.engine;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.unidevel.logo.engine.cmd.LogoMake;
import com.unidevel.logo.engine.cmd.LogoPrint;
import com.unidevel.logo.engine.cmd.LogoRandom;
import com.unidevel.logo.engine.cmd.LogoRepeat;
import com.unidevel.logo.engine.cmd.LogoTo;
import com.unidevel.logo.engine.cmd.control.LogoIf;
import com.unidevel.logo.engine.cmd.control.LogoIfElse;
import com.unidevel.logo.engine.cmd.control.LogoStop;
import com.unidevel.logo.engine.cmd.graphic.LogoBack;
import com.unidevel.logo.engine.cmd.graphic.LogoForward;
import com.unidevel.logo.engine.cmd.graphic.LogoLeft;
import com.unidevel.logo.engine.cmd.graphic.LogoPenDown;
import com.unidevel.logo.engine.cmd.graphic.LogoPenUp;
import com.unidevel.logo.engine.cmd.graphic.LogoRight;
import com.unidevel.logo.engine.cmd.graphic.LogoSetPenColor;
import com.unidevel.logo.engine.cmd.graphic.LogoSetPenSize;
import com.unidevel.logo.engine.cmd.math.LogoAND;
import com.unidevel.logo.engine.cmd.math.LogoAdd;
import com.unidevel.logo.engine.cmd.math.LogoNOT;
import com.unidevel.logo.engine.cmd.math.LogoOR;
import com.unidevel.logo.engine.cmd.math.LogoSum;
import com.unidevel.logo.engine.cmd.operation.LogoGreaterEqualP;
import com.unidevel.logo.engine.cmd.operation.LogoGreaterP;
import com.unidevel.logo.engine.cmd.operation.LogoLessEqualP;
import com.unidevel.logo.engine.cmd.operation.LogoLessP;
import com.unidevel.logo.script.LogoAtom;
import com.unidevel.logo.script.LogoList;
import com.unidevel.logo.script.LogoWord;

public abstract class LogoContext {
	class StackData {
		Map<String, Object> values;
		LogoList list;
		public StackData(LogoList list){
			this.list = list;
			this.values = new HashMap<String, Object>();
		}
	};
	
	Map<String, LogoCmd> cmds;
	Stack<LogoAtom> stack;
	Stack<StackData> listData;
	Map<String, Object> globalValues;
	
	protected LogoContext(){
		this.cmds = new HashMap<String, LogoCmd>();
		this.listData = new Stack<StackData>();
		this.stack = new Stack<LogoAtom>();
		this.globalValues = new HashMap<String, Object>();
		addCmd(new LogoPrint(this));
		addCmd(new LogoMake(this));
		addCmd(new LogoSum(this));
		addCmd(new LogoRepeat(this));
		addCmd(new LogoRandom(this));
		addCmd(new LogoTo(this));
		addCmd(new LogoAND(this));
		addCmd(new LogoOR(this));
		addCmd(new LogoNOT(this));
		addCmd(new LogoAdd(this));
		
		addCmd(new LogoForward(this));
		addCmd(new LogoBack(this));
		addCmd(new LogoRight(this));
		addCmd(new LogoLeft(this));
		addCmd(new LogoPenUp(this));
		addCmd(new LogoPenDown(this));
		addCmd(new LogoSetPenSize(this));
		addCmd(new LogoSetPenColor(this));
		
		addCmd(new LogoStop(this));
		addCmd(new LogoIf(this));
		addCmd(new LogoIfElse(this));
		
		addCmd(new LogoLessP(this));
		addCmd(new LogoLessEqualP(this));
		addCmd(new LogoGreaterP(this));
		addCmd(new LogoGreaterEqualP(this));
		
		addGlobal("PI", 3.14159265358979f);
	}
	
	private void addGlobal(String name, Object value){
		globalValues.put(name, value);
	}
	
	public void enterList(LogoList list){
		this.listData.add(new StackData(list));
	}
	
	public void leaveList(){
		this.listData.pop();
	}
	
	public void addCmd(String name, LogoCmd cmd){
		cmds.put(name.toUpperCase(), cmd);
	}
	
	private void addCmd(LogoCmd cmd){
		addCmd(cmd.getName(), cmd);
		try {
			Field field = cmd.getClass().getField("ABBRNAME");
			String abbrName = (String)field.get(null);
			addCmd(abbrName, cmd);
		}
		catch(Throwable ex){
			
		}
	}
	
	public Object getValue(int index) throws LogoEvalException {
		return getValue(toAtom(index));
	}
	
	public Object getValue(LogoAtom atom) throws LogoEvalException{
		if ( atom instanceof LogoList ) return atom;
		if ( atom instanceof LogoWord) {
			LogoWord word = (LogoWord)atom;
			if ( word.isColon() ) return getValue(word.getWord()); 
			else return word.getValue();
		}
		return null;
	}
	
	public Object getValue(String name) throws LogoEvalException{
		for ( int n = listData.size()-1; n > 0 ; --n ){
			StackData data = listData.get(n);
			if ( data.values.containsKey(name) ) return data.values.get(name);
		}
		if ( globalValues.containsKey(name) ) {
			return globalValues.get(name);
		}
		throw new LogoEvalException("Can't find variable "+name);
	}
	
	public void setValue(String name, LogoAtom value) throws LogoEvalException{
		listData.peek().values.put(name, getValue(value));
	}
	
	public LogoCmd getCmd(String name){
		LogoCmd cmd = cmds.get(name.toUpperCase());
		if ( cmd != null ) return cmd.clone();
		return null;
	}
	
	public void push(LogoAtom value){
		stack.push(value);
	}
	
	public void popup(int n){
		while ( n-- > 0 ) stack.pop();
	}
	
	public LogoAtom pop(){
		return stack.pop();
	}
	
	public LogoAtom toAtom(int index) {
		if ( index < 0 ) index = stack.size()+index;
		return stack.get(index);
	}
	
	public LogoWord toWord(int index) throws LogoEvalException {
		LogoAtom atom = toAtom(index);
		if ( atom instanceof LogoWord ) return (LogoWord)atom;
		throw new LogoEvalException("Not a word", atom);
	}
	
	public LogoList toList(int index) throws LogoEvalException {
		LogoAtom atom = toAtom(index);
		if ( atom instanceof LogoList ) return (LogoList)atom;
		throw new LogoEvalException("Not a list", atom);
	}
	
	public abstract void run(String cmd, Object... args);
	
	public int getTop(){
		return stack.size();
	}
	
	public LogoList getList(){
		return listData.peek().list;
	}
	
	public void eval(LogoList list) throws LogoEvalException {
		list = list.clone();
		enterList(list);
		try {
			while ( !list.isEmpty() ) {
				LogoAtom atom = list.pop();
				if (!( atom instanceof LogoWord )) throw new LogoEvalException("Not a word", atom);
				LogoWord word = (LogoWord)atom;
				LogoCmd cmd = getCmd(word.getWord());
				if ( cmd == null ) throw new LogoEvalException("Not a command", atom);
//				if ( cmd instanceof LogoStop ) {
//					break;
//				}
				cmd.eval();
			}
		}
		finally {
			leaveList();
		}
	}
	
	public void eval(LogoWord word) throws LogoEvalException {
		LogoCmd cmd = getCmd(word.getWord());
		if ( cmd == null ) throw new LogoEvalException("Not a command", word);
		cmd.eval();
	}
}
