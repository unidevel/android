package com.unidevel.power2;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.input.*;
import android.util.*;


public class MyGdxGame implements ApplicationListener
{
	Texture texture;
	SpriteBatch batch;
	TriangleBlocks blocks;
	int size;
	BitmapFont font;
	
	@Override
	public void create()
	{
		this.size=4;
		this.blocks=new TriangleBlocks(size);
		//texture = new Texture(Gdx.files.internal("android.jpg"));
		texture=textureFromPixmap(createBlocks());
		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("verdana39.fnt"));
		Gdx.input.setInputProcessor(new GestureDetector(new Controller()));
	}
	
	Texture textureFromPixmap (Gdx2DPixmap pixmap) {
		Texture texture = new Texture(pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGBA4444);
		texture.bind();
		Gdx.gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, pixmap.getGLInternalFormat(), pixmap.getWidth(), pixmap.getHeight(), 0,
							pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
		return texture;
	}

	public Gdx2DPixmap createBlocks(){
		int size=256;
		Gdx2DPixmap p=new Gdx2DPixmap(size,size,Gdx2DPixmap.GDX2D_FORMAT_RGBA4444);
		p.fillRect(0,0,size,size,Color.rgba4444(0,0,0,1));
		p.fillTriangle(size/2,size/2,0,size-1,size-1,size-1,Color.rgba4444(0.2f,0.2f,0.2f,1.0f));
		return p;
	}
	
	@Override
	public void render()
	{        
	    Gdx.gl.glClearColor(1, 1, 1, 1);
	    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.begin();//batch.
		//batch.draw(texture, Gdx.graphics.getWidth() / 4, 0, 
		//		   Gdx.graphics.getWidth() / 2, Gdx.graphics.getWidth() / 2);
		batch.draw(texture, Gdx.graphics.getWidth() / 4, 0, 
				   256,256);
		font.setColor(Color.BLACK);
		font.draw(batch, "hello world", 20,20);
		batch.end();
		GLCommon g = Gdx.gl;
		int w=Gdx.graphics.getWidth();
		int h=Gdx.graphics.getHeight();
	}

	@Override
	public void dispose()
	{
	}

	@Override
	public void resize(int width, int height)
	{
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}
	
	class Controller implements GestureListener
	{

		@Override
		public boolean touchDown(float p1, float p2, int p3, int p4)
		{
			// TODO: Implement this method
			return false;
		}

		@Override
		public boolean tap(float p1, float p2, int p3, int p4)
		{
			// TODO: Implement this method
			return false;
		}

		@Override
		public boolean longPress(float p1, float p2)
		{
			// TODO: Implement this method
			return false;
		}

		@Override
		public boolean fling(float p1, float p2, int p3)
		{
			// TODO: Implement this method
			return false;
		}

		@Override
		public boolean pan(float x, float y, float x1, float y1)
		{
			Log.i("pan","("+x+","+y+"),("+x1+","+y1+")");
			return false;
		}

		@Override
		public boolean panStop(float p1, float p2, int p3, int p4)
		{
			// TODO: Implement this method
			return false;
		}

		@Override
		public boolean zoom(float p1, float p2)
		{
			// TODO: Implement this method
			return false;
		}

		@Override
		public boolean pinch(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4)
		{
			// TODO: Implement this method
			return false;
		}

		
	}
}
