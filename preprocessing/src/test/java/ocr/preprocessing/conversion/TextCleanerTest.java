package ocr.preprocessing.conversion;

import com.google.common.base.Joiner;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.PosixParser;
import org.junit.Assert;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TextCleanerTest {

  @Test
  public void happyPathTest() throws IOException, CommandFailedException {
    String input = "src/test/resources/images/brscan_original_r90.jpg";
    BufferedImage inputImage = ImageUtils.INSTANCE.readImage(new File(input));
    Assert.assertEquals(1024, inputImage.getHeight());
    Assert.assertEquals(768, inputImage.getWidth());
    String output = "src/test/resources/images/brscan_original_r90-out.jpg";
    String args = "-g -e normalize -f 15 -o 10 -u -s 2 -T -p 20";
    DefaultParser parser = new DefaultParser();
    CommandLine cli = CleaningOptions.parse(parser, CLIUtils.translateCommandline(args) );
    TextCleaner cleaner = CleaningOptions.createTextCleaner(cli, null);
    String commandLine = Joiner.on(" ").join(cleaner.getCommandLine(input, output));
    Assert.assertNotNull(commandLine);
    Assert.assertEquals("-respect-parenthesis ( src/test/resources/images/brscan_original_r90.jpg -colorspace gray -type grayscale ) ( -clone 0 -colorspace gray -negate -lat 15x15+10% -contrast-stretch 0 ) -compose copy_opacity -composite -fill white -opaque none -alpha off -background white -deskew 40% -sharpen 0x2 -trim +repage -compose over -bordercolor white -border 20 src/test/resources/images/brscan_original_r90-out.jpg"
                       , commandLine
                       );

    byte[] result = cleaner.convert(input, ".jpg");
    Assert.assertTrue(result.length > 0);
    BufferedImage outputImage = ImageUtils.INSTANCE.readImage(result);
    Assert.assertEquals(1074, outputImage.getHeight());
    Assert.assertEquals(812, outputImage.getWidth());
  }
}
