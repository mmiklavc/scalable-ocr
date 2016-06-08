package ocr.nifi.conversion;

import ocr.preprocessing.conversion.ImageUtils;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.ghost4j.document.PDFDocument;
import org.junit.Assert;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConversionProcessorTest {
  @Test
  public void test() throws Exception {
    // Generate a test runner to mock a processor in a flow
    TestRunner runner = TestRunners.newTestRunner(new ConversionProcessor());
    File inputFile = new File("../conversion/src/test/resources/text-detection.pdf");
    // Add properties
    runner.setProperty(ConversionProcessor.JNI_PATH, "/opt/local/lib");
    runner.setProperty(ConversionProcessor.TEMP_DIR, "/tmp");

    // Add the content to the runner
    runner.enqueue(new FileInputStream(inputFile));

    // Run the enqueued content, it also takes an int = number of contents queued
    runner.run(1);

    // All results were processed with out failure
    runner.assertQueueEmpty();

    // If you need to read or do additional tests on results you can access the content
    List<MockFlowFile> results = runner.getFlowFilesForRelationship(ConversionProcessor.SUCCESS);
    assertEquals(2, results.size() );
    for(MockFlowFile result : results) {
      byte[] value = runner.getContentAsByteArray(result);
      BufferedImage bi = ImageUtils.INSTANCE.readImage(value);
      Assert.assertTrue(bi.getHeight() > 0);
      Assert.assertTrue(bi.getWidth() > 0);
      String pageNum = result.getAttribute("pageNumber");
      Assert.assertTrue(pageNum.equals("0") || pageNum.equals("1"));
    }
    List<MockFlowFile> rawResults = runner.getFlowFilesForRelationship(ConversionProcessor.RAW);
    assertEquals(1, rawResults.size() );
    byte[] value = runner.getContentAsByteArray(rawResults.get(0));
    PDFDocument doc = new PDFDocument();
    doc.load(new ByteArrayInputStream(value));
  }
}
