
package com.unidevel.power2;

import java.io.File;
import java.nio.ByteBuffer;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.BufferUtils;

public class Power2Game extends InputAdapter implements ApplicationListener
{
	static final int SIZE = 4;
	Texture texture;
	SpriteBatch batch;
	TriangleBlocks blocks;
	int size;
	BitmapFont font;
	ShapeRenderer shapeRenderer;
	OrthographicCamera cam;
	int sw, sh;
	float[][] points;
	float[] npos;
	boolean over;
	boolean screenshot;
	int maxScore;
	GameListener listener;

	public Power2Game()
	{
		this.blocks = new TriangleBlocks( SIZE );
	}

	public void setGameListener( GameListener l )
	{
		this.listener = l;
	}

	public void create()
	{
		sw = Gdx.graphics.getWidth();
		sh = Gdx.graphics.getHeight();
		Gdx.graphics.setContinuousRendering( false );
		this.cam = new OrthographicCamera( sw, sh );
		cam.position.set( sw / 2 - 1, sh / 2, 0 );
		this.shapeRenderer = new ShapeRenderer();
		font = new BitmapFont( Gdx.files.internal( "verdana39.fnt" ) );
		// Gdx.input.setInputProcessor(new GestureDetector(new Controller()));
		Gdx.input.setInputProcessor( this );
		batch = new SpriteBatch();
		startGame();
	}

	private void startGame()
	{
		this.prepareBlocks( SIZE, 48 );
		over = false;
		boolean isResume = false;
		for ( Box b : blocks.data )
		{
			if ( b.value != 0 )
				isResume = true;
		}
		if ( isResume )
		{
			if ( !blocks.canMove() )
			{
				setGameOver();
			}
		}
		else
		{
			blocks.fill();
		}
		Gdx.graphics.requestRendering();
	}

	public void newGame()
	{
		this.blocks = new TriangleBlocks( SIZE );
		this.startGame();
	}

	public void newFlashScreen()
	{
		this.maxScore = 99999;
		this.blocks = new TriangleBlocks( SIZE );
		this.blocks.score = 99999;
		for (int i =0, n=0 ; i < blocks.data.length; i++ )
		{
			blocks.data[i].value = n;
			if ( n == 0 )
				n = 2;
			else
				n += n;
		}
		over = false;
	}
	
	void prepareBlocks( int n, float ypos )
	{
		points = new float[ n * n ][];
		npos = new float[ n * n * 2 ];
		float len = sw > sh
				? sh - 4
				: sw - 4;
		float x = len / 2.0f;
		float y = ypos;
		float dx = len / (float)n / 2.0f;
		float cos60 = (float)Math.cos( 60 / 180 * 3.1415926359 );

		float dy = dx * 2.0f * cos60;
		float ty = dy * 2f / 3f;
		int p = 0;
		float sx = 4.0f * cos60;
		float sy = 4.0f;
		for ( int i = 0; i < n; ++i )
		{
			float xx = x;
			for ( int j = 0; j <= 2 * i; ++j, ++p )
			{
				npos[ p * 2 ] = x;
				if ( j % 2 == 0 )
				{
					points[ p ] = new float[ 6 ];
					points[ p ][ 0 ] = x;
					points[ p ][ 1 ] = y + sy;
					points[ p ][ 2 ] = x - dx + sx;
					points[ p ][ 3 ] = y + dy - sy;
					points[ p ][ 4 ] = x + dx - sx;
					points[ p ][ 5 ] = y + dy - sy;
					npos[ p * 2 + 1 ] = y + sy + ty;
				}
				else
				{
					points[ p ] = new float[ 6 ];
					points[ p ][ 0 ] = x;
					points[ p ][ 1 ] = y + dy - sy;
					points[ p ][ 2 ] = x - dx + sx;
					points[ p ][ 3 ] = y + sy;
					points[ p ][ 4 ] = x + dx - sx;
					points[ p ][ 5 ] = y + sy;
					npos[ p * 2 + 1 ] = y + sy;
				}
				translate( points[ p ] );
				x += dx;
			}
			x = xx - dx;
			y += dy;
		}
		translate( npos );
	}

