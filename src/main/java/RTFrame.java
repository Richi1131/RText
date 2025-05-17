import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RTFrame extends JFrame {
    private JScrollPane sideBar;
    private JPanel sideBarPanel;
    private JPanel bottomBar;
    private List<JComponent> sideBarComponents = new ArrayList<>();
    private JScrollPane scrollPane;
    private RTextArea textArea;
    private JMenuBar menuBar;
    private JLabel infoLabel;

    private String currentPath;

    private Thread uiUpdateThread;

    public RTFrame() {
        this.setTitle("~ untitled");

        textArea = new RTextArea();
        scrollPane = new JScrollPane(textArea);

        // ----- initialise menu bar -----
        menuBar = new JMenuBar();
        menuBar.add(new JMenu("File"));
        menuBar.getMenu(0).add("New").addActionListener(e -> fileMenuNew());
        menuBar.getMenu(0).add("Open").addActionListener(e -> fileMenuOpen());
        menuBar.getMenu(0).add("Save").addActionListener(e -> fileMenuSave());
        menuBar.getMenu(0).add("Save As").addActionListener(e -> fileMenuSaveAs());

        menuBar.add(new JMenu("Settings"));
        menuBar.getMenu(1).add(new JCheckBoxMenuItem("Line Wrap")).addActionListener(e -> textArea.setLineWrap(!textArea.getLineWrap()));
        menuBar.getMenu(1).add("---").addActionListener(e -> fileMenuOpen());
        menuBar.getMenu(1).add("---").addActionListener(e -> fileMenuSave());
        menuBar.getMenu(1).add("---").addActionListener(e -> fileMenuSaveAs());

        menuBar.add(new JMenu("Side-Bar"));
        menuBar.getMenu(2).add(new JCheckBoxMenuItem("Edit History")).addActionListener(e -> toggleSideBarElement(e, EditHistory.class));
        menuBar.getMenu(2).add(new JCheckBoxMenuItem("---"));
        menuBar.getMenu(2).add(new JCheckBoxMenuItem("---"));
        menuBar.getMenu(2).add(new JCheckBoxMenuItem("---"));

        // ----- initialise side bar -----
        sideBarPanel = new JPanel();
        sideBarPanel.setLayout(new GridLayout(0, 1));
        sideBar = new JScrollPane(sideBarPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        updateSideBar();

        // ----- initialize bottom bar -----
        bottomBar = new JPanel();
        infoLabel = new JLabel();
        bottomBar.add(infoLabel);
        bottomBar.add(new ZoomControl(textArea));
        //bottomBar.setPreferredSize(new Dimension(this.getWidth(), 30));

        // ----- add elements -----
        this.add(sideBar, BorderLayout.EAST);
        this.add(scrollPane);
        this.add(menuBar, BorderLayout.NORTH);
        this.add(bottomBar, BorderLayout.SOUTH);

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
                    this.getHeight()));
        } else {
            sideBar.setPreferredSize(new Dimension(0, 0));
        }
        this.revalidate();

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
    private <T extends JComponent> void toggleSideBarElement(ActionEvent ae, Class<T> clazz) {
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

    private void updateInfoLabel() {
        int wordCount = textArea.getText().trim().split("\\s+").length;
        infoLabel.setText(textArea.getLineCount() + " Lines    " + wordCount + " Words");
    }

}
