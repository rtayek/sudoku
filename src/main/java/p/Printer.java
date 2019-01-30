package p;
import java.awt.*;
import java.awt.print.*;
import java.util.function.Consumer;
import javax.print.PrintService;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
class Printer implements Printable {
    Printer(Consumer<Graphics> painter) {
        this.painter=painter;
    }
    final Consumer<Graphics> painter;
    static void paintLargeRectange(Graphics g) {
        Graphics2D g2=(Graphics2D)g;
        Rectangle r=g.getClipBounds();
        Stroke old=g2.getStroke();
        int w=10;
        Stroke stroke=new BasicStroke(w);
        g2.setStroke(stroke);
        g.drawRoundRect(r.x+w/2,r.y+w/2,r.width-w,r.height-w,r.width/5,r.height/5);
        g.drawString("clip bounds: "+r,100,100);
        g2.setStroke(old);
    }
    @Override public int print(Graphics g,PageFormat pf,int page) throws PrinterException {
        System.out.println("in print(), page= "+page);
        if(page>0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }
        Main.printPageFormat(pf);
        Paper paper=pf.getPaper();
        System.out.println("paper from PageFormat:");
        Main.printPaper(paper);
        Graphics2D g2d=(Graphics2D)g;
        g2d.translate(pf.getImageableX(),pf.getImageableY());
        paintLargeRectange(g);
        return PAGE_EXISTS;
    }
    static void print(Consumer<Graphics> painter) {
        System.out.println("in static print(Consumer<Graphics>)");
        HashPrintRequestAttributeSet aset=new HashPrintRequestAttributeSet();
        aset.add(new MediaPrintableArea(.25f,.25f,8.f,10.5f,MediaPrintableArea.INCH));
        aset.add(OrientationRequested.PORTRAIT);
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new Printer(painter));
        job.setJobName("sudoku");
        //job.pageDialog(aset);
        // not using aset yest!
        boolean ok = job.printDialog(aset);
        if (ok) {
            try {
                 job.print();
            } catch (PrinterException e) {
                System.err.println("caight: "+e);
            }
        }
    }
    public static void main(String[] args) {
        System.out.println("calling main!");
        MainForPrinter.main(args);
    }

}
