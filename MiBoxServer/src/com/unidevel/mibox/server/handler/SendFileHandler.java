package com.unidevel.mibox.server.handler;

import java.io.File;
import java.io.RandomAccessFile;
import android.content.Context;
import android.os.Environment;
import com.unidevel.mibox.data.SendFileRequest;
import com.unidevel.mibox.data.SendFileResponse;

public class SendFileHandler extends MiBoxRequestHandler<SendFileRequest, SendFileResponse>
{
	@Override
	public SendFileResponse handleRequest( Context context, SendFileRequest request )
	{
		SendFileResponse result = new SendFileResponse();
		File dir = null;
		if ( Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) )
		{
			dir = new File( Environment.getExternalStorageDirectory() + "/miboxserver" ); //$NON-NLS-1$
		}
		else
		{
			dir = context.getDir( "miboxserver", Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE );
		}
		if ( !dir.exists() )
			dir.mkdirs();
		File file = new File( dir, request.name );
		try
		{
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

}
