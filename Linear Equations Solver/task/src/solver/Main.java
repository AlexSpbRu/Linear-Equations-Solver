package solver;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

class Complex implements Comparable<Complex>{
    protected static  DecimalFormat complForm = new DecimalFormat("###.###");
    public static Complex  Zero = new Complex(0.0, 0.0);
    public static Complex  One = new Complex(1.0, 0.0);
    public static Complex  OneMinus = new Complex(-1.0, 0.0);
    protected double  r;
    protected double  im;
    public Complex(double real, double image ) {
        r = real;
        im = image;
    }

    public static Complex parseComplex( String str) {
        if( str.isEmpty() )
            return Complex.Zero;
        double rr = 0.0;
        double ii = 0.0;
        int imPos = Math.max(str.lastIndexOf('-'), str.lastIndexOf('+'));
        if( imPos <= 0 ){
            if(str.charAt(str.length()-1) == 'i') {
                ii = Double.parseDouble(str.substring(0, str.length()-1));
            } else {
                rr = Double.parseDouble(str);
            }
        } else {
            String ss = str.substring(0, imPos);
            rr = Double.parseDouble(ss);
            ss = str.substring(imPos, str.length()-1);
            ii = Double.parseDouble(ss);
        }

        return new Complex(rr,ii);
    }

    public Complex add( Complex num ) {
        Complex newVal = new  Complex(r,im);
        newVal.r += num.r;
        newVal.im += num.im;
        return newVal;
    }

    public Complex mult( Complex num ) {
        Complex newVal = new  Complex(r,im);
        double rr = r*num.r - im*num.im;
        double ii = r*num.im + im*num.r;
        newVal.r = rr;
        newVal.im = ii;
        return newVal;
    }

    public Complex divide( Complex num ) {
        Complex rr = new Complex(num.r,-num.im);
        rr = this.mult(rr);
        double rrr = num.r*num.r + num.im*num.im;
        rr.r /= rrr;
        rr.im /= rrr;
        return rr;
    }
    public String toString() {
        return ( r == 0.0 ? "" : complForm.format(r) ) +
                ( im == 0.0 ? "" : ( (im >= 0.0 ? '+' : '-') + complForm.format(Math.abs(im) ) + "i") );
        //return complForm.format(r) + (im >= 0.0 ? '+' : '-') + complForm.format(Math.abs(im) ) + "i";
        //return String.format("%f%c%fi", r, im >= 0.0 ? '+' : '-', Math.abs(im));
    }

    @Override
    public int compareTo(Complex anotherComplex) {
        if( anotherComplex.r == r &&  anotherComplex.im == im )
            return 0;
        return 1;
    }
   /* public Complex conjugate( ) {
        im = -im;
        return this;
    }*/
}

interface VectorI<T> {
    public String[] init( int count,  String[] inData );

    public  T get( int item );
    public  void set( int item, T elem );

    public int length();

    public VectorI<T> multiplyDouble( T mult );

    public VectorI<T> divide( T mult );

    public void addVector( VectorI<T> vec, T mult );
}

class Vector implements VectorI<Double> {
    public int         length = 0;
    protected double[]    data = null;

    public Vector( ) {

    }
    @Override
    public Double get( int item ) {
        return data[item];
    }
    @Override
    public  void set( int item, Double elem ) {
        data[item] = elem;
    }

    @Override
    public String[] init( int count,  String[] inData ) {
        length = count;
        data = new double[count];

        int i = 0;
        for( int col = 0 ; i < inData.length && col < length  ; ++i ) {
            if(inData[i].isEmpty() )
                continue;
            data[col] = Double.parseDouble(inData[i]);
            col++;
        }

        return Arrays.copyOfRange(inData, i, inData.length);
    }

    @Override
    public int length() {
        return data.length;
    }

    @Override
    public Vector multiplyDouble( Double mult ) {
        for (int i = 0; i < data.length; i++) {
           data[i] *= mult;
        }
        return this;
    }

    @Override
    public Vector divide( Double div ) {
        for (int i = 0; i < data.length ; i++) {
            data[i] /= div;
        }
        return this;
    }

    @Override
    public void addVector( VectorI<Double> vec, Double mult ) {
        for (int i = 0; i < data.length && i < vec.length() ; i++) {
           data[i] += mult*vec.get(i);
        }
    }
}

class VectorC implements VectorI<Complex> {
    public int         length = 0;
    protected Complex[]    data = null;

    public VectorC( ) {

    }
    @Override
    public Complex get( int item ) {
        return data[item];
    }
    @Override
    public  void set( int item, Complex elem ) {
        data[item] = elem;
    }

