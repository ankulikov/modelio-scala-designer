package org.modelio.module.scaladesigner.reverse.ast2modelio.api;

import edu.kulikov.ast_parser.elements.AstElement;

public interface IAstVisitHandler {


    /**
     * raises when handler met the {@code element}
     * @param element element to visit
     */
    void onStartVisit(AstElement element);

    /**
     * raises when handler visited the {@code element} with all its children
     * @param element element which was fully visited
     */
    void onEndVisit(AstElement element);
}
