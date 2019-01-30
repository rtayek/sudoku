package p;
import java.io.*;
import java.nio.file.*;
import java.util.*;
class Hack {
    static String[] hack() throws IOException {
        System.out.println("Working Directory = "+System.getProperty("user.dir"));
        String dir="./.settings";
        String name="org.eclipse.jdt.core.prefs";
        File file=new File(dir,name);
        String[] strings=new String[3];
        for(int i=0;i<strings.length;i++)
            strings[i]="";
        if(file.exists()) System.out.println(file.toString()+" exists.");
        else return strings;
        List<String> lines=new ArrayList<>();
        try {
            if(usePath) {
                Path path=FileSystems.getDefault().getPath(dir,name);
                lines=java.nio.file.Files.readAllLines(path);
            } else {
                BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
                for(String line=bufferedReader.readLine();line!=null;line=bufferedReader.readLine())
                    lines.add(line);
                bufferedReader.close();
            }
            int index;
            for(String line:lines) {
                if(line.startsWith("org.eclipse.jdt.core.compiler.compliance")) {
                    index=line.indexOf("=");
                    if(index>0) {
                        System.out.println("compliance: "+line.substring(index+1));
                        strings[0]=line.substring(index+1);
                    }
                }
                if(line.startsWith("org.eclipse.jdt.core.compiler.source=1.8")) {
                    index=line.indexOf("=");
                    if(index>0) {
                        System.out.println("source: "+line.substring(index+1));
                        strings[1]=line.substring(index+1);
                    }
                }
                if(line.startsWith("org.eclipse.jdt.core.compiler.codegen.targetPlatform")) {
                    index=line.indexOf("=");
                    if(index>0) {
                        System.out.println("target: "+line.substring(index+1));
                        strings[2]=line.substring(index+1);
                    }
                }
            }
        } catch(Exception e) {
            System.out.println("caught: "+e);
        }
        return strings;
    }
    public static void main(String[] args) throws IOException {
        hack();
    }
    static boolean usePath;
}