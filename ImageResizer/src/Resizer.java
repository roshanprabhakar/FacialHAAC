import java.awt.*;
import java.awt.image.BufferedImage;

public class Resizer {

    private BufferedImage image;

    private int nh, nw;
    private int sh, sw;

    public Resizer(BufferedImage image, int nw, int nh) {

        this.image = image;

        this.nw = nw;
        this.nh = nh;

        sh = image.getHeight() / nh;
        sw = image.getWidth() / nw;
    }

    public BufferedImage getResized() {
        BufferedImage resized = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        int max;
        for (int r = 0; r < sh * nh; r += sh) {
            for (int c = 0; c < sw * nw; c += sw) {

                max = 0;
                for (int i = 0; i < sh; i++) {
                    for (int j = 0; j < sw; j++) {

                        if (Math.abs(image.getRGB(c + j, r + i)) > max) {
                            max = image.getRGB(c + j, r + i);
                        }
                    }
                }

                for (int row = r; row < r + sh; row++) {
                    for (int col = c; col < c + sw; col++) {
                        resized.setRGB(col, row, max);
                    }
                }

            }
        }

        return resized;
    }
}
