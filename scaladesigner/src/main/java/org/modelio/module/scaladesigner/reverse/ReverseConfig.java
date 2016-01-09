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

    private List<File> classpath;

    private File containerFile;

    private File outputFile;

    private NameSpace reverseRoot;


    private IReportWriter report;

    private List<IVisitorElement> filteredElements;

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
     * @return the classpath
     */
    public List<File> getClasspath() {
        return this.classpath;
    }

    /**
     * @param classpath the classpath to set
     */
    public void setClasspath(List<File> classpath) {
        this.classpath = classpath;
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
    

    /**
     * @return the filteredElements
     */
    public List<IVisitorElement> getFilteredElements() {
        return this.filteredElements;
    }

    /**
     * @param filteredElements the filteredElements to set
     */
    public void setFilteredElements(List<IVisitorElement> filteredElements) {
        this.filteredElements = filteredElements;
    }

    public ReverseConfig(Hashtable<String, ElementStatus> filesToReverse,
                         List<File> sourcepath,
                         List<File> classpath,

                         File containerFile,
                         File outputFile) {
        this.filesToReverse = filesToReverse;
        this.sourcepath = sourcepath;
        this.classpath = classpath;
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
        
        ScalaDesignerModule.logService.info ("Nb classpath=" + //$NON-NLS-1$
                this.classpath.size ());
        for (File path : this.classpath) {
            ScalaDesignerModule.logService.info ("classpath=" + path); //$NON-NLS-1$
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

    //TODO: add encoding?
}
