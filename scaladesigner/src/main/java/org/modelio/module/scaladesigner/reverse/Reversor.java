package org.modelio.module.scaladesigner.reverse;

import com.modelio.module.xmlreverse.IReportWriter;
import org.eclipse.swt.widgets.Display;
import org.modelio.api.model.IModelingSession;
import org.modelio.api.module.IModule;
import org.modelio.api.module.IModuleUserConfiguration;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.module.scaladesigner.api.ScalaDesignerParameters;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.newwizard.ImageManager;
import org.modelio.module.scaladesigner.reverse.newwizard.api.IFileChooserModel;
import org.modelio.module.scaladesigner.reverse.newwizard.api.ISourcePathModel;
import org.modelio.module.scaladesigner.reverse.newwizard.filechooser.ScalaFileChooserModel;
import org.modelio.module.scaladesigner.reverse.newwizard.sourcepath.ScalaSourcePathModel;
import org.modelio.module.scaladesigner.reverse.newwizard.wizard.ScalaReverseWizardView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class Reversor {
    public ReverseMode lastReverseMode = ReverseMode.Retrieve;
    private IModule module;
    private Collection<NameSpace> elementsToReverse;
    private IReportWriter report;

    public Reversor(IModule module, IReportWriter report) {
        this.module = module;
        this.report = report;
    }

    public void reverseWizard(NameSpace reverseRoot) {
        this.elementsToReverse = new HashSet<>();
        this.elementsToReverse.add(reverseRoot);
        reverse(true);
    }

    private void reverse(final boolean withWizard) {
        final IModelingSession session = this.module.getModelingSession();
        boolean error = false;
        File file;
        List<String> extensions = new ArrayList<>();
        extensions.add(".scala");

        String modulePath = module.getConfiguration().getModuleResourcesPath().toAbsolutePath().toString();
        ImageManager.setModulePath(modulePath);

        IFileChooserModel fileChooserModel = new ScalaFileChooserModel(this.module.getConfiguration().getProjectSpacePath().toFile(), extensions, new ReverseConfig(null, null, null, null, null));

        ScalaReverseWizardView reverseWizardView = new ScalaReverseWizardView(Display.getDefault().getActiveShell(), fileChooserModel,createSourcePathModel());
        int open = reverseWizardView.open();

    }

    private ISourcePathModel createSourcePathModel() {
        String absolutePath = new File("D:\\Downloads\\scala-2.11.7\\scala-2.11.7").getAbsolutePath();
//        IModuleUserConfiguration configuration = this.module.getConfiguration();
//
//        ScalaDesignerModule.logService.info("absolute path: " + absolutePath);
//        configuration.setParameterValue(ScalaDesignerParameters.SCALA_SOURCES, absolutePath);
//        String value = module.getConfiguration()
//                .getParameterValue(ScalaDesignerParameters.SCALA_SOURCES);
//        ScalaDesignerModule.logService.info("param value: " + value);


        ScalaDesignerModule.logService.info("Source path: "+absolutePath);
        return new ScalaSourcePathModel(new File(absolutePath));

    }
}
