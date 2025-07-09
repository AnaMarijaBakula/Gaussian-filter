import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {

            BufferedImage originalImage = ImageIO.read(new File("images/originalImage.png"));
            long startTimeSequential = System.nanoTime();

            int radius = 50;
            BufferedImage blurred = GaussianFilter.applyGaussianBlur(originalImage, radius);

            long endTimeSequential = System.nanoTime();
            long totalProcessingTime = (endTimeSequential - startTimeSequential) / 1_000_000_000;

            ImageIO.write(blurred, "png", new File("images/blurredImageSequential.png"));

            System.out.println("Vrijeme obrade slike sekvencijalno: " + totalProcessingTime + " s");

            System.out.println("---------------------------------");

            long startTimeParallel = System.nanoTime();

            BufferedImage blurredParallel = GaussianFilterParallel.applyGaussianBlurParallel(originalImage, radius);

            long endTimeParallel = System.nanoTime();
            long totalProcessingTimeParallel = (endTimeParallel - startTimeParallel) / 1_000_000_000;

            ImageIO.write(blurredParallel, "png", new File("images/blurredImageParallel.png"));

            System.out.println("Vrijeme obrade slike paralelno: " + totalProcessingTimeParallel + " s");



        } catch (IOException e) {
            System.err.println("Greška pri učitavanju ili spremanju slike: " + e.getMessage());
        }
    }
}
