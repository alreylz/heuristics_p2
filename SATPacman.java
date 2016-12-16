
import org.jacop.core.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.jacop.constraints.*; 
import org.jacop.search.*;
import org.jacop.jasat.utils.structures.* ;
import org.jacop.satwrapper.*;


public class SATPacman {

	
	
	
	
	//Attributes
	private char[][] inputMaze = null;   //Char matrix containing the characters of the input file
	private int height = 0;			//height of the matrix == number of rows
	private int width  = 0;				//width of the matrix == number of columns
			
	private int n_ghosts = 1 ;
	private BooleanVar [][][] matrices_bool = null; //Contains the boolean variables
	private int [][][]  matrices_literals = null; //Contains the literals used to create the actual clauses and then use them to define the SAT problem
	private BooleanVar[] allVariables = null ;
	
	//WORKS
	public SATPacman(String filename, int n_ghosts ) throws IOException{ //C
		readFileAndSetDimensions(filename);
		fillMaze(filename);
		this.n_ghosts = n_ghosts ; //Assign number of ghosts
		matrices_bool = new BooleanVar[n_ghosts+1][this.height][this.width] ;  
		matrices_literals =  new int [n_ghosts+1][this.height][this.width] ; 
		
		allVariables = new BooleanVar[height*width*(n_ghosts+1)] ;  //All variables in an array as we need an array for solving
		
		
		
	}
	//WORKS
	public void readFileAndSetDimensions(String filename) throws IOException {//Fills the maze of chars used to extract information on empty cells

		BufferedReader read = new BufferedReader(new FileReader(filename));

		String readline;
		int rows = 0; //will
		char[] ch = null ;
		
		while((readline = read.readLine()) != null){
			ch = readline.toCharArray();   //read a line and store it in a character array
			
			rows++;
		}
		
		this.width=ch.length;
		this.height=rows;
		
		read.close();
		
		
		
		
}
	//WORKS
	public void fillMaze (String filename) throws IOException{
		
		BufferedReader read2 = new BufferedReader(new FileReader(filename));

		String readline2;
		int num = 0;
		inputMaze = new char[height][width];
		while((readline2 = read2.readLine()) != null){
			char[] ch2 = readline2.toCharArray();
				inputMaze[num] = ch2;
			num++;
		}
		read2.close();
		
	}
	
	public void initializeBoolVariables( Store store, SatWrapper wrapper)  {

		int m_columns = this.width;
		int n_rows = this.height ;

		for(int x=0 ; x<n_rows ; x++ ){
			for(int y=0 ; y<m_columns ; y++ ){
				matrices_bool[0][x][y] = new BooleanVar(store, "p("+x+","+y+")" ); // variables which represent whether the pacman is 
				//in a given cell or not 0->nope  1->Yes
				wrapper.register(matrices_bool[0][x][y]);
				
				matrices_literals[0][x][y] = wrapper.cpVarToBoolVar(matrices_bool[0][x][y],1,true);
				
			}
		}	
		
		
		
		for (int g=1 ;g<n_ghosts+1; g++ ){
			for(int x=0 ; x<n_rows ; x++ ){
				for(int y=0 ; y<m_columns ; y++ ){
					matrices_bool[g][x][y] = new BooleanVar(store, "g("+g+","+x+","+y+")" ); // variables which represent whether the a ghost (g) is 
				//in a given cell or not 0->nope  1->Yes
					wrapper.register(matrices_bool[g][x][y]);
					
					matrices_literals[g][x][y] = wrapper.cpVarToBoolVar(matrices_bool[g][x][y],1,true);
				}
			}
		}


	}

	//Pacman can only be placed in one cell, if it is placed in pos x, y then no other position can take value true
	public void one_Pacman_Only ( Store store, SatWrapper wrapper){
		
		
		int cols = this.width;
		int rows = this.height  ;

		IntVec clause = new IntVec(wrapper.pool); //Clause set creation
		
		for(int x=0 ; x<rows ; x++ ){
			for(int y=0 ; y<cols ; y++ ){
				for(int k=0 ; k<rows ; k++ ){
					for(int j=0 ; j<cols  ; j++ ){

						if(x==k && y==j){ //If we are comparing with the same row
							//DO NOTHING!!!!
						}
						else{  //Compare with all the other cells
							clause.add(- matrices_literals[0][x][y]) ;  
							clause.add(- matrices_literals[0][k][j]);
							wrapper.addModelClause(clause.toArray()) ;// -p[x][y] v -p[k][j]
						}
					}
				}
				
				if(inputMaze[x][y]=='%' || inputMaze[x][y]=='O'){   //Pacman cannot be placed on a nonempty cell
					clause.add(- matrices_literals[0][x][y]);
					wrapper.addModelClause(clause.toArray()) ;  
					
				}
			}
		}
		
		
		
		
		
	}

