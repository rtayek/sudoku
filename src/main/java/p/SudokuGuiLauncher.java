package p;

import java.util.List;

public class SudokuGuiLauncher {
    public static void main(String[] args) {
        Cli myOptions=new Cli();
        myOptions.options(args);
        if(myOptions.line.hasOption('h')) return;
        List<Sudoku> sudokus=Main.loadSudokus(myOptions);
        Main.showWindow(myOptions,sudokus);
    }
}
