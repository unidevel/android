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
	int maxScore;
	GameListener listener;
	public Power2Game(){
		this(0);
	}
	
	public Power2Game(int maxScore){
		this.maxScore = maxScore;
	}
	
	public void setGameListener(GameListener l){
		this.listener = l;
	}
	
	public void create()
	{
		sw=Gdx.graphics.getWidth();
		sh=Gdx.graphics.getHeight();
		Gdx.graphics.setContinuousRendering(false);
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
				//p[i]=sh-sh/6f-p[i];
				p[i]=sh-p[i]-sh/4f;
			}
		}
	}

	private float drawScore(BitmapFont font, boolean left, float offx,float offy, String s1, int n ){
		float textScale = 0.5f;
		float scoreScale = 1.0f;
		BitmapFont.TextBounds b1,b2;
		float x,y,w,h;
		float w1, w2, h1, h2;
		String s2=String.format( "%05d", n);
		font.setScale(textScale);
		b1=font.getBounds(s1);
		w1 = b1.width;
		h1 = b1.height;
		font.setScale(scoreScale);
		b2=font.getBounds(s2);
		w2 = b2.width;
		h2 = b2.height;
		h=h1+6+h2;
		w=w2>w1?w2:w1;
		w+=4;
		x=offx;y=h+offy;
		ShapeRenderer s=shapeRenderer;
		s.begin(ShapeType.Filled);
		s.setColor(Color.GRAY);
		if ( left ) {
			s.rect(x,sh-y,w,h);
		}
		else {
			s.rect(sw-x-w,sh-y,w,h);
		}
		s.end();
		
		batch.begin();
		font.setColor(0.7f,0.7f,0.7f,1.0f);
		font.setScale(textScale);
		if ( left ){
			font.draw(batch, s1, x+(w-w1)/2f+2,sh-(y+2-h));
		}
		else{
			font.draw(batch, s1, sw-(x+(w-w1)/2f+2)-w1,sh-(y+2-h));
		}
		font.setColor(1f,1f,1f,1.0f);
		font.setScale(scoreScale);
		if ( left ){
			font.draw(batch, s2, x+2, sh-(y+2-h+h1));
		}
		else{
			font.draw(batch, s2, sw-(x-2)-w, sh-(y+2-h+h1));
		}
		batch.end();
		
		return w;
	}
	
	public void render()
	{
	    Gdx.gl.glClearColor(0, 0, 0, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glViewport(0,0,sw,sh);

		ShapeRenderer s=shapeRenderer;
		
		cam.update();
		s.setProjectionMatrix(cam.combined);
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
		s.end();
				
		batch.begin();//batch.
		font.setColor(Color.WHITE);
		font.setScale(1.0f);
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
		
	/*	String score=String.format("Your score: %d",blocks.score);
		font.setColor(Color.BLUE);
		font.draw(batch, score, 30,sh-60);
		*/
		batch.end();
		drawScore(font, true, 10f, 10f, "SCORE", blocks.score);
		drawScore(font, false, 10f, 10f, "BEST", maxScore);
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
			batch.begin();
			String os="Game over!";
			font.setScale(2.0f);
			font.setColor(Color.RED);
			BitmapFont.TextBounds r = font.getBounds(os);
			float px=(this.sw-r.width)/2f;
			float py=(this.sh-r.height)/2f;
			font.draw(batch, os, px,py);
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

	public int getScore(){
		return blocks.score;
	}
	
	public boolean touchUp (int x, int y, int pointer, int button) {
		if(over)
			return false;
		float dx,dy;
		dx=x-px;dy=y-py;
		handleInput(dx,dy);
		return true; // return true to indicate the event was handled
	}
	
	boolean animating = false;
	
	class MoveThread extends Thread{
		int dir;
		boolean next;
		public MoveThread(int d,boolean n){
			dir=d;
			next=n;
		}
		public void run(){
			try{
				boolean moved=false;
				while(blocks.move(dir,next)){
					Gdx.graphics.requestRendering();
					try{sleep(70);}catch(Exception ex){}
					moved=true;						
				}				
				if(moved){
					Log.i("move(%d,%s)",0,String.valueOf(next));
					blocks.fill();
				}
				if(!blocks.canMove()){
					over=true;
					onGameOver();
				}
			}
			finally{
				animating=false;
				Gdx.graphics.requestRendering();
			}
		}
	}
	
	public void onGameOver(){
		if(blocks.score>this.maxScore){
			this.maxScore=blocks.score;
		}
		if(listener!=null){
			listener.onGameOver();
		}
	}
	
	public boolean handleInput(float dx,float dy)
	{
		dx=(dx==0?0.001f:dx);
		dy=(dy==0?0.001f:dy);
		float d=Math.abs(dx)+Math.abs(dy);
		if(d>20f){
			blocks.resetState();
			float ang=dy/dx;
			int dir=-1; 
			boolean next=false;
			if(Math.abs(ang)<0.35f){
				dir=0;
				next=dx>0;
			}
			else if(Math.abs(ang)>0.6&&Math.abs(ang)<15){
				if(ang<0){
					dir=1;
					next=dy>0;
				}
				else{
					dir=2;
					next=dy>0;
				}
			}
			
			if(dir>=0){
				MoveThread thread=new MoveThread(dir,next);
				thread.start();
			}
		}
		return true;
	}
}
