/** Used to set up Connect Four games. */
public class Setup {
	/** Starts a Connect Four game. */
	public static void main(String[] args) {
		/* Initialize setup however you want.
		 * You can assign it values directly,
		 * or you can make it read from the console.
		 * You can also set up run configurations for ConnectFour
		 * by going to menu Run -> Run Configurations...,
		 * or you can make other application classes like Setup.
		 * As you can see, args[1..2] describes the first player and
	     * args[3..4] the second. Three possibilities for a player:
	     *   1. "Human"  The next arg is their name
	     *   2. "Random" The the next arg is a seed for the random number generator
	     *   3. "AI"     The next arg is the depth of the game tree, > 0.
	     */ 
		String[] setup= {"GUI", "Human", "Ross", "Human", "David"};
		ConnectFour.main(setup);
	}
}
