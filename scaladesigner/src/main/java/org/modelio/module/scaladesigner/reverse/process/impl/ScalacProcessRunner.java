package org.modelio.module.scaladesigner.reverse.process.impl;

import org.apache.commons.io.IOUtils;
import org.modelio.module.scaladesigner.reverse.process.api.IProcessRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScalacProcessRunner implements IProcessRunner {

    private static final List<String> defaultOptions
            = Arrays.asList("-Xprint:parser", "-Ystop-after:parser", "-Yshow-trees-compact");
    private final File pathToCompiler;
    private final List<File> sources;
    private List<String> resultContent;
    private List<String> errorContent;

    public ScalacProcessRunner(File pathToCompiler, List<File> sources) {
        this.pathToCompiler = pathToCompiler;
        this.sources = sources;
    }

    public int run() throws Exception {
        List<String> commands = new ArrayList<String>();
        commands.add(pathToCompiler.getAbsolutePath());
        commands.addAll(sources.stream().map(File::getAbsolutePath).collect(Collectors.toList()));
        commands.addAll(defaultOptions);

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        try {
            Process start = processBuilder.start();
            resultContent = IOUtils.readLines(start.getInputStream(), "UTF-8");
            errorContent = IOUtils.readLines(start.getErrorStream(), "UTF-8");

            return start.waitFor();
        } catch (IOException | InterruptedException e) {
            throw e;
        }
    }

    @Override
    public List<String> getResultContent() {
        return resultContent;
    }

    @Override
    public List<String> getErrorContent() {
        return errorContent;
    }


}
