package org.modelio.module.scaladesigner.command;

import org.eclipse.jface.dialogs.MessageDialog;

import org.modelio.vcore.smkernel.mapi.MObject;
import org.modelio.api.log.ILogService;
import org.modelio.api.module.IModule;
import org.modelio.api.module.commands.DefaultModuleCommandHandler;
import org.modelio.api.model.IModelingSession;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.api.modelio.Modelio;
import org.modelio.api.module.IModuleUserConfiguration;
import java.util.List;

/**
 * Implementation of the IModuleContextualCommand interface.
 * <br>The module contextual commands are displayed in the contextual menu and in the specific toolbar of each module property page.
 * <br>The developer may inherit the DefaultModuleContextualCommand class which contains a default standard contextual command implementation.
 *
 */
public class HelloWorldCommand extends DefaultModuleCommandHandler {

    private ILogService logService;

    /**
     * Constructor.
     */
    public HelloWorldCommand() {
        super();
        // services logs
        this.logService = Modelio.getInstance().getLogService();
    }

    /**
     * @see org.modelio.api.module.commands.DefaultModuleContextualCommand#accept(java.util.List,
     *      org.modelio.api.module.IModule)
     */
    @Override
    public boolean accept(List<MObject> selectedElements, IModule module) {
        // Check that there is only one selected element
        return selectedElements.size() == 1;
    }

    /**
     * @see org.modelio.api.module.commands.DefaultModuleContextualCommand#actionPerformed(java.util.List,
     *      org.modelio.api.module.IModule)
     */
    @Override
    public void actionPerformed(List<MObject> selectedElements, IModule module) {

        this.logService.info(module, "HelloWorldCommand - actionPerformed(...)");

        IModelingSession session = Modelio.getInstance().getModelingSession();
        List<MObject> root = session.getModel().getModelRoots();
        IModuleUserConfiguration configuration = module.getConfiguration();

        ModelElement modelelt = (ModelElement)selectedElements.get(0);
        MessageDialog.openInformation(null, "Hello", modelelt.getName());
    }


}
