import javax.swing.*;
import java.awt.*;

public class SearchPanel extends JPanel {
    JTextField findTextField;
    JButton findButton;
    JTextField replaceTextField;
    RTextArea textArea;
    JButton replaceButton;

    public SearchPanel(RTextArea textArea) {
        this.textArea = textArea;
        this.setLayout(new FlowLayout());
        this.setPreferredSize(new Dimension(180, 80));
        this.setMaximumSize(new Dimension(180, 80));

        findTextField = new JTextField();
        findButton = new JButton("find");
        replaceTextField = new JTextField();
        replaceButton = new JButton("replace");

        findButton.addActionListener(e -> textArea.highlightString(findTextField.getText(), Color.YELLOW));
        replaceButton.addActionListener(e -> textArea.replaceString(findTextField.getText(), replaceTextField.getText()));

        findTextField.setPreferredSize(new Dimension(110, 36));
        replaceTextField.setPreferredSize(new Dimension(110, 36));
        findButton.setPreferredSize(new Dimension(60,36));
        replaceButton.setPreferredSize(new Dimension(60,36));

        this.add(findTextField);
        this.add(findButton);
        this.add(replaceTextField);
        this.add(replaceButton);
    }
}
