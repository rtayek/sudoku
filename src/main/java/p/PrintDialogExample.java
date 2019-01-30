package p;
import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.UIManager;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
/**
 * Install same printer driver multiple times all configured to print to File.
 * Driver used is Xerox 510 Series PS Wide Format with FreeFlow Accxes <br>
 * Printer#1 Printer with default size 36x126(Xerox) <br> 2.
 * Printer#2 Printer with default size A4 <br>
 * (Xerox)
 * <p/>
 * Print to A4 via both printers, output differs
 * <p/>
 * 3. Printer with default size 36x126(PDFCreator) <br> 4. Printer with default size A4 (HP5500PS3) Print to A4 via <br> both
 * printers, output matches
 */
public class PrintDialogExample implements Printable {
    public int print(Graphics g,PageFormat pf,int page) throws PrinterException {
        if(page>0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }
        Graphics2D g2d=(Graphics2D)g;
        g2d.translate(pf.getImageableX(),pf.getImageableY());
        /* Now we perform our rendering */
        g.drawString("Test the print dialog!",100,100);
        debug(g2d,0,0,pf.getImageableWidth(),pf.getImageableHeight());
        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }
    private void debug(Graphics2D g2,double x,double y,double width,double height) {
        final Stroke oldStroke=g2.getStroke();
        float dash1[]= {10.0f};
        BasicStroke dashed=new BasicStroke(1.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10.0f,dash1,0.0f);
        g2.setStroke(dashed);
        g2.drawRect((int)x,(int)y,(int)width,(int)height);
        g2.setStroke(oldStroke);
    }
    public static void main(String args[]) {
        try {
            String cn=UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(cn); // Use the native L&F
        } catch(Exception cnf) {}
        PrinterJob job=PrinterJob.getPrinterJob();
        PrintRequestAttributeSet aset=new HashPrintRequestAttributeSet();
        PageFormat pf=job.pageDialog(aset);
        job.setPrintable(new PrintDialogExample(),pf);
        boolean ok=job.printDialog(aset);
        if(ok) {
            try {
                job.print(aset);
            } catch(PrinterException ex) {
                /* The job did not successfully complete */
            }
        }
        System.exit(0);
    }
}