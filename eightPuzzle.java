import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;
import java.util.Comparator;
import java.util.PriorityQueue;

public class eightPuzzle
{
	static boolean DEBUG_MODE = false;
	static boolean VERBOSE_MODE = false;
	static Board goal;
	public enum HEURISTIC {
		NONE,
		CONSTANT, 
		INCORRECT_TILES,
		PATH_PLUS_INCORRECT,
		MANHATTAN_DIST,
		DBL_MANHATTAN
	};
	static HEURISTIC currentHeuristic;
	public long heuristicTime = 0;
	
	// BoardComparator.java
	public class BoardComparator implements Comparator<Board>
	{
		// TODO implement a switch to use different heuristics and a getHeuristic method to use it
		@Override
		public int compare(Board board1, Board board2)
		{
			int retVal = 0;
			// TODO need to setCostEstimate here for each board
			
			if (board1 == board2){// optimization 
				retVal = 0;
			}else if (board1.getCostEstimate() < board2.getCostEstimate()){
				retVal = -1;
			}else if (board1.getCostEstimate() > board1.getCostEstimate()){
				retVal = 1;
			};
			return retVal;
		}		
		
	}
	
	public static void main(String[] args)
	{
		goal = new Board(new int[] {1,2,3,8,0,4,7,6,5});
		long startTime, endTime, duration;
		
		Board b = new Board(new int[] {1,3,4,8,6,2,7,0,5});//easy
		//Board b = new Board(new int[] {2,8,1,0,4,3,7,6,5});//medium
		//Board b = new Board(new int[] {2,8,1,4,6,3,0,7,5});//hard
		//Board b = new Board(new int[] {5,6,7,4,0,8,3,2,1});//worst
		
		// DFS
		eightPuzzle solver = new eightPuzzle();
		System.out.println("===DFS===");
		startTime = System.nanoTime();
		solver.dfs(b);
		endTime = System.nanoTime();
		duration = endTime - startTime;
		System.out.format("DFS duration = %3.5f s\n", (double) duration
				/ (double) 1000000000);
		
		// BFS
		System.out.println();
		solver = new eightPuzzle();
		System.out.println("===BFS===");
		startTime = System.nanoTime();
		solver.bfs(b);
		endTime = System.nanoTime();
		duration = endTime - startTime;
		System.out.format("BFS duration = %3.5f s\n", (double) duration
				/ (double) 1000000000);
		
	}
	
	
	
	/*
	 * This method implements blind depth-first search on the 8 puzzle,
	 *   keeping track of visited nodes to avoid infinite search. This 
	 *   method also outputs the first 15 nodes visited.
	 */
	public void dfs(Board b)
	{
		int count = 0;//used to output the first 15 nodes visited
		String first15states = "";
		HashSet<String> observedNodes = new HashSet<String>();//keeps track of visited states
		Stack<Board> stack = new Stack<Board>();//holds future states to explore 
		
		while(!b.equals(goal))
		{
			observedNodes.add(b.toString());
			stack.addAll(b.getSuccessors());
			b = stack.pop();
			while(observedNodes.contains(b.toString()))
			{
				b = stack.pop();
			}
			if(count < 15)
			{
				first15states += b + "\n";
				count++;
			}
		}
		System.out.println(observedNodes.size() + " nodes examined.");
		if(observedNodes.size() < 10000)
			printHistory(b);
		else
			System.out.println("Not printing history--leads to stack overflow");
		System.out.println(first15states);
	}
	
	// begin additional search methods
	/*
	 * 1. Breadth-first (blind) search This method implements blind
	 * breadth-first search on the 8 puzzle, keeping track of visited nodes to
	 * avoid infinite search. This method also outputs the first 15 nodes
	 * visited.
	 */
	public void bfs(Board b) {
		int count = 0;// used to output the first 15 nodes visited
		String first15states = "";
		// keeps track of visited states
		HashSet<String> observedNodes = new HashSet<String>();
		// vector acting as a FIFO queue to hold future states to explore 
		// 	since vector.remove(0): Removes the element at the specified position 
		// 	in this Vector. Shifts any subsequent elements to the left (subtracts 
		//	one from their indices). Returns the element that was removed from the Vector. 
		Vector<Board> nodeQueue = new Vector<Board>(); 

		while (!b.equals(goal)) {// check if b=goal
			observedNodes.add(b.toString()); // add b to list of observed nodes
			nodeQueue.addAll(b.getSuccessors()); // add successor nodes to the end of the queue
			b = nodeQueue.remove(0); // remove the first successor node from the queue to be examined

			// skip over nodes that have been visited before
			while (!b.equals(goal) && observedNodes.contains(b.toString())) { 
				b = nodeQueue.remove(0);
			}
			// record the node if less than 15
			if (count < 15) {
				first15states += b + "\n";
				count++;
			}
			// now loop to check if the current node is goal and add its successors to end of queue
		}
		System.out.println(observedNodes.size() + " nodes examined.");
		if (observedNodes.size() < 10000)
			printHistory(b);
		else
			System.out.println("Not printing history--leads to stack overflow");
		System.out.println(first15states);
		showCostFunction(b);
	}

	
	/**
	 * function prints path and heuristic costs for the solution
	 * @param b
	 */
	private void showCostFunction(Board b){
//		System.out.println("f(p) = c(p) + h(p) = " +
//				 b.getPathLength() + " + " + b.incorrectTilesHeuristic(goal));
//		System.out.format("Total heuristic time = %3.8f s\n", (double) b.getHeuristicTime()
//				/ (double) 1000000000);
		if (VERBOSE_MODE){
			System.out.println("Final Board: \n " + b.toString());
		}
	}
	public void setHeuristicType(HEURISTIC newHeuristic){
		currentHeuristic = newHeuristic;
		return;
	}

