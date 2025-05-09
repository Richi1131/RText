import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RTFrame extends JFrame {
    private JScrollPane scrollPane;
    private JTextArea textArea;
    private JMenuBar menuBar;

    private String currentPath;

    public RTFrame() {
        this.setTitle("~ untitled");

        textArea = new JTextArea();
        UndoManager undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(undoManager);

        // todo move somewhere else, add buffer?
        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        int menuShortcutKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        KeyStroke undoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, menuShortcutKey);
        inputMap.put(undoKeyStroke, "undoAction");
        ActionMap actionMap = textArea.getActionMap();
        actionMap.put("undoAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            }

            @Override
            public boolean accept(Object sender) {
                return super.accept(sender);
            }
        });

        KeyStroke redoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, menuShortcutKey);
        inputMap.put(redoKeyStroke, "redoAction");
        actionMap.put("redoAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
            }

            @Override
            public boolean accept(Object sender) {
                return super.accept(sender);
            }
        });

        scrollPane = new JScrollPane(textArea);

        menuBar = new JMenuBar();
        menuBar.add(new JMenu("File"));
        menuBar.getMenu(0).add("New").addActionListener(e -> fileMenuNew());
        menuBar.getMenu(0).add("Open").addActionListener(e -> fileMenuOpen());
        menuBar.getMenu(0).add("Save").addActionListener(e -> fileMenuSave());
        menuBar.getMenu(0).add("Save As").addActionListener(e -> fileMenuSaveAs());

        this.add(scrollPane);
        this.add(menuBar, BorderLayout.NORTH);
        this.setSize(1200, 800);

    }
    public void fileMenuNew() {
        textArea.setText("");
        currentPath = null;
    }

    public void fileMenuOpen() {
        // todo handle cancel button press
        JFileChooser fc = new JFileChooser();
        fc.showDialog(this, "Open");
        try (FileReader fr = new FileReader(fc.getSelectedFile())) {
            textArea.read(fr, "");
            this.setTitle(fc.getSelectedFile().getName());
            currentPath = fc.getSelectedFile().getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void fileMenuSave() {
        if (currentPath == null) {
            fileMenuSaveAs();
            return;
        }
        File file = new File(currentPath);
        try (FileWriter fw = new FileWriter(file)) {
            textArea.write(fw);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void fileMenuSaveAs() {
        JFileChooser fc = new JFileChooser();
        fc.showDialog(this, "Save As");
        this.setTitle(fc.getSelectedFile().getName());
        currentPath = fc.getSelectedFile().getAbsolutePath();
        fileMenuSave();
    }
}
