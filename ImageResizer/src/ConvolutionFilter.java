import java.awt.*;
import java.awt.image.BufferedImage;

public class ConvolutionFilter {

    private BufferedImage image;
    private ConvolutionKernel k;

    //Red, Green, Blue in index 1
    private int[][][] Channels;
    private static String[] names = new String[] {"red", "green", "blue"};

    public ConvolutionFilter(ConvolutionKernel k, BufferedImage image) {
        this.image = image;
        this.k = k;

        Channels = channel(image);
    }

    public void convolve() {
        for (int i = 0; i < 3; i++) {
            Channels[i] = convolve(Channels[i]);
        }

        reconstruct(Channels);
    }

    private void reconstruct(int[][][] channels) {
        for (int r = 0; r < image.getHeight(); r++) {
            for (int c = 0; c < image.getWidth(); c++) {
                Color pixel = new Color(channels[0][r][c], channels[1][r][c], channels[2][r][c]);
                image.setRGB(c, r, pixel.getRGB());
            }
        }
    }

    private int[][] convolve(int[][] component) {
        int[][] convolved = new int[component.length][component[0].length];
        for (int r = 0; r < image.getHeight() - k.getHeight(); r++) {
            for (int c = 0; c < image.getWidth() - k.getWidth(); c++) {

                int sum = 0;
                for (int i = 0; i < k.getHeight(); i++) {
                    for (int j = 0; j < k.getWidth(); j++) {
                        sum += k.value(i, j) * component[r + i][c + j];
                    }
                }

                if (sum > 255) sum = 255;
                else if (sum < 0) sum = 0;

                convolved[r][c] = sum;
            }
        }
        return convolved;
    }

    private static int[][][] channel(BufferedImage image) {
        int[][][] channels = new int[3][image.getHeight()][image.getWidth()];
        for (int i = 0; i < 3; i++) {
            for (int r = 0; r < image.getHeight(); r++) {
                for (int c = 0; c < image.getWidth(); c++) {
                    channels[i][r][c] = getComponent(image, i, r, c);
                }
            }
        }
        return channels;
    }

    private static int getComponent(BufferedImage image, int channel, int r, int c) {
        Color pixel = new Color(image.getRGB(c, r));
        if (channel == 0) {
            return pixel.getRed();
        } else if (channel == 1) {
            return pixel.getGreen();
        } else {
            return pixel.getBlue();
        }
    }

    public BufferedImage getImage() {
        return this.image;
    }
}
