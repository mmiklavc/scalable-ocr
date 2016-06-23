package ocr.extraction.tesseract;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public enum TesseractUtil {
    INSTANCE;

    public String ocr(byte[] img, File dataPath) throws IOException, TesseractException {
        return ocr(new ByteArrayInputStream(img), dataPath);
    }

    public String ocr(InputStream is, File dataPath) throws IOException, TesseractException {
        Tesseract instance = new Tesseract();
        instance.setDatapath(dataPath.getPath());
        BufferedImage bi = ImageIO.read(IOUtils.toBufferedInputStream(is));
        return instance.doOCR(bi);
    }

    public String ocr(byte[] img, File dataPath, Map<String, String> variables) throws IOException, TesseractException {
        return ocr(new ByteArrayInputStream(img), dataPath, variables);
    }

    public String ocr(InputStream is, File dataPath, Map<String, String> variables) throws IOException, TesseractException {
        Tesseract instance = new Tesseract();
        for (Map.Entry<String, String> kv : variables.entrySet()) {
            instance.setTessVariable(kv.getKey(), kv.getValue());
        }
        instance.setDatapath(dataPath.getPath());
        BufferedImage bi = ImageIO.read(IOUtils.toBufferedInputStream(is));
        return instance.doOCR(bi);
    }

}
