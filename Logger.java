/** Logger is used to log the events of a game. */
public interface Logger {
	/** Register with this logger the actual Players playing the game. */
	void registerPlayer(Turn turn, Player player);
	
	/** Called when game starts with board. */
	void start(Board board);
	
	/** Called after every time a move is made,
	 *  after the players have observed the move.
	 *  board is the state of the board after the move.
	 *  player is the player that made the move.
	 *  move is the move that was made. */
	void observeMove(Board board, Turn player, Move move);
	
	/** Called when the game ends with victor winner, which is null for a tie. */
	void gameOver(Player winner);
}
