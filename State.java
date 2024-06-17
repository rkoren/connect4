import java.util.SortedMap;
import java.util.TreeMap;

/** An instance represents a potential state of a game of Connect Four. */
public class State {
 private final Turn ai;     // The AI's turn.
    private final Board board; // The current Board layout.
    private final Turn player; // It is player's turn to make a move.

    /** Map all possible moves from this state's board to the states
     *  that would result from making those moves by this state's player.
     *  This is null iff this state has not been expanded yet.
     *  Keys are sorted, so that moves in earlier columns are placed earlier in the map. */
    private SortedMap<Move,State> children= null; // will not point to a map containing null keys or values

    private int value; // How desirable this State is for the AI.

    /** Constructor: a game State consisting of a board and a player who will move next.
     *  ai indicates which turn is the AI's turn. */
    public State(Turn ai, Board board, Turn player) {
     this.ai= ai;
        this.board= board;
        this.player= player;
    }
    
    /** Indicate whether this state has been expanded or not. */
    public boolean isExpanded() {
     return children != null;
    }
    
    /** Return the child resulting from move.
     *  Precondition: move is a possible move of this board's state
     *  Precondition: this state is expanded */
    public State getChild(Move move) {
     return children.get(move);
    }
    
    /** Return the preferred move for this state's player on this state's board.
     *  If there are multiple such moves, return the one with the left-most column.
     *  Precondition: this state has been expanded.
     *  Precondition: minimax has been calculated for this state and its descendants
     *  Precondition: this state's board has at least one possible move */
    public Move getPreferredMove() {
     //TODO: part 2 of A5
        //Hint: Look at the class invariant. Note that field children contains all the
        // children of this State. Note that field value gives the best value over all children.
        // Study the spec of class java.util.SortedMap to see how you can enumerate
        // states in children in order to look at their values.
        for (Move move : children.keySet()) {
            State childState = children.get(move);
            if (childState.value == this.value) {
                return move;
            }
        }
        return null;
    }
    
    /** If depth = zero, this does nothing.
     *  For depth > zero:
     *     1. If this state does not yet have children, first create child
     *        states corresponding to each move this state's player could make on
     *        this state's board.
     *     2. Expand this state's children up to depth-1.

     *  Precondition: depth >= 0. */
    public void expandUpTo(int depth) {
     //TODO: part 2 of A5
        //Hint: Field children is a SortedMap. SortedMap is an interface. So to
        // create an new object to store in field children, you need to use some class
        // that implements SortedMap. We suggest using class java.util.TreeMap.
        if (depth == 0) return;
        if (!isExpanded()) {
            children = new TreeMap<>();
            for (Move move : board.getPossibleMoves()) {
                Board newBoard = new Board(board, player, move);
                children.put(move, new State(ai, newBoard, player.getNext()));
            }
        }
        for (State child : children.values()) {
            child.expandUpTo(depth - 1);
        }
    }
    
    /** Compute and store the value of this state in field value.
     *    1. If this state's board has a connect four, its value is
     *       the maximum or minimum int value depending on who wins.
     *    2. If this state's board is full, its value is 0.
     *    3. If this state is not expanded, its value is the value of the board.
     *    4. Otherwise, this state's value is its player's preferred value of its
     *       child states' values. */
    public void computeMinimax() {
        //TODO: part 2 of A5
        //Hint: Implement in the order given by the specification.
        // Note that the player to move is in field player, which may or not be
        //   the same as field ai.
        // Note that step 4 requires calling minimax on the children.
        // Note that there is a method for computing preferred values.
        this.value = minimax(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private int minimax(int alpha, int beta) {
        Turn c4 = board.hasConnectFour();
        if(c4 != null) {
            if (c4 == ai) {
                this.value = Integer.MAX_VALUE;
                return this.value;
            } else {
                this.value = Integer.MIN_VALUE;
                return this.value;
            }
        } else if(board.isFull()) {
            this.value = 0;
            return 0;
        } else if(!this.isExpanded()) {
            this.value = computeBoardValue();
            return this.value;
        } else {
            if (player == ai) {
                int maxVal = Integer.MIN_VALUE;
                for (State child : children.values()) {
                    int val = child.minimax(alpha, beta);
                    maxVal = preferredValue(maxVal, val);
                    alpha = preferredValue(alpha, val);
                    if (alpha > beta) {
                        break;
                    }
                }
                this.value = maxVal;
                return this.value;
            } else {
                int minVal = Integer.MAX_VALUE;
                for (State child : children.values()) {
                    int val = child.minimax(alpha, beta);
                    minVal = preferredValue(minVal, val);
                    alpha = preferredValue(beta, val);
                    if (alpha > beta) {
                        break;
                    }
                }
                this.value = minVal;
                return this.value;
            }
        }
    }
    
    /** Compute the preferred value for this state's player. */
    private int preferredValue(int v1, int v2) {
     return player == ai ? Math.max(v1, v2) : Math.min(v1, v2);
    }

    /** Evaluate the desirability of this state's board for the AI. */
    private int computeBoardValue() {
        // Store in sum the value of this state's board. 
        int sum= 0;
        for (Iterable<? extends Board.Location> fourinarow : Board.getFourInARows())
            for (Board.Location loc : fourinarow)
                sum+= !loc.isOccupied(board) ? 0 : loc.getPlayer(board) == ai ? 1 : -1;
        return sum;
    }

    /** Return a String representation of this State. */
    public @Override String toString() {
        return toString(0,"");
    }

    /** Return a string that contains a representation of this board indented
     *  with string indent (expected to be a string of blank characters) followed
     *  by a similar representation of all its children,
     *  indented an additional indent characters. depth is the depth of this state. */
    private String toString(int depth, String indent) {
        String str= indent + (player == ai ? "AI" : "Opponent") +
                " will play next on the board below as " + player.getInitial() + "\n";
        str= str + indent + "Value: " + value + "\n";
        str= str + board.toString(indent) + "\n";
        if (children != null && !children.isEmpty()) {
            str= str + indent + "Children at depth "+ (depth+1) + ":\n" +
                    indent + "----------------\n";

            for (State s : children.values())
                str= str + s.toString(depth+1,indent + "   ");
        }
        return str;
    }
}
