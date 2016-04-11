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
      * Convert each page of PDF to TIFF file
      *
      * @param args
      */
    def main(args: Array[String]): Unit = {
        val pdf = new File(args(0))
        val outDir = new File(args(1))

        if (System.getProperty("os.name").toLowerCase.contains("mac os x")) {
            System.getProperties.setProperty("jna.library.path", "/opt/local/lib/")
        }
        outDir.mkdirs()

        val document = new PDFDocument()
        document.load(pdf)

        val renderer: SimpleRenderer = new SimpleRenderer()
        renderer.setResolution(300)
        val images: List[Image] = renderer.render(document)
        images.toList.zipWithIndex foreach {
            case (img, i) => ImageIO.write(img.asInstanceOf[RenderedImage], "png", new File(outDir, i + ".png"))
        }
    }

}
