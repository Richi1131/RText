import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Arrays;

public class FileTree extends JTree {
    public FileTree(RTFrame frame, File root) {
        super(createTreeModel(root));
        this.addMouseListener(new MouseListener() {
                                  @Override
                                  public void mouseClicked(MouseEvent e) {
                                      if (e.getClickCount() != 2) {
                                          return;
                                      }
                                      TreePath path = FileTree.this.getPathForLocation(e.getX(), e.getY());
                                      if (path.getLastPathComponent() instanceof DefaultMutableTreeNode node) {
                                          if (node.getUserObject() instanceof File file) {
                                              frame.loadFile(file);
                                          }
                                      }
                                  }

                                  @Override
                                  public void mousePressed(MouseEvent e) {

                                  }

                                  @Override
                                  public void mouseReleased(MouseEvent e) {

                                  }

                                  @Override
                                  public void mouseEntered(MouseEvent e) {

                                  }

                                  @Override
                                  public void mouseExited(MouseEvent e) {

                                  }
                              }
        );
    }
    public static TreeModel createTreeModel(File root) {
        RFile rRoot = new RFile(root);
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rRoot);
        addChildren(rootNode, rRoot);
        return new DefaultTreeModel(rootNode);
    }
    private static void addChildren(DefaultMutableTreeNode node, RFile file) {
        RFile[] files = Arrays.stream(file.listFiles()).map(RFile::new).toArray(RFile[]::new);

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
