package org.modelio.module.scaladesigner.reverse.newwizard.sourcepath;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.newwizard.ImageManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

class SourcePathLabelProvider implements ILabelProvider {

    @Override
    public Image getImage(Object arg0) {
        if (arg0 instanceof File) {
            File f = (File)arg0;
            String name = f.getName();
        
            if (!f.exists()) {
                return ImageManager.getInstance().getIcon("missing");
            } else  if (f.isDirectory()) {
                return ImageManager.getInstance().getIcon("directory");
            } else if (name != null && name.endsWith (".jar")) {
                return ImageManager.getInstance().getIcon("jar");
            }
        }
        return null;
    }

    @Override
    public String getText(Object arg0) {
        if (arg0 instanceof File) {
            File f = (File)arg0;
            String version = scalaVersion(f);
            if (version == null) version = "UNKNOWN";
            return "Scala: " + version+ " (" + f.getAbsolutePath() + ")";
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

    public String scalaVersion(File initialDirectory) {
        File propsFile = new File(initialDirectory, "versions.properties");
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(propsFile));
            return properties.getProperty("scala.full.version");
        } catch (IOException e) {
            ScalaDesignerModule.logService.warning(e);
        }
        return null;
    }

}
