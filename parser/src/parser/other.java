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

public class other {
	static char[][] maze = null;
	static int width =  4;
	static int height = 4;
	static int n =2;

	public void readFile(String filename) throws IOException {

		BufferedReader read = new BufferedReader(new FileReader(filename));

		String readline;
		int num = 0;
		maze = new char[width][height];
		while((readline = read.readLine()) != null){
			char[] ch = readline.toCharArray();
			for(int i = 0;i < ch.length;i++){
				maze[i][num] = ch[i];
			}
			num++;
		}
		read.close();
	}

	public void print() {
		if (maze != null) {
			for (int row = 0; row < width; row++) {
				for (int col = 0; col < height; col++) {

					System.out.print(maze[row][col]);
					//System.out.print(" ");
				}
				System.out.print('\n');
			}

		}
	}

	public other(String filename) {
		try {
			readFile(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addClause(SatWrapper satWrapper, int literal1){
		IntVec clause = new IntVec(satWrapper.pool);
		clause.add(literal1);
		satWrapper.addModelClause(clause.toArray());
	}
	
	public static void addClause1(SatWrapper satWrapper, ArrayList<Integer> literals){
		
	}
	
	
	
	public static void main(String[] args) throws IOException {
		other pp = new other("C:\\Users\\David\\Desktop\\test");
		pp.print();
		//System.out.println(countLines("C:/Users/David/Desktop/test"));
		//System.out.println(square.toString());



		Store store = new Store();
		SatWrapper satWrapper = new SatWrapper(); 
		store.impose(satWrapper);					/* Importante: sat problem */

		ArrayList<BooleanVar[][]> nombreArrayList = new ArrayList<BooleanVar[][]>();  //
		//ArrayList<BooleanVar> variables = new ArrayList<BooleanVar>();
		ArrayList<Integer[][]> literals = new ArrayList<Integer[][]>(); //Matrixes of literals
		
		
		for(int ii=0;ii<n+1;++ii){
			nombreArrayList.add(new BooleanVar[width][height]);
			literals.add(new Integer[width][height]);
		}
		int a=0;
		int b= nombreArrayList.size();
		while(a < b){
			BooleanVar[][] aux = nombreArrayList.get(a);
			int [][] aux2 = literals.get(a);
			for (int row = 0; row < width; row++) {
				for (int col = 0; col < height; col++) {
					if(Character.isWhitespace(maze[row][col])){
						aux[row][col] = new BooleanVar(store,"Space");
						variables.add(aux[row][col]);
						satWrapper.register(aux[row][col]);
						satWrapper.cpVarToBoolVar(aux2[row][column], 1,true );
					}
					
				}
			}
			++a;		
		}
		
		/*Only one pacman */
		int [][] aux = literals.get(0);
		for (int row = 0; row < width; row++) {
			for (int col = 0; col < height; col++) {
				if(aux[row][col]!=null){
					addClause(satWrapper,-literal);
				}
				
			}
		}
		/*Ghost in maze*/
		a = 1;
		while(a < b){
			BooleanVar[][] aux1 = nombreArrayList.get(a);
		for (int row = 0; row < width; row++) {
			for (int col = 0; col < height; col++) {
				if(aux[row][col]!=null){
				int literal = satWrapper.cpVarToBoolVar(aux1[row][col], 1, true);
				addClause(satWrapper,literal);
				}
			}
		}
		++a;
		}
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
