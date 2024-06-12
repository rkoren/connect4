import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/** A player that gets its moves from a file. */
public class File extends SynchronousPlayer {
	private final BufferedReader input; // the input being read from for moves
	private int linecount= 0; // the number of lines read so far
	
	/** An instance reading moves from file filename. */
	public File(Turn turn, String filename) throws FileNotFoundException {
		super(filename);
		input= new BufferedReader(new FileReader(filename));
	}

	/** Return the next move specified by this file.
	 *  Throw an IllegalArgumentException if the move is not a playable column. */
	public @Override Move getMove(Board board) {
		int column;
		try {
			String line= input.readLine();
			linecount++;
			try {
				column= Integer.parseInt(line);
			} catch (NumberFormatException err) {
				throw new IllegalArgumentException(
						"Line " + linecount + " specifies invalid move \"" + line + '\"');
			}
		} catch (IOException err) {
			throw new RuntimeException(err);
		}
		Move[] moves= board.getPossibleMoves();
		for (Move move : moves)
			if (move.getColumn() == column)
				return move;
		throw new IllegalArgumentException(
				"Line " + linecount + " specifies unplayable move " + column);
	}
}
