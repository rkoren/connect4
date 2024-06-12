/** An instance is an entity that can supply moves for a Connect Four game. */
public abstract class Player {
	private String name; // The name of this player

	/** Constructor: a Player whose name is name. */
	protected Player(String name) {
		this.name= name;
	}
	
	/** Return the name to use for this player. */
	public final String getName() {
		return name;
	}
	
	/** Return this Player's move for board.
	 *  This uses Async so that the move can be determined on a separate thread.
	 *  Precondition: board is not null and has at least one possible move. */
	public abstract Async<Move> getAsyncMove(Board board);
	
	/** Called after every time a move is made.
	 *  board is the state of the board after the move.
	 *  player is the player that made the move.
	 *  move is the move that was made. */
	public void observeMove(Board board, Turn player, Move move) { }
	
	/** Return a string representation of this player. */
	public String toString() { return "Player " + getName(); }
}