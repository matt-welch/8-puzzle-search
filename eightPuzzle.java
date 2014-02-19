import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;
import java.util.PriorityQueue;
//import java.util.Queue;
//import java.util.LinkedList;

public class eightPuzzle {
	static boolean DEBUG_MODE = false;
	static boolean VERBOSE_MODE = false;
	static Board goal;


	public static void main(String[] args) {
		goal = new Board(new int[] { 1, 2, 3, 8, 0, 4, 7, 6, 5 });
		long startTime, endTime, duration;

//		int[] array = {1,3,4,8,6,2,7,0,5};//easy (d=5)
//		int[] array = {1,3,4,8,0,5,7,2,6};//less easy (d=6)
//		int[] array = {2,8,1,0,4,3,7,6,5};// medium
		int[] array = {2,8,1,4,6,3,0,7,5};//hard
//		int[] array = {5,6,7,4,0,8,3,2,1};//worst
		
		Board b = new Board(array);
//		b = Board.randomBoard();
		b.setGoal(goal);
		eightPuzzle solver = null;
		
		// DFS
		solver = new eightPuzzle();
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

		// following search methods need a goal and heuristics
		
		//BestFS
		// use the incorrect tiles heuristic
		b = new Board(array);//easy (d=5)
		b.setHeuristicType(Board.HEURISTIC.INCORRECT_TILES);
		solver = new eightPuzzle();
		System.out.println("===BestFS===");
		System.out.println("Current Heuristic = " + b.getHeuristicType());
		startTime = System.nanoTime();
		solver.bestfs(b);
		endTime = System.nanoTime();
		duration = endTime - startTime;
		System.out.format("BestFS duration = %3.5f s\n", (double) duration
				/ (double) 1000000000);

		/*3. A* search using the heuristic function h = number of tiles that are
		 * not in the correct place (not counting the blank).*/
		//A* search, h(n)=PATH+PLUS_INCORRECT_TILES
		b = new Board(array);//easy (d=5)
		b.setGoal(goal);
		b.setHeuristicType(Board.HEURISTIC.PATH_PLUS_INCORRECT);
		solver = new eightPuzzle();
		System.out.println("===A* (Incorrect Tiles)===");
		System.out.println("Current Heuristic = " + b.getHeuristicType());
		startTime = System.nanoTime();
		solver.aStarSearch(b);
		endTime = System.nanoTime();
		duration = endTime - startTime;
		System.out.format("A* (IncorrectTiles) duration = %3.5f s\n", (double) duration
				/ (double) 1000000000);
		
		/*
		 * 4. A* search using the Manhattan heuristic function h = sum of Manhattan
		 * distances between all tiles and their correct positions. (Manhattan
		 * distance is the sum of the x distance and y distance magnitudes.)
		 */
		//A* search, h(n)=MANHATTAN_DIST+PATH
		b = new Board(array);//easy (d=5)
		b.setGoal(goal);
		solver = new eightPuzzle();
		System.out.println("===A* (Manhattan Dist)===");
		b.setHeuristicType(Board.HEURISTIC.MANHATTAN_DIST);
		System.out.println("Current Heuristic = " + b.getHeuristicType());
		startTime = System.nanoTime();
		solver.aStarSearch(b);
		endTime = System.nanoTime();
		duration = endTime - startTime;
		System.out.format("A* (Manhattan Dist) duration = %3.5f s\n", (double) duration
				/ (double) 1000000000);

		/*
		 * 5. A* search using the heuristic function 
		 * h = (sum of Manhattan distances) * 2.
		 */
		//A* search, h(n)=DBL_MANHATTAN_DIST
		b = new Board(array);
		b.setGoal(goal);
		solver = new eightPuzzle();
		System.out.println("===A* (Double Manhattan Dist)===");
		b.setHeuristicType(Board.HEURISTIC.DBL_MANHATTAN);
		System.out.println("Current Heuristic = " + b.getHeuristicType());
		startTime = System.nanoTime();
		solver.aStarSearch(b);
		endTime = System.nanoTime();
		duration = endTime - startTime;
		System.out.format("A* (Double Manhattan Dist) duration = %3.5f s\n", (double) duration
				/ (double) 1000000000);
	
		/*
		 * 6. Iterative Deepening search, with testing for duplicate states.
		 * "Consider making a breadth first search into an iterative deepening
		 * search. This is carried out by having a depth-first searcher, which
		 * searches only to a limited depth. It can first do a depth first search to
		 * depth 1 by building paths of length 1 in a depth-first manner. Then d=2
		 * and so on
		 */
		b = new Board(array);
		b.setGoal(goal);
		solver = new eightPuzzle();
		System.out.println("===Iterative Deepening===");
		b.setHeuristicType(Board.HEURISTIC.NONE);
		startTime = System.nanoTime();
		solver.iterativeDeepening(b);
		endTime = System.nanoTime();
		duration = endTime - startTime;
		System.out.format("Iterative Deepening duration = %3.5f s\n", (double) duration
				/ (double) 1000000000);
	}

