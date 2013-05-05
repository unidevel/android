
package com.unidevel.miboxhome.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.unidevel.miboxhome.data.Constants;
import com.unidevel.miboxhome.data.DisconnectRequest;
import com.unidevel.miboxhome.data.MiBoxRequest;
import com.unidevel.miboxhome.data.MiBoxResponse;

public class MiBoxServer implements Runnable
{
	public static final String SERVICE_ACTION = "com.unidevel.miboxserver.START_SERVER";

	class ClientThread extends Thread
	{
		Socket socket;
		ObjectInputStream in;
		ObjectOutputStream out;

		public ClientThread( Socket socket )
		{
			this.socket = socket;
		}

		@Override
		public void run()
		{
			addClientThread( this );
			try
			{
				this.in = new ObjectInputStream( this.socket.getInputStream() );
				this.out = new ObjectOutputStream( this.socket.getOutputStream() );
				handleClient( this.in, this.out );
			}
			catch (Exception ex)
			{
				Log.e( "MiBoxServer.handleClient", ex.getMessage(), ex ); //$NON-NLS-1$
			}
			finally
			{
				try
				{
					this.in.close();
				}
				catch (Throwable ex)
				{
				}
				try
				{
					this.out.close();
				}
				catch (Throwable ex)
				{
				}
				removeClientThread( this );
			}
		}

		public void stopThis()
		{
			try
			{
				this.socket.close();
			}
			catch (IOException e)
			{
				Log.e( "ClientThread.close", e.getMessage(), e ); //$NON-NLS-1$
			}
			try
			{
				this.join();
			}
			catch (InterruptedException e)
			{
				Log.e( "ClientThread.join", e.getMessage(), e ); //$NON-NLS-1$
			}
		}
	}

	int port;
	boolean stop = false;
	ServerSocket serverSocket = null;
	Thread serverThread = null;
	List<ClientThread> clientThreads = null;
	Context context;
	MiBoxRequestHandlerManager requestHandlerManager;

	public MiBoxServer( Context context, int port ) throws IOException
	{
		this.port = port;
		this.context = context;
		this.serverSocket = new ServerSocket( port );
		this.requestHandlerManager = new MiBoxRequestHandlerManager( context );
	}

	public void handleClient( ObjectInputStream in, ObjectOutputStream out ) throws OptionalDataException,
			ClassNotFoundException, IOException
	{
		Object request;
		while ( (request = in.readObject()) != null )
		{
			if ( request instanceof DisconnectRequest )
			{
				break;
			}
			else if ( request instanceof MiBoxRequest )
			{
				MiBoxResponse response = this.requestHandlerManager.handleRequest( (MiBoxRequest)request );
				if ( response == null )
					break;
				out.writeObject( response );
			}
		}
	}

	public void start()
	{
		if ( this.serverThread != null )
			return;
		this.serverThread = new Thread( this );
		this.clientThreads = new ArrayList<ClientThread>();
		this.serverThread.start();
	}

	public void stop()
	{
		if ( this.serverThread == null )
			return;
		this.stop = true;
		try
		{
			this.serverSocket.close();
		}
		catch (IOException e)
		{
			Log.e( "Server.close", e.getMessage(), e ); //$NON-NLS-1$
		}
		try
		{
			this.serverThread.join();
		}
		catch (InterruptedException e)
		{
		}
		this.serverThread = null;
		stopClientThreads();
		return;
	}

	protected void addClientThread( ClientThread thread )
	{
		synchronized (this.clientThreads)
		{
			this.clientThreads.add( thread );
		}
	}

	protected void removeClientThread( ClientThread thread )
	{
		synchronized (this.clientThreads)
		{
			this.clientThreads.remove( thread );
		}
	}

	protected void stopClientThreads()
	{
		List<ClientThread> threads;
		synchronized (this.clientThreads)
		{
			threads = Collections.unmodifiableList( this.clientThreads );
		}
		for ( ClientThread thread : threads )
		{
			if ( thread.isAlive() )
				thread.stopThis();
		}
	}

	public void run()
	{
		JmDNS jmdns = null;
		WifiManager.MulticastLock socketLock = null;
		try
		{
			WifiManager wm = (WifiManager)context.getSystemService( Context.WIFI_SERVICE );
			socketLock = wm.createMulticastLock( Constants.SERVICE_LOCK_NAME );
			socketLock.acquire();
			int i = wm.getConnectionInfo().getIpAddress();
			byte[] arrayOfByte = new byte[ 4 ];
			arrayOfByte[ 0 ] = (byte)(i & 0xFF);
			arrayOfByte[ 1 ] = (byte)(0xFF & i >> 8);
			arrayOfByte[ 2 ] = (byte)(0xFF & i >> 16);
			arrayOfByte[ 3 ] = (byte)(0xFF & i >> 24);
			InetAddress localInetAddress = InetAddress.getByAddress( arrayOfByte );
			jmdns = JmDNS.create( localInetAddress, InetAddress.getByName( localInetAddress.getHostName() ).toString() );

			ServiceInfo serviceInfo =
					ServiceInfo.create( Constants.JMDNS_TYPE, Constants.SERVICE_NAME, Constants.SERVICE_PORT,
							"Hello world!" );
			jmdns.registerService( serviceInfo );
			while ( !this.stop )
			{
				try
				{
					Socket socket = this.serverSocket.accept();
					ClientThread thread = new ClientThread( socket );
					thread.start();
				}
				catch (SocketException ex)
				{
					// close outside the thread.
					break;
				}
				catch (IOException ex)
				{
					Log.e( "Server.run", ex.getMessage(), ex ); //$NON-NLS-1$
				}
			}
		}
		catch (IOException e)
		{
			Log.e( "startServer", e.getMessage(), e );
		}
		finally
		{
			if ( jmdns != null )
				jmdns.unregisterAllServices();
			if ( socketLock != null )
				socketLock.release();
		}
	}
}