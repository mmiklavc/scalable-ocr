package ocr.conversion

import java.io.File

class ConfigOptions(pdf: File, outDir: File, jnaLibPath: Option[String]) {
    def getPdfFile() = pdf

    def getOutDir() = outDir

    def getJnaLibPath = jnaLibPath
}
