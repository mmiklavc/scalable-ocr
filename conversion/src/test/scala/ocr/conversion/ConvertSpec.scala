package ocr.conversion

import org.scalatest.{FlatSpec, Matchers}

class ConvertSpec extends FlatSpec with Matchers {

    "converter" should "spit out files" in {
        // TODO just getting it working
        Convert.main(Array("/Users/mmiklavcic/devprojects/scalable-ocr/presentation/text-detection.pdf", "target/tifffiles"))
    }
}
