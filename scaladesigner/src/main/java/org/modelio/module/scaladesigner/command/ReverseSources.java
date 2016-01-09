package org.modelio.module.scaladesigner.command;

import org.modelio.api.module.IModule;
import org.modelio.api.module.commands.DefaultModuleCommandHandler;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.module.scaladesigner.report.ReportManager;
import org.modelio.module.scaladesigner.report.ReportModel;
import org.modelio.module.scaladesigner.reverse.Reversor;
import org.modelio.vcore.smkernel.mapi.MObject;

import java.util.List;

public class ReverseSources extends DefaultModuleCommandHandler {

    /**
     * The command is displayed if the selected element is the root package.
     */
    @Override
    public boolean accept(List<MObject> selectedElements, IModule module) {
        if (!super.accept(selectedElements, module)) {
            return false;
        }

        boolean result = (selectedElements.size () != 0);
        // "Reverse" button only for Root package
        for (MObject element : selectedElements) {
            if (!((element instanceof Package) && ((Package) element).getOwner() == null))
                result = false;
            //TODO: add checking for Scala project
        }
        return result;
    }


    @Override
    public void actionPerformed(List<MObject> selectedElements, IModule module) {
        ReportModel report = ReportManager.getNewReport ();

        Reversor reversor = new Reversor(module, report);
        // No confirm box. Date files are not checked.
        reversor.reverseWizard ((NameSpace) selectedElements.get (0));

        ReportManager.showGenerationReport (report);
    }
}
