package ocr.nifi.extraction;

import ocr.common.Util;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class ExtractionProcessorTest {

    @Test
    public void test() throws FileNotFoundException {
        // Generate a test runner to mock a processor in a flow
        TestRunner runner = TestRunners.newTestRunner(new ExtractionProcessor());
        File inputFile = new File("../extraction/src/test/resources/pdf-test.tiff");
        // Add properties
        runner.setProperty(ExtractionProcessor.JNI_PATH, Util.Locations.JNA.find().get().getAbsolutePath());
        runner.setProperty(ExtractionProcessor.TESS_DATA, Util.Locations.TESSDATA.find().get().getAbsolutePath());
        // Add the content to the runner
        runner.enqueue(new FileInputStream(inputFile));

        // Run the enqueued content, it also takes an int = number of contents queued
        runner.run(1);

        // All results were processed with out failure
        runner.assertQueueEmpty();

        // If you need to read or do additional tests on results you can access the content
        List<MockFlowFile> results = runner.getFlowFilesForRelationship(ExtractionProcessor.SUCCESS);
        Assert.assertEquals(1, results.size());
        String text = new String(runner.getContentAsByteArray(results.get(0)));
        Assert.assertTrue(text.contains("Congratulations, your computer is equipped with a PDF (Portable Document Format)\nreader!"));

    }

}
