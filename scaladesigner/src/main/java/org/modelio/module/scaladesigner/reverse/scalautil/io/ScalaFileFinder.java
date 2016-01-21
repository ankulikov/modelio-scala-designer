package org.modelio.module.scaladesigner.reverse.scalautil.io;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to gather Scala files recursively.
 */
public class ScalaFileFinder {

    /**
     * Get all .scala files in a directory, recursively.
     * @param directory the directory to look into.
     * @return All found .scala files.
     */
    public static List<File> listJavaFilesRec(File directory) {
        return listFileRec(directory, new ScalaFileFilterImpl());
    }

    /**
     * Get all .jar files in a directory, recursively.
     * @param directory the directory to look into.
     * @return All found .jar files.
     */
    public static List<File> listJarFilesRec(File directory) {
        return listFileRec(directory, new JarFileFilterImpl ());
    }

    /**
     * Get all files matching the filter in a directory, recursively.
     * @param directory the directory to look into.
     * @param filter the filter.
     * @return All found files.
     */
    private static List<File> listFileRec(File directory, FileFilter filter) {
        List<File> listToReturn = new ArrayList<> ();

        File[] filesList = directory.listFiles (filter);
        if (filesList != null) {
            for (File tmpFile : filesList) {
                if (tmpFile.isDirectory ()) {
                    listToReturn.addAll (listFileRec (tmpFile, filter));
                } else if (tmpFile.isFile () && !listToReturn.contains (tmpFile)) {
                    listToReturn.add (tmpFile);
                }
            }
        }
        return listToReturn;
    }
}
