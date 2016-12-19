package part2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author David Yagüe Cuevas & Alejandro Rey López
 * @version 1.5
 * @Description A* Algorithm to solve a Pacman like maze with n ghosts (Goals) and 1 Source (Pacman).
 * @Input File where the maze is constructed
 * @Output 3 Files: 1 solved maze with the first heuristic (Manhattan Distance), 1 solved maze with second heuristic (Steve Distance) and 1
 * final file with execution statistics.
 * @since 01/12/2016
 */
public class AStarPacman {

	static Cell [][] grid = null;	//A Cell can be blocked: doing so its value wil be set to null.
	private int height = 0;			//height of the matrix == number of rows
	private int width  = 0;			//width of the matrix == number of columns
	private static ArrayList<Cell> goals = new ArrayList<Cell>();
	private static ArrayList<Cell> path = new ArrayList<Cell>();
	private static ArrayList<Integer> distances = new ArrayList<Integer>();
	private static ArrayList<Integer> order = new ArrayList<Integer>();
	/* Unbounded priority queue based on a priority heap
	 * provides O(log(n)) time for the enqueing and dequeing methods
	 * linear time for the remove and contains methods
	 * and constant time for the retrieval methods.
	 * */
	static PriorityQueue<Cell> open;
	static boolean closed[][]; //Set of nodes already evaluated.
	static int startI, startJ; //Start position: row and column.
	static int endI, endJ; //End position: row and column.
	static int costSoFar=0; //Total final cost of the path.
	static int n_Expanded = 0; //Total number of nodes expanded.
	static long execTime = 0; //Total execution time of the search.
	static int n_ghosts = 0;

	/**
	 * @Description: AstartPacman constructor to solve the problem.
	 * @Param: fileName: Path of the file where the maze layout is, heuristic: Name of the heuristic to use.
	 * @Throws: IOException.
	 * @Calls: readFileAndSetDimensions(String), fillMazeAndPrepareSearch(String) and defineHeuristics(String).
	 */
	public AStarPacman(String fileName,String heuristic) throws IOException{
		readFileAndSetDimensions(fileName); //Read the file to set dimensions.
		fillMazeAndPrepareSearch(fileName); //Prepare the search.
		defineHeuristics(heuristic); //Define the heuristic to use.
	}

	/**
	 * @Description: Defines the heuristic within the maze passed as an argument.
	 * @Param: heuristic: Name of the heuristic to use.
	 */
	public void defineHeuristics(String heuristic){
		int count=0;
		int index = 0;
		if(heuristic.equalsIgnoreCase("Manhattan")){ //If the heuristic is Manhattan distance.
			//Loop the maze
			for(int i=0;i<height;++i){
				for(int j=0;j<width;++j){
					if(grid[i][j]!=null && grid[i][j].value=='G'){ //If the cell is a ghost its heuristic cost is set to 0.
						grid[i][j].heuristicCost=0;
					}else if(grid[i][j]!=null){ //Otherwise

						while(count<goals.size()){ //For each of the ghosts we have.
							Cell goal = goals.get(count); //Get the cell.
							int distance = Math.abs(i-goal.j)+Math.abs(j-goal.i); //Calculate the Manhattan distance.
							distances.add(distance); //Store the distance.
							count++;
						}
						int min = 0;
						//Loop to get the minimum distance in the set of distances we already filled.
						for (int a = 0; a < distances.size(); a++) {
							Integer f = distances.get(a);
							if (Integer.compare(f, min) < 0) {
								min = f;
								index = a;
							}
						}
						//update the heuristic cost of the cell. 
						grid[i][j].heuristicCost=distances.get(index);
						//Clear the set of distances so we can continue the loop.
						distances.clear();
						count=0;
					}

				}
			}

		}else if(heuristic.equalsIgnoreCase("Steven")){ //If the heuristic is
			for(int i=0;i<height;++i){
				for(int j=0;j<width;++j){
					if(grid[i][j]!=null && grid[i][j].value=='G'){
						grid[i][j].heuristicCost=0;
					}else if(grid[i][j]!=null){
						int h = grid[i][j].heuristicCost;
						while(count<goals.size()){
							Cell goal = goals.get(count);
							int dx1 = i-goal.i;
							int dy1 = j-goal.j;
							int dx2 = startI-goal.i;
							int dy2 = startJ-goal.j;
							int cross = Math.abs(dx1*dy2 - dx2*dy1);
							h += cross;
							distances.add(h);
							count++;
						}
						int min = 0;
						for (int a = 0; a < distances.size(); a++) {
							Integer f = distances.get(a);
							if (Integer.compare(f, min) < 0) {
								min = f;
								index = a;
							}
						}
						grid[i][j].heuristicCost=distances.get(index);
						distances.clear();
						count=0;
					}

				}

			}
		}
	}

