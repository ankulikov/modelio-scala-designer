package org.modelio.module.scaladesigner.reverse;

import com.modelio.module.xmlreverse.IReportWriter;
import edu.kulikov.ast_parser.AstElementEventHandler;
import edu.kulikov.ast_parser.AstTreeParser;
import edu.kulikov.ast_parser.elements.AstElement;
import edu.kulikov.ast_parser.reader.ReaderConfig;
import org.apache.commons.io.IOUtils;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.modelio.api.model.IModelingSession;
import org.modelio.api.model.ITransaction;
import org.modelio.api.model.InvalidTransactionException;
import org.modelio.api.modelio.Modelio;
import org.modelio.api.module.IModule;
import org.modelio.api.module.IModuleUserConfiguration;
import org.modelio.metamodel.mda.Project;
import org.modelio.metamodel.uml.statik.NameSpace;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.module.scaladesigner.api.ScalaDesignerParameters;
import org.modelio.module.scaladesigner.i18n.Messages;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.AstVisitor;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IAstVisitHandler;
import org.modelio.module.scaladesigner.reverse.ast2modelio.impl.ElementCreatorFromAst;
import org.modelio.module.scaladesigner.reverse.newwizard.ImageManager;
import org.modelio.module.scaladesigner.reverse.newwizard.api.IFileChooserModel;
import org.modelio.module.scaladesigner.reverse.newwizard.api.ISourcePathModel;
import org.modelio.module.scaladesigner.reverse.newwizard.filechooser.FileChooserModel;
import org.modelio.module.scaladesigner.reverse.newwizard.sourcepath.ScalaSourcePathModel;
import org.modelio.module.scaladesigner.reverse.newwizard.wizard.ScalaReverseWizardView;
import org.modelio.module.scaladesigner.reverse.text2ast.ScalacUtils;
import org.modelio.module.scaladesigner.reverse.text2ast.api.ITextRunner;
import org.modelio.module.scaladesigner.reverse.text2ast.impl.ScalacTextRunner;
import org.modelio.module.scaladesigner.reverse.util.ScalaFileFinder;
import org.modelio.module.scaladesigner.reverse.ui.ElementStatus;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static org.modelio.module.scaladesigner.reverse.ui.ElementStatus.ElementType;
import static org.modelio.module.scaladesigner.reverse.ui.ElementStatus.ReverseStatus;
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

        if (withWizard) {
            // Reverse with wizard
            // Build the elements to reverse
            try (ITransaction transaction = session.createTransaction(Messages.getString("Info.Session.Reverse"))) {
                // ===================== Config ==========================================
                ReverseConfig config = new ReverseConfig(
                        new Hashtable<>(), new ArrayList<>(), null, null);
                // Store the reverse root
                if (this.elementsToReverse.size() == 1) {
                    config.setReverseRoot(this.elementsToReverse.iterator().next());
                } else {
                    config.setReverseRoot(getFirstRootPackage());
                }
                config.setReport(report);
                // ==================== Create Wizard Models ==============================
                String modulePath = module.getConfiguration().getModuleResourcesPath().toAbsolutePath().toString();
                ImageManager.setModulePath(modulePath);

                IFileChooserModel scalaFileChooserModel = new FileChooserModel(this.module.getConfiguration().getProjectSpacePath().toFile(), extensions);

                ISourcePathModel sourcePathModel = createSourcePathModel();
                IFileChooserModel compilerChooserModel = createCompilerChooserModel();
                ScalaReverseWizardView reverseWizardView = new ScalaReverseWizardView(Display.getDefault().getActiveShell(), scalaFileChooserModel, sourcePathModel, compilerChooserModel);
                // =====================  Get Result From Wizard ==================================
                int result = reverseWizardView.open();
                if (result == 0) {
                    //set directory of scala sources (root folder from GitHub)
                    if (sourcePathModel.isUsed())
                        config.getSourcepath().add(sourcePathModel.getInitialDirectory());
                    //set files to reverse
                    for (File f : scalaFileChooserModel.getFilesToImport()) {
                        if (f.isDirectory()) {
                            config.getFilesToReverse().put(f.getAbsolutePath(), new ElementStatus(f.getAbsolutePath(), ElementType.DIRECTORY, ReverseStatus.REVERSE));
                        } else {
                            config.getFilesToReverse().put(f.getAbsolutePath(), new ElementStatus(f.getAbsolutePath(), ElementType.SCALA_FILE, ReverseStatus.REVERSE));
                        }
                    }
                    //set compiler path
                    ScalaDesignerModule.logService.info("CompilerChooserModel files to import: " + compilerChooserModel.getFilesToImport());
                    config.setCompiler(compilerChooserModel.getFilesToImport().get(0));
                    ScalaDesignerModule.logService.info("Config: " + config.toString());
                    //========== Process Config ============================
                    if (processRun(config)) {
                        transaction.commit();
                    }

                }

            } catch (InvalidTransactionException e) {
                // Error during the commit, the rollback is already done
            } catch (Exception e) {
                ScalaDesignerModule.logService.error(e);
            }
        }

    }

    private ISourcePathModel createSourcePathModel() {

        File absolutePath;
        IModuleUserConfiguration configuration = this.module.getConfiguration();

        if (noParamter(configuration, ScalaDesignerParameters.SCALA_SOURCES)) {
            absolutePath = module.getConfiguration().getProjectSpacePath().toFile();
        } else
            absolutePath = new File(configuration.getParameterValue(ScalaDesignerParameters.SCALA_SOURCES));
        ScalaDesignerModule.logService.info("createSourcePathModel path: " + absolutePath);
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
        ScalaDesignerModule.logService.info("createCompilerChooserModel path: " + absolutePath);
        return new FileChooserModel(absolutePath, extensions);

    }

    private Package getFirstRootPackage() {
        //noinspection LoopStatementThatDoesntLoop
        for (Project project : Modelio.getInstance().getModelingSession().findByClass(Project.class)) {
            return project.getModel();
        }
        return null;
    }

    private boolean processRun(ReverseConfig config) {
        try {
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
            dialog.open();
            dialog.run(true, true, new ReverseProgressTask(module, config));
            return true;
        } catch (Exception e) {
            ScalaDesignerModule.logService.error(e);
            return false;
        }
    }





}
