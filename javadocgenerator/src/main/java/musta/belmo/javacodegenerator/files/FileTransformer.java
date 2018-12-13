package musta.belmo.javacodegenerator.files;

import java.util.function.Consumer;
import java.util.function.Function;
import java.io.File;

public class FileTransformer {

    public static <E extends Exception> File transformAndGet(File file,
                                                             Function<File, File> transformer) throws E {
        return transformer.apply(file);
    }

    public static <E extends Exception> void transformInPlace(File file, Consumer<File> transformer) throws E {
        transformer.accept(file);
    }
}
