import java.io.FileNotFoundException;
import java.io.PrintStream;

/** A logger that log the states of an AI player. */
public class StateLogger implements Logger, MultilinePrinter {
    private final Turn turn; // The AI's turn
    private final PrintStream output; // Where to output to
    private AI ai; // The AI whose states are being logged
    private Player opponent; // The AI's opponent

    /** The first argument indicates which player to log the states of.
     *  The second argument indicates which .txt file to log to. */
    public StateLogger(String[] args) throws FileNotFoundException {
        if (args.length != 2)
            throw new IllegalArgumentException("StateLogger takes precisely two arguments");
        if (args[0].toLowerCase().equals("first"))
            turn= Turn.FIRST;
        else if (args[0].toLowerCase().equals("second"))
            turn= Turn.SECOND;
        else
            throw new IllegalArgumentException("StateLogger's first argument must" +
                    " be \"first\" or \"second\"");
        if (!args[1].endsWith(".txt"))
            throw new IllegalArgumentException("To prevent accidental overwrites, " +
                    " StateLogger's second argument must be a .txt file");
        output= new PrintStream(args[1]);
    }

    /** Record which player is the AI and which is the opponent.
     *  Check that the AI player is in fact an AI. */
    public @Override void registerPlayer(Turn turn, Player player) {
        if (this.turn == turn)
            try {
                ai= (AI)player;
            } catch (ClassCastException err) {
                throw new IllegalArgumentException("Can log states only for AI players");
            }
        else
            opponent= player;
    }

    /** Log that the game has begun.
     *  If the AI player goes first, then also log its initial state. */
    public @Override void start(Board board) {
        println(output, "Let the game begin!");
        Player first= turn == Turn.FIRST ? ai : opponent;
        Player second= turn == Turn.SECOND ? ai : opponent;
        println(output, first.getName() + " is yellow. "
                + second.getName() + " is red.");
        println(output, board);
        if (turn == Turn.FIRST) {
            ai.getMove(board);
            println(output, "The AI's state is initially");
            print(output, ai.getCurrentState());
        }
    }

    /** Log the move that was made.
     *  Log the change in the AI's state, if any. */
    public @Override void observeMove(Board board, Turn turn, Move move) {
        Player player= turn == this.turn ? ai : opponent;
        println(output, player.getName() + " put a chip in column "
                + move.getColumn() + ", resulting in");
        println(output, board);
        if (turn != this.turn) {
            println(output, "The AI's state is now");
            print(output, ai.getCurrentState());
        }
    }

    /** Log the conclusion of the game. */
    public @Override void gameOver(Player winner) {
        if (winner == null)
            println(output, "Tie game!");
        else
            println(output, winner.getName() + " won the game!!!");
    }
}
