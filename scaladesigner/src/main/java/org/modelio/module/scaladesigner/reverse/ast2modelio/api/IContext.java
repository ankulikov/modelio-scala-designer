package org.modelio.module.scaladesigner.reverse.ast2modelio.api;

import edu.kulikov.ast_parser.elements.AstElement;
import edu.kulikov.ast_parser.elements.Import;

import java.util.List;

public interface IContext {
    List<Import> getImportScope();
}
