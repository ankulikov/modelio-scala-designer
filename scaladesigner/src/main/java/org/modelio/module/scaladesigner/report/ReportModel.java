package org.modelio.module.scaladesigner.report;

import com.modelio.module.xmlreverse.IReportWriter;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.vcore.smkernel.mapi.MObject;

import java.util.Set;
import java.util.TreeSet;

public class ReportModel implements IReportWriter {
    private Set<ElementMessage> errors;

    private Set<ElementMessage> warnings;

    private Set<ElementMessage> infos;


    public ReportModel() {
        this.errors = new TreeSet<> ();
        this.warnings = new TreeSet<> ();
        this.infos = new TreeSet<> ();
    }

    @Override
    public void addWarning(String initialMessage, MObject element, String description) {
        String message = initialMessage;
        if (message == null) {
            message = "";
        }
        
        this.warnings.add (new ElementMessage (message, element, description));
    }

    @Override
    public void addError(String initialMessage, MObject element, String description) {
        String message = initialMessage;
        if (message == null) {
            message = "";
        }
        
        this.errors.add (new ElementMessage (message, element, description));
    }

    public Set<ElementMessage> getErrors() {
        return this.errors;
    }

    public Set<ElementMessage> getWarnings() {
        return this.warnings;
    }

    @Override
    public boolean isEmpty() {
        return !(hasErrors () || hasWarnings () || hasInfos ());
    }

    public void addWarning(String message, MObject element) {
        this.warnings.add (new ElementMessage (message, element, ""));
    }

    public void addError(String message, MObject element) {
        this.errors.add (new ElementMessage (message, element, ""));
    }

    @Override
    public void addInfo(String message, MObject element, String description) {
        this.infos.add (new ElementMessage (message, element, description));
    }

    public void addInfo(String message, MObject element) {
        this.infos.add (new ElementMessage (message, element, ""));
    }

    public Set<ElementMessage> getInfos() {
        return this.infos;
    }

    @Override
    public boolean hasErrors() {
        return !this.errors.isEmpty ();
    }

    @Override
    public boolean hasInfos() {
        return !this.infos.isEmpty ();
    }

    @Override
    public boolean hasWarnings() {
        return !this.warnings.isEmpty ();
    }

    class ElementMessage implements Comparable<ElementMessage> {
        public String message;

        public MObject element;

        public String description;


        ElementMessage(String message, MObject element, String description) {
            this.message = message;
            this.element = element;
            this.description = description;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((this.description == null) ? 0 : this.description.hashCode());
            result = prime * result + ((this.element == null) ? 0 : this.element.hashCode());
            result = prime * result + ((this.message == null) ? 0 : this.message.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ElementMessage other = (ElementMessage) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (this.description == null) {
                if (other.description != null)
                    return false;
            } else if (!this.description.equals(other.description))
                return false;
            if (this.element == null) {
                if (other.element != null)
                    return false;
            } else if (!this.element.equals(other.element))
                return false;
            if (this.message == null) {
                if (other.message != null)
                    return false;
            } else if (!this.message.equals(other.message))
                return false;
            return true;
        }

        @Override
        public int compareTo(ElementMessage anotherMessage) {
            int msgval = this.message.compareTo (anotherMessage.message);
            int thiselthash = (this.element != null) ? this.element.hashCode() : 0;
            int othelthash = (anotherMessage.element != null) ? anotherMessage.element.hashCode() : 0;            
            return (msgval == 0)? (thiselthash-othelthash):msgval;
        }

        private ReportModel getOuterType() {
            return ReportModel.this;
        }

    }

    @Override
    public void addTrace(String message) {
        ScalaDesignerModule.logService.info(message);
    }

}
