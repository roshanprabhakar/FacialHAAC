import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class Rater {

    private JFrame frame;
    private BufferedImage image;

    private JLabel imageLabel;
    private JTextField recogTitle;
    private JTextField recogField;
    private JTextField satisTitle;
    private JTextField satisfactionField;
    private JPanel mainPanel;
    private JButton submitButton;
    private JCheckBox unknown;

    private boolean submitted = false;
    private boolean known = true;

    public Rater(BufferedImage image) {
        this.image = image;
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitted = true;
            }
        });
        unknown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                known = false;
            }
        });
    }

    private void createUIComponents() {
        imageLabel = new JLabel(new ImageIcon(image));
    }

    //returns point (recognition, rating)
    public Point getRating() {
        display();
        while (!submitted) {
            System.out.print("");
        }
        close();

        if (known) {
            return new Point(Integer.parseInt(recogField.getText()),
                    Integer.parseInt(satisfactionField.getText()));
        }
        return null;
    }

    public void display() {
        frame = new JFrame("Rater");
        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

    public void close() {
        try {
            frame.setVisible(false);
        } catch (NullPointerException e) {
        }
    }
}
