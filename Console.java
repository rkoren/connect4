import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** A user interface that uses the console to display the progress of the game.
 *  It implements the Human player by asking for input on the console. */
public class Console extends UI implements MultilinePrinter {
    private final BufferedReader input; // the input to read moves from
    private final PrintStream output; // the output to print progress to

    /** Constructor: an instance reading from and printing to the command line. */
    public Console() {
        input= new BufferedReader(new InputStreamReader(System.in));
        output= System.out;
    }

    /** If player is "Human", return a Player that gets moves from a human via the console,
     *  named argument; otherwise, defer to UI. */
    public @Override Player createPlayer(Turn turn, String player, String argument)
            throws ClassNotFoundException, ClassCastException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException, InstantiationException {
        if (player.equals("Human"))
            return new SynchronousPlayer(argument) {
            /** Ask the human for a move on the console. */
            public @Override Move getMove(Board board) {
                int column;
                while (true) {
                    try {
                        Move[] moves= board.getPossibleMoves();
                        boolean[] available= new boolean[Board.NUM_COLS];
                        for (Move move : moves)
                            available[move.getColumn()]= true;
                        for (int c= 0; c < Board.NUM_COLS; c++)
                            print(output, available[c] ? " " + c : "  ");
                        println(output, "\n\nWhat is " + getName() + "'s move?");
                        String move= input.readLine();
                        column= Integer.parseInt(move);
                        if (column < 0 || column >= Board.NUM_COLS)
                            println(output, "Move must be an integer within 0 through " + Board.NUM_COLS);
                        else if (board.columnIsFull(column))
                            println(output, "Move must be a column that is not full");
                        else
                            break;
                    } catch (IOException err) {
                        throw new RuntimeException(err);
                    } catch (NumberFormatException err) {
                        println(output, "Move must be an integer");
                    }
                }
                return new Move(column);
            }
        };
        return super.createPlayer(turn, player, argument);
    }

    /** Do long tasks immediately, since the console is single-threaded. */
    protected @Override <T> Async<T> doLongTask(Supplier<T> task) {
        return (Consumer<T> consumer) -> consumer.accept(task.get());
    }

    /** Display the start of the game on the console. */
    protected @Override void start(Board board) {
        println(output, "Let the game begin!");
        println(output, players.get(Turn.FIRST).getName() + " is yellow. "
                + players.get(Turn.SECOND).getName() + " is red.");
        print(output, board);
    }

    /** Display a move being made by player on board on the console.
     *  board is the state of the board before the move is made by player.
     *  Return the board resulting from this move. */
    protected @Override Board makeMove(Board board, Turn player, Move move) {
        board= new Board(board, player, move);
        println(output, "\n" + players.get(player).getName() + 
                " put a chip in column " + move.getColumn() + ", resulting in");
        print(output, board);
        return board;
    }

    /** Display the end of the game on the console, where winner won.
     *  If winner is null, then the game ended with a tie. */
    protected @Override void gameOver(Player winner) {
        println(output);
        if (winner == null)
            println(output, "Tie game!");
        else
            println(output, winner.getName() + " won the game!!!");
    }
}
