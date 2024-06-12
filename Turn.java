import java.awt.Color;

/** A player can start either first or second. */
public enum Turn {
    FIRST(new Color(240, 240, 0), 'Y') { public Turn getNext() { return SECOND; } },
    SECOND(Color.RED, 'R') { public Turn getNext() { return FIRST; } };
    
	private final Color color; // color for this turn.
	private final char initial; // initial to use to represent the color.
	
	/** Construct a Turn playing chips of color color represented by character initial. */
	private Turn(Color color, char initial) {
		this.color= color;
		this.initial= initial;
	}
	
	/** Return the color of the chips for this turn. */
	public Color getColor() { return color; }
	
	/** Return the initial for the color of the chips for this turn. */
	public char getInitial() { return initial; }
	
	/** Return the turn of the player that goes after this one. */
	public abstract Turn getNext();
}
