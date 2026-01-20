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
import p.Sudoku;
import static p.Magic.*;
import static p.Main.Buttons.*;
import static p.Sudoku.*;
public class Main extends JFrame implements /*Printable,*/ActionListener {
    enum Buttons { Print, Postscript, Image, Left, Right, Number, Style, Colors; }
    // make a pdf
    // use circle, with number
    public Main(Cli myOptions,java.util.List<Sudoku> sudokus) {
        System.out.println("options: "+myOptions);
        Paper paper=new Paper();
        toString(paper);
        properties=properties(propertiesFile);
        int size=sudokus!=null&&sudokus.size()>0?sudokus.get(0).puzzle.magic.length:9;
        Color[] colors=getColors(size,properties);
        String string=properties.getProperty("squareSizeForPrinting");
        squareSizeForPrinting=Integer.valueOf(string); // do this somewhere else?
        String title=myOptions.title!=null?myOptions.title.trim():"";
        if(myOptions.printImages) {
            Properties properties=properties(propertiesFile);
            string=properties.getProperty("squareSizeForImage");
            int aSquareSize=Integer.valueOf(string);
            s=new Struct(sudokus,aSquareSize,colors,title);
            s.writeImages(myOptions.startingPuzzleIndex,myOptions.numberOfPuzzles);
            return;
        } else if(myOptions.printPdfs) {
            System.out.println("pdfs");
            Properties properties=properties(propertiesFile);
            size=sudokus!=null&&sudokus.size()>0?sudokus.get(0).puzzle.magic.length:9;
            // maybe need one for pdf files
            string=properties.getProperty("squareSizeForPrinting"); // maybe need one just for pdfs?
            int aSquareSize=Integer.valueOf(string);
            // title was myOptions.title
            s=new Struct(sudokus,aSquareSize,colors,title);
            System.out.println("number of puzzles: "+myOptions.numberOfPuzzles);
            print(myOptions.startingPuzzleIndex,myOptions.numberOfPuzzles);
            return;
        }
        string=properties.getProperty("squareSizeForScreen");
        squareSizeForScreen=Integer.valueOf(string);
        s=new Struct(sudokus,squareSizeForScreen,colors,title);
        canvas=new SudokuCanvas(s);
        controls=new MainControls(this,s,properties,propertiesFile);
        setJMenuBar(MainMenu.create(this));
        setSize(s.dimension);
        setLayout(new BorderLayout());
        add(canvas,BorderLayout.CENTER);
        add(controls,BorderLayout.EAST);
        controls.updateIndexDisplay(s.index);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        if(sudokus!=null&&sudokus.size()>0) gridOfButtons(sudokus);
    }
    void gridOfButtons(java.util.List<Sudoku> sudokus) {
        Sudoku sudoku=sudokus.get(s.index);
        JFrame frame=Grid.grid(sudoku);
        Rectangle rectangle=frame.getBounds();
        rectangle.x+=800;
        frame.setBounds(rectangle);
        //System.out.println("grid frame: "+frame.getBounds());
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
        for(Object key:properties.keySet()) if(((String)key).startsWith("color")) colorKeys.add((String)key);
        Color[] colors=null;
        if(colorKeys.size()<size) {
            System.err.println(colorKeys.size()+","+size+" not enough colors from properties file!");
            colors=defaultColors;
        } else {
            colors=new Color[size];
            for(String key:colorKeys) {
                char c=key.charAt(key.length()-1);
                int colorIndex=Integer.valueOf(c-'0')-1;
                String value=properties.getProperty(key);
                colors[colorIndex]=fromString(value);
            }
        }
        return colors;
    }
    void print2DtoStream(String name) {
        System.out.println("entry print2DtoStream");
        /* Use the pre-defined flavor for a Printable from an InputStream */
        DocFlavor flavor=DocFlavor.SERVICE_FORMATTED.PRINTABLE;
        /* Specify the type of the output stream */
        String psMimeType=DocFlavor.BYTE_ARRAY.POSTSCRIPT.getMimeType();
        /* Locate factory which can export a GIF image stream as Postscript */
        StreamPrintServiceFactory[] factories=StreamPrintServiceFactory.lookupStreamPrintServiceFactories(flavor,
                psMimeType);
        if(factories.length==0) { System.err.println("No suitable factories"); System.exit(0); }
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
    static int pages(int puzzles,int howManyUp) { return (int)Math.ceil(puzzles/(double)howManyUp); }
    class Printer implements Printable {
        Printer(int start,int n) { this.start=start; this.n=n; pages=pages(n,s.howManyUp); index=start; }
        //https://stackoverflow.com/questions/25283110/how-to-set-printer-margin-in-java
        @Override public int print(Graphics g,PageFormat pf,int page) throws PrinterException {
            // may he called more than once for each page!
            System.out.println("------------------------------------------");
            System.out.println("in print(), page: "+page+" of "+pages+", pages: ");
            if(page>pages-1) return NO_SUCH_PAGE;
            System.out.println("page format: "+Main.toString(pf));
            Paper paper=pf.getPaper();
            System.out.println("paper from PageFormat:"+Main.toString(paper));
            Graphics2D g2d=(Graphics2D)g;
            g2d.translate(pf.getImageableX(),pf.getImageableY());
            s.setSquareSizeEtc(squareSizeForPrinting);
            System.out.println("paint screen");
            int x0=s.dx0,y0=s.dy0;
            System.out.println("s.index: "+s.index);
            System.out.println("using dx0 and dy0 from struct.");
            s.dx2+=s.squareSize; // not sure why we need this!
            s.paint(g,x0,y0,index);
            s.dx2-=s.squareSize;
            s.setSquareSizeEtc(s.squareSize0);
            s.printerName="screen";
            //index=(s.index+s.howManyUp)%s.sudokus.size();
            index=start+page*s.howManyUp;
            System.out.println("------------------------------------------");
            return PAGE_EXISTS;
        }
        final int start,n; // puzzle indices, not pages!
        final int pages;
        int index;
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
    void print(int start,int n) {
        System.out.println("in static Printer::print()");
        HashPrintRequestAttributeSet set=new HashPrintRequestAttributeSet();
        set.add(new MediaPrintableArea(.25f,.25f,8.f,10.5f,MediaPrintableArea.INCH));
        set.add(s.howManyUp==3?OrientationRequested.LANDSCAPE:OrientationRequested.PORTRAIT);
        PrinterJob job=PrinterJob.getPrinterJob();
        job.setPrintable(new Printer(start,n));
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
                        controls.updateIndexDisplay(s.index);
                        canvas.repaint();
                    }
                    break;
                case Right:
                    if(s.sudokus!=null&&s.sudokus.size()>0) {
                        s.index=(s.index+s.howManyUp)%s.sudokus.size();
                        controls.updateIndexDisplay(s.index);
                        canvas.repaint();
                    }
                    break;
                case Print:
                    print(s.index,s.howManyUp);
                    break;
                case Postscript:
                    print2DtoStream(s.index+".ps");
                    break;
                case Image:
                    s.writeImage(s.index);
                    break;
                case Style:
                    s.dark=!s.dark;
                    canvas.repaint();
                    break;
                case Colors:
                    controls.showColorDialog();
                    break;
                default:
                    System.out.println("default: "+button.getName());
                    break;
            }
        } else System.out.println("not a JButton!");
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
    static void writeProperties(Properties properties,File file) {
        Writer writer=null;
        try {
            writer=new FileWriter(file);
            properties.store(writer,"written by program.");
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
    //found a bunch here. found a program to scrape the site.
    //http://www.menneske.no/sudoku/
    //https://github.com/apauliuc/sudoku-scraper
    static java.util.List<Sudoku> loadSudokus(Cli myOptions) {
        java.util.List<Sudoku> sudokus=null;
        if(myOptions.filename!=null) try {
            System.out.println("file name is: '"+myOptions.filename+"'");
            sudokus=readcsv(myOptions.filename);
            System.out.println(sudokus.size()+" sudokus.");
        } catch(IOException e) {
            System.out.println("xx caught: "+e);
        }
        if(sudokus==null||sudokus.size()==0) {
            System.err.println("no sudokus found, using builins");
            sudokus=new ArrayList<>();
            for(int i=0;i<defaultSudokus.length;i++) {
                Sudoku sudoku=Sudoku.fromString(defaultSudokus[i]);
                sudokus.add(sudoku);
                break;
            }
        }
        return sudokus;
    }
    public static void showWindow(Cli myOptions, java.util.List<Sudoku> sudokus) {
        if(sudokus==null||sudokus.isEmpty()) throw new IllegalArgumentException("need puzzles to show");
        javax.swing.SwingUtilities.invokeLater(() -> new Main(myOptions,sudokus));
    }
    public static void main(String[] args) {
        Cli myOptions=new Cli();
        myOptions.options(args);
        if(myOptions.line.hasOption('h')) return;
        java.util.List<Sudoku> sudokus=loadSudokus(myOptions);
        System.out.println("sudokus loaded: "+sudokus.size());
        Histogram histogram=new Histogram(20,20,40);
        for(Sudoku sudoku:sudokus) histogram.add(sudoku.hints());
        System.out.println("hints: "+histogram);
        showWindow(myOptions,sudokus);
    }
    //final JButton number;
    final Struct s;
    SudokuCanvas canvas;
    MainControls controls;
    Properties properties;
    final Random random=new Random();
    int squareSizeForPrinting=defaultSquareSizeForPrinting;
    //int squareSizeForImage=defaultSquareSizeForImage;
    int squareSizeForScreen=defaultSquareSizeForScreen;
    static final File propertiesFile=new File("sudoku.properties");
    static final Color white=new Color(255,255,255);
    static Properties defaultProperties=new Properties();
    {
        for(int i=0;i<defaultColors.length;i++) defaultProperties.put("color"+(i+1),defaultColors[i].toString());
        defaultProperties.put("squareSizeForPrinting",""+defaultSquareSizeForPrinting);
        defaultProperties.put("squareSizeForImage",""+defaultSquareSizeForImage);
        defaultProperties.put("squareSizeForScreen",""+defaultSquareSizeForScreen);
    }
    static final Color[] oldColors=new Color[] {Color.black,Color.red,Color.pink,Color.orange,Color.yellow,Color.green,
            Color.magenta,Color.cyan,Color.blue,};
    static final Color[] defaultColors=new Color[] {new Color(196,0,3),new Color(255,105,0),new Color(245,248,53),
            new Color(50,205,50),new Color(0,74,2),new Color(102,255,245),new Color(0,0,176),new Color(162,109,255),
            new Color(75,0,97),};
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
