package p;
import java.awt.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.Border;
class Grid {
    private static void smallGrid(JPanel smallPanel,int n,int squareSize,Magic magic,int i0,int j0) {
        smallPanel.setLayout(new GridLayout(n,n));
        int fontSize=squareSize*5/6;
        for(int k=0;k<n*n;k++) {
            int i=k/n,j=k%n;
            JButton button=new JButton();
            button.setMargin(new Insets(2,2,2,2));
            button.setFont(new Font("TimesRoman",Font.PLAIN,fontSize));
            //button.setPreferredSize(dimension);
            int index;
            if(magic!=null) index=magic.magic[i0+i][j0+j];
            else index=random.nextInt(colors.length+1);
            Color color=index!=0?colors[index-1]:Color.white;
            if(index!=0) button.setText(""+index);
            button.setBackground(color);
            smallPanel.add(button);
        }
    }
    private static void grid(JPanel bigPanel,int n,Magic magic) {
        bigPanel.setLayout(new GridLayout(n,n));
        for(int k=0;k<n*n;k++) {
            int i=k/n,j=k%n;
            JPanel smallPanel=new JPanel();
            smallPanel.setPreferredSize(new Dimension(3*squareSize+2*light,3*squareSize+2*light));
            Border blackline=BorderFactory.createLineBorder(Color.black,light);
            smallPanel.setBorder(blackline);
            bigPanel.add(smallPanel);
            smallGrid(smallPanel,3,squareSize,magic,3*i,3*j);
        }
    }
    static JFrame grid(Sudoku sudoku) {
        JFrame frame=new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel bigPanel=new JPanel();
        bigPanel.setPreferredSize(new Dimension(9*squareSize+6*heavy,9*squareSize+6*heavy));
        JPanel containerPanel=new JPanel();
        Border blackline=BorderFactory.createLineBorder(Color.black,heavy);
        bigPanel.setBorder(blackline);
        Magic magic=sudoku!=null?sudoku.solution:null;
        grid(bigPanel,3,magic);
        containerPanel.add(bigPanel);
        frame.getContentPane().add(containerPanel);
        frame.pack();
        frame.setVisible(true);
        return frame;
    }
    public static void main(String[] args) throws Exception {
        grid(null);
    }
    static final int light=2,heavy=6;
    static final int squareSize=36;
    static Random random=new Random();
    static final Color[] colors=new Color[] {new Color(0,0,176),new Color(0,74,2),new Color(50,205,50),new Color(245,248,53),new Color(196,0,3),new Color(255,105,0),new Color(102,255,245),
            new Color(75,0,97),new Color(162,109,255)};
}
