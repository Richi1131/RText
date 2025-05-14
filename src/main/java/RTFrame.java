import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RTFrame extends JFrame {
    private JScrollPane sideBar;
    private JPanel sideBarPanel;
    private List<JComponent> sideBarComponents = new ArrayList<>();
    private JScrollPane scrollPane;
    private JTextArea textArea;
    private JMenuBar menuBar;
    private JLabel infoLabel;

    private String currentPath;

    private Thread uiUpdateThread;

    public RTFrame() {
        this.setTitle("~ untitled");

        textArea = new JTextArea();
        UndoManager undoManager = new UndoManager();
        final CompoundEdit[] compoundEdit = {new CompoundEdit()};
        final CompoundEdit[] lastEdit = {null};
        textArea.getDocument().addUndoableEditListener(e -> {
            String insertedText = ""; // bad naming -> might be deleted text
            if (e.getEdit() instanceof AbstractDocument.DefaultDocumentEvent event) {
                try {
                    int offset = event.getOffset();
                    int length = event.getLength();
                    insertedText = textArea.getDocument().getText(offset, length);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
                if (insertedText.length() != 1 || !Character.isLetter(insertedText.charAt(0))) {
                    compoundEdit[0].end();
                    compoundEdit[0] = new CompoundEdit();
                }
                compoundEdit[0].addEdit(e.getEdit());
                if (compoundEdit[0] != lastEdit[0]) {
                    undoManager.addEdit(compoundEdit[0]);
                    lastEdit[0] = compoundEdit[0];
                }
            }
        });

        // todo move somewhere else, add buffer?
        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        int menuShortcutKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        KeyStroke undoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, menuShortcutKey);
        inputMap.put(undoKeyStroke, "undoAction");
        ActionMap actionMap = textArea.getActionMap();
        actionMap.put("undoAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!lastEdit[0].canUndo()) {
                    lastEdit[0].end();
                    compoundEdit[0] = new CompoundEdit();
                }
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

        menuBar.add(new JMenu("Side-Bar"));
        menuBar.getMenu(1).add("Undo History").addActionListener(e -> fileMenuNew());
        menuBar.getMenu(1).add("---");
        menuBar.getMenu(1).add("---");
        menuBar.getMenu(1).add("---");

        sideBarPanel = new JPanel();
        sideBarPanel.setLayout(new GridLayout(0, 1));
        sideBar = new JScrollPane(sideBarPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(sideBar, BorderLayout.EAST);
        updateSideBar();

        this.add(scrollPane);
        this.add(menuBar, BorderLayout.NORTH);

        infoLabel = new JLabel();
        this.add(infoLabel, BorderLayout.SOUTH);

        uiUpdateThread = new Thread(this::uiUpdateLoop);
        uiUpdateThread.start();

        this.setSize(1200, 800);

    }

    private void updateSideBar() {
        sideBarPanel.removeAll();
        for (JComponent sideBarComponent : sideBarComponents) {
            sideBarPanel.add(sideBarComponent);
        }
        if (!sideBarComponents.isEmpty()) {
            sideBar.setPreferredSize(new Dimension(
                    sideBar.getVerticalScrollBar().getPreferredSize().width + sideBarPanel.getPreferredSize().width,
                    sideBar.getPreferredSize().height));
        } else {
            sideBar.setPreferredSize(new Dimension(0, 0));
        }

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

    public void uiUpdateLoop() {
        while (true) {
            try {
                Thread.sleep(100);
                updateInfoLabel();
            } catch (InterruptedException e) {

            }

        }
    }

    private void updateInfoLabel() {
        int wordCount = textArea.getText().trim().split("\\s+").length;
        infoLabel.setText(textArea.getLineCount() + " Lines    " + wordCount + " Words");
    }
}