	/**
	 * @Description: Read the maze layout to get the height and width.
	 * @Param: fileName: Path of the file where the maze layout is.
	 * @Throws: IOException.
	 */
	public void readFileAndSetDimensions(String filename) throws IOException {//Fills the maze of chars used to extract information on empty cells

		try{
			//Reader.
			BufferedReader read = new BufferedReader(new FileReader(filename));

			//Auxiliary variables to read the file.
			String readline;
			int rows = 0;
			char[] ch = null ;

			//Loop to read each line
			while((readline = read.readLine()) != null){
				//Read a line and store it in a character array.
				ch = readline.toCharArray();
				rows++;
			}

			//Set dimensions.
			this.width=ch.length;
			this.height=rows;

			//Close the reader.
			read.close();

		}catch(IOException e){
			System.err.println("Path of the file not correct");
		}
	}

	/**
	 * @Description: Fill the grid and prepare all for the search.
	 * @Param: fileName: Path of the file where the maze layout is.
	 * @Throws: IOException.
	 */
	public void fillMazeAndPrepareSearch (String filename) throws IOException{

		try{
			//Reader
			BufferedReader read2 = new BufferedReader(new FileReader(filename));

			//Auxiliary variables to read the file.
			String readline2;
			int num = 0;

			//Create the grid to fill and the closed set of cells to support the search.
			grid = new Cell[height][width];
			closed = new boolean[height][width];

			//Loop to read each line of the file.
			while((readline2 = read2.readLine()) != null){
				//Sore the line in the array
				char[] ch2 = readline2.toCharArray();
				//Loop to fill the grid depending on the value stored in the file.
				for(int i=0;i<ch2.length;++i){
					//Create the cell.
					grid[num][i] = new Cell(num, i);
					//Set the value attribute of the cell.
					if(ch2[i]=='P'){
						setStartCell(num, i);
						//If it is the start cell the final cost is always 0.
						grid[num][i].finalCost=0;
						grid[num][i].setAsSource();
					}else if (ch2[i]=='G'){
						goals.add(grid[num][i]);
						//Update the number of ghost we have in the problem.
						n_ghosts++;
						grid[num][i].setAsGoal();
					}else if(ch2[i]=='%'){
						setBlocked(num, i);
					}else if(ch2[i]=='O'){
						grid[num][i].setAsFood();
						//If it is a food cell, its cost attribute must be changed to 2 instead of 4.
						grid[num][i].setCost();
					}
				}
				num++;
			}
			//Close the reader.
			read2.close();

		}catch(IOException e){
			//System.err.println("Error"); No need cause it is already printed.
		}
	}


