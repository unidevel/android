package com.unidevel.logo.script;

import java.util.regex.Pattern;

public class LogoWord extends LogoAtom{
	String word;
	Object value;
	
	public LogoWord(String word){
		this.word = word;
	}
	
	public String getRawWord() {
		return word;
	}
	
	public boolean isColon(){
		return word.charAt(0) == ':';
	}
	
	public boolean isQuote(){
		return word.charAt(0) == '"';
	}
	
	public boolean isInteger(){
		return Pattern.matches("[+-]?[0-9]+", getWord());
	}
	
	public boolean isFloat(){
		return Pattern.matches("[+-]?[0-9]*.[0-9]+", getWord());
	}
	
	public boolean isNumber(){
		return isInteger() || isFloat();
	}
	
	public boolean isBool(){
		return "TRUE".equalsIgnoreCase(word) || "FALSE".equalsIgnoreCase(word);
	}
	
	public String getWord(){
		if ( isColon() || isQuote() ) return word.substring(1);
		return word;
	}
	
	public String toString(){
		return word;
	}
	
	public float toFloat(){
		return Float.valueOf(word).floatValue();
	}
	
	public int toInt(){
		return Integer.valueOf(word).intValue();
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public static void main(String[] args){
		System.err.println(new LogoWord("-1234").isInteger());
		System.err.println(new LogoWord("+1234").isInteger());
		System.err.println(new LogoWord("1234").isInteger());
		System.err.println(new LogoWord("12.34").isFloat());
		System.err.println(new LogoWord("+12.34").isFloat());
		System.err.println(new LogoWord("-12.34").isFloat());
		System.err.println(new LogoWord("-.34").isFloat());
		System.err.println(new LogoWord("+.34").isFloat());
		System.err.println(new LogoWord(".34").isFloat());
	}

	public boolean toBool() {
		return Boolean.valueOf(word);
	}
}
