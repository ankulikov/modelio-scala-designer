package org.modelio.module.scaladesigner.reverse.ast2modelio.api;

import edu.kulikov.ast_parser.elements.AstElement;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.module.scaladesigner.reverse.ast2modelio.repos.Ast2ModelioRepo;
import org.modelio.module.scaladesigner.reverse.ast2modelio.repos.IdentifierRepo;

public interface IElementFactory<From extends AstElement, To extends ModelElement> {
    To createElement(From from, IUmlModel model, IContext context, boolean fill);
    void setIdentRepo(IdentifierRepo identRepo);
    void setTransformRepo(Ast2ModelioRepo transformRepo);
}
