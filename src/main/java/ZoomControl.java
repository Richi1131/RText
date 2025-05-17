import javax.swing.*;
import java.awt.*;

public class ZoomControl extends JPanel {
    private JButton increaseZoomButton;
    private JButton decreaseZoomButton;
    private JLabel infoLabel;
    private RTextArea textArea;
    private int zoomLevel = 100;
    private final int DEFAULT_FONT_SIZE = 16;
    public ZoomControl(RTextArea textArea) {
        this.textArea = textArea;
        this.setLayout(new GridLayout(1, 3));
        increaseZoomButton = new JButton("+");
        increaseZoomButton.setPreferredSize(new Dimension(20, 20));
        increaseZoomButton.addActionListener(e->this.increaseZoom());
        decreaseZoomButton = new JButton("-");
        decreaseZoomButton.setPreferredSize(new Dimension(20, 20));
        decreaseZoomButton.addActionListener(e->this.decreaseZoom());
        infoLabel = new JLabel();
        this.add(decreaseZoomButton);
        this.add(infoLabel);
        this.add(increaseZoomButton);
        updateInfoLabel();
    }
    private void increaseZoom() {
        zoomLevel += 10;
        this.textArea.setFontSize((int) (zoomLevel/100.0*DEFAULT_FONT_SIZE));
        updateInfoLabel();
    }
    private void decreaseZoom() {
        zoomLevel -= 10;
        zoomLevel = Math.max(zoomLevel, 0);
        this.textArea.setFontSize((int) (zoomLevel/100.0*DEFAULT_FONT_SIZE));
        updateInfoLabel();
    }
    private void updateInfoLabel(){
        infoLabel.setText(""+zoomLevel);
    }
}
