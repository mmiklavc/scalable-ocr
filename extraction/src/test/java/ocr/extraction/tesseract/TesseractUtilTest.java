package ocr.extraction.tesseract;

import com.google.common.base.Joiner;
import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class TesseractUtilTest {
  @Test
  public void testTesseractHappyPath() throws Exception {
    File inFile = new File("src/test/resources/pdf-test.tiff");
    File txtFile = new File("src/test/resources/pdf-test.txt");
    String text = TesseractUtil.INSTANCE.ocr(Files.readAllBytes(inFile.toPath())
                                            ,new File("/usr/local/Cellar/tesseract/3.04.01_1/share/tessdata/")
                                            );
    Assert.assertTrue(text.contains("Congratulations, your computer is equipped with a PDF (Portable Document Format)\nreader!"));
  }
}