	public void one_Pacman_for_sure ( Store store, SatWrapper wrapper) {
		
		int cols = this.width;
		int rows = this.height  ;

		IntVec clause = new IntVec(wrapper.pool); //Clause set creation
		
		for(int x=0 ; x<rows ; x++ ){
			for(int y=0 ; y<cols ; y++ ){
							clause.add( matrices_literals[0][x][y]) ;  
					}
				}
			wrapper.addModelClause(clause.toArray()) ;
		
	}
	
	//Ghosts can neither be placed in nonempty cells nor in two positions (meaning a specific ghost)
 	public void one_ghost_one_pos ( Store store, SatWrapper wrapper){  
		/**
		 * Given that we have not established constraints to avoid that the same ghost is assigned to 2 positions
		 * 		this function is in charge of that. It always avoids a ghost to be placed in a nonempty cell.
		 */
		int cols = this.width;
		int rows = this.height ;
		int number_of_ghosts = this.n_ghosts;
		
		IntVec clause = new IntVec(wrapper.pool); //Clause set creation
		
		for(int g=1; g<number_of_ghosts+1 ; g++){
			for(int x=0 ; x<rows ; x++ ){
				for(int y=0 ; y<cols ; y++ ){
					for(int k=0 ; k<rows ; k++ ){
						for(int j=0 ; j<cols  ; j++ ){

							if(x==k && y==j){ //If we are comparing with the same row
								//DO NOTHING!!!!
							}
							else{  //Compare with all the other cells
								clause.add(- matrices_literals[g][x][y]) ;  
								clause.add(- matrices_literals[g][k][j]);
								wrapper.addModelClause(clause.toArray()) ;// -g[number][x][y] v -g[number][k][j]
							}
						}
					}
				}
			}
		}
	}
	
 	public void do_put_ghosts (Store store, SatWrapper wrapper){
 		
 		int cols = this.width;
		int rows = this.height  ;

		IntVec clause = new IntVec(wrapper.pool); //Clause set creation
		for(int g=1 ; g<this.n_ghosts+1 ;g++ ){
			for(int x=0 ; x<rows ; x++ ){
				for(int y=0 ; y<cols ; y++ ){
								clause.add( matrices_literals[g][x][y]) ;  
						}
					}
		}
			wrapper.addModelClause(clause.toArray()) ;
			
			
			for(int g=1 ; g<this.n_ghosts+1 ;g++ ){
				for(int x=0 ; x<rows ; x++ ){
					for(int y=0 ; y<cols ; y++ ){
						if(inputMaze[x][y]=='%' || inputMaze[x][y]=='O'){
							clause.add(- matrices_literals[0][x][y]);
							wrapper.addModelClause(clause.toArray()) ;  
						}
					}
				}}

 		
 	}
 	
	//Different ghosts cannot be placed in the same row
	public void no_Two_ghosts_same_row ( Store store, SatWrapper wrapper){

		int cols = this.width;
		int rows = this.height;
		int ghosts = this.n_ghosts;
		IntVec clause = new IntVec(wrapper.pool); //Clause set creation

		//Un ghost no puede estar en dos posiciones
		//Un ghost no puede estar en la misma fila que otro
		//Un pacman no puede estar cerca de un ghost


		for(int g1=1; g1<ghosts+1 ; g1++){
			for(int g2=2; g2<ghosts+1 ; g2++){
				for(int x=0 ; x<rows ; x++ ){
					for(int y=0 ; y<cols ; y++ ){
						for(int k=0 ; k<cols ; k++ ){
							clause.add(- matrices_literals[g1][x][y]) ;  
							clause.add(- matrices_literals[g2][x][k]);
							wrapper.addModelClause(clause.toArray()) ;// -g[x][y] v -g[x][k]
						}

					}
				}

			}
		}
		
		
		
		
		
	}

	
	public void no_Ghosts_around_Pacman ( Store store, SatWrapper wrapper){


		int rows = this.height ;
		int cols = this.width;


		IntVec clause = new IntVec(wrapper.pool); //Clause set creation
		for(int g=1 ; g<this.n_ghosts+1; g++){
			for(int x=0 ; x<rows ; x++ ){
				for(int y=0 ; y<cols ; y++ ){



					clause.add(- matrices_literals[0][x][y]) ;  //Pacman [x][y]
					clause.add(- matrices_literals[g][x][y]) ;
					wrapper.addModelClause(clause.toArray()) ;  // -p[x][y] v -ghost[g][x][y]

					try{
						clause.add(- matrices_literals[0][x][y]) ; 
						clause.add(- matrices_literals[g][x+1][y]) ;
						wrapper.addModelClause(clause.toArray()) ;
					}
					catch(Exception e){
					}

					try{
						clause.add( - matrices_literals[0][x][y]) ; 
						clause.add( - matrices_literals[g][x-1][y] );
						wrapper.addModelClause(clause.toArray()) ;
					}
					catch(Exception e){
					}
					try{
						clause.add( - matrices_literals[0][x][y]) ;
						clause.add( - matrices_literals[g][x][y+1]);
						wrapper.addModelClause(clause.toArray()) ;
					}
					catch(Exception e){
					}				
					try{
						clause.add( - matrices_literals[0][x][y]) ;
						clause.add( -	matrices_literals[g][x][y-1] );
						wrapper.addModelClause(clause.toArray()) ;
					}
					catch(Exception e){
					}
					try{
						clause.add( - matrices_literals[0][x][y]) ;
						clause.add( -	matrices_literals[g][x+1][y+1] );
						wrapper.addModelClause(clause.toArray()) ;
					}

					catch(Exception e){
					}

					try{
						clause.add( - matrices_literals[0][x][y]) ;
						clause.add( - matrices_literals[g][x+1][y-1]) ;
						wrapper.addModelClause(clause.toArray()) ;
					}
					catch(Exception e){
					}
					try{
						clause.add( - matrices_literals[0][x][y]) ;
						clause.add( - matrices_literals[g][x-1][y+1] );
						wrapper.addModelClause(clause.toArray()) ;
					}
					catch(Exception e){
					}


					try{
						clause.add( - matrices_literals[0][x][y]) ;
						clause.add( -	matrices_literals[g][x-1][y-1] ) ;
						wrapper.addModelClause(clause.toArray()) ;
					}
					catch(Exception e){
					}


				}
			}
		}

	}
	
