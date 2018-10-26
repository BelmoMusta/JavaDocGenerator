package musta.belmo.javacodecore;

import java.io.*;
import java.net.URI;
import java.util.Deque;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * TODO : Compléter la description de cette classe
 */
public class ZipUtils {

    /**
     * TODO: Compléter la description de cette méthode
     *
     * @param in {@link InputStream}
     * @param out {@link OutputStream}
     * @throws IOException Exception levée si erreur.
     */
    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        while (true) {
            int readCount = in.read(buffer);
            if (readCount < 0) {
                break;
            }
            out.write(buffer, 0, readCount);
        }
    }

    /**
     * TODO: Compléter la description de cette méthode
     *
     * @param file {@link File}
     * @param out {@link OutputStream}
     */
    private static void copy(File file, OutputStream out) throws IOException {
        InputStream in = new FileInputStream(file);
        try {
            copy(in, out);
        } finally {
            in.close();
        }
    }

    /**
     * TODO: Compléter la description de cette méthode
     *
     * @param directory {@link File}
     * @param zipfile {@link File}
     */
    public static void zip(File directory, File zipfile) throws IOException {
        URI base = directory.toURI();
        Deque<File> queue = new LinkedList<>();
        queue.push(directory);
        OutputStream out = new FileOutputStream(zipfile);
        Closeable res = out;
        try {
            ZipOutputStream zout = new ZipOutputStream(out);
            res = zout;
            while (!queue.isEmpty()) {
                directory = queue.pop();
                if (directory != null && directory.listFiles() != null)
                    for (File kid : directory.listFiles()) {
                        String name = base.relativize(kid.toURI()).getPath();
                        if (kid.isDirectory()) {
                            queue.push(kid);
                            name = name.endsWith("/") ? name : name + "/";
                            zout.putNextEntry(new ZipEntry(name));
                        } else {
                            zout.putNextEntry(new ZipEntry(name));
                            if (!zipfile.getName().equals(kid.getName()))
                                copy(kid, zout);
                            zout.closeEntry();
                        }
                    }
            }
        } finally {
            res.close();
        }
    }
}
