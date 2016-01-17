package org.modelio.module.scaladesigner.reverse.newwizard.filechooser;

import com.modelio.module.xmlreverse.model.IVisitorElement;
import com.modelio.module.xmlreverse.model.JaxbReversedData;
import org.modelio.module.scaladesigner.reverse.ReverseConfig;
import org.modelio.module.scaladesigner.reverse.newwizard.api.IFileChooserModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileChooserModel implements IFileChooserModel {
    private List<File> filesToImport;

    private File initialDirectory;

    private List<String> extensions;

    private List<IVisitorElement> result;

    private Set<File> cachedFiles;
    
    private ReverseConfig config;
    
    public FileChooserModel(File initialDirectory, List<String> extensions, ReverseConfig config) {
        this.initialDirectory = initialDirectory;
        this.filesToImport = new ArrayList<>();
        this.extensions = extensions;
        this.result = new ArrayList<>();
        this.cachedFiles = new HashSet<>();
        this.config = config;
    }

    @Override
    public List<String> getValidExtensions() {
        return this.extensions;
    }

    @Override
    public File getInitialDirectory() {
        return this.initialDirectory;
    }

    @Override
    public List<File> getFilesToImport() {
        return this.filesToImport;
    }

    @Override
    public void setFilesToImport(List<File> filesToImport) {
        this.filesToImport = filesToImport;
    }

    @Override
    public void setInitialDirectory(File initialDirectory) {
        this.initialDirectory = initialDirectory;
    }


    @Override
    public List<IVisitorElement> getResult() {
        return this.result;
    }

    @Override
    public String getValidExtensionsList() {
        // Build the extension list
        StringBuilder extensionsList = new StringBuilder();

        if (getValidExtensions().size() >0) {
            for (String extension : this.getValidExtensions()) {
                extensionsList.append(extension);
                extensionsList.append(", ");
            }
            extensionsList.delete(extensionsList.length() - 2, extensionsList.length());
        }
        return extensionsList.toString();
    }

    @Override
    public List<File> getReverseRoots() {
        List<File> ret = new ArrayList<>();
        for (File f : getFilesToImport()) {
            if (f.isDirectory()) {
                ret.add(f);
            } else if (f.isFile()) {
                final File parentFile = f.getParentFile();
                if (!ret.contains(parentFile)) {
                    ret.add(parentFile);
                }
            }
        }

        return ret;
    }
}
