package p;
import static java.lang.Math.*;
import static p.Magic.*;
import static p.Sudoku.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
//http://sudopedia.enjoysudoku.com/Canonical_Form.html
class Magic {
    private Magic(int size) {
        n=(int)round(sqrt(size));
        magic=new int[size][size];
    }
    static Magic create(int[][] magic) {
        Magic m=new Magic(magic.length);
        m.set(magic);
        return m;
    }
    void set(int[][] magic) {
        for(int i=0;i<magic.length;i++)
            for(int j=0;j<magic[0].length;j++)
                this.magic[i][j]=magic[i][j];
        if(!isMagic(false)) System.out.println(this.toString4()+"\n+is not magic!"+"\n"+this);
    }
    @Override public String toString() { // to wide string with no spaces
        StringBuffer stringBuffer=new StringBuffer();
        for(int i=0;i<magic.length;i++)
            for(int j=0;j<magic[0].length;j++)
                stringBuffer.append(magic[i][j]);
        return stringBuffer.toString();
    }
    static Magic fromString(String string) {
        int size=mySqrt(string.length());
        int[][] magic=new int[size][size];
        for(int k=0;k<string.length();k++) {
            int i=k/size,j=k%size;
            magic[i][j]=Integer.valueOf(""+string.charAt(k));
        }
        Magic m=new Magic(size);
        m.set(magic);
        return m;
    }
    public String toString2() { // to wide string with spaces
        StringBuffer stringBuffer=new StringBuffer();
        for(int i=0;i<magic.length;i++)
            for(int j=0;j<magic[0].length;j++)
                stringBuffer.append(magic[i][j]).append(' ');
        return stringBuffer.toString();
    }
    static Magic fromString2(String string) {
        int[][] magic=get(string,0);
        return create(magic);
    }
    public String toString3() { // to vertical string with line feeds
        StringBuffer stringBuffer=new StringBuffer();
        for(int i=0;i<magic[0].length;i++)
            for(int j=0;j<magic[0].length;j++)
                stringBuffer.append(""+magic[i][j]+'\n');
        return stringBuffer.toString();
    }
    static Magic fromString3(String string) {
        int[][] magic=get(string,0);
        return create(magic);
    }
    public String toString4() { // matrix
        StringBuffer stringBuffer=new StringBuffer();
        for(int i=0;i<magic[0].length;i++) {
            for(int j=0;j<magic[0].length;j++)
                stringBuffer.append(""+magic[i][j]);
            stringBuffer.append('\n');
        }
        return stringBuffer.toString();
    }
    Integer[] frequencies(int i0,int j0,int n,int di,int dj) {
        Integer[] frequencies=new Integer[n*n];
        for(int k=0;k<n*n;k++)
            frequencies[k]=0;
        int i,j;
        ArrayList<Integer> list=new ArrayList<>();
        for(int k=0;k<n;k++) {
            i=i0+k*di;
            j=j0+k*dj;
            int value=magic[i][j];
            list.add(value);
            if(value>0) frequencies[value-1]++;
        }
        return frequencies;
    }
    boolean ok(Integer[] frequencies,final int sum) {
        for(int i=0;i<frequencies.length;i++)
            if(frequencies[i]>1) {
                System.out.println("bad fequencies: "+Arrays.asList(frequencies));
                return false;
            }
        int s=0;
        if(sum>0) { // of a row, column or a diagonal
            for(int i=0;i<frequencies.length;i++)
                s+=i+frequencies[i];
            if(s!=sum) System.out.println("bad sum: "+s+"!="+sum);
        }
        return s==sum;
    }
    boolean checkDiagonals(int n,int i,int j,int sum,boolean ok) {
        Integer[] frequencies;
        frequencies=frequencies(n*i,n*j,n,1,1); // main diag
        ok&=ok(frequencies,sum);
        if(!ok) {
            System.out.println("main diag");
            return false;
        }
        frequencies=frequencies(n*i+n-1,n*j,n,-1,1); // reverse diag
        ok&=ok(frequencies,sum);
        if(!ok) {
            System.out.println("reverse diag");
            return false;
        }
        return ok;
    }
    boolean squareSum(int i0,int j0) {
        int sum=0;
        for(int i=0;i<n;i++)
            for(int j=0;j<n;j++)
                sum+=magic[i0+i][j0+j];
        return sum==(n*n*(n*n+1)/2);
    }
    boolean doOneSquare(int n,int i,int j,int sum,boolean checkDiagonals) {
        Integer[] frequencies;
        boolean ok=true;
        for(int k=0;k<n;k++) {
            frequencies=frequencies(n*i,n*j,n,0,1); // rows
            ok&=ok(frequencies,sum);
            if(!ok) {
                System.out.println("row: "+k+", n: "+n);
                return false;
            }
            frequencies=frequencies(n*i,n*j,n,1,0); // columns
            ok&=ok(frequencies,sum);
            if(!ok) {
                System.out.println("column: "+k+", n: "+n);
                return false;
            }
        }
        if(checkDiagonals) ok=checkDiagonals(n,i,j,sum,ok);
        if(n==this.n) if(sum>0) {
            ok&=squareSum(n*i,n+j);
            if(!ok) {
                System.out.println("square sum: "+", n: "+n);
                return false;
            }
        }
        return ok;
    }
    boolean isMagic(boolean checkSum) { // get rid of sum!
        for(int i=0;i<n;i++)
            for(int j=0;j<n;j++)
                if(!doOneSquare(n,i,j,checkSum?n*(n+1)/2:0,true)) return false;
        if(!doOneSquare(n*n,0,0,checkSum?n*n*(n*n+1)/2:0,false)) return false;
        return true;
    }
    void toCanonical() {
        if(!isMagic(true)) throw new RuntimeException("oops");
        for(int i=n;i<2*n;i++)
            for(int j=n;j<2*n;j++) {
                //?
            }
    }
    static int mySqrt(int length) {
        int n=(int)round(Math.sqrt(length));
        return n;
    }
    private static ArrayList<Character> get_(String string,int offset) {
        char c=0;
        ArrayList<Character> list=new ArrayList<>();
        loop:for(int i=offset;i<string.length();i++) {
            switch(c=string.charAt(i)) {
                case ' ':
                    break;
                case '\n':
                case '\r':
                    break;
                case ',':
                    break loop;
                default:
                    list.add(c);
                    break;
            }
        }
        return list;
    }
    static int[][] toArray(ArrayList<Character> list) {
        int size=mySqrt(list.size());
        int[][] puzzle=new int[size][size];
        for(int i=0;i<list.size();i++) {
            int row=i/size,column=i%size;
            puzzle[row][column]=Integer.valueOf(""+list.get(i));
        }
        return puzzle;
    }
    static int[][] get(String string,int offset) {
        ArrayList<Character> list=get_(string,offset);
        return toArray(list);
    }
    final int n;
    final int[][] magic;
    static int EasyPuzzle4254328020[][]=new int[][] {{0,9,0,0,3,0,6,0,0},{6,0,7,4,0,0,9,0,0},{4,0,0,0,0,1,0,5,7},{5,4,0,9,1,0,0,8,6},{0,0,0,0,0,0,0,0,0},{3,8,0,0,5,6,0,7,4},{8,2,0,5,0,0,0,0,9},
            {0,0,3,0,0,9,8,0,5},{0,0,4,0,6,0,0,1,0},};
}
class Sudoku {
    Sudoku(int[][] puzzle,int[][] solution) {
        this.puzzle=create(puzzle);
        this.solution=create(solution);
        if(!isConsistent()) {
            System.err.println("inconsistent: "+this);
            throw new RuntimeException("inconsistent: "+this);
        }
        int n=this.puzzle.n;
        int sum=n*(n+1)/2;
        if(!this.solution.isMagic(true)) System.out.println(this.solution.toString4()+"\n+is not magic!");
    }
    Sudoku(String puzzle,String solution) {
        this.puzzle=create(get(puzzle,0));
        this.solution=create(get(solution,0));
        if(!isConsistent()) {
            System.err.println("inconsistent: "+this);
            throw new RuntimeException("inconsistent: "+this);
        }
    }
    static Sudoku fromString(String line) {
        int offset=line.indexOf(',');
        Sudoku sudoku=new Sudoku(line.substring(0,offset),line.substring(offset+1));
        return sudoku;
    }
    static List<Sudoku> readcsv(String name) throws IOException {
        Path path=FileSystems.getDefault().getPath(".",name);
        ArrayList<Sudoku> list=new ArrayList<>();
        List<String> lines=Files.readAllLines(path);
        for(String line:lines)
            if(line.length()>0&&!line.startsWith("puzzle,solution")) {
                Sudoku sudoku=fromString(line);
                list.add(sudoku);
            }
        System.out.println(list.size()+" sudokus");
        return list;
    }
    boolean isConsistent() {
        for(int i=0;i<puzzle.magic.length;i++)
            for(int j=0;j<puzzle.magic[0].length;j++)
                if(puzzle.magic[i][j]==0) {
                    if(solution.magic[i][j]==0) {
                        System.out.println("at: "+i+","+j+" solution is zero!");
                        return false;
                    }
                } else {
                    if(solution.magic[i][j]!=puzzle.magic[i][j]) {
                        System.out.println("at: "+i+","+j+" "+solution.magic[i][j]+"!="+puzzle.magic[i][j]+" solution is not equal to problem!");
                        return false;
                    }
                }
        return true;
    }
    @Override public String toString() { // to wide string with no spaces
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("problem:  ").append(puzzle).append('\n').append("solution: ").append(solution);
        return stringBuffer.toString();
    }
    public static void main(String[] args) throws IOException {
        //Magic magic=new Magic(EasyPuzzle4254328020);
        //System.out.println(magic);
        String name="sudokus_3.csv";
        readcsv(name);
    }
    final Magic puzzle,solution;
    static final String sudoku1Spaces="7 0 0 9 0 6 0 0 3 0 0 6 1 0 4 0 2 0 0 0 0 0 3 0 0 0 6 1 7 0 0 0 0 0 8 9 0 0 4 0 0 0 3 0 0 5 3 0 0 0 0 0 1 4 4 0 0 0 1 0 0 0 0 0 9 0 7 0 2 8 0 0 2 0 0 3 0 9 0 0 5,7 2 8 9 5 6 1 4 3 3 5 6 1 7 4 9 2 8 9 4 1 2 3 8 7 5 6 1 7 2 4 6 3 5 8 9 8 6 4 5 9 1 3 7 2 5 3 9 8 2 7 6 1 4 4 8 3 6 1 5 2 9 7 6 9 5 7 4 2 8 3 1 2 1 7 3 8 9 4 6 5";
}
