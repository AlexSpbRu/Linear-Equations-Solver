import java.util.Scanner;
import java.util.ArrayList;

class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int[][] matrix = null;
        int wdt = 2;
        int hgt = 2;
        int lineNum = 0;
        ArrayList<String> list = new ArrayList<>();
        while(scan.hasNext() ) {
            String str = scan.nextLine();
            if( "end".equalsIgnoreCase(str) )  {
                break;
            } else {
                list.add(str);
            }
        }
        hgt = list.size();

        for( String str : list) {
             String[] row = str.split(" ");
            if (matrix == null) {
                wdt = row.length;
                matrix = new int[hgt][wdt];
            }
            for (int i = 0; i < wdt && i < row.length; ++i) {
                matrix[lineNum][i] = Integer.parseInt(row[i]);
            }
            lineNum++;
        }


        int[][] matrix1 = new int[hgt][wdt];
        for( int i = 0 ; i < hgt ; ++i){
            for( int j = 0 ; j < wdt ; ++j ){
                matrix1[i][j] = 0;
                matrix1[i][j] += i - 1 < 0 ? matrix[hgt-1][j] : matrix[i-1][j];
                matrix1[i][j] += i + 1 >= hgt ? matrix[0][j] : matrix[i+1][j];
                matrix1[i][j] += j - 1 < 0 ? matrix[i][wdt-1] : matrix[i][j - 1];
                matrix1[i][j] += j + 1 >= wdt ? matrix[i][0] : matrix[i][j + 1];
            }
        }
       /* for( int[] rr : matrix ){
            for( int val : rr ){
                System.out.print(val + " ");
            }
            System.out.println();
        } */

        for( int[] rr : matrix1 ){
            for( int val : rr ){
                System.out.print(val + " ");
            }
            System.out.println();
        }
    }
}

/*
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int size = scan.nextInt();
        int[][] matrix = new int[size][size];
        for( int i = 0 ; i < size ; ++i) {
            for( int j = 0 ; j < size ; ++j ){
                matrix[i][j] = scan.nextInt();
            }
        }

        boolean simm = true;
        for( int i = 0 ; i < size ; ++i) {
            for( int j = i +1  ; j < size ; ++j ){
                if( matrix[i][j] != matrix[j][i] ) {
                    simm = false;
                    break;
                }
            }
        }
        System.out.println( simm ? "YES" : "NO");
    }
}





import java.util.Scanner;
import java.util.Arrays;

class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int cellSize = scan.nextInt();
        int size = cellSize*cellSize;

        int[][] matrix = new int[size][size];

        for( int i = 0 ; i < size ; ++i) {
            for( int j = 0 ; j < size ; ++j ){
                matrix[i][j] = scan.nextInt();
            }
        }

        int[] control = new int[size];
        Arrays.fill(control,1);
        int[] row = new int[size];
        int[] col = new int[size];
        int[] sell = new int[size];
        //check rows and  column
        for( int i = 0 ; i < size ; ++i) {
            Arrays.fill(row, 0);
            Arrays.fill(col, 0);

            for( int j = 0  ; j < size ; ++j ){
                if( matrix[i][j] <= row.length )
                    row[ matrix[i][j]-1 ] = 1;
                if( matrix[j][i] <= col.length )
                    col[ matrix[j][i]-1 ] = 1;
            }
            if( !Arrays.equals(row, control ) || !Arrays.equals(col, control ) ) {
                System.out.println( "NO");
                return;
            }
        }

        for( int i = 0 ; i < size ; i += cellSize )  {

            for( int j = 0  ; j < size ; j += cellSize ) {
                Arrays.fill(sell, 0);
                for( int l = i ; l < i + cellSize ; ++l ) {
                    for( int m = j ; m < j + cellSize ; ++m  ) {
                        if( matrix[l][m] <= row.length )
                            sell[ matrix[l][m]-1 ] = 1;
                    }
                }

                if( !Arrays.equals(sell, control ) ) {
                    System.out.println( "NO");
                    return;
                }
            }

        }
        System.out.println( "YES" );

    }
}
 */