package org.modelio.module.scaladesigner.reverse;

import edu.kulikov.ast_parser.AstElementEventHandler;
import edu.kulikov.ast_parser.AstTreeParser;
import edu.kulikov.ast_parser.elements.AstElement;
import edu.kulikov.ast_parser.reader.ReaderConfig;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.modelio.api.model.IUmlModel;
import org.modelio.api.modelio.Modelio;
import org.modelio.api.module.IModule;
import org.modelio.module.scaladesigner.i18n.Messages;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.progress.ProgressBar;
import org.modelio.module.scaladesigner.reverse.ast2modelio.AstVisitor;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IAstVisitHandler;
import org.modelio.module.scaladesigner.reverse.ast2modelio.impl.ContainerScannerHandler;
import org.modelio.module.scaladesigner.reverse.ast2modelio.impl.ContextFillerHandler;
import org.modelio.module.scaladesigner.reverse.ast2modelio.impl.ElementCreatorFromAstHandler;
import org.modelio.module.scaladesigner.reverse.ast2modelio.repos.Ast2ModelioRepo;
import org.modelio.module.scaladesigner.reverse.ast2modelio.repos.IdentifierRepo;
import org.modelio.module.scaladesigner.reverse.text2ast.ScalacUtils;
import org.modelio.module.scaladesigner.reverse.text2ast.api.ITextRunner;
import org.modelio.module.scaladesigner.reverse.text2ast.impl.ScalacTextRunner;
import org.modelio.module.scaladesigner.reverse.ui.ElementStatus;
import org.modelio.module.scaladesigner.reverse.util.ScalaFileFinder;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class ReverseProgressTask extends ProgressBar implements IRunnableWithProgress {

    private final IModule module;
    private final ReverseConfig config;

    public ReverseProgressTask(IModule module, ReverseConfig config) {
        super(module, 0);
        this.module = module;
        this.config = config;
        startTime = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    public void run(IProgressMonitor localMonitor) throws InvocationTargetException, InterruptedException {
        ScalaDesignerModule.logService.info("Parsing start at " + Calendar.getInstance().getTime().toString());
        //TODO: rollback transaction in case of exception, javadesigner: Reversor.java#454
        monitor = localMonitor;
        try {
            // Source reverse
            launchSourceReverse();

        } catch (Exception e) {
            // Store the stack trace in the error report
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));

            this.config.getReport().addError(Messages.getString("Error.UnexpectedException"), null, errors.toString());

            // Log it too
            ScalaDesignerModule.logService.error(e);
            if (e instanceof InvocationTargetException)
                throw (InvocationTargetException) e;
            if (e instanceof InterruptedException)
                throw (InterruptedException) e;
        } finally {
            if (this.config.getReport().hasErrors()) {
                this.config.getReport().addError(Messages.getString("Error.ReverseCanceled"), null, Messages.getString("Error.ReverseCanceled.Description"));
            }
        }
    }

    private void launchSourceReverse() throws Exception {
        init(true);
        ArrayList<File> sourcesFilesToReverse = getSourcesFilesToReverse(config);
        //set number of files to track progress
        int taskSize = sourcesFilesToReverse.size() * 3;
        setMaximumValue(taskSize);
        ScalaDesignerModule.logService.info("Progress maximum value: " + String.valueOf(sourcesFilesToReverse.size() * 4)); //generate + process + transform*2
        monitor.beginTask("Reversing", taskSize);
        //1) Generate text-AST from files
        setTaskName(Messages.getString("Gui.Reverse.GeneratingASTs"));
        List<Map<File, String>> textASTs = getTextASTs(sourcesFilesToReverse);
        Map<File, String> validASTs = textASTs.get(0);
        Map<File, String> invalidFiles = textASTs.get(1); //TODO: process files with errors
        // ProgressBar.updateProgressBar(null);
        updateProgressBarNTimes(sourcesFilesToReverse.size(), null);
        //2) Process text-AST to create model-AST
        setTaskName(Messages.getString("Gui.Reverse.CreatingAstModels"));
        List<AstElement> ASTmodels = getASTmodels(validASTs);
        //3) Transform model-AST into Modelio
        setTaskName(Messages.getString("Gui.Reverse.ProcessingAstModels"));
        convertToModelio(ASTmodels);
        monitor.done();
//
//        //debug
//        monitor.beginTask("Reversing", 10);
//        for (int i = 0; i < 10; i++) {
//            setTaskName("Task No "+i);
//            Thread.sleep((long) (Math.random()*4000));
//            ProgressBar.updateProgressBar(null);
//        }
//        Thread.sleep(2000);
//        monitor.done ();

    }

    //returns two maps:
    // 1 - valid file and string AST
    // 2 - invalid file and error as string
    private List<Map<File, String>> getTextASTs(ArrayList<File> filesToReverse) throws Exception {

        // ====== Get text Scala ASTs  ================
        ScalaDesignerModule.logService.info("Get text Scala ASTs");
        ITextRunner processRunner = new ScalacTextRunner(config.getCompiler(),
                filesToReverse);
        processRunner.run();
        Map<File, String> parsedFiles = ScalacUtils.mapSourceAndStringAst(filesToReverse, processRunner.getResultContent());
        Map<File, String> errorFiles = new HashMap<>(); //TODO: get error content
        return Arrays.asList(parsedFiles, errorFiles);
    }

    private List<AstElement> getASTmodels(Map<File, String> validASTs) {
        // ====== Create models from Text ASTs ==========
        ScalaDesignerModule.logService.info("Create models from Text ASTs");
        List<AstElement> models = new ArrayList<>();
        AstTreeParser parser = new AstTreeParser(new ReaderConfig(true));
        AstElementEventHandler defaultHandler = new AstElementEventHandler();
        models.addAll(validASTs.entrySet().stream().map(fileStringEntry -> {
            setTaskName(Messages.getString("Gui.Reverse.CreatingAstModel", fileStringEntry.getKey().getName()));
            // config.getReport().addWarning("Test warning AHAHA",null,"Test warning AHAHA description");
            AstElement parse = parser.parse(IOUtils.toInputStream(fileStringEntry.getValue()), defaultHandler);
            ProgressBar.updateProgressBar(null);
            return parse;

        }).collect(Collectors.toList()));
        return models;
    }

    private void convertToModelio(List<AstElement> models) {
        //====== Transform models to Modelio =============
        ScalaDesignerModule.logService.info("Transform models to Modelio");
        clearRepos();
        //create handlers - may be reused
        IUmlModel umlModel = Modelio.getInstance().getModelingSession().getModel();
        IAstVisitHandler contextFillerHandler = new ContextFillerHandler();
        IAstVisitHandler creatorHandler = new ElementCreatorFromAstHandler(umlModel);
        ContainerScannerHandler containerScannerHandler = new ContainerScannerHandler(umlModel);
        //1st step - package and class scanner
        subtaskConvertToModelio(models, "Gui.Reverse.ScanningContainerElements", contextFillerHandler, containerScannerHandler);
        //2nd step - element creator
        subtaskConvertToModelio(models, "Gui.Reverse.CreatingUMLElements", contextFillerHandler, creatorHandler);
    }

    private void subtaskConvertToModelio(List<AstElement> models, String title, IAstVisitHandler... handlers) {
        int size = models.size();
        for (int i = 0; i < size; i++) {
            String taskName = Messages.getString(title, i + 1, size);
            ScalaDesignerModule.logService.info(taskName);
            setTaskName(taskName);
            AstVisitor visitor = new AstVisitor(models.get(i));
            for (IAstVisitHandler handler : handlers) {
                visitor.addHandler(handler);
            }
            visitor.visit();
            ProgressBar.updateProgressBar(null);
        }
    }


    private ArrayList<File> getSourcesFilesToReverse(ReverseConfig config) {
        ArrayList<File> list = new ArrayList<>();
        ArrayList<ElementStatus> listToReverse = new ArrayList<>(config.getFilesToReverse().values());
        File fileToAdd;

        for (ElementStatus eStatus : listToReverse) {
            if (eStatus.getReverseStatus() == ElementStatus.ReverseStatus.REVERSE &&
                    !eStatus.getValue().endsWith(".class")) {
                fileToAdd = new File(eStatus.getValue());
                if (fileToAdd.isFile()) {
                    if (!isInFileList(list, fileToAdd)) {
                        list.add(fileToAdd);
                    }
                } else {
                    List<File> tmpList = ScalaFileFinder.listScalaFilesRec(fileToAdd);
                    ScalaDesignerModule.logService.info("getSourcesFilesToReverse,  tmpList:" + tmpList.toString());
                    tmpList.stream().filter(tmpFile -> !isInFileList(list, tmpFile)).forEach(list::add);
                }
            }
        }
        ScalaDesignerModule.logService.info("getSourcesFilesToReverse list:" + list.toString());
        return list;
    }


    private boolean isInFileList(List<File> list, File file) {
        return list.stream().anyMatch(f -> f.getAbsolutePath().equals(file.getAbsolutePath()));
    }

    private void updateProgressBarNTimes(int n, String message) {
        for (int i = 0; i < n; i++) {
            ProgressBar.updateProgressBar(message);
        }
    }

    private void clearRepos() {
        Ast2ModelioRepo.getInstance().clear();
        IdentifierRepo.getInstance().clear();
    }
}
