import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
        menuBar.getMenu(0).getItem(2).addActionListener(e->save());
        menuBar.getMenu(0).getItem(3).addActionListener(e->saveAs());

        this.add(textArea);
        this.add(menuBar, BorderLayout.NORTH);
        this.setSize(1200, 800);

    }

    public void open() {
        JFileChooser fc = new JFileChooser();
        fc.showDialog(this, "Open");
        try (FileReader fr = new FileReader(fc.getSelectedFile())) {
            textArea.read(fr, "");
            currentPath = fc.getSelectedFile().getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void save() {
        if (currentPath == null) {
            saveAs();
            return;
        }
        File file = new File(currentPath);
        try (FileWriter fw = new FileWriter(file)) {
            textArea.write(fw);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void saveAs() {
        JFileChooser fc = new JFileChooser();
        fc.showDialog(this, "Save As");
        currentPath = fc.getSelectedFile().getAbsolutePath();
        save();
    }
}
