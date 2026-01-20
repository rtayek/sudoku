package p;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import p.Magic;

class SudokuPainter {
    SudokuPainter(Struct struct) {
        this.struct = struct;
    }

    void paint(Graphics g,int x0,int y0,int index) {
        if(struct.sudokus == null || struct.sudokus.isEmpty()) {
            g.drawString("no puzzle!", 100, 100);
            return;
        }
        paint_(g,x0,y0,index);
        if(struct.paintText) text(g,x0,y0);
    }

    private void paint_(Graphics g,int x0,int y0,int index) {
        Graphics2D g2=(Graphics2D)g;
        System.out.println(struct);
        paintOnePuzzleAndSolution(x0,y0,g2,index);
        if(struct.howManyUp>1) paintOnePuzzleAndSolution(x0+struct.dx2,y0,g2,(index+1)%struct.sudokus.size());
        if(struct.howManyUp>2) paintOnePuzzleAndSolution(x0+2*struct.dx2,y0,g2,(index+2)%struct.sudokus.size());
    }

    private void text(Graphics g,int x0,int y0) {
        java.awt.Rectangle r=g.getClipBounds();
        Font oldFont=g.getFont();
        g.setFont(new Font("TimesRoman",Font.PLAIN,struct.squareSize/2));
        g.drawString("printer: "+struct.printerName+" is painting at: ("+x0+","+y0+")"+", clip bounds: "+r,x0,
                y0+struct.dy-2*struct.squareSize-g.getFont().getSize());
        g.drawString(""+struct,x0,y0+struct.dy-2*struct.squareSize);
        g.setFont(oldFont);
    }

    private void paintOnePuzzleAndSolution(int x0,int y0,Graphics2D g2,int startingIndex) {
        Color oldColor=g2.getColor();
        Sudoku sudoku=struct.sudokus.get(startingIndex);
        paint(sudoku.puzzle,g2,x0,y0);
        g2.setColor(oldColor);
        g2.drawString("Puzzle: "+(startingIndex+1)+" ("+struct.difficulty+")",x0,y0-struct.squareSize/2);
        if(struct.paintText2) text2(g2,x0,y0+struct.squareSize,startingIndex);
        y0+=struct.dy;
        paint(sudoku.solution,g2,x0,y0);
        g2.setColor(oldColor);
    }

    private void paint(Magic magic,Graphics2D g2,int x0,int y0) {
        Stroke old=g2.getStroke();
        int totalDx=struct.n*(struct.heavy+2*struct.light)-struct.heavy/2;
        int totalDy=struct.n*(struct.heavy+2*struct.light)-struct.heavy/2;
        lines(g2,x0,y0,struct.light,struct.heavy,totalDx,totalDy,true);
        lines(g2,x0,y0,struct.light,struct.heavy,totalDx,totalDx,false);
        g2.setStroke(old);
        paintSquares(magic,g2,x0,y0,struct.light,struct.heavy);
    }

    private void lines(Graphics2D g2,int x0,int y0,int light,int heavy,int totalDx,int totalDy,boolean vertical) {
        for(int i=0;i<=struct.size;i++) {
            boolean isHeavy=i%struct.n==0;
            final int strokeWidth=isHeavy?heavy:light;
            Stroke stroke=new BasicStroke(strokeWidth);
            g2.setStroke(stroke);
            int dd=d(light,heavy,i,struct.n)-strokeWidth/2;
            if(vertical) g2.drawLine(x0+dd+i*struct.squareSize,y0,x0+dd+i*struct.squareSize,y0+struct.size*struct.squareSize+totalDy);
            else g2.drawLine(x0,y0+dd+i*struct.squareSize,x0+struct.size*struct.squareSize+totalDx,y0+dd+i*struct.squareSize);
        }
    }

    private void paintSquares(Magic magic,Graphics g,int x0,int y0,int light,int heavy) {
        int fontSize=struct.squareSize*5/6;
        for(int i=0;i<struct.n*struct.n;i++) {
            for(int j=0;j<struct.n*struct.n;j++) {
                g.setFont(new Font("TimesRoman",Font.PLAIN,fontSize));
                int x=x0+i*struct.squareSize,y=y0+j*struct.squareSize;
                x+=d(light,heavy,i,struct.n);
                y+=d(light,heavy,j,struct.n);
                int index=magic.magic[j][i];
                Color color=index!=0?struct.colors[index-1]:Main.white;
                g.setColor(color);
                if(struct.dark) {
                    if(struct.circle) {
                        int dx=4;
                        g.drawOval(x+dx/2,y+dx/2,struct.squareSize-4,struct.squareSize-4);
                        g.fillOval(x+dx/2,y+dx/2,struct.squareSize-4,struct.squareSize-4);
                        g.setColor(Color.black);
                    } else {
                        g.fillRect(x,y,struct.squareSize,struct.squareSize);
                        g.setColor(Color.black);
                    }
                }
                if(index!=0) {
                    String string=""+index;
                    Rectangle rectangle=new Rectangle(x,y,struct.squareSize,struct.squareSize);
                    Main.centerString(g,rectangle,string,g.getFont());
                }
            }
        }
    }

    private void text2(Graphics g,int x0,int y0,int index) {
        Rectangle r=g.getClipBounds();
        Font oldFont=g.getFont();
        g.setFont(new Font("TimesRoman",Font.PLAIN,struct.squareSize/2));
        String extra=struct.sudokus.get(index).extra;
        g.drawString("info: "+extra,x0,y0+struct.dy-2*struct.squareSize-g.getFont().getSize());
        g.setFont(oldFont);
    }

    private static int d(final int light,final int heavy,int i,int n) {
        int delta=0;
        delta+=i%n*light;
        delta+=i/n*(heavy+(n-1)*light);
        return delta;
    }

    private final Struct struct;
}
