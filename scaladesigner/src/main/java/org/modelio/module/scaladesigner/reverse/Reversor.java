package org.modelio.module.scaladesigner.reverse;

import com.modelio.module.xmlreverse.IReportWriter;
import org.eclipse.swt.widgets.Display;
import org.modelio.api.model.IModelingSession;
import org.modelio.api.module.IModule;
import org.modelio.api.module.IModuleUserConfiguration;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.module.scaladesigner.api.ScalaDesignerParameters;
import org.modelio.module.scaladesigner.reverse.newwizard.ImageManager;
import org.modelio.module.scaladesigner.reverse.newwizard.api.IFileChooserModel;
import org.modelio.module.scaladesigner.reverse.newwizard.api.ISourcePathModel;
import org.modelio.module.scaladesigner.reverse.newwizard.filechooser.FileChooserModel;
import org.modelio.module.scaladesigner.reverse.newwizard.sourcepath.ScalaSourcePathModel;
import org.modelio.module.scaladesigner.reverse.newwizard.wizard.ScalaReverseWizardView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.modelio.module.scaladesigner.util.ScalaDesignerUtils.noParamter;

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

        IFileChooserModel scalaFileChooserModel = new FileChooserModel(this.module.getConfiguration().getProjectSpacePath().toFile(), extensions, new ReverseConfig(null, null, null, null, null));

        ScalaReverseWizardView reverseWizardView = new ScalaReverseWizardView(Display.getDefault().getActiveShell(), scalaFileChooserModel, createSourcePathModel(), createCompilerChooserModel());
        int open = reverseWizardView.open();

    }

    private ISourcePathModel createSourcePathModel() {

        File absolutePath;
        IModuleUserConfiguration configuration = this.module.getConfiguration();

        if (noParamter(configuration, ScalaDesignerParameters.SCALA_SOURCES)) {
            absolutePath = module.getConfiguration().getProjectSpacePath().toFile();
        } else
            absolutePath = new File(configuration.getParameterValue(ScalaDesignerParameters.SCALA_SOURCES));
        return new ScalaSourcePathModel(absolutePath);
    }

    private IFileChooserModel createCompilerChooserModel() {
        List<String> extensions = new ArrayList<>();
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win"))
            extensions.add(".bat");
        else
            extensions.add(".*");
        IModuleUserConfiguration configuration = this.module.getConfiguration();

        File absolutePath;
        if (noParamter(configuration, ScalaDesignerParameters.SCALA_COMPILER)) {
            absolutePath = module.getConfiguration().getProjectSpacePath().toFile();
        } else
            absolutePath = new File(configuration.getParameterValue(ScalaDesignerParameters.SCALA_COMPILER));
        return new FileChooserModel(absolutePath, extensions, new ReverseConfig(null, null, null, null, null));

    }

}
