package com.unidevel.andmaze;

public class Maze {
	public interface Listener{
		void done();
	}
	
	Listener listener;
	
    int[][] maze;  // Description of state of maze.  The value of maze[i][j]
                   // is one of the constants wallCode, pathcode, emptyCode,
                   // or visitedCode.  (Value can also be negative, temporarily,
                   // inside createMaze().)
                   //    A maze is made up of walls and corridors.  maze[i][j]
                   // is either part of a wall or part of a corridor.  A cell
                   // cell that is part of a cooridor is represented by pathCode
                   // if it is part of the current path through the maze, by
                   // visitedCode if it has already been explored without finding
                   // a solution, and by emptyCode if it has not yet been explored.

    public final static int wallCode = 1;
    public final static int emptyCode = 0;
    public final static int manCode = 2;
	public final static int doorCode = 3;
	public final static int pathCode = 4;
       // the next six items are set up in init(), and can be specified 
       // using applet parameters
    int rows;          // number of rows of cells in maze, including a wall around edges
    int columns;       // number of columns of cells in maze, including a wall around edges

    boolean mazeExists = false;  // set to true when maze[][] is valid; used in
                                 // redrawMaze(); set to true in createMaze(), and
	int manX, manY;
    public Maze(int rows, int columns){
        if (rows % 2 == 0)
            rows++;
        if (columns % 2 == 0)
            columns++;    	
    	this.rows = rows;
    	this.columns = columns;
    }
    
    public void generateMaze(){
    	makeMaze();
		maze[1][0]=manCode;
		maze[rows-2][columns-1]=doorCode;
		manX = 0; manY=1;
    	//makeWalls();
    }
    
    private void makeMaze() {
            // Create a random maze.  The strategy is to start with
            // a grid of disconnnected "rooms" separated by walls.
            // then look at each of the separating walls, in a random
            // order.  If tearing down a wall would not create a loop
            // in the maze, then tear it down.  Otherwise, leave it in place.
        if (maze == null)
           maze = new int[rows][columns];
        int i,j;
        int emptyCt = 0; // number of rooms
        int wallCt = 0;  // number of walls
        int[] wallrow = new int[(rows*columns)/2];  // position of walls between rooms
        int[] wallcol = new int[(rows*columns)/2];
        for (i = 0; i<rows; i++)  // start with everything being a wall
            for (j = 0; j < columns; j++)
                maze[i][j] = wallCode;
        for (i = 1; i<rows-1; i += 2)  // make a grid of empty rooms
            for (j = 1; j<columns-1; j += 2) {
                emptyCt++;
                maze[i][j] = -emptyCt;  // each room is represented by a different negative number
                if (i < rows-2) {  // record info about wall below this room
                    wallrow[wallCt] = i+1;
                    wallcol[wallCt] = j;
                    wallCt++;
                }
                if (j < columns-2) {  // record info about wall to right of this room
                    wallrow[wallCt] = i;
                    wallcol[wallCt] = j+1;
                    wallCt++;
                }
             }
        mazeExists = true;
        int r;
        for (i=wallCt-1; i>0; i--) {
            r = (int)(Math.random() * i);  // choose a wall randomly and maybe tear it down
            tearDown(wallrow[r],wallcol[r]);
            wallrow[r] = wallrow[i];
            wallcol[r] = wallcol[i];
        }
        for (i=1; i<rows-1; i++)  // replace negative values in maze[][] with emptyCode
           for (j=1; j<columns-1; j++)
              if (maze[i][j] < 0)
                  maze[i][j] = emptyCode;
    }
    
    private void makeWalls()
    {
		for ( int row = 1; row < rows - 1; ++ row )
		{
			for ( int col = 1; col < columns - 1; ++col ) 
			{
				if ( maze[row][col] > 0 )
				{
					if ( maze[row-1][col] > 0 ) 
						maze[row][col] += 1;
					if ( maze[row][col-1] > 0 )
						maze[row][col] += 2;
					if ( maze[row][col+1] > 0 )
						maze[row][col] += 4;
					if ( maze[row+1][col] > 0 )
						maze[row][col] += 8;
				}
			}
		}
    }

    private void tearDown(int row, int col) {
       // Tear down a wall, unless doing so will form a loop.  Tearing down a wall
       // joins two "rooms" into one "room".  (Rooms begin to look like corridors
       // as they grow.)  When a wall is torn down, the room codes on one side are
       // converted to match those on the other side, so all the cells in a room
       // have the same code.   Note that if the room codes on both sides of a
       // wall already have the same code, then tearing down that wall would 
       // create a loop, so the wall is left in place.
            if (row % 2 == 1 && maze[row][col-1] != maze[row][col+1]) {
                // row is odd; wall separates rooms horizontally
                fill(row, col-1, maze[row][col-1], maze[row][col+1]);
                maze[row][col] = maze[row][col+1];
             }
            else if (row % 2 == 0 && maze[row-1][col] != maze[row+1][col]) {
                // row is even; wall separates rooms vertically
              	fill(row-1, col, maze[row-1][col], maze[row+1][col]);
                maze[row][col] = maze[row+1][col];
             }
    }

