package org.modelio.module.scaladesigner.reverse.ast2modelio.analyzers;

import edu.kulikov.ast_parser.elements.TypeWrapper;
import org.modelio.api.model.IUmlModel;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.statik.Parameter;
import org.modelio.metamodel.uml.statik.StructuralFeature;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.ast2modelio.util.StringUtils;

import java.util.Arrays;
import java.util.List;

public class MultiplicityAnalyzer {
    private final static String ARRAY = "Array";
    private final static List<String> MULTIPLE = Arrays.asList(
            "Traversable", "Iterable",
            "Seq", "Set", "Map",
            "IndexedSeq", "LinearSeq", "SortedSet", "BiSet", "SortedMap",
            "HasSet", "TreeSet", "BiSet", "ListSet",
            "HashMap", "TreeMap", "ListMap",
            "Vector", "NumericRange", "Array", "Range",
            "List", "Stream", "Queue", "Stack",
            "WeakHashMap", "OpenHashMap", "LinkedHashMap", "ObservableMap", "SynchronizedMap",
            "ImmutableMapAdaptor", "MultiMap",
            "ObservableSet", "SynchronizedSet", "ImmutableSetAdaptor", "LinkedHashSet",
            "ArraySeq", "ArrayBuffer", "ListBuffer",
            "SynchronizedStack", "ArrayStack", "PriorityQueue", "SynchronizedPriorityQueue",
            "MutableList", "LinkedList", "DoubleLinkedList");


    /**
     * if type is array, then set [0..*] multiplicity
     * @param element
     * @param typeWrapper
     */
    public static void setMultiplicity(ModelElement element, TypeWrapper typeWrapper) {
        if (typeWrapper == null) return;
        if (isArray(typeWrapper)) {
            if (element instanceof StructuralFeature) {
                ((StructuralFeature) element).setMultiplicityMin("0");
                ((StructuralFeature) element).setMultiplicityMax("*");
            }
            if (element instanceof Parameter) {
                ((Parameter) element).setMultiplicityMin("0");
                ((Parameter) element).setMultiplicityMax("*");
            }
        }
    }

    public static boolean isArray(TypeWrapper typeWrapper) {
        return typeWrapper != null && (StringUtils.afterLastDot(typeWrapper.getType()).equals(ARRAY));
    }
}
