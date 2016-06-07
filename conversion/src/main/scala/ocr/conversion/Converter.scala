package ocr.conversion

import java.awt.image.RenderedImage
import java.io.File
import javax.imageio.ImageIO

import org.ghost4j.document.PDFDocument
import org.ghost4j.renderer.SimpleRenderer

import scala.collection.JavaConversions._

class Converter(config: ConfigOptions) {

    def convert(): Int = {
        config.getOutDir().mkdirs()

        val document = new PDFDocument()
        document.load(config.getPdfFile())

        val renderer: SimpleRenderer = new SimpleRenderer()
        renderer.setResolution(300)
        val images = renderer.render(document)
        images.toList.zipWithIndex foreach {
            case (img, i) => ImageIO.write(img.asInstanceOf[RenderedImage], "png", new File(config.getOutDir(), i + ".png"))
        }
        return 0
    }

}
