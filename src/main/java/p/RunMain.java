package p;
import static p.Main.*;
// scraper is in ../printg/
public class RunMain {
    public static void main(String[] args) {
        printers();
        String[] difficulties=new String[] {"simple","easy","intermediate","expert"};
        //Main.main(new String[] {"-h"});
        //Main.main(new String[] {"-p","-s 2","-n 8"});
        if(true) {
            //Main.main(new String[] {"-p","-file="+"simple.csv","-n=9"});
            //Main.main(new String[] {"-p","-file="+"expert30.csv","-n=30"});
            //Main.main(new String[] {"-file="+"kaggle1000000.csv"});
            //Main.main(new String[] {"-p","-file="+"sudokus_3_hard.csv","-n=60"}); // small number of hints
            //Main.main(new String[] {"-p","-file="+"qqwing60expert.csv","-n=60"}); 
            //Main.main(new String[] {"-p","-file="+"../sudokuqq/qqexpert1500edited.csv","-n=1500"}); 
            // the -s is the index to start with, so it will be 100.
            // the -n is how many to do.
            Main.main(new String[] {"-s=99","-p","-file="+"qqexpert5000.csv","-n=1500","-t=expert"});
        } else for(String difficulty:difficulties) {
            String filename=difficulty+".csv";
            System.out.println(filename);
            Main.main(new String[] {"-t="+difficulty,"-p","-f="+filename,"-n=12"});
        }
    }
}