	/*
	 * This method implements blind depth-first search on the 8 puzzle, keeping
	 * track of visited nodes to avoid infinite search. This method also outputs
	 * the first 15 nodes visited.
	 */
	public void dfs(Board b) {
		int count = 0;// used to output the first 15 nodes visited
		String first15states = "";
		// keeps track of visited states
		HashSet<String> observedNodes = new HashSet<String>();
		// holds future states to explore
		Stack<Board> stack = new Stack<Board>();
		if (DEBUG_MODE) 
			System.out.println("h(n) (before) = " + b.calcHeuristic(goal));
		while (!b.equals(goal)) {
			observedNodes.add(b.toString());
			stack.addAll(b.getSuccessors());
			if(stack.isEmpty()){
				// this shouldn't happen
				break;
			}
			b = stack.pop();
			while (observedNodes.contains(b.toString()) && !stack.isEmpty()) {
				b = stack.pop();
			}
			if (count < 15) {
				first15states += b + "\n";
				count++;
			}
		}
		System.out.println(observedNodes.size() + " nodes examined.");
		if (observedNodes.size() < 10000){
			printHistory(b);
			showCostFunction(b);
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
		String first15states = "";
		// keeps track of visited states
		HashSet<String> observedNodes = new HashSet<String>();
		// vector acting as a FIFO queue to hold future states to explore 
		// 	since vector.remove(0): Removes the element at the specified position 
		// 	in this Vector. Shifts any subsequent elements to the left (subtracts 
		//	one from their indices). Returns the element that was removed from the Vector. 
		Vector<Board> nodeQueue = new Vector<Board>(); 
		if (DEBUG_MODE){
			b.setHeuristicType(Board.HEURISTIC.INCORRECT_TILES);
			System.out.println("h(n) (before) = " + b.calcHeuristic(goal));			
		}
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
		String first15states = "";
		// keeps track of visited states
		HashSet<String> observedNodes = new HashSet<String>();
		// hold future states to explore in a priority queue
		PriorityQueue<Board> boardPriorityQueue = new PriorityQueue<Board>(100);

		// set the cost estimate for b based on the current heuristic
		int h = b.calcHeuristic(goal);
		b.setCostEstimate(h);

		System.out.println("h(n) (before) " + h);
		System.out.println(b.toString());
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
		showCostFunction(b);
	}

	/**
	 * A* is implemented by treating the frontier as a priority queue
	 * ordered by f(p)=c(p)+h2(n)
	 * 
	 * @param b
	 */
	public void aStarSearch(Board b) {
		int count = 0;// used to output the first 15 nodes visited
		String first15states = "";
		// keeps track of visited states
		HashSet<String> observedNodes = new HashSet<String>();
		// hold future states to explore in a priority queue
		PriorityQueue<Board> boardPriorityQueue = new PriorityQueue<Board>(100);
		// set the cost estimate for b based on the current heuristic
		int h = b.calcHeuristic(goal);
		b.setCostEstimate(h);

		System.out.println("Using heuristic: " + b.getHeuristicType());
		System.out.println("h(n) (before) " + h);
		System.out.println(b.toString());
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
		showCostFunction(b);
	}
	/*
	 * This method implements Iterative Deepening search on the 8 puzzle, keeping
	 * track of visited nodes to avoid infinite search. Iterative DDeepening is a 
	 * modification of depth first search where the DFS is repeated at increasing 
	 * depth bounds, beginning with the root each time.  
	 * This method also outputs the first 15 nodes visited.
	 */
	public void iterativeDeepening(Board root) {
		int depthBound = -1;
		final int maxDepth = 50;
		boolean goalFound = false;
		// copy root so that b can be reset when it returns from the bounded DFS
		Board b = root;
		if (VERBOSE_MODE){
			System.out.println("Starting State: \n" + root);
			System.out.println("Goal State: \n" + goal);
		}
		while (!goalFound && depthBound < maxDepth){
			depthBound++;
			if (VERBOSE_MODE)
				System.out.println("Searching at depthBound = " + depthBound);
			goalFound = boundedDFS(b, depthBound);
//			if (VERBOSE_MODE)
//				System.out.println("Current state: \n" + b);
//			// reset the search history - Unnecessary??
//			b = root;
		}
		if (goalFound)
			System.out.println("Iterative deepening finished successfully in " + depthBound + " rounds.");
		else
			System.out.println("Iterative deepening terminated in " + depthBound + " rounds.");
	}
	
	public boolean boundedDFS(Board b, int depthBound){
		boolean goalFound = false;
		int pathLength = 0;
		int count = 0;// used to output the first 15 nodes visited
		String first15states = "";
		// keeps track of visited states
		HashSet<String> observedNodes = new HashSet<String>();
		// holds future states to explore
		Stack<Board> stack = new Stack<Board>();

		while (!b.equals(goal) && goalFound == false) {
			observedNodes.add(b.toString());
			if(pathLength < depthBound){
				// don't add any more successors if the depth bound has been reached
				stack.addAll(b.getSuccessors());
			}
			if(stack.isEmpty()){
				// will cause bounded DFS to terminate unnaturally (empty stack)
				goalFound=false;
				break;
			}
			b = stack.pop();

			while (observedNodes.contains(b.toString()) && !stack.isEmpty()) {
				b = stack.pop();
				if (b.equals(goal)){
					goalFound = true;
					System.out.println("Goal found in previously examined node");
					break;
				}				
			}
			if (count < 15) {
				first15states += b + "\n";
				count++;
			}

			pathLength = b.getPathLength();
		}
	
		if (goalFound == false){
			// terminate "unnaturally"
			if (DEBUG_MODE)
				System.out.println("boundedDFS terminating unnaturally at depth =" + depthBound);
		}else{
			// terminate naturally
			System.out.println(observedNodes.size() + " nodes examined.");
			if (observedNodes.size() < 10000)
				printHistory(b);
			else
				System.out.println("Not printing history--leads to stack overflow");
			System.out.println(first15states);
			showCostFunction(b);
		}
		return goalFound;
	}
	

	/*
	 * Note that there should only be two basic search functions (depth-first
	 * and breadth-first), of which the searches listed above are parameter
	 * variations. If any of these searches is too long to be completed
	 * successfully or runs out of memory, so state (just be sure you are
	 * right!).
	 */

	// end additional search methods
	
	/**
	 * function prints path and heuristic costs for the solution
	 * @param b
	 */
	private void showCostFunction(Board b){
		System.out.println("f(p) = c(p) + h(p) = " +
				 b.getPathLength() + " + " + b.incorrectTilesHeuristic(goal));
		System.out.format("Total heuristic time = %3.8f s\n", (double) b.getHeuristicTime()
				/ (double) 1000000000);
		if (VERBOSE_MODE){
			System.out.println("Final Board: \n " + b.toString());
		}
	}

	/*
	 * This method prints the move history of the beginning state to the current
	 * state where each move is understood as moving the blank so that 1 2 3 1 2
	 * 3 4 5 6 to 4 5 6 is understood as "Left" rather than "Right" 7 8 0 7 0 8
	 */
	public void printHistory(Board b) {
		Vector<Board> boards = b.getPathFromStartNode();
		System.out.println();
		for (int i = 0; i < boards.size() - 1; i++) {
			if (boards.get(i).getSuccessor(Board.DOWN) != null)
				if (boards.get(i).getSuccessor(Board.DOWN)
						.equals(boards.get(i + 1)))
					System.out.print("Down ");
			if (boards.get(i).getSuccessor(Board.LEFT) != null)
				if (boards.get(i).getSuccessor(Board.LEFT)
						.equals(boards.get(i + 1)))
					System.out.print("Left ");
			if (boards.get(i).getSuccessor(Board.RIGHT) != null)
				if (boards.get(i).getSuccessor(Board.RIGHT)
						.equals(boards.get(i + 1)))
					System.out.print("Right ");
			if (boards.get(i).getSuccessor(Board.UP) != null)
				if (boards.get(i).getSuccessor(Board.UP)
						.equals(boards.get(i + 1)))
					System.out.print("Up ");
		}
		System.out.println();
	}
}