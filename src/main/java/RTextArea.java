import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class RTextArea extends JTextArea {
    public SearchPanel searchPanel = new SearchPanel(this);
    public RTextArea() {
        UndoManager undoManager = new UndoManager();
        final CompoundEdit[] compoundEdit = {new CompoundEdit()};
        final CompoundEdit[] lastEdit = {null};
        this.getDocument().addUndoableEditListener(e -> {
            String insertedText = ""; // bad naming -> might be deleted text
            if (e.getEdit() instanceof AbstractDocument.DefaultDocumentEvent event) {
                try {
                    int offset = event.getOffset();
                    int length = event.getLength();
                    insertedText = this.getDocument().getText(offset, length);
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
        InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        int menuShortcutKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        KeyStroke undoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, menuShortcutKey);
        inputMap.put(undoKeyStroke, "undoAction");
        ActionMap actionMap = this.getActionMap();
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
        KeyStroke searchKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F, menuShortcutKey);
        inputMap.put(searchKeyStroke, "searchAction");
        actionMap.put("searchAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((RTFrame) SwingUtilities.getWindowAncestor(RTextArea.this)).toggleSideBarElement(e, SearchPanel.class);
            }

            @Override
            public boolean accept(Object sender) {
                return super.accept(sender);
            }
        });
    }
    public void setFontSize(int fontSize) {
        this.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
    }
}
