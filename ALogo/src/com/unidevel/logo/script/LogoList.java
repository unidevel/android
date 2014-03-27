package com.unidevel.logo.script;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LogoList extends LogoAtom {
	List<LogoAtom> list;
	public LogoList(){
		list = new ArrayList<LogoAtom>();
	}
	
	public void append(LogoAtom object){
		list.add(object);
	}
	
	public void push(LogoAtom atom){
		list.add(0, atom);
	}
	
	public void insert(int pos, LogoAtom atom){
		list.add(pos, atom);
	}
	
	public LogoAtom peek(){
		return list.get(0);
	}
	
	public LogoAtom pop(){
		return list.remove(0);
	}
	
	public boolean isEmpty(){
		return list.isEmpty();
	}
	
	public Iterator<LogoAtom> atoms(){
		return list.iterator();
	}
	
	public LogoAtom getAtom(int index){
		return list.get(index);
	}
	
	public int count(){
		return list.size();
	}

//	public Object getValue() {
//		return this;
//	}
	
	private void toString(StringBuffer buf, LogoAtom atom ){
		if ( atom instanceof LogoList) {
			buf.append('[').append(atom.toString()).append(']');
		}
		else buf.append(atom.toString());
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer();
		int size = list.size();
		if ( size > 0 ) toString(buf, list.get(0));
		for(int i = 1; i < size; ++ i){
			buf.append(' ');
			toString(buf, list.get(i));
		}
		return buf.toString();
	}

	public LogoList clone(){
		LogoList newList = new LogoList();
		newList.list.addAll(list);
		return newList;
	}
}
