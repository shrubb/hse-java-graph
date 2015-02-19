import javax.lang.model.type.ArrayType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.nio.file.FileSystem;
import java.util.*;

/**
 * Created by shrubb on 12.02.15.
 */

public class Graph {
    private String treeRoot;
    private PrintStream out;
    private HashMap<String, ArrayList<String>> adj;

    public Graph() {
        adj = new HashMap<String, ArrayList<String>>(0);
        out = System.out;
    }

    public int size() {
        return adj.size();
    }

    public boolean exists(String x) {
        return adj.containsKey(x);
    }

    public void add(String x) {
        if (!adj.containsKey(x)) {
            if (adj.size() == 0)
                treeRoot = x;
            adj.put(x, new ArrayList<String>(0));
        }
    }

    public void connect(String x, String y) {
        if (!adj.containsKey(x))
            System.out.printf("No element named " + x + "\n");
        else
            adj.get(x).add(y);
    }

    private String filenameFormatForOutput(String x) {
        String[] arr = x.split(File.separator);
        return arr[arr.length - 1];
    }

    private void _fileSystemWalk(String node, int level) {

        for (int i=0; i<level; ++i) {
            out.print("-  ");
        }

        out.println(filenameFormatForOutput(node));

        for (String next : adj.get(node)) {
            _fileSystemWalk(next, level + 1);
        }
    }

    public void fileSystemWalk(PrintStream stream) {
        out = stream;
        _fileSystemWalk(treeRoot, 1);
    }

    public boolean vertexExists(String x) {
        return adj.containsKey(x);
    }

    public boolean areConnected(String x, String y) {
        return (vertexExists(x) && vertexExists(y) && (adj.get(x).contains(y) || adj.get(y).contains(y)));
    }

    private void _recursiveDelete(String node) {
        if (node == null)
            return;

        for (String next: adj.get(node)) {
            _recursiveDelete(next);
        }
        new File(node).delete();
        adj.remove(node);
    }

    public void deleteVertexAndFile(String query) {
        for (ArrayList<String> list : adj.values()) {
            for (String path : list) {
                if (path.equals(query)) {
                    _recursiveDelete(path);
                    list.remove(path);
                }
            }
        }
    }

    public boolean createNewFile(String query, boolean folder) {

        if (this.check(treeRoot + File.separator + query) != 0) {
            System.out.println("Already exists");
            return false;
        }

        String[] dirs = query.split(File.separator);
        String curr = treeRoot;

        for (int i = 0; i < dirs.length - 1; ++i) {
            curr += File.separator + dirs[i];
            if (adj.get(curr) == null) {
                return false;
            }
        }

        ArrayList<String> children = adj.get(curr);

        if (children == null)
            adj.put(curr, new ArrayList<String>(0));
        adj.get(curr).add(curr + File.separator + dirs[dirs.length - 1]);
        adj.put(curr + File.separator + dirs[dirs.length - 1], new ArrayList<String>(0));

        File file = new File(curr + File.separator + dirs[dirs.length - 1]);

        if (folder) {

            return file.mkdir();
        }
        else {
            try {
                boolean created = file.createNewFile();
                PrintStream stream = new PrintStream(file);
                stream.println("Lorem ipsum");
            }
            catch (Exception e) {
                return false;
            }

            return true;
        }
    }

    public int check(String query) {
        if (query.equals(
                treeRoot.substring(
                    treeRoot.lastIndexOf(File.separator) + 1,
                    treeRoot.length()))) {
            return 2;
        }

        for (ArrayList<String> list : adj.values()) {
            for (String path : list) {
                if (query.equals(
                        path.substring(
                                path.lastIndexOf(File.separator) + 1,
                                path.length()))) {
                    if (new File(path).isDirectory())
                        return 2;
                    else
                        return 1;
                }
            }
        }

        return 0;
    }
}