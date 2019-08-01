import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Main {


    public static ArrayList<int[]> FEATURES;
    public static ArrayList<int[]> FACES;

    public static void main(String[] args) {

//        BufferedImage before = getImage("test.jpg");
//        BufferedImage after = new Resizer(before, 70, 50).getResized();
//
//        display(before, "before");
//        display(after, "after");
//
//        writeImage(before, "before");
//        writeImage(after, "after");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(
                    new File(previousDirectory() + "haar_knn/" + "features.txt")));
            init(reader);

            BufferedImage original = ImageIO.read(new File(inDirectory() + "original.jpg"));
            BufferedImage preprocessed = new Resizer(original, 90, 45).getResized();





            int featureI = 0;
            int faceI = 0;
            for (int[] feature : FEATURES) {
                featureI++;

//                System.out.println("------------------------------");
//                System.out.println("featureI: " + featureI);
//                System.out.println("faceI: " + faceI);
//                System.out.println("face: " + Arrays.toString(FACES.get(faceI)));
//                System.out.println("feature: " + Arrays.toString(feature));
//                System.out.println("writing: " + Arrays.toString(feature) + " to: " + Arrays.toString(FACES.get(faceI)));

                copySection(original, preprocessed, feature, FACES.get(faceI));
                if (featureI % 4 == 0) {
                    faceI++;
                }
            }

//            display(original, "original");
//            display(preprocessed, "processed");



            writeImage(preprocessed, previousDirectory() + "Out/processed" +
                    new File(previousDirectory() + "Out/").listFiles().length);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Utils
    public static void init(BufferedReader reader) throws IOException {

        FEATURES = new ArrayList<>();
        FACES = new ArrayList<>();

        String line;
        int count = 0;
        while ((line = reader.readLine()) != null) {
            count++;
            if (count % 5 == 0) {
                FACES.add(parseLine(line));
            } else {
                FEATURES.add(parseLine(line));
            }
        }
    }

    public static void display(BufferedImage image, String title) {
        JFrame frame = new JFrame(title);
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setVisible(true);
    }

    public static BufferedImage getImage(String filepath) {
        try {
            return ImageIO.read(new File(filepath));
        } catch (IOException e) {
            return null;
        }
    }

    public static void writeImage(BufferedImage image, String name) {
        File outputfile = new File(name + ".jpg");
        try {
            ImageIO.write(image, "jpg", outputfile);
        } catch (IOException e) {
            return;
        }
    }

    public static String previousDirectory() {
        File file = new File("");
        String[] pathChain = file.getAbsolutePath().split("/");

        StringBuilder path = new StringBuilder();
        for (int i = 0; i < pathChain.length - 1; i++) {
            path.append(pathChain[i]);
            path.append("/");
        }
        return path.toString().trim();
    }

    public static String inDirectory() {
        return previousDirectory() + "in/";
    }

    public static String outDirectory() {
        return previousDirectory() + "out/";
    }

    public static int[] parseLine(String line) {

        int[] features = new int[4];

        int i = 0;
        for (String indice : line.replaceAll("\\[", "").
                replaceAll("]", "").split(",")) {
            features[i] = Integer.parseInt(indice.trim());
            i++;
        }

        return features;
    }

    public static void copySection(BufferedImage from, BufferedImage to, int[] feature, int[] face) {
        for (int r = feature[1] + face[1]; r < face[1] + feature[1] + feature[3]; r++) {
            for (int c = feature[0] + face[0]; c < face[0] + feature[0] + feature[2]; c++) {
                to.setRGB(c, r, from.getRGB(c, r));
            }
        }
    }
}
