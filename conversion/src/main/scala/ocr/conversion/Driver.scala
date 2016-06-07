package ocr.conversion

import java.io.File

import scala.collection.JavaConversions._

object Driver {

    val usage =
        """
          |Usage: convert pdfFile outDir [jnaLibPath]
        """.stripMargin

    /**
      * Convert each page of PDF to TIFF file
      *
      * @param args
      */
    def main(args: Array[String]): Unit = {
        sys.exit(run(args))
    }

    def run(args: Array[String]): Int = {
        val config: ConfigOptions = buildConfig(args)
        setupJnaLibPath(config)
        setupOutputLocation(config)
        val converter = new Converter(config)
        converter.convert()
    }

    def setupOutputLocation(config: ConfigOptions): Unit = {
        config.getOutDir().mkdirs()
    }

    def buildConfig(args: Array[String]): ConfigOptions = {
        if (args.length < 2) {
            println("Incorrect arguments: \n" + usage)
            System.exit(1)
        }
        val argsList = args.toList

        val config = new ConfigOptions(new File(argsList.get(0)), new File(argsList.get(1)), getJnaLibPath(argsList))
        config
    }

    def setupJnaLibPath(config: ConfigOptions): Any = {
        config.getJnaLibPath match {
            case Some(s) => System.getProperties.setProperty("jna.library.path", s)
            case None => println("No jna lib path set")
        }
    }

    def getJnaLibPath(argsList: List[String]): Option[String] = {
        if (argsList.isDefinedAt(3)) {
            return Some(argsList.get(3))
        } else if (System.getProperty("os.name").toLowerCase.contains("mac os x")) {
            return Some("/opt/local/lib/")
        } else {
            return None
        }
    }

}
