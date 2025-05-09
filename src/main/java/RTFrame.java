import javax.swing.*;
import java.awt.*;

public class RTFrame extends JFrame {
    private JTextArea textArea;
    private JMenuBar menuBar;

    public RTFrame() {
        textArea = new JTextArea();

        menuBar = new JMenuBar();
        menuBar.add(new JMenu("File"));
        menuBar.getMenu(0).add("New");
        menuBar.getMenu(0).add("Open");
        menuBar.getMenu(0).add("Save");
        this.add(textArea);
        this.add(menuBar, BorderLayout.NORTH);
        this.setSize(1200,800);
    }
}
