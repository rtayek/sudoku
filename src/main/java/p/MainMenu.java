package p;

import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

class MainMenu {
    static JMenuBar create(Main main) {
        JMenuBar menuBar = new JMenuBar();
        JMenu options = new JMenu("Options");
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> main.dispose());
        options.add(exit);
        menuBar.add(options);
        return menuBar;
    }
}
