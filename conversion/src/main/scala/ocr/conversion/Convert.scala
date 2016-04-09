package ocr.conversion

import java.io.File

import org.ghost4j.document.{Document, PDFDocument}
import org.ghost4j.renderer.SimpleRenderer

object Convert {

  /**
    * Test conversion
    *
    * @param args
    */
  def main(args: Array[String]): Unit = {
    val document :Document = new PDFDocument()
    document.load(new File(args(0)))

    val renderer :SimpleRenderer = new SimpleRenderer()
    renderer.setResolution(300)
    renderer.render(document)
  }
}
