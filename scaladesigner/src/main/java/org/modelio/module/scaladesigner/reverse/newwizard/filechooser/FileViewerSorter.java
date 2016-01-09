package org.modelio.module.scaladesigner.reverse.newwizard.filechooser;

import org.eclipse.jface.viewers.ViewerSorter;

import java.io.File;

/**
 * Orders the items in such a way that directories appear
 * before other files.
 */
public class FileViewerSorter extends ViewerSorter {

    @Override
    public int category(Object element) {
        if(element instanceof File) {
            File f = (File) element;
            if (f.isDirectory()) {
                return 1;
            } else {
                return 2;
            }
        }
        // Should never happen
        return 3;
    }

}
