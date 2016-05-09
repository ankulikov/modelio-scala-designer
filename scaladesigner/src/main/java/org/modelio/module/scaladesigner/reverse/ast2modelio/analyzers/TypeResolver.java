package org.modelio.module.scaladesigner.reverse.ast2modelio.analyzers;

import edu.kulikov.ast_parser.elements.TypeWrapper;
import org.modelio.api.model.IUMLTypes;
import org.modelio.metamodel.uml.statik.DataType;
import org.modelio.metamodel.uml.statik.GeneralClass;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.api.IContext;
import org.modelio.module.scaladesigner.reverse.ast2modelio.repos.ReposManager;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TypeResolver {
    private ReposManager rm;
    private static List<GeneralClass> undef;

    public TypeResolver(ReposManager reposManager) {
        this.rm = reposManager;
    }

    public List<GeneralClass> resolveType(String type,  IContext context, IUMLTypes types) {
        return resolveType(new TypeWrapper(type), context, types);
    }

    public List<GeneralClass> resolveType(TypeWrapper typeWrapper, IContext context, IUMLTypes types) {
        if (undef == null) {
            undef = Collections.singletonList(types.getUNDEFINED());
        }
        if (typeWrapper == null || typeWrapper.getType() == null) {
            return undef;
        }
        //for array we extract its type; don't forget to set multiplicity for UML parameter!
        if (MultiplicityAnalyzer.isArray(typeWrapper) && typeWrapper.isApplied()) {
            return resolveType(new TypeWrapper(typeWrapper.getTypeParams().get(0)), context, types);
        }

        List<GeneralClass> toReturn = Collections.singletonList(resolveUMLPrimitive(typeWrapper, types));
        if (toReturn.get(0) == null) {
            toReturn = rm.getByAnyIdent(typeWrapper.getType(), context.getCurrentPackage(), context.getImportScope(), GeneralClass.class);
            ScalaDesignerModule.logService.info("ResolveType, byIdent=" + toReturn);
        }
        return (toReturn == null || toReturn.isEmpty()) ? undef : toReturn;
    }

    public Map<TypeWrapper, List<GeneralClass>> resolveTypes(List<TypeWrapper> types, IContext context, IUMLTypes umlTypes) {
        LinkedHashMap<TypeWrapper, List<GeneralClass>> map = new LinkedHashMap<>();
        for (TypeWrapper type : types) {
            map.put(type, resolveType(type, context, umlTypes));
        }
        return map;
    }

    private DataType resolveUMLPrimitive(TypeWrapper typeWrapper, IUMLTypes types) {
        switch (typeWrapper.getType()) {
            case "Int":
            case "scala.Int":
                return types.getINTEGER();
            case "Char":
            case "scala.Char":
                return types.getCHAR();
            case "Byte":
            case "scala.Byte":
                return types.getBYTE();
            case "Double":
            case "scala.Double":
                return types.getDOUBLE();
            case "Boolean":
            case "scala.Boolean":
                return types.getBOOLEAN();
            case "Long":
            case "scala.Long":
                return types.getLONG();
            case "Short":
            case "scala.Short":
                return types.getSHORT();
            case "String":
            case "Predef.String":
                return types.getSTRING();
            default:
                return null;
        }
    }
}
