/** Move represents a player and a column number, so that when this move
 *  is applied to a Board, this Move's player will place a piece in
 *  this Move's column of the Board. */
public class Move implements Comparable<Move> {
    private int column;     // in this column, which is within 0..Board.NUM_COLS-1

    /** Constructor: an instance with column c.
     *  Throw an IllegalArgumentException if c is not in 0..Board.NUM_COLS-1. */
    public Move(int column) {
        if (column < 0 || Board.NUM_COLS <= column)
            throw new IllegalArgumentException("Cannot create a Move with column " + column
                    + " that is not in range 0.." + (Board.NUM_COLS-1));
        this.column= column;
    }

    /** Return the column of this Move. */
    public int getColumn() {
        return column;
    }

    /** Return a string representation of this Move. */
    public @Override String toString() {
        return "Put a piece in column " + column;
    }
    
    /** Two moves are equal if they apply to the same column. */
    public @Override boolean equals(Object that) {
    	return that instanceof Move && this.column == ((Move)that).column;
    }
    
    /** Hash the column of this move. */
    public @Override int hashCode() {
    	return Integer.hashCode(column);
    }

    /** A move is smaller than another move if its column is to the left of the other's. */
	public @Override int compareTo(Move that) {
		return this.column - that.column;
	}
}
