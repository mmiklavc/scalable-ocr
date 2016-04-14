package ocr.conversion

import java.io.File

import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class ConverterSpec extends FlatSpec with Matchers with BeforeAndAfter {

    val outDir = new File("target/converter-output")

    before {
        FileUtils.deleteDirectory(outDir)
    }

    "converter" should "put 2 files in destination directory" in {
        val samplePDF = "target/test-classes/text-detection.pdf"
        Driver.run(Array(samplePDF, outDir.getAbsolutePath))

        outDir.exists() shouldBe true
        outDir.listFiles().length shouldBe 2
    }

}
