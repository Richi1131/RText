import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;

public class RTFrame extends JFrame {
    private JTextArea textArea;
    private JMenuBar menuBar;

    private String currentPath;

    public RTFrame() {
        textArea = new JTextArea();

        menuBar = new JMenuBar();
        menuBar.add(new JMenu("File"));
        menuBar.getMenu(0).add("New");
        menuBar.getMenu(0).add("Open");
        menuBar.getMenu(0).add("Save");
        menuBar.getMenu(0).add("Save As");

        menuBar.getMenu(0).getItem(1).addActionListener(e->open());

        this.add(textArea);
        this.add(menuBar, BorderLayout.NORTH);
        this.setSize(1200, 800);

    }

    public void open() {
        JFileChooser fc = new JFileChooser();
        fc.showDialog(this, "Open");
        try (FileReader fr = new FileReader(fc.getSelectedFile())) {
            textArea.read(fr, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
