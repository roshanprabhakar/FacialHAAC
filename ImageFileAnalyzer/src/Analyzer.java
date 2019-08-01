import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * analyzer which charts image size vs (recognizability and satisfaction)
 */
public class Analyzer {

    public static final double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    public static final double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    private ArrayList<BufferedImage> images;
    private ArrayList<Point> ratings; //recognition, satisfaction

    public Analyzer(ArrayList<BufferedImage> images) {
        this();
        this.images = images;
    }

    public void addImage(BufferedImage image) {
        images.add(image);
    }

    public Analyzer() {
        images = new ArrayList<>();
        ratings = new ArrayList<>();
    }

    public void analyzeImages() throws NullPointerException {
        Rater rater;
        for (BufferedImage trialImage : images) {
            rater = new Rater(trialImage);
            Point rating = rater.getRating();
            if (rating == null) continue;
            ratings.add(rating);
            System.out.println(ratings);
        }
    }

    private static BufferedImage resize(BufferedImage image, int newH, int newW) {
        Image tmp = image.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return dimg;
    }

    public ArrayList<Point> getRatings() {
        return this.ratings;
    }

    public ArrayList<BufferedImage> getImages() {
        return this.images;
    }

    public void writeRatings() {
        try {
            File out = new File("out.csv");
            BufferedWriter writer = new BufferedWriter(new FileWriter(out));
            writer.write("recognition, satisfaction \n");
            for (Point p : ratings) {
                writer.write(p.getX() + ", " + p.getY() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Analyzer analyzer = new Analyzer();
            File images = new File(previousDirectory() + "Out");
            for (File image : images.listFiles()) {
                if (image.getName().contains(".DS_Store")) continue;
//                analyzer.addImage(resize(ImageIO.read(image), 500, 500));

                BufferedImage bImage = ImageIO.read(image);

                if (bImage.getHeight() > screenHeight / 2) {
                    analyzer.addImage(resize(bImage, (int) (screenHeight / 2), (int) (bImage.getWidth() / (bImage.getHeight() / (screenHeight / 2)))));
                } else {
                    analyzer.addImage(ImageIO.read(image));
                }
            }
            analyzer.analyzeImages();
            analyzer.writeRatings();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String previousDirectory() {
        File file = new File("");
        String[] pathChain = file.getAbsolutePath().split("/");

        StringBuilder path = new StringBuilder();
        for (int i = 0; i < pathChain.length - 1; i++) {
            path.append(pathChain[i]);
            path.append("/");
        }
        return path.toString().trim();
    }
}
