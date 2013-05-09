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
		SendFileResponse result = new SendFileResponse();
		File dir = null;
		// if ( Environment.getExternalStorageState().equals(
		// Environment.MEDIA_MOUNTED ) )
		// {
		//			dir = new File( Environment.getExternalStorageDirectory() + "/miboxserver" ); //$NON-NLS-1$
		// }
		// else
		// {
		dir = context.getDir( "miboxserver", Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE ); //$NON-NLS-1$
		// dir = new File( "/data/local/tmp/" );
		// // dir = new File( "/tmp/mibox" );
		// }
		if ( !dir.exists() )
			dir.mkdirs();
		chmod( dir.getPath(), "0777" );
		// File file = File.createTempFile( request.name, ".apk" ); // ( dir,
																	// request.name
																	// );
		try
		{
			File file = new File( dir, request.name );
			RandomAccessFile f = new RandomAccessFile( file, "rw" ); //$NON-NLS-1$
			if ( request.offset > 0 )
				f.seek( request.offset );

			f.write( request.block );
			f.close();
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
		String command = "chmod -R " + mode + " " + path;
		Process proc;
		try
		{
			proc = runtime.exec( command );
			proc.waitFor();
		}
		catch (Exception e)
		{
			Log.e( "chmod", command + " failed:" + e.getMessage(), e );
		}
	}
}
