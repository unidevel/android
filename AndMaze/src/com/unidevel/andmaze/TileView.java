package com.unidevel.andmaze;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TileView extends View {
	public static final String TAG = "TetrisBlast";
	 
    /**
     * Parameters controlling the size of the tiles and their range within view.
     * Width/Height are in pixels, and Drawables will be scaled to fit to these
     * dimensions. X/Y Tile Counts are the number of tiles that will be drawn.
     */
	protected static final double mXRatio = 0.625; //This is ratio of the tetris map to size of view
    protected static int mXTileSize;
    protected static int mYTileSize;

    protected int mXTileCount = 10;
    protected int mYTileCount = 20;

    protected static int mXOffset;
    protected static int mYOffset;


    /**
     * A hash that maps integer handles specified by the subclasser to the
     * drawable that will be used for that reference
     */
    private Bitmap[] mTileArray; 

    /**
     * A two-dimensional array of integers in which the number represents the
     * index of the tile that should be drawn at that locations
     */
    private int[][] mTileGrid;

    private final Paint mPaint = new Paint();

	protected int mCurNext;

    //public TextView myText;
    //Constructors
    public TileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TileView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    

    public void setSize(int rows, int cols)
    {
    	this.mXTileCount = cols;
    	this.mYTileCount = rows;
        mTileGrid = new int[rows][cols];
    }
     /**
     * Rests the internal array of Bitmaps used for drawing tiles, and
     * sets the maximum index of tiles to be inserted
     * 
     * @param tilecount
     */
    
    public void resetTiles(int tilecount) {
    	mTileArray = new Bitmap[tilecount];
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	calculateTileSize(w, h);
    	resetTiles(19);
    	Resources r = this.getContext().getResources();
    	loadTile(0, r.getDrawable(R.drawable.wall0));
    	loadTile(1, r.getDrawable(R.drawable.wall1));
    	loadTile(2, r.getDrawable(R.drawable.wall2));
    	loadTile(3, r.getDrawable(R.drawable.wall3));
    	loadTile(4, r.getDrawable(R.drawable.wall4));
    	loadTile(5, r.getDrawable(R.drawable.wall5));
    	loadTile(6, r.getDrawable(R.drawable.wall6));
    	loadTile(7, r.getDrawable(R.drawable.wall7));
    	loadTile(8, r.getDrawable(R.drawable.wall8));
    	loadTile(9, r.getDrawable(R.drawable.wall9));
    	loadTile(10, r.getDrawable(R.drawable.wall10));
    	loadTile(11, r.getDrawable(R.drawable.wall11));
    	loadTile(12, r.getDrawable(R.drawable.wall12));
    	loadTile(13, r.getDrawable(R.drawable.wall13));
    	loadTile(14, r.getDrawable(R.drawable.wall14));
    	loadTile(15, r.getDrawable(R.drawable.wall15));
    	loadTile(16, r.getDrawable(R.drawable.wall16));
    	loadTile(17, r.getDrawable(R.drawable.man));
    	loadTile(18, r.getDrawable(R.drawable.door));
    	clearTiles();
    }
    
    protected void calculateTileSize(int w, int h) {
    	Log.d(TAG, "OnSize changed, w = " + Integer.toString(w)+"h = " + Integer.toString(h));
    	mXTileSize = (int)Math.floor((w)/mXTileCount);
    	mYTileSize = (int)Math.floor((h)/mYTileCount);
        mXOffset = 0;
        mYOffset = 0;
        
    }

    /**
     * Function to set the specified Drawable as the tile for a particular
     * integer key.
     * 
     * @param key
     * @param tile
     */
    public void loadTile(int key, Drawable tile) {
        Bitmap bitmap = Bitmap.createBitmap(mXTileSize, mYTileSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        tile.setBounds(0, 0, mXTileSize, mYTileSize);
        tile.draw(canvas);
        mTileArray[key] = bitmap;
    }

    /**
     * Resets all tiles to BLOCK_EMPTY
     * 
     */
    public void clearTiles() {
        for (int row = 0; row < mYTileCount; row++) {
            for (int col = 0; col < mXTileCount; col++) {
            	setTile(0, col, row);
            }
        }
    }

    /**
     * Used to indicate that a particular tile (set with loadTile and referenced
     * by an integer) should be drawn at the given x/y coordinates during the
     * next invalidate/draw cycle.
     * 
     * @param tileindex
     * @param x
     * @param y
     */
    public void setTile(int tileindex, int x, int y) {
        mTileGrid[y][x] = tileindex;
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int row = 0; row < mYTileCount; row += 1) {
            for (int col = 0; col < mXTileCount; col += 1) {
                if (mTileGrid[row][col] > 0) {
                    canvas.drawBitmap(mTileArray[mTileGrid[row][col]], 
                    		mYOffset + col * mXTileSize,
                    		mXOffset + row * mYTileSize,
                    		mPaint);
                }
            }
        }
    }
}
