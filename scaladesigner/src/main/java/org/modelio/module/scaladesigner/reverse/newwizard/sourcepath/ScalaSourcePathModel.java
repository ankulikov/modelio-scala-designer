package org.modelio.module.scaladesigner.reverse.newwizard.sourcepath;

import org.modelio.module.scaladesigner.reverse.newwizard.api.ISourcePathModel;

import java.io.File;

public class ScalaSourcePathModel implements ISourcePathModel {

    private File initialDirectory;
    private boolean used;

    public ScalaSourcePathModel(File initialDirectory) {
        this.initialDirectory = initialDirectory;
    }


    @Override
    public File getInitialDirectory() {
        return this.initialDirectory;
    }

    @Override
    public boolean isUsed() {
        return used;
    }

    @Override
    public void setUsed(boolean used) {
        this.used = used;
    }


}
