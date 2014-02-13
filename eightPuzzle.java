import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;
//import java.util.Queue;
//import java.util.LinkedList;

public class eightPuzzle
{
	static Board goal;
	
	public static void main(String[] args)
	{
		goal = new Board(new int[] {1,2,3,8,0,4,7,6,5});
		
		//Board b = new Board(new int[] {1,3,4,8,6,2,7,0,5});//easy
		Board b = new Board(new int[] {2,8,1,0,4,3,7,6,5});//medium
		//Board b = new Board(new int[] {2,8,1,4,6,3,0,7,5});//hard
		//Board b = new Board(new int[] {5,6,7,4,0,8,3,2,1});//worst
		 
		long startTime, endTime, duration;

		
		// DFS
		eightPuzzle solver = new eightPuzzle();
		System.out.println("===DFS===");
		startTime = System.nanoTime();
		solver.dfs(b);
		endTime = System.nanoTime();		
		duration = endTime - startTime;
		System.out.format("DFS duration = %3.5f s\n", 
				(double) duration /(double)1000000000);
		
		// BFS
		System.out.println();
		solver = new eightPuzzle();
		System.out.println("===BFS===");
		startTime = System.nanoTime();
		solver.bfs(b);
		endTime = System.nanoTime();		
		duration = endTime - startTime;
		System.out.format("BFS duration = %3.5f s\n", 
				(double) duration /(double)1000000000);
		
		
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
	/*	1. Breadth-first (blind) search
	 *	This method implements blind breadth-first search on the 8 puzzle,
	 *	keeping track of visited nodes to avoid infinite search. This 
	 *	method also outputs the first 15 nodes visited.
	 */
	public void bfs(Board b)
	{
		int count = 0;//used to output the first 15 nodes visited
		String first15states = "";
		HashSet<String> observedNodes = new HashSet<String>();//keeps track of visited states
		Vector<Board> nodeQueue = new Vector<Board>(); // hold future states to explore in a FIFO queue
		
		while(!b.equals(goal))
		{
			observedNodes.add(b.toString());
			// add successor nodes to the queue
			nodeQueue.addAll(b.getSuccessors());
			b = nodeQueue.remove(0);
			while(observedNodes.contains(b.toString()))
			{
				b = nodeQueue.remove(0);
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
	
	/* 2. Best-first search using the heuristic function h = number of tiles 
	 * that are not in the correct place (not counting the blank).
	 * 
	 * TODO: implemented as a priority queue where the next node chosen is the node 
	 * with the lowest h1(n) where h1(n) is the number of tiles that are not in 
	 * the correct place (not counting the blank*/
	
	
	
	/* 3. A* search using the heuristic function h = number of tiles that are not 
	 * in the correct place (not counting the blank).
	 * 
	 * TODO A* is implemented as a depth first search where the node to 
	 */

	/* 4. A* search using the Manhattan heuristic function h = sum of Manhattan 
	 * distances between all tiles and their correct positions. (Manhattan 
	 * distance is the sum of the x distance and y distance magnitudes.)
	 * 
	 * TODO: A* is implemented by treating the frontier as a priority queue 
	 * ordered by f(p)=c(p)+h2(n)
	 */
	
	/* 5. A* search using the heuristic function h = (sum of Manhattan distances) * 2.
	 * 
	 * TODO: A* is implemented by treating the frontier as a priority queue 
	 * ordered by f(p)=c(p)+h3(n)
	 */
	
	/* 6. Iterative Deepening search, with testing for duplicate states.
	 * "Consider making a breadth first search into an iterative deepening search.  
	 * This is carried out by having a depth-first searcher, which searches only 
	 * to a limited depth.  It can first do a depth first search to depth 1 by 
	 * building paths of length 1 in a depth-first manner.  Then d=2 and so on
	 */
	
	/* Heuristic 1
	 * TODO h1(n) = number of tiles that are not in the correct place
	 */
	
	/* Heuristic 2
	 * TODO h2(n) = the Manhattan heuristic function h = sum of Manhattan distances
	 * between all tiles and their correct positions. (Manhattan distance is the 
	 * sum of the x distance and y distance magnitudes.)
	 */
	
	/* Heuristic 3
	 * TODO: h3(n) = h2(n) * 2
	 * heuristic function h = (sum of Manhattan distances) * 2
	 */

	/* Note that there should only be two basic search functions (depth-first and breadth-first), of which
		the searches listed above are parameter variations. If any of these searches is too long to be
		completed successfully or runs out of memory, so state (just be sure you are right!).
	*/
	
	
	// end additional search methods
	
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