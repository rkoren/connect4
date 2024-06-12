import java.util.function.Consumer;

/** A player that supplies moves synchronously for a Connect Four game. */
public abstract class SynchronousPlayer extends Player {
	/** Construct a SynchronousPlayer whose name is name. */
	protected SynchronousPlayer(String name) {
		super(name);
	}
	
	/** Return this Player's move for board.
	 *  Precondition: board is not null and has at least one possible move. */
	protected abstract Move getMove(Board board);
	
	/** Get the move for this player and use it synchronously. */
	public @Override final Async<Move> getAsyncMove(Board board) {
		return (Consumer<Move> consumer) -> consumer.accept(getMove(board));
	}
}
