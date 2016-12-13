package parser;

import java.util.ArrayList;

import org.jacop.satwrapper.SatWrapper;

public class funciones {

	public static void print(int[][] a){

		for (int row = 0; row < a.length; row++) {
			for (int col = 0; col < a[0].length; col++) {
				System.out.print(a[row][col] + " ");
			}
			System.out.print('\n');
		}
	}

	public static void parse(ArrayList<int[]> a){
		//Parse all the matrix into an array
		int newArray[] = a.get(0);


		//Iterate through the array already created in previous steps and add clause for each pair of literals:
		// A(-Pij V -Pkl) where k!=i,j!=l, i,k-->Rows,j,l-->Columns
		for (int i = 0; i < newArray.length; i++) {
			for (int k = i + 1; k < newArray.length; k++) {
				System.out.println(newArray[i] +" con " + newArray[k]);

			}
		}
	}

	public static void eachGhostInMaze(ArrayList<int[]> literals){

		//Iterate the auxiliar array list and add a clause for each pair of literals for each ghost.
		//A(V Gijg) where j-->Colums g-->(nGhosts),i-->Rows
		for(int ii=1;ii<literals.size();++ii){
			int[] m = literals.get(ii);

			for (int q = 0; q < m.length; q++) {
				for (int k = q + 1; k < m.length; k++) { 
					System.out.println(m[q] + " con " + m[k]);

				}
			}
		}

	}

	public static void nGhostInMaze(ArrayList<int[]> literals){
		//Iterate the auxiliar array list and add a clause for each pair of literals for each ghost.
		//A(V Gijg) where j-->Colums g-->(nGhosts),i-->Rows
		for(int ii=1;ii<literals.size();++ii){
			int[] m = literals.get(ii);
			for (int f = 0; f < m.length; f++) {
				for (int g = f+1; g < m.length; g++) {
					System.out.println(m[f] +" con " + m[g]);

				}
			}
			int[] n;
			for(int ai=ii;ai<literals.size();++ai){
				try{
					n = literals.get(ai+1);
					for (int i = 0; i < n.length; i++) {
					for (int k = 0; k < n.length; k++) {
						System.out.println(m[i] +" con " + n[k]);

					}
				}
					
				}catch(Exception e){
					n=literals.get(literals.size()-1);
				}
				
				
			}
			
		}
	}

	public static ArrayList<int[]> parse2Array(ArrayList<int[][]> a){
		//Array list to support dynamic number of ghost
		ArrayList<int[]> aux = new ArrayList<int[]>();
		int c=a.size();
		//Iterate through the array list and parse each matrix into an array
		for(int b=0;b<c;++b){ //Pacman always fixed in the first position
			int [][] au = a.get(b);
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

	public static void recorrer(int[][] a){
		/*for(int q = row; q < a.length; q++){

					for(int w = col; w < a[0].length; w++){

						if(row!=q || col!=w){
							System.out.println("row: " + row + " col: " + col + " q: " + q + " w: " + w);
							System.out.println();
							System.out.println(a[row][col]+ " con " + a[q][w]);
							System.out.println();
						}
					}*/
		for (int row = 0; row < a.length; row++) {

			for (int col = 0; col < a[0].length; col++) {		

				for(int q = 0; q < a.length; q++){

					for(int w = 0; w < a[0].length; w++){

						if(row!=q || col!=w){
							//System.out.println("row: " + row + " col: " + col + " q: " + q + " w: " + w);
							if(row+col<=q+w){
								System.out.println();
								System.out.println(a[row][col]+ " con " + a[q][w]);
								System.out.println();
							}
						}
					}
				}
			}}
	}







	public static void main(String[] args) {
		int m [][]= { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 }};//pacman
		int n [][] = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 }};
		int b [][] = { { 10, 11, 12 }, { 13, 14, 15 }, { 16, 17, 18 }};
		//int c [][] = { { 0, 2, 4 }, { 6, 8, 10 }, { 12, 14, 16 }};
		ArrayList<int[][]> aux = new ArrayList<int[][]>();

		aux.add(m);
		aux.add(n);
		aux.add(b);
		//aux.add(c);

		ArrayList<int[]> d = parse2Array(aux);
		
		System.out.println("------------eachGhostInMaze---------");
		
		eachGhostInMaze(d);
		
		System.out.println("-----------nGhostInMaze----------");
		nGhostInMaze(d);
	}

}
