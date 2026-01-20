package p;
import java.io.*;
import java.util.function.Consumer;
public class Filter {
    static void filter25(String line) {
        Sudoku sudoku=Sudoku.fromString(line);
        int hints=sudoku.hints();
        boolean ok=hints<25;
        if(ok) System.out.println(line);
    }
    static void iterate(BufferedReader buffer,final Consumer<String> consumer) throws IOException {
        for(String line=buffer.readLine();line!=null;line=buffer.readLine()) {
            if(line.length()>0&&!line.startsWith("Puzzle")) {
                consumer.accept(line);
            }
        }
    }
    public static void main(String[] args) throws IOException {
        BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
        iterate(buffer,Filter::filter25);
        buffer.close();
    }
}
