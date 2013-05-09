package com.unidevel.mibox.launcher.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import com.unidevel.mibox.data.DisconnectRequest;
import com.unidevel.mibox.data.GetAppIconRequest;
import com.unidevel.mibox.data.GetAppIconResponse;
import com.unidevel.mibox.data.InstallAppRequest;
import com.unidevel.mibox.data.InstallAppResponse;
import com.unidevel.mibox.data.ListAppRequest;
import com.unidevel.mibox.data.ListAppResponse;
import com.unidevel.mibox.data.MiBoxRequest;
import com.unidevel.mibox.data.MiBoxResponse;
import com.unidevel.mibox.data.SendFileRequest;
import com.unidevel.mibox.data.SendFileResponse;
import com.unidevel.mibox.data.StartAppRequest;
import com.unidevel.mibox.data.StartAppResponse;

public class MiBoxClient
{
	Socket socket;
	ObjectInputStream in;
	ObjectOutputStream out;

	public MiBoxClient()
	{
	}

	public void connect( String host, int port ) throws StreamCorruptedException, IOException
	{
		this.socket = new Socket( host, port );
	}

	public MiBoxResponse sendRecv( MiBoxRequest request ) throws IOException, ClassNotFoundException
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

	public InstallAppResponse installApp( String path ) throws IOException, ClassNotFoundException
	{
		File file = new File( path );
		SendFileRequest request = new SendFileRequest();
		request.name = file.getName();
		request.size = file.length();
		request.offset = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte buf[] = new byte[ 8192 ];
		int len;
		FileInputStream in = new FileInputStream( file );
		while ( (len = in.read( buf )) > 0 )
		{
			out.write( buf, 0, len );
		}
		in.close();
		out.close();
		request.block = out.toByteArray();

		SendFileResponse response = (SendFileResponse)sendRecv( request );
		InstallAppResponse result = new InstallAppResponse();
		if ( !response.failed )
		{
			InstallAppRequest request2 = new InstallAppRequest();
			request2.remotePath = response.remotePath;
			result = (InstallAppResponse)sendRecv( request2 );
		}
		else
		{
			result.failed = response.failed;
			result.failedMessage = response.failedMessage;
		}
		return result;
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
			if ( this.out != null )
			{
				this.out.writeObject( new DisconnectRequest() );
			}
		}
		catch (Exception e)
		{
		}
		try
		{
			if ( this.in != null )
			{
				this.in.close();
			}
		}
		catch (Exception e)
		{
		}
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
		this.in = null;
		this.out = null;
		this.socket = null;
	}
}
