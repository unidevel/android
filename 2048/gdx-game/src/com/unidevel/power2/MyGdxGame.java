package com.unidevel.power2;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.input.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;



public class MyGdxGame extends InputAdapter implements ApplicationListener
{
	Texture texture;
	SpriteBatch batch;
	TriangleBlocks blocks;
	int size;
	BitmapFont font;
	ShapeRenderer shapeRenderer;
	OrthographicCamera cam;
	int sw,sh;
	float[][] points;
	
	public void create()
	{
		sw=Gdx.graphics.getWidth();
		sh=Gdx.graphics.getHeight();
		this.cam = new OrthographicCamera(sw, sh);
		cam.position.set(sw / 2-1, sh / 2, 0);
		this.shapeRenderer=new ShapeRenderer();
		this.prepareBlocks(4,48);
		blocks.fill();
		//Gdx.input.setInputProcessor(new GestureDetector(new Controller()));
		Gdx.input.setInputProcessor(this);
	}

	void prepareBlocks(int n, float ypos){
		points = new float[n*n][];
		this.blocks=new TriangleBlocks(n);
		float len=sw>sh?sh -4:sw-4;
		float x = len/2.0f;
		float y = ypos;
		float dx= len/(float)n/2.0f;
		float cos60=(float)Math.cos(60/180*3.1415926359);
		
		float dy= dx*2.0f*cos60;	
		int p=0;
		float sx=4.0f*cos60;
		float sy=4.0f;
		for(int i=0;i<n;++i){
			float xx=x;
			for(int j=0;j<=2*i;++j,++p){
				
				if(j%2==0)
				{
					points[p]=new float[6];
					points[p][0]=x;
					points[p][1]=y+sy;
					points[p][2]=x-dx+sx;
					points[p][3]=y+dy-sy;
					points[p][4]=x+dx-sx;
					points[p][5]=y+dy-sy;
				}
				else{
					points[p]=new float[6];
					points[p][0]=x;
					points[p][1]=y+dy-sy;
					points[p][2]=x-dx+sx;
					points[p][3]=y+sy;
					points[p][4]=x+dx-sx;
					points[p][5]=y+sy;
				}
				translate(points[p]);
				x+=dx;
			}
			x=xx-dx;
			y+=dy;
		}
	}
	
	@Override
	public void create2()
	{
		this.size=4;
		this.blocks=new TriangleBlocks(size);
		//texture = new Texture(Gdx.files.internal("android.jpg"));
		texture=textureFromPixmap(createBlocks());
		batch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("verdana39.fnt"));
	//	Gdx.input.setInputProcessor(new GestureDetector(new Controller()));
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
	
	private void translate(float[] p){
		for(int i = 0;i<p.length;++i){
			if(i%2==1){
				p[i]=sh-p[i];
			}
		}
	}
	
	public void render()
	{
	    Gdx.gl.glClearColor(0, 0, 0, 1);
	    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glViewport(0,0,sw,sh);
		ShapeRenderer s=shapeRenderer;
		
		cam.update();
		cam.apply(Gdx.gl10);
		s.setProjectionMatrix(cam.combined);
		//s.begin(ShapeType.Line);
		//shapeRenderer.setColor(1, 1, 1, 1);
		//s.rect(0,0,sw-1,sh-1);
		//shapeRenderer.line(0, 0, 120, 40);
		//s.end();
		s.begin(ShapeType.Filled);
		s.setColor(0.8f,0.8f,0.8f,1.0f);
		s.rect(0,0,sw-1,sh-1);
		int[] v=blocks.getValues();
		float ln2=(float)Math.log(2);
		for(int i=0;i<points.length;++i){
			float c=(float)Math.log(v[i])/ln2/4.0f;
			s.setColor(c, 0, 0, 1);
			float p[] = points[i];
			//translate(p);
			s.triangle(p[0],p[1],p[2],
				p[3],p[4],p[5]);
		}
		
		/*
		shapeRenderer.setColor(1, 1, 0, 1);
		shapeRenderer.rect(40,40, 100, 120);
		shapeRenderer.circle(80, 80, 50);
		*/
		shapeRenderer.end();

	}	
	@Override
	public void render1()
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

	public void log(String fmt, Object ...args){
		Gdx.app.log(this.getClass().getSimpleName(),String.format(fmt,args));
	}

	public void log(Throwable ex){
		Gdx.app.log(this.getClass().getSimpleName(),ex.getMessage(),ex);
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
	
	int px,py;
	public boolean touchDown (int x, int y, int pointer, int button) {
		px=x;py=y;
		return true; // return true to indicate the event was handled
	}

	public boolean touchUp (int x, int y, int pointer, int button) {
		float dx,dy;
		dx=x-px;dy=y-py;
		handleInput(dx,dy);
		return true; // return true to indicate the event was handled
	}
	
	public boolean handleInput(float dx,float dy)
	{
		// TODO: Implement this method
		dx=(dx==0?0.001f:dx);
		dy=(dy==0?0.001f:dy);
		log("moving %f,%f",dx,dy);
		float d=Math.abs(dx)+Math.abs(dy);
		if(d>sw/3f){
			float ang=dy/dx;
			if(Math.abs(ang)<0.08f){
				blocks.move(0,dx>0);
			}
			else if(Math.abs(ang)>1.6&&Math.abs(ang)<1.85){
				if(dx<0){
					blocks.move(2,dy>0);
				}
				else{
					blocks.move(1,dy>0);
				}
			}
			blocks.fill();
		}
		return true;
	}
}
