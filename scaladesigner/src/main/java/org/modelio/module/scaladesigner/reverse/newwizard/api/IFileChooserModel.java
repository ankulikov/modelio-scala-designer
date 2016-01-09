package org.modelio.module.scaladesigner.reverse.newwizard.api;

import com.modelio.module.xmlreverse.model.IVisitorElement;


import java.io.File;
import java.util.List;

public interface IFileChooserModel {

    List<String> getValidExtensions();

    File getInitialDirectory();

    void setInitialDirectory(File initialDirectory);

    List<File> getFilesToImport();

    void setFilesToImport(List<File> filesToImport);

    List<IVisitorElement> getResult();

    String getValidExtensionsList();

    List<File> getReverseRoots();
}
