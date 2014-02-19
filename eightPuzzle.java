import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;

public class eightPuzzle
{
	static boolean DEBUG_MODE = false;
	static boolean VERBOSE_MODE = false;
	static Board goal;
	
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