package p;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import p.Magic;
import p.Sudoku;

class Struct {
    Struct(List<Sudoku> sudokus, int squareSize, Color[] colors, String difficulty) {
        this.sudokus = sudokus;
        size = sudokus.get(index).puzzle.magic.length;
        if (colors.length < size) System.err.println(colors.length + " is not enough colors!");
        this.colors = colors;
        n = Magic.mySqrt(size);
        if (howManyUp == 3) {
            width = 1080;
            height = (int) Math.round(width * 8.5 / 11);
        } else {
            height = 1080;
            width = (int) Math.round(height * 8.5 / 11);
        }
        dimension = new Dimension(width, height);
        squareSize0 = squareSize;
        setSquareSizeEtc(squareSize);
        this.difficulty = difficulty;
        painter = new SudokuPainter(this);
    }

    void setSquareSizeEtc(int newSquareSize) {
        if (howManyUp == 3) {
            System.out.println("new square size: " + newSquareSize);
            newSquareSize = 5 * newSquareSize / 6;
            System.out.println("shrinking square size to: " + newSquareSize + " for 3 up (old was: " + squareSize + ")");
        }
        this.squareSize = newSquareSize;
        dx0 = 2 * newSquareSize;
        dy = 500 * newSquareSize / Main.defaultSquareSizeForScreen;
        dx2 = 11 * newSquareSize;
        dy0 = 3 * newSquareSize;
        if (howManyUp == 3) dy0 = 2 * newSquareSize;
    }

    static int d(final int light, final int heavy, int i, int n) {
        int delta = 0;
        delta += i % n * light;
        delta += i / n * (heavy + (n - 1) * light);
        return delta;
    }

    void paint(Graphics g, int x0, int y0, int index) {
        painter.paint(g, x0, y0, index);
    }

    void writeImages(int startingPuzzleIndex, int numberOfPuzzles) {
        for (int i = startingPuzzleIndex; i < startingPuzzleIndex + numberOfPuzzles; i += howManyUp) { writeImage(i); }
    }

    void writeImage(int index) {
        try {
            BufferedImage bi = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bi.createGraphics();
            g2.setColor(Color.black);
            printerName = "image";
            System.out.println("------------------------------------------");
            paint(g2, dx0, dy0, index);
            System.out.println("------------------------------------------");
            saveImageVariants(bi, "" + index);
            printerName = "screen";
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    static void saveImageVariants(BufferedImage bi, String name) throws IOException {
        if (!ImageIO.write(bi, "PNG", new File(name + ".png"))) System.out.println(name + " no writer for png!");
        if (!ImageIO.write(bi, "JPEG", new File(name + ".jpg"))) System.out.println(name + "no writer for jpeg!");
        if (!ImageIO.write(bi, "gif", new File(name + ".gif"))) System.out.println(name + " no writer for gif!");
        if (!ImageIO.write(bi, "BMP", new File(name + ".bmp"))) System.out.println(name + " no writer for bmp!");
    }

    @Override
    public String toString() {
        return "Struct [" + "printerName=" + printerName
                + ", height=" + height + ", width=" + width
                + ", n=" + n + ", size=" + size + ", index=" + index + ", squareSize=" + squareSize
                + ", dy=" + dy + ", howManyUp=" + howManyUp + ", dx0=" + dx0 + ", dy0=" + dy0 + ", dx2=" + dx2 + ", pages=" + pages + "]";
    }

    String difficulty;
    String printerName;
    final Color[] colors;
    final int height;
    final int width;
    final Dimension dimension;
    boolean dark = true;
    boolean circle = true;
    boolean paintGuidlines = false;
    final int n, size;
    Integer index = 0;
    final int squareSize0;
    int squareSize;
    final int light = 2, heavy = 6;
    int dy;
    int howManyUp = 3;
    int dx0, dy0, dx2;
    int pages = 1;
    List<Sudoku> sudokus;
    final SudokuPainter painter;
    boolean paintText2 = true;
    boolean paintText = false;
}
