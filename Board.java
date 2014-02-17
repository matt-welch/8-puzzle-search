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
public class Board implements Comparable<Board>
{

	static private boolean VERBOSE_MODE = false;
	static private boolean DEBUG_MODE = true;
	// the internal board representation
	protected int[] board;
	public enum HEURISTIC {
		NONE,
		CONSTANT, 
		INCORRECT_TILES,
		PATH_PLUS_INCORRECT,
		MANHATTAN_DIST,
		DBL_MANHATTAN
	};
	// myHeuristic is static so it will be a class variable - all instances should share it
	private static HEURISTIC myHeuristic = HEURISTIC.NONE;

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
	// goal is static so all instances share it
	protected static Board goal;
	
	public void setGoal(Board newGoal){
		this.goal = newGoal;
	}
	public Board getGoal(){
		return this.goal;
	}

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
			if (successor != null){
				if (successor.getHeuristicType() != HEURISTIC.NONE){
//					successor.setGoal(this.getGoal());
//					successor.setHeuristicType(this.getHeuristicType());
					successor.setCostEstimate(successor.calcHeuristic(goal));
				}
				res.addElement(successor);
			}
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
	 * returns the location of a given tile represented by tileVal (0-8) within
	 * the internal data structure
	 * 
	 * @param tileVal
	 * @return the location of the tile in the internal data structure
	 */
	protected int getTileLocation(int tileVal){
		if (tileLocationCache[tileVal] < 0){
			int i = -1;
			for (i = 0; i < board.length && board[i] != tileVal; i++){
			}
			tileLocationCache[tileVal] = i;
		}
		return tileLocationCache[tileVal];
	}
	private int[] tileLocationCache = {-1,-1,-1,-1,-1,-1,-1,-1,-1};
	
	/**
	 * 
	 * @param tileVal
	 * @return the X coordinate of the location of the valued tile
	 */
	public int getTileLocationX(int tileVal){
		return getTileLocation(tileVal) % 3;
	}
	/**
	 * 
	 * @param tileVal
	 * @return the Y coordinate of the location of the valued tile
	 */	
	public int getTileLocationY(int tileVal){
		return (int) Math.floor(getTileLocation(tileVal) / 3);
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
	 * Returns the length of the path (i.e. the number of transitions)
	 * from the start state to this one
	 * 
	 * @return the length of the path from the start state
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

	// override the default compareTo method
	// reference from http://www.javapractices.com/topic/TopicAction.do?Id=10
	
	public int compareTo(Board thatBoard)
	{
		int retVal = 0;
		// Assume neither board is null
		if (this == thatBoard){// optimization 
			retVal = 0;
		}else if (getCostEstimate() < thatBoard.getCostEstimate()){
			retVal = -1;
		}else if (getCostEstimate() > thatBoard.getCostEstimate()) {
			retVal = 1;
		};
		if (VERBOSE_MODE)
			System.out.println("h(this) = " + calcHeuristic(this) + 
					", h(that) = " + calcHeuristic(thatBoard));
		return retVal;
	}

	public void setHeuristicType(HEURISTIC newHeuristic){
		myHeuristic = newHeuristic;
		return;
	}

	public HEURISTIC getHeuristicType(){
		if (VERBOSE_MODE) 
			System.out.println("Using Heuristic " + myHeuristic);
		return myHeuristic;
	}

	public int calcHeuristic(Board goal){
		int heuristicValue = 0;
		switch (myHeuristic) {
		case CONSTANT: 
			heuristicValue = constantHeuristic();
			break;
		case INCORRECT_TILES:
			// this is used for BestFS
			heuristicValue = incorrectTilesHeuristic(goal);
			break;
		case PATH_PLUS_INCORRECT:
			// this is only used for A* so add the path length to it
			heuristicValue = incorrectTilesHeuristic(goal);
			heuristicValue = getPathLength() + heuristicValue;
			break;
		case MANHATTAN_DIST: 
			// this is only used for A* so add the path length to it
			heuristicValue = manhattanDistanceHeuristic(goal);
			heuristicValue = getPathLength() + heuristicValue;
			break;
		case DBL_MANHATTAN:
			// this is only used for A* so add path length to it
			heuristicValue = doubleManhattanHeuristic(goal);
			heuristicValue = getPathLength() + heuristicValue;
			break;
		default :
			heuristicValue = 0;
		};
		if(VERBOSE_MODE)
			System.out.println("Using " + myHeuristic + 
					" h(n)=" + heuristicValue);
		return heuristicValue;
	}

	/*
	 * Heuristic 0 h0(n) = 0 simple heuristic that provides a constant output
	 * value for heuristics so algorithms can emulate their base cases
	 */
	protected int constantHeuristic() {
		return 1;
	}

	/*
	 * Heuristic 1 h1(n) = number of tiles that are not in the correct place
	 */
	protected int incorrectTilesHeuristic(Board thatBoard) {
		int count = 0;
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				if (this.getTileAt(x, y) != thatBoard.getTileAt(x, y))
					count++;
			}
		}
		return count;
	}

	/*
	 * Heuristic 2 h2(n) = the Manhattan heuristic function h = sum of
	 * Manhattan distances between all tiles and their correct positions.
	 * (Manhattan distance is the sum of the x distance and y distance
	 * magnitudes.)
	 */
	protected int manhattanDistanceHeuristic(Board thatBoard) {
		int manDist=0;
		int dx = 0;
		int dy = 0;
		for (int tileVal = 1; tileVal < 9; tileVal++) {
			dx = Math.abs(getTileLocationX(tileVal) - thatBoard.getTileLocationX(tileVal));
			dy = Math.abs(getTileLocationY(tileVal) - thatBoard.getTileLocationY(tileVal));
			if (VERBOSE_MODE)
				System.out.println("(V, this(x,y) , goal(x,y) ) = (" + tileVal 
						+ ", " + getTileLocationX(tileVal)
						+ "," + getTileLocationY(tileVal)
						+ ", " + thatBoard.getTileLocationX(tileVal)
						+ "," + thatBoard.getTileLocationY(tileVal)
						+ ")");
			manDist += (dx + dy);
		}
		return manDist;
	}

	/*
	 * Heuristic 3 h3(n) = h2(n) * 2 heuristic function h = (sum of
	 * Manhattan distances) * 2
	 */
	protected int doubleManhattanHeuristic(Board board) {
		return ( manhattanDistanceHeuristic(board)*2 );
	}



	public int hashCode() {
		return toString().hashCode();
	}

}
