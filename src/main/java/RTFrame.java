import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RTFrame extends JFrame {
    private FileTree fileTree;
    private JScrollPane leftSideBar;
    private JScrollPane rightSideBar;
    private JPanel rightSideBarPanel;
    private JPanel bottomBar;
    private List<JComponent> sideBarComponents = new ArrayList<>();
    private JScrollPane scrollPane;
    private RTextArea textArea;
    private JMenuBar menuBar;
    private JLabel infoLabel;
    private JLabel saveLabel;

    private String currentPath;

    private Thread uiUpdateThread;

    public RTFrame() {
        this.setTitle("~ untitled");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new RTextArea();
        scrollPane = new JScrollPane(textArea);

        // ----- initialise menu bar -----
        menuBar = new JMenuBar();
        menuBar.add(new JMenu("File"));
        menuBar.getMenu(0).add("New").addActionListener(e -> fileMenuNew());
        menuBar.getMenu(0).add("Open").addActionListener(e -> fileMenuOpen());
        menuBar.getMenu(0).add("Open Folder").addActionListener(e -> fileMenuOpenFolder());
        menuBar.getMenu(0).add("Save").addActionListener(e -> fileMenuSave());
        menuBar.getMenu(0).add("Save As").addActionListener(e -> fileMenuSaveAs());

        menuBar.add(new JMenu("Settings"));
        menuBar.getMenu(1).add(new JCheckBoxMenuItem("Line Wrap")).addActionListener(e -> textArea.setLineWrap(!textArea.getLineWrap()));
        menuBar.getMenu(1).add(new JCheckBoxMenuItem("Toggle File Bar")).addActionListener(e -> SwingUtilities.invokeLater(this::toggleLeftSideBar));
        menuBar.getMenu(1).add("---").addActionListener(e -> fileMenuSave());
        menuBar.getMenu(1).add("---").addActionListener(e -> fileMenuSaveAs());

        menuBar.add(new JMenu("Side-Bar"));
        menuBar.getMenu(2).add(new JCheckBoxMenuItem("Search and Replace")).addActionListener(e -> toggleSideBarElement(e, textArea.searchPanel));
        menuBar.getMenu(2).add(new JCheckBoxMenuItem("Edit History")).addActionListener(e -> toggleSideBarElement(e, EditHistory.class));
        menuBar.getMenu(2).add(new JCheckBoxMenuItem("---"));
        menuBar.getMenu(2).add(new JCheckBoxMenuItem("---"));
        menuBar.getMenu(2).add(new JCheckBoxMenuItem("---"));

        // ----- initialise left side bar -----
        fileTree = new FileTree(this, new File(""));
        leftSideBar = new JScrollPane(fileTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        leftSideBar.setPreferredSize(new Dimension(200, this.getHeight()));
        leftSideBar.setVisible(false);

        // ----- initialise right side bar -----
        rightSideBarPanel = new JPanel();
        rightSideBarPanel.setLayout(new BoxLayout(rightSideBarPanel, BoxLayout.Y_AXIS));
        rightSideBar = new JScrollPane(rightSideBarPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        updateSideBar();

        // ----- initialize bottom bar -----
        bottomBar = new JPanel();
        infoLabel = new JLabel();
        saveLabel = new JLabel();
        bottomBar.add(infoLabel);
        bottomBar.add(new ZoomControl(textArea));
        updateSaveLabel(false);
        bottomBar.add(saveLabel);
        //bottomBar.setPreferredSize(new Dimension(this.getWidth(), 30));

        // ----- add elements -----
        this.add(leftSideBar, BorderLayout.WEST);
        this.add(rightSideBar, BorderLayout.EAST);
        this.add(scrollPane);
        this.add(menuBar, BorderLayout.NORTH);
        this.add(bottomBar, BorderLayout.SOUTH);

        uiUpdateThread = new Thread(this::uiUpdateLoop);
        uiUpdateThread.start();

        this.setSize(1200, 800);

    }

    private void toggleLeftSideBar() {
        leftSideBar.setVisible(!leftSideBar.isVisible());
        this.revalidate();
        this.repaint();
    }

    private void updateSideBar() {
        rightSideBarPanel.removeAll();
        for (JComponent sideBarComponent : sideBarComponents) {
            rightSideBarPanel.add(sideBarComponent);
        }
        if (!sideBarComponents.isEmpty()) {
            rightSideBar.setPreferredSize(new Dimension(200, this.getHeight()));
        } else {
            rightSideBar.setPreferredSize(new Dimension(0, 0));
        }
        this.revalidate();

    }

    public void fileMenuNew() {
        textArea.setText("");
        currentPath = null;
        updateSaveLabel(false);
    }
    public void fileMenuOpenFolder() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setAcceptAllFileFilterUsed(false);
        fc.showDialog(this, "Open Folder");
        fileTree.setModel(FileTree.createTreeModel(fc.getSelectedFile()));
    }

    public void fileMenuOpen() {
        // todo handle cancel button press
        JFileChooser fc = new JFileChooser();
        fc.showDialog(this, "Open");
        File selectedFile = fc.getSelectedFile();
        loadFile(selectedFile);
    }

    public void loadFile(File selectedFile) {
        try (FileReader fr = new FileReader(selectedFile)) {
            textArea.read(fr, "");
            this.setTitle(selectedFile.getName());
            currentPath = selectedFile.getAbsolutePath();
            updateSaveLabel(false);
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
            updateSaveLabel(true);
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
    public  <T extends JComponent> void toggleSideBarElement(ActionEvent ae, Class<T> clazz) {
        if (ae.getSource() instanceof JCheckBoxMenuItem cbmi) {
            if (cbmi.isSelected()) {
                try {
                    sideBarComponents.add(clazz.getDeclaredConstructor().newInstance());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                List<JComponent> componentsToRemove = sideBarComponents.stream().filter(clazz::isInstance).toList();
                sideBarComponents.removeAll(componentsToRemove);
            }
        }
        updateSideBar();
    }
    public <T extends JComponent> void toggleSideBarElement(ActionEvent ae, T t) {
        if (ae.getSource() instanceof JCheckBoxMenuItem cbmi) {
            if (cbmi.isSelected()) {
                try {
                    sideBarComponents.add(t);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                sideBarComponents.remove(t);
            }
        }
        updateSideBar();
    }

    private void updateInfoLabel() {
        int wordCount = textArea.getText().trim().split("\\s+").length;
        infoLabel.setText(textArea.getLineCount() + " Lines    " + wordCount + " Words");
    }

    private void updateSaveLabel(Boolean wasSaved) {
        if (wasSaved) {
            saveLabel.setText("Last Saved: " + LocalTime.now().format(DateTimeFormatter.ofPattern("H:mm:ss")));
        } else {
            saveLabel.setText("~ not saved");
        }
    }

}
