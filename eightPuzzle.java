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
	static int[] goalLocationCache = {-1,-1,-1,-1,-1,-1,-1,-1,-1};
	static int[] goalLocationCacheX = {-1,-1,-1,-1,-1,-1,-1,-1,-1};
	static int[] goalLocationCacheY = {-1,-1,-1,-1,-1,-1,-1,-1,-1};
	private int nodesVisited = 0;
	
	public enum HEURISTIC {
		NONE,
		CONSTANT, 
		INCORRECT_TILES,
		PATH_PLUS_INCORRECT,
		MANHATTAN_DIST,
		DBL_MANHATTAN
	};
	static HEURISTIC currentHeuristic;
	static protected long heuristicTime = 0;
	Comparator<Board> boardComparator = new BoardComparator();

	// BoardComparator.java
	public class BoardComparator implements Comparator<Board>
	{
		@Override
		public int compare(Board board1, Board board2)
		{
			int retVal = 0;
			// set Cost Estimate here for each board
			int cost1 = calcCostEstimate(board1);
			int cost2 = calcCostEstimate(board2);
			board1.setCostEstimate(cost1);
			board2.setCostEstimate(cost2);
			
			if (board1 == board2){// optimization 
				retVal = 0;
			}else if (cost1 < cost2){
				retVal = -1;
			}else if (cost1 > cost2){
				retVal = 1;
			};
			return retVal;
		}		
	}
	
	public static void main(String[] args)
	{
		goal = new Board(new int[] {1,2,3,8,0,4,7,6,5});
		buildGoalLocationCache();
		long startTime, endTime, duration;
		
		int[] easy = {1,3,4,8,6,2,7,0,5};//easy (d=5)
		int[] medium = {2,8,1,0,4,3,7,6,5};// medium
		int[] hard = {2,8,1,4,6,3,0,7,5};//hard
		int[] worst = {5,6,7,4,0,8,3,2,1};//worst
		int[] array = easy; // default

		int boardSelection = 0;
		String boardSelectionString = "easy";
		int testsToRun = 0;
		boolean[] algorithmSelection = {true,true,true,true,true,true};

		if (args.length == 0){
			printUsage();
			System.out.println("\n Using defaults [easy, all algorithms]\n");
		}
		if (args.length > 0) {
			// determine which starting board to use 
			if (args[0].contentEquals("-h")){
				printUsage();
				return;
			}else{
				try {
					boardSelection = Integer.parseInt(args[0]);
				} catch (NumberFormatException e) {
					System.err.println("Argument[1]" + " must be an integer or \"-h\"");
					System.exit(1);
				}
				switch (boardSelection){
				case 1:
					array=easy;
					boardSelectionString = "easy";
					break;
				case 2: 
					array=medium;
					boardSelectionString = "medium";
					break;
				case 3: 
					array=hard;
					boardSelectionString = "hard";
					break;
				case 4:
					array=worst;
					boardSelectionString = "worst";
					break;
				default:
					array=easy;
				}
			}
		}
		if(args.length > 1){
			// determine which algorithms (1-6) to run
			int whichAlgorithm = 0;
			String errMsg = "Argument[2]" + " must be an integer between 1 and 6 (1<=i<=6) or \"all\"";
			if(!args[1].startsWith("a")){
				// if algorithms argument begins with a, run all algorithms, else, parse for index
				try {
					whichAlgorithm = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					System.err.println(errMsg);
					System.exit(1);
				}
				if(whichAlgorithm<1 || whichAlgorithm>6){
					System.err.println(errMsg);
					System.exit(1);	
				}	
				for(int i=0; i<6; i++){
					algorithmSelection[i]=false;
				}
				algorithmSelection[whichAlgorithm-1] = true;
			}
		}
		
		
		Board b = new Board(array);
//		b = Board.randomBoard();
		System.out.println("Searching on \"" + boardSelectionString + "\" board: \n" + b.toString());
		System.out.println("Searching for goal: \n" + goal.toString());

		eightPuzzle solver;
		
//		// DFS
//		solver = new eightPuzzle();
//		System.out.println("===DFS (#0)===");
//		startTime = System.nanoTime();
//		solver.dfs(b);
//		endTime = System.nanoTime();
//		duration = endTime - startTime;
//		System.out.format("DFS duration = %3.8f s\n\n", (double) duration
//				/ (double) 1000000000);
		
		if(algorithmSelection[0]){
			// BFS
			solver = new eightPuzzle();
			System.out.println("===BFS (#1)===");
			startTime = System.nanoTime();
			solver.bfs(b);
			endTime = System.nanoTime();
			duration = endTime - startTime;
			System.out.format("BFS duration = %3.8f s\n\n", duration / 1000000000.0);
		}
		// following search methods need a goal and heuristics

		if(algorithmSelection[1]){
			//BestFS
			// use the incorrect tiles heuristic
			b = new Board(array);
			solver = new eightPuzzle();
			System.out.println("===BestFS (#2)===");
			setHeuristicType(HEURISTIC.INCORRECT_TILES);
			startTime = System.nanoTime();
			solver.bestfs(b);
			endTime = System.nanoTime();
			duration = endTime - startTime;
			System.out.format("BestFS duration = %3.8f s\n\n", (double) duration
					/ (double) 1000000000);
		}
		
		if(algorithmSelection[2]){
			/*3. A* search using the heuristic function h = number of tiles that are
			 * not in the correct place (not counting the blank).*/
			//A* search, h(n)=PATH+PLUS_INCORRECT_TILES
			b = new Board(array);
			solver = new eightPuzzle();
			System.out.println("===A* (Incorrect Tiles) (#3)===");
			setHeuristicType(HEURISTIC.PATH_PLUS_INCORRECT);
			startTime = System.nanoTime();
			solver.aStarSearch(b);
			endTime = System.nanoTime();
			duration = endTime - startTime;
			System.out.format("A* (IncorrectTiles) duration = %3.8f s\n\n", (double) duration
					/ (double) 1000000000);
		}
		
		if(algorithmSelection[3]){
			/*
			 * 4. A* search using the Manhattan heuristic function h = sum of Manhattan
			 * distances between all tiles and their correct positions. (Manhattan
			 * distance is the sum of the x distance and y distance magnitudes.)
			 */
			//A* search, h(n)=MANHATTAN_DIST+PATH
			b = new Board(array);
			solver = new eightPuzzle();
			System.out.println("===A* (Manhattan Dist) (#4)===");
			setHeuristicType(HEURISTIC.MANHATTAN_DIST);
			startTime = System.nanoTime();
			solver.aStarSearch(b);
			endTime = System.nanoTime();
			duration = endTime - startTime;
			System.out.format("A* (Manhattan Dist) duration = %3.5f s\n\n", (double) duration
					/ (double) 1000000000);
		}
		
		if(algorithmSelection[4]){
			/*
			 * 5. A* search using the heuristic function 
			 * h = (sum of Manhattan distances) * 2.
			 */
			//A* search, h(n)=DBL_MANHATTAN_DIST
			b = new Board(array);
			solver = new eightPuzzle();
			System.out.println("===A* (Double Manhattan Dist) (#5)===");
			setHeuristicType(HEURISTIC.DBL_MANHATTAN);
			startTime = System.nanoTime();
			solver.aStarSearch(b);
			endTime = System.nanoTime();
			duration = endTime - startTime;
			System.out.format("A* (Double Manhattan Dist) duration = %3.5f s\n\n", (double) duration
					/ (double) 1000000000);
		}

		if(algorithmSelection[5]){
			/*
			 * 6. Iterative Deepening search, with testing for duplicate states.
			 * "Consider making a breadth first search into an iterative deepening
			 * search. This is carried out by having a depth-first searcher, which
			 * searches only to a limited depth. It can first do a depth first search to
			 * depth 1 by building paths of length 1 in a depth-first manner. Then d=2
			 * and so on
			 */
			b = new Board(array);
			solver = new eightPuzzle();
			System.out.println("===Iterative Deepening (#6)===");
			setHeuristicType(HEURISTIC.NONE);
			startTime = System.nanoTime();
			solver.iterativeDeepening(b);
			endTime = System.nanoTime();
			duration = endTime - startTime;
			System.out.format("Iterative Deepening duration = %3.8f s\n\n", (double) duration
					/ (double) 1000000000);
		}
	}
		
	/*
	 * This method implements blind depth-first search on the 8 puzzle,
	 *   keeping track of visited nodes to avoid infinite search. This 
	 *   method also outputs the first 15 nodes visited.
	 */
	public void dfs(Board b)
	{
		int count = 0;//used to output the first 15 nodes visited
		setHeuristicTime(0);
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
		if(observedNodes.size() < 10000){
			printHistory(b);
			printPathInfo(b, observedNodes);
		}
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
		setHeuristicTime(0);
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

			// skip over nodes that have been visited before since any parent of 
			// a state is also a child of the state
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
		printPathInfo(b, observedNodes);
	}
	
	/*
	 * 2. Best-first search using the heuristic function h = number of tiles
	 * that are not in the correct place (not counting the blank).
	 * 
	 * implemented as a priority queue where the next node chosen is the
	 * node with the lowest h1(n) where h1(n) is the number of tiles that are
	 * not in the correct place (not counting the blank
	 */
	public void bestfs(Board b) {
		int count = 0;// used to output the first 15 nodes visited
		setHeuristicTime(0);
		String first15states = "";
		// keeps track of visited states
		HashSet<String> observedNodes = new HashSet<String>();
		// hold future states to explore in a priority queue
		PriorityQueue<Board> boardPriorityQueue = new PriorityQueue<Board>(1000, boardComparator);

		// set the cost estimate for b based on the current heuristic
		int h = calcCostEstimate(b);
		b.setCostEstimate(h);

		System.out.println("h(n) (init) " + h);
		while (!b.equals(goal)) {
			observedNodes.add(b.toString());
			// add successor nodes to the queue
			boardPriorityQueue.addAll(b.getSuccessors());
			b = boardPriorityQueue.remove();
			while (observedNodes.contains(b.toString())) {
				b = boardPriorityQueue.remove();
			}
			if (count < 15) {
				first15states += b + "\n";
				count++;
			}
		}
		System.out.println(observedNodes.size() + " nodes examined.");
		if (observedNodes.size() < 10000)
			printHistory(b);
		else
			System.out.println("Not printing history--leads to stack overflow");
		System.out.println(first15states);
		printPathInfo(b, observedNodes);
	}

	/**
	 * A* is implemented by treating the frontier as a priority queue
	 * ordered by f(p)=c(p)+h2(n)
	 * 
	 * @param b
	 */
	public void aStarSearch(Board b) {
		int count = 0;// used to output the first 15 nodes visited
		setHeuristicTime(0);
		String first15states = "";
		// keeps track of visited states
		HashSet<String> observedNodes = new HashSet<String>();
		// hold future states to explore in a priority queue
		PriorityQueue<Board> boardPriorityQueue = new PriorityQueue<Board>(1000, boardComparator);
		// set the cost estimate for b based on the current heuristic
		int h = calcCostEstimate(b);
		b.setCostEstimate(h);

		System.out.println("Using heuristic: " + getHeuristicType());
		System.out.println("h(n) (init) " + h);
		while (!b.equals(goal)) {
			observedNodes.add(b.toString());
			// add successor nodes to the queue
			boardPriorityQueue.addAll(b.getSuccessors());
			b = boardPriorityQueue.remove();
			while (observedNodes.contains(b.toString())) {
				b = boardPriorityQueue.remove();
			}
			if (count < 15) {
				first15states += b + "\n";
				count++;
			}
		}
		System.out.println(observedNodes.size() + " nodes examined.");
		if (observedNodes.size() < 10000)
			printHistory(b);
		else
			System.out.println("Not printing history--leads to stack overflow");
		System.out.println(first15states);
		printPathInfo(b, observedNodes);
	}
	
	/*
	 * This method implements Iterative Deepening search on the 8 puzzle, keeping
	 * track of visited nodes to avoid infinite search. Iterative DDeepening is a 
	 * modification of depth first search where the DFS is repeated at increasing 
	 * depth bounds, beginning with the root each time.  
	 * This method also outputs the first 15 nodes visited.
	 */
	public void iterativeDeepening(Board b) {
		setHeuristicTime(0);
		int depthBound = -1;
		long iterDeepStartTime = 0;
		final int maxDepth = 36; // diameter of search space & optimal path length (biased high)
		nodesVisited = 0;
		boolean goalFound = false;
		
		if (VERBOSE_MODE){
			System.out.println("Starting State: \n" + b);
			System.out.println("Goal State: \n" + goal);
		}
		while (!goalFound && depthBound < maxDepth){
			iterDeepStartTime = System.nanoTime();
			depthBound++;
			if (DEBUG_MODE)
				System.out.println("Searching at depthBound = " + depthBound + "...");
			goalFound = boundedDFS(b, depthBound);
			if (DEBUG_MODE) 
				System.out.format("\tCompleted in %4.4f s\n", (System.nanoTime() - iterDeepStartTime)/1000000000.0 );
		}
		if (goalFound)
			System.out.println("Iterative deepening found a solution in " + depthBound + " rounds.");
		else
			System.out.println("Iterative deepening terminated unnaturally in " + depthBound + " rounds.");
	}
	
	public boolean boundedDFS(Board b, int depthBound){
		boolean goalFound = false;
		boolean loopCheck = true;
		int pathLength = 0;
		int count = 1;// used to output the first 15 nodes visited
		String first15states = "";
		// keeps track of visited states
		HashSet<String> observedNodes = new HashSet<String>();
		// holds future states to explore
		Stack<Board> stack = new Stack<Board>();

		while (!b.equals(goal)) {
			pathLength = b.getPathLength();
			observedNodes.add(b.toString());
			if (VERBOSE_MODE)
				System.out.println(b.toString());
			if(pathLength < depthBound){
				// only add more successors of a node if the depth bound has not been reached
				// as if the graph terminated there
				stack.addAll(b.getSuccessors());
			}
			if(stack.isEmpty()){
				// will cause bounded DFS to terminate unnaturally (empty stack)
				goalFound=false;
				break;
			}
			b = stack.pop();

			// don't need cycle checking for iterative deepening since the depth 
			// bound will be reached before it gets stuck in cycles 
			if (loopCheck){
				while (observedNodes.contains(b.toString()) && !stack.isEmpty()) {
					b = stack.pop();
				}
			}
			if (b.equals(goal)){
				goalFound = true;
				System.out.println("Goal found in previously examined node");
				break;
			}
			if (count < 15) {
				first15states += b + "\n";
			}
			count++; // increment count regardless of first15 to accumulate nodesVisited
		}
	
		nodesVisited += count;
		if (DEBUG_MODE)
			System.out.println("Iterative Deepening visited " + count + " states, "+ nodesVisited + " total");
		if (goalFound == false){
			// terminate "unnaturally"
			if (VERBOSE_MODE)
				System.out.println("boundedDFS terminating unnaturally at depth =" +
						depthBound  + ", Observed Nodes = " + observedNodes.size());

		}else{
			// terminate naturally
			System.out.println(first15states);
			System.out.println(observedNodes.size() + " nodes examined.");
			if (observedNodes.size() < 10000)
				printHistory(b);
			else
				System.out.println("Not printing history--leads to stack overflow");
			printPathInfo(b, observedNodes);
		}
		return goalFound;
	}

	
	/**
	 * function prints path and heuristic costs for the solution
	 * @param b
	 */
	static private void printPathInfo(Board b, HashSet observedNodes){
		System.out.println("c(p) = " + b.getPathLength() );
		if (currentHeuristic != null && currentHeuristic != HEURISTIC.NONE){
			double hTime = (double) getHeuristicTime() / (double) 1000000000;
			System.out.format("Total heuristic time    = %3.8f s\n", hTime);
			System.out.format("Heuristic Time per node = %3.8f ms\n",  
					( (hTime / observedNodes.size() ) * 1000) );
		}
		if (VERBOSE_MODE){
			System.out.println("Final Board: \n " + b.toString());
		}
	}
	public static void setHeuristicType(HEURISTIC newHeuristic){
		currentHeuristic = newHeuristic;
		return;
	}

	public static HEURISTIC getHeuristicType(){
		return currentHeuristic;
	}
	public int calcCostEstimate(Board b){
		long startTime = System.nanoTime();
		int heuristicValue = 0;
		switch (currentHeuristic) {
		case CONSTANT: 
			heuristicValue = constantHeuristic();
			break;
		case INCORRECT_TILES:
			// this is used for BestFS
			heuristicValue = incorrectTilesHeuristic(b);
			break;
		case PATH_PLUS_INCORRECT:
			// this is only used for A* so add the path length to it
			heuristicValue = incorrectTilesHeuristic(b);
			heuristicValue = b.getPathLength() + heuristicValue;
			break;
		case MANHATTAN_DIST: 
			// this is only used for A* so add the path length to it
			heuristicValue = manhattanDistanceHeuristic(b);
			heuristicValue = b.getPathLength() + heuristicValue;
			break;
		case DBL_MANHATTAN:
			// this is only used for A* so add path length to it
			heuristicValue = doubleManhattanHeuristic(b);
			heuristicValue = b.getPathLength() + heuristicValue;
			break;
		default :
			heuristicValue = 0;
		};
		if(VERBOSE_MODE)
			System.out.println("Using " + currentHeuristic + 
					" h(n)=" + heuristicValue);
		long endTime = System.nanoTime();
		// accumulate time spent in the heuristic function
		heuristicTime += (endTime - startTime);
		return heuristicValue;
	}
	
	/**
	 * Function returns the total time spent in the heuristic function so far
	 * @return heuristicTime
	 */
	static public long getHeuristicTime(){
		return heuristicTime;
	}
	static public void setHeuristicTime(long timeVal)	{
		heuristicTime = timeVal;
		return;
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
		int bTile=-1,goalTile=-1;
		
		for (int i=0;i<9;i++){
			bTile = b.board[i];
			goalTile = goal.board[i];
			// if tiles are not equal and goal tile is not 0, count them
			if( (bTile != goalTile) && goalTile != 0){
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
	 * Algorithm builds a location cache in linear time then determines the x and y coordinates for 
	 * each position in linear time so algorithm is O(n) 
	 */

	// build goal location cache in linear time - placed in a function 
	// so it's only performed once
	static protected void buildGoalLocationCache(){
		for (int index = 0; index < 9; index++) {
			// get the tile value at the index (0..8) and set the location 
			// cache at that tile value to the index
			goalLocationCache[goal.board[index]] = index;
		}
		for (int tileValue=0; tileValue < 9; tileValue++){
			goalLocationCacheX[tileValue] =  eightPuzzle.getTileLocationX(goalLocationCache[tileValue]);
			goalLocationCacheY[tileValue] =  eightPuzzle.getTileLocationY(goalLocationCache[tileValue]);	
		}		
	}

	protected int manhattanDistanceHeuristic(Board b) {
		int manDist=0;
		int dx = 0;
		int dy = 0;
		// linear array representing the tiles from 0 to 8
		int[] bLocationCache = {-1,-1,-1,-1,-1,-1,-1,-1,-1};
		
		// build location caches in linear time
		for (int index = 0; index < 9; index++) {
			// get the tile value at the index (0..8) and set the location cache at that tile value to the index
			bLocationCache[b.board[index]] = index;
		}
		// for each tile value, get the location and determine the X & Y coordinate, then the difference
		// ignore the blank tile because it gets moved passively and will be in place when all others are in place
		for (int tileValue=1; tileValue < 9; tileValue++){
			dx = Math.abs(getTileLocationX(bLocationCache[tileValue]) - goalLocationCacheX[tileValue]);
			dy = Math.abs(getTileLocationY(bLocationCache[tileValue]) - goalLocationCacheY[tileValue]);
			if (VERBOSE_MODE){
				System.out.println("(tileValue, board(x,y) , goal(x,y) ) = (" + tileValue 
						+ ", " + getTileLocationX(bLocationCache[tileValue])
						+ ","  + getTileLocationY(bLocationCache[tileValue])
						+ ", " + getTileLocationX(goalLocationCache[tileValue])
						+ ","  + getTileLocationY(goalLocationCache[tileValue])
						+ ")");
			}
			manDist += (dx + dy);
		}
		return manDist;
	}
	// functions to calculate the x and y coordinates of a tile given its index 
	// in the array.  
	// both are static so that the buildGoalLocationCache function can use them 
	// from the static main context
	protected static int getTileLocationX(int tileLocation){
		return tileLocation % 3;
	}
	protected static int getTileLocationY(int tileLocation){
		return (int) Math.floor(tileLocation / 3);
	}
	
	
	/*
	 * Heuristic 3 h3(n) = h2(n) * 2 heuristic function h = (sum of
	 * Manhattan distances) * 2
	 */
	protected int doubleManhattanHeuristic(Board b) {
		return ( manhattanDistanceHeuristic(b)*2 );
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
		System.out.println("\n");
	}
	
	public static void printUsage(){
		String boardHeader = ("Goal:\t\tEasy(1):\tMedium(2):\tHard(3):\tWorst(4):\n" +  
			"1 2 3\t\t1 3 4\t\t2 8 1\t\t2 8 1\t\t5 6 7\n" + 
			"8   4\t\t8 6 2\t\t  4 3\t\t4 6 3\t\t4   8\n"+ 
			"7 6 5\t\t7   5\t\t7 6 5\t\t  7 5\t\t3 2 1\n\n");
		System.out.println("\nUsage:\tjava eightPuzzle [startingBoard] [algorithmChoice]\n\n" + 
				"The eightPuzzle solver may be called with 0, 1, or 2 arguments, additional \n"
				+ "arguments will be ignored.\n" + 
				"This help output may be called with \"java eightPuzzle -h\".\n" + 
				"When called with 0 arguments, the default starting board, easy(1), will be used\n" + 
				"and all algorithms will be analyzed. This can be also modified in source by\n" + 
				"changing the default board assignment.  The starting board may be specified\n" + 
				"as an optional integer argument, startingBoard, to select among the following\n" +
				"boards:\n\n" + boardHeader + 
				"If two integer arguments are used, the second argument, algorithmChoice, will \n" +
				"be used to specify which algorithm should be run individually.\n\n" +
				"The search algorithms and their heuristic variants include:\n" +
				"1: Breadth-first (blind) search\n" +
				"2: Best-first search using the heuristic function h = number of tiles that \n" +
				"   are not in the correct place (not counting the blank).\n" +
				"3: A* search using the heuristic function h = number of tiles that are not \n" +
				"   in the correct place (not counting the blank).\n" +
				"4: A* search using the Manhattan heuristic function h = sum of Manhattan \n" +
				"   distances between all tiles and their correct positions. (Manhattan distance \n" +
				"   is the sum of the x distance and y distance magnitudes.)\n" +
				"5: A* search using the heuristic function h = (sum of Manhattan distances) * 2.\n" +
				"6: Iterative Deepening search, with testing for duplicate states.\n" +
				"\n\n");
	}
}