    void fill(int row, int col, int replace, int replaceWith) {
           // called by tearDown() to change "room codes".
        if (maze[row][col] == replace) {
            maze[row][col] = replaceWith;
            fill(row+1,col,replace,replaceWith);
            fill(row-1,col,replace,replaceWith);
            fill(row,col+1,replace,replaceWith);
            fill(row,col-1,replace,replaceWith);
        }
    }
    
	public boolean move2(int dx, int dy){
		int newX, newY;
		newX = manX+dx;
		newY = manY+dy;
		return moveTo(newX,newY);
	}
	
	public boolean move(int dx, int dy){
		int newX, newY;
		newX = manX+dx;
		newY = manY+dy;
		
		if(newX<0||newX>=columns)return false;
		if(newY<0||newY>=rows)return false;
		
		if(moveTo(newX,newY))return true;
		if (dx!=0){
			int d = rows;
			for (int n = 0;n < rows;n++){
				if(n<manY){
					if (maze[n][manX]==wallCode){
						d = rows;
						continue;
					}
				}
				else if(n>manY){
					if (maze[n][manX]==wallCode){
						break;
					}
				}
				else continue;
				if(maze[n][newX]!=wallCode){
					if (Math.abs(manY-n)<=Math.abs(d)){
						d=n-manY;
					}
				}
			}
			if(d<0){
				dy=-1;dx=0;
			}
			else{
				dy=1;dx=0;
			}
		}
		else if (dy!=0){
			int d = columns;
			for (int n = 0;n < columns;n++){
				if(n<manX){
					if (maze[manY][n]==wallCode){
						d = columns;
						continue;
					}
				}
				else if(n>manX){
					if (maze[manY][n]==wallCode){
						break;
					}
				}
				else continue;
				if(maze[newY][n]!=wallCode){
					if (Math.abs(manX-n)<=Math.abs(d)){
						d=n-manX;
					}
				}
			}
			if(d>0){
				dy=0;dx=1;
			}
			else{
				dy=0;dx=-1;
			}
		}
		newX = manX+dx;
		newY = manY+dy;
		
		return moveTo(newX,newY);
	}
	
	public boolean moveTo(int newX, int newY){
		if(newX<0||newX>=columns)return false;
		if(newY<0||newY>=rows)return false;
		if(maze[newY][newX]==wallCode)return false;
		if(maze[newY][newX]==pathCode)
			maze[manY][manX]=emptyCode;
		else
			maze[manY][manX]=pathCode;
		manX=newX; manY=newY;
		maze[manY][manX]=manCode;
		if (manX==columns-1){
			notifyDone();
		}
		return true;
	}
	
	public boolean isOut(){
		return manX==columns-1;
	}
	
    public int getRows()
    {
    	return rows;
    }
    
    public int getColumns()
    {
    	return columns;
    }
   
    public int[][] getData()
    {
    	return maze;
    }

    public void dump()
    {
    	for ( int row = 0; row < this.rows; ++ row ) 
    	{
    		for ( int col = 0; col <this.columns; ++col )
    		{
    			if ( maze[row][col] < 10 ) 
    				System.out.print(' ');
    			System.out.print(maze[row][col]);
    			System.out.print(' ');
    		}
    		System.out.println();
    	}
    }
    
	public int getManX(){
		return manX;
	}
	
	public int getManY(){
		return manY;
	}
	
	public int[] toArray(){
		int[] array = new int[rows*columns+4];
		int n = 0;
		array[n++] = rows;
		array[n++] = columns;
		array[n++] = manX;
		array[n++] = manY;
		for ( int y = 0; y < rows; ++y){
			for( int x = 0; x < columns; ++x){
				array[n++] = maze[y][x];
			}
		}
		return array;
	}
	
	public void fromArray(int[] data){
		int n = 0;
		rows = data[n++];
		columns = data[n++];
		manX = data[n++];
		manY = data[n++];
		for ( int y = 0; y < rows; ++y){
			for( int x = 0; x < columns; ++x){
				maze[y][x] = data[n++];
			}
		}
	}
	
	public void reset(){
		manX = 0;
		manY = 1;
		maze[manY][manX]=manCode;
		maze[rows-2][columns-1]=doorCode;
		for ( int y = 0; y < rows; ++y){
			for( int x = 0; x < columns; ++x){
				if (maze[y][x]==pathCode)
					maze[y][x]=emptyCode;
			}
		}
	}
	
	public void setListener(Listener l){
		this.listener = l;
	}
	
	public void notifyDone(){
		if(this.listener!=null)
			this.listener.done();
	}
    public static void main(String[] args)
    {
    	Maze maze = new Maze(20, 40);
    	maze.makeMaze();
    	maze.makeWalls();
    	maze.dump();
    }
}
