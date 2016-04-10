# Conversion

Convert images from PDF to an image format

#### Requirements

Must have Ghostscript installed

Example on Mac

```bash
sudo port install ghostscript
```

#### Running project from command line

Note: On Mac you will need to specify the jna native library path as the Maven jar's for Ghostscript do not contain the required binary dependencies.
More details can be found in this [Stack Overflow response](http://stackoverflow.com/a/36533605/2163229)

```bash
$ mvn exec:java -Dexec.mainClass="ocr.conversion.Convert" -Dexec.args="file-location"

or, including custom jna path

$ mvn -Djna.library.path=/opt/local/lib/ exec:java -Dexec.mainClass="ocr.conversion.Convert" -Dexec.args="file-location"
```

