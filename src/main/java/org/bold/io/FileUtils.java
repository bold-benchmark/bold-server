package org.bold.io;

import org.bold.sim.SimulationEngine;

import java.io.*;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class FileUtils {

    /**
     * Returns a list of file names matching the given pattern.
     *
     * FIXME comply to shell file patterns (e.g. '*' also matches empty strings)
     * TODO escape regex special characters in pattern
     *
     * @param pattern path that may include wildcards (*)
     * @return
     */
    public static Set<String> listFiles(String pattern) {
        if (pattern == null) pattern = "";

        String regex = pattern.replaceAll("\\*", ".+");
        if (pattern.startsWith("/")) {
            // absolute path
            return listFilesRec(regex, new File("/"));
        } else {
            // relative path
            return listFilesRec(regex, new File("."));
        }
    }

    /**
     * Creates subdirectories included in path if these do not exist (prior to writing files at the end of that path)
     *
     * @param pattern path that may include wildcards (in which case, no subdirectory is created)
     *                or format specifiers (%s, %d, ...)
     */
    public static void makePath(String pattern) {
        if (pattern == null) return;

        int i = pattern.lastIndexOf("/");

        if (i >= 0) {
            String head = pattern.substring(0, i);
            new File(head).mkdirs();
        }
    }

    /**
     * First tries to open the file from the file system. If it does not exist, interpret it as a resource file.
     *
     * @param filename name of the file or resource
     * @return an input stream pointing to the content of the file or resource
     * @throws IOException
     */
    public static InputStream getFileOrResource(String filename) throws IOException {
        File f = new File(filename);
        URL url = SimulationEngine.class.getClassLoader().getResource(filename);

        return f.exists() ? new FileInputStream(f) : url.openStream();
    }

    /**
     * Buffers the content of an input stream into a string.
     *
     * @param is the input stream
     * @return the content of the stream buffered into a string
     * @throws IOException
     */
    public static String asString(InputStream is) throws IOException {
        StringWriter w = new StringWriter();

        int buf = -1;
        while ((buf = is.read()) > -1) w.write(buf);

        return w.toString();
    }

    private static Set<String> listFilesRec(String regex, File root) {
        Set<String> matches = new HashSet<>();

        if (root.exists()) {
            int i = regex.indexOf("/");

            String head, tail;
            if (i >= 0) {
                head = regex.substring(0, i);
                tail = regex.substring(i + 1);
            } else {
                head = regex;
                tail = "";
            }

            for (String match : root.list((f, name) -> name.matches(head))) {
                if (tail.isEmpty()) {
                    matches.add(match);
                } else {
                    File subroot = new File(root.getPath() + "/" + match);

                    // TODO wildcard should include subfolders?
                    for (String tailMatch : listFilesRec(tail, subroot)) {
                        matches.add(match + "/" + tailMatch);
                    }
                }
            }
        }

        return matches;
    }

}
