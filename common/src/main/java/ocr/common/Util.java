package ocr.common;

import java.io.File;
import java.util.Optional;
import java.util.function.Function;

public class Util {
    public enum Locations {
        CONVERT(new String[]{
                "/usr/local/bin/convert",
                "/opt/local/bin/convert"
        }, t -> findFile(t, "convert tool")),
        TESSDATA(new String[]{
                "/opt/local/share/tessdata/",
                "/usr/local/Cellar/tesseract/3.04.01_1/share/tessdata/"
        }, t -> findFile(t, "tessdata")),
        JNA(new String[]{
                "/opt/local/lib"
        }, t -> findDir(t, "jna library"));

        private final String[] locs;
        private final Function<String[], Optional<File>> searchHandler;

        Locations(String[] locs, Function<String[], Optional<File>> searchHandler) {
            this.locs = locs;
            this.searchHandler = searchHandler;
        }

        public Optional<File> find() {
            return searchHandler.apply(locs);
        }

        public Optional<File> find(Optional<?> path) {
            if (path.isPresent()) {
                File f = new File(path.get().toString());
                if (f.exists()) {
                    return Optional.of(f);
                }
            }
            return find();
        }
    }

    public static Optional<File> findFile(String[] locs, String item) {
        return findFile(locs, item, false);
    }

    public static Optional<File> findDir(String[] locs, String item) {
        return findFile(locs, item, true);
    }

    public static Optional<File> findFile(String[] locs, String item, boolean checkIsDir) {
        for (String loc : locs) {
            File binPath = new File(loc);
            if (binPath.exists()) {
                if (checkIsDir) {
                    if (binPath.isDirectory()) {
                        return Optional.of(binPath);
                    }
                    continue;
                }
                return Optional.of(binPath);
            }
        }
        return Optional.empty();
    }

}
