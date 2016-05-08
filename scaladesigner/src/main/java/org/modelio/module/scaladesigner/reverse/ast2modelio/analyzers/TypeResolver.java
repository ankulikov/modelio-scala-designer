package org.modelio.module.scaladesigner.reverse.ast2modelio.analyzers;

import edu.kulikov.ast_parser.elements.Entity;
import edu.kulikov.ast_parser.elements.TypeBoundsTree;
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

    public TypeResolver(ReposManager reposManager) {
        this.rm = reposManager;
    }

   public List<GeneralClass> resolveType(String type, IContext context, IUMLTypes types) {
        List<GeneralClass> undef = Collections.singletonList(types.getUNDEFINED());
        if (type == null) {
            return undef;
        }
        List<GeneralClass> toReturn = Collections.singletonList(resolveUMLPrimitive(type, types));
        if (toReturn.get(0) == null) {
            toReturn = rm.getByAnyIdent(type, context.getCurrentPackage(), context.getImportScope(), GeneralClass.class);
            ScalaDesignerModule.logService.info("ResolveType, byIdent=" + toReturn);
        }
        return (toReturn == null || toReturn.isEmpty()) ? undef : toReturn;
    }

    public Map<Entity.BaseTypeWrapper, List<GeneralClass>> resolveTypes(List<Entity.BaseTypeWrapper> types, IContext context, IUMLTypes umlTypes) {
        LinkedHashMap<Entity.BaseTypeWrapper, List<GeneralClass>> map = new LinkedHashMap<>();
        for (Entity.BaseTypeWrapper type : types) {
            map.put(type, resolveType(type.getBaseType(), context, umlTypes));
        }
        return map;
    }

    private DataType resolveUMLPrimitive(String typeIdent, IUMLTypes types) {
        switch (typeIdent) {
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
