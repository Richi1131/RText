import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.util.Arrays;

public class FileTree extends JTree {
    public FileTree(RTFrame frame, File root) {
        super(createTreeModel(root));
        this.addTreeSelectionListener(e -> {
            TreePath path = e.getPath();
            if (path.getLastPathComponent() instanceof DefaultMutableTreeNode node)
                if (node.getUserObject() instanceof File file) {
                    frame.loadFile(file);
                }
        });
    }
    public static TreeModel createTreeModel(File root) {
        RFile rRoot = new RFile(root);
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rRoot);
        addChildren(rootNode, rRoot);
        return new DefaultTreeModel(rootNode);
    }
    private static void addChildren(DefaultMutableTreeNode node, RFile file) {
        RFile[] files = Arrays.stream(file.listFiles()).map(RFile::new).toArray(RFile[]::new);
        if (files == null) return;

        for (RFile child : files) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
            node.add(childNode);
            if (child.isDirectory()) {
                addChildren(childNode, child);
            }
        }
    }
    private static class RFile extends File {
        public RFile(File file) {
            super(file.getAbsolutePath());
        }
        public RFile(String pathname) {
            super(pathname);
        }
        @Override
        public String toString() {
            return getName();
        }
    }
}
