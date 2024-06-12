import java.util.*;

/** An instance represents a grid of pieces from two opposing
 *  players in a game of Connect Four. The grid is 0-indexed first by rows
 *  starting at the top, then by columns 0-indexed starting at the left.
 *  Board is immutable. */
public class Board {
    /** The number of rows on a Connect Four board. */
    public static final int NUM_ROWS= 6;
    
    /** The number of columns on a Connect Four board. */
    public static final int NUM_COLS= 7;

    /** The grid of pieces.
     *  grid[col][row] is the chip at column col and Row row.
     *  Rows are 0-indexed starting at the top.
     *  Columns are 0-indexed starting at the left.
     *  A null value indicates an empty location. */
    private final Turn[][] grid;

    /** Constructor: an empty Board. */
    public Board() {
        grid= new Turn[NUM_COLS][NUM_ROWS];
    }

    /** Constructor: the board that would result from copying board
     *  and than having player make the Move move*/
    public Board(Board board, Turn player, Move move) {
    	int column= move.getColumn();
    	grid= Arrays.copyOf(board.grid, NUM_COLS);
    	grid[column]= Arrays.copyOf(grid[column], NUM_ROWS);
        makeMove(player, move);
    }

    /** Return the chip at the specified row and column.
     * Precondition: row and column give a position on the board */
    public Turn getPlayer(int row, int column) {
        assert 0 <= row && row < NUM_ROWS && 0 <= column && column < NUM_COLS;
        return grid[column][row];
    }
    
    /** Indicate whether column col is full. */
    public boolean columnIsFull(int col) {
    	return grid[col][0] != null;
    }

    /** Apply Move move to this Board by placing a chip from move's
     *  player into move's column on this Board. Throw an
     *  IllegalArgumentException if move's column is full on this Board. */
    private void makeMove(Turn player, Move move) {
    	Turn[] column= grid[move.getColumn()];
    	if (column[0] != null)
    		throw new IllegalArgumentException("Cannot place chip into full column");
    	
    	for (int r= 0; r < NUM_ROWS; r++)
    		if (column[r] != null) {
    			column[r-1]= player;
    			return;
    		}
    	column[NUM_ROWS-1]= player;
    }

    /** Return an array of all moves that can possibly be made on this board.
     *  The length of the returned array must be the number of possible moves.
     *  If the board has a winner (4 chips for the same player in a row), no
     *  move is possible because the game is over, so the array will be empty.
     *  Note: If the game is not over, the number of possible moves is the number
     *  of columns that are not full. Thus, if all columns are full, return an
     *  array of length 0.
     *  Note: the returned value will not be null.
     *  Note: the returned array will not contain any null values or duplicates. */
    public Move[] getPossibleMoves() {
        //TODO: part 1 of A5
        // Hint. Read the spec carefully. Study the class invariant.
        //       All methods you have to call are in this class.
    	return new Move[0];
    }

    /** Return a representation of this board */
    public @Override String toString() {
        return toString("");
    }

    /** Return the String representation of this Board with indent
     *  prepended to each line. */
    public String toString(String indent) {
        String str= "";
        for (int r= 0; r < NUM_ROWS; r++) {
            str+= indent + "|";
            for (int c= 0; c < NUM_COLS; c++) {
                if (grid[c][r] == null)
                    str+= ' ';
                else
                    str+= grid[c][r].getInitial();
                str+= '|';
            }
            str+= "\n";
        }
        return str;
    }
    
    /** Indicate whether the board is completely full.
     *  If so, then the game is over with tie. */
    public boolean isFull() {
    	for (int c= 0; c < NUM_COLS; c++)
    		if (!columnIsFull(c))
    			return false;
    	return true;
    }

    /** Return the player that has four in a row, or null if no player does. */
    public Turn hasConnectFour() {
        search: for (List<? extends Location> fourinarow : getFourInARows()) {
        	Turn player= fourinarow.get(0).getPlayer(this);
        	if (player == null)
        		continue;
        	for (Location loc : fourinarow)
        		if (loc.getPlayer(this) != player)
        			continue search;
        	return player;
        }
        return null;
    }

    /** Return all possible ways to have four chips in a row. */
    public static Iterable<? extends List<? extends Location>> getFourInARows() {
    	return Location.fourinarows;
    }

    /** A location on boards. */
    public static class Location {
    	private final int row; // a valid row
    	private final int column; // a valid column
    	
    	/** Construct the location for row and col.
    	 *  Precondition: row is a valid row and col is a valid column */
    	private Location(int row, int col) {
    		this.row= row;
    		this.column= col;
    	}
    	
    	/** Indicates whether this location is occupied in board. */
    	public boolean isOccupied(Board board) {
    		return board.grid[column][row] != null;
    	}
    	
    	/** Return the player at this location in board. */
    	public Turn getPlayer(Board board) {
    		return board.grid[column][row];
    	}
    	
    	/** Return a string representation of this location. */
    	public @Override String toString() {
    		return "{row=" + row + ",column=" + column + "}";
    	}
    	
        /** The four possible orientations of a four-in-a-row. */
        private static enum Orientation {
        	HORIZONTAL(0,1), VERTICAL(1,0), DOWNSLASH(-1,1), UPSLASH(1,1);
        	
        	public final int drow, dcolumn;
        	
        	Orientation(int drow, int dcol) {
        		this.drow= drow;
        		this.dcolumn= dcol;
        	}
        }
        
        /** A list of all possible four-in-a-rows on the board. */
    	private static final List<List<Location>> fourinarows;
    	static {
    		fourinarows = new ArrayList<List<Location>>();
            for (Orientation orientation : Orientation.values())
                for (int r= 0; r < NUM_ROWS; r++)
                    for (int c= 0; c < NUM_COLS; c++) {
                    	Location[] fourinarow= possibleFourInARow(new Location(r, c), orientation);
                        if (fourinarow != null)
                            fourinarows.add(Collections.unmodifiableList(Arrays.asList(fourinarow)));
                    }
    	}
    	
    	/** Return the four-in-a-row specified by this location and orientation,
    	 *  or null if it doesn't fit on the board. */
    	private static Location[] possibleFourInARow(Location loc, Orientation orientation) {
    		Location[] fourinarow= new Location[4];
    		fourinarow[0]= loc;
    		for (int i= 1; i <= 3; i++) {
    			loc= loc.getOffset(orientation);
    			if (loc == null)
    				return null;
    			fourinarow[i]= loc;
    		}
    		return fourinarow;
    	}
    	
    	/** Return the location that is offset from this by orientation,
    	 *  or null if that location would be off the board. */
    	private Location getOffset(Orientation orientation) {
    		int r= row + orientation.drow;
    		if (r < 0 || r >= NUM_ROWS)
    			return null;
    		int c= column + orientation.dcolumn;
    		if (c < 0 || c >= NUM_COLS)
    			return null;
    		return new Location(r, c);
    	}
    }
}