    @Override
    public String[] init( int count,  String[] inData ) {
        length = count;
        data = new Complex[count];

        int i = 0;
        for( int col = 0 ; i < inData.length && col < length  ; ++i ) {
            if(inData[i].isEmpty() )
                continue;
            data[col] = Complex.parseComplex(inData[i]);
            col++;
        }

        return Arrays.copyOfRange(inData, i, inData.length);
    }

    @Override
    public int length() {
        return data.length;
    }

    @Override
    public VectorC multiplyDouble( Complex num ) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i].mult( num );
        }
        return this;
    }

    @Override
    public VectorC divide( Complex div ) {
        for (int i = 0; i < data.length ; i++) {
            data[i] = data[i].mult( div );
        }
        return this;
    }

    @Override
    public void addVector( VectorI<Complex> vec, Complex num ) {
        for (int i = 0; i < data.length && i < vec.length() ; i++) {
            data[i] = data[i].add( num.mult(vec.get(i)));
        }
    }
}

class Matrix< T, V extends VectorI<T> > {
    protected V[]        matrix;
    protected int        countRow = -1;
    protected int        countCol = -1;

    public ArrayList< Command > undoCommands = new ArrayList<>();

    public Matrix( String[] inData, int rowCount, int colCount, V[]  mat) {
        countRow = rowCount;
        countCol = colCount;

       // String[] data = Arrays.copyOfRange(inData, 1, inData.length);
        matrix = mat;//new V[countRow];
        for (int i = 0; i < matrix.length && inData.length > 0 ; i++) {
            //matrix[i] = new V();
            inData =  matrix[i].init(countCol, inData);
        }
    }

 //   public void init( String[] inData ) {
//
 //   }

    public T get( int row, int col ) {
        if( row < 0 || row >= countRow  || col < 0 || col >= countCol  )
            return null;
        return matrix[row].get(col);
    }

    public V getVector( int row ) {
        if( row < 0 || row >= countRow )
            return null;
        return matrix[row];
    }

    public boolean multiplyDouble( int row, T mult ) {
        if( row < 0 || row >= countRow )
            return false;
        matrix[row].multiplyDouble(mult);
        return true;
    }

    public boolean divide( int row, T div ) {
        if( row < 0 || row >= countRow )
            return false;
        matrix[row].divide(div);
        return true;
    }

    public boolean  addVector( int row, int vec, T mult ) {
        if( row < 0 || row >= countRow ||  vec < 0 || vec >= countRow || row == vec)
            return false;
        matrix[row].addVector(matrix [vec], mult);
        return true;
    }

    public boolean swapRow( int row1, int row2) {
        if( row1 < 0 || row1 >= countRow ||  row2 < 0 || row2 >= countRow || row1 == row2)
            return false;
        V vv =  matrix[row1];
        matrix[row1] =  matrix[row2];
        matrix[row2] = vv;
        return true;
    }

    public boolean swapColumn( int col1, int col2) {
        if( col1 < 0 || col1 >= countCol ||  col2 < 0 || col2 >= countCol || col1 == col2)
            return false;
        V v1 =  matrix[col1];
        V v2 =  matrix[col1];
        for (int i = 0; i < countRow; i++) {
            T d =  v1.get(i);
            v1.set(i, v2.get(i));
            v2.set(i, d);
        }
        return true;
    }
}

interface Command {
    void execute();
}

class SwapColCommand< T, V extends VectorI<T> > implements  Command {

    protected Matrix< T, V>    matrix;
    protected int col1;
    protected int col2;
    public SwapColCommand( Matrix< T, V>  matrix, int col1, int col2) {
        this.matrix = matrix;
        this.col1 = col1;
        this.col2 = col2;

    }
    public void execute() {
        matrix.undoCommands.add(this);
        matrix.swapColumn(col1, col2);
        matrix.undoCommands.add(this);
        System.out.format("C%d <-> C%d\n", col1+1, col2+1);
    }
}

class SwapRowCommand< T, V extends VectorI<T> > implements  Command {

    protected Matrix< T, V>    matrix;
    protected int row1;
    protected int row2;
    public SwapRowCommand( Matrix< T, V>  matrix, int row1, int row2) {
        this.matrix = matrix;
        this.row1 = row1;
        this.row2 = row2;

    }
    public void execute() {
        matrix.swapRow(row1, row2);
        System.out.format("R%d <-> R%d\n", row1+1, row2+1);
    }
}

class MultRowCommand< T, V extends VectorI<T> > implements  Command {

