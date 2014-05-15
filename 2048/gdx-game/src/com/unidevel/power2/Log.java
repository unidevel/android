package com.unidevel.power2;
import com.badlogic.gdx.*;

public class Log
{
	public static void i(String fmt, Object ...args){
		Gdx.app.log(Power2Game.class.getSimpleName(),String.format(fmt,args));
	}

	public static void e(Throwable ex){
		StackTraceElement[] stack=ex.getStackTrace();
		String tag;
		if(stack.length>0){
			tag=stack[0].getClassName();
		}
		else {
			tag=Power2Game.class.getSimpleName();
		}
		Gdx.app.log(tag,ex.getMessage(),ex);
	}
}
