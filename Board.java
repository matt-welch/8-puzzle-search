/*
 * Created on Mar 30, 2004
 * 
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/**
 * @author kgajos
 * 
 * A representation for the 8-puzzle board + some useful methods for generating
 * successor states
 */
public class Board {

  // the internal board representation
  protected int[] board;

  // constants for specifying move directions
  public static final int UP = -3;
  public static final int LEFT = -1;
  public static final int DOWN = 3;
  public static final int RIGHT = 1;

  // a complete list of all legal moves
  protected static final int[] legalMoves =
    new int[] { UP, LEFT, DOWN, RIGHT };

  // helps us keep track of how a solution was generated
  protected Board parent;

  // keeps track of the estimated distance to the goal state
  // (the f() function from the book)
  protected int costEstimate;

  /**
   * Creates an instance of the 8-puzzle board from a 1D array of
   * ints; blank tile is represented by 0
   * 
   * @param board
   *            an array of 9 ints
   * @param parent
   *            the board that generated this one as a successor
   */
  public Board(int[] board, Board parent) {
    this.board = board;
    this.parent = parent;
  }

  /**
   * Creates an instance of the 8-puzzle board from a 1D array of
   * ints; blank tile is represented by 0
   * 
   * @param board
   *            an array of 9 ints
   */
  public Board(int[] board) {
    this(board, null);
  }

  /**
   * returns the tile at a given location
   * 
   * @param x
   *            the X coordinate [0-2]
   * @param y
   *            the Y coordinate [0-2]
   * @return the tile at the specified location (0 for blank)
   */
  public int getTileAt(int x, int y) {
    return board[3 * y + x];
  }

  /**
   * Generate a specific successor of the current board by moving the
   * blank in the specified direction
   * 
   * @param moveDirection
   *            the direction for blank to move
   * @return a new Board object or null if the specified move is not legal
   */
  public Board getSuccessor(int moveDirection) {
    int newPos = getBlankLocation() + moveDirection;
    // check if the move is legal
    if (newPos < 0 || newPos >= board.length)
      return null;
    if (getBlankLocationX() == 0
	&& moveDirection == LEFT
	|| getBlankLocationX() == 2
	&& moveDirection == RIGHT)
      return null;
    int[] newBoard = new int[board.length];
    System.arraycopy(board, 0, newBoard, 0, board.length);
    // swap the blank tile and the tile that we are moving
    newBoard[getBlankLocation()] = board[newPos];
    newBoard[newPos] = 0;
    // create a new board object and return it
    return new Board(newBoard, this);
  }

  /**
   * Generates all possible successors of this board configuration
   * 
   * @return a Vector of Board objects
   */
  public Vector getSuccessors() {
    Vector res = new Vector();
    // attempt to generate successors for all possible moves
    for (int i = 0; i < legalMoves.length; i++) {
      Board successor = getSuccessor(legalMoves[i]);
      // check if the move was legal
      if (successor != null)
	res.addElement(successor);
    }
    return res;
  }

  /**
   * returns the location of the blank tile (represented by number 0) within
   * the internal data structure
   * 
   * @return the location of the blank tile in the internal data structure
   */
  protected int getBlankLocation() {
    if (blankLocationCache < 0) {
      int i = -1;
      for (i = 0; i < board.length && board[i] != 0; i++) {
      }
      blankLocationCache = i;
    }
    return blankLocationCache;
  }
  // cache of the blank location
  private int blankLocationCache = -1;

  /**
   * @return the X coordinate of the location of the blank tile
   */
  public int getBlankLocationX() {
    return getBlankLocation() % 3;
  }

  /**
   * @return the Y coordinate of the location of the blank tile
   */
  public int getBlankLocationY() {
    return (int) Math.floor(getBlankLocation() / 3);
  }

  /**
   * Returns the board that generated this one as a successor
   * 
   * @return the parent board (or null if this is the starting state)
   */
  public Board getParent() {
    return parent;
  }

  /**
   * Returns a vector that shows all the baord states from the
   * starting node to this one
   * 
   * @return the path from the start node (including the start node)
   */
  public Vector getPathFromStartNode() {
    if (pathCache == null) {
      if (parent == null)
	pathCache = new Vector();
      else
	pathCache = parent.getPathFromStartNode();
      pathCache.add(this);
    }
    // we are returning a clone of the cache because vectors are
    // passed by reference and we do not want other classes to be
    // messing around with our data structures
    return (Vector)pathCache.clone();
  }
  // cache for the path from the start node
  protected Vector pathCache;

	
	
  /**
   * Returns the lenght of the path (i.e. the number of transitions)
   * from the start state to this one
   * 
   * @return the lenght of the path from the start state
   */
  public int getPathLength() {
    if (pathCache == null)
      getPathFromStartNode();
    // we substract 1 because we are interested in the number of
    // _moves_ that were necessary to get from the start state to this
    // one
    return pathCache.size() - 1;
  }

  /**
   * Returns the stored value of the estimate of the cost of reaching
   * the goal state through this node
   */
  public int getCostEstimate() {
    return costEstimate;
  }

  /**
   * Allows you to set the estimate of the cost of reaching the goal state
   * through this node
   * 
   * @param costEstimate
   *            The cost Estimate to set.
   */
  public void setCostEstimate(int costEstimate) {
    this.costEstimate = costEstimate;
  }
	
  /**
   * Generates a random board instance
   * 
   * @return a random board instance
   */
  public static Board randomBoard() {
    ArrayList tiles = new ArrayList();
    for(int i=0; i<=8; i++)
      tiles.add(new Integer(i));
    int[] newBoard = new int[9];
    for(int i=0; i<=8; i++)
      newBoard[i] = ((Integer)tiles.remove((int)((double)tiles.size() 
						 * Math.random()))).intValue();
    return new Board(newBoard);
  }

  public String toString() {
    if (stringRepCache == null) {
      stringRepCache = "  |0 1 2\n--+-----\n";
      for (int y = 0; y < 3; y++) {
	stringRepCache += y + " |";
	for (int x = 0; x < 3; x++)
	  stringRepCache += getTileAt(x, y) + " ";
	stringRepCache += "\n";
      }
    }
    return stringRepCache;
  }
  // string rep cache -- useful because the hashCode is computed from it
  private String stringRepCache;

  public boolean equals(Object o) {
    return (
	    o != null
	    && o instanceof Board
	    && Arrays.equals(board, ((Board) o).board));
  }

  public int hashCode() {
    return toString().hashCode();
  }

}