    protected Matrix< T, V>    matrix;
    protected int row;
    protected T k;
    public MultRowCommand( Matrix< T, V>  matrix, int row, T K ) {
        this.matrix = matrix;
        this.row = row;
        this.k = K;

    }
    public void execute() {
        //if( k != 1.0 ) {
            matrix.multiplyDouble(row, k);
            System.out.format("%s * R%d /  -> R%d\n", k.toString(), row+1, row+1);
        //}
    }
}

class DivideRowCommand< T, V extends VectorI<T> > implements  Command {

    protected Matrix< T, V>    matrix;
    protected int row;
    protected T k;
    public DivideRowCommand( Matrix< T, V>  matrix, int row, T K ) {
        this.matrix = matrix;
        this.row = row;
        this.k = K;

    }
    public void execute() {
        //if( k != 1.0 ) {
        matrix.divide(row, k);
        System.out.format("R%d / %s -> R%d\n", row+1, k.toString(), row+1);
        //}
    }
}

class MultandAddRowCommand< T, V extends VectorI<T> > implements  Command {

    protected Matrix< T, V>    matrix;
    protected int row1;
    protected int row2;
    protected T k;
    public MultandAddRowCommand( Matrix< T, V>  matrix, int row1, int row2, T K ) {
        this.matrix = matrix;
        this.row1 = row1;
        this.row2 = row2;
        this.k = K;
    }
    public void execute() {
       // if( k.compareTo(0.0) != 0 ) {
            matrix.addVector(row1, row2, k);
            System.out.format("%s * R%d + R%d -> R%d\n", k.toString(), row2 + 1, row1 + 1, row1 + 1);
       // }
    }
}

/*enum Commands {
    SWAP_ROW,
    SWAP_COLUMN,
    MULT_ROW,
    MULT_AND_ADD_ROW
} */

class SolvingSystemEquations {
    final int   oneSolution = 0;
    final int   noSolutions = -1;
    final int   infinitSolutions = -2;
    final int   zeroEquation = -3;
    final double  EPS = 0.0;

    protected int countRow;
    protected int countCol;
    //protected Matrix< Double, Vector >        matrix;
    protected Matrix< Complex, VectorC >      matrix;
    protected ArrayList< Command > commandsList = new ArrayList<>();
    public String   reply = "";

    SolvingSystemEquations( String[] inData ) {
        countCol = Integer.parseInt(inData[0]) + 1;
        countRow = Integer.parseInt(inData[1]);

        String[] data = Arrays.copyOfRange(inData, 2, inData.length);
        //Vector[] vv = new  Vector[countRow];
        VectorC[] vv = new  VectorC[countRow];
        for (int i = 0; i < vv.length ; i++) {
            vv[i] = new VectorC();
        }
        matrix = new Matrix(data, countRow, countCol, vv);
    }

    void swapRow( int row1, int row2 ) {
        Command  com = new SwapRowCommand(matrix, row1,  row2);
        com.execute();
        commandsList.add(com);
    }

    void swapColumn( int col1, int col2 ) {
        Command  com = new SwapColCommand(matrix,col1,  col2);
        com.execute();
        commandsList.add(com);
    }

    void multRow( int row, Complex k ) {
        Command  com = new MultRowCommand(matrix, row,  k);
        com.execute();
        commandsList.add(com);
    }

    void divideRow( int row, Complex k ) {
        Command  com = new DivideRowCommand(matrix, row,  k);
        com.execute();
        commandsList.add(com);
    }

    void multAndAddRow( int row1, int row2, Complex k  ) {
        Command  com = new MultandAddRowCommand(matrix, row1,  row2, k);
        com.execute();
        commandsList.add(com);
    }

    boolean correctMatrix( int col ) {
        if( col == countCol-1 || col == countRow )
            return false;
        if( matrix.get(col,col).compareTo(Complex.Zero) != 0 )
            return true;
        // find non-zero in column
        boolean cont = true;
        for( int j = col+1 ; j <  countRow ; ++j ) {
            if (matrix.get(j, col).compareTo(Complex.Zero) != 0) {
                swapRow(col, j);
                 cont = false;
                return true;
            }
        }
        if( cont ) {   // find non-zero in row
            for( int j = col+1 ; j <  countCol-1 ; ++j ) {
                if (matrix.get(col, j).compareTo(Complex.Zero) != 0) {
                    swapColumn(col, j);
                      cont = false;
                    return true;
                }
            }
        }
        return correctMatrix( col+1 );

    }

