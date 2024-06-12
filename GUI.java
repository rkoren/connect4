import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;

/** A Graphic User Interface for Connect Four. */
public class GUI extends UI {
	private boolean hasHumanPlayer= false; // indicates whether there are any human players
	private JFrame window; // the main window for this GUI
    private GamePanel gamePanel; // the panel in charge of displaying the board and animations
    private JLabel messageLabel; // the label displaying the game setting and result
    private JButton[] columnButtons; // the buttons used for getting moves from human players
                                    // ---null if there are no human players

    /** If playerClass is "Human", create a Player that uses buttons to get moves from a human;
     *  otherwise, defer to UI. */
	@Override
	public Player createPlayer(Turn turn, String playerClass, String argument)
			throws ClassNotFoundException, ClassCastException, NoSuchMethodException,
			InvocationTargetException, IllegalAccessException, InstantiationException {
		if (playerClass.equals("Human")) {
			hasHumanPlayer= true;
			return new Player(argument) {
				/** Wait for the human to indicate a move by clicking a column button. */
				public @Override Async<Move> getAsyncMove(Board board) {
					return (final Consumer<Move> callback) -> {
						final Move[] moves= board.getPossibleMoves();
						ActionListener listener= new ActionListener() {
							public @Override void actionPerformed(ActionEvent event) {
				                for (Move move : moves) {
				                	columnButtons[move.getColumn()].setEnabled(false);
				                	columnButtons[move.getColumn()].removeActionListener(this);
				                }
				                Object button= event.getSource();
				                for (int c= 0; c < columnButtons.length; c++)
				                    if (button == columnButtons[c]) {
				                    	final Move move= new Move(c);
				                    	java.awt.EventQueue.invokeLater(() -> {
				                    		callback.accept(move);
				                    	});
				                    	return;
				                    }
							}
						};
		                for (Move move : moves) {
		                	columnButtons[move.getColumn()].setEnabled(true);
		                	columnButtons[move.getColumn()].addActionListener(listener);
		                }
					};
				}
			};
		}
		return super.createPlayer(turn, playerClass, argument);
	}
	
    /** Create and display the window for board. */
    public void start(Board board) {
        window= new JFrame("CS 2110: Connect Four");

        window.setLayout(new BorderLayout());

        gamePanel= new GamePanel(board);
        columnButtons= new JButton[Board.NUM_COLS];

        //Message Panel
        JPanel msgPanel= new JPanel();
        msgPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        msgPanel.setPreferredSize(new Dimension(window.getWidth(), 18));
        msgPanel.setLayout(new GridLayout(1, 4));
        messageLabel= new JLabel(players.get(Turn.FIRST).getName() + " is yellow. "
                + players.get(Turn.SECOND).getName() + " is red.");
        msgPanel.add(messageLabel);

        if (hasHumanPlayer) // Toolbar of column buttons
	        window.add(createToolBar(), BorderLayout.NORTH);
        
        window.add(gamePanel.getPanel(), BorderLayout.CENTER);
        window.add(msgPanel, BorderLayout.SOUTH);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.setResizable(false); 
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /** Set up the action buttons at the top of the board for human interaction. */
    private JToolBar createToolBar() {
        for (int i= 0; i < columnButtons.length; i++) {
            columnButtons[i]= new JButton("Column " + i);
            columnButtons[i].setEnabled(false);
        }

        JToolBar toolBar= new JToolBar(JToolBar.HORIZONTAL);
        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        for (int i= 0; i < columnButtons.length; i++)
            toolBar.add(columnButtons[i]);

        return toolBar;
    }

    /** Run long tasks on a separate thread to prevent the GUI from locking up. */
	protected @Override <T> Async<T> doLongTask(final Supplier<T> task) {
		return (Consumer<T> consumer) -> {
			new SwingWorker<T,Void>() {
				T result;

				@Override
				protected T doInBackground() throws Exception {
					result= task.get();
					return result;
				}
				
				protected void done() {
					consumer.accept(result);
				}
			}.execute();
		};
	}

	/** Animate the move that was made. */
	protected @Override Board makeMove(Board board, Turn player, Move move) {
		int column= move.getColumn();
		int stopRow= 0;
		while (stopRow < Board.NUM_ROWS && board.getPlayer(stopRow, column) == null)
			stopRow++;
		board= new Board(board, player, move);
		gamePanel.playColumn(player, column, stopRow, board);
		return board;
	}

	/** Change the lower message to display the result of the game.
	 *  If winner is null, the game ended with a tie. */
	protected @Override void gameOver(Player winner) {
        if (winner == null)
        	messageLabel.setText(("Tie game!"));
        else
        	messageLabel.setText((winner.getName() + " won the game!!!"));
	}
}
