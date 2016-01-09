package org.modelio.module.scaladesigner.reverse.newwizard.filechooser;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class FileContentProvider implements ITreeContentProvider {
    private final Map<File , File> parentMap = new HashMap<>();


    @Override
    public void dispose() {
        this.parentMap.clear();
    }

    @Override
    public Object[] getChildren(Object obj) {
        if (obj instanceof Object[]) {
            return (Object[]) obj;
        } else if (obj instanceof Collection<?>) {
            return ((Collection<?>) obj).toArray ();
        } else if (obj instanceof File) {
            File f = (File) obj;
            if (f.isDirectory()) {
                File [] children = f.listFiles();
                if (children != null) {
                    for (File child: children) {
                        this.parentMap.put(child, f);
                    }
                }
                return children;
            }
        }
        return null;
    }

    @Override
    public Object[] getElements(Object obj) {
        return this.getChildren (obj);
    }

    @Override
    public Object getParent(Object obj) {
        if (obj instanceof File) {
            File f = (File) obj;
            return this.parentMap.get(f);
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object obj) {
        if (obj instanceof Object[]) {
            return ((Object[]) obj).length > 0;
        } else if (obj instanceof Collection<?>) {
            return ((Collection<?>) obj).size () > 0;
        } else if (obj instanceof File) {
            File f = (File) obj;
            if (f.isDirectory()) {
                return f.listFiles().length > 0;
            }
        }
        return false;
    }

    @Override
    public void inputChanged(Viewer viewer, Object obj, Object obj1) {
        this.parentMap.clear();
    }

}
