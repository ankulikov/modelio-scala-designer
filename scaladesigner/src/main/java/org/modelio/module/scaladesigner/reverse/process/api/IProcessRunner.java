package org.modelio.module.scaladesigner.reverse.process.api;

import java.util.List;

public interface IProcessRunner {
    int run() throws  Exception;
    List<String> getResultContent();
    List<String> getErrorContent();
}
