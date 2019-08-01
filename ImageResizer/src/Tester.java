import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Tester {

    public static void main(String[] args) throws IOException, InterruptedException {
        ConvolutionKernel k = new ConvolutionKernel(new int[][] {
                {-1,1,-1},
                {1,1,1},
                {-1,1,-1}
        });


        ConvolutionFilter filter = new ConvolutionFilter(k, ImageIO.read(new File("before.png")));
        filter.convolve();

        JFrame frame = new JFrame("convolved");
        frame.getContentPane().add(new JLabel(new ImageIcon(filter.getImage())));
        frame.pack();
        frame.setVisible(true);
        Thread.sleep(100000);

    }
}
