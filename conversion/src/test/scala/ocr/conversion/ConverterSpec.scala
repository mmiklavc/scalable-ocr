package ocr.conversion

import java.io.File
import javax.imageio.ImageIO

import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class ConverterSpec extends FlatSpec with Matchers with BeforeAndAfter {

    val outDir = new File("target/converter-output")

    before {
        FileUtils.deleteDirectory(outDir)
    }

    "converter" should "put 2 files in destination directory" in {
        val samplePDF = "target/test-classes/text-detection.pdf"
        val outFiles = Driver.run(Array(samplePDF, outDir.getAbsolutePath))
        outDir.exists() shouldBe true
        outDir.listFiles().length shouldBe outFiles.length
        outDir.listFiles().length shouldBe 2
        outDir.listFiles.map(f => f.getName).toSet shouldBe outFiles.map(f => f.getName).toSet
        outFiles.map( f => ImageIO.read(f)).foreach( bi => bi.getHeight > 0 && bi.getWidth > 0 shouldBe true)
    }

}
