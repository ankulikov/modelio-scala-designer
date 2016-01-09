package org.modelio.module.scaladesigner.reverse.newwizard.filechooser;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import java.io.File;
import java.util.List;

class FileFilter extends ViewerFilter {
    private List<String> extensions;


    public FileFilter(List<String> extensions) {
        this.extensions = extensions;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof File) {
            File f = (File) element;
            
            // Filter hidden files
            if (f.isHidden()) {
                return false;
            }
            
            // Accept directories
            if (f.isDirectory()) {
                return true;
            }
        
            // Accepts file with valid extensions
            String name = f.getName();
            for (String extension : this.extensions) {
                if (name.endsWith (extension)) { //$NON-NLS-1$
                    return true;
                }
            }
            
            // Filter elements with invalid extensions
            return false;
        }
        
        // Filter elements that aren't files
        return false;
    }

}