    int checkRow( VectorC row ) {
        boolean allZero = true;
        for( int i = 0 ; i < countCol -1 && allZero ; ++i ){
            if( row.data[i].compareTo(Complex.Zero) != 0 )
                allZero = false;
        }
        if( allZero && row.data[countCol -1].compareTo(Complex.Zero) != 0 )
            return noSolutions;
        if( allZero && row.data[countCol -1].compareTo(Complex.Zero) == 0 )
            return zeroEquation;
        return 0;
    }
    int checkMatrix() {
        int result = 0;
        // check all rows
        for (VectorC row : matrix.matrix) {
            int rr = checkRow( row );
            switch( rr ) {
                case 0 : result++; break;
                case noSolutions :
                    reply = "No solutions";
                    System.out.println("No solutions");
                    return noSolutions;
            }
        }
        if( result >= countCol-1 ) {
            return oneSolution;
        } else if(result < countCol-1) {
            reply = "Infinitely many solutions";
            System.out.println("Infinitely many solutions");
            return infinitSolutions;
        }
        return result;
    }

    boolean correctMatrix() {
        boolean ret = true;
        for (int i = 0; i < countCol - 1 && ret; ++i) {
            ret = ret & correctMatrix(i);
        }
        if (!ret) {   // do something

        }
        return ret;
    }

     public void solveEquations() {
        correctMatrix();
        int res = checkMatrix();
        if( res == noSolutions )
            return;
        for( int i = 0 ; i < countCol-1 && i < countRow  ; ++i) {
            if( matrix.get(i, i).compareTo(Complex.Zero) != 0 )
                divideRow(i, matrix.get(i, i));
                //multRow(i,Complex.One.divide(matrix.get(i, i)));
            // matrix.multiplyDouble(i, 1.0/matrix.get(i, i));

            for( int l = i + 1 ; l < matrix.countRow ; ++l) {
                multAndAddRow(l, i, Complex.OneMinus.mult(matrix.get(l, i)));
                //double k = matrix.get(l, i);
                //matrix.addVector(l, i, -k);
            }

            //multRow(i + 1, 1/matrix.get(i + 1, i + 1) );
            //double k = 1/matrix.get(i + 1, i + 1);
            //matrix.multiplyDouble(i + 1, k);
        }

         res = checkMatrix();
         if( res != oneSolution )
             return;

        for( int i = matrix.countCol-2 ; i > 0 ; --i )   {
            for( int l = i-1 ; l >= 0 ; --l) {
                multAndAddRow(l, i, Complex.OneMinus.mult(matrix.get(l, i)));
                //double k = matrix.get(l, i);
                //matrix.addVector(l, i, -k);
            }
        }

         DecimalFormat myFormatter = new DecimalFormat("###.###");
         for(  int i = 0 ; i <  matrix.countCol-1 ; ++i ) {
             reply += myFormatter.format(matrix.matrix[i].data[matrix.countCol-1]/*/matrix[i][i]*/) + "\r\n";
         }


        System.out.print("The solution is: (");
        for(  int i = 0 ; i <  matrix.countCol-1 ; ++i ) {
            if( i < matrix.countCol-2)
                System.out.print(myFormatter.format(matrix.matrix[i].data[matrix.countCol-1]) + ", ");
            else
                System.out.print(myFormatter.format(matrix.matrix[i].data[matrix.countCol-1]) );
        }
        System.out.println(")");

    }
}

public class Main {
    public static void main(String[] args) {
        String fileInName = "d:\\Alex\\Java\\Test1\\linear.txt";
        String fileOutName = "d:\\Alex\\Java\\Test1\\linearOut.txt";
        for  ( int i = 0 ; i < args.length -1 ; ++i) {
            //System.out.print(" args["+i+"] : " + args[i]);
            if("-in".equals(args[i])) {
                fileInName = args[++i];
            } else if("-out".equals(args[i])) {
                fileOutName = args[++i];
            }
        }

       // System.out.println();
        //System.out.println( args.length + "  " + fileInName + "  " + fileOutName);
        System.out.println("Start solving the equation.");
        System.out.println("Rows manipulation:");

        if( !fileInName.isEmpty() ) {
            try {
                File fileIn = new File(fileInName);
                if (!fileIn.canRead())
                    return;
                long sizeIn = fileIn.length();
                char[] buff = new char[(int)sizeIn];
                FileReader fileInR = new FileReader(fileIn);
                fileInR.read(buff);
                fileInR.close();

                String strBuff = new String(buff);
                System.out.println(strBuff);
                String[] words = strBuff.split("[\\s\n]");

                SolvingSystemEquations linSystem = new SolvingSystemEquations(words);

                linSystem.solveEquations();
                //Matrix matrix = new Matrix(words);
                //matrix.init();


                File fileOut = new File(fileOutName);
                FileWriter fileOutW = new FileWriter(fileOut);

                fileOutW.write(linSystem.reply);
                fileOutW.close();

                System.out.println("Saved to file  " + fileOutName );
            }
            catch( FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}
