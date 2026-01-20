package p;

import java.awt.Graphics;
import java.util.Objects;
import javax.swing.JComponent;

class SudokuCanvas extends JComponent {
    private static final long serialVersionUID = 1L;

    SudokuCanvas(Struct struct) {
        this.struct = Objects.requireNonNull(struct, "struct is required");
        setPreferredSize(struct.dimension);
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(struct.sudokus == null || struct.sudokus.isEmpty()) {
            g.drawString("no puzzle!", 100, 100);
            return;
        }
        struct.paint(g, struct.dx0, struct.dy0, struct.index);
    }

    final Struct struct;
}
