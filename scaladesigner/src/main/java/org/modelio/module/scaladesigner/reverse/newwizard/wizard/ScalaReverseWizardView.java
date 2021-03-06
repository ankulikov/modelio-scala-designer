package org.modelio.module.scaladesigner.reverse.newwizard.wizard;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import org.modelio.api.ui.ModelioDialog;
import org.modelio.module.scaladesigner.i18n.Messages;
import org.modelio.module.scaladesigner.impl.ScalaDesignerModule;
import org.modelio.module.scaladesigner.reverse.newwizard.api.ISourcePathModel;
import org.modelio.module.scaladesigner.reverse.newwizard.api.IFileChooserModel;
import org.modelio.module.scaladesigner.reverse.newwizard.filechooser.FileChooserComposite;
import org.modelio.module.scaladesigner.reverse.newwizard.sourcepath.SourcePathEditorComposite;


import java.util.ArrayList;
import java.util.List;

public class ScalaReverseWizardView extends ModelioDialog implements Listener {
    public Button okButton;

    public Button cancel;

    public Button next;

    public Button previous;

    public TabFolder tabFolder;

    public IFileChooserModel fileChooserModel;

    public ISourcePathModel sourcePathModel;

    public IFileChooserModel compilerChooserModel;

    List<String> titles = new ArrayList<>();


    public ScalaReverseWizardView(Shell parentShell, IFileChooserModel scalaFileChooserModel, ISourcePathModel sourcePathModel, IFileChooserModel compilerChooserModel) {
        super(parentShell);
        this.fileChooserModel = scalaFileChooserModel;
        this.sourcePathModel = sourcePathModel;
        this.compilerChooserModel = compilerChooserModel;
    }

    @Override
    public void handleEvent(Event event) {
        if (event.widget.equals(this.next)) {
            this.tabFolder.setSelection(this.tabFolder
                    .getSelectionIndex() + 1);
        } else if (event.widget.equals(this.previous)) {
            this.tabFolder.setSelection(this.tabFolder
                    .getSelectionIndex() - 1);
        }
        
        updateButtonStatus();
        setMessage(this.titles.get(this.tabFolder.getSelectionIndex()));
    }

    private void updateButtonStatus() {
        if (this.tabFolder.getSelectionIndex() == 0) {
            this.previous.setEnabled(false);
            this.next.setEnabled(true);
        } else if (this.tabFolder.getSelectionIndex() < this.tabFolder
                .getItemCount() - 1) {
            this.previous.setEnabled(true);
            this.next.setEnabled(true);
        } else {
            this.previous.setEnabled(true);
            this.next.setEnabled(false);
        }
        
        this.okButton.setEnabled(isValid());
    }

    private boolean isValid() {
        return this.tabFolder.getSelectionIndex() == this.tabFolder.getItemCount() - 1;
    }

    @Override
    protected void okPressed() {
        super.okPressed();
    }

    @Override
    public void addButtonsInButtonBar(Composite parent) {
        this.previous = createButton(parent, IDialogConstants.PROCEED_ID,
                Messages.getString("Gui.ScalaReverseWizardView.PreviousButton"),
                false);
        this.previous.setEnabled(false); // At creation, the first page is
                                                // selected
        this.next = createButton(parent, IDialogConstants.NEXT_ID,
                Messages.getString("Gui.ScalaReverseWizardView.NextButton"), true);
        this.okButton = createButton(parent, IDialogConstants.OK_ID,
                Messages.getString("Gui.ScalaReverseWizardView.OkButton"), false);
        this.okButton.setEnabled(false); // At creation, the wizard isn't
                                                // complete
        this.cancel = createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
    }

    private void addListeners() {
        this.next.addListener(SWT.Selection, this);
        this.previous.addListener(SWT.Selection, this);
        this.okButton.addListener(OK, this);
        this.cancel.addListener(CANCEL, this);
        this.tabFolder.addListener(SWT.Selection, this);
    }

    @Override
    public Control createContentArea(Composite parent) {
        // Avoid box closing when pressing enter
        parent.addTraverseListener(new TraverseListener() {
            @Override
            public void keyTraversed(TraverseEvent event) {
                if ((event.character == SWT.CR)
                        || (event.character == SWT.KEYPAD_CR)) {
                    event.doit = false;
                }
            }
        });
        
        Composite root_composite = new Composite(parent, SWT.NONE);
        root_composite.setLayout(new FillLayout(SWT.HORIZONTAL));
        root_composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        this.tabFolder = new TabFolder(root_composite, SWT.NONE);
        
        
        // Create file chooser tab
        TabItem filechooserTab = new TabItem(this.tabFolder, SWT.NONE);
        this.titles.add(Messages.getString("Gui.ScalaReverseWizardView.FileChooserTab.Title", this.fileChooserModel.getValidExtensionsList()));
        filechooserTab.setText(Messages.getString("Gui.ScalaReverseWizardView.FileChooserTab.Name"));
        filechooserTab.setControl(new FileChooserComposite(this.tabFolder, this.fileChooserModel));
        
        // Create classpath tab
        TabItem classpathTab = new TabItem(this.tabFolder, SWT.NONE);
        this.titles.add(Messages.getString("Gui.ScalaReverseWizardView.SourcePathTab.Title"));
        classpathTab.setText(Messages.getString("Gui.ScalaReverseWizardView.SourcePathTab.Name"));
        classpathTab.setControl(new SourcePathEditorComposite(this.tabFolder, this.sourcePathModel));

        //Create compiler chooser tab
        TabItem compilerChooserTab = new TabItem(this.tabFolder, SWT.NONE);
        this.titles.add(Messages.getString("Gui.ScalaReverseWizardView.CompilerChooserTab.Title", this.compilerChooserModel.getValidExtensionsList()));
        compilerChooserTab.setText(Messages.getString("Gui.ScalaReverseWizardView.CompilerChooserTab.Name"));
        compilerChooserTab.setControl(new FileChooserComposite(this.tabFolder, this.compilerChooserModel));

        return root_composite;
    }

    @Override
    public void init() {
        Shell shell = getShell();
        shell.setMinimumSize(450, 450);
        
        shell
                .setText(Messages
                        .getString("Gui.ScalaReverseWizardView.WindowName"));
        setTitle(Messages.getString("Gui.ScalaReverseWizardView.Title"));
        setMessage(this.titles.get(this.tabFolder.getSelectionIndex()));
        
        addListeners();
    }

}
