package ocr.conversion

import java.awt.Image
import java.awt.image.RenderedImage
import java.io.File
import java.util.List
import javax.imageio.ImageIO

import org.ghost4j.document.PDFDocument
import org.ghost4j.renderer.SimpleRenderer

import scala.collection.JavaConversions._

object Convert {

  /**
    * Test conversion
    *
    * @param args
    */
  def main(args: Array[String]): Unit = {
    val document = new PDFDocument()
    document.load(new File(args(0)))

    val renderer: SimpleRenderer = new SimpleRenderer()
    renderer.setResolution(300)
    val images: List[Image] = renderer.render(document)
    /**images.toList.foreach ( image =>
      ImageIO.write(image.asInstanceOf[RenderedImage], "png", new File((i + 1) + ".png"))
    )
      */
  }

}