	/**
	 * @Description: Method that performs the search.
	 * @Calls: Cell class setters, AStar(), printMaze() and reconstructPaths().
	 */
	public void doSearch(){
		//Control loop boolean.
		boolean seguir = true;
		//Auxiliary counters.
		int ii=0;
		int coint=0;
		//Set the fist goal.
		endI = goals.get(0).i;
		endJ = goals.get(0).j;

		//Loop until Pacman has reached all ghosts.
		while(seguir){
			//If there is more than one ghost part of the maze must be reseted (Parents to begin the new search).
			if(n_ghosts>=2){	
				for(int i=0;i<height;++i){
					for(int j=0;j<width;++j){
						if(grid[i][j]!=null){
							grid[i][j].parent=null;
						}
					}
				}
			}

			//Reset closed and open set.
			closed = new boolean[height][width];
			open = new PriorityQueue<>((Object o1, Object o2) -> {
				Cell c1 = (Cell)o1;
				Cell c2 = (Cell)o2;

				return c1.finalCost<c2.finalCost?-1:
					c1.finalCost>c2.finalCost?1:0;
			});

			//If it is not the fist ghost set the start cell with the end coordinates of the last iteration.
			if(coint!=0){
				setStartCell(endI,endJ);
			}

			//Update the counter
			coint++;

			//if there is more than one goal set the end coordinates to the next goal of the iteration.
			if(ii<n_ghosts){
				setEndCell(goals.get(ii).i,goals.get(ii).j);
			}

			ii++;

			AStar(); 


			if(closed[endI][endJ]){
				//Trace back the path 
				//System.out.println("Path: ");
				Cell current = grid[endI][endJ];
				path.add(current);
				//System.out.print(current);
				while(current.parent!=null){					

					path.add(current.parent);

					current = current.parent;
				} 
				order.add(path.size());
				costSoFar=grid[endI][endJ].finalCost;
			}else System.out.println("No possible path");

			if(n_ghosts==coint){
				seguir=false;
			}

			for(int i=0;i<height;++i){
				for(int j=0;j<width;++j){
					if(closed[i][j]){
						n_Expanded++;
					}
				}
			}

		}
	}

	/**
	 * @Description: reconstruct the path A* found.
	 */
	public void reconstructPaths(){
		System.out.println("\nPath:\n");
		for(int i=0;i<height;++i){
			for(int j=0;j<width;++j){
				if(grid[i][j]==null){
					System.out.print(" %");
				}else if (grid[i][j].value=='G' && path.contains(grid[i][j])){
					System.out.print(" " + goals.indexOf(grid[i][j]));
				}else if (grid[i][j].value=='P'){
					System.out.print(" P");
				}else if(path.contains(grid[i][j])){
					System.out.print(" ·");
				}else{
					System.out.print("  ");
				}
			}
			System.out.println();
		}

		int w=0;
		int stop=0;
		System.out.println();
		while(w<n_ghosts){
			int index = order.get(w);

			for(int h=index-1;h>=stop;--h){
				if(w>0 && h!=stop) System.out.print(path.get(h-1)+" --> ");
				else if (w==0 && h>=stop)System.out.print(path.get(h)+" --> ");
			}
			w++;
			stop = index;
		}
		System.out.print("End\n");
		System.out.println("\nFinal cost:" + costSoFar);
	}

	/**
	 * @Description: prints the maze layout.
	 */
	public void printMaze(){
		System.out.println("\nMaze layout:\n");
		for(int i=0;i<height;++i){
			for(int j=0;j<width;++j){
				if(grid[i][j]==null){
					System.out.print(" %");
				}else if (grid[i][j].value=='G'){
					System.out.print(" G");
				}else if (grid[i][j].value=='P'){
					System.out.print(" P");
				}else if(grid[i][j].value=='O'){
					System.out.print(" O");
				}else{
					System.out.print("  ");
				}
			}
			System.out.println();
		}
	}

