package org.modelio.module.scaladesigner.reverse.newwizard.sourcepath;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Tree;
import org.modelio.module.scaladesigner.reverse.newwizard.ListContentProvider;
import org.modelio.module.scaladesigner.reverse.newwizard.api.ISourcePathModel;

import java.io.File;
import java.util.ArrayList;

public class SourcePathEditorComposite extends Composite {
    private final ISourcePathModel model;
    protected CheckboxTreeViewer treeViewer;


    public SourcePathEditorComposite(Composite parent, ISourcePathModel model) {
        super(parent, SWT.NONE);
        this.model = model;
        this.createContent();
    }

    public void createContent() {
        this.setLayout(new FormLayout());

        final Group modelElementsIncludedGroup = new Group(this, SWT.NONE);
        modelElementsIncludedGroup.setLayout(new FormLayout());
        final FormData fd_modelElementsIncludedGroup = new FormData();
        fd_modelElementsIncludedGroup.bottom = new FormAttachment(100, -5);
        fd_modelElementsIncludedGroup.right = new FormAttachment(100, -5);
        fd_modelElementsIncludedGroup.top = new FormAttachment(0, 5);
        fd_modelElementsIncludedGroup.left = new FormAttachment(0, 5);
        modelElementsIncludedGroup.setLayoutData(fd_modelElementsIncludedGroup);

        final Tree tree = new Tree(modelElementsIncludedGroup, SWT.SINGLE | SWT.BORDER | SWT.RADIO);
        this.treeViewer = new CheckboxTreeViewer(tree);
        this.treeViewer.setContentProvider(new ListContentProvider());
        this.treeViewer.setLabelProvider(new SourcePathLabelProvider());

        final FormData fd_tree = new FormData();
        fd_tree.top = new FormAttachment(0, 5);
        fd_tree.right = new FormAttachment(100, -5);
        fd_tree.left = new FormAttachment(0, 5);
        fd_tree.bottom = new FormAttachment(100, -5);
        tree.setLayoutData(fd_tree);

        getDataFromModel();
    }

    public void getDataFromModel() {
        ArrayList<File> input = new ArrayList<>();
        input.add(this.model.getInitialDirectory());
        this.treeViewer.setInput(input);
        this.treeViewer.addCheckStateListener(event -> {
            Object element = event.getElement();
            final boolean isChecked = treeViewer.getChecked(element);
            model.setUsed(isChecked);
            //select first element?
        });

        // this.treeViewer.setCheckedElements(this.model.getClasspath().toArray());
    }

}
