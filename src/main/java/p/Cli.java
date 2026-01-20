package p;
import java.io.File;
import java.util.*;
import java.util.Map.Entry;
import org.apache.commons.cli.*;
class Cli {
    public static String getMainClassName() {
        //for(final Map.Entry<String,String> entry:System.getenv().entrySet()) {
        for(final Entry<Object,Object> entry:System.getProperties().entrySet()) {
            System.out.println(entry);
            if(entry.getKey() instanceof String&&((String)entry.getKey()).startsWith("JAVA_MAIN_CLASS")) // like JAVA_MAIN_CLASS_13328
                return (String)entry.getValue();
        }
        throw new IllegalStateException("Cannot determine main class.");
    }
    class OptionComparator<T extends Option> implements Comparator<T> {
        private static final String order="lfisnh";
        public int compare(T o1,T o2) {
            return order.indexOf(o1.getOpt())-order.indexOf(o2.getOpt());
        }
    }
    void options(String[] args) {
        options.addOption("h","help",false,"print this message.");
        options.addOption("l","light",false,"use light option.");
        options.addOption("f","file",true,"csv file.");
        options.addOption("i","images",false,"print images and quit.");
        options.addOption("p","pdf",false,"make pdf file and quit.");
        options.addOption("t","title",true,"difficulty/title used in headers.");
        Option start=OptionBuilder.withArgName("s").hasArg().withLongOpt("start").withDescription("start at puzzle <n>").create("s");
        options.addOption(start);
        Option number=OptionBuilder.withArgName("n").hasArg().withLongOpt("number").withDescription("set number of puzzles to <n>").create("n");
        options.addOption(number);
        CommandLineParser parser=new DefaultParser();
        try {
            // parse the command line arguments
            line=parser.parse(options,args);
            if(line.hasOption('h')) {
                help();
            }
            if(line.hasOption('l')) {
                light=true;
                System.out.println("l has option: true");
            }
            if(line.hasOption('f')) {
                String string=line.getOptionValue('f');
                filename=string;
                System.out.println("f has option: "+string);
            }
            if(line.hasOption('i')) {
                printImages=true;
                System.out.println("i has option: true");
            }
            if(line.hasOption('p')) {
                printPdfs=true;
                System.out.println("p has option: true");
            }
            if(line.hasOption('t')) {
                String string=line.getOptionValue('t');
                title=string;
                System.out.println("t has option: "+string);
            }
            if(line.hasOption('s')) {
                String string=line.getOptionValue('s').trim();
                try {
                    this.startingPuzzleIndex=Integer.valueOf(string);
                } catch(NumberFormatException e) {
                    System.err.println("caught: "+e);
                }
                System.out.println("s has option: "+string);
            }
            if(line.hasOption('n')) {
                String string=line.getOptionValue('n').trim();
                try {
                    numberOfPuzzles=Integer.valueOf(string);
                } catch(NumberFormatException e) {
                    System.err.println("caught: "+e);
                }
                System.out.println("n has option: "+string);
            }
        } catch(ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: "+exp.getMessage());
            help();
        }
    }
    static void lookInStackTraces() {
        Map<Thread,StackTraceElement[]> stackTraceMap=Thread.getAllStackTraces();
        for(Thread t:stackTraceMap.keySet()) {
            if("main".equals(t.getName())) {
                StackTraceElement[] mainStackTrace=stackTraceMap.get(t);
                for(StackTraceElement element:mainStackTrace) {
                    System.out.println(element);
                }
            }
        }
    }
    void help() {
        String programName=System.getProperty("sun.java.command");
        if(programName==null) lookInStackTraces();
        if(programName==null) programName="<program name>";
        formatter.setOptionComparator(new OptionComparator<Option>());
        formatter.printHelp(programName,options);
    }
    @Override public String toString() {
        return "MyOptions [light="+light+", printImages="+printImages+", defaultFilename="+defaultFilename+", filename="+filename+", title="+title+", start="+startingPuzzleIndex+", n="+numberOfPuzzles+"]";
    }
    static boolean isGood(String name) {
        File file=new File(name);
        return file.exists()&&file.canRead();
    };
    public static void main(String[] args) {
        Cli myOptions=new Cli();
        myOptions.options(new String[] {"-l -s=3"});
        System.out.println(myOptions.line);
    }
    CommandLine line;
    Options options=new Options();
    HelpFormatter formatter=new HelpFormatter();
    boolean light=false;
    boolean printImages=false;
    boolean printPdfs=false;
    String defaultFilename="sudokus_3.csv";
    String filename=null;
    String title="";
    {
        if(isGood(defaultFilename)) filename=defaultFilename;
    }
    int startingPuzzleIndex=0,numberOfPuzzles=10;
}
