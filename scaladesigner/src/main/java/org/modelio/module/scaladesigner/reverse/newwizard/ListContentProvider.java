package org.modelio.module.scaladesigner.reverse.newwizard;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import java.util.List;

/**
 * This class is used to create TreeItem elements from the Modelio UML model.
 * Use the singleton pattern.
 */
public class ListContentProvider implements ITreeContentProvider {
    private static Object[] EMPTY_ARRAY = new Object[0];


    @Override
    public void dispose() {
        // Nothing to do
    }

    @Override
    public Object[] getChildren(Object p_Object) {
        if (p_Object instanceof List<?>) {
            List<?> obj = (List<?>) p_Object;
        
            return obj.toArray();
        }
        return EMPTY_ARRAY;
    }

    @Override
    public Object[] getElements(Object p_Object) {
        Object[] res = this.getChildren(p_Object);
        return res;
    }

    @Override
    public boolean hasChildren(Object p_Object) {
        boolean res = (this.getChildren(p_Object).length > 0);
        return res;
    }

    @Override
    public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
        // Nothing to do
    }

    @Override
    public Object getParent(Object arg0) {
        // Nothing to do
        return null;
    }

}
