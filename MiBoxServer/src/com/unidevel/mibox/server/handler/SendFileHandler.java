package com.unidevel.mibox.server.handler;

import java.io.File;
import java.io.RandomAccessFile;
import android.content.Context;
import android.util.Log;
import com.unidevel.mibox.data.SendFileRequest;
import com.unidevel.mibox.data.SendFileResponse;

public class SendFileHandler extends MiBoxRequestHandler<SendFileRequest, SendFileResponse>
{
	@SuppressWarnings ("deprecation")
	@Override
	public SendFileResponse handleRequest( Context context, SendFileRequest request )
	{
		boolean isTemp = false;
		SendFileResponse result = new SendFileResponse();
		File dir = null;
		if ( request.remoteDir != null && request.remoteDir.length() > 0 )
		{
			dir = new File( request.remoteDir );
		}
		else
		{
			dir = context.getDir( "tmp", Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE ); //$NON-NLS-1$
			isTemp = true;
		}
		if ( !dir.exists() )
			dir.mkdirs();
		try
		{
			String name = request.remoteName;
			if ( name == null || name.length() == 0 )
			{
				name = request.name;
			}
			File file = new File( dir, name );
			RandomAccessFile f = new RandomAccessFile( file, "rw" ); //$NON-NLS-1$
			if ( request.offset > 0 )
				f.seek( request.offset );

			f.write( request.block );
			f.close();
			if ( isTemp )
			{
				chmod( dir.getPath(), "0777" ); //$NON-NLS-1$
			}
			result.remotePath = file.getPath();
		}
		catch (Exception e)
		{
			result.failed = true;
			result.failedMessage = e.getMessage();
		}
		return result;
	}

	public void chmod( String path, String mode )
	{
		Runtime runtime = Runtime.getRuntime();
		String command = "chmod -R " + mode + " " + path; //$NON-NLS-1$ //$NON-NLS-2$
		Process proc;
		try
		{
			proc = runtime.exec( command );
			proc.waitFor();
		}
		catch (Exception e)
		{
			Log.e( "chmod", command + " failed:" + e.getMessage(), e ); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
