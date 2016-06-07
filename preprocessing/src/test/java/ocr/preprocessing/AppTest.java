package ocr.preprocessing;

import ocr.preprocessing.conversion.CleaningOptions;
import org.junit.Test;

import static org.junit.Assert.*;

public class AppTest {
    @Test
    public void testHello() throws Exception {
        System.out.println(CleaningOptions.getUsage());
    }
}
