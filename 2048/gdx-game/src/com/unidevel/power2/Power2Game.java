package com.unidevel.power2;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;



public class Power2Game extends InputAdapter implements ApplicationListener
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
	float[] npos;
	boolean over;
	public void create()
	{
		sw=Gdx.graphics.getWidth();
		sh=Gdx.graphics.getHeight();
		this.cam = new OrthographicCamera(sw, sh);
		cam.position.set(sw / 2-1, sh / 2, 0);
		this.shapeRenderer=new ShapeRenderer();
		font = new BitmapFont(Gdx.files.internal("verdana39.fnt"));
		//Gdx.input.setInputProcessor(new GestureDetector(new Controller()));
		Gdx.input.setInputProcessor(this);
		batch = new SpriteBatch();
		newGame();
	}

	public void newGame(){
		this.prepareBlocks(4,48);
		blocks.fill();
		over = false;
	}
	
	void prepareBlocks(int n, float ypos){
		points = new float[n*n][];
		npos= new float[n*n*2];
		this.blocks=new TriangleBlocks(n);
		float len=sw>sh?sh -4:sw-4;
		float x = len/2.0f;
		float y = ypos;
		float dx= len/(float)n/2.0f;
		float cos60=(float)Math.cos(60/180*3.1415926359);
		
		float dy= dx*2.0f*cos60;	
		float ty= dy*2f/3f;
		int p=0;
		float sx=4.0f*cos60;
		float sy=4.0f;
		for(int i=0;i<n;++i){
			float xx=x;
			for(int j=0;j<=2*i;++j,++p){
				npos[p*2]=x;
				if(j%2==0)
				{
					points[p]=new float[6];
					points[p][0]=x;
					points[p][1]=y+sy;
					points[p][2]=x-dx+sx;
					points[p][3]=y+dy-sy;
					points[p][4]=x+dx-sx;
					points[p][5]=y+dy-sy;
					npos[p*2+1]=y+sy+ty;
				}
				else{
					points[p]=new float[6];
					points[p][0]=x;
					points[p][1]=y+dy-sy;
					points[p][2]=x-dx+sx;
					points[p][3]=y+sy;
					points[p][4]=x+dx-sx;
					points[p][5]=y+sy;
					npos[p*2+1]=y+sy;
				}
				translate(points[p]);
				x+=dx;
			}
			x=xx-dx;
			y+=dy;
		}
		translate(npos);
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
				p[i]=sh-sh/6f-p[i];
			}
		}
	}
	
	public void render()
	{
	    Gdx.gl.glClearColor(0, 0, 0, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glViewport(0,0,sw,sh);

		ShapeRenderer s=shapeRenderer;
		
		cam.update();
		s.setProjectionMatrix(cam.combined);
		//s.begin(ShapeType.Line);
		//shapeRenderer.setColor(1, 1, 1, 1);
		//s.rect(0,0,sw-1,sh-1);
		//shapeRenderer.line(0, 0, 120, 40);
		//s.end();
		s.begin(ShapeType.Filled);
		s.setColor(0.7f,0.7f,0.7f,1.0f);
		s.rect(0,0,sw-1,sh-1);
		int[] v=blocks.getValues();
		float ln2=(float)Math.log(2);
		for(int i=0;i<points.length;++i){
			float c=(float)Math.log(v[i])/ln2/4.0f;
			s.setColor(c, 0, c, 1);
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
		s.end();
				
		batch.begin();//batch.
		font.setColor(Color.WHITE);
		float sw=font.getSpaceWidth();
		float lh=font.getLineHeight();
		for(int i=0;i<v.length;++i){
			int value=v[i];
			if(value>0){
				String sv=String.valueOf(value);
				float x=npos[2*i]-sw*((float)sv.length());
				float y=npos[2*i+1];
				font.draw(batch,""+value,x,y);
			}
		}
		
		String score=String.format("Your score: %d",blocks.score);
		font.setColor(Color.BLUE);
		font.draw(batch, score, 30,sh-60);
		batch.end();
		//font.setColor(Color.WHITE);
		//font.draw(
		if(over)
		{
			Gdx.gl.glEnable( GL20.GL_BLEND );
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			cam.update();
			s.setProjectionMatrix(cam.combined);
			s.begin(ShapeType.Filled);
			s.setColor(0.5f,0.5f,0.5f,0.8f);
			s.rect(0,0,this.sw-1,this.sh-1);
			s.end();
			batch.begin();//batch.
			String os="Game over!";
			font.setColor(Color.RED);
			font.draw(batch, os, 30,sh-60);
			batch.end();
			Gdx.gl.glDisable( GL20.GL_BLEND );
		}
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
		float d=Math.abs(dx)+Math.abs(dy);
		if(d>sw/8f){
			float ang=dy/dx;
			boolean moved=false;
			//log("moving dx=%f,dy=%f,d=%f,ang=%f",dx,dy,d,ang);
			if(Math.abs(ang)<0.2f){
				moved=blocks.move(0,dx>0);
				Log.i("move(%d,%s)",0,dx>0?"true":"false");
			}
			else if(Math.abs(ang)>0.6&&Math.abs(ang)<3){
				if(ang<0){
					moved=blocks.move(1,dy>0);
					Log.i("move(%d,%s)=%s",1,dy>0?"true":"false",String.valueOf(moved));
				}
				else{
					moved=blocks.move(2,dy>0);
					Log.i("move(%d,%s)=%s",2,dy>0?"true":"false",String.valueOf(moved));
				}
			}
			if(moved){
				blocks.dump3();
				if(!blocks.fill()){
				}
			}
			if(!blocks.canMove()){
				over=true;
				return false;
			}
		}
		return true;
	}
}
