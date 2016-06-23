package ocr.conversion

import java.awt.image.RenderedImage
import java.io.{File, FileInputStream, InputStream}
import java.util.UUID
import javax.imageio.ImageIO
import javax.imageio.spi.IIORegistry

import com.google.common.base.Splitter
import com.google.common.collect.Iterables
import org.apache.commons.io.IOUtils
import org.geotoolkit.image.io.plugin.RawTiffImageReader
import org.ghost4j.document.PDFDocument

import scala.collection.JavaConversions._

class Converter {

    object StaticConfig {
        IIORegistry.getDefaultInstance()
            .registerServiceProvider(new RawTiffImageReader.Spi());
    }

    def convert(in: InputStream, outDir: File): List[Tuple2[File, Boolean]] = {
        val document = new PDFDocument()
        document.load(IOUtils.toBufferedInputStream(in))

        val renderer: AlmostSimpleRenderer = new AlmostSimpleRenderer()
        renderer.setResolution(300)
        val images = renderer.render(document)
        val uuid = UUID.randomUUID().toString
        images.toList
            .zipWithIndex.map {
            case (img, i) => {
                val outFile = new File(outDir, uuid + "-" + i + ".tiff");
                Tuple2(outFile, ImageIO.write(img.asInstanceOf[RenderedImage], "tif", outFile))
            }
        }
    }

    def convert(config: ConfigOptions): List[File] = {
        config.getOutDir().mkdirs()
        convert(new FileInputStream(config.getPdfFile()), config.getOutDir())
            .map {
                case (f, b) => f
            }
    }

    def toJava(in: List[Tuple2[File, Boolean]]): java.util.List[java.util.Map.Entry[java.io.File, java.lang.Boolean]] = {
        val ret = new java.util.ArrayList[java.util.Map.Entry[java.io.File, java.lang.Boolean]]
        in.foreach {
            case (f, b) => ret.add(new java.util.AbstractMap.SimpleEntry[java.io.File, java.lang.Boolean](f, b))
        }
        ret
    }

    def getPageNumber(fileName: String): Integer = {
        val it = Splitter.on(".tiff").split(fileName);
        val first = Iterables.getFirst(it, null);
        Integer.parseInt(Iterables.getLast(Splitter.on("-").split(first)));
    }

}
