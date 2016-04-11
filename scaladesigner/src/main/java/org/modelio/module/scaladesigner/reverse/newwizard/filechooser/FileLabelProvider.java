package org.modelio.module.scaladesigner.reverse.newwizard.filechooser;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.modelio.module.scaladesigner.reverse.newwizard.ImageManager;

import java.io.File;

class FileLabelProvider implements ILabelProvider {

    @Override
    public Image getImage(Object arg0) {
        if (arg0 instanceof File) {
            File f = (File)arg0;
            String name = f.getName();
        
            if (name.endsWith (".scala")) { //$NON-NLS-1$
                return ImageManager.getInstance().getIcon("scala");
            } else if (f.isDirectory()) {
                return ImageManager.getInstance().getIcon("directory");
            }
        }
        return null;
    }

    @Override
    public String getText(Object arg0) {
        if (arg0 instanceof File) {
            File f = (File)arg0;
            return f.getName();
        }
        return arg0.toString();
    }

    @Override
    public void addListener(ILabelProviderListener arg0) {
        // Nothing to do
    }

    @Override
    public void dispose() {
        // Nothing to do
    }

    @Override
    public boolean isLabelProperty(Object arg0, String arg1) {
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener arg0) {
        // Nothing to do
    }

}
