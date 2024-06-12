import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** A user interface for running, displaying, and interacting with a Connect Four game. */
public abstract class UI {
    // the players of the game
    protected EnumMap<Turn,Player> players= new EnumMap<Turn,Player>(Turn.class);
    private Board board= new Board(); // the current board for the game
    // the logger for the game - initially one that does nothing
    private Logger logger= new Logger() { 
        public @Override void start(Board board) { }
        public @Override void observeMove(Board board, Turn player, Move move) { }
        public @Override void gameOver(Player winner) { }
        public @Override void registerPlayer(Turn turn, Player player) { }
    };

    /** Look up a class implementing Player with name player.
     *  Construct an instance of that class
     *  by providing turn and argument as the arguments to its constructor. */
    public Player createPlayer(Turn turn, String player, String argument)
            throws ClassNotFoundException, ClassCastException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException, InstantiationException {
        Class<? extends Player> playerClass = Class.forName(player).asSubclass(Player.class);
        return playerClass.getConstructor(Turn.class, String.class).newInstance(turn, argument);
    }

    /** Set which player has which turn.
     *  This is only called once for each turn. */
    public void setPlayer(Turn turn, Player player) {
        players.put(turn, player);
    }

    /** Set the logger for this game to logger. */
    public final void setLogger(Logger logger) {
        this.logger= logger;
    }

    /** Run the Connect Four game. */
    public final void runGame() {
        for (Entry<Turn,Player> entry : players.entrySet())
            logger.registerPlayer(entry.getKey(), entry.getValue());
        logger.start(board);
        start(board);
        players.get(Turn.FIRST).getAsyncMove(board).async(new Consumer<Move>() {
            Turn turn = Turn.FIRST;

            public @Override void accept(Move move) {
                final Consumer<Move> driver= this;
                board = makeMove(board, turn, move);
                UI.this.<Void>doLongTask(() -> {
                    for (Player player : players.values())
                        player.observeMove(board, turn, move);
                    logger.observeMove(board, turn, move);
                    return null;
                }).async((Void v) -> {
                    turn= turn.getNext();
                    if (board.isFull()) {
                        gameOver(null);
                        return;
                    }
                    Turn winner= board.hasConnectFour();
                    if (winner != null) {
                        logger.gameOver(players.get(winner));
                        gameOver(players.get(winner));
                        return;
                    }
                    doLongTask(() -> {
                        return players.get(turn).getAsyncMove(board);
                    }).async((Async<Move> async) -> {
                        async.async(driver);
                    });
                });
                return;
            }
        });
    }

    /** Do a long task. If appropriate, do it on a separate thread. */
    protected abstract <T> Async<T> doLongTask(Supplier<T> task);

    /** Display the start of the game. */
    protected abstract void start(Board board);

    /** Display the move made on board by player.
     *  board is the state of the board before the move was made. */
    protected abstract Board makeMove(Board board, Turn player, Move move);

    /** Display the end of the game, with winner as the victor.
     *  If winner is null, then the game ended in a tie. */
    protected abstract void gameOver(Player winner);
}