	Texture textureFromPixmap( Gdx2DPixmap pixmap )
	{
		Texture texture = new Texture( pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGBA4444 );
		texture.bind();
		Gdx.gl.glTexImage2D( GL20.GL_TEXTURE_2D, 0, pixmap.getGLInternalFormat(), pixmap.getWidth(),
				pixmap.getHeight(), 0, pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels() );
		return texture;
	}

	public Gdx2DPixmap createBlocks()
	{
		int size = 256;
		Gdx2DPixmap p = new Gdx2DPixmap( size, size, Gdx2DPixmap.GDX2D_FORMAT_RGBA4444 );
		p.fillRect( 0, 0, size, size, Color.rgba4444( 0, 0, 0, 1 ) );
		p.fillTriangle( size / 2, size / 2, 0, size - 1, size - 1, size - 1, Color.rgba4444( 0.2f, 0.2f, 0.2f, 1.0f ) );
		return p;
	}

	private void translate( float[] p )
	{
		for ( int i = 0; i < p.length; ++i )
		{
			if ( i % 2 == 1 )
			{
				// p[i]=sh-sh/6f-p[i];
				p[ i ] = sh - p[ i ] - 80;
			}
		}
	}

	private float drawScore( BitmapFont font, boolean left, float offx, float offy, String s1, int n )
	{
		float textScale = 0.5f;
		float scoreScale = 1.0f;
		BitmapFont.TextBounds b1, b2;
		float x, y, w, h;
		float w1, w2, h1, h2;
		String s2 = String.format( "%05d", n );
		font.setScale( textScale );
		b1 = font.getBounds( s1 );
		w1 = b1.width;
		h1 = b1.height;
		font.setScale( scoreScale );
		b2 = font.getBounds( s2 );
		w2 = b2.width;
		h2 = b2.height;
		h = h1 + 6 + h2;
		w = w2 > w1
				? w2
				: w1;
		w += 4;
		x = offx;
		y = h + offy;
		ShapeRenderer s = shapeRenderer;
		s.begin( ShapeType.Filled );
		s.setColor( Color.GRAY );
		if ( left )
		{
			s.rect( x, sh - y, w, h );
		}
		else
		{
			s.rect( sw - x - w, sh - y, w, h );
		}
		s.end();

		batch.begin();
		font.setColor( 0.7f, 0.7f, 0.7f, 1.0f );
		font.setScale( textScale );
		if ( left )
		{
			font.draw( batch, s1, x + (w - w1) / 2f + 2, sh - (y + 2 - h) );
		}
		else
		{
			font.draw( batch, s1, sw - (x + (w - w1) / 2f + 2) - w1, sh - (y + 2 - h) );
		}
		font.setColor( 1f, 1f, 1f, 1.0f );
		font.setScale( scoreScale );
		if ( left )
		{
			font.draw( batch, s2, x + 2, sh - (y + 2 - h + h1) );
		}
		else
		{
			font.draw( batch, s2, sw - (x - 2) - w, sh - (y + 2 - h + h1) );
		}
		batch.end();

		return w;
	}
	
	private void setColor(ShapeRenderer render, int value)
	{
		if ( value < 2 )
		{
			render.setColor( 0f, 0f, 0f, 1f );
		}
		else if ( value < 4  )
		{
			render.setColor( 0.4f, 0.4f, 0.4f, 1f );	
		}
		else if ( value < 8 )
		{
			render.setColor( 0.4f, 0.4f, 0.5f, 1f );
		}
		else if ( value < 16 )
		{
			render.setColor( 0.6f, 0.5f, 0.4f, 1f );
		}
		else if ( value < 32 )
		{
			render.setColor( 0.6f, 0.6f, 0.4f, 1f );
		}
		else if ( value < 64 )
		{
			render.setColor( 0.4f, 0.6f, 0.4f, 1f );
		}
		else if ( value < 128 )
		{
			render.setColor( 0.4f, 0.6f, 0.6f, 1f );
		}
		else if ( value < 256 )
		{
			render.setColor( 0.4f, 0.5f, 0.6f, 1f );
		}
		else if ( value < 512 )
		{
			render.setColor( 0.6f, 0.8f, 0.8f, 1f );
		}
		else if ( value < 1024 )
		{
			render.setColor( 0.6f, 0.8f, 0.6f, 1f );
		}
		else if ( value < 2048 )
		{
			render.setColor( 0.8f, 0.6f, 0.6f, 1f );
		}
		else if ( value < 4096 )
		{
			render.setColor( 0.8f, 0.4f, 0.4f, 1f );
		}
		else if ( value < 8192 )
		{
			render.setColor( 0.8f, 0.6f, 0.4f, 1f );
		}
		else if ( value < 16384 )
		{
			render.setColor( 0.8f, 0.4f, 0.8f, 1f );
		}
		else if ( value < 32768 )
		{
			render.setColor( 0.9f, 0.6f, 0.8f, 1f );
		}
		else
		{
			render.setColor( 1f, 0.4f, 0.4f, 1f );
		}
	}

