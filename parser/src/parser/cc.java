package parser;
import java.io.*;
import java.util.ArrayList;

import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.jasat.utils.structures.IntVec;
import org.jacop.satwrapper.SatWrapper;
import org.jacop.search.*;

public class cc {
	static char[][] maze = null;
	static int width =  4;
	static int height = 4;
	static int n =2;

	public static void addClause(SatWrapper satWrapper, int literal1, int literal2){
		IntVec clause = new IntVec(satWrapper.pool);
		clause.add(literal1);
		clause.add(literal2);
		satWrapper.addModelClause(clause.toArray());
	}


	public static void addClause(SatWrapper satWrapper, int literal1, int literal2, int literal3){
		IntVec clause = new IntVec(satWrapper.pool);
		clause.add(literal1);
		clause.add(literal2);
		clause.add(literal3);
		satWrapper.addModelClause(clause.toArray());
	}	
	
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

	public cc(String filename) {
		try {
			readFile(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) throws IOException {
		cc pp = new cc("C:\\Users\\David\\Desktop\\test");
		pp.print();
		//System.out.println(countLines("C:/Users/David/Desktop/test"));
		//System.out.println(square.toString());


		


		Store store = new Store();
		SatWrapper satWrapper = new SatWrapper(); 
		store.impose(satWrapper);					/* Importante: sat problem */

		
		BooleanVar x = new BooleanVar(store, "empty");
		BooleanVar y = new BooleanVar(store, "wall");
		BooleanVar z = new BooleanVar(store, "capsule");
		BooleanVar w = new BooleanVar(store, "ghost");
		BooleanVar p = new BooleanVar(store, "pacman");
		
		BooleanVar[] allVariables = new BooleanVar[]{x, y, z, w, p};
		
		satWrapper.register(x);
		satWrapper.register(y);
		satWrapper.register(z);
		satWrapper.register(w);
		satWrapper.register(p);
		
		int xLiteral = satWrapper.cpVarToBoolVar(x, 1, true);
		int yLiteral = satWrapper.cpVarToBoolVar(y, 1, true);
		int zLiteral = satWrapper.cpVarToBoolVar(z, 1, true);
		int wLiteral = satWrapper.cpVarToBoolVar(w, 1, true);
		int pLiteral = satWrapper.cpVarToBoolVar(p, 1, true);
		
		
		ArrayList<BooleanVar[][]> nombreArrayList = new ArrayList<BooleanVar[][]>();		

		for(int ii=0;ii<n+1;++ii){
			nombreArrayList.add(new BooleanVar[width][height]);
		}
		
		int a=0;
		int b= nombreArrayList.size();
		while(a < b){
			BooleanVar[][] aux = nombreArrayList.get(a);
			for (int row = 0; row < width; row++) {
				for (int col = 0; col < height; col++) {
					if(maze[row][col]=='%'){
						aux[row][col] = y;
					}else  if(maze[row][col]=='o'){
						aux[row][col] = z;
					}else{
						aux[row][col] = x;
					}
					System.out.println(aux[row][col].toString());
					satWrapper.register(aux[row][col]);
				}
			}
			++a;		
		}
		int k = satWrapper.cpVarToBoolVar(nombreArrayList.get(0)[0][0], 0, true);
		int k2 = satWrapper.cpVarToBoolVar(nombreArrayList.get(0)[1][1], 0, false);
		
		
		
		IntVec clause = new IntVec(satWrapper.pool);
		clause.add(k);
		clause.add(k2);
		System.out.println(clause.toArray()[0]);
//		while(a < (b-1)){
//			BooleanVar[][] aux = nombreArrayList.get(a);
//			BooleanVar[][] aux2 = nombreArrayList.get(a+1);
//			for (int row = 0; row < width; row++) {
//				for (int col = 0; col < height; col++) {
//					//addClause(satWrapper, aux[row][col], aux2[row][col]);
//				}
//
//			}
//			++a;
//		}






















	}
}
