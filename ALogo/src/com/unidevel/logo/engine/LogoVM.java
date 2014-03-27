package com.unidevel.logo.engine;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.unidevel.logo.script.LogoAtom;
import com.unidevel.logo.script.LogoList;
import com.unidevel.logo.script.LogoToken;
import com.unidevel.logo.script.LogoWord;

public class LogoVM {
	
	public void eval(LogoContext context, String s) throws LogoEvalException {
		LogoList list = null;
		try {
			list = interpret(s);
		} catch (IOException e) {
			throw new LogoEvalException(e.getMessage());
		}
		context.eval(list);
	}
	
	protected void error(String msg, LogoAtom atom) throws LogoEvalException {
		throw new LogoEvalException(msg+atom.toString());
	}
	
	protected LogoList interpret(String s) throws IOException {
		Reader in = new StringReader(s);
		LogoList list = new LogoList();
		interpret(list, in);
		return list;
	}
	
	protected void interpret(LogoList list, Reader in) throws IOException{
		LogoToken token;
		for (token = nextToken(in); token != LogoToken.NULL; token = nextToken(in) ){
			if (token == LogoToken.LIST_START){
				LogoList childList = new LogoList();
				list.append(childList);
				interpret(childList, in);
			}
			else if ( token.getType() == LogoToken.TYPE_LIST_END ) {
				if ( !token.isEmpty() )	{
					list.append(newWord(token));
				}
				break;
			}
			else {
				list.append(newWord(token));
			}
		}
	}
	
	private LogoWord newWord(LogoToken token){
		LogoWord word = new LogoWord(token.toString());
		if ( word.isQuote() ) {
			word.setValue(word.getWord());
		}
		if ( word.isInteger() ) {
			word.setValue(Integer.valueOf(word.getWord()));
		}
		else if ( word.isFloat() ) {
			word.setValue(Float.valueOf(word.getWord()));
		}
		return word;
	}
	
	public boolean isSpace(int ch){
		return ch == ' ' || ch =='\t' || ch == '\r' || ch == '\n' || Character.isSpaceChar(ch);
	}
	
	public LogoToken nextToken(Reader in) throws IOException{
		int ch;
		for ( ch = in.read(); ch >= 0; ch = in.read() ) {
			if  ( !isSpace(ch) ) break;
		}
		if ( ch < 0 ) return LogoToken.NULL;
		switch ( ch ) {
		case '[': 
			return LogoToken.LIST_START;
		default:
			{
				StringBuffer buf = new StringBuffer();
				boolean escape = false;
				int type = LogoToken.TYPE_TOKEN;
				for (; ch >= 0; ch = in.read()){
					if ( ch == '\\' && !escape ) {escape = true;continue;}
					if ( escape ) { buf.append((char)ch); continue; }
					if ( isSpace(ch) )break;
					if ( ch == ']' ) {
						type = LogoToken.TYPE_LIST_END;
						break;
					}
					buf.append((char)ch);
				}
				return new LogoToken(type, buf.toString());
			}
		}
	}
	
	
	public static void main(String[] args) throws Exception{
		LogoVM vm = new LogoVM();
//		vm.eval(new ConsoleContext(), "print [hello world]");
//		vm.eval(new ConsoleContext(), "print \"10");
//		vm.eval(new ConsoleContext(), "make \"a 1 print :a");
//		vm.eval(new ConsoleContext(), "make \"a sum 1 2 print :a");
//		vm.eval(new ConsoleContext(), "make \"a sum 1 sum 2 3 print :a");
//		vm.eval(new ConsoleContext(), "repeat 4 [print [hello world]]");
//		vm.eval(new ConsoleContext(), "REPEAT 20 [PRINT RANDOM 12]");
//		vm.eval(new ConsoleContext(), "TO ECHO :TEXT PRINT :TEXT END ECHO \"Hello");
//		vm.eval(new ConsoleContext(), "TO SQUARE :SIZE REPEAT 4 [PRINT :SIZE] END SQUARE 20");
//		vm.eval(new ConsoleContext(), "TO ECHO :times :thing\r\nREPEAT :times [PRINT :thing]\r\n\tEND \r\n ECHO 10 \"TEST");
//		vm.eval(new ConsoleContext(), "TO ECHO :times :thing\r\nREPEAT SUM :times 10 [PRINT :thing]\r\n\tEND \r\n ECHO 10 :PI");
//		vm.eval(new ConsoleContext(), "MAKE \"X 10 IFELSE :X > 10 [PRINT :X] [PRINT \"HELLO]");
		vm.eval(new ConsoleContext(), "MAKE \"X 10 IFELSE FALSE [PRINT :X] [PRINT \"HELLO]");
	}
}
