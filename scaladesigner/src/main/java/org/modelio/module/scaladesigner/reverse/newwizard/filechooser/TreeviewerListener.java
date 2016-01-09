package org.modelio.module.scaladesigner.reverse.newwizard.filechooser;

import org.eclipse.jface.viewers.*;

import java.io.File;
import java.util.List;

class TreeViewerListener implements ICheckStateListener, ITreeViewerListener {
    private CheckboxTreeViewer tree;

    private List<File> result;


    TreeViewerListener(CheckboxTreeViewer tree, List<File> result) {
        this.tree = tree;
        this.result = result;
    }

    @Override
    public void checkStateChanged(CheckStateChangedEvent event) {
        Object element = event.getElement();
        final boolean isChecked = this.tree.getChecked(element);
        if (!isChecked) {
            this.tree.setGrayed(element, false);
        }
        final boolean isGrayed = this.tree.getGrayed(element);
        
        updateChildrenElements(element, isGrayed, isChecked);
        
        final ITreeContentProvider provider = (ITreeContentProvider) this.tree.getContentProvider();
        Object parent = provider.getParent(element);
        if (parent != null) {
            doCheckStateChanged(parent);
        }
        
        saveFilesToImport();
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
        // Nothing to do
    }

    @Override
    public void treeExpanded(TreeExpansionEvent event) {
        final Object element = (event.getElement());
        final boolean checked = this.tree.getChecked(element);
        final boolean grayed = this.tree.getGrayed(element);
        
        final ITreeContentProvider provider = (ITreeContentProvider) this.tree.getContentProvider();
        
        if (checked && !grayed) {
            final Object[] children = provider.getChildren(element);
        
            if (children != null) {
                for (Object child : children) {
                    this.tree.setGrayed(child, false);
                    this.tree.setChecked(child, true);
                    updateChildrenElements(child, false, true);
                }
            }
        }
    }

    protected void doCheckStateChanged(Object element) {
        final ITreeContentProvider provider = (ITreeContentProvider) this.tree.getContentProvider();
        
        // Check children
        final Object[] children = provider.getChildren(element);
        boolean hasCheck = false;
        boolean hasUnCheck = false;
        if (children != null) {
            for (Object child : children) {
                if (this.tree.getGrayed(child)) {
                    hasCheck = true;
                    hasUnCheck = true;
                    break;
                } else if (this.tree.getChecked(child)) {
                    hasCheck = true;
                } else {
                    hasUnCheck = true;
                }
            }
        
            if (hasCheck) {
                if (hasUnCheck) {
                    this.tree.setChecked(element, true);
                    this.tree.setGrayed(element, true);
                } else {
                    this.tree.setChecked(element, true);
                    this.tree.setGrayed(element, false);
                }
            } else {
                this.tree.setChecked(element, false);
                this.tree.setGrayed(element, false);
            }
        }
        
        Object parent = provider.getParent(element);
        if (parent != null) {
            doCheckStateChanged(parent);
        }
    }

    private void updateChildrenElements(Object element, boolean grayed, boolean checked) {
        boolean expanded = this.tree.getExpandedState(element);
        
        if (expanded) {
            ITreeContentProvider provider = (ITreeContentProvider) this.tree
                    .getContentProvider();
            Object[] children = provider.getChildren(element);
        
            for (Object child : children) {
                this.tree.setGrayed(child, grayed);
                this.tree.setChecked(child, checked);
                updateChildrenElements(child, grayed, checked);
            }
        }
    }

    private void saveFilesToImport() {
        this.result.clear();
        
        collectElementsToImport();
    }

    private void collectElementsToImport() {
        Object[] elts = this.tree.getCheckedElements();
        
        // Add all selected files
        for (Object elt : elts) {
            if (elt instanceof File) {
                this.result.add((File) elt);                
            }
        }
        
        // Remove grayed ones
        elts = this.tree.getGrayedElements();
        for (Object elt : elts) {
            this.result.remove(elt);
        }
    }

}
