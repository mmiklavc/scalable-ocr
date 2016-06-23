package ocr.extraction.tesseract;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import ocr.common.Util;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public enum TesseractUtil {
    INSTANCE;

    public String ocr(byte[] img, File dataPath) throws IOException, TesseractException {
        return ocr(new ByteArrayInputStream(img), dataPath);
    }

    public String ocr(InputStream is, File dataPath) throws IOException, TesseractException {
        setup();
        Tesseract instance = new Tesseract();
        instance.setDatapath(dataPath.getPath());
        BufferedImage bi = ImageIO.read(IOUtils.toBufferedInputStream(is));
        return instance.doOCR(bi);
    }

    public void setup() {
        System.getProperties().setProperty("jna.library.path", Util.Locations.JNA.find().toString());
    }

}
