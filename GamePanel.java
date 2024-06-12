import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.IntFunction;

import javax.swing.JPanel;
import javax.swing.Timer;


/** Display the Connect Four board and animations with a GUI panel. */
public class GamePanel {
	private Board board; // The board.
	
	private int moveNumber; // Number of moves made
	
	/** The animations that are in progress, sorted by when they were started. */
	private SortedSet<Animation> animations= new TreeSet<Animation>();

	/** Constructor: an instance displays the Connect Four board with a GUI panel. */
	public GamePanel(Board board) {
		this.board= board;

		final int animDelay= 15; //milliseconds
		new Timer(animDelay, (ActionEvent event) -> {
			boolean occupied= false;
			boolean removing= true;
			for (Iterator<Animation> iter= animations.iterator(); iter.hasNext(); ) {
				Animation animation= iter.next();
				if (animation.row <= 0) {
					if (occupied) {
						removing= false;
						continue;
					} else
						occupied= true;
				}
				if (animation.row < animation.stopRow) {
					animation.step++;
					if (animation.step >= Animation.MAX_STEPS) {
						animation.step= 0;
						animation.row++;
					}
				}
				if (animation.row >= animation.stopRow) {
					animation.row= animation.stopRow;
					if (removing) {
						GamePanel.this.board= animation.after;
						iter.remove();
					}
				} else
					removing= false;
			}
			panel.repaint();
		}).start();
	}
	
	/** The JPanel that paints the board and animations. */
	private final JPanel panel= new JPanel() {
		private static final long serialVersionUID= -4970049947798562800L;
		
	    // The colors for various parts of the panel.
		private final Color emptyColor= new Color(208,208,208);
		private final Color backColor= new Color(0,123,255);

		// The size and separation of the cells. 
		private final int cellSize= 62;
		private final int sepSize= 5;

		/** The width of the panel (in pixels). */
		private final int width= Board.NUM_COLS*cellSize + (Board.NUM_COLS+1)*sepSize;
		/** The height of the panel (in pixels). */
		private final int height= Board.NUM_ROWS*cellSize + (Board.NUM_ROWS+1)*sepSize;

		/** Paint the entire game panel, painting whatever is represented in this
		 *  object's version of the board and as well as the animation's falling tiles. */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d= (Graphics2D) g;
			g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
			
			// Paint tiles
			for (int x= sepSize, c= 0; c < Board.NUM_COLS; c++) {
				for (int y= sepSize, r= 0; r < Board.NUM_ROWS; r++) {
					paintChip(g2d, board.getPlayer(r, c), x, y, cellSize);
					y+= sepSize + cellSize;
				}
				x+= sepSize + cellSize;
			}
			
			// Paint the falling tiles of the animations
			for (Animation animation : animations)
				paintChip(g2d,
						animation.player,
						animation.col*(sepSize+cellSize) + sepSize,
						animation.row*(sepSize+cellSize) + sepSize + (animation.step-Animation.MAX_STEPS)*(sepSize+cellSize)/Animation.MAX_STEPS,
						cellSize);

			// Paint the background.
			Shape oldClip= g2d.getClip();
			Area clip= new Area(new Rectangle2D.Double(0, 0, width, height));
			for (int x= sepSize, c= 0; c < Board.NUM_COLS; c++) {
				for (int y= sepSize, r= 0; r < Board.NUM_ROWS; r++) {
					clip.subtract(new Area(new Ellipse2D.Double(x, y, cellSize, cellSize)));
					y+= sepSize + cellSize;
				}
				x+= sepSize + cellSize;
			}
			g2d.setClip(clip);
			g2d.setColor(backColor);
			g2d.fillRect(0, 0, width, height);
			g2d.setClip(oldClip);
			
			// Paint insets
			g2d.setStroke(new BasicStroke(2));
			for (int x= sepSize, c= 0; c < Board.NUM_COLS; c++) {
				for (int y= sepSize, r = 0; r < Board.NUM_ROWS; r++) {
					g2d.setColor(backColor);
					g2d.drawArc(x, y, cellSize, cellSize, 40, 10);
					g2d.drawArc(x, y, cellSize, cellSize, 220, 10);
					g2d.setColor(darker(backColor));
					g2d.drawArc(x, y, cellSize, cellSize, 50, 170);
					g2d.setColor(lighter(backColor));
					g2d.drawArc(x, y, cellSize, cellSize, 40, -170);
					y+= sepSize + cellSize;
				}
				x+= sepSize + cellSize;
			}
		}
		
		/** Paint a chip for player with circumference circum at (x, y). */
		private void paintChip(Graphics2D g2d, Turn player, int x, int y, int circum) {
			Color color= player == null ? emptyColor : player.getColor();
			g2d.setColor(color);
			g2d.fillOval(x, y, circum, circum);
			if (player != null) {
				g2d.setColor(darker(color));
				g2d.drawArc(x+circum/4, y+circum/4, circum/2, circum/2, 55, 160);
				g2d.setColor(lighter(color));
				g2d.drawArc(x+circum/4, y+circum/4, circum/2, circum/2, 35, -160);
			}
		}

		/** Return the Dimension of the panel. */
		public Dimension getPreferredSize() {
			return new Dimension(width,height);
		}
		
		/** Return a color slightly darker than color. */
		private Color darker(Color color) {
			return new Color(color.getRed()*3/4, color.getGreen()*3/4, color.getBlue()*3/4);
		}
		
		/** Return a color slightly lighter than color. */
		private Color lighter(Color color) {
			int luminance= (color.getRed() + color.getGreen() + color.getBlue() + 3*255)/2;
			IntFunction<Integer> brighten= (int v) -> (v*(3*255-luminance) + 255*luminance)/(3*255);
			return new Color(brighten.apply(color.getRed()), brighten.apply(color.getGreen()), brighten.apply(color.getBlue()));
		}
	};
	
	/** Animation is used to track the intermediate steps of a chip falling into place. */
	private static class Animation implements Comparable<Animation> {
		private static final int MAX_STEPS= 5;
		
		private final int moveNumber; // when did this move happen in the game
		public final Turn player; // who made the move
		public final int col; // in which column
		public int row; // where is the chip currently
		public int step; // where this chip is between rows
		public final int stopRow; // where should the chip stop
		public final Board after; // what is the board after this chip is in place
		
		/** Constructor: an animation for move number by player in col
		 * stopping at stop and resulting in after. */
		public Animation(int number, Turn player, int col, int stop, Board after) {
			this.moveNumber= number;
			this.player= player;
			this.col= col;
			this.row= -1;
			this.step= 0;
			this.stopRow= stop;
			this.after= after;
		}

		/** Animations for earlier moves are smaller than animations for later moves. */
		public @Override int compareTo(Animation that) {
			return this.moveNumber - that.moveNumber;
		}
	}

	/** Start the animation for player using column, falling until stopRow
	 * and resulting in Board after. */
	public void playColumn(Turn player, int column, int stopRow, Board after) {
		final Animation animation= new Animation(moveNumber++, player, column, stopRow, after);
		
		java.awt.EventQueue.invokeLater(() -> {
			animations.add(animation);
		});
	}
	
	/** Return the panel on which GamePanel draws. */
	public JPanel getPanel() {
		return panel;
	}
}
