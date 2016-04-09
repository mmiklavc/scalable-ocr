package ocr.nifi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppMain.class);

    public static void main(String[] args) {
        LOGGER.info("Starting application.");
        System.out.println("Hello World!");
        LOGGER.info("Application finished.");
    }
}