	public void render()
	{
		Gdx.gl.glViewport( 0, 0, sw, sh );
		Gdx.gl.glClearColor( 0, 0, 0, 1 );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

		ShapeRenderer s = shapeRenderer;

		cam.update();
		s.setProjectionMatrix( cam.combined );
		s.begin( ShapeType.Filled );
		s.setColor( 0.8f, 0.8f, 0.8f, 1.0f );
		s.rect( 0, 0, sw - 1, sh - 1 );
		int[] v = blocks.getValues();
//		float ln2 = (float)Math.log( 2 );
		for ( int i = 0; i < points.length; ++i )
		{
//			float c = (float)Math.log( v[ i ] ) / ln2 / 4.0f;
//			s.setColor( c, 0, c, 1 );
			setColor(s, v[i]);
			float p[] = points[ i ];
			// translate(p);
			s.triangle( p[ 0 ], p[ 1 ], p[ 2 ], p[ 3 ], p[ 4 ], p[ 5 ] );
		}
		s.end();

		batch.begin();// batch.
		font.setColor( Color.WHITE );
		font.setScale( 1.0f );
		float sw = font.getSpaceWidth();
		// float lh=font.getLineHeight();
		for ( int i = 0; i < v.length; ++i )
		{
			int value = v[ i ];
			if ( value > 0 )
			{
				String sv = String.valueOf( value );
				float x = npos[ 2 * i ] - sw * ((float)sv.length());
				float y = npos[ 2 * i + 1 ];
				font.draw( batch, "" + value, x, y );
			}
		}

		batch.end();
		if ( maxScore < blocks.score )
		{
			maxScore = blocks.score;
		}
		drawScore( font, true, 10f, 10f, "SCORE", blocks.score );
		drawScore( font, false, 10f, 10f, "BEST", maxScore );
		if ( over )
		{
			Gdx.gl.glEnable( GL20.GL_BLEND );
			Gdx.gl.glBlendFunc( GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA );
			cam.update();
			s.setProjectionMatrix( cam.combined );
			s.begin( ShapeType.Filled );
			s.setColor( 0.5f, 0.5f, 0.5f, 0.8f );
			s.rect( 0, 0, this.sw - 1, this.sh - 1 );
			s.end();
			batch.begin();
			String os = "Game over!";
			font.setScale( 2.0f );
			font.setColor( Color.RED );
			BitmapFont.TextBounds r = font.getBounds( os );
			float px = (this.sw - r.width) / 2f;
			float py = (this.sh - r.height) / 2f;
			font.draw( batch, os, px, py );
			batch.end();
			Gdx.gl.glDisable( GL20.GL_BLEND );
		}
	}

	@Override
	public void dispose()
	{
	}

	@Override
	public void resize( int width, int height )
	{
	}

	@Override
	public void pause()
	{
		if ( !over && this.listener != null )
		{
			this.listener.onGamePause();
		}
	}

	@Override
	public void resume()
	{
		if ( this.listener != null )
		{
			this.listener.onGameResume();
		}
		if ( !blocks.canMove() )
		{
			setGameOver();
		}
		Gdx.graphics.requestRendering();
	}

	int px, py;

	public boolean touchDown( int x, int y, int pointer, int button )
	{
		px = x;
		py = y;
		return true; // return true to indicate the event was handled
	}

	public int getMaxScore()
	{
		return maxScore;
	}

	public int getScore()
	{
		return blocks.score;
	}

	public int[] getData()
	{
		return blocks.getValues();
	}

	public void setScore( int score )
	{
		blocks.score = score;
	}

	public void setMaxScore( int maxScore )
	{
		this.maxScore = maxScore;
	}

