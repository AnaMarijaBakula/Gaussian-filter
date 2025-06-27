import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

public class GaussianFilterParallel {

    public static float[] createGaussianKernel(int radius) {
        int size = radius * 2 + 1;
        float[] kernel = new float[size];
        float sigma = radius / 3.0f;
        float sigma22 = 2 * sigma * sigma;
        float sigmaPi2 = (float)(2 * Math.PI * sigma);
        float sqrtSigmaPi2 = (float)Math.sqrt(sigmaPi2);
        float normalization = 1.0f / (sqrtSigmaPi2 * sigma);
        float sum = 0;

        for (int i = -radius; i <= radius; i++) {
            float distance = i * i;
            kernel[i + radius] = (float)(normalization * Math.exp(-distance / sigma22));
            sum += kernel[i + radius];
        }
        for (int i = 0; i < size; i++) {
            kernel[i] /= sum;
        }
        return kernel;
    }

    public static BufferedImage horizontalBlur(BufferedImage src, float[] kernel) {
        int width = src.getWidth();
        int height = src.getHeight();
        int radius = kernel.length / 2;

        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        IntStream.range(0, height).parallel().forEach(y -> {
            for (int x = 0; x < width; x++) {
                float r = 0, g = 0, b = 0;
                float sum = 0;

                for (int k = -radius; k <= radius; k++) {
                    int px = x + k;
                    if (px < 0) px = 0;
                    if (px >= width) px = width - 1;

                    int rgb = src.getRGB(px, y);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    float weight = kernel[k + radius];
                    r += red * weight;
                    g += green * weight;
                    b += blue * weight;
                    sum += weight;
                }

                int finalRed = Math.min(255, Math.max(0, Math.round(r / sum)));
                int finalGreen = Math.min(255, Math.max(0, Math.round(g / sum)));
                int finalBlue = Math.min(255, Math.max(0, Math.round(b / sum)));

                int newRgb = (0xFF << 24) | (finalRed << 16) | (finalGreen << 8) | finalBlue;

                synchronized (output) {
                    output.setRGB(x, y, newRgb);
                }
            }
        });

        return output;
    }

    public static BufferedImage verticalBlur(BufferedImage src, float[] kernel) {
        int width = src.getWidth();
        int height = src.getHeight();
        int radius = kernel.length / 2;

        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        IntStream.range(0, height).parallel().forEach(y -> {
            for (int x = 0; x < width; x++) {
                float r = 0, g = 0, b = 0;
                float sum = 0;

                for (int k = -radius; k <= radius; k++) {
                    int py = y + k;
                    if (py < 0) py = 0;
                    if (py >= height) py = height - 1;

                    int rgb = src.getRGB(x, py);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    float weight = kernel[k + radius];
                    r += red * weight;
                    g += green * weight;
                    b += blue * weight;
                    sum += weight;
                }

                int finalRed = Math.min(255, Math.max(0, Math.round(r / sum)));
                int finalGreen = Math.min(255, Math.max(0, Math.round(g / sum)));
                int finalBlue = Math.min(255, Math.max(0, Math.round(b / sum)));

                int newRgb = (0xFF << 24) | (finalRed << 16) | (finalGreen << 8) | finalBlue;

                synchronized (output) {
                    output.setRGB(x, y, newRgb);
                }
            }
        });

        return output;
    }

    public static BufferedImage applyGaussianBlurParallel(BufferedImage image, int radius) {
        float[] kernel = createGaussianKernel(radius);
        BufferedImage horizontal = horizontalBlur(image, kernel);
        BufferedImage vertical = verticalBlur(horizontal, kernel);
        return vertical;
    }
}
