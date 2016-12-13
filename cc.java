package parser;
import java.io.*;
import java.util.ArrayList;

import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.jasat.utils.structures.IntVec;
import org.jacop.satwrapper.SatWrapper;
import org.jacop.search.*;
import org.jacop.core.BooleanVar;
import org.jacop.core.Store;
import org.jacop.jasat.utils.structures.IntVec;
import org.jacop.satwrapper.SatWrapper;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMin;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleSelect;
import org.jacop.search.SmallestDomain;

public class cc {
	static char[][] maze = null;
	static int width =  4;
	static int height = 4;
	static int n =2;

	//Function to read the file
	public void readFile(String filename) throws IOException {
		//Create a buffer to read the file
		BufferedReader read = new BufferedReader(new FileReader(filename));
		//Line to read
		String readline;
		int num = 0;
		//Maze generated
		maze = new char[width][height];
		//Reading lines
		while((readline = read.readLine()) != null){
			char[] ch = readline.toCharArray();
			for(int i = 0;i < ch.length;i++){
				//Store the character read
				maze[i][num] = ch[i];
			}
			num++;
		}
		//close buffer
		read.close();
	}

	//Function to print the result of reading the file
	public void print() {
		if (maze != null) {
			for (int row = 0; row < width; row++) {
				for (int col = 0; col < height; col++) {
					System.out.print(maze[row][col]);
				}
				System.out.print('\n');
			}

		}
	}

	//Constructor --> File reader
	public cc(String filename) {
		try {
			readFile(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Function to add clause with two literals
	public static void addClause(SatWrapper satWrapper, int literal1, int literal2){
		IntVec clause = new IntVec(satWrapper.pool);
		clause.add(literal1);
		clause.add(literal2);
		satWrapper.addModelClause(clause.toArray());
	}


	//Implementation of the constraint: "Only one Pacman in the maze"
	public static void only1Pacman(ArrayList<int[]> literals, SatWrapper satWrapper){
		//Parse all the matrix into an array
		int newArray[] = literals.get(0); //Pacman always 1º position

		//Iterate through the array already created in previous steps and add clause for each pair of literals:
		// A(-Pij V -Pkl) where k!=i,j!=l, i,k-->Rows,j,l-->Columns
		for (int i = 0; i < newArray.length; i++) {
			for (int k = i + 1; k < newArray.length; k++) {

				addClause(satWrapper,-(newArray[i]),-(newArray[k])); 

			}
		}
	}

	public static ArrayList<int[]> allLiterals(ArrayList<int[][]> literals){
		//Array list to support dynamic number of ghost
		ArrayList<int[]> aux = new ArrayList<int[]>();
		int c=literals.size();
		//Iterate through the array list and parse each matrix into an array
		for(int b=0;b<c;++b){ //Pacman always fixed in the first position
			int [][] au = literals.get(b);
			int newArray[] =  new int[au.length*au[0].length];

			for(int i = 0; i < au.length; i++) {
				int[] row = au[i];
				for(int j = 0; j < row.length; j++) {
					int number = au[i][j];
					newArray[i*row.length+j] = number;
				}
			}
			aux.add(newArray);
		}
		return aux;
	}

	//Este metodo creo que sobra -- -- Implementation of the constraint: "Each of the ghost must be placed in the maze"
	public static void eachGhostInMaze(ArrayList<int[]> literals,SatWrapper satWrapper){

		//Iterate the auxiliar array list and add a clause for each pair of literals for each ghost.
		//A(V Gijg) where j-->Colums g-->(nGhosts),i-->Rows
		for(int ii=1;ii<literals.size();++ii){
			int[] m = literals.get(ii);

			for (int q = 0; q < m.length; q++) {
				for (int k = q + 1; k < m.length; k++) {
					addClause(satWrapper,m[q],m[k]); 

				}
			}
		}

	}
	
	//Implementation of the constraint: "Each of the ghost must be placed in the maze"
	public static void nGhostInMaze(ArrayList<int[]> literals,SatWrapper satWrapper){
		//Iterate the auxiliar array list and add a clause for each pair of literals for each pair of ghosts.
		//A(-Gijg V -Gklg) where j-->Colums g-->(nGhosts),i-->Rows
		for(int ii=1;ii<literals.size();++ii){
			int[] m = literals.get(ii);
			//each ghost is compare to itselft
			for (int f = 0; f < m.length; f++) {
				for (int g = f+1; g < m.length; g++) {
					addClause(satWrapper,-m[f],-m[g]);
				}
			}
			int[] n;
			//For each ghost there is a comparison between all the remaining ghosts.
			for(int ai=ii;ai<literals.size();++ai){
				try{
					n = literals.get(ai+1);
					for (int i = 0; i < n.length; i++) {
						for (int k = 0; k < n.length; k++) {
							addClause(satWrapper,-m[i],-n[k]);

						}
					}

				}catch(Exception e){
					n=literals.get(literals.size()-1);
				}


			}

		}
	}

	public static void main(String[] args) throws IOException {
		cc pp = new cc("C:\\Users\\David\\Desktop\\test");
		pp.print();
		//System.out.println(countLines("C:/Users/David/Desktop/test"));
		//System.out.println(square.toString());


		//Create the store and wrapper to solve the problem as sat problem
		Store store = new Store();
		SatWrapper satWrapper = new SatWrapper(); 
		store.impose(satWrapper);					

		//Array lists to store all binary variables and all literals
		ArrayList<BooleanVar[][]> variables = new ArrayList<BooleanVar[][]>();  //Matrixes of variables
		ArrayList<int[][]> literals = new ArrayList<int[][]>(); // Matrixes of literals


		for(int ii=0;ii<n+1;++ii){
			//Create an entry in the array list for each agent
			variables.add(new BooleanVar[width][height]);
			literals.add(new int[width][height]);
		}
		int a=0;
		int b= variables.size();
		while(a < b){
			//Iterate through the array lists
			BooleanVar[][] aux = variables.get(a);
			int [][] aux2 = literals.get(a);
			for (int row = 0; row < width; row++) {
				for (int col = 0; col < height; col++) {
					//Iterate through the maze read: if character is white space...
					if(Character.isWhitespace(maze[row][col])){
						//The position of the matrix of boolean variables is initialized
						aux[row][col] = new BooleanVar(store,"Space");
						//Then the boolean variable is registered int the wrapper
						satWrapper.register(aux[row][col]);
						//The position of the matrix of literals variables is initialized
						aux2[row][col] = satWrapper.cpVarToBoolVar(aux[row][col], 1,true );
					}	
				}
			}
			++a;		
		}


		ArrayList<int[]> allLiterals = allLiterals(literals);		

		/*Only one Pacman constraint */
		only1Pacman(allLiterals,satWrapper);

		/*each ghost in the maze constraint*/
		eachGhostInMaze(allLiterals,satWrapper);


		/* Resolvemos el problema */
		Search<BooleanVar> search = new DepthFirstSearch<BooleanVar>();
		SelectChoicePoint<BooleanVar> select = new SimpleSelect<BooleanVar>(variables.toArray(new BooleanVar[variables.size()]),
				new SmallestDomain<BooleanVar>(), new IndomainMin<BooleanVar>());
		Boolean result = search.labeling(store, select);


		if (result) {
			System.out.println("Solution: ");
		}


















	}
}
