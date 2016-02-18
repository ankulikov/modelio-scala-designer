package org.modelio.module.scaladesigner.reverse.text2ast.api;

import java.util.List;

public interface ITextRunner {
    int run() throws  Exception;
    List<String> getResultContent();
    List<String> getErrorContent();
}
