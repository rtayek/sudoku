package p;
import static p.Main.*;
public class RunMain {
    public static void main(String[] args) {
        printers();
        //Main.main(new String[] {"-h"});
        Main.main(new String[] {"-p","-s 2","-n 8"});
    }
}
