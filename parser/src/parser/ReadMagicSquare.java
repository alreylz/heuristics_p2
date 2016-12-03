package parser;
import java.io.*;
public class ReadMagicSquare {

    private int[][] matrix;
    private int size = -1;
    private int size2 = -1;
    private int log10 = 0;
    private String numberFormat;

    
    
    
    public static int countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 1;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }
    
    
    
    
    
    
    public ReadMagicSquare(String filename) {
        try {
            readFile(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFile(String filename) throws IOException {
        BufferedReader buffer = new BufferedReader(new FileReader(filename));

        String line;
        int row = 0;

        while ((line = buffer.readLine()) != null) {
            String[] vals = line.trim().split("\\s+");

            // Lazy instantiation.
            if (matrix == null) {
                size = vals.length; //col
                size2 = countLines(filename); //row
                matrix = new int[size2][size];
                log10 = (int) Math.floor(Math.log10(size * size2)) + 1;
                numberFormat = String.format("%%%dd", log10);
            }

            for (int col = 0; col < size; col++) {
                matrix[row][col] = Integer.parseInt(vals[col]);
            }

            row++;
        }
        buffer.close();
    }

    public void print() {
        if (matrix != null) {
            for (int row = 0; row < size2; row++) {
                for (int col = 0; col < size; col++) {
                	
                   System.out.print(matrix[row][col]);
                   System.out.print(" ");
            }
                System.out.println('\n');
        }
            
    }
    }

    public static void main(String[] args) throws IOException {
       ReadMagicSquare square = new ReadMagicSquare("C:\\Users\\David\\Desktop\\test");
        square.print();
        //System.out.println(countLines("C:/Users/David/Desktop/test"));
        //System.out.println(square.toString());
    }
}