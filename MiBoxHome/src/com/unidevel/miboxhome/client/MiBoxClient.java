package com.unidevel.miboxhome.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import com.unidevel.miboxhome.data.DisconnectRequest;
import com.unidevel.miboxhome.data.GetAppIconRequest;
import com.unidevel.miboxhome.data.GetAppIconResponse;
import com.unidevel.miboxhome.data.ListAppRequest;
import com.unidevel.miboxhome.data.ListAppResponse;
import com.unidevel.miboxhome.data.MiBoxRequest;
import com.unidevel.miboxhome.data.MiBoxResponse;
import com.unidevel.miboxhome.data.StartAppRequest;
import com.unidevel.miboxhome.data.StartAppResponse;

public class MiBoxClient
{
	Socket socket;
	ObjectInputStream in;
	ObjectOutputStream out;
	String host;
	int port;

	public MiBoxClient( String host, int port )
	{
		this.host = host;
		this.port = port;
	}

	public void connect() throws StreamCorruptedException, IOException
	{
		this.socket = new Socket( host, port );
	}

	private MiBoxResponse sendRecv( MiBoxRequest request ) throws IOException, ClassNotFoundException
	{
		if ( this.out == null )
		{
			this.out = new ObjectOutputStream( this.socket.getOutputStream() );
		}
		this.out.writeObject( request );
		if ( this.in == null )
		{
			this.in = new ObjectInputStream( this.socket.getInputStream() );
		}
		MiBoxResponse response = (MiBoxResponse)this.in.readObject();
		return response;
	}

	public ListAppResponse listApps() throws IOException, ClassNotFoundException
	{
		ListAppRequest request = new ListAppRequest();
		ListAppResponse response = (ListAppResponse)sendRecv( request );
		return response;
	}
	
	public StartAppResponse startApp( String packageName, String className ) throws IOException, ClassNotFoundException
	{
		StartAppRequest request = new StartAppRequest();
		request.packageName = packageName;
		request.className = className;

		StartAppResponse response = (StartAppResponse)sendRecv( request );
		return response;
	}

	public GetAppIconResponse getIcon( String packageName, String className ) throws IOException,
			ClassNotFoundException
	{
		GetAppIconRequest request = new GetAppIconRequest();
		request.packageName = packageName;
		request.className = className;
		GetAppIconResponse response = (GetAppIconResponse)sendRecv( request );
		return response;
	}

	public void disconnect()
	{
		try
		{
			this.out.writeObject( new DisconnectRequest() );
		}
		catch (IOException e)
		{
		}
		try
		{
			this.in.close();
		}
		catch (IOException e)
		{
		}
		try
		{
			this.out.close();
		}
		catch (IOException e)
		{
		}
		try
		{
			this.socket.close();
		}
		catch (IOException e)
		{
		}
		this.in = null;
		this.out = null;
		this.socket = null;
	}
}
