import java.io.*;
import java.util.ArrayList;

public class Main {

    public static Graph fileSystemGraph;
    public static PrintStream out;
    public static long startTime, endTime;

    public static void main(String[] args) {

        System.out.println("Enter a root path to work with:");
        String rootPath = System.console().readLine();
        File root = new File(rootPath);
        if (!root.exists() || !root.isDirectory()) {
            System.out.println("Sorry, I need an existing directory");
            return;
        }

        fileSystemGraph = new Graph();

        System.out.print("Scanning files...");
        startTime = System.currentTimeMillis();
        addDirToGraph(root, "");
        endTime = System.currentTimeMillis();
        System.out.println("done.");
        System.out.print("Filesystem walk took " + (endTime - startTime) + " ms\n\n");

        System.out.println(
                "Usage:\n" +
                        "write <filename>  : output dir tree to <filename> file\n" +
                        "mkdir <dirname>   : make empty <dirname> dir\n" +
                        "mkfile <filename> : make <filename> file, containing 'Lorem ipsum'\n" +
                        "check <filename>  : print all files named <filename>\n" +
                        "q                 : exit");

        while (true) {
            System.out.print("Command: ");
            String command = System.console().readLine();

            String[] tokens = command.split(" ", 2);

            if (tokens[0].equals("write")) {
                PrintStream stream;
                try {
                    stream = new PrintStream(tokens[1]);
                    System.out.print("Printing to file...");
                    fileSystemGraph.fileSystemWalk(stream);
                    System.out.println("done");
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            } else if (tokens[0].equals("mkdir")) {
                if (fileSystemGraph.createNewFile(tokens[1], true)) {
                    System.out.println("Created");
                }
                else {
                    System.out.println("Error creating directory");
                }
            } else if (tokens[0].equals("mkfile")) {
                if (fileSystemGraph.createNewFile(tokens[1], false)) {
                    System.out.println("Created");
                }
                else {
                    System.out.println("Error creating file");
                }
            } else if (tokens[0].equals("check")) {
                switch (fileSystemGraph.check(tokens[1])) {
                    case 0: { System.out.println(tokens[1] + " doesn't exist"); break; }
                    case 1: { System.out.println(tokens[1] + " is a file"); break; }
                    case 2: { System.out.println(tokens[1] + " is a folder"); break; }
                }
            } else if (tokens[0].equals("q")) {
                break;
            } else {
                System.out.println("Invalid command");
            }
        }
    }

    public static void addDirToGraph(File currDir, String parentFileName) {
        try {
            fileSystemGraph.add(currDir.getCanonicalPath());
            if (!parentFileName.isEmpty()) {
                fileSystemGraph.connect(parentFileName, currDir.getCanonicalPath());
            }

            for (File file : currDir.listFiles()) {
                if (file.isDirectory()) {
                    addDirToGraph(file, currDir.getCanonicalPath());
                }
                else {
                    fileSystemGraph.add(file.getCanonicalPath());
                    fileSystemGraph.connect(currDir.getCanonicalPath(), file.getCanonicalPath());
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
