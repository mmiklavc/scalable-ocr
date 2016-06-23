package ocr.nifi.preprocessing;

import ocr.common.Util;
import ocr.preprocessing.conversion.ImageUtils;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class PreprocessingTest {

    @Test
    public void test() throws Exception {
        // Generate a test runner to mock a processor in a flow
        TestRunner runner = TestRunners.newTestRunner(new PreprocessingProcessor());
        File inputFile = new File("../preprocessing/src/test/resources/images/brscan_original_r90.jpg");
        // Add properties
        runner.setProperty(PreprocessingProcessor.CONVERT_PATH, Util.Locations.CONVERT.find().get().getAbsolutePath());
        runner.setProperty(PreprocessingProcessor.TEMP_DIR, "/tmp");
        runner.setProperty(PreprocessingProcessor.DEFINITIONS, "-g -e normalize -f 15 -o 10 -u -s 2 -T -p 20");
        // Add the content to the runner
        runner.enqueue(new FileInputStream(inputFile));

        // Run the enqueued content, it also takes an int = number of contents queued
        runner.run(1);

        // All results were processed with out failure
        runner.assertQueueEmpty();

        // If you need to read or do additional tests on results you can access the content
        List<MockFlowFile> results = runner.getFlowFilesForRelationship(PreprocessingProcessor.SUCCESS);
        Assert.assertEquals(1, results.size());
        byte[] value = runner.getContentAsByteArray(results.get(0));
        BufferedImage bi = ImageUtils.INSTANCE.readImage(value);
        Assert.assertEquals(1074, bi.getHeight());
        Assert.assertEquals(812, bi.getWidth());
    }

    private String findBin(String[] locs) {
        for (String loc : locs) {
            File binPath = new File(loc);
            if (binPath.exists() && binPath.isFile()) {
                return loc;
            }
        }
        return "";
    }
}
