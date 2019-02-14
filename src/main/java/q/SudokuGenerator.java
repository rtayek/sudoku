package q;
import java.lang.*;
public class SudokuGenerator { // https://www.geeksforgeeks.org/program-sudoku-generator/
    SudokuGenerator(int size,int K) {
        this.size=size;
        this.K=K;
        Double SRNd=Math.sqrt(size);
        n=SRNd.intValue();
        mat=new int[size][size];
    }
    public void fillValues() {
        // Fill the diagonal of SRN x SRN matrices 
        fillDiagonal();
        // Fill remaining blocks 
        fillRemaining(0,n);
        // Remove Randomly K digits to make game 
        removeKDigits();
    }
    // Fill the diagonal SRN number of SRN x SRN matrices 
    void fillDiagonal() {
        for(int i=0;i<size;i=i+n)
            // for diagonal box, start coordinates->i==j 
            fillBox(i,i);
    }
    // Returns false if given 3 x 3 block contains num. 
    boolean unUsedInBox(int rowStart,int colStart,int num) {
        for(int i=0;i<n;i++)
            for(int j=0;j<n;j++)
                if(mat[rowStart+i][colStart+j]==num) return false;
        return true;
    }
    // Fill a 3 x 3 matrix. 
    void fillBox(int row,int col) {
        int num;
        for(int i=0;i<n;i++) {
            for(int j=0;j<n;j++) {
                do {
                    num=randomGenerator(size);
                } while(!unUsedInBox(row,col,num));
                mat[row+i][col+j]=num;
            }
        }
    }
    // Random generator 
    int randomGenerator(int num) {
        //return (int)Math.floor(r); // from 1 to num
        double r=Math.random()*num+1;
        int i=(int)Math.floor(r);
        if(i<1||i>num)
            System.out.println("range error: "+i+" "+num);
        return i;
    }
    // Check if safe to put in cell 
    boolean CheckIfSafe(int i,int j,int num) {
        return(unUsedInRow(i,num)&&unUsedInCol(j,num)&&unUsedInBox(i-i%n,j-j%n,num));
    }
    // check in the row for existence 
    boolean unUsedInRow(int i,int num) {
        for(int j=0;j<size;j++)
            if(mat[i][j]==num) return false;
        return true;
    }
    // check in the row for existence 
    boolean unUsedInCol(int j,int num) {
        for(int i=0;i<size;i++)
            if(mat[i][j]==num) return false;
        return true;
    }
    // A recursive function to fill remaining  
    // matrix 
    boolean fillRemaining(int i,int j) {
        //  System.out.println(i+" "+j); 
        if(j>=size&&i<size-1) {
            i=i+1;
            j=0;
        }
        if(i>=size&&j>=size) return true;
        if(i<n) {
            if(j<n) j=n;
        } else if(i<size-n) {
            if(j==(int)(i/n)*n) j=j+n;
        } else {
            if(j==size-n) {
                i=i+1;
                j=0;
                if(i>=size) return true;
            }
        }
        for(int num=1;num<=size;num++) {
            if(CheckIfSafe(i,j,num)) {
                mat[i][j]=num;
                if(fillRemaining(i,j+1)) return true;
                mat[i][j]=0;
            }
        }
        return false;
    }
    // Remove the K no. of digits to 
    // complete game 
    public void removeKDigits() {
        int count=K;
        while(count!=0) {
            int cellId=randomGenerator(size*size);
            // System.out.println(cellId); 
            // extract coordinates i  and j 
            int i=(cellId/size);
            int j=cellId%9;
            System.out.println(i+" "+j);
            if(j!=0) j=j-1;
            // System.out.println(i+" "+j); 
            if(mat[i][j]!=0) {
                count--;
                mat[i][j]=0;
            }
        }
    }
    // Print sudoku 
    public void printSudoku() {
        for(int i=0;i<size;i++) {
            for(int j=0;j<size;j++)
                System.out.print(mat[i][j]+" ");
            System.out.println();
        }
        System.out.println();
    }
    // Driver code 
    public static void main(String[] args) {
        int size=9,K=20;
        SudokuGenerator sudoku=new SudokuGenerator(size,K);
        sudoku.fillValues();
        sudoku.printSudoku();
    }
    final int[] mat[];
    final int size;
    final int n;
    final int K; // No. Of missing digits 
}
