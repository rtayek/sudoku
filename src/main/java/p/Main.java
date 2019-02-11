package p;
import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.swing.*;
import static p.Magic.*;
import static p.Sudoku.*;
import static p.Main.Buttons.*;
public class Main extends JFrame implements /*Printable,*/ActionListener {
    enum Buttons {
        Print,Postscript,Image,Left,Right,Number,Style,Colors;
    }
    // make a pdf
    // use circle, with number
    public Main(Cli myOptions,java.util.List<Sudoku> sudokus) {
        Paper paper=new Paper();
        toString(paper);
        properties=properties(propertiesFile);
        int size=sudokus!=null&&sudokus.size()>0?sudokus.get(0).puzzle.magic.length:9;
        Color[] colors=getColors(size,properties);
        String string=properties.getProperty("squareSizeForPrinting");
        squareSizeForPrinting=Integer.valueOf(string); // do this somewhere else?
        if(myOptions.printImages) {
            Properties properties=properties(propertiesFile);
            string=properties.getProperty("squareSizeForImage");
            int aSquareSize=Integer.valueOf(string);
            s=new Struct(sudokus,aSquareSize,colors);
            s.writeImages(myOptions.start,myOptions.n);
            return;
        } else if(myOptions.printPdfs) {
            System.out.println("pdfs");
            Properties properties=properties(propertiesFile);
            size=sudokus!=null&&sudokus.size()>0?sudokus.get(0).puzzle.magic.length:9;
            // maybe need one for pdf files
            string=properties.getProperty("squareSizeForPrinting"); // maybe need one just for pdfs?
            int aSquareSize=Integer.valueOf(string);
            s=new Struct(sudokus,aSquareSize,colors);
            System.out.println("n: "+myOptions.n);
            s.pages=myOptions.n;
            print();
            return;
        }
        string=properties.getProperty("squareSizeForScreen");
        squareSizeForScreen=Integer.valueOf(string);
        s=new Struct(sudokus,squareSizeForScreen,colors);
        colorButtons=new JButton[s.colors.length];
        setJMenuBar(createMenuBar());
        setSize(s.dimension);
        setLayout(null);
        addButtons();
        setSizeAndText();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setupColorsDialog();
        if(sudokus!=null&&sudokus.size()>0) gridOfButtons(sudokus);
    }
    void addButtons() { // put these in a separate frame so they don't print
        Insets insets=getInsets();
        int left=insets.left+400,top=5+insets.top+100;
        int y=top,dy=25;
        JButton button;
        for(Buttons b:Buttons.values()) {
            button=new JButton(b.toString());
            EnumButtonsInFrame[b.ordinal()]=button;
            add(button);
            //buttonPanel.add(button);
            button.setName(b.toString());
            button.setBounds(left,y,100,20);
            button.addActionListener(this);
            y+=dy;
        }
        y=top;
        for(Buttons b:Buttons.values()) {
            button=new JButton(b.toString());
            EnumButtonsInPanel[b.ordinal()]=button;
            buttonPanel.add(button);
            button.setName(b.toString());
            button.setBounds(left,top,100,20);
            button.addActionListener(this);
            y+=dy;
        }
        add(colorsPanel); // not visible???
        colorsPanel.setBounds(insets.left,insets.top+22*s.squareSize,500,20);
        add(buttonPanel);
        buttonPanel.setBounds(left+20,top+200,100,10*dy);
    }
    void gridOfButtons(java.util.List<Sudoku> sudokus) {
        Sudoku sudoku=sudokus.get(s.index);
        JFrame frame=Grid.grid(sudoku);
        Rectangle rectangle=frame.getBounds();
        rectangle.x+=800;
        frame.setBounds(rectangle);
        System.out.println("grid frame: "+frame.getBounds());
    }
    static Color fromString(String string) {
        try {
            int offset=0;
            string=string.substring(offset);
            int start=string.indexOf('=')+1;
            int end=string.indexOf(',');
            int r=Integer.valueOf(string.substring(start,end));
            offset=end+1;
            string=string.substring(offset);
            start=string.indexOf('=')+1;
            end=string.indexOf(',');
            int g=Integer.valueOf(string.substring(start,end));
            offset=end+1;
            string=string.substring(offset);
            start=string.indexOf('=')+1;
            end=string.indexOf(']');
            int b=Integer.valueOf(string.substring(start,end));
            //offset=end+1;
            //string=string.substring(offset);
            return new Color(r,g,b);
        } catch(NumberFormatException e) {
            System.err.println("caught: "+e+" with string");
            return null;
        }
    }
    static Color[] getColors(int size,Properties properties) {
        SortedSet<String> colorKeys=new TreeSet<>();
        for(Object key:properties.keySet())
            if(((String)key).startsWith("color")) colorKeys.add((String)key);
        Color[] colors=null;
        if(colorKeys.size()<size) {
            System.err.println(colorKeys.size()+","+size+" not enough colors from properties file!");
            colors=defaultColors;
        } else {
            colors=new Color[size];
            for(String key:colorKeys) {
                char c=key.charAt(key.length()-1);
                int index=Integer.valueOf(c-'0')-1;
                String value=properties.getProperty(key);
                colors[index]=fromString(value);
            }
        }
        return colors;
    }
    void setSizeAndText() {
        if(s.sudokus!=null&&s.sudokus.size()>0) {
            EnumButtonsInFrame[Number.ordinal()].setText(""+s.index);
            EnumButtonsInPanel[Number.ordinal()].setText(""+s.index);
        }
    }
    void setColorsInButtons() {
        for(int i=0;i<s.colors.length;i++)
            colorButtons[i].setBackground(s.colors[i]);
    }
    void setupColorsDialog() {
        ActionListener actionListener=new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                Object source=e.getSource();
                if(source instanceof JButton) {
                    JButton button=(JButton)source;
                    for(int i=0;i<s.colors.length;i++) {
                        String name=""+(i+1);
                        if(button.getName().equals(name)) {
                            JColorChooser cc=new JColorChooser(s.colors[i]);
                            final int ii=i;
                            Color newColor=JColorChooser.showDialog(null,"Choose Color "+(ii+1),s.colors[ii]);
                            System.out.println("got new color: "+newColor);
                            if(newColor!=null) {
                                s.colors[ii]=newColor;
                                setColorsInButtons();
                                colorsPanel.invalidate();
                                colorsPanel.repaint();
                                invalidate();
                                repaint();
                                colorsDialog.setVisible(false);
                                writeProperties(properties,propertiesFile);
                            }
                        }
                    }
                } else System.out.println("not a JButton!");
            }
        };
        for(int i=0;i<s.colors.length;i++) {
            String name=""+(i+1);
            colorButtons[i]=new JButton(name);
            add(colorButtons[i]);
            colorButtons[i].setName(name);
            colorButtons[i].addActionListener(actionListener);
            colorsPanel.add(colorButtons[i]);
        }
        setColorsInButtons();
        colorsDialog.getContentPane().add(colorsPanel);
    }
    void print2DtoStream(String name) {
        System.out.println("entry print2DtoStream");
        /* Use the pre-defined flavor for a Printable from an InputStream */
        DocFlavor flavor=DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        /* Specify the type of the output stream */
        String psMimeType=DocFlavor.BYTE_ARRAY.POSTSCRIPT.getMimeType();
        /* Locate factory which can export a GIF image stream as Postscript */
        StreamPrintServiceFactory[] factories=StreamPrintServiceFactory.lookupStreamPrintServiceFactories(flavor,psMimeType);
        if(factories.length==0) {
            System.err.println("No suitable factories");
            System.exit(0);
        }
        try {
            /* Create a file for the exported postscript */
            FileOutputStream fos=new FileOutputStream(name);
            /* Create a Stream printer for Postscript */
            StreamPrintService sps=factories[0].getPrintService(fos);
            /* Create and call a Print Job */
            DocPrintJob pj=sps.createPrintJob();
            PrintRequestAttributeSet aset=new HashPrintRequestAttributeSet();
            aset.add(OrientationRequested.PORTRAIT);
            Doc doc=new SimpleDoc(this,flavor,null);
            pj.print(doc,aset);
            fos.close();
        } catch(PrintException pe) {
            System.err.println(pe);
        } catch(IOException ie) {
            System.err.println(ie);
        }
        System.out.println("entry print2DtoStream");
    }
    class Printer implements Printable {
        //https://stackoverflow.com/questions/25283110/how-to-set-printer-margin-in-java
        @Override public int print(Graphics g,PageFormat pf,int page) throws PrinterException {
            System.out.println("in print(), page: "+page+", pages: "+s.pages);
            if(page>s.pages-1) return NO_SUCH_PAGE;
            System.out.println("page format: "+Main.toString(pf));
            Paper paper=pf.getPaper();
            System.out.println("paper from PageFormat:"+Main.toString(paper));
            Graphics2D g2d=(Graphics2D)g;
            g2d.translate(pf.getImageableX(),pf.getImageableY());
            s.setSquareSizeEtc(squareSizeForPrinting);
            int x0=s.dx0,y0=s.dy0;
            System.out.println("using dx0 and dy0 from struct.");
            System.out.println("x0, y0: "+x0+","+y0);
            s.paint(g,x0,y0);
            s.text(g,x0,y0);
            s.setSquareSizeEtc(squareSizeForScreen);
            s.index=(s.index+s.howManyUp)%s.sudokus.size();
            return PAGE_EXISTS;
        }
    }
    public static String toString(Paper paper) {
        double ix=paper.getImageableX();
        double iy=paper.getImageableY();
        double iw=paper.getImageableWidth();
        double ih=paper.getImageableHeight();
        double w=paper.getWidth();
        double h=paper.getHeight();
        String string="ix "+ix+", iy "+iy+", iw "+iw+", ih "+ih+", w "+w+", h "+h;
        return string;
    }
    public static String toString(PageFormat pageFormat) {
        double ix=pageFormat.getImageableX();
        double iy=pageFormat.getImageableY();
        double iw=pageFormat.getImageableWidth();
        double ih=pageFormat.getImageableHeight();
        double w=pageFormat.getWidth();
        double h=pageFormat.getHeight();
        int orientation=pageFormat.getOrientation();
        String string="ix "+ix+", iy "+iy+", iw "+iw+", ih "+ih+", w "+w+", h "+h+", orientation: "+orientation;
        return string;
    }
    void print() {
        System.out.println("in static Printer::print()");
        HashPrintRequestAttributeSet set=new HashPrintRequestAttributeSet();
        set.add(new MediaPrintableArea(.25f,.25f,8.f,10.5f,MediaPrintableArea.INCH));
        if(s.howManyUp==3) set.add(OrientationRequested.LANDSCAPE);
        set.add(OrientationRequested.PORTRAIT);
        PrinterJob job=PrinterJob.getPrinterJob();
        job.setPrintable(new Printer());
        System.out.println(set);
        boolean ok=job.printDialog(set);
        if(ok) try {
            String name=job.getPrintService().getName();
            System.out.println("printing on: "+name);
            s.printerName=name;
            job.print(set);
        } catch(PrinterException e) {
            System.err.println("caight: "+e);
        }
    }
    public void actionPerformed(ActionEvent e) {
        Object source=e.getSource();
        if(source instanceof JButton) {
            JButton button=(JButton)source;
            switch(Buttons.valueOf(button.getName())) {
                case Left:
                    if(s.sudokus!=null&&s.sudokus.size()>0) {
                        s.index=(s.index-s.howManyUp+s.sudokus.size())%s.sudokus.size();
                        setSizeAndText();
                        invalidate();
                        repaint();
                    }
                    break;
                case Right:
                    if(s.sudokus!=null&&s.sudokus.size()>0) {
                        s.index=(s.index+s.howManyUp)%s.sudokus.size();
                        setSizeAndText();
                        invalidate();
                        repaint();
                    }
                    break;
                case Print:
                    print();
                    break;
                case Postscript:
                    print2DtoStream(s.index+".ps");
                    break;
                case Image:
                    s.writeImage(s.index);
                    break;
                case Style:
                    s.dark=!s.dark;
                    invalidate();
                    repaint();
                    break;
                case Colors:
                    colorsDialog.pack();
                    colorsDialog.setVisible(true);
                    break;
                default:
                    System.out.println("default: "+button.getName());
                    break;
            }
        } else System.out.println("not a JButton!");
    }
    public JMenuBar createMenuBar() {
        JMenuBar menuBar=new JMenuBar();
        JMenu menu=new JMenu("Options");
        menu.setMnemonic(KeyEvent.VK_O);
        menu.getAccessibleContext().setAccessibleDescription("Options menu");
        //menuBar.add(menu);
        return menuBar;
    }
    static void centerString(Graphics g,Rectangle rectangle,String string,Font font) {
        Rectangle2D r2D=font.getStringBounds(string,frc);
        int width=(int)Math.round(r2D.getWidth());
        int height=(int)Math.round(r2D.getHeight());
        int x=(int)Math.round(r2D.getX());
        int y=(int)Math.round(r2D.getY());
        int dx=(rectangle.width-width)/2-x;
        int dy=(rectangle.height-height)/2-y;
        g.setFont(font);
        g.drawString(string,rectangle.x+dx,rectangle.y+dy);
    }
    static void printDiffs(String string,java.util.List<Integer> list) {
        System.out.println(string);
        for(int i=0;i<list.size()-1;i++)
            System.out.println(i+" "+list.get(i+1)+"-"+list.get(i)+", diff: "+(list.get(i+1)-list.get(i)));
    }
    @Override public void paint(Graphics g) {
        super.paint(g);
        System.out.println("current square size is: "+s.squareSize);
        Insets insets=getInsets(); // maybe use these. they may be better than guessing?
        System.out.println("insets in frame: "+insets);
        if(s.sudokus!=null&&s.sudokus.size()>0) {
            System.out.println("adding to insets.");
            int x0=insets.left+s.dx0,y0=insets.top+s.dy0;
            System.out.println("x0, y0: "+x0+","+y0);
            s.printerName="screen";
            s.paint(g,x0,y0);
            s.text(g,x0,y0);
        } else g.drawString("no puzzle!",100,100);
    }
    static class Struct {
        Struct(java.util.List<Sudoku> sudokus,int squareSize,Color[] colors) {
            this.sudokus=sudokus;
            size=sudokus.get(index).puzzle.magic.length;
            if(colors.length<size) System.err.println(colors.length+" is not enough colors!");
            this.colors=colors;
            n=mySqrt(size);
            if(howManyUp==3) {
                width=1080;
                height=(int)Math.round(width*8.5/11);
            } else {
                height=1080;
                width=(int)Math.round(height*8.5/11);
            }
            dimension=new Dimension(width,height);
            setSquareSizeEtc(squareSize);
        }
        void setSquareSizeEtc(int newSquareSize) {
            int old=newSquareSize;
            if(howManyUp==3) {
                newSquareSize=5*newSquareSize/6;
                System.out.println("shrinking square size to: "+newSquareSize+" (old was: "+old+")");
            }
            this.squareSize=newSquareSize;
            dx0=2*newSquareSize;
            dy=500*newSquareSize/defaultSquareSizeForScreen;
            //if(howManyUp==3) dy=400;
            dx2=11*newSquareSize;
            dy0=3*newSquareSize;
            if(howManyUp==3) dy0=2*newSquareSize;
        }
        static int d(final int light,final int heavy,int i,int n) {
            int delta=0;
            delta+=i%n*light;
            delta+=i/n*(heavy+(n-1)*light);
            return delta;
        }
        void lines(Graphics2D g2,final int x0,final int y0,final int light,final int heavy,int totalDx,int totalDy,boolean vertical) {
            for(int i=0;i<=size;i++) {
                boolean isHeavy=i%n==0;
                final int strokeWidth=isHeavy?heavy:light;
                Stroke stroke=new BasicStroke(strokeWidth);
                g2.setStroke(stroke);
                int dd=d(light,heavy,i,n)-strokeWidth/2;
                if(vertical) g2.drawLine(x0+dd+i*squareSize,y0,x0+dd+i*squareSize,y0+size*squareSize+totalDy);
                else g2.drawLine(x0,y0+dd+i*squareSize,x0+size*squareSize+totalDx,y0+dd+i*squareSize);
            }
        }
        void verticalLines(Graphics2D g2,final int x0,final int y0,final int light,final int heavy,int totalDy) {
            int dx;
            java.util.List<Integer> list=new ArrayList<>();
            for(int i=0;i<=size;i++) {
                boolean isHeavy=i%n==0;
                final int strokeWidth=isHeavy?heavy:light;
                Stroke stroke=new BasicStroke(strokeWidth);
                g2.setStroke(stroke);
                dx=d(light,heavy,i,n);
                dx-=strokeWidth/2;
                Color old=g2.getColor();
                g2.setColor(Color.red);
                g2.drawLine(x0+dx+i*squareSize,y0,x0+dx+i*squareSize,y0+size*squareSize+totalDy);
                g2.setColor(old);
                if(paintGuidlines&&i%n==0) {
                    stroke=new BasicStroke(1);
                    g2.setStroke(stroke);
                    g2.drawLine(x0+dx+i*squareSize,y0-50,x0+dx+i*squareSize,y0+size*squareSize+totalDy+50);
                    if(i==0) {
                        Color old2=g2.getColor();
                        g2.setColor(Color.red);
                        g2.drawLine(x0,y0-100,x0,y0+size*squareSize+totalDy+100);
                        g2.setColor(old2);
                    }
                }
            }
            //System.out.println("lines");
            //printDiffs("x",list);
        }
        void horizontalLines(Graphics2D g2,final int x0,final int y0,final int light,final int heavy,int totalDx) {
            int dy;
            java.util.List<Integer> list=new ArrayList<>();
            for(int i=0;i<=size;i++) { // horizontal
                boolean isHeavy=i%n==0;
                final int strokeWidth=isHeavy?heavy:light;
                Stroke stroke=new BasicStroke(strokeWidth);
                g2.setStroke(stroke);
                dy=d(light,heavy,i,n);
                dy-=strokeWidth/2;
                g2.drawLine(x0,y0+dy+i*squareSize,x0+size*squareSize+totalDx,y0+dy+i*squareSize);
                list.add(y0+dy+i*squareSize);
                if(paintGuidlines&&i%n==0) {
                    stroke=new BasicStroke(1);
                    g2.setStroke(stroke);
                    g2.drawLine(x0-50,y0+dy+i*squareSize,x0+size*squareSize+totalDx+50,y0+dy+i*squareSize);
                    if(i==0) {
                        Color old=g2.getColor();
                        g2.setColor(Color.red);
                        g2.drawLine(x0-100,y0,x0+size*squareSize+totalDx+100,y0);
                        g2.setColor(old);
                    }
                }
            }
            //System.out.println("lines");
            //printDiffs("y",list);
        }
        void paintSquares(Magic magic,Graphics g,final int x0,final int y0,final int light,final int heavy) {
            java.util.List<Integer> list=new ArrayList<>();
            int fontSize=squareSize*5/6;
            for(int i=0;i<n*n;i++)
                for(int j=0;j<n*n;j++) {
                    g.setFont(new Font("TimesRoman",Font.PLAIN,fontSize));
                    int x=x0+i*squareSize,y=y0+j*squareSize;
                    x+=d(light,heavy,i,n);
                    y+=d(light,heavy,j,n);
                    int index=magic.magic[j][i]; // was i,j
                    Color color=index!=0?colors[index-1]:white;
                    g.setColor(color);
                    if(dark) {
                        if(circle) {
                            int dx=4;
                            g.drawOval(x+dx/2,y+dx/2,squareSize-4,squareSize-4);
                            g.fillOval(x+dx/2,y+dx/2,squareSize-4,squareSize-4);
                            g.setColor(Color.black);
                        } else {
                            g.fillRect(x,y,squareSize,squareSize);
                            g.setColor(Color.black);
                        }
                    }
                    if(index!=0) {
                        String string=""+index;
                        Rectangle rectangle=new Rectangle(x,y,squareSize,squareSize);
                        centerString(g,rectangle,string,g.getFont());
                    }
                    //fontSize++;
                    if(i==0) list.add(y);
                }
            //System.out.println("squares");
            //printDiffs("squares",list);
        }
        void paint(Magic magic,Graphics2D g2,final int x0,final int y0) {
            Stroke old=g2.getStroke();
            boolean useNew=true;
            int totalDx=n*(heavy+2*light)-heavy/2;
            int totalDy=n*(heavy+2*light)-heavy/2;
            if(useNew) {
                lines(g2,x0,y0,light,heavy,totalDx,totalDy,true);
                lines(g2,x0,y0,light,heavy,totalDx,totalDx,false);
            } else {
                verticalLines(g2,x0,y0,light,heavy,totalDy);
                horizontalLines(g2,x0,y0,light,heavy,totalDx);
            }
            g2.setStroke(old);
            paintSquares(magic,g2,x0,y0,light,heavy);
        }
        void paintOnePUzzleAndSolution(int x0,int y0,Sudoku sudoku,Graphics2D g2,int n) {
            System.out.println("paint one puzzle at: "+x0+","+y0);
            Color oldColor=g2.getColor();
            paint(sudoku.puzzle,g2,x0,y0);
            g2.setColor(oldColor);
            g2.drawString("Puzzle: "+n,x0,y0-squareSize/2);
            y0+=dy;
            System.out.println("paint one solution at: "+x0+","+y0);
            paint(sudoku.solution,g2,x0,y0);
            g2.setColor(oldColor);
        }
        void paint(Graphics g,int x0,int y0) {
            System.out.println("painting at: ("+x0+","+y0+")");
            Sudoku sudoku=sudokus.get(index); // maybe i don't need to pass inindex?
            Graphics2D g2=(Graphics2D)g;
            paintOnePUzzleAndSolution(x0,y0,sudoku,g2,(index+1));
            if(howManyUp>1) {
                sudoku=sudokus.get((index+1)%sudokus.size());
                System.out.println("paint the second one.");
                paintOnePUzzleAndSolution(x0+dx2,y0,sudoku,g2,((index+1)%sudokus.size()+1));
            }
            if(howManyUp>2) {
                sudoku=sudokus.get((index+2)%sudokus.size());
                System.out.println("paint the third one.");
                paintOnePUzzleAndSolution(x0+2*dx2,y0,sudoku,g2,((index+2)%sudokus.size()+1));
            }
        }
        void text(Graphics g,int x0,int y0) {
            Rectangle r=g.getClipBounds();
            Font oldFont=g.getFont();
            g.setFont(new Font("TimesRoman",Font.PLAIN,squareSize/2));
            g.drawString("printer: "+printerName+" is painting at: ("+x0+","+y0+")"+", square size: "+squareSize+", dy="+dy,x0+dx2/4,y0+dy-2*squareSize-g.getFont().getSize());
            g.drawString("clip bounds: "+r,x0+dx2/4,y0+dy-2*squareSize);
            g.setFont(oldFont);
        }
        static void writeImages(BufferedImage bi,String name) throws IOException {
            if(!ImageIO.write(bi,"PNG",new File(name+".png"))) System.out.println(name+" no writer for png!");
            if(!ImageIO.write(bi,"JPEG",new File(name+".jpg"))) System.out.println(name+"no writer for jpeg!");
            if(!ImageIO.write(bi,"gif",new File(name+".gif"))) System.out.println(name+" no writer for gif!");
            if(!ImageIO.write(bi,"BMP",new File(name+".bmp"))) System.out.println(name+" no writer for bmp!");
        }
        void writeImage(int index) {
            try {
                BufferedImage bi=new BufferedImage(dimension.width,dimension.height,BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2=bi.createGraphics();
                g2.setColor(Color.black);
                int x0=dx0,y0=dy0;
                printerName="image";
                paint(g2,x0,y0);
                text(g2,x0,y0);
                writeImages(bi,""+index);
            } catch(IOException ie) {
                ie.printStackTrace();
            }
        }
        void writeImages(int start,int n) {
            for(int i=start;i<start+n;i++) {
                writeImage(i);
                if(twoUp) ++i;
            }
        }
        // control shift p goes to matching brace
        String printerName;
        final Color[] colors;
        final int height;
        final int width;
        final Dimension dimension;
        boolean dark=true;
        boolean circle=true;
        boolean paintGuidlines=false;
        final int n,size;
        Integer index=0;
        int squareSize;
        final int light=2,heavy=6;
        int dy;
        boolean twoUp=true;
        int howManyUp=3;
        int dx0,dy0,dx2;
        int pages=1;
        java.util.List<Sudoku> sudokus;
    }
    static void writeProperties(Properties properties,File file) {
        Writer writer=null;
        try {
            writer=new FileWriter(file);
            properties.store(writer,"written by ppgram.");
            writer.close();
            System.out.println("wrote properties");
            System.out.println(properties);
        } catch(IOException e) {
            System.err.println("caught: "+e+" writing properties file!");
        } finally {
            if(writer!=null) try {
                writer.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
    static Properties properties(File file) {
        System.out.println("defaultProperties: "+defaultProperties);
        Properties properties=new SortedProperties();
        properties.putAll(defaultProperties);
        if(file.exists()&&file.canRead()) {
            System.out.println("reading properties");
            Reader reader=null;
            try {
                reader=new FileReader(file);
                properties.load(reader);
                reader.close();
                System.out.println("read: "+properties);
            } catch(FileNotFoundException e) {
                ;
            } catch(IOException e) {
                System.err.println("caught: "+e);
            } finally {
                if(reader!=null) try {
                    reader.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        } else writeProperties(properties,file);
        return properties;
    }
    public static void printers() {
        PrintService[] printServices=PrinterJob.lookupPrintServices();
        for(PrintService printService:printServices) {
            String name=printService.getName();
            System.out.println("Name = "+name);
        }
    }
    public static void main(String[] args) {
        Cli myOptions=new Cli();
        myOptions.options(args);
        System.out.println(myOptions.line);
        if(myOptions.line.hasOption('h')) return;
        java.util.List<Sudoku> sudokus=null;
        if(myOptions.filename!=null) try {
            sudokus=readcsv(myOptions.filename);
        } catch(IOException e) {
            System.out.println("caught: "+e);
        }
        if(sudokus==null||sudokus.size()==0) {
            sudokus=new ArrayList<>();
            for(int i=0;i<defaultSudokus.length;i++) {
                Sudoku sudoku=Sudoku.fromString(defaultSudokus[i]);
                sudokus.add(sudoku);
                break;
            }
        }
        new Main(myOptions,sudokus);
    }
    //final JButton number;
    final JButton[] EnumButtonsInFrame=new JButton[Buttons.values().length];
    final JButton[] EnumButtonsInPanel=new JButton[Buttons.values().length];
    JButton[] colorButtons;
    final JPanel colorsPanel=new JPanel();
    final JPanel buttonPanel=new JPanel();
    final Struct s;
    Properties properties;
    final Random random=new Random();
    int squareSizeForPrinting=defaultSquareSizeForPrinting;
    //int squareSizeForImage=defaultSquareSizeForImage;
    int squareSizeForScreen=defaultSquareSizeForScreen;
    final JDialog colorsDialog=new JDialog(this,Colors.toString(),true);
    static final File propertiesFile=new File("sudoku.properties");
    static final Color white=new Color(255,255,255);
    static Properties defaultProperties=new Properties();
    {
        for(int i=0;i<defaultColors.length;i++)
            defaultProperties.put("color"+(i+1),defaultColors[i].toString());
        defaultProperties.put("squareSizeForPrinting",""+defaultSquareSizeForPrinting);
        defaultProperties.put("squareSizeForImage",""+defaultSquareSizeForImage);
        defaultProperties.put("squareSizeForScreen",""+defaultSquareSizeForScreen);
    }
    static final Color[] oldColors=new Color[] {Color.black,Color.red,Color.pink,Color.orange,Color.yellow,Color.green,Color.magenta,Color.cyan,Color.blue,};
    static final Color[] defaultColors=new Color[] {new Color(0,0,176),new Color(0,74,2),new Color(50,205,50),new Color(245,248,53),new Color(196,0,3),new Color(255,105,0),new Color(102,255,245),
            new Color(75,0,97),new Color(162,109,255)};
    // https://www.websudoku.com
    static final int defaultSquareSizeForPrinting=24;
    static final int defaultSquareSizeForImage=42;
    static final int defaultSquareSizeForScreen=36;
    static final FontRenderContext frc=new FontRenderContext(null,true,true);
    static String[] defaultSudokus=new String[] {
            "7 0 0 9 0 6 0 0 3 0 0 6 1 0 4 0 2 0 0 0 0 0 3 0 0 0 6 1 7 0 0 0 0 0 8 9 0 0 4 0 0 0 3 0 0 5 3 0 0 0 0 0 1 4 4 0 0 0 1 0 0 0 0 0 9 0 7 0 2 8 0 0 2 0 0 3 0 9 0 0 5,7 2 8 9 5 6 1 4 3 3 5 6 1 7 4 9 2 8 9 4 1 2 3 8 7 5 6 1 7 2 4 6 3 5 8 9 8 6 4 5 9 1 3 7 2 5 3 9 8 2 7 6 1 4 4 8 3 6 1 5 2 9 7 6 9 5 7 4 2 8 3 1 2 1 7 3 8 9 4 6 5",
            "8 1 0 0 5 0 0 9 0 0 0 0 9 0 2 0 8 0 0 0 9 0 4 0 0 0 0 0 6 0 7 2 0 0 5 0 5 0 1 8 0 6 3 0 7 0 7 0 0 3 5 0 1 0 0 0 0 0 1 0 2 0 0 0 8 0 2 0 3 0 0 0 0 2 0 0 8 0 0 7 3,8 1 2 3 5 7 4 9 6 7 3 4 9 6 2 5 8 1 6 5 9 1 4 8 7 3 2 9 6 3 7 2 1 8 5 4 5 4 1 8 9 6 3 2 7 2 7 8 4 3 5 6 1 9 3 9 7 5 1 4 2 6 8 1 8 6 2 7 3 9 4 5 4 2 5 6 8 9 1 7 3",
            "1 0 0 0 0 0 0 7 0 0 0 0 0 4 6 0 0 5 0 0 8 2 0 1 0 0 0 0 3 0 0 8 5 0 0 0 0 7 5 0 0 0 9 0 0 0 0 0 0 0 0 4 0 8 0 0 0 0 0 4 0 0 0 0 0 9 0 0 0 7 0 3 0 5 0 0 0 0 0 6 1,1 4 3 8 5 9 6 7 2 7 9 2 3 4 6 1 8 5 5 6 8 2 7 1 3 9 4 4 3 6 9 8 5 2 1 7 8 7 5 4 1 2 9 3 6 9 2 1 6 3 7 4 5 8 3 8 7 1 6 4 5 2 9 6 1 9 5 2 8 7 4 3 2 5 4 7 9 3 8 6 1",
            "0 0 6 0 2 0 1 0 0 0 8 0 0 0 0 0 9 0 0 4 7 0 8 6 3 2 0 0 0 2 0 9 0 0 0 0 7 0 1 8 0 4 5 0 2 0 0 0 0 1 0 9 0 0 0 1 9 2 4 0 8 3 0 0 2 0 0 0 0 0 1 0 0 0 8 0 6 0 2 0 0,9 5 6 7 2 3 1 4 8 2 8 3 4 5 1 7 9 6 1 4 7 9 8 6 3 2 5 5 3 2 6 9 7 4 8 1 7 9 1 8 3 4 5 6 2 8 6 4 5 1 2 9 7 3 6 1 9 2 4 5 8 3 7 4 2 5 3 7 8 6 1 9 3 7 8 1 6 9 2 5 4",
            "0 2 0 0 0 6 1 0 3 6 0 0 0 0 0 0 5 0 0 0 0 9 0 5 2 0 0 0 9 3 0 7 0 0 0 0 4 0 0 3 0 9 0 0 7 0 0 0 0 6 0 3 9 0 0 0 6 2 0 3 0 0 0 0 4 0 0 0 0 0 0 9 9 0 7 4 0 0 0 3 0,5 2 9 8 4 6 1 7 3 6 3 8 7 2 1 9 5 4 7 1 4 9 3 5 2 8 6 8 9 3 5 7 2 4 6 1 4 6 1 3 8 9 5 2 7 2 7 5 1 6 4 3 9 8 1 8 6 2 9 3 7 4 5 3 4 2 6 5 7 8 1 9 9 5 7 4 1 8 6 3 2",
            "0 0 0 0 6 1 0 0 0 0 0 0 3 4 0 5 0 0 6 0 0 0 5 0 0 3 2 8 0 0 0 0 0 0 7 0 2 5 3 0 0 0 6 9 8 0 9 0 0 0 0 0 0 1 1 4 0 0 3 0 0 0 0 0 0 2 0 8 6 0 0 0 0 0 0 1 7 0 4 0 0,3 2 5 8 6 1 7 4 9 9 7 1 3 4 2 5 8 6 6 8 4 9 5 7 1 3 2 8 1 6 5 9 3 2 7 4 2 5 3 7 1 4 6 9 8 4 9 7 6 2 8 3 5 1 1 4 9 2 3 5 8 6 7 7 3 2 4 8 6 9 1 5 5 6 8 1 7 9 4 2 3",
            "0 3 0 0 0 9 0 7 4 0 7 0 0 0 0 0 0 0 0 0 0 0 4 2 0 9 0 0 0 0 3 0 0 0 0 6 0 2 8 0 0 0 1 3 0 3 0 0 0 0 1 0 0 0 0 0 0 2 8 0 0 0 0 9 0 6 0 0 0 0 8 7 7 0 0 6 0 0 0 0 0,2 3 1 8 5 9 6 7 4 4 7 9 1 3 6 8 5 2 8 6 5 7 4 2 3 9 1 5 1 7 3 2 8 9 4 6 6 2 8 9 7 4 1 3 5 3 9 4 5 6 1 7 2 8 1 4 3 2 8 7 5 6 9 9 5 6 4 1 3 2 8 7 7 8 2 6 9 5 4 1 3",
            "0 0 3 0 0 2 8 0 0 2 0 5 1 0 0 4 0 9 0 0 0 3 4 0 0 0 0 7 0 0 0 0 1 0 0 6 0 4 0 0 9 0 0 7 0 5 0 0 7 0 0 0 0 8 0 0 0 0 7 6 0 0 0 6 0 9 0 0 3 7 0 4 0 0 1 4 0 0 2 0 0,4 1 3 9 6 2 8 5 7 2 6 5 1 8 7 4 3 9 9 8 7 3 4 5 6 1 2 7 3 8 5 2 1 9 4 6 1 4 2 6 9 8 3 7 5 5 9 6 7 3 4 1 2 8 3 2 4 8 7 6 5 9 1 6 5 9 2 1 3 7 8 4 8 7 1 4 5 9 2 6 3",
            "0 0 0 2 0 4 0 0 0 0 4 0 0 9 1 6 0 0 0 0 5 0 0 0 1 2 0 3 0 0 7 0 6 0 4 0 0 0 6 0 1 0 3 0 0 0 1 0 9 0 8 0 0 6 0 3 4 0 0 0 5 0 0 0 0 9 0 2 5 0 3 0 0 0 0 8 0 3 0 0 0,1 7 3 2 6 4 8 9 5 2 4 8 5 9 1 6 7 3 6 9 5 3 8 7 1 2 4 3 8 2 7 5 6 9 4 1 9 5 6 4 1 2 3 8 7 4 1 7 9 3 8 2 5 6 8 3 4 6 7 9 5 1 2 7 6 9 1 2 5 4 3 8 5 2 1 8 4 3 7 6 9",
            "0 2 0 4 0 0 0 0 6 0 0 0 0 2 0 5 0 0 0 0 4 0 0 5 0 8 0 0 0 0 0 0 0 0 0 8 9 0 0 6 0 0 0 1 0 0 3 0 0 9 0 0 5 0 0 0 0 3 0 6 0 0 0 0 0 0 2 4 0 0 7 1 0 8 0 0 0 0 0 0 2,5 2 9 4 8 7 1 3 6 8 6 1 9 2 3 5 4 7 3 7 4 1 6 5 2 8 9 1 5 6 7 3 4 9 2 8 9 4 8 6 5 2 7 1 3 2 3 7 8 9 1 6 5 4 4 1 2 3 7 6 8 9 5 6 9 5 2 4 8 3 7 1 7 8 3 5 1 9 4 6 2",};
    private static final long serialVersionUID=1L;
}