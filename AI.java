/** An instance represents a Player that intelligently determines 
 *  Moves using the minimax algorithm. */
public class AI extends SynchronousPlayer {
	/** This AI's turn. */
    private final Turn turn; // not null

    /** The depth of the search of the game space for minimax. */
    private final int depth; // greater than or equal to 1
    
    /** The state of the board after this AI's last move.
     *  This will be initialized by the constructor (if AI is first)
     *  or by the first call of getMove (if AI is second). */
    private State state= null;

    /** Constructor: an instance with Turn turn who searches to depth depth
     * when searching the game space for moves.
     * Throw an IllegalArgumentException of depth does not represent an integer >= 1. */
    public AI(Turn turn, String depth) {
    	super("AI-" + turn.getInitial() + "" + depth);
    	this.turn= turn;
    	try {
    		this.depth= Integer.parseInt(depth);
        	if (this.depth < 1)
        		throw new IllegalArgumentException("depth must be at least 1");
    	} catch (NumberFormatException err) {
    		throw new IllegalArgumentException("depth must be an integer");
    	}
    }

	/** Return the AI's move for board recommend by minimax.
	 *  Precondition: board is not null and has at least one possible move. */
    public @Override Move getMove(Board board) {
    	if (state == null) {
    		state= new State(turn, board, turn);
    		state.expandUpTo(depth);
    		state.computeMinimax();
    	}
    	// At this point, thanks to observeMove,
    	// the board in state is the same as the parameter board
    	// and it has been expanded and had minimax computed
    	return state.getPreferredMove();
    }

    /** Change the state to reflect the opponent's move. */
	public @Override void observeMove(Board board, Turn player, Move move) {
		if (state == null)
			state= new State(turn, board, turn);
		else {
			if (!state.isExpanded())
				state.expandUpTo(1);
			state= state.getChild(move);
		}
		if (player != turn) {
			state.expandUpTo(depth);
			state.computeMinimax();
		}
	}
	
	/** Return the current state.
	 *  This exists solely for the StateLogger, which logs states
	 *  and their full minimax trees. */
	public State getCurrentState() {
		return state;
	}
}
