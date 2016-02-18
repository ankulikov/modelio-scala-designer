package org.modelio.module.scaladesigner.reverse.ast2modelio.api;

import edu.kulikov.ast_parser.elements.AstElement;

public interface IAstVisitHandler {
    void onVisit(AstElement element);
}