	/**
	 * @Description: Cell class, each position of the maze is a cell.
	 */
	static class Cell {  
		int heuristicCost = 0; //This is the heuristic cost of the cell.
		int finalCost = 0; //This is the evaluation function of the cell--> g + h.
		int i, j; //Variables to define the positions --> row(i),column(j).
		char value = ' '; //Specific value of the cell --> by default it is assumed to be blank space.
		int costMove=4; //Cost of the movement to cell --> since it is assumed a black space, the cost of movement must be 4.	
		Cell parent; //Parent cell of the cell --> Use to reconstruct the path A* found.

		//Constructor - A cell is defined by its position within the maze.
		Cell(int i, int j){
			this.i = i;
			this.j = j;
		}

		//Useful to print the position of the cell.
		@Override
		public String toString(){
			return "["+this.i+", "+this.j+"]";
		}

		//Set the cell as goal --> G.
		public void setAsGoal(){
			this.value='G';
		}

		//Set the cell as source --> P.
		public void setAsSource(){
			this.value='P';
		}

		//Set the cell as food --> O.
		public void setAsFood(){
			this.value='O';
		}

		/*Set the coast of the cell. By default all cost are initialized to 4 and when the maze is read and it is found 
		a position with food, the cost of that position is update to 2.*/
		public void setCost(){
			this.costMove=2;
		}

		//Define the way two Cell objects are the same.
		@Override
		public boolean equals(Object o){
			boolean same = false;
			if(o!=null && o instanceof Cell){
				boolean sameI = this.i == ((Cell) o).i;
				boolean sameJ = this.j == ((Cell) o).j;
				same = sameI && sameJ;
			}
			return same;
		}

	}

	//Block a Cell --> the value of the Cell is set to null.
	public static void setBlocked(int i, int j){
		grid[i][j] = null;
	}


	//Set a start Cell.
	public static void setStartCell(int i, int j){
		startI = i;
		startJ = j;

	}


	//Set a end Cell.
	public static void setEndCell(int i, int j){
		endI = i;
		endJ = j; 
	}


	//Method to check values and update costs if necessary.
	static void checkAndUpdateCost(Cell current, Cell t, int cost){
		if(t == null || closed[t.i][t.j])return; //If the cell is blocked or it is already closed --> nothing to update.
		int t_final_cost = t.heuristicCost+cost; //Update the Cell cost.

		boolean inOpen = open.contains(t); //Check if the Cell is already in the open list.
		if(!inOpen || t_final_cost<t.finalCost){ //If it is not || the estimation is upperbounded by the optimal cost.
			t.finalCost = t_final_cost; //Update its final cost
			t.parent = current; //Set the parent Cell
			if(!inOpen)open.add(t); //Add it to the open list
		}
	}


	//Method implementing A* algorithm.
	public static void AStar(){ 

		//add the start location to open list. This location correspond to the cell where the pacman is located within the maze.
		open.add(grid[startI][startJ]);

		//Current cell.
		Cell current;

		//Endless loop
		while(!open.isEmpty()){ 
			//Retrieves and removes the head of the queue (returns null if it is empty).
			current = open.poll();
			//If it is null --> blocked cell, nothing to do here.
			if(current==null)break;
			//Close current Cell.
			closed[current.i][current.j]=true; 

			//If it is the goal --> End
			if(current.equals(grid[endI][endJ])){
				return; 
			}

			//Check neighbors
			Cell t;int cost=0;  
			if(current.i-1>=0){
				t = grid[current.i-1][current.j];
				if(t!=null)cost=t.costMove;
				checkAndUpdateCost(current, t, current.finalCost+cost); 

				//Allow diagonal movement
				if(current.j-1>=0){                      
					t = grid[current.i-1][current.j-1];
					if(t!=null)cost=t.costMove;
					checkAndUpdateCost(current, t, current.finalCost+cost); 
				}

				if(current.j+1<grid[0].length){
					t = grid[current.i-1][current.j+1];
					if(t!=null)cost=t.costMove;
					checkAndUpdateCost(current, t, current.finalCost+cost); 
				}
			} 

			if(current.j-1>=0){
				t = grid[current.i][current.j-1];
				if(t!=null)cost=t.costMove;
				checkAndUpdateCost(current, t, current.finalCost+cost); 
			}

			if(current.j+1<grid[0].length){
				t = grid[current.i][current.j+1];
				if(t!=null)cost=t.costMove;
				checkAndUpdateCost(current, t, current.finalCost+cost); 
			}

			if(current.i+1<grid.length){
				t = grid[current.i+1][current.j];
				if(t!=null)cost=t.costMove;
				checkAndUpdateCost(current, t, current.finalCost+cost); 

				//Allow diagonal movement
				if(current.j-1>=0){
					t = grid[current.i+1][current.j-1];
					if(t!=null)cost=t.costMove;
					checkAndUpdateCost(current, t, current.finalCost+cost); 
				}

				if(current.j+1<grid[0].length){
					t = grid[current.i+1][current.j+1];
					if(t!=null)cost=t.costMove;
					checkAndUpdateCost(current, t, current.finalCost+cost); 
				}  
			} 
		}
	}


