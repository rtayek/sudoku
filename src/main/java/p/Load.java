package p;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
public class Load {
	public static void main(String[] args) {
		for(Object[] data:data()) {
			String name=(String)data[0];
			Path csv=Paths.get((String)data[1]);
			try {
				System.out.println("Loading "+name+" from "+csv);
				Cli options=new Cli();
				options.filename=csv.toString();
				List<Sudoku> sudokus=Main.loadSudokus(options);
			} catch(Exception e) {
				e.printStackTrace();
			}
			System.out.println("------------");
		}
	}
	public static Collection<Object[]> data() {
		return Arrays
				.asList(new Object[][] {{"small built-in set","sudokus_3.csv"},{"qqwing edited","qqexpert1500edited.csv"},});
	}
}
