import javax.swing.*;
import java.awt.*;

public class ZoomControl extends JPanel {
    private JButton increaseZoomButton;
    private JButton decreaseZoomButton;
    private JLabel infoLabel;
    private JTextArea textArea;
    private int zoomLevel = 16;
    public ZoomControl(JTextArea textArea) {
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
        zoomLevel += 2;
        this.textArea.setFont(new Font("Monospaced", Font.PLAIN, zoomLevel));
        updateInfoLabel();
    }
    private void decreaseZoom() {
        zoomLevel -= 2;
        this.textArea.setFont(new Font("Monospaced", Font.PLAIN, zoomLevel));
        updateInfoLabel();
    }
    private void updateInfoLabel(){
        infoLabel.setText(""+zoomLevel);
    }
}