	public void setArray() {
		
		ArrayList<BooleanVar>  aux = new ArrayList<BooleanVar>() ;
		
		for(int g=0; g<this.n_ghosts+1 ; g++){
			for(int i=0; i<this.height ; i++){
				for(int j=0; j<this.width ; j++){

					aux.add(matrices_bool[g][i][j]) ;
				}
			}
		}
		
		aux.toArray(this.allVariables);
	}
	
	public void PrintMaze (){
		for (int row = 0; row < this.height; row++) {
			for (int col = 0; col < this.width; col++) {
				System.out.print(inputMaze[row][col]);
			}
			System.out.println();
		}

	}






	public static void main(String args[]) throws IOException{

		Store store = new Store();
		SatWrapper satWrapper = new SatWrapper(); 
		store.impose(satWrapper);	
		
		SATPacman AutomatedFilling = new SATPacman("C:\\Users\\Nolliejandro\\Dropbox\\testMaze0" ,2);

		AutomatedFilling.PrintMaze();
		AutomatedFilling.initializeBoolVariables(store, satWrapper);
		AutomatedFilling.one_Pacman_Only( store, satWrapper);
		AutomatedFilling.one_Pacman_for_sure(store, satWrapper);
		AutomatedFilling.one_ghost_one_pos(store, satWrapper);
		AutomatedFilling.no_Two_ghosts_same_row(store, satWrapper);
		AutomatedFilling.do_put_ghosts(store, satWrapper);
		AutomatedFilling.no_Ghosts_around_Pacman(store, satWrapper);
		AutomatedFilling.setArray();

		
		/* Solve*/ 
		
	    Search<BooleanVar> search = new DepthFirstSearch<BooleanVar>();

		SelectChoicePoint<BooleanVar> select = new SimpleSelect<BooleanVar>(AutomatedFilling.getAllVariables() , new SmallestDomain<BooleanVar>(), new IndomainMin<BooleanVar>());
		Boolean result = search.labeling(store, select);
		
		
		
	}
	
	public BooleanVar[] getAllVariables() {
		return allVariables;
	}
	public void setAllVariables(BooleanVar[] allVariables) {
		this.allVariables = allVariables;
	}
	public char[][] getInputMaze() {
		return inputMaze;
	}
	public void setInputMaze(char[][] inputMaze) {
		this.inputMaze = inputMaze;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getN_ghosts() {
		return n_ghosts;
	}
	public void setN_ghosts(int n_ghosts) {
		this.n_ghosts = n_ghosts;
	}
	public BooleanVar[][][] getMatrices_bool() {
		return matrices_bool;
	}
	public void setMatrices_bool(BooleanVar[][][] matrices_bool) {
		this.matrices_bool = matrices_bool;
	}
	public int[][][] getMatrices_literals() {
		return matrices_literals;
	}
	public void setMatrices_literals(int[][][] matrices_literals) {
		this.matrices_literals = matrices_literals;
	}

}
