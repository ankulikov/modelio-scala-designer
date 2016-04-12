package org.modelio.module.scaladesigner.reverse.text2ast.api;

import java.io.IOException;
import java.util.List;

public interface ITextRunner {
    int run() throws IOException, InterruptedException;
    List<String> getResultContent();
    List<String> getErrorContent();
}
