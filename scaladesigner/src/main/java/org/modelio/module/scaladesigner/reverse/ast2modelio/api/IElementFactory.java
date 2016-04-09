package org.modelio.module.scaladesigner.reverse.ast2modelio.api;

import edu.kulikov.ast_parser.elements.AstElement;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.module.scaladesigner.reverse.ast2modelio.repos.ReposManager;

public interface IElementFactory<From extends AstElement, To extends ModelElement> {
    To createElement(From from, IUmlModel model, IContext context, Stage stage);

    void setReposManager(ReposManager reposManager);

    enum Stage {
        REVERSE_SELF_MINIMUM,
        REVERSE_SELF_FULL,
        REVERSE_RELATIONS
    }
}
