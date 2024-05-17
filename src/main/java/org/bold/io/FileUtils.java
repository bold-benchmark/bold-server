package org.bold.io;

import org.bold.sim.SimulationEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FileUtils {

	private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    /**
     * Returns a list of file names matching the given pattern.
     *
     *
     * @param pattern path that may include wildcards (*)
     * @return
     * @throws IOException
     */
	public static Set<String> listFiles(String pattern) throws IOException {

		Path path = Paths.get(pattern);

		// Covering trivial cases:
		if (Files.isRegularFile(path)) {
			log.debug("file found: {}", pattern);
			return Collections.singleton(pattern);
		}
		if (Files.isDirectory(path)) {
			log.debug("sorry, is a directory: {}", pattern);
			return Collections.emptySet();
		}

		// Where the results will be stored:
		Set<String> returned = new HashSet<String>();

		// We need to work with the patterns aka globs. They can contain relative URIs
		// that go up the directory tree, such we cannot just traverse the tree starting
		// in the current directory. We need to find the common prefix and traverse from
		// there.

		// Getting the current directory, i.e. the directory from which the code has
		// been called.
		Path currentAbsoluteDir = Paths.get(System.getProperty("user.dir"));

		// Resolving the supplied glob against that directory.
		// If path is absolute, resolved is set to path.
		Path resolved = currentAbsoluteDir.resolve(path);

		// Starting point of the walk.
		Path walkingStartPoint;
		// Matcher created from the pattern supplied.
		PathMatcher matcher;

		if (currentAbsoluteDir.getRoot().equals(resolved.getRoot())) {
			// Case: Pattern supplied is on the same root as the current directory.

			// Determining the common prefix between the resolved URI and the current
			// directory.
			Path commonPrefix = getCommonPrefix(currentAbsoluteDir, resolved);

			// Relativizing the resolved glob against the common prefix.
			Path relativized = commonPrefix.relativize(resolved);

			// Creating a path matcher for the relativized glob.
			matcher = FileSystems.getDefault().getPathMatcher("glob:" + relativized.toString());
			walkingStartPoint = commonPrefix;
		} else {
			// Case: Pattern supplied is absolute and does not have a common root with the
			// current directory.

			// Probably this is very unoptimized.
			// TODO find the point in the pattern before the wildcard and start walking from
			// there.
			walkingStartPoint = path.getRoot();
			matcher = path.getFileSystem().getPathMatcher("glob" + pattern);
		}

		// Walking the directory tree from the possible common prefix, memorizing the
		// files that match the possibly relativized glob.
		Files.walkFileTree(walkingStartPoint, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				log.debug("file considered via walking: {}", file.toString());
				if (matcher.matches(walkingStartPoint.relativize(file))) {
					log.debug("file found via walking: {}", file.toString());
					returned.add(file.toAbsolutePath().toString());
				}
				return FileVisitResult.CONTINUE;
			}
		});

		return returned;
	}

	/**
	 * Determines the common prefix of two {@link Path}s. The {@link Path}s must
	 * have a common root.
	 *
	 * @param absoluteOne   one of the paths. Must be absolute.
	 * @param absoluteOther the other path. Must also be absolute.
	 * @return the common prefix, or null if the {@link Path}s do not have a common
	 *         root
	 * @throws IllegalArgumentException if the paths are not absolute.
	 */
	public static Path getCommonPrefix(Path absoluteOne, Path absoluteOther) {

		if (!absoluteOne.isAbsolute() || !absoluteOther.isAbsolute()) {
			throw new IllegalArgumentException("Must supply absolute Paths");
		}

		if (!absoluteOne.getRoot().equals(absoluteOther.getRoot())) {
			log.warn("No common root found between {} and {}.", absoluteOne, absoluteOther);
			return Paths.get("");
		}

		Path subOne, subOther;

		// Traversing the paths from root in parallel
		int i = 0;
		do {
			++i;
			try {
				subOne = absoluteOne.subpath(0, i);
				subOther = absoluteOther.subpath(0, i);
			} catch (IllegalArgumentException e) {
				// i too large for one of the paths, which is OK
				break;
			}
		} while (subOne.equals(subOther));
		// we need to resolve against root, as the subpath comes without root.
		return absoluteOne.getRoot().resolve(absoluteOne.subpath(0, i - 1));
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

}