	public HEURISTIC getHeuristicType(){
		return currentHeuristic;
	}
	public int calcHeuristic(Board b){
		long startTime = System.nanoTime();
		int heuristicValue = 0;
		
		switch (currentHeuristic) {
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
			heuristicValue = b.getPathLength() + heuristicValue;
			break;
		case MANHATTAN_DIST: 
			// this is only used for A* so add the path length to it
			heuristicValue = manhattanDistanceHeuristic(goal);
			heuristicValue = b.getPathLength() + heuristicValue;
			break;
		case DBL_MANHATTAN:
			// this is only used for A* so add path length to it
			heuristicValue = doubleManhattanHeuristic(goal);
			heuristicValue = b.getPathLength() + heuristicValue;
			break;
		default :
			heuristicValue = 0;
		};
		if(VERBOSE_MODE)
			System.out.println("Using " + currentHeuristic + 
					" h(n)=" + heuristicValue);
		long endTime = System.nanoTime();
		// accumulate time spent int he heuristic function
		heuristicTime += (endTime - startTime);
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
	protected int incorrectTilesHeuristic(Board b) {
		int count = 0;
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				if (b.getTileAt(x, y) != goal.getTileAt(x, y))
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
//		for (int tileVal = 1; tileVal < 9; tileVal++) {
//			dx = Math.abs(getTileLocationX(tileVal) - thatBoard.getTileLocationX(tileVal));
//			dy = Math.abs(getTileLocationY(tileVal) - thatBoard.getTileLocationY(tileVal));
//			if (VERBOSE_MODE)
//				System.out.println("(V, this(x,y) , goal(x,y) ) = (" + tileVal 
//						+ ", " + getTileLocationX(tileVal)
//						+ "," + getTileLocationY(tileVal)
//						+ ", " + thatBoard.getTileLocationX(tileVal)
//						+ "," + thatBoard.getTileLocationY(tileVal)
//						+ ")");
//			manDist += (dx + dy);
//		}
		return manDist;
	}

	/*
	 * Heuristic 3 h3(n) = h2(n) * 2 heuristic function h = (sum of
	 * Manhattan distances) * 2
	 */
	protected int doubleManhattanHeuristic(Board board) {
		return ( manhattanDistanceHeuristic(board)*2 );
	}
	
	/*
	 * This method prints the move history of the beginning state to the current state 
	 *   where each move is understood as moving the blank so that
	 *   1 2 3         1 2 3
	 *   4 5 6    to   4 5 6   is understood as "Left" rather than "Right"
	 *   7 8 0         7 0 8 
	 */
	public void printHistory(Board b)
	{
		Vector<Board> boards = b.getPathFromStartNode();
		System.out.println();
		for(int i = 0; i < boards.size()-1; i++)
		{
			if(boards.get(i).getSuccessor(Board.DOWN) != null)
				if(boards.get(i).getSuccessor(Board.DOWN).equals(boards.get(i+1)))
					System.out.print("Down ");
			if(boards.get(i).getSuccessor(Board.LEFT) != null)
				if(boards.get(i).getSuccessor(Board.LEFT).equals(boards.get(i+1)))
					System.out.print("Left ");
			if(boards.get(i).getSuccessor(Board.RIGHT) != null)
				if(boards.get(i).getSuccessor(Board.RIGHT).equals(boards.get(i+1)))	
					System.out.print("Right ");
			if(boards.get(i).getSuccessor(Board.UP) != null)
				if(boards.get(i).getSuccessor(Board.UP).equals(boards.get(i+1)))
					System.out.print("Up ");
		}
		System.out.println();
	}
}