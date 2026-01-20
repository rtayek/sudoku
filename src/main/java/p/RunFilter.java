package p;
import java.io.*;
import java.util.ArrayList;
public class RunFilter {
    static void foo(String line) {
        ArrayList<Integer> list=new ArrayList<>();
       for(int i=0;i<line.length();i++)
           if(line.charAt(i)==',')
               list.add(i);
       if(list.size()>1)
           System.out.println(line.substring(list.get(1)));
    }
    public static void main(String[] args) throws IOException {
        //InputStreamReader inputStreamReader=new FileReader("kaggle1000000.csv");
        //InputStreamReader inputStreamReader=new FileReader("sudokus_3.csv");
        InputStreamReader inputStreamReader=new FileReader("qqwing60expert.csv");
        BufferedReader buffer=new BufferedReader(inputStreamReader);
        //Filter.iterate(buffer,Filter::filter1);
        Filter.iterate(buffer,line-> {
            foo(line);
            Sudoku sudoku=Sudoku.fromString(line);
            int hints=sudoku.hints();
            boolean ok=hints<25;
            if(ok) System.out.println(line);
        });
        buffer.close();
    }

}
