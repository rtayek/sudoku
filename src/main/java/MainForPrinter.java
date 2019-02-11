
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainForPrinter extends JFrame implements KeyListener {
    MainForPrinter() { // just draws a bug rectangle
        insets=getInsets();
        System.out.println("insets: "+insets);
        setSize(dimension); // fiddle with this using insets?
        setLayout(null);
        setUndecorated(true);
        addKeyListener(this);
        setFocusable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //pack(); // collapses!
        setVisible(true);
    }
    @Override public void keyTyped(KeyEvent e) {
        int modifiers=e.getModifiersEx();
        String text=InputEvent.getModifiersExText(modifiers);
        char c=e.getKeyChar();
        System.out.println(c+" "+text);
        if(c=='p') Printer.print(Printer::paintLargeRectange);
    }
    @Override public void keyPressed(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void paint(Graphics g) {
        super.paint(g);
        Printer.paintLargeRectange(g);
    }
    public static void main(String[] args) {
        new MainForPrinter();
    }
    int height=1080;
    int width=(int)Math.round(height*8.5/11);
    Insets insets;
    Dimension dimension=new Dimension(width,height);
    private static final long serialVersionUID=1L;
}
