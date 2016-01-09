package org.modelio.module.scaladesigner.reverse.newwizard.api;

import java.io.File;
import java.util.List;

public interface ISourcePathModel {
    File getInitialDirectory();
    boolean isUsed();
    void setUsed(boolean used);

}
