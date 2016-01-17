package org.modelio.module.scaladesigner.reverse;

import com.modelio.module.xmlreverse.IReportWriter;
import com.modelio.module.xmlreverse.model.IVisitorElement;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ui.ElementStatus;


import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ReverseConfig {
    private Map<String, ElementStatus> filesToReverse;

    private List<File> sourcepath;

    private File compiler;

    private File containerFile;

    private File outputFile;

    private NameSpace reverseRoot;


    private IReportWriter report;


    /**
     * @return the filesToReverse
     */
    public Map<String, ElementStatus> getFilesToReverse() {
        return this.filesToReverse;
    }

    /**
     * @param filesToReverse the filesToReverse to set
     */
    public void setFilesToReverse(Map<String, ElementStatus> filesToReverse) {
        this.filesToReverse = filesToReverse;
    }
    
    public List<File> getSourcepath() {
        return this.sourcepath;
    }

    /**
     * @return the containerFile
     */
    public File getContainerFile() {
        return this.containerFile;
    }

    /**
     * @param containerFile the containerFile to set
     */
    public void setContainerFile(File containerFile) {
        this.containerFile = containerFile;
    }

    /**
     * @return the outputFile
     */
    public File getOutputFile() {
        return this.outputFile;
    }


    public ReverseConfig(Hashtable<String, ElementStatus> filesToReverse,
                         List<File> sourcepath,
                         File containerFile,
                         File outputFile) {
        this.filesToReverse = filesToReverse;
        this.sourcepath = sourcepath;

        this.containerFile = containerFile;
        this.outputFile = outputFile;
    }

    public void print() {
        ScalaDesignerModule.logService.info ("---------------");
        ScalaDesignerModule.logService.info ("Nb files to reverse=" + //$NON-NLS-1$
                this.filesToReverse.size ());
        for (String file : this.filesToReverse.keySet ()) {
            ElementStatus status = this.filesToReverse.get(file);
            ScalaDesignerModule.logService.info(status.getReverseStatus().toString());
            ScalaDesignerModule.logService.info("\t" + status.getType());
            ScalaDesignerModule.logService.info ("\tfile=" + file); //$NON-NLS-1$
        }
        
        ScalaDesignerModule.logService.info ("Nb sourcepath=" + //$NON-NLS-1$
                this.sourcepath.size ());
        for (File path : this.sourcepath) {
            ScalaDesignerModule.logService.info ("sourcepath=" + path); //$NON-NLS-1$
        }

        ScalaDesignerModule.logService.info ("containerFile=" + this.containerFile); //$NON-NLS-1$
        ScalaDesignerModule.logService.info ("outputFile=" + this.outputFile); //$NON-NLS-1$
        ScalaDesignerModule.logService.info ("---------------");
    }

    public void setReport(IReportWriter report) {
        this.report = report;
    }

    public IReportWriter getReport() {
        return this.report;
    }

    public NameSpace getReverseRoot() {
        return this.reverseRoot;
    }

    public void setReverseRoot(NameSpace reverseRoot) {
        this.reverseRoot = reverseRoot;
    }

    public File getCompiler() {
        return compiler;
    }

    public void setCompiler(File compiler) {
        this.compiler = compiler;
    }

    @Override
    public String toString() {
        return "ReverseConfig{" +
                "filesToReverse=" + filesToReverse +
                ", sourcepath=" + sourcepath +
                ", compiler=" + compiler +
                ", containerFile=" + containerFile +
                ", outputFile=" + outputFile +
                ", reverseRoot=" + reverseRoot +
                ", report=" + report +
                '}';
    }

    //TODO: add encoding?
}