	public void writeToFileWithCorrectFormat(String completepath){

		File actualFile = new File (completepath + ".output");
		File statisticFile = new File (completepath + ".statistics");

		try{
			PrintWriter writer = new PrintWriter(statisticFile);
			writer.print("Statistics: \n\n");
			writer.print("Execution time: " + execTime + "milliseconds\n");
			writer.print("Lenth of the path: " + path.size() + "\n");
			writer.print("Number of nodes expanded: " + n_Expanded + "\n");
			writer.print("Total cost: "+ costSoFar+ "\n");
			writer.print("Total final path:\n" );

			int w=0;
			int stop=0;
			System.out.println();
			while(w<n_ghosts){
				int index = order.get(w);
				for(int h=index-1;h>=stop;--h){
					if(w>0 && h!=stop) writer.print(path.get(h-1)+" --> ");
					else if (w==0 && h>=stop)writer.print(path.get(h)+" --> ");
				}
				w++;
				stop = index;
			}
			writer.print("End\n");
			writer.close();

			writer = new PrintWriter(actualFile);

			writer.println("Maze layout:\n");
			for(int i=0;i<grid.length;++i){
				for(int j=0;j<grid[0].length;++j){
					if(grid[i][j]==null){
						writer.print(" %");
					}else if (grid[i][j].value=='G'){
						writer.print(" G");
					}else if (grid[i][j].value=='P'){
						writer.print(" P");
					}else if(grid[i][j].value=='O'){
						writer.print(" O");
					}else{
						writer.print("  ");
					}
				}
				writer.println(" | ");
			}
			for(int a=0;a<grid[0].length;++a){
				writer.print(" -");
			}
			
			int index=0;
			int index2=0;
			for(int control = 0;control<goals.size();++control){
				writer.println("\nPath to ghost: " + control + "\n");
				index = order.get(control);
				for(int i=0;i<grid.length;++i){
					for(int j=0;j<grid[0].length;++j){
						if(grid[i][j]==null){
							writer.print(" %");
						}else if (grid[i][j].value=='G'){
							writer.print(" G");
						}else if (grid[i][j].value=='P'){
							writer.print(" P");
						}else if(path.subList(index2,index ).contains(grid[i][j])){
							writer.print(" X");
						}else{
							writer.print("  ");
						}
					}
					writer.println();
				}
				
				index2=index;
			}

			writer.close();
		} catch (IOException e) {
			// do something
		}

	}

	public static void main(String[] args) throws Exception{   

		try{
			
			AStarPacman a = new AStarPacman(args[0],args[1]);
			long start = System.currentTimeMillis();
			a.doSearch();
			long end = System.currentTimeMillis();
			execTime = end-start;
			a.writeToFileWithCorrectFormat(args[0]);

		}catch(Exception e){
			System.err.println("Something went wrong. Try again.");
		}
	}
}