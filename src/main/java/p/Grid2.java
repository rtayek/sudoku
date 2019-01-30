package p;
import javax.swing.*;
public class Grid2 {
    Grid2(Sudoku sudoku) {
        this.sudoku=sudoku;
    }
    Grid2() {
        this(null);
    }
    JPanel buildSmallPanel(JPanel comtainer) {
        final JPanel panel=new JPanel();
        return panel;
    }
    void run() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel containerPanel=new JPanel();
        JPanel smallPanel=new JPanel();
        frame.getContentPane().add(containerPanel);
        frame.pack();
        frame.setVisible(true);
    }
    public static void main(String[] args) throws Exception {
        new Grid2().run();
    }
    Sudoku sudoku;
    JFrame frame=new JFrame();
    int squareSize=36;
    final int light=2,heavy=6;
}
