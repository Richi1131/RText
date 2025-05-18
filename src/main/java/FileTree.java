import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.io.File;

public class FileTree extends JTree {
    public FileTree(File root) {
        super(createTreeModel(root));
    }
    public static TreeModel createTreeModel(File root) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root.getName());
        addChildren(rootNode, root);
        return new DefaultTreeModel(rootNode);
    }
    private static void addChildren(DefaultMutableTreeNode node, File file) {
        File[] files = file.listFiles();
        if (files == null) return;

        for (File child : files) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child.getName());
            node.add(childNode);
            if (child.isDirectory()) {
                addChildren(childNode, child);
            }
        }
    }
}
