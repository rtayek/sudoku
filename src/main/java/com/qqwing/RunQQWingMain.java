package com.qqwing;
import java.io.*;
public class RunQQWingMain {
    public static void main(String[] argv) {
        String[] difficulties=new String[] {"simple","easy","intermediate","expert"};
        PrintStream old=System.out;
        for(String difficulty:difficulties) {
            System.out.println(difficulty);
            String name=difficulty+".csv";
            File file=new File(name);
            try {
                PrintStream printStream=new PrintStream(new FileOutputStream(file));
                System.setOut(printStream);
                QQWingMain.main(new String[] {"--count-solutions","--difficulty",difficulty,"--generate","12","--solution","--csv"});
                printStream.close();
                System.setOut(old);
            } catch(FileNotFoundException e) {
                e.printStackTrace(System.err);
            }
        }
        System.setOut(old);
    }
}
