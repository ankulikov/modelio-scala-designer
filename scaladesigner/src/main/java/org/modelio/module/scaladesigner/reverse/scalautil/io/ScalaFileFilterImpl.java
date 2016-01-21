package org.modelio.module.scaladesigner.reverse.scalautil.io;

import java.io.File;
import java.io.FileFilter;

/**
 * A filter for abstract pathnames, keeping only .scala files.
 */
class ScalaFileFilterImpl implements FileFilter {

    /**
     * Indicates whether or not this file must be part of the listFiles command result.
     * @see FileFilter#accept(File)
     * @param pathname The file to test.
     * @return <code>true</code> if the file ends with .java.
     */
    @Override
    public boolean accept(File pathname) {
        if (pathname.isDirectory ()) {
            return true;
        } else if (pathname.isFile () &&
                pathname.getAbsolutePath ().endsWith (".java")) {
            return true;
        } else {
            return false;
        }
    }

}
