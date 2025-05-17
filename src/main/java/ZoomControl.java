import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;

public class ZoomControl extends JPanel {
    private JButton increaseZoomButton;
    private JButton decreaseZoomButton;
    private JFormattedTextField zoomTextField;
    private RTextArea textArea;
    private int zoomLevel = 100;
    private final int DEFAULT_FONT_SIZE = new RTextArea().getFont().getSize();
    public ZoomControl(RTextArea textArea) {
        this.textArea = textArea;
        this.setLayout(new FlowLayout());
        increaseZoomButton = new JButton("+");
        increaseZoomButton.setPreferredSize(new Dimension(20, 20));
        increaseZoomButton.addActionListener(e->this.increaseZoom());
        increaseZoomButton.setMargin(null);
        decreaseZoomButton = new JButton("-");
        decreaseZoomButton.setPreferredSize(new Dimension(20, 20));
        decreaseZoomButton.addActionListener(e->this.decreaseZoom());
        decreaseZoomButton.setMargin(null);

        // Create a NumberFormatter for numeric input
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        numberFormat.setGroupingUsed(false);
        NumberFormatter numberFormatter = new NumberFormatter(numberFormat);
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false); // Prevent invalid input
        numberFormatter.setCommitsOnValidEdit(true); // Commit on valid edit

        zoomTextField = new JFormattedTextField(numberFormatter);
        zoomTextField.addActionListener(e-> {
            if (e.getSource() instanceof JTextField jtf) {
                setZoomLevel(Integer.parseInt(jtf.getText()));
            }
        });
        zoomTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                zoomTextField.postActionEvent(); // Trigger action event
            }
        });
        zoomTextField.setPreferredSize(new Dimension(45, 20));
        zoomTextField.setMargin(null);
        this.add(decreaseZoomButton);
        this.add(zoomTextField);
        this.add(increaseZoomButton);
        updateZoomTextField();
    }
    private void increaseZoom() {
        setZoomLevel(zoomLevel + 10);
    }
    private void decreaseZoom() {
        setZoomLevel(zoomLevel - 10);
    }
    private void setZoomLevel(int zoom){
        zoomLevel = zoom;
        zoomLevel = Math.max(zoomLevel, 0);
        this.textArea.setFontSize((int) (zoomLevel/100.0*DEFAULT_FONT_SIZE));
        updateZoomTextField();
    }
    private void updateZoomTextField(){
        zoomTextField.setText(""+zoomLevel);
    }
}
