
package com.unidevel.mibox.launcher.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import android.util.Log;

public class MiBoxRemoter
{
	public static final int KEY_CODE_BACK = 4;
	public static final int KEY_CODE_DOWN = 20;
	public static final int KEY_CODE_HOME = 3;
	public static final int KEY_CODE_LEFT = 21;
	public static final int KEY_CODE_MENU = 82;
	public static final int KEY_CODE_OK = 66;
	public static final int KEY_CODE_RIGHT = 22;
	public static final int KEY_CODE_UP = 19;
	public static final String LOCAL_BC_USER_OPERATION = "LOCAL_BC_USER_OPERATION";
	public static final String POINT_CHANGED = "POINT_CHANGED";
	public static final int SCREEN_SAVER_TIME = 15000;

	Socket socket;
	byte[] buffer;
	int order = 11;

	public MiBoxRemoter()
	{
		byte[] arrayOfByte = new byte[ 68 ];
		arrayOfByte[ 0 ] = 4;
		arrayOfByte[ 1 ] = 0;
		arrayOfByte[ 2 ] = 65;
		arrayOfByte[ 3 ] = 1;
		arrayOfByte[ 4 ] = 0;
		arrayOfByte[ 5 ] = 0;
		arrayOfByte[ 6 ] = 0;
		arrayOfByte[ 7 ] = 0;
		arrayOfByte[ 8 ] = 0;
		arrayOfByte[ 9 ] = 58;
		arrayOfByte[ 10 ] = 1;
		arrayOfByte[ 11 ] = 0;
		arrayOfByte[ 12 ] = 0;
		arrayOfByte[ 13 ] = 0;
		arrayOfByte[ 14 ] = 0;
		arrayOfByte[ 15 ] = 2;
		arrayOfByte[ 16 ] = 0;
		arrayOfByte[ 17 ] = 0;
		arrayOfByte[ 18 ] = 0;
		arrayOfByte[ 19 ] = 0;
		arrayOfByte[ 20 ] = 3;
		arrayOfByte[ 21 ] = 0;
		arrayOfByte[ 22 ] = 0;
		arrayOfByte[ 23 ] = 0;
		arrayOfByte[ 24 ] = 20;
		arrayOfByte[ 25 ] = 4;
		arrayOfByte[ 26 ] = 0;
		arrayOfByte[ 27 ] = 0;
		arrayOfByte[ 28 ] = 0;
		arrayOfByte[ 29 ] = 0;
		arrayOfByte[ 30 ] = 5;
		arrayOfByte[ 31 ] = 0;
		arrayOfByte[ 32 ] = 0;
		arrayOfByte[ 33 ] = 0;
		arrayOfByte[ 34 ] = 0;
		arrayOfByte[ 35 ] = 5;
		arrayOfByte[ 36 ] = 0;
		arrayOfByte[ 37 ] = 0;
		arrayOfByte[ 38 ] = 0;
		arrayOfByte[ 39 ] = 0;
		arrayOfByte[ 40 ] = 7;
		arrayOfByte[ 41 ] = 0;
		arrayOfByte[ 42 ] = 0;
		arrayOfByte[ 43 ] = 0;
		arrayOfByte[ 44 ] = 0;
		arrayOfByte[ 45 ] = 0;
		arrayOfByte[ 46 ] = 0;
		arrayOfByte[ 47 ] = 0;
		arrayOfByte[ 48 ] = 0;
		arrayOfByte[ 49 ] = 8;
		arrayOfByte[ 50 ] = 0;
		arrayOfByte[ 51 ] = 0;
		arrayOfByte[ 52 ] = 0;
		arrayOfByte[ 53 ] = 0;
		arrayOfByte[ 54 ] = 0;
		arrayOfByte[ 55 ] = 0;
		arrayOfByte[ 56 ] = 0;
		arrayOfByte[ 57 ] = 0;
		arrayOfByte[ 58 ] = 10;
		arrayOfByte[ 59 ] = -1;
		arrayOfByte[ 60 ] = -1;
		arrayOfByte[ 61 ] = -1;
		arrayOfByte[ 62 ] = -1;
		arrayOfByte[ 63 ] = 11;
		arrayOfByte[ 64 ] = 0;
		arrayOfByte[ 65 ] = 0;
		arrayOfByte[ 66 ] = 0;
		arrayOfByte[ 67 ] = 0;
		this.buffer = arrayOfByte;
	}

	public void connect( String host, int port ) throws StreamCorruptedException, IOException
	{
		this.socket = new Socket( host, port );
	}

	public boolean isConnected()
	{
		return this.socket != null && this.socket.isConnected();
	}

	public void disconnect()
	{
		if ( this.socket == null || !this.socket.isConnected() )
			return;
		try
		{
			if ( this.socket != null )
			{
				this.socket.close();
			}
		}
		catch (Exception e)
		{
		}
		this.socket = null;
	}

	public void sendKeyCode( int paramInt )
	{
		DataOutputStream out = null;
		if ( (this.socket != null) && (this.socket.isConnected()) )
			out = null;
		try
		{
			OutputStream socketOut = this.socket.getOutputStream();
			out = new DataOutputStream( socketOut );
			order = 1 + order;
			this.buffer[ 24 ] = (byte)paramInt;
			this.buffer[ 7 ] = (byte)order;
			this.buffer[ 19 ] = 0;
			out.write( this.buffer, 0, 68 );
			Thread.sleep( 20L );
			order = 1 + order;
			this.buffer[ 19 ] = 1;
			this.buffer[ 7 ] = (byte)order;
			out.write( this.buffer, 0, 68 );
			if ( order > 120 )
				order = 11;
		}
		catch (Exception ex)
		{
			Log.e( "sendKeyCode", ex.getMessage(), ex );
		}
	}
}
