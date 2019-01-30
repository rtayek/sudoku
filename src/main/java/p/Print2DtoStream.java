package p;
/*
 * Copyright (c) 2001, Oracle and/or its affiliates. All rights reserved.
 *
 * This software is the proprietary information of Oracle.
 * Use is subject to license terms.
 *
 */
import java.io.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.print.*;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
public class Print2DtoStream implements Printable {
    public Print2DtoStream() {
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
            FileOutputStream fos=new FileOutputStream("out.ps");
            /* Create a Stream printer for Postscript */
            StreamPrintService sps=factories[0].getPrintService(fos);
            /* Create and call a Print Job */
            DocPrintJob pj=sps.createPrintJob();
            PrintRequestAttributeSet aset=new HashPrintRequestAttributeSet();
            Doc doc=new SimpleDoc(this,flavor,null);
            pj.print(doc,aset);
            fos.close();
        } catch(PrintException pe) {
            System.err.println(pe);
        } catch(IOException ie) {
            System.err.println(ie);
        }
    }
    public int print(Graphics g,PageFormat pf,int pageIndex) {
        if(pageIndex==0) {
            //FontRenderContext frc=new FontRenderContext(null,true,true);
            System.out.println("print page: "+pageIndex);
            Graphics2D g2d=(Graphics2D)g;
            g2d.translate(pf.getImageableX(),pf.getImageableY());
            g2d.setColor(Color.black);
            g2d.drawString("example string",250,250);
            g2d.fillRect(0,0,200,200);
            return Printable.PAGE_EXISTS;
        } else {
            System.out.println("no such page: "+pageIndex);
            return Printable.NO_SUCH_PAGE;
        }
    }
    public static void main(String args[]) {
        Print2DtoStream sp=new Print2DtoStream();
    }
}
