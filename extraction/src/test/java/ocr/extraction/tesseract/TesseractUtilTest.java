package ocr.extraction.tesseract;

import ocr.common.Util;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;

public class TesseractUtilTest {

    @Test
    public void testTesseractHappyPath() throws Exception {
        System.getProperties().setProperty("jna.library.path", Util.Locations.JNA.find().get().getAbsolutePath());
        File inFile = new File("src/test/resources/pdf-test.tiff");
        File txtFile = new File("src/test/resources/pdf-test.txt");
        String text = TesseractUtil.INSTANCE.ocr(Files.readAllBytes(inFile.toPath()), Util.Locations.TESSDATA.find().get(), new HashMap<>());
        Assert.assertTrue(text.contains("Congratulations, your computer is equipped with a PDF (Portable Document Format)\nreader!"));
    }

}
