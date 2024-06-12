import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.FileSystems;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

/** A customizable logger that can log just about anything. */
public class MegaLogger implements Logger, MultilinePrinter {
    /** A PrintStream that prints to nothing. */
    private static final PrintStream ignore= new PrintStream(new OutputStream() {
        public @Override void write(int b) throws IOException { }
    });

    private final PrintStream board; // not null - all output also printed to states values
    private final Map<Turn,PrintStream> moves; // not null, total map to non-null values
    private final Map<Turn,PrintStream> states; // not null, all keys are non-null
    // not null, total map to non-null values - after all players are registered
    // if states is defined for some key, then corresponding player is an AI
    private final Map<Turn,Player> players= new EnumMap<Turn,Player>(Turn.class);

    /** Use args to create a logger that logs the requested events to the specified .txt files:<br/>
     *  board=FILENAME.txt - logs the board progress to the file<br/>
	    move1=FILENAME.txt - logs the moves for player 1 to the file<br/>
		move2=FILENAME.txt - logs the moves for player 2 to the file<br/>
		state1=FILENAME.txt - logs the states for AI 1 to the file<br/>
		state2=FILENAME.txt - logs the states for AI 2 to the file */
    public MegaLogger(String[] args) throws FileNotFoundException {
        if (args.length == 0)
            throw new IllegalArgumentException(
                    "MegaLogger requires at least one argument of the following forms:"
                            + " board=file.txt move1=file.txt move2=file.txt"
                            + " state1=file.txt state2=file.txt");
        // parse arguments
        String board= null;
        Map<Turn,String> moves= new EnumMap<Turn,String>(Turn.class);
        Map<Turn,String> states= new EnumMap<Turn,String>(Turn.class);
        for (String arg : args)
            if (arg.startsWith("board=")) {
                if (board != null)
                    throw new IllegalArgumentException(
                            "MetaLogger can only have output the board to one file");
                board= arg.substring(6);
            } else if (arg.startsWith("move1=")) {
                if (moves.put(Turn.FIRST, arg.substring(6)) != null)
                    throw new IllegalArgumentException(
                            "MetaLogger can only have output the moves for the first player to one file");
            } else if (arg.startsWith("move2=")) {
                if (moves.put(Turn.SECOND, arg.substring(6)) != null)
                    throw new IllegalArgumentException(
                            "MetaLogger can only have output the moves for the second player to one file");
            } else if (arg.startsWith("state1=")) {
                if (states.put(Turn.FIRST, arg.substring(7)) != null)
                    throw new IllegalArgumentException(
                            "MetaLogger can only have output the states for the first player to one file");
            } else if (arg.startsWith("state2=")) {
                if (states.put(Turn.SECOND, arg.substring(7)) != null)
                    throw new IllegalArgumentException(
                            "MetaLogger can only have output the states for the second player to one file");
            } else
                throw new IllegalArgumentException(
                        "MetaLogger only accepts arguments of the following forms:"
                                + " board=file.txt move1=file.txt move2=file.txt"
                                + " state1=file.txt state2=file.txt");

        // initialize using parsed arguments
        @SuppressWarnings("resource")
        final OutputStream output= board == null ? ignore : getOutput(board);
        this.board= new PrintStream(new OutputStream() {
            public @Override void write(int b) throws IOException {
                output.write(b);
                // Duplicate all board output to the state outputs
                for (PrintStream state : MegaLogger.this.states.values())
                    state.write(b);
            }
        }, true);

        this.moves= new EnumMap<Turn,PrintStream>(Turn.class);
        for (Entry<Turn,String> entry : moves.entrySet())
            this.moves.put(entry.getKey(), new PrintStream(getOutput(entry.getValue())));
        for (Turn turn : Turn.values())
            if (!this.moves.containsKey(turn))
                this.moves.put(turn, ignore);
        this.states= new EnumMap<Turn,PrintStream>(Turn.class);
        for (Entry<Turn,String> entry : states.entrySet())
            this.states.put(entry.getKey(), new PrintStream(getOutput(entry.getValue())));
    }

    /** Return an OutputStream for file filename.
     * Throw an IllegalArgumentException if filename is not a .txt file. */
    private static OutputStream getOutput(String filename) throws FileNotFoundException {
        if (!filename.endsWith(".txt"))
            throw new IllegalArgumentException(
                    "MegaLogger only outputs to .txt files to avoid unintended overwrites");
        return new FileOutputStream(FileSystems.getDefault().getPath(filename).toFile());
    }

    /** Record which player has which turns.
     *  Throw an IllegalArgumentException if player is not an AI. */
    public @Override void registerPlayer(Turn turn, Player player) {
        if (states.containsKey(turn) && !(player instanceof AI))
            throw new IllegalArgumentException("MegaLogger can only log the states of AI players");
        players.put(turn, player);
    }

    /** Log to file board  (and state files) that the game has begun.
     *  Log to file state the initial state of the first-player AI. */
    public @Override void start(Board board) {
        println(this.board, "Let the game begin!");
        println(this.board, players.get(Turn.FIRST).getName() + " is yellow. "
                + players.get(Turn.SECOND).getName() + " is red.");
        println(this.board, board);
        if (states.containsKey(Turn.FIRST)) {
            AI ai= (AI)players.get(Turn.FIRST);
            ai.getMove(board);
            println(states.get(Turn.FIRST), ai.getName() + "'s state is initially");
            print(states.get(Turn.FIRST), ai.getCurrentState());
        }
    }

    /** Log the move that was made to the move file for player.
     *  Log the move that was made to the board file (and state files).
     *  Log the change to the AI players' states, if any. */
    public @Override void observeMove(Board board, Turn player, Move move) {
        println(moves.get(player), move.getColumn());
        println(this.board, players.get(player).getName() + " put a chip in column " +
                move.getColumn() + ", resulting in");
        println(this.board, board);
        Turn opponent= player.getNext();
        if (states.containsKey(opponent)) {
            AI ai= (AI)players.get(opponent);
            println(states.get(opponent), ai.getName() + "'s state is now");
            print(states.get(opponent), ai.getCurrentState());
        }
    }

    /** Log to the board and state files the conclusion of the game,
     * based on winner. null means that no one won. */
    public @Override void gameOver(Player winner) {
        if (winner == null)
            println(board, "Tie game!");
        else
            println(board, winner.getName() + " won the game!!!");
    }

}
