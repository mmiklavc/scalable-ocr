package ocr.preprocessing.conversion;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public enum ImageUtils {
  INSTANCE;

  public BufferedImage readImage(byte[] inputImage) throws IOException {
    return ImageIO.read(new ByteArrayInputStream(inputImage));
  }
  public BufferedImage readImage(File inputFile) throws IOException {
    return ImageIO.read(inputFile);
  }

  public int getHeight(BufferedImage img) {
    return img.getHeight();
  }

  public int getWidth(BufferedImage img) {
    return img.getWidth();
  }

  public double getAspectRatio(BufferedImage img) {
    return (1.0*getHeight(img))/getWidth(img);
  }

}
