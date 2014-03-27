package com.unidevel.logo.script;

public class LogoToken {
	public static int TYPE_LIST_START = 1;
	public static int TYPE_LIST_END = 2;
	public static int TYPE_TOKEN = 0;
	public static int TYPE_NULL = -1;
	
	public static final LogoToken NULL = new LogoToken(TYPE_NULL);
	public static final LogoToken LIST_START = new LogoToken(TYPE_LIST_START);
	
	String token;
	int type;
	
	public LogoToken(int type){
		this.type = type;
	}
	
	
	public LogoToken(int type, String token){
		this.type = type;
		this.token = token;
	}
	
	public LogoToken(String token){
		this.type = TYPE_TOKEN;
		this.token = token;
	}
	
	public String toString(){
		return token;
	}
	
	public boolean isEmpty(){
		return token == null || token.length() == 0;
	}

	public int getType() {
		return type;
	}
}

