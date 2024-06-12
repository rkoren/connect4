import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/** The Connect Four game */
public class ConnectFour {
 /** Run a Connect Four game using the specified user interface, players, and logging. */
 public static void main(String[] args) {
  // Give usage instructions
  if (args.length < 5) {
   System.out.println("Format is UI Player Argument Player Argument [Logger Arguments...]");
   System.out.println("UI is the user interface you want to use. We provide for you");
   System.out.println("    Console - executes an interactive textual game on the console");
   System.out.println("    GUI     - executes an interactive graphic game in a window");
   System.out.println("Player is the player you want to use. We provide for you");
   System.out.println("    Random - picks a random possible move each time. The argument is the seed.");
   System.out.println("    Human  - has a human as the player. The argument is the human's name.");
   System.out.println("    AI     - has a minimax algorithm as the player. The argument is the depth.");
   System.out.println("    File   - reads moves froom the lines of the file given by the argument.");
   System.out.println("Logger logs the events of the game. It is optional. We provide for you");
   System.out.println("    StateLogger - logs the states of the AI player specified by first argument");
   System.out.println("                  in the .txt file specified by the second argument");
   System.out.println("    MegaLogger  - logs the requested events to the specified .txt files");
   System.out.println("                  board=FILENAME.txt - logs the board progress to the file");
   System.out.println("                  move1=FILENAME.txt - logs the moves for player 1 to the file");
   System.out.println("                  move2=FILENAME.txt - logs the moves for player 2 to the file");
   System.out.println("                  state1=FILENAME.txt - logs the states for AI 1 to the file");
   System.out.println("                  state2=FILENAME.txt - logs the states for AI 2 to the file");
   return;
  }
  
  // Create the user interface
  UI ui;
  try {
   Class<? extends UI> uiClass= Class.forName(args[0]).asSubclass(UI.class);
   ui= uiClass.newInstance();
  } catch (ClassNotFoundException err) {
   System.out.println("The first argument must be the name of a class");
   return;
  } catch (ClassCastException err) {
   System.out.println("The first argument must be the name of a class extending the UI class");
   return;
  } catch (InstantiationException err) {
   System.out.println("The first argument must be the name of a non-abstract class with a nullary constructor");
   return;
  } catch (IllegalAccessException err) {
   System.out.println("The first argument must be the name of a non-abstract class with a public nullary constructor");
   return;
  }

  // Set up the first player
  setupPlayer(ui, Turn.FIRST, "second", args[1], args[2]);
  
  // Set up the second player
        setupPlayer(ui, Turn.SECOND, "fourth", args[3], args[4]);
  
  // Set up the logging if requested
  if (args.length > 5) {
   try {
    Class<? extends Logger> loggerClass= Class.forName(args[5]).asSubclass(Logger.class);
    ui.setLogger(loggerClass.getConstructor(String[].class).newInstance((Object)Arrays.copyOfRange(args, 6, args.length)));
   } catch (ClassNotFoundException err) {
    System.out.println("The optional fifth argument must be the name of a class");
    return;
   } catch (ClassCastException err) {
    System.out.println("The optional fifth argument must be the name of a class implementing the Logger interface");
    return;
   } catch (NoSuchMethodException err) {
    System.out.println("The fourth argument must be the name of a class with a constructor taking a String");
    return;
   } catch (InstantiationException err) {
    System.out.println("The optional fifth argument must be the name of a non-abstract class with a constructor taking a String[]");
    return;
   } catch (IllegalAccessException err) {
    System.out.println("The optional fifth argument must be the name of a non-abstract class with a public constructor taking a String[]");
    return;
   } catch (InvocationTargetException err) {
    throw new RuntimeException(err.getCause());
   }
  }
  
  // Finally, run the game!
  ui.runGame();
 }
 
 /** Set up the player for turn.
  *  position is which command-line argument specifies player: either "second" or "fourth"
  *  player is the name of the class implementation Player.
  *  turn and argument are the arguments for that class's constructor. */
 public static void setupPlayer(UI ui, Turn turn, String position, String player, String argument) {
     String theArg= "The " + position + "argument must be ";
     try {
            ui.setPlayer(turn, ui.createPlayer(turn, player, argument));
        } catch (ClassNotFoundException err) {
            System.out.println(theArg + " the name of a class");
            return;
        } catch (ClassCastException err) {
            System.out.println(theArg + " the name of a class extending the Player class");
            return;
        } catch (NoSuchMethodException err) {
            System.out.println(theArg + " the name of a class with a constructor taking a String");
            return;
        } catch (InstantiationException err) {
            System.out.println(theArg + " the name of a non-abstract class with a constructor taking a String");
            return;
        } catch (IllegalAccessException err) {
            System.out.println(theArg + " the name of a non-abstract class with a public constructor taking a String");
            return;
        } catch (InvocationTargetException err) {
            throw new RuntimeException(err.getCause());
        }
 }

}