	public void setData( int[] values )
	{
		for ( int i = 0; i < blocks.data.length && i < values.length; ++i )
		{
			Box b = blocks.data[ i ];
			b.value = values[ i ];
		}
	}

	public boolean touchUp( int x, int y, int pointer, int button )
	{
		if ( over || animating )
			return false;
		float dx, dy;
		dx = x - px;
		dy = y - py;
		handleInput( dx, dy );
		return true; // return true to indicate the event was handled
	}

	boolean animating = false;

	class MoveThread extends Thread implements Runnable
	{
		int dir;
		boolean next;

		public MoveThread( int d, boolean n )
		{
			dir = d;
			next = n;
		}

		public void run()
		{
			synchronized (Power2Game.this)
			{
				try
				{
					boolean moved = false;
					while ( blocks.move( dir, next ) )
					{
						Gdx.graphics.requestRendering();
						try
						{
							Thread.sleep( 50 );
						}
						catch (Exception ex)
						{
						}
						moved = true;
					}
					if ( moved )
					{
						Log.i( "move(%d,%s)", 0, String.valueOf( next ) );
						blocks.fill();
					}
					if ( !blocks.canMove() )
					{
						setGameOver();
					}
				}
				finally
				{
					Gdx.graphics.requestRendering();
					animating = false;
				}
			}
		}
	}

	private void setGameOver()
	{
		over = true;
		screenshot = false;
		onGameOver();
	}

	public void onGameOver()
	{
		if ( blocks.score > this.maxScore )
		{
			this.maxScore = blocks.score;
		}
		Gdx.app.postRunnable( new Runnable()
		{
			@Override
			public void run()
			{
				//File f = saveScreenshot( "2048.png" );
				File f = null;
				if ( listener != null )
				{
					listener.onGameOver( f );
				}
			}
		} );

		// listener.onGameOver( null );
	}

	MoveThread thread;

	public boolean handleInput( float dx, float dy )
	{
		dx = (dx == 0
				? 0.001f
				: dx);
		dy = (dy == 0
				? 0.001f
				: dy);
		float d = Math.abs( dx ) + Math.abs( dy );
		if ( d > 20f )
		{
			blocks.resetState();
			float ang = dy / dx;
			int dir = -1;
			boolean next = false;
			if ( Math.abs( ang ) < 0.35f )
			{
				dir = 0;
				next = dx > 0;
			}
			else if ( Math.abs( ang ) > 0.6 && Math.abs( ang ) < 15 )
			{
				if ( ang < 0 )
				{
					dir = 1;
					next = dy > 0;
				}
				else
				{
					dir = 2;
					next = dy > 0;
				}
			}

			if ( dir >= 0 )
			{
				synchronized (this)
				{
					this.thread = new MoveThread( dir, next );
					thread.start();
				}
			}
		}
		return true;
	}
	
	public File saveScreenshot(String name) {
        FileHandle fh = Gdx.files.local( name );
        Graphics g = Gdx.graphics;
        final Pixmap picture = new Pixmap(g.getWidth(), g.getHeight(), Format.RGBA8888);
        final FrameBuffer buffer = new FrameBuffer(Format.RGBA8888, g.getWidth(), g.getHeight(), false);
        try {
            Gdx.graphics.getGL20().glViewport(0, 0, g.getWidth(), g.getHeight());
            buffer.begin();
            render(); // Or however you normally draw it
            final byte[] data = this.readData(g.getWidth(), g.getHeight());
            buffer.end();
            picture.getPixels().put(data, 0, data.length);
            PixmapIO.writePNG(fh, picture);
            return fh.file();
        } catch (final Exception e) {
        	Log.e( e );
            e.printStackTrace();
        } finally {
            picture.dispose();
            buffer.dispose();
        }
        return null;
    }

    // Adapted from ScreenUtil class
    public byte[] readData(final int width, final int height) {
        final int numBytes = width * height * 4;
        final ByteBuffer pixels = BufferUtils.newByteBuffer(numBytes);
        Gdx.gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);
        Gdx.gl.glReadPixels(0, 0, width, height, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, pixels);

        final byte[] lines = new byte[numBytes];
        final int numBytesPerLine = width * 4;
        for (int i = 0; i < height; i++) {
            pixels.position((height - i - 1) * numBytesPerLine);
            pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
        }

        return lines;
    }
}
